package com.github.sp3wam.baseband.modem.core.pcm;

import org.junit.Test;

public class PcmToFileWriterBlockTest
{

    @Test
    public void test()
    {
        PcmToFileWriterBlock pcmWriter = new PcmToFileWriterBlock();
        pcmWriter.startSaving();
    }
}
