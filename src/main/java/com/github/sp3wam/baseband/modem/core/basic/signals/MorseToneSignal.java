package com.github.sp3wam.baseband.modem.core.basic.signals;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class MorseToneSignal implements SignalIf
{
    private double toneFrequency;
    
    public MorseToneSignal(double frequency)
    {
        this.toneFrequency = frequency;
    }
    
    public double getToneFrequency()
    {
        return toneFrequency;
    }
}
