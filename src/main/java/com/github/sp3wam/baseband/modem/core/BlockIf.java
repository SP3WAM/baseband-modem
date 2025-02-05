package com.github.sp3wam.baseband.modem.core;

public interface BlockIf< I extends SignalIf, O extends SignalIf >
{

    public void execute( SystemClock systemClock, I inputSignalValue );

    public void setNextBlock( BlockIf< O, ? > nextBlock );
    
    public O getCurrentValue();
}
