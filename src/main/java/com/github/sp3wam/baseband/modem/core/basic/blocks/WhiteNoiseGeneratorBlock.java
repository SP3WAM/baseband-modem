package com.github.sp3wam.baseband.modem.core.basic.blocks;

import java.util.Random;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.DummySignal;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public class WhiteNoiseGeneratorBlock extends AbstractBlock< DummySignal, FloatingPointSignal >
{
    private double amplitude;

    private Random random = new Random( System.currentTimeMillis() );

    public WhiteNoiseGeneratorBlock( double amplitude )
    {
        this.amplitude = amplitude;
    }

    @Override
    protected boolean execute0( SystemClock systemClock, DummySignal inputSignalValue )
    {
        double value = amplitude * (2.0 * random.nextDouble() - 1.0);

        setCurrentValue( new FloatingPointSignal( value ) );

        return true;
    }

}
