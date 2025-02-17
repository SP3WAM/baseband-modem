package com.github.sp3wam.baseband.modem.core.fft;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public class FFTBlock extends AbstractBlock< FloatingPointSignal, FFTSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( FFTBlock.class );

    private List< Complex > fftWindow = new ArrayList< Complex >();
    private int fftWindowSize;
    private int samplingFreq;

    public FFTBlock( int samplingFreq, int fftWindowSize )
    {
        this.samplingFreq = samplingFreq;
        this.fftWindowSize = fftWindowSize;

        for( int q = 0; q < fftWindowSize; q++ )
        {
            fftWindow.add( new Complex( 0.0 ) );
        }
    }

    public int getSamplingFreq()
    {
        return samplingFreq;
    }

    public int getFftWindowSize()
    {
        return fftWindowSize;
    }

    @Override
    protected boolean execute0( SystemClock systemClock, FloatingPointSignal inputSignalValue )
    {
        fftWindow.remove( 0 );
        fftWindow.add( new Complex( inputSignalValue.getValue() ) );

        FastFourierTransformer fastFourierTransformer =
            new FastFourierTransformer( DftNormalization.STANDARD );

        Complex[] inputArray = fftWindow.toArray( new Complex[]
        {} );

        Complex[] outputArray = fastFourierTransformer.transform( inputArray, TransformType.FORWARD );

        currentValue = new FFTSignal( outputArray, samplingFreq );

        LOGGER.debug( String.format( "Calculating FFT from \n %s \n into %s", sourcesSamplesToString(),
            currentValue.toString() ) );

        return true;
    }

    private String sourcesSamplesToString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "\n    Samples:" );

        for( int q = 0; q < fftWindow.size(); q++ )
        {
            sb.append( String.format( "\n    %s", fftWindow.get( q ).getReal() ) );
        }

        return sb.toString();
    }

}
