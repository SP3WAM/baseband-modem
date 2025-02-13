package com.github.sp3wam.baseband.modem.core.basic.blocks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public class FloatingPointAvgMagnitudeCalculatorBlock
    implements BlockIf< FloatingPointSignal, FloatingPointSignal >
{
    private final static Logger LOGGER =
        LoggerFactory.getLogger( FloatingPointAvgMagnitudeCalculatorBlock.class );

    private BlockIf< FloatingPointSignal, ? > nextBlock;
    private FloatingPointSignal currentValue = null;

    private List< FloatingPointSignal > samples = new ArrayList< FloatingPointSignal >();
    private int sampleWindow;
    private int samplesTaken = 0;

    /***
     * @param sampleWindow
     *            how many samples it should average
     */
    public FloatingPointAvgMagnitudeCalculatorBlock( int sampleWindow )
    {
        this.sampleWindow = sampleWindow;
    }

    @Override
    public void execute( SystemClock systemClock, FloatingPointSignal inputSignalValue )
    {
        execute0( systemClock, inputSignalValue );

        if( nextBlock != null )
        {
            nextBlock.execute( systemClock, currentValue );
        }
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
        currentValue = inputSignalValue;

        samples.add( inputSignalValue );
        if( samples.size() > sampleWindow )
        {
            samples.remove( 0 );
        }

        samplesTaken++;

        if( samplesTaken >= sampleWindow )
        {
            samplesTaken = 0;

            double summ = 0.0;
            double count = 0.0;
            for( int q = 0; q < samples.size(); q++ )
            {
                summ += Math.abs( samples.get( q ).getValue() );
                count++;
            }

            double avgMagnitude = summ / count;
            // and log it to the file
            LOGGER.info(
                String.format( "Average magnitude of last %s samples is %s", samples.size(), avgMagnitude ) );
        }
    }
}
