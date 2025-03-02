package com.github.sp3wam.baseband.modem.core.basic.blocks;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class BitSignal implements SignalIf
{
    private boolean bitValue;

    public BitSignal( boolean value )
    {
        this.bitValue = value;
    }

    public boolean getBitValue()
    {
        return bitValue;
    }

    @Override
    public String toString()
    {
        return "BitSignal [bitValue=" + (bitValue == true ? "1" : "0") + "]";
    }

}
