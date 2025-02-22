package com.github.sp3wam.baseband.modem.core.fft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class FFTPeaks implements SignalIf
{
    private List< Double > peakFrequencies;
    private double avgNoiseLevel = 0.0;

    public FFTPeaks( FFTSpectrum fftSpectrum, double minPeakValue )
    {
        this.peakFrequencies = calculatePeakFrequencies( fftSpectrum, minPeakValue );
    }

    public List< Double > getPeakFrequencies()
    {
        return peakFrequencies;
    }

    private List< Double > calculatePeakFrequencies( FFTSpectrum fftSpectrum, double minPeakValue )
    {
        double[] upperSideSpectrumMag = fftSpectrum.getUpperSideMagnitudesValues();

        // find the peak candidates
        List< Integer > peakIndexes = new ArrayList<>();

        // find the average noise level but don't take peak condidates into account
        double summ = 0;
        double summCount = 0;
        for( int q = 0; q < upperSideSpectrumMag.length; q++ )
        {
            if( q == 0 )
            {
                // summ += upperSideSpectrumMag[ q ];
                // summCount++;
                // don't check the first element (0 Hz)
                // as it seems to be a DC offset only
                continue;
            }

            if( q == 1 )
            {
                // first element of the result
                if( upperSideSpectrumMag[ q ] > upperSideSpectrumMag[ q + 1 ] )
                {
                    if( upperSideSpectrumMag[ q ] > minPeakValue )
                    {
                        peakIndexes.add( q );
                    }
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
                    if( upperSideSpectrumMag[ q ] > minPeakValue )
                    {
                        peakIndexes.add( q );
                    }
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
                if( upperSideSpectrumMag[ q ] > minPeakValue )
                {
                    peakIndexes.add( q );
                }
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

        List< Double > list = new ArrayList<>();
        for( int peakIndex : peakIndexes )
        {
            if( upperSideSpectrumMag[ peakIndex ] > 2.0 * avgNoiseLevel )
            {
                double frequency = peakIndex * fftSpectrum.getFrequencyResolution();
                list.add( frequency );
            }
        }

        return list;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "\n    Peak frequencies:\n" );
        List< Double > peaks = getPeakFrequencies();
        for( Double peak : peaks )
        {
            sb.append( String.format( "        %s Hz avg noise level %s \n", peak, avgNoiseLevel ) );
        }

        return sb.toString();
    }

}
