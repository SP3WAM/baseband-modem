package com.github.sp3wam.baseband.modem.core.pcm;

import org.junit.Test;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.FloatingPointSinusGeneratorBlock;

public class PcmToFileWriterBlockTest
{

    @Test
    public void test()
    {
        FloatingPointSinusGeneratorBlock generator = new FloatingPointSinusGeneratorBlock( 100.0, 1000.0 );
        PcmToFileWriterBlock pcmWriter = new PcmToFileWriterBlock();

        generator.setNextBlock( pcmWriter );

        SystemClock clock = new SystemClock( 44100 );
        pcmWriter.startSavingAsync();
        
        for( int q = 0; q < 44100; q++ )
        {
            generator.execute( clock, null );

            clock.step();
        }
        
        pcmWriter.stopSaving();
    }
}
