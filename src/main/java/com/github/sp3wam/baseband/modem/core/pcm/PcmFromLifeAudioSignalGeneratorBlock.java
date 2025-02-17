package com.github.sp3wam.baseband.modem.core.pcm;

import java.io.IOException;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.DummySignal;

public abstract class PcmFromLifeAudioSignalGeneratorBlock extends PcmSignalGeneratorBlock
{

    public PcmFromLifeAudioSignalGeneratorBlock( double amplitude )
    {
        super( amplitude );
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
