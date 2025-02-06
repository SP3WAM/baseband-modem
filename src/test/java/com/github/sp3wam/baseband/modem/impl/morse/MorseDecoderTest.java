package com.github.sp3wam.baseband.modem.impl.morse;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioSystem;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.blocks.AbstractConsumerBlock;
import com.github.sp3wam.baseband.modem.core.blocks.FFTBlock;
import com.github.sp3wam.baseband.modem.core.blocks.Mp3FromFileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.blocks.SamplerBlock;
import com.github.sp3wam.baseband.modem.core.blocks.ToneToBitConverterBlock;
import com.github.sp3wam.baseband.modem.core.blocks.WavFromFileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.signals.BitSignal;
import com.github.sp3wam.baseband.modem.core.signals.FloatingPointSignal;
import com.github.sp3wam.baseband.modem.core.signals.StringSignal;

public class MorseDecoderTest
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseDecoderTest.class );

    @Test
    public void testLetterC_fromWav() throws IOException
    {
        final int MAIN_CLOCK_FREQ = 44100; // meaning 44100 Hz
        final double SIGNAL_AMPLITUDE = 100.0;
        final int FFT_SAMPLING_DIVIDER = 7; // meaning 44100 Hz / 7 = 6300 Hz
        final int FFT_SAMPLE_FREQ = MAIN_CLOCK_FREQ / FFT_SAMPLING_DIVIDER;
        final int FFT_WINDOW_SIZE = 16;
        final int BIT_SAMPLING_DIVIDER = 63; // meaning 6300 Hz / 63 = 100 Hz

        WavFromFileSignalGeneratorBlock wavSignal = new WavFromFileSignalGeneratorBlock( SIGNAL_AMPLITUDE,
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/C_morse_code.wav" );
        SamplerBlock< FloatingPointSignal, FloatingPointSignal > fftSampler =
            new SamplerBlock< FloatingPointSignal, FloatingPointSignal >( FFT_SAMPLING_DIVIDER );
        FFTBlock fftBlock = new FFTBlock( FFT_SAMPLE_FREQ, FFT_WINDOW_SIZE );
        MorseToneDetectorBlock morseToneDetectorBlock = new MorseToneDetectorBlock();
        ToneToBitConverterBlock toneToBitConverterBlock = new ToneToBitConverterBlock();
        SamplerBlock< BitSignal, BitSignal > bitSampler =
            new SamplerBlock< BitSignal, BitSignal >( BIT_SAMPLING_DIVIDER );
        BitStreamMorseDecoderBlock morseDecoderBlock = new BitStreamMorseDecoderBlock();
        MorseSymbolDecoderBlock morseSymbolDecoderBlock = new MorseSymbolDecoderBlock();
        ConsumerBlock consumerBlock = new ConsumerBlock();

        assertEquals( MAIN_CLOCK_FREQ, wavSignal.getSampleRate() );

        SystemClock systemClock = new SystemClock( wavSignal.getSampleRate() );

        // connect the blocks
        wavSignal.setNextBlock( fftSampler );
        fftSampler.setNextBlock( fftBlock );
        fftBlock.setNextBlock( morseToneDetectorBlock );
        morseToneDetectorBlock.setNextBlock( toneToBitConverterBlock );
        toneToBitConverterBlock.setNextBlock( bitSampler );
        bitSampler.setNextBlock( morseDecoderBlock );
        morseDecoderBlock.setNextBlock( morseSymbolDecoderBlock );
        morseSymbolDecoderBlock.setNextBlock( consumerBlock );

        while( wavSignal.hasMoreSamples() )
        {
            wavSignal.execute( systemClock, null );

            systemClock.step();
        }

        System.currentTimeMillis();
    }
    
    @Test
    public void testLetterC_fromMp3() throws IOException
    {
        final int MAIN_CLOCK_FREQ = 44100; // meaning 44100 Hz
        final double SIGNAL_AMPLITUDE = 100.0;
        final int FFT_SAMPLING_DIVIDER = 7; // meaning 44100 Hz / 7 = 6300 Hz
        final int FFT_SAMPLE_FREQ = MAIN_CLOCK_FREQ / FFT_SAMPLING_DIVIDER;
        final int FFT_WINDOW_SIZE = 16;
        final int BIT_SAMPLING_DIVIDER = 63; // meaning 6300 Hz / 63 = 100 Hz

        Mp3FromFileSignalGeneratorBlock wavSignal = new Mp3FromFileSignalGeneratorBlock( SIGNAL_AMPLITUDE,
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/C_morse_code.mp3" );
        SamplerBlock< FloatingPointSignal, FloatingPointSignal > fftSampler =
            new SamplerBlock< FloatingPointSignal, FloatingPointSignal >( FFT_SAMPLING_DIVIDER );
        FFTBlock fftBlock = new FFTBlock( FFT_SAMPLE_FREQ, FFT_WINDOW_SIZE );
        MorseToneDetectorBlock morseToneDetectorBlock = new MorseToneDetectorBlock();
        ToneToBitConverterBlock toneToBitConverterBlock = new ToneToBitConverterBlock();
        SamplerBlock< BitSignal, BitSignal > bitSampler =
            new SamplerBlock< BitSignal, BitSignal >( BIT_SAMPLING_DIVIDER );
        BitStreamMorseDecoderBlock morseDecoderBlock = new BitStreamMorseDecoderBlock();
        MorseSymbolDecoderBlock morseSymbolDecoderBlock = new MorseSymbolDecoderBlock();
        ConsumerBlock consumerBlock = new ConsumerBlock();

        assertEquals( MAIN_CLOCK_FREQ, wavSignal.getSampleRate() );

        SystemClock systemClock = new SystemClock( wavSignal.getSampleRate() );

        // connect the blocks
        wavSignal.setNextBlock( fftSampler );
        fftSampler.setNextBlock( fftBlock );
        fftBlock.setNextBlock( morseToneDetectorBlock );
        morseToneDetectorBlock.setNextBlock( toneToBitConverterBlock );
        toneToBitConverterBlock.setNextBlock( bitSampler );
        bitSampler.setNextBlock( morseDecoderBlock );
        morseDecoderBlock.setNextBlock( morseSymbolDecoderBlock );
        morseSymbolDecoderBlock.setNextBlock( consumerBlock );

        while( wavSignal.hasMoreSamples() )
        {
            wavSignal.execute( systemClock, null );

            systemClock.step();
        }

        System.currentTimeMillis();
    }

    @Test
    public void testWikipedia_fromMp3() throws IOException
    {
        final int MAIN_CLOCK_FREQ = 16000; // meaning 16000 Hz
        final double SIGNAL_AMPLITUDE = 100.0;
        final int FFT_SAMPLING_DIVIDER = 2; // meaning 16000 Hz / 2 = 8000 Hz
        final int FFT_SAMPLE_FREQ = MAIN_CLOCK_FREQ / FFT_SAMPLING_DIVIDER;
        final int FFT_WINDOW_SIZE = 16;
        final int BIT_SAMPLING_DIVIDER = 80; // meaning 8000 Hz / 80 = 100 Hz

        Mp3FromFileSignalGeneratorBlock wavSignal = new Mp3FromFileSignalGeneratorBlock( SIGNAL_AMPLITUDE,
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/Wikipedia-Morse.mp3" );
        SamplerBlock< FloatingPointSignal, FloatingPointSignal > fftSampler =
            new SamplerBlock< FloatingPointSignal, FloatingPointSignal >( FFT_SAMPLING_DIVIDER );
        FFTBlock fftBlock = new FFTBlock( FFT_SAMPLE_FREQ, FFT_WINDOW_SIZE );
        MorseToneDetectorBlock morseToneDetectorBlock = new MorseToneDetectorBlock();
        ToneToBitConverterBlock toneToBitConverterBlock = new ToneToBitConverterBlock();
        SamplerBlock< BitSignal, BitSignal > bitSampler =
            new SamplerBlock< BitSignal, BitSignal >( BIT_SAMPLING_DIVIDER );
        BitStreamMorseDecoderBlock morseDecoderBlock = new BitStreamMorseDecoderBlock();
        MorseSymbolDecoderBlock morseSymbolDecoderBlock = new MorseSymbolDecoderBlock();
        ConsumerBlock consumerBlock = new ConsumerBlock();

        assertEquals( MAIN_CLOCK_FREQ, wavSignal.getSampleRate() );

        SystemClock systemClock = new SystemClock( wavSignal.getSampleRate() );

        // connect the blocks
        wavSignal.setNextBlock( fftSampler );
        fftSampler.setNextBlock( fftBlock );
        fftBlock.setNextBlock( morseToneDetectorBlock );
        morseToneDetectorBlock.setNextBlock( toneToBitConverterBlock );
        toneToBitConverterBlock.setNextBlock( bitSampler );
        bitSampler.setNextBlock( morseDecoderBlock );
        morseDecoderBlock.setNextBlock( morseSymbolDecoderBlock );
        morseSymbolDecoderBlock.setNextBlock( consumerBlock );

        while( wavSignal.hasMoreSamples() )
        {
            wavSignal.execute( systemClock, null );

            systemClock.step();
        }

        System.currentTimeMillis();
    }

    private class ConsumerBlock extends AbstractConsumerBlock< StringSignal, StringSignal >
    {
        private Logger LOGGER = LoggerFactory.getLogger( ConsumerBlock.class );

        @Override
        public void execute( SystemClock systemClock, StringSignal inputSignalValue )
        {
            for( int index = 0; index < inputSignalValue.getValue().length(); index++ )
            {
                LOGGER.info( String.format( "Detected Morse character: %s",
                    inputSignalValue.getValue().charAt( index ) ) );
            }
        }
    }
}
