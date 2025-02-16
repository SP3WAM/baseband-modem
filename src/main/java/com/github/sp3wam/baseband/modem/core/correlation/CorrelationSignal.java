package com.github.sp3wam.baseband.modem.core.correlation;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class CorrelationSignal implements SignalIf
{
    private double[] result;

    public CorrelationSignal( double[] result )
    {
        this.result = result;
    }

    public double[] getResult()
    {
        return result;
    }
}
