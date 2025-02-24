package com.github.sp3wam.baseband.modem.core.fft;

import org.apache.commons.math3.complex.Complex;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class FFTSpectrum implements SignalIf
{
    private Complex[] fftValue;
    private double[] frequencies;
    private double frequencyResolution;
    private double totalPower;

    public FFTSpectrum( FFTSignal fftSignal )
    {
        int n = fftSignal.getResult().length;
        frequencyResolution = fftSignal.getSamplingFreq() / n;

        fftValue = new Complex[ n + 1 ];
        frequencies = new double[ n + 1 ];
        double freqResolution = ((double)fftSignal.getSamplingFreq()) / ((double)n);
        totalPower = 0.0;

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
            totalPower += fftValue[ newIndex ].abs();
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

    public double getFrequencyResolution()
    {
        return frequencyResolution;
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

    public double[] getPowerPercentageValues()
    {
        double[] result = new double[ fftValue.length ];
        for( int i = 0; i < fftValue.length; i++ )
        {
            result[ i ] = 100.0 * 2.0 * fftValue[ i ].abs() / totalPower;
        }

        return result;
    }

    public double[] getUpperSideMagnitudesValues()
    {
        double[] magnitudes = getMagnitudeValues();
        int startIndex = magnitudes.length / 2;

        double[] result = new double[ magnitudes.length / 2 + 1 ];
        for( int i = startIndex; i < magnitudes.length; i++ )
        {
            result[ i - startIndex ] = magnitudes[ i ];
        }

        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "\n    Spectrum upper side magnitudes:\n" );

        double[] upperSideMagnitudes = getUpperSideMagnitudesValues();

        for( int q = 0; q < upperSideMagnitudes.length; q++ )
        {
            double frequency = q * getFrequencyResolution();

            sb.append( String.format( "        %s Hz is %s\n", frequency, upperSideMagnitudes[ q ] ) );
        }

        return sb.toString();
    }
}
