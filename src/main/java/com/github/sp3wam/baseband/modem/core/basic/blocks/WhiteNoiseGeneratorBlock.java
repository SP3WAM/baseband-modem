package com.github.sp3wam.baseband.modem.core.basic.blocks;

import java.util.Random;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.DummySignal;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public class WhiteNoiseGeneratorBlock implements BlockIf< DummySignal, FloatingPointSignal >
{
    private BlockIf< FloatingPointSignal, ? > nextBlock;
    private double amplitude;
    private FloatingPointSignal currentValue = null;

    private Random random = new Random( System.currentTimeMillis() );

    public WhiteNoiseGeneratorBlock( double amplitude )
    {
        this.amplitude = amplitude;
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
        double value = amplitude * (2.0 * random.nextDouble() - 1.0);

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
