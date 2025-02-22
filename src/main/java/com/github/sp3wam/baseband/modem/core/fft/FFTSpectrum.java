package com.github.sp3wam.baseband.modem.core.fft;

import org.apache.commons.math3.complex.Complex;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class FFTSpectrum implements SignalIf
{
    private Complex[] fftValue;
    private double[] frequencies;

    public FFTSpectrum( FFTSignal fftSignal )
    {
        int n = fftSignal.getResult().length;

        fftValue = new Complex[ n + 1 ];
        frequencies = new double[ n + 1 ];
        double freqResolution = ((double)fftSignal.getSamplingFreq()) / ((double)n);

        for( int newIndex = 0; newIndex < n + 1; newIndex++ )
        {
            if( newIndex < n / 2 )
            {
                fftValue[ newIndex ] = fftSignal.getResult()[ n / 2 + newIndex ];
            }
            else
            {
                fftValue[ newIndex ] = fftSignal.getResult()[ newIndex - n / 2 ];
            }

            frequencies[ newIndex ] = (newIndex - n / 2) * freqResolution;
        }
    }

    public Complex[] getComplexValues()
    {
        return fftValue;
    }

    public double[] getFreqencies()
    {
        return frequencies;
    }

    public double[] getMagnitudeValues()
    {
        double[] result = new double[ fftValue.length ];
        for( int i = 0; i < fftValue.length; i++ )
        {
            result[ i ] = fftValue[ i ].abs();
        }

        return result;
    }

}
