package com.github.sp3wam.baseband.modem.core.fft;

import org.apache.commons.math3.complex.Complex;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class FFTSpectrum implements SignalIf
{
    private Complex[] fftValue;
    private double[] frequencies;
    private double frequencyResolution;

    public FFTSpectrum( FFTSignal fftSignal )
    {
        int n = fftSignal.getResult().length;
        frequencyResolution = fftSignal.getSamplingFreq() / n;

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

    public double getFrequencyResolution()
    {
        return frequencyResolution;
    }

    /***
     * Gets the Power Spectral Density values.
     * <p>
     * DC offset information is not taken into account (set always to zero).
     * 
     * @return
     */
    public double[] getPowerSpectralDensityValues()
    {
        double[] result = new double[ fftValue.length ];
        for( int i = 0; i < fftValue.length; i++ )
        {
            if( i == getIndexOfDCOffset() )
            {
                result[ i ] = 0.0;
            }
            else
            {
                result[ i ] = fftValue[ i ].abs();
            }
        }

        return result;
    }

    /***
     * Gets the Power Spectral Density value as a percentage of total power of the signal.
     * <p>
     * DC offset information is not taken into account (set always to zero).
     * 
     * @return
     */
    public double[] getPowerSpectralDensityPercentageValues()
    {
        double totalPower = 0.0;
        for( int i = 0; i < fftValue.length; i++ )
        {
            if( i == getIndexOfDCOffset() )
            {
                continue;
            }

            totalPower += fftValue[ i ].abs();
        }

        double[] result = new double[ fftValue.length ];
        for( int i = 0; i < fftValue.length; i++ )
        {
            if( i == getIndexOfDCOffset() )
            {
                result[ i ] = 0.0;
            }
            else
            {
                result[ i ] = 100.0 * fftValue[ i ].abs() / totalPower;
            }
        }

        return result;
    }

    public double[] getUpperSidePowerSpectralDensityValues()
    {
        double[] magnitudes = getPowerSpectralDensityValues();
        int startIndex = getIndexOfDCOffset();

        // TODO: consider not including DC offset here
        double[] result = new double[ magnitudes.length / 2 + 1 ];
        for( int i = startIndex; i < magnitudes.length; i++ )
        {
            result[ i - startIndex ] = magnitudes[ i ];
        }

        return result;
    }

    private int getIndexOfDCOffset()
    {
        return fftValue.length / 2;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "\n    Spectrum upper side magnitudes:\n" );

        double[] upperSideMagnitudes = getUpperSidePowerSpectralDensityValues();

        for( int q = 0; q < upperSideMagnitudes.length; q++ )
        {
            double frequency = q * getFrequencyResolution();

            sb.append( String.format( "        %s Hz is %s\n", frequency, upperSideMagnitudes[ q ] ) );
        }

        return sb.toString();
    }
}
