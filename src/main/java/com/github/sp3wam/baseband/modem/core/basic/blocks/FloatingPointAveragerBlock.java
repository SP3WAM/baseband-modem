package com.github.sp3wam.baseband.modem.core.basic.blocks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public class FloatingPointAveragerBlock implements BlockIf< FloatingPointSignal, FloatingPointSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( FloatingPointAveragerBlock.class );

    private FloatingPointSignal currentValue;
    private BlockIf< FloatingPointSignal, ? > nextBlock;

    private List< FloatingPointSignal > samples = new ArrayList< FloatingPointSignal >();
    private int numberOfSamples;

    public FloatingPointAveragerBlock( int numberOfSamples )
    {
        this.numberOfSamples = numberOfSamples;
    }

    @Override
    public void execute( SystemClock systemClock, FloatingPointSignal inputSignalValue )
    {
        execute0( systemClock, inputSignalValue );

        if( nextBlock == null )
        {
            return;
        }

        nextBlock.execute( systemClock, currentValue );
    }

    @Override
    public void setNextBlock( BlockIf< FloatingPointSignal, ? > nextBlock )
    {
        this.nextBlock = nextBlock;
    }

    @Override
    public FloatingPointSignal getCurrentValue()
    {
        return currentValue;
    }

    protected void execute0( SystemClock systemClock, FloatingPointSignal inputSignalValue )
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
        currentValue = new FloatingPointSignal( value );

//        LOGGER.debug( String.format( "Average bit value is %s", currentValue.getValue() ) );
    }
}
