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

        return sb.toString();
    }
}
