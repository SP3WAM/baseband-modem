package com.github.sp3wam.baseband.modem.core.basic.signals;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class StringSignal implements SignalIf
{
    private String value;

    public StringSignal( String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
