package com.github.sp3wam.baseband.modem.core.pcm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private AudioInputStream audioInputStream = null;
    private ByteArrayInputStream bais = null;

    @Override
    protected boolean execute0( SystemClock systemClock, FloatingPointSignal inputSignalValue )
    {
        return true;
    }

    public void startSaving()
    {
        // Sample rate and other audio properties
        float sampleRate = 44100; // 44.1 kHz sample rate
        int sampleSizeInBits = 16; // 16 bits per sample
        int channels = 1; // Mono audio
        boolean signed = true; // Signed samples
        boolean bigEndian = false; // Little-endian

        AudioFormat format = new AudioFormat( sampleRate, sampleSizeInBits, channels, signed, bigEndian );

        // Define the number of samples (for example, 1 second of audio)
        int numSamples = (int)sampleRate;

        // Generate an array of audio samples (e.g., a sine wave for this example)
        byte[] audioData = new byte[ numSamples * 2 ]; // 2 bytes per sample for 16-bit audio
        for( int i = 0; i < numSamples; i++ )
        {
            // Simple sine wave generator
            double frequency = 440.0; // A4 note (440Hz)
            double amplitude = 32760; // Max amplitude for 16-bit PCM
            double sample = amplitude * Math.sin( 2 * Math.PI * frequency * i / sampleRate );

            // Convert the sample to bytes
            short s = (short)sample;
            audioData[ 2 * i ] = (byte)(s & 0xFF); // Low byte
            audioData[ 2 * i + 1 ] = (byte)((s >> 8) & 0xFF); // High byte
        }

        // Create an AudioInputStream from the audio data
        bais = new ByteArrayInputStream( audioData );
        audioInputStream = new AudioInputStream( bais, format, numSamples );

        // Write the audio data to a WAV file
        String out = new SimpleDateFormat( "'target/'yyyy-MM-dd_hh-mm-ss'.wav'" ).format( new Date() );
        File outputFile = new File( out );
        try
        {
            AudioSystem.write( audioInputStream, AudioFileFormat.Type.WAVE, outputFile );
            LOGGER.info( "WAV file written to " + outputFile.getAbsolutePath() );
        }
        catch( IOException e )
        {
            LOGGER.error( e.getMessage(), e );
        }
        finally
        {
            try
            {
                bais.close();
            }
            catch( IOException e )
            {
                LOGGER.error( e.getMessage(), e );
            }
        }
    }

    public void stopSaving()
    {
        // close the streams
    }
}
