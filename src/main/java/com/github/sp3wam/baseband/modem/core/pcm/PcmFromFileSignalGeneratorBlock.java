package com.github.sp3wam.baseband.modem.core.pcm;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

public abstract class PcmFromFileSignalGeneratorBlock extends PcmSignalGeneratorBlock
{
    private String filePath;

    public PcmFromFileSignalGeneratorBlock( double amplitude, String filePath ) throws IOException
    {
        super( amplitude );

        this.filePath = filePath;
    }

    protected AudioInputStream createAudioInputStream() throws IOException
    {
        return createAudioInputStream( filePath );
    }

    protected abstract AudioInputStream createAudioInputStream( String filePath ) throws IOException;
}
