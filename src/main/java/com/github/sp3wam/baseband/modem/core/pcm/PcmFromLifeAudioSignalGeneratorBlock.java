package com.github.sp3wam.baseband.modem.core.pcm;

import java.io.IOException;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.DummySignal;

public abstract class PcmFromLifeAudioSignalGeneratorBlock extends PcmSignalGeneratorBlock
{

    private Thread thread = null;
    private SystemClock systemClock;

    public PcmFromLifeAudioSignalGeneratorBlock( double amplitude )
    {
        super( amplitude );
    }

    public synchronized void startAsync()
    {
        if( thread != null )
        {
            // already started
            return;
        }

        systemClock = new SystemClock( getSampleRate() );

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                PcmFromLifeAudioSignalGeneratorBlock generator = PcmFromLifeAudioSignalGeneratorBlock.this;

                while( generator.hasMoreSamples() )
                {
                    generator.execute( systemClock, null );

                    systemClock.step();
                }
            }

        };
        thread = new Thread( runnable, "PcmFromLifeAudioSignalGeneratorBlock-Thread" );
        
        thread.start();
    }

    public synchronized void stop()
    {

    }

    @Override
    protected boolean execute0( SystemClock systemClock, DummySignal inputSignalValue )
    {
        try
        {
            if( audioStream.available() == 0 )
            {
                return false;
            }
        }
        catch( IOException e )
        {
            throw new RuntimeException( e );
        }

        return super.execute0( systemClock, inputSignalValue );
    }
}
