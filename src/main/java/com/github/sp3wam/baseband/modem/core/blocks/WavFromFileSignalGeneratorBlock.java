package com.github.sp3wam.baseband.modem.core.blocks;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.signals.DummySignal;
import com.github.sp3wam.baseband.modem.core.signals.FloatingPointSignal;
import com.github.sp3wam.baseband.modem.core.wav.WavFile;
import com.github.sp3wam.baseband.modem.core.wav.WavFileException;

public class WavFromFileSignalGeneratorBlock implements BlockIf< DummySignal, FloatingPointSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( WavFromFileSignalGeneratorBlock.class );

    private final static long SILENCE_AT_END_DURATION_MS = 1000;

    private WavFile wavFile;
    private boolean hasMoreSamples = false;
    private double amplitude;
    private BlockIf< FloatingPointSignal, ? > nextBlock;
    private FloatingPointSignal currentValue = null;
    private long silenceAtEndFrameCountdown = -1;

    public WavFromFileSignalGeneratorBlock( double amplitude, String wavFilePath )
        throws IOException, WavFileException
    {
        this.amplitude = amplitude;

        // Open the wav file specified as the first argument
        wavFile = WavFile.openWavFile( new File( wavFilePath ) );

        // Display information about the wav file
        wavFile.display();

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
            int numChannels = wavFile.getNumChannels();

            // Create a buffer of 100 frames
            double[] buffer = new double[ 1 * numChannels ];
            int framesRead = 0;

            try
            {
                framesRead = wavFile.readFrames( buffer, 1 );
                double value = amplitude * buffer[ 0 ];
                currentValue = new FloatingPointSignal( value );
            }
            catch( IOException | WavFileException e )
            {
                throw new RuntimeException( e );
            }

            if( framesRead != 1 )
            {
                silenceAtEndFrameCountdown =
                    (long)(wavFile.getSampleRate() * SILENCE_AT_END_DURATION_MS / 1000.0);
                try
                {
                    wavFile.close();
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
        return wavFile.getSampleRate();
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
