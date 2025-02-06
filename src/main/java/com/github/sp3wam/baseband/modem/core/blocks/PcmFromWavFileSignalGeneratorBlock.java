package com.github.sp3wam.baseband.modem.core.blocks;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.signals.DummySignal;
import com.github.sp3wam.baseband.modem.core.signals.FloatingPointSignal;

public class PcmFromWavFileSignalGeneratorBlock implements BlockIf< DummySignal, FloatingPointSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( PcmFromWavFileSignalGeneratorBlock.class );

    private final static long SILENCE_AT_END_DURATION_MS = 1000;

    // private WavFile wavFile;
    private boolean hasMoreSamples = false;
    private double amplitude;
    private BlockIf< FloatingPointSignal, ? > nextBlock;
    private FloatingPointSignal currentValue = null;
    private long silenceAtEndFrameCountdown = -1;

    private AudioInputStream audioStream = null;

    public PcmFromWavFileSignalGeneratorBlock( double amplitude, String wavFilePath ) throws IOException
    {
        this.amplitude = amplitude;

        InputStream inputStream = new FileInputStream( wavFilePath );
        InputStream inputStream2 = new BufferedInputStream( inputStream );

        try
        {
            audioStream = AudioSystem.getAudioInputStream( inputStream2 );
            AudioFormat audioFormat = audioStream.getFormat();

            if( audioFormat.getChannels() != 1 )
            {
                throw new RuntimeException( String.format(
                    "Files with number of channels %s are not supported.", audioFormat.getChannels() ) );
            }
            if( audioFormat.getFrameSize() != 2 )
            {
                throw new RuntimeException( String.format(
                    "Files with %s bytes per sample are not supported.", audioFormat.getFrameSize() ) );
            }
            if( !Encoding.PCM_SIGNED.equals( audioFormat.getEncoding() ) )
            {
                throw new RuntimeException( String.format( "Files with %s encoding are not supported.",
                    audioFormat.getEncoding().toString() ) );
            }

            LOGGER.info( audioFormat.toString() );
        }
        catch( UnsupportedAudioFileException e )
        {
            throw new RuntimeException( e );
        }

        hasMoreSamples = true;
    }

    @Override
    public void execute( SystemClock systemClock, DummySignal inputSignalValue )
    {
        execute0( systemClock );

        if( nextBlock != null )
        {
            nextBlock.execute( systemClock, currentValue );
        }
    }

    protected void execute0( SystemClock systemClock )
    {
        LOGGER.debug( String.format( "Processing sample nr %s", systemClock.getClockValue() ) );

        if( silenceAtEndFrameCountdown == -1 )
        {
            // need to generate samples from file

            // Get the number of audio channels in the wav file
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

                currentValue = new FloatingPointSignal( value );
            }
            catch( IOException e )
            {
                throw new RuntimeException( e );
            }

            if( bytesRead != (numChannels * bytesPerSample) )
            {
                silenceAtEndFrameCountdown =
                    (long)(audioStream.getFormat().getSampleRate() * SILENCE_AT_END_DURATION_MS / 1000.0);

                try
                {
                    audioStream.close();
                }
                catch( IOException e )
                {
                    // silently fail
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
    }

    @Override
    public void setNextBlock( BlockIf< FloatingPointSignal, ? > nextBlock )
    {
        this.nextBlock = nextBlock;
    }

    public long getSampleRate()
    {
        return (long)audioStream.getFormat().getSampleRate();
    }

    public boolean hasMoreSamples()
    {
        return hasMoreSamples;
    }

    @Override
    public FloatingPointSignal getCurrentValue()
    {
        return currentValue;
    }
}
