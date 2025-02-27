package com.github.sp3wam.baseband.modem.impl.morse.detector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.AbstractConsumerBlock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.BitSignal;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromWavFileSignalGeneratorBlock;

public class MorseSignalDetectorByPercentageSpectrumBlockTest
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseSignalDetectorByPercentageSpectrumBlockTest.class );

    private final double SIGNAL_AMPLITUDE = 100.0;
    private final double FFT_SAMPLE_RATE = 6000.0;

    @Test
    public void testRealLoudNoise_FFT08_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 31;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 8, FFT_SAMPLE_RATE );
        subject.setDesiredOutputSignalSampleFreq( FFT_SAMPLE_RATE );
        subject.setSignalThreshold( minimalSignalThrehsold );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info(
            String.format( "Percentage ratio of detected 0s is >=%s", consumer.getZerosPercentageRatio() ) );

        // For noise there should be no signals detected
        // or at least signal ratio should be very low
        assertTrue( consumer.getZerosPercentageRatio() > 95.0 ); // acceptable percentage error...
        assertEquals( 31.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealLoudNoise_FFT16_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 20;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 16, FFT_SAMPLE_RATE );
        subject.setDesiredOutputSignalSampleFreq( FFT_SAMPLE_RATE );
        subject.setSignalThreshold( minimalSignalThrehsold );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info(
            String.format( "Percentage ratio of detected 0s is >=%s", consumer.getZerosPercentageRatio() ) );

        // For noise there should be no signals detected
        // or at least signal ratio should be very low
        assertTrue( consumer.getZerosPercentageRatio() > 95.0 ); // acceptable percentage error...
        assertEquals( 20.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealLoudNoise_FFT32_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 11;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 32, FFT_SAMPLE_RATE );
        subject.setDesiredOutputSignalSampleFreq( FFT_SAMPLE_RATE );
        subject.setSignalThreshold( minimalSignalThrehsold );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info(
            String.format( "Percentage ratio of detected 0s is >=%s", consumer.getZerosPercentageRatio() ) );

        // For noise there should be no signals detected
        // or at least signal ratio should be very low
        assertTrue( consumer.getZerosPercentageRatio() > 95.0 ); // acceptable percentage error...
        assertEquals( 11.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealLoudNoise_FFT64_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 7;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 64, FFT_SAMPLE_RATE );
        subject.setDesiredOutputSignalSampleFreq( FFT_SAMPLE_RATE );
        subject.setSignalThreshold( minimalSignalThrehsold );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info(
            String.format( "Percentage ratio of detected 0s is >=%s", consumer.getZerosPercentageRatio() ) );

        // For noise there should be no signals detected
        // or at least signal ratio should be very low
        assertTrue( consumer.getZerosPercentageRatio() >= 95.0 ); // acceptable percentage error...
        assertEquals( 7.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealNoisedSignal_FFT08_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 18;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_noised_signal.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 8, FFT_SAMPLE_RATE );
        subject.setDesiredOutputSignalSampleFreq( FFT_SAMPLE_RATE );
        subject.setSignalThreshold( minimalSignalThrehsold );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info(
            String.format( "Percentage ratio of detected 1s is >=%s", consumer.getOnesPercentageRatio() ) );

        // For noised signal there should be no silence detected
        // or at least signal ratio should be very high
        assertTrue( consumer.getOnesPercentageRatio() > 95.0 ); // acceptable percentage error...
        assertEquals( 18.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealNoisedSignal_FFT16_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 13;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_noised_signal.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 16, FFT_SAMPLE_RATE );
        subject.setDesiredOutputSignalSampleFreq( FFT_SAMPLE_RATE );
        subject.setSignalThreshold( minimalSignalThrehsold );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info(
            String.format( "Percentage ratio of detected 1s is >=%s", consumer.getOnesPercentageRatio() ) );

        // For noised signal there should be no silence detected
        // or at least signal ratio should be very high
        assertTrue( consumer.getOnesPercentageRatio() > 95.0 ); // acceptable percentage error...
        assertEquals( 13.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealNoisedSignal_FFT32_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 12;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_noised_signal.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 32, FFT_SAMPLE_RATE );
        subject.setDesiredOutputSignalSampleFreq( FFT_SAMPLE_RATE );
        subject.setSignalThreshold( minimalSignalThrehsold );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info(
            String.format( "Percentage ratio of detected 1s is >=%s", consumer.getOnesPercentageRatio() ) );

        // For noised signal there should be no silence detected
        // or at least signal ratio should be very high
        assertTrue( consumer.getOnesPercentageRatio() >= 95.0 ); // acceptable percentage error...
        assertEquals( 12.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealNoisedSignal_FFT64_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 9;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_noised_signal.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 64, FFT_SAMPLE_RATE );
        subject.setDesiredOutputSignalSampleFreq( FFT_SAMPLE_RATE );
        subject.setSignalThreshold( minimalSignalThrehsold );
        MorseSignalDetectorConsumer consumer = new MorseSignalDetectorConsumer();

        signalGenerator.setNextBlock( subject );
        subject.setNextBlock( consumer );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }

        LOGGER.info(
            String.format( "Percentage ratio of detected 1s is >=%s", consumer.getOnesPercentageRatio() ) );

        // For noised signal there should be no silence detected
        // or at least signal ratio should be very high
        assertTrue( consumer.getOnesPercentageRatio() >= 95.0 ); // acceptable percentage error...
        assertEquals( 9.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
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

        public double getZerosPercentageRatio()
        {
            return 100.0 * (double)countOfZeros / ((double)(countOfZeros + countOfOnes));
        }
    }

}
