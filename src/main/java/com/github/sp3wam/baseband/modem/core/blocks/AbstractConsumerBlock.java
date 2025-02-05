package com.github.sp3wam.baseband.modem.core.blocks;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SignalIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;

public abstract class AbstractConsumerBlock < I extends SignalIf, O extends SignalIf > implements BlockIf< I, I >
{

    @Override
    public abstract void execute( SystemClock systemClock, I inputSignalValue );

    @Override
    public void setNextBlock( BlockIf< I, ? > nextBlock )
    {
        // do nothing
    }

    @Override
    public I getCurrentValue()
    {
        return null;
    }

}
