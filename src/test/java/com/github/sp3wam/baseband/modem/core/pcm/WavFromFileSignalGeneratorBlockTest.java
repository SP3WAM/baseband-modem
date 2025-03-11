package com.github.sp3wam.baseband.modem.core.pcm;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.sp3wam.baseband.modem.core.SystemClock;

public class WavFromFileSignalGeneratorBlockTest
{
    private final double SIGNAL_AMPLITUDE = 100.0;

    @Test
    public void test() throws IOException
    {
        PcmFromWavFileSignalGeneratorBlock wavSignal =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE,
                "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/C_morse_code.wav" );
        wavSignal.init();

        assertEquals( 44100, wavSignal.getSampleRate() );

        SystemClock systemClock = new SystemClock( wavSignal.getSampleRate() );

        while( wavSignal.hasMoreSamples() )
        {
            wavSignal.execute( systemClock, null );

            systemClock.step();
        }

        assertEquals( 53803, systemClock.getClockValue() );
    }
}
