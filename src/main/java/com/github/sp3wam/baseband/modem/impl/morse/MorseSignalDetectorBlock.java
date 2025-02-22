package com.github.sp3wam.baseband.modem.impl.morse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.BitAveragerBlock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.BitSignal;
import com.github.sp3wam.baseband.modem.core.basic.blocks.FloatingPointAveragerBlock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.FloatingPointAvgMagnitudeCalculatorBlock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.SamplerBlock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.ToneToBitConverterBlock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTBlock;
import com.github.sp3wam.baseband.modem.core.fft.FFTSignal;

public class MorseSignalDetectorBlock extends AbstractBlock< FloatingPointSignal, BitSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseSignalDetectorBlock.class );

    private final int FFT_WINDOW_SIZE = 16;
    private final static double FFT_DESIRED_SAMPLE_FREQ = 6000.0;
    private final static double BIT_DESIRED_SAMPLE_FREQ = 100.0;

    private FloatingPointAveragerBlock floatingPointAveragerBlock;
    private SamplerBlock< FloatingPointSignal, FloatingPointSignal > fftFasterSampler;
    private FloatingPointAvgMagnitudeCalculatorBlock avgMagnitude;
    private FFTBlock fftBlock;
    private SamplerBlock< FFTSignal, FFTSignal > fftSlowerSampler;
    private MorseToneDetectorBlock morseToneDetectorBlock;
    private ToneToBitConverterBlock toneToBitConverterBlock;
    private BitAveragerBlock bitAveragerBlock;

    public MorseSignalDetectorBlock( double inputSignalSampleRate )
    {
        int fftSamplerDivider = (int)(inputSignalSampleRate / FFT_DESIRED_SAMPLE_FREQ);
        int fftSampleFreq = (int)(inputSignalSampleRate / fftSamplerDivider);
        int bitSamplerDivider = (int)(fftSampleFreq / BIT_DESIRED_SAMPLE_FREQ);

        floatingPointAveragerBlock = new FloatingPointAveragerBlock( 1 );
        avgMagnitude = new FloatingPointAvgMagnitudeCalculatorBlock( (int)inputSignalSampleRate );
        fftFasterSampler = new SamplerBlock< FloatingPointSignal, FloatingPointSignal >( fftSamplerDivider );
        fftBlock = new FFTBlock( fftSampleFreq, FFT_WINDOW_SIZE );
        fftSlowerSampler = new SamplerBlock< FFTSignal, FFTSignal >( bitSamplerDivider );
        morseToneDetectorBlock = new MorseToneDetectorBlock();
        toneToBitConverterBlock = new ToneToBitConverterBlock();
        bitAveragerBlock = new BitAveragerBlock( 3 );

        // connect the blocks
        floatingPointAveragerBlock.setNextBlock( avgMagnitude );
        avgMagnitude.setNextBlock( fftFasterSampler );
        fftFasterSampler.setNextBlock( fftBlock );
        fftBlock.setNextBlock( fftSlowerSampler );
        fftSlowerSampler.setNextBlock( morseToneDetectorBlock );
        morseToneDetectorBlock.setNextBlock( toneToBitConverterBlock );
        toneToBitConverterBlock.setNextBlock( bitAveragerBlock );
    }

    protected boolean execute0( SystemClock systemClock, FloatingPointSignal inputSignalValue )
    {
        floatingPointAveragerBlock.execute( systemClock, inputSignalValue );

        currentValue = bitAveragerBlock.getCurrentValue();

        return true;
    }
}
