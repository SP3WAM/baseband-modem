package com.github.sp3wam.baseband.modem.core.blocks;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.SamplerBlock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromWavFileSignalGeneratorBlock;

public class SamplerBlockTest
{
    private final double SIGNAL_AMPLITUDE = 100.0;
    private final int FFT_REQUIRED_SAMPLE_FREQ = 6400;

    @Test
    public void test() throws IOException
    {
        PcmFromWavFileSignalGeneratorBlock wavSignal =
            new PcmFromWavFileSignalGeneratorBlock( SIGNAL_AMPLITUDE,
                "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/C_morse_code.wav" );
        wavSignal.init();

        int samplerDivider = (int)(wavSignal.getSampleRate() / FFT_REQUIRED_SAMPLE_FREQ);

        assertEquals( 6, samplerDivider );

        SamplerBlock< FloatingPointSignal, FloatingPointSignal > fftSampler =
            new SamplerBlock< FloatingPointSignal, FloatingPointSignal >( samplerDivider );

        wavSignal.setNextBlock( fftSampler );

        SystemClock systemClock = new SystemClock( wavSignal.getSampleRate() );

        while( wavSignal.hasMoreSamples() )
        {
            wavSignal.execute( systemClock, null );

            systemClock.step();
        }

        assertEquals( 53803, systemClock.getClockValue() );
        assertEquals( 8968, fftSampler.getSamplesTaken() );
    }
}
