package com.github.sp3wam.baseband.modem.impl.morse.decoder;

import java.io.IOException;

import com.github.sp3wam.baseband.modem.core.BlockListenerIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromJavaxAudioSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromMp3FileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromWavFileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.pcm.PcmSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.impl.morse.detector.MorseSignalDetectorByPercentageSpectrumBlock;

public class MorseDecoder
{
    private final double SIGNAL_AMPLITUDE = 100.0;
    private double signalThreshold = 16.0;
    private MorseSignalDetectorByPercentageSpectrumBlock morseSignalDetectorBlock = null;
    private BlockListenerIf< FloatingPointSignal > fftSamplerListener = null;

    public void setSignalThreshold( double threshold )
    {
        this.signalThreshold = threshold;

        if( morseSignalDetectorBlock != null )
        {
            morseSignalDetectorBlock.setSignalThreshold( signalThreshold );
        }
    }

    public void addFftSamplerListener( BlockListenerIf< FloatingPointSignal > fftSamplerListener )
    {
        this.fftSamplerListener = fftSamplerListener;

        if( morseSignalDetectorBlock != null )
        {
            morseSignalDetectorBlock.addFftSamplerListener( fftSamplerListener );
        }
    }

    /***
     * Decodes from WAV file synchronously
     * 
     * @param filePath
     * @param consumer
     * @throws IOException
     */
    public void decodeFromWavSync( String filePath, MorseDecoderConsumer consumer ) throws IOException
    {
        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.setGenerateSilenceAtEnd( 1000 );
        signalGenerator.init();

        decodeSync( signalGenerator, consumer );
    }

    /***
     * Decodes from WAV file synchronously
     * 
     * @param filePath
     * @param consumer
     * @throws IOException
     */
    public void decodeFromMp3Sync( String filePath, MorseDecoderConsumer consumer ) throws IOException
    {
        PcmFromMp3FileSignalGeneratorBlock signalGenerator =
            new PcmFromMp3FileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.setGenerateSilenceAtEnd( 1000 );
        signalGenerator.init();

        decodeSync( signalGenerator, consumer );
    }

    /***
     * Decodes from JAVAX audio asynchronously. The method return immediatelly and decoding is
     * 
     * @param consumer
     * @throws IOException
     */
    public void decodeFromJavaxAudioSync( MorseDecoderConsumer consumer ) throws IOException
    {
        PcmFromJavaxAudioSignalGeneratorBlock signalGenerator =
            new PcmFromJavaxAudioSignalGeneratorBlock( SIGNAL_AMPLITUDE );
        signalGenerator.init();

        decodeSync( signalGenerator, consumer );
    }

    public void decodeFromXtAudioSync( MorseDecoderConsumer consumer ) throws IOException
    {
        PcmFromJavaxAudioSignalGeneratorBlock signalGenerator =
            new PcmFromJavaxAudioSignalGeneratorBlock( SIGNAL_AMPLITUDE );
        signalGenerator.init();

        decodeSync( signalGenerator, consumer );
    }

    private void decodeSync( PcmSignalGeneratorBlock signalGenerator, MorseDecoderConsumer consumer )
    {
        morseSignalDetectorBlock =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        morseSignalDetectorBlock.setFftParams( 32, 3000.0 );
        morseSignalDetectorBlock.setDesiredOutputSignalSampleFreq( 200.0 );
        morseSignalDetectorBlock.setSignalThreshold( signalThreshold );
        if( fftSamplerListener != null )
        {
            morseSignalDetectorBlock.addFftSamplerListener( fftSamplerListener );
        }

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
