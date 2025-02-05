package com.github.sp3wam.baseband.modem.core.blocks;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.blocks.WavFromFileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.wav.WavFileException;

public class WavFromFileSignalGeneratorBlockTest
{
    private final double SIGNAL_AMPLITUDE = 100.0;

    @Test
    public void test() throws IOException, WavFileException
    {
        WavFromFileSignalGeneratorBlock wavSignal = new WavFromFileSignalGeneratorBlock( SIGNAL_AMPLITUDE,
            "src/main/resources/morse/C_morse_code.wav" );

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
