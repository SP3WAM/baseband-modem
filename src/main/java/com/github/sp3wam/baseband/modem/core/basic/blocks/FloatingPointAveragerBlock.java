package com.github.sp3wam.baseband.modem.core.basic.blocks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public class FloatingPointAveragerBlock extends AbstractBlock< FloatingPointSignal, FloatingPointSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( FloatingPointAveragerBlock.class );

    private List< FloatingPointSignal > samples = new ArrayList< FloatingPointSignal >();
    private int numberOfSamples;

    public FloatingPointAveragerBlock( int numberOfSamples )
    {
        this.numberOfSamples = numberOfSamples;
    }

    @Override
    protected boolean execute0( SystemClock systemClock, FloatingPointSignal inputSignalValue )
    {
        samples.add( inputSignalValue );
        if( samples.size() > numberOfSamples )
        {
            samples.remove( 0 );
        }

        double summ = 0;
        for( FloatingPointSignal sample : samples )
        {
            summ += sample.getValue();
        }

        double value = summ / ((double)samples.size());
        setCurrentValue( new FloatingPointSignal( value ) );

        // LOGGER.debug( String.format( "Average bit value is %s", currentValue.getValue() ) );

        return true;
    }
}
