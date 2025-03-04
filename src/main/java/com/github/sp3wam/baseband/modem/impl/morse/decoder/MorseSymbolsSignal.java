package com.github.sp3wam.baseband.modem.impl.morse.decoder;

import java.util.ArrayList;

import com.github.sp3wam.baseband.modem.core.SignalIf;

class MorseSymbolsSignal implements SignalIf
{
    private ArrayList< MorseSymbol > symbols;

    public MorseSymbolsSignal()
    {
        this.symbols = new ArrayList< MorseSymbol >();
    }

    public void addSymbol( MorseSymbol symbol )
    {
        symbols.add( symbol );
    }

    public MorseSymbol[] getSymbols()
    {
        return symbols.toArray( new MorseSymbol[]
        {} );
    }

    @Override
    public String toString()
    {
        return "MorseSymbolsSignal [symbols=" + symbols + "]";
    }
}
