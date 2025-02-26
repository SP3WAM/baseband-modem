package com.github.sp3wam.baseband.modem.impl.morse.decoder;

import java.io.IOException;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromJavaxAudioSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromMp3FileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromWavFileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.pcm.PcmSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.impl.morse.detector.MorseSignalDetectorBlock;

public class MorseDecoder
{
    private final double SIGNAL_AMPLITUDE = 100.0;

    public void decodeFromWav( String filePath, MorseDecoderConsumer consumer ) throws IOException
    {
        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.setGenerateSilenceAtEnd( 1000 );
        signalGenerator.init();

        decode( signalGenerator, consumer );
    }

    public void decodeFromMp3( String filePath, MorseDecoderConsumer consumer ) throws IOException
    {
        PcmFromMp3FileSignalGeneratorBlock signalGenerator =
            new PcmFromMp3FileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.setGenerateSilenceAtEnd( 1000 );
        signalGenerator.init();

        decode( signalGenerator, consumer );
    }

    public void decodeFromJavaxAudio( MorseDecoderConsumer consumer ) throws IOException
    {
        PcmFromJavaxAudioSignalGeneratorBlock signalGenerator =
            new PcmFromJavaxAudioSignalGeneratorBlock( SIGNAL_AMPLITUDE );
        signalGenerator.init();

        decode( signalGenerator, consumer );
    }

    public void decodeFromXtAudio( MorseDecoderConsumer consumer ) throws IOException
    {
        PcmFromJavaxAudioSignalGeneratorBlock signalGenerator =
            new PcmFromJavaxAudioSignalGeneratorBlock( SIGNAL_AMPLITUDE );
        signalGenerator.init();

        decode( signalGenerator, consumer );
    }

    private void decode( PcmSignalGeneratorBlock signalGenerator, MorseDecoderConsumer consumer )
    {
        MorseSignalDetectorBlock morseSignalDetectorBlock =
            new MorseSignalDetectorBlock( signalGenerator.getSampleRate() );
        BitStreamMorseDecoderBlock morseDecoderBlock = new BitStreamMorseDecoderBlock();
        MorseSymbolDecoderBlock morseSymbolDecoderBlock = new MorseSymbolDecoderBlock();

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );

        // connect the blocks
        signalGenerator.setNextBlock( morseSignalDetectorBlock );
        morseSignalDetectorBlock.setNextBlock( morseDecoderBlock );
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
