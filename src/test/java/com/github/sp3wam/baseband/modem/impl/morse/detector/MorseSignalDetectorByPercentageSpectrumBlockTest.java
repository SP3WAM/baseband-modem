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

    @Test
    public void testRealLoudNoise_FFT08_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 32;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 8, 44100.0 );
        subject.setDesiredOutputSignalSampleFreq( 44100.0 );
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

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noise there should be no signals detected
        // or at least signal ratio should be very low
        assertTrue( consumer.getOnesPercentageRatio() < 5.0 ); // acceptable percentage error...
        assertEquals( 32.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealLoudNoise_FFT16_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 41;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 16, 44100.0 );
        subject.setDesiredOutputSignalSampleFreq( 44100.0 );
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

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noise there should be no signals detected
        // or at least signal ratio should be very low
        assertTrue( consumer.getOnesPercentageRatio() < 5.0 ); // acceptable percentage error...
        assertEquals( 41.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealLoudNoise_FFT32_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 34;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 32, 44100.0 );
        subject.setDesiredOutputSignalSampleFreq( 44100.0 );
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

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noise there should be no signals detected
        // or at least signal ratio should be very low
        assertTrue( consumer.getOnesPercentageRatio() < 5.0 ); // acceptable percentage error...
        assertEquals( 34.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealNoisedSignal_FFT08_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 20;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_noised_signal.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 8, 44100.0 );
        subject.setDesiredOutputSignalSampleFreq( 44100.0 );
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

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noised signal there should be no silence detected
        // or at least signal ratio should be very high
        assertTrue( consumer.getOnesPercentageRatio() > 95.0 ); // acceptable percentage error...
        assertEquals( 20.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealNoisedSignal_FFT16_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 15;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_noised_signal.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 16, 44100.0 );
        subject.setDesiredOutputSignalSampleFreq( 44100.0 );
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

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noised signal there should be no silence detected
        // or at least signal ratio should be very high
        assertTrue( consumer.getOnesPercentageRatio() > 95.0 ); // acceptable percentage error...
        assertEquals( 15.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
    }

    @Test
    public void testRealNoisedSignal_FFT32_fromWav() throws IOException
    {
        double minimalSignalThrehsold = 17;

        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_noised_signal.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorByPercentageSpectrumBlock subject =
            new MorseSignalDetectorByPercentageSpectrumBlock( signalGenerator.getSampleRate() );
        subject.setFftParams( 32, 44100.0 );
        subject.setDesiredOutputSignalSampleFreq( 44100.0 );
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

        LOGGER.info( String.format( "Percentage ratio of 1 is %s", consumer.getOnesPercentageRatio() ) );

        // For noised signal there should be no silence detected
        // or at least signal ratio should be very high
        assertTrue( consumer.getOnesPercentageRatio() > 95.0 ); // min error percentage value...
        assertEquals( 17.0, minimalSignalThrehsold, 0.1 ); // ... with this minimal threshold
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
