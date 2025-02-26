package com.github.sp3wam.baseband.modem.impl.morse.detector;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.AbstractConsumerBlock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.BitSignal;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromWavFileSignalGeneratorBlock;

public class MorseSignalDetectorBySpectrumPeaksBlockTest
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseSignalDetectorBySpectrumPeaksBlockTest.class );

    private final double SIGNAL_AMPLITUDE = 100.0;

    @Test
    public void testRealLoudNoise_FFT08_fromWav() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorBySpectrumPeaksBlock subject =
            new MorseSignalDetectorBySpectrumPeaksBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 8, 6000.0 );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noise there should be no signals detected
        // or at least signal ratio should be very low
        assertTrue( consumer.getOnesPercentageRatio() < 11.0 );
    }

    @Test
    public void testRealLoudNoise_FFT16_fromWav() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorBySpectrumPeaksBlock subject =
            new MorseSignalDetectorBySpectrumPeaksBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 16, 6000.0 );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noise there should be no signals detected
        // or at least signal ratio should be very low
        assertTrue( consumer.getOnesPercentageRatio() < 36.0 );
    }

    @Test
    public void testRealLoudNoise_FFT32_fromWav() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorBySpectrumPeaksBlock subject =
            new MorseSignalDetectorBySpectrumPeaksBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 32, 6000.0 );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noise there should be no signals detected
        // or at least signal ratio should be very low
        assertTrue( consumer.getOnesPercentageRatio() < 15.0 );
    }

    @Test
    public void testRealNoisedSignal_FFT08_fromWav() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_noised_signal.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorBySpectrumPeaksBlock subject =
            new MorseSignalDetectorBySpectrumPeaksBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 8, 6000.0 );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noised signal there should be no silence detected
        // or at least signal ratio should be very high
        assertTrue( consumer.getOnesPercentageRatio() > 84.0 );
    }

    @Test
    public void testRealNoisedSignal_FFT16_fromWav() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_noised_signal.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorBySpectrumPeaksBlock subject =
            new MorseSignalDetectorBySpectrumPeaksBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 16, 6000.0 );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noised signal there should be no silence detected
        // or at least signal ratio should be very high
        assertTrue( consumer.getOnesPercentageRatio() > 94.0 );
    }

    @Test
    public void testRealNoisedSignal_FFT32_fromWav() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_noised_signal.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorBySpectrumPeaksBlock subject =
            new MorseSignalDetectorBySpectrumPeaksBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 32, 6000.0 );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noised signal there should be no silence detected
        // or at least signal ratio should be very high
        assertTrue( consumer.getOnesPercentageRatio() > 94.0 );
    }

    private class MorseSignalDetectorConsumer extends AbstractConsumerBlock< BitSignal, BitSignal >
    {
        private long countOfZeros = 0;
        private long countOfOnes = 0;

        @Override
        public boolean execute0( SystemClock systemClock, BitSignal inputSignalValue )
        {
            if( inputSignalValue.getBitValue() )
            {
                countOfOnes++;
            }
            else
            {
                countOfZeros++;
            }

            return false;
        }

        public long getCountOfZeros()
        {
            return countOfZeros;
        }

        public long getCountOfOnes()
        {
            return countOfOnes;
        }

        public double getOnesPercentageRatio()
        {
            return 100.0 * (double)countOfOnes / ((double)(countOfZeros + countOfOnes));
        }
    }

}
