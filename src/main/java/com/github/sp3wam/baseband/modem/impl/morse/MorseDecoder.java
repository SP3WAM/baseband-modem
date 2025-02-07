package com.github.sp3wam.baseband.modem.impl.morse;

import java.io.IOException;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.blocks.FFTBlock;
import com.github.sp3wam.baseband.modem.core.blocks.PcmFromFileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.blocks.PcmFromMp3FileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.blocks.PcmFromWavFileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.blocks.SamplerBlock;
import com.github.sp3wam.baseband.modem.core.blocks.ToneToBitConverterBlock;
import com.github.sp3wam.baseband.modem.core.signals.BitSignal;
import com.github.sp3wam.baseband.modem.core.signals.FloatingPointSignal;

public class MorseDecoder
{
    private final double SIGNAL_AMPLITUDE = 100.0;
    private final int FFT_WINDOW_SIZE = 16;
    private final static double FFT_DESIRED_SAMPLE_FREQ = 6000.0;
    private final static double BIT_DESIRED_SAMPLE_FREQ = 100.0;

    public void decodeFromWav( String filePath, MorseDecoderConsumer consumer ) throws IOException
    {
        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );

        decode( signalGenerator, consumer );
    }

    public void decodeFromMp3( String filePath, MorseDecoderConsumer consumer ) throws IOException
    {
        PcmFromMp3FileSignalGeneratorBlock signalGenerator =
            new PcmFromMp3FileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );

        decode( signalGenerator, consumer );
    }

    private void decode( PcmFromFileSignalGeneratorBlock signalGenerator, MorseDecoderConsumer consumer )
    {
        double sampleRate = signalGenerator.getSampleRate();
        int fftSamplerDivider = (int)(sampleRate / FFT_DESIRED_SAMPLE_FREQ);
        int fftSampleFreq = (int)(sampleRate / fftSamplerDivider);
        int bitSamplerDivider = (int)(fftSampleFreq / BIT_DESIRED_SAMPLE_FREQ);

        SamplerBlock< FloatingPointSignal, FloatingPointSignal > fftSampler =
            new SamplerBlock< FloatingPointSignal, FloatingPointSignal >( fftSamplerDivider );
        FFTBlock fftBlock = new FFTBlock( fftSampleFreq, FFT_WINDOW_SIZE );
        MorseToneDetectorBlock morseToneDetectorBlock = new MorseToneDetectorBlock();
        ToneToBitConverterBlock toneToBitConverterBlock = new ToneToBitConverterBlock();

        SamplerBlock< BitSignal, BitSignal > bitSampler =
            new SamplerBlock< BitSignal, BitSignal >( bitSamplerDivider );
        BitStreamMorseDecoderBlock morseDecoderBlock = new BitStreamMorseDecoderBlock();
        MorseSymbolDecoderBlock morseSymbolDecoderBlock = new MorseSymbolDecoderBlock();

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );

        // connect the blocks
        signalGenerator.setNextBlock( fftSampler );
        fftSampler.setNextBlock( fftBlock );
        fftBlock.setNextBlock( morseToneDetectorBlock );
        morseToneDetectorBlock.setNextBlock( toneToBitConverterBlock );
        toneToBitConverterBlock.setNextBlock( bitSampler );
        bitSampler.setNextBlock( morseDecoderBlock );
        morseDecoderBlock.setNextBlock( morseSymbolDecoderBlock );
        morseSymbolDecoderBlock.setNextBlock( consumer );

        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        System.currentTimeMillis();
    }
}
