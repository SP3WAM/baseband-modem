package com.github.sp3wam.baseband.modem.impl.morse.detector;

import com.github.sp3wam.baseband.modem.core.basic.blocks.BitSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTSpectrum;

public class MorseSignalDetectorByPercentageSpectrumBlock extends AbstractMorseSignalDetectorBlock
{

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

        double[] percentagePSD = spectrum.getPowerSpectralDensityPercentageValues();

        for( int q = 0; q < percentagePSD.length; q++ )
        {
            if( percentagePSD[ q ] >= signalThreshold )
            {
                return new BitSignal( true );
            }
        }

        return new BitSignal( false );
    }

}
