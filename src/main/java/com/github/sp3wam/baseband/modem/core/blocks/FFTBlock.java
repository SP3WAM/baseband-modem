package com.github.sp3wam.baseband.modem.core.blocks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.signals.FFTSignal;
import com.github.sp3wam.baseband.modem.core.signals.FloatingPointSignal;

public class FFTBlock implements BlockIf< FloatingPointSignal, FFTSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( FFTBlock.class );

    private List< Complex > fftWindow = new ArrayList< Complex >();
    private int fftWindowSize;
    private int samplingFreq;
    private FFTSignal curentValue = null;
    private BlockIf< FFTSignal, ? > nextBlock;

    public FFTBlock( int samplingFreq, int fftWindowSize )
    {
        this.samplingFreq = samplingFreq;
        this.fftWindowSize = fftWindowSize;

        for( int q = 0; q < fftWindowSize; q++ )
        {
            fftWindow.add( new Complex( 0.0 ) );
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
    public void setNextBlock( BlockIf< FFTSignal, ? > nextBlock )
    {
        this.nextBlock = nextBlock;
    }

    @Override
    public FFTSignal getCurrentValue()
    {
        return curentValue;
    }

    public int getSamplingFreq()
    {
        return samplingFreq;
    }

    public int getFftWindowSize()
    {
        return fftWindowSize;
    }

    protected void execute0( FloatingPointSignal inputSignalValue )
    {
        fftWindow.remove( 0 );
        fftWindow.add( new Complex( inputSignalValue.getValue() ) );

        FastFourierTransformer fastFourierTransformer =
            new FastFourierTransformer( DftNormalization.STANDARD );

        Complex[] inputArray = fftWindow.toArray( new Complex[]
        {} );

        Complex[] outputArray = fastFourierTransformer.transform( inputArray, TransformType.FORWARD );

        curentValue = new FFTSignal( outputArray, samplingFreq );

        LOGGER.debug( String.format( "Calculating FFT from \n %s \n into %s", sourcesSamplesToString(),
            curentValue.toString() ) );
    }

    private String sourcesSamplesToString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "\n    Samples:" );
        
        for(int q = 0 ; q < fftWindow.size() ; q ++)
        {
            sb.append( String.format( "\n    %s", fftWindow.get( q ).getReal() ) );
        }
        
        return sb.toString();
    }
}
