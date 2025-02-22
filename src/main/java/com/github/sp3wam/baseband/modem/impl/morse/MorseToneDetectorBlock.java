package com.github.sp3wam.baseband.modem.impl.morse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.fft.FFTPeaks;
import com.github.sp3wam.baseband.modem.core.fft.FFTSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTSpectrum;

class MorseToneDetectorBlock extends AbstractBlock< FFTSignal, MorseToneSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseToneDetectorBlock.class );

    protected boolean execute0( SystemClock systemClock, FFTSignal inputSignalValue )
    {
        FFTSpectrum fftSpectrum = new FFTSpectrum( inputSignalValue );
        FFTPeaks fftPeaks = new FFTPeaks( fftSpectrum, 100.0 );

        if( fftPeaks.getPeakFrequencies().size() == 1 )
        {
            currentValue = new MorseToneSignal( fftPeaks.getPeakFrequencies().get( 0 ) );
        }
        else
        {
            currentValue = new MorseToneSignal( 0.0 );
        }

        LOGGER.debug( String.format( "Detected tone %s Hz from peaks %s", currentValue.getToneFrequency(),
            fftPeaks.toString() ) );

        return true;
    }
}
