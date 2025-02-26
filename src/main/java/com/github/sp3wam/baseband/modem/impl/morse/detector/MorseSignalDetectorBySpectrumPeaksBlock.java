package com.github.sp3wam.baseband.modem.impl.morse.detector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.basic.blocks.BitSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTPeak;
import com.github.sp3wam.baseband.modem.core.fft.FFTPeaks;
import com.github.sp3wam.baseband.modem.core.fft.FFTSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTSpectrum;

/**
 * From the input audio signal samples detects the Morse signal into a stream of {@linkplain BitSignal}:
 * <ul>
 * <li>bit signal of true - means there is a audible beep (dit or dah)</li>
 * <li>bit signal of false - means there is no audible beep (just noise)</li>
 * </ul>
 * <p>
 * It uses a spectrum peaks analysis to find out if the signal is present.
 */
public class MorseSignalDetectorBySpectrumPeaksBlock extends AbstractMorseSignalDetectorBlock
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseSignalDetectorBySpectrumPeaksBlock.class );

    public MorseSignalDetectorBySpectrumPeaksBlock( double inputSignalSampleRate )
    {
        super( inputSignalSampleRate );
    }

    @Override
    protected BitSignal detectSignal( FFTSignal fftSignal )
    {
        FFTSpectrum fftSpectrum = new FFTSpectrum( fftSignal );
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
            return new BitSignal( false );
        }

        // check if the magnitude of max power peak is big enough
        if( maxPowerPeak.getMagnitude() < 100.0 )
        {
            return new BitSignal( false );
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
                return new BitSignal( false );
            }
        }

        LOGGER.debug( String.format( "Detected signal from spectrum %s and peaks %s and peak %s",
            fftSpectrum.toString(), fftPeaks.toString(), maxPowerPeak.toString() ) );

        return new BitSignal( true );
    }
}
