package com.github.sp3wam.baseband.modem.impl.morse;

import com.github.sp3wam.baseband.modem.core.SignalIf;

class MorseSymbolSignal implements SignalIf
{
    private MorseSymbol value;

    public MorseSymbolSignal( MorseSymbol value )
    {
        this.value = value;
    }

    public MorseSymbol getValue()
    {
        return value;
    }
}
