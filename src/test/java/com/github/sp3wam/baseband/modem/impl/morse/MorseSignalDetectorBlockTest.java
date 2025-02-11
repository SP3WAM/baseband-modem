package com.github.sp3wam.baseband.modem.impl.morse;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.blocks.AbstractConsumerBlock;
import com.github.sp3wam.baseband.modem.core.blocks.PcmFromMp3FileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.blocks.PcmFromWavFileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.signals.BitSignal;

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
        assertTrue( consumer.getOnesPercentageRatio() < 5.0 );
    }

    @Test
    public void testRealLoudNoise_fromMp3() throws IOException
    {
         String filePath =
         "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_loud_noise.mp3";

//        String filePath =
//            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/White-noise-sound-20sec-mono-44100Hz.mp3";

        PcmFromMp3FileSignalGeneratorBlock signalGenerator =
            new PcmFromMp3FileSignalGeneratorBlock( SIGNAL_AMPLITUDE, filePath );
        signalGenerator.init();

        MorseSignalDetectorBlock subject = new MorseSignalDetectorBlock( signalGenerator.getSampleRate() );
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
        assertTrue( consumer.getOnesPercentageRatio() < 5.0 );
    }

    private class MorseSignalDetectorConsumer extends AbstractConsumerBlock< BitSignal, BitSignal >
    {
        private long countOfZeros = 0;
        private long countOfOnes = 0;

        @Override
        public void execute( SystemClock systemClock, BitSignal inputSignalValue )
        {
            if( inputSignalValue.getBitValue() )
            {
                countOfOnes++;
            }
            else
            {
                countOfZeros++;
            }
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
