package com.github.sp3wam.baseband.modem.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBlock< I extends SignalIf, O extends SignalIf > implements BlockIf< I, O >
{
    private Logger LOGGER = LoggerFactory.getLogger( AbstractBlock.class );

    private BlockIf< O, ? > nextBlock = null;
    protected O currentValue = null;
    private long processedSamples = 0;

    @Override
    public void execute( SystemClock systemClock, I inputSignalValue )
    {
        LOGGER.debug(
            String.format( " %s - processing sample number %s", getClass().getName(), processedSamples ) );
        processedSamples++;

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
