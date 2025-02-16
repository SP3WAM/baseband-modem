package com.github.sp3wam.baseband.modem.core.correlation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public class AutocorrelationBlock implements BlockIf< FloatingPointSignal, CorrelationSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( AutocorrelationBlock.class );

    private List< Double > samples = new ArrayList< Double >();
    private int windowSize;
    private CorrelationSignal curentValue = null;
    private BlockIf< CorrelationSignal, ? > nextBlock;

    public AutocorrelationBlock( int windowSize )
    {
        this.windowSize = windowSize;

        for( int q = 0; q < windowSize; q++ )
        {
            samples.add( 0.0 );
        }
    }

    @Override
    public void execute( SystemClock systemClock, FloatingPointSignal inputSignalValue )
    {
        execute0( inputSignalValue );

        if( nextBlock == null )
        {
            return;
        }

        nextBlock.execute( systemClock, curentValue );
    }

    @Override
    public void setNextBlock( BlockIf< CorrelationSignal, ? > nextBlock )
    {
        this.nextBlock = nextBlock;
    }

    @Override
    public CorrelationSignal getCurrentValue()
    {
        return curentValue;
    }

    protected void execute0( FloatingPointSignal inputSignalValue )
    {
        samples.add( 0, inputSignalValue.getValue() );
        samples.remove( samples.size() - 1 );

        // calculate autocorrelation
        double[] values = new double[ windowSize ];
        int N = windowSize;

        // AGH_funkcja_autokorelacji.pdf page 2
        // Dyskretne_sygnaly_stochastyczne.pdf page 3 equation 2.8
        for( int r = 0; r < windowSize; r++ )
        {
            double sum = 0.0;
            for( int i = 0; i < N - r; i++ )
            {
                sum += samples.get( i ) * samples.get( i + r );
            }
            values[ r ] = sum / (N - r);
        }

        curentValue = new CorrelationSignal( values );
    }
}
