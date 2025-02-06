package com.github.sp3wam.baseband.modem.core.blocks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
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

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import javazoom.spi.mpeg.sampled.file.MpegEncoding;

public class PcmFromMp3FileSignalGeneratorBlock implements BlockIf< DummySignal, FloatingPointSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( PcmFromMp3FileSignalGeneratorBlock.class );

    private final static long SILENCE_AT_END_DURATION_MS = 1000;

    private boolean hasMoreSamples = false;
    private double amplitude;
    private BlockIf< FloatingPointSignal, ? > nextBlock;
    private FloatingPointSignal currentValue = null;
    private long silenceAtEndFrameCountdown = -1;

    private AudioInputStream audioStream = null;
    private AudioFormat audioFormat = null;

    public PcmFromMp3FileSignalGeneratorBlock( double amplitude, String mp3FilePath ) throws IOException
    {
        this.amplitude = amplitude;

        try
        {
            MpegAudioFileReader mpegAudioFileReader = new MpegAudioFileReader();

            AudioFileFormat audioFileFormat =
                mpegAudioFileReader.getAudioFileFormat( new File( mp3FilePath ) );
            AudioFormat originalAudioFormat = audioFileFormat.getFormat();

            LOGGER.info(
                String.format( "Original audio format of MP3 file is: %s", originalAudioFormat.toString() ) );

            if( !(originalAudioFormat.getEncoding() instanceof MpegEncoding) )
            {
                throw new RuntimeException( String.format( "Files with %s encoding are not supported.",
                    audioFormat.getEncoding().toString() ) );
            }

            audioFormat = new AudioFormat( originalAudioFormat.getSampleRate(), 16,
                originalAudioFormat.getChannels(), true, false );

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

            // create stream which decodes MP3 into PCM
            InputStream inputStream = new FileInputStream( mp3FilePath );
            InputStream inputStream2 = new BufferedInputStream( inputStream );
            AudioInputStream audioInputStream = mpegAudioFileReader.getAudioInputStream( inputStream2 );

            audioStream = new DecodedMpegAudioInputStream( audioFormat, audioInputStream );

            LOGGER.info(
                String.format( "Original MP3 format will be converted into: %s", audioFormat.toString() ) );
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
            int numChannels = audioFormat.getChannels();
            int bytesPerSample = audioFormat.getFrameSize();
            int sampleSizeInBits = audioFormat.getSampleSizeInBits();

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
        return (long)audioFormat.getSampleRate();
        // return (long)audioStream.getFormat().getSampleRate();
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
