package com.github.sp3wam.baseband.modem.core.basic.blocks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.SystemClock;

public class BitAveragerBlock extends AbstractBlock< BitSignal, BitSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( BitAveragerBlock.class );

    private List< BitSignal > list = new ArrayList< BitSignal >();
    private int numberOfSamples;

    public BitAveragerBlock( int numberOfSamples )
    {
        this.numberOfSamples = numberOfSamples;
    }

    @Override
    protected boolean execute0( SystemClock systemClock, BitSignal inputSignalValue )
    {
        list.add( inputSignalValue );
        if( list.size() > numberOfSamples )
        {
            list.remove( 0 );
        }

        int onesCount = 0;
        for( BitSignal bitSignal : list )
        {
            if( bitSignal.getBitValue() )
            {
                onesCount++;
            }
        }

        if( onesCount >= ((double)(list.size() / 2.0)) )
        {
            currentValue = new BitSignal( true );
        }
        else
        {
            currentValue = new BitSignal( false );
        }

        LOGGER.debug(
            String.format( "Average bit value is %s", currentValue.getBitValue() == true ? "1" : "0" ) );

        return true;
    }
}
