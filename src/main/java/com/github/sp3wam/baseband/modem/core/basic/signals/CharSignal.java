package com.github.sp3wam.baseband.modem.core.basic.signals;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class CharSignal implements SignalIf
{
    private char value;

    public CharSignal( char value )
    {
        this.value = value;
    }

    public char getValue()
    {
        return value;
    }
}
