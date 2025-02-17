package com.github.sp3wam.baseband.modem.core.basic.blocks;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.DummySignal;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public class FloatingPointSinusGeneratorBlock extends AbstractBlock< DummySignal, FloatingPointSignal >
{
    private double amplitude;
    private double frequencyHz;

    public FloatingPointSinusGeneratorBlock( double amplitude, double frequencyHz )
    {
        this.amplitude = amplitude;
        this.frequencyHz = frequencyHz;
    }

    @Override
    protected boolean execute0( SystemClock systemClock, DummySignal inputSignalValue )
    {
        double value =
            amplitude * Math.sin( 2 * Math.PI * frequencyHz * systemClock.getClockValueInSeconds() );

        currentValue = new FloatingPointSignal( value );

        return true;
    }

}
