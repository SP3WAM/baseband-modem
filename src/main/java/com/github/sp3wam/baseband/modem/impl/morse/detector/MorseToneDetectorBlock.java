package com.github.sp3wam.baseband.modem.impl.morse.detector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.fft.FFTPeak;
import com.github.sp3wam.baseband.modem.core.fft.FFTPeaks;
import com.github.sp3wam.baseband.modem.core.fft.FFTSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTSpectrum;

/***
 * From the provided FFT result finds out if we have a real beep (dit or dah) signal or noise. Result provided
 * as {@linkplain MorseToneSignal}.
 */
class MorseToneDetectorBlock extends AbstractBlock< FFTSignal, MorseToneSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseToneDetectorBlock.class );

    protected boolean execute0( SystemClock systemClock, FFTSignal inputSignalValue )
    {
        FFTSpectrum fftSpectrum = new FFTSpectrum( inputSignalValue );
        FFTPeaks fftPeaks = new FFTPeaks( fftSpectrum, 100.0 );

        // find peak with max power percentage
        FFTPeak maxPowerPeak = null;
        for( FFTPeak fftPeak : fftPeaks.getPeaks() )
        {
            if( maxPowerPeak == null )
            {
                maxPowerPeak = fftPeak;
            }
            if( fftPeak.getPowerPercentage() > maxPowerPeak.getPowerPercentage() )
            {
                maxPowerPeak = fftPeak;
            }
        }

        if( maxPowerPeak == null )
        {
            currentValue = new MorseToneSignal( 0.0 );

            return true;
        }

        // check if the magnitude of max power peak is big enough
        if( maxPowerPeak.getMagnitude() < 100.0 )
        {
            currentValue = new MorseToneSignal( 0.0 );

            return true;
        }

        // check if the max power peak is at least twice bigger than the others
        for( FFTPeak fftPeak : fftPeaks.getPeaks() )
        {
            if( maxPowerPeak == fftPeak )
            {
                continue;
            }

            if( maxPowerPeak.getPowerPercentage() < 2.0 * fftPeak.getPowerPercentage() )
            {
                currentValue = new MorseToneSignal( 0.0 );

                return true;
            }
        }

        currentValue = new MorseToneSignal( maxPowerPeak.getFrequency() );
        LOGGER.debug( String.format( "Detected tone %s Hz from spectrum %s and peaks %s and peak %s",
            currentValue.getToneFrequency(), fftSpectrum.toString(), fftPeaks.toString(),
            maxPowerPeak.toString() ) );

        return true;
    }
}
