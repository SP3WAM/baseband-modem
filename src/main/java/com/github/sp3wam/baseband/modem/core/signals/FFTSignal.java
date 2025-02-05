package com.github.sp3wam.baseband.modem.core.signals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.complex.Complex;

import com.github.sp3wam.baseband.modem.core.SignalIf;

public class FFTSignal implements SignalIf
{
    private Complex[] result;
    private long samplingFreq;

    public FFTSignal( Complex[] result, long samplingFreq )
    {
        this.result = result;
        this.samplingFreq = samplingFreq;
    }

    public Complex[] getResult()
    {
        return result;
    }

    public long getSamplingFreq()
    {
        return samplingFreq;
    }

    public List< Double > getPeakFrequencies( double minPeakValue )
    {
        // find the peak candidates
        List< Integer > peakIndexes = new ArrayList<>();
        // find the summ of all items
        double summ = 0;
        double summCount = 0;
        for( int q = 0; q < result.length / 2 + 1; q++ )
        {
            if( q == 0 )
            {
                summ += result[ q ].abs();
                summCount++;
                // don't check the first element (0 Hz)
                // as it seems to be a DC offset only
                continue;
            }

            if( q == 1 )
            {
                // first element of the result
                if( result[ q ].abs() > result[ q + 1 ].abs() )
                {
                    if( result[ q ].abs() > minPeakValue )
                    {
                        peakIndexes.add( q );
                    }
                }
                else
                {
                    summ += result[ q ].abs();
                    summCount++;
                }

                continue;
            }

            if( q == result.length - 1 )
            {
                // last element of the result
                if( result[ q ].abs() > result[ q - 1 ].abs() )
                {
                    if( result[ q ].abs() > minPeakValue )
                    {
                        peakIndexes.add( q );
                    }
                }

                summ += result[ q ].abs();
                summCount++;

                continue;
            }

            if( result[ q ].abs() > result[ q - 1 ].abs() && result[ q ].abs() > result[ q + 1 ].abs() )
            {
                if( result[ q ].abs() > minPeakValue )
                {
                    peakIndexes.add( q );
                }
            }
            else
            {
                summ += result[ q ].abs();
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
            if( result[ peakIndex ].abs() > maxPeakValue )
            {
                maxPeakValue = result[ peakIndex ].abs();
            }
        }

        double avg = summ / summCount;

        List< Double > list = new ArrayList<>();
        double halfOfMaxPeakValue = 0.5 * maxPeakValue;
        for( int peakIndex : peakIndexes )
        {
            // if( result[ peakIndex ].abs() >= halfOfMaxPeakValue )
            // {
            // double frequency = peakIndex * samplingFreq / result.length;
            // list.add( frequency );
            // }

            // if(result[peakIndex].abs() / 2.0 > avg)
            if( result[ peakIndex ].abs() / 2.0 > avg )
            {
                double frequency = peakIndex * samplingFreq / result.length;
                list.add( frequency );
            }
        }

        return list;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "\n    Magnitudes:\n" );

        int endIndex = result.length / 2 + 1;
        for( int q = 0; q < endIndex; q++ )
        {
            double magnitude = result[ q ].abs();
            double frequency = q * samplingFreq / result.length;

            sb.append( String.format( "        %s Hz is %s\n", frequency, magnitude ) );
        }

        sb.append( "    Peak frequencies:\n" );
        List< Double > peaks = getPeakFrequencies( 0.0 );
        for( Double peak : peaks )
        {
            sb.append( String.format( "        %s Hz \n", peak ) );
        }

        // calculate average
        sb.append( "    Average: \n" );
        double summOfElements = 0;
        double summOfElements2 = 0;
        double numOfelements = result.length / 2 + 1;
        for( int q = 0; q < result.length / 2 + 1; q++ )
        {
            summOfElements += result[ q ].abs();
            summOfElements2 += result[ q ].abs() * result[ q ].abs();
        }
        double avg = summOfElements / (result.length / 2 + 1);
        // dev jest estymatorem obciążonym
        // https://pl.wikipedia.org/wiki/Odchylenie_standardowe
        double dev =
            Math.sqrt( numOfelements / (numOfelements - 1) * (summOfElements2 / numOfelements - avg * avg) );
        // żeby wyznaczyć estymator nieobciążonoy trzeba podzielić przez wskaznik dal próby 5-cio elementowej
        // https://pl.wikisource.org/wiki/Czynnik_c4
        double devUnbalasted = dev / 0.93999;
        sb.append( String.format( "        avg = %s \n", avg ) );
        sb.append( String.format( "        dev = %s \n", devUnbalasted ) );

        return sb.toString();
    }
}
