package com.github.sp3wam.baseband.modem.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

public abstract class AbstractBlock< I extends SignalIf, O extends SignalIf > implements BlockIf< I, O >
{
    private Logger LOGGER = LoggerFactory.getLogger( AbstractBlock.class );

    private BlockIf< O, ? > nextBlock = null;
    private O currentValue = null;
    protected long processedSamples = 0;
    private boolean loggingEnabled = true;
    private List< BlockListenerIf< O > > listeners = new ArrayList<>();

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

    @Override
    public void addListener( BlockListenerIf< O > listener )
    {
        listeners.add( listener );
    }

    public void setLoggingEnabled( boolean loggingEnabled )
    {
        this.loggingEnabled = loggingEnabled;

        Logger logger = getLogger();
        if( logger == null )
        {
            return;
        }

        if( loggingEnabled )
        {
            return;
        }

        Configurator.setLevel( getLogger().getName(), Level.OFF );
    }

    protected abstract boolean execute0( SystemClock systemClock, I inputSignalValue );

    protected void setCurrentValue(O value)
    {
        this.currentValue = value;
        
        for(BlockListenerIf< O > listener : listeners)
        {
            listener.onCurrentValueSet( currentValue );
        }
    }
    
    protected Logger getLogger()
    {
        if( loggingEnabled )
        {
            return getLogger0();
        }
        return NOPLogger.NOP_LOGGER;
    }

    protected Logger getLogger0()
    {
        return null;
    }
}
