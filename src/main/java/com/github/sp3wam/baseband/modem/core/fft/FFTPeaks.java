package com.github.sp3wam.baseband.modem.core.fft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class FFTPeaks implements SignalIf
{
    private List< FFTPeak > peaks;
    private double avgNoiseLevel = 0.0;

    public FFTPeaks( FFTSpectrum fftSpectrum, double minPeakValue )
    {
        this.peaks = calculatePeakFrequencies( fftSpectrum, minPeakValue );
    }

    public List< FFTPeak > getPeaks()
    {
        return peaks;
    }

    private List< FFTPeak > calculatePeakFrequencies( FFTSpectrum fftSpectrum, double minPeakValue )
    {
        double[] upperSideSpectrumMag = fftSpectrum.getUpperSidePowerSpectralDensityValues();

        // find the peak candidates
        List< Integer > peakIndexes = new ArrayList<>();

        // find the average noise level but don't take peak condidates into account
        double summ = 0;
        double summCount = 0;
        double powerSumm = 0;
        for( int q = 0; q < upperSideSpectrumMag.length; q++ )
        {
            if( q == 0 )
            {
                // don't check the first element (0 Hz)
                // as it seems to be a DC offset only
                continue;
            }

            powerSumm += upperSideSpectrumMag[ q ];

            if( q == 1 )
            {
                // first element of the result
                if( upperSideSpectrumMag[ q ] > upperSideSpectrumMag[ q + 1 ] )
                {
                    peakIndexes.add( q );
                }
                else
                {
                    summ += upperSideSpectrumMag[ q ];
                    summCount++;
                }

                continue;
            }

            if( q == upperSideSpectrumMag.length - 1 )
            {
                // last element of the result
                if( upperSideSpectrumMag[ q ] > upperSideSpectrumMag[ q - 1 ] )
                {
                    peakIndexes.add( q );
                }
                else
                {
                    summ += upperSideSpectrumMag[ q ];
                    summCount++;
                }

                continue;
            }

            if( upperSideSpectrumMag[ q ] > upperSideSpectrumMag[ q - 1 ]
                && upperSideSpectrumMag[ q ] > upperSideSpectrumMag[ q + 1 ] )
            {
                peakIndexes.add( q );
            }
            else
            {
                summ += upperSideSpectrumMag[ q ];
                summCount++;
            }
        }

        if( peakIndexes.size() == 0 )
        {
            return Collections.emptyList();
        }

        // find the max peak value and average value
        double maxPeakValue = 0;
        for( int peakIndex : peakIndexes )
        {
            if( upperSideSpectrumMag[ peakIndex ] > maxPeakValue )
            {
                maxPeakValue = upperSideSpectrumMag[ peakIndex ];
            }
        }

        double avgNoiseLevel = summ / summCount;
        this.avgNoiseLevel = avgNoiseLevel;

        List< FFTPeak > list = new ArrayList<>();
        for( int peakIndex : peakIndexes )
        {
            if( upperSideSpectrumMag[ peakIndex ] > 2.0 * avgNoiseLevel )
            {
                double frequency = peakIndex * fftSpectrum.getFrequencyResolution();
                double powerPercentage = 100.0 * upperSideSpectrumMag[ peakIndex ] / powerSumm;
                FFTPeak peak = new FFTPeak( frequency, upperSideSpectrumMag[ peakIndex ], powerPercentage );
                list.add( peak );
            }
        }

        return list;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "\n    Peak frequencies:\n" );
        List< FFTPeak > peaks = getPeaks();
        for( FFTPeak peak : peaks )
        {
            sb.append( String.format( "        %s avg noise level %s \n", peak.toString(), avgNoiseLevel ) );
        }

        return sb.toString();
    }

}
