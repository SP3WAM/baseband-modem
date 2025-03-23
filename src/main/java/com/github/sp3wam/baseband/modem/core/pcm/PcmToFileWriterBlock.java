package com.github.sp3wam.baseband.modem.core.pcm;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public class PcmToFileWriterBlock extends AbstractBlock< FloatingPointSignal, FloatingPointSignal >
{
    private final static Logger LOGGER = LoggerFactory.getLogger( PcmToFileWriterBlock.class );

    private BlockingCustomInputStream customInputStream = null;
    private AudioInputStream audioInputStream = null;
    private Thread thread = null;
    private AtomicBoolean fileSaved = new AtomicBoolean( false );

    @Override
    protected boolean execute0( SystemClock systemClock, FloatingPointSignal inputSignalValue )
    {
        if( customInputStream == null )
        {
            return false;
        }

        // assuming:
        // 1. the sample value is from -100 to 100
        // 2. max amplitude for 16-bit PCM is 32760
        double sample = 32760.0 * inputSignalValue.getValue() / 100.0;

        // Convert the sample to bytes
        short s = (short)sample;
        byte lowByte = (byte)(s & 0xFF);
        byte hightByte = (byte)((s >> 8) & 0xFF);
        customInputStream.appendByte( lowByte ); // Low byte
        customInputStream.appendByte( hightByte ); // High byte

        LOGGER.debug(
            String.format( "Saving sample value double %s as short %s", inputSignalValue.getValue(), s ) );

        return true;
    }

    public void startSavingAsync()
    {
        // Sample rate and other audio properties
        float sampleRate = 44100; // 44.1 kHz sample rate
        int sampleSizeInBits = 16; // 16 bits per sample
        int channels = 1; // Mono audio
        boolean signed = true; // Signed samples
        boolean bigEndian = false; // Little-endian

        AudioFormat format = new AudioFormat( sampleRate, sampleSizeInBits, channels, signed, bigEndian );

        customInputStream = new BlockingCustomInputStream();
        audioInputStream = new AudioInputStream( customInputStream, format, AudioSystem.NOT_SPECIFIED );

        // Write the audio data to a WAV file
        String out = new SimpleDateFormat( "'target/'yyyy-MM-dd_HH-mm-ss'.wav'" ).format( new Date() );
        File outputFile = new File( out );

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    AudioSystem.write( audioInputStream, AudioFileFormat.Type.WAVE, outputFile );
                    LOGGER.info( "WAV file written to " + outputFile.getAbsolutePath() );

                    fileSaved.set( true );
                }
                catch( IOException e )
                {
                    LOGGER.error( e.getMessage(), e );
                }
            }
        };
        thread = new Thread( runnable, "PCM to WAV writer thread" );
        fileSaved.set( false );
        thread.start();
    }

    public void stopSaving()
    {
        // stop feeding the new data to the stream
        try
        {
            customInputStream.close();
        }
        catch( IOException e )
        {
            LOGGER.error( e.getMessage(), e );
        }

        while( !fileSaved.get() )
        {
            try
            {
                Thread.currentThread().sleep( 10 );
            }
            catch( InterruptedException e )
            {
                LOGGER.warn( e.getMessage(), e );
            }
        }

        try
        {
            audioInputStream.close();
        }
        catch( IOException e )
        {
            LOGGER.error( e.getMessage(), e );
        }
        
        audioInputStream = null;
        customInputStream = null;
    }
    
    public boolean isRecording()
    {
        if(customInputStream == null)
        {
            return false;
        }
        
        return true;
    }
}
