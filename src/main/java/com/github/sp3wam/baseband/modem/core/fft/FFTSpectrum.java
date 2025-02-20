package com.github.sp3wam.baseband.modem.core.fft;

import org.apache.commons.math3.complex.Complex;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class FFTSpectrum implements SignalIf
{
    private Complex[] fftValue;
    private double[] freqencies;
    private long samplingFreq;

    public FFTSpectrum( FFTSignal fftSignal )
    {
        int n = fftSignal.getResult().length;

        fftValue = new Complex[ n + 1 ];
        freqencies = new double[ n + 1 ];

        for( int newIndex = 0; newIndex < n + 1; newIndex++ )
        {
            if( newIndex < n / 2 )
            {
                fftValue[ newIndex ] = fftSignal.getResult()[ n / 2 + newIndex ];
                fftValue[ newIndex ] = fftSignal.getResult()[ n / 2 + newIndex ];
            }
            else
            {
                fftValue[ newIndex ] = fftSignal.getResult()[ newIndex - n / 2 ];
            }
        }
    }

    public Complex[] getFftValue()
    {
        return fftValue;
    }

    public double[] getFreqencies()
    {
        return freqencies;
    }

}
