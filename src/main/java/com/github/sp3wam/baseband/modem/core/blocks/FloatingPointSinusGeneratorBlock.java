package com.github.sp3wam.baseband.modem.core.blocks;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.signals.DummySignal;
import com.github.sp3wam.baseband.modem.core.signals.FloatingPointSignal;

public class FloatingPointSinusGeneratorBlock implements BlockIf< DummySignal, FloatingPointSignal >
{
    private BlockIf< FloatingPointSignal, ? > nextBlock;
    private double amplitude;
    private double frequencyHz;
    private FloatingPointSignal currentValue = null;

    public FloatingPointSinusGeneratorBlock( double amplitude, double frequencyHz )
    {
        this.amplitude = amplitude;
        this.frequencyHz = frequencyHz;
    }

    @Override
    public void execute( SystemClock systemClock, DummySignal inputSignalValue )
    {
        execute0(systemClock);
        
        if(nextBlock != null)
        {
            nextBlock.execute( systemClock, currentValue );
        }
    }

    protected void execute0( SystemClock systemClock )
    {
        double value =
            amplitude * Math.sin( 2 * Math.PI * frequencyHz * systemClock.getClockValueInSeconds() );

        currentValue = new FloatingPointSignal( value );
    }

    @Override
    public void setNextBlock( BlockIf< FloatingPointSignal, ? > nextBlock )
    {
        this.nextBlock = nextBlock;
    }

    @Override
    public FloatingPointSignal getCurrentValue()
    {
        return currentValue;
    }

}
