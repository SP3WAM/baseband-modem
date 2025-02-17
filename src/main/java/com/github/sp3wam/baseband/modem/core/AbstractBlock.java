package com.github.sp3wam.baseband.modem.core;

public abstract class AbstractBlock< I extends SignalIf, O extends SignalIf > implements BlockIf< I, O >
{
    private BlockIf< O, ? > nextBlock = null;
    protected O currentValue = null;

    @Override
    public void execute( SystemClock systemClock, I inputSignalValue )
    {
        boolean result = execute0( systemClock, inputSignalValue );

        if( result == false )
        {
            return;
        }

        if( nextBlock == null )
        {
            return;
        }

        nextBlock.execute( systemClock, currentValue );
    }

    @Override
    public void setNextBlock( BlockIf< O, ? > nextBlock )
    {
        this.nextBlock = nextBlock;
    }

    @Override
    public O getCurrentValue()
    {
        return currentValue;
    }

    protected abstract boolean execute0( SystemClock systemClock, I inputSignalValue );
}
