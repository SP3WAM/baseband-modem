package com.github.sp3wam.baseband.modem.impl.morse;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.AbstractConsumerBlock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.BitSignal;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromMp3FileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromWavFileSignalGeneratorBlock;

public class MorseSignalDetectorBlockTest
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseSignalDetectorBlockTest.class );

    private final double SIGNAL_AMPLITUDE = 100.0;

    @Test
    public void testRealLoudNoise_fromWav() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorBlock subject = new MorseSignalDetectorBlock( signalGenerator.getSampleRate() );
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
        // TODO: why the same signal read from MP3 has signal ratio of 13.0?
        // TODO: 16.0% is too high. Make it somehow smaller.
        assertTrue( consumer.getOnesPercentageRatio() < 16.0 );
    }

    @Test
    public void testRealLoudNoise_fromMp3() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.mp3";

        // String filePath =
        // "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/White-noise-sound-20sec-mono-44100Hz.mp3";

        PcmFromMp3FileSignalGeneratorBlock signalGenerator =
            new PcmFromMp3FileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorBlock subject = new MorseSignalDetectorBlock( signalGenerator.getSampleRate() );
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
        // TODO: why the same signal read from MP3 has signal ratio of 16.0?
        // TODO: 13.0% is too high. Make it somehow smaller.
        assertTrue( consumer.getOnesPercentageRatio() < 13.0 );
    }

    @Test
    public void testRealNoisedSignal_fromWav() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_noised_signal.wav";

        PcmFromWavFileSignalGeneratorBlock signalGenerator =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorBlock subject = new MorseSignalDetectorBlock( signalGenerator.getSampleRate() );
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
        // TODO: 14.0% is a way too low. Make it somehow bigger.
        assertTrue( consumer.getOnesPercentageRatio() > 17.0 );
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
