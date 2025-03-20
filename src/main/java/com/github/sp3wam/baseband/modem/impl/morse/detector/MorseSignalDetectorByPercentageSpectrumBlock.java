package com.github.sp3wam.baseband.modem.impl.morse.detector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.basic.blocks.BitSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTPeaks;
import com.github.sp3wam.baseband.modem.core.fft.FFTSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTSpectrum;

public class MorseSignalDetectorByPercentageSpectrumBlock extends AbstractMorseSignalDetectorBlock
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseSignalDetectorByPercentageSpectrumBlock.class );

    private double signalThreshold = 10.0;

    public MorseSignalDetectorByPercentageSpectrumBlock( double inputSignalSampleRate )
    {
        super( inputSignalSampleRate );
    }

    public void setSignalThreshold( double signalThreshold )
    {
        this.signalThreshold = signalThreshold;
    }

    @Override
    protected BitSignal detectSignal( FFTSignal fftSignal )
    {
        FFTSpectrum spectrum = new FFTSpectrum( fftSignal );

        FFTPeaks fftPeaks = new FFTPeaks(spectrum, 25);
        if(fftPeaks.getPeaks().size() == 0)
        {
            LOGGER.debug( "Detected signal: 0" );
            return new BitSignal( false );
        }
        
        double[] percentagePSD = spectrum.getPowerSpectralDensityPercentageValues();

        
        for( int q = 0; q < percentagePSD.length; q++ )
        {
            if( percentagePSD[ q ] >= signalThreshold )
            {
                LOGGER.debug( "Detected signal: 1" );
                return new BitSignal( true );
            }
        }

        LOGGER.debug( "Detected signal: 0" );
        return new BitSignal( false );
    }

}
