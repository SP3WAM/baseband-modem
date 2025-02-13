package com.github.sp3wam.baseband.modem.core.basic.blocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.impl.morse.MorseToneSignal;

public class ToneToBitConverterBlock implements BlockIf< MorseToneSignal, BitSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( ToneToBitConverterBlock.class );

    private final static double TONE_TO_BIT_TRESHOLD = 300.0;

    private BitSignal currentValue;
    private BlockIf< BitSignal, ? > nextBlock;

    @Override
    public void execute( SystemClock systemClock, MorseToneSignal inputSignalValue )
    {
        execute0( systemClock, inputSignalValue );

        if( nextBlock == null )
        {
            return;
        }

        nextBlock.execute( systemClock, currentValue );
    }

    @Override
    public void setNextBlock( BlockIf< BitSignal, ? > nextBlock )
    {
        this.nextBlock = nextBlock;
    }

    @Override
    public BitSignal getCurrentValue()
    {
        return currentValue;
    }

    protected void execute0( SystemClock systemClock, MorseToneSignal inputSignalValue )
    {
        if( inputSignalValue.getToneFrequency() >= TONE_TO_BIT_TRESHOLD )
        {
            currentValue = new BitSignal( true );
        }
        else
        {
            currentValue = new BitSignal( false );
        }

        LOGGER.debug( String.format( "Detected bit %s", currentValue.getBitValue() ) );
    }
}
