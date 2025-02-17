package com.github.sp3wam.baseband.modem.core.basic.blocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.impl.morse.MorseToneSignal;

public class ToneToBitConverterBlock extends AbstractBlock< MorseToneSignal, BitSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( ToneToBitConverterBlock.class );

    private final static double TONE_TO_BIT_TRESHOLD = 300.0;

    @Override
    protected boolean execute0( SystemClock systemClock, MorseToneSignal inputSignalValue )
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
        
        return true;
    }
}
