package com.github.sp3wam.baseband.modem.core.basic.signals;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class FloatingPointSignal implements SignalIf
{
    private double value;

    public FloatingPointSignal( double value )
    {
        this.value = value;
    }

    public double getValue()
    {
        return value;
    }

}
