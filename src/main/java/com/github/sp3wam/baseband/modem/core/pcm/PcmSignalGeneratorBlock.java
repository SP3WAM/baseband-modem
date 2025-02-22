package com.github.sp3wam.baseband.modem.core.pcm;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.DummySignal;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public abstract class PcmSignalGeneratorBlock extends AbstractBlock< DummySignal, FloatingPointSignal >
{
    private final static Logger LOGGER = LoggerFactory.getLogger( PcmSignalGeneratorBlock.class );

    protected boolean hasMoreSamples = false;
    protected double amplitude;
    protected long silenceAtEndFrameCountdown = -1;
    protected long silenceAtEndDurationMillis = 0;

    protected AudioInputStream audioStream = null;
    protected long samplesCount = 0;

    public PcmSignalGeneratorBlock( double amplitude )
    {
        this.amplitude = amplitude;
    }

    public void init() throws IOException
    {
        audioStream = createAudioInputStream();

        AudioFormat audioFormat = audioStream.getFormat();

        LOGGER.info( audioFormat.toString() );

        if( audioFormat.getChannels() != 1 )
        {
            throw new RuntimeException( String.format( "Files with number of channels %s are not supported.",
                audioFormat.getChannels() ) );
        }
        if( audioFormat.getFrameSize() != 2 )
        {
            throw new RuntimeException( String.format( "Files with %s bytes per sample are not supported.",
                audioFormat.getFrameSize() ) );
        }
        if( !Encoding.PCM_SIGNED.equals( audioFormat.getEncoding() ) )
        {
            throw new RuntimeException( String.format( "Files with %s encoding are not supported.",
                audioFormat.getEncoding().toString() ) );
        }

        hasMoreSamples = true;
    }

    public long getSampleRate()
    {
        return (long)audioStream.getFormat().getSampleRate();
    }

    public boolean hasMoreSamples()
    {
        return hasMoreSamples;
    }

    public void setGenerateSilenceAtEnd( long silenceDurationMillis )
    {
        this.silenceAtEndDurationMillis = silenceDurationMillis;
    }

    protected abstract AudioInputStream createAudioInputStream() throws IOException;

    @Override
    protected boolean execute0( SystemClock systemClock, DummySignal inputSignalValue )
    {
        samplesCount++;
        // LOGGER.debug( String.format( "Processing sample nr %s", samplesCount ) );

        if( !hasMoreSamples )
        {
            return false;
        }

        if( silenceAtEndFrameCountdown == -1 )
        {
            // need to generate samples from file

            int numChannels = audioStream.getFormat().getChannels();
            int bytesPerSample = audioStream.getFormat().getFrameSize();
            int sampleSizeInBits = audioStream.getFormat().getSampleSizeInBits();

            // Create a buffer
            byte[] buffer = new byte[ 1 * numChannels * bytesPerSample ];
            int bytesRead = 0;

            try
            {
                // framesRead = wavFile.readFrames( buffer, 1 );
                bytesRead = audioStream.read( buffer );

                // assuming we have 2 bytes per sample and PCM_SIGNED encoding
                int val = (buffer[ 0 ] & 0xFF) + (buffer[ 1 ] << 8);
                double floatScale = 1 << (sampleSizeInBits - 1);
                double value = amplitude * val / floatScale;

                LOGGER.trace( String.format( "%s", value ) );

                currentValue = new FloatingPointSignal( value );

                LOGGER.debug( String.format( "Processing sample nr %s with value %s", samplesCount,
                    currentValue.getValue() ) );
            }
            catch( IOException e )
            {
                throw new RuntimeException( e );
            }

            if( bytesRead != (numChannels * bytesPerSample) )
            {
                if( silenceAtEndDurationMillis > 0 )
                {
                    silenceAtEndFrameCountdown =
                        (long)(audioStream.getFormat().getSampleRate() * silenceAtEndDurationMillis / 1000.0);
                }
                else
                {
                    hasMoreSamples = false;
                }

                try
                {
                    audioStream.close();
                }
                catch( IOException e )
                {
                    LOGGER.error( e.getMessage(), e );
                }
            }
        }
        else
        {
            // need to generate silence at end of the sequence
            silenceAtEndFrameCountdown--;
            if( silenceAtEndFrameCountdown == 0 )
            {
                hasMoreSamples = false;
            }
            currentValue = new FloatingPointSignal( 0.0 );
        }

        return true;
    }
}
