package com.github.sp3wam.baseband.modem.core.pcm;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PcmFromWavFileSignalGeneratorBlock extends PcmFromFileSignalGeneratorBlock
{

    public PcmFromWavFileSignalGeneratorBlock( double amplitude, String wavFilePath ) throws IOException
    {
        super( amplitude, wavFilePath );
    }

    protected AudioInputStream createAudioInputStream( String filePath ) throws IOException
    {
        InputStream inputStream = new FileInputStream( filePath );
        InputStream inputStream2 = new BufferedInputStream( inputStream );

        try
        {
            return AudioSystem.getAudioInputStream( inputStream2 );
        }
        catch( UnsupportedAudioFileException e )
        {
            throw new RuntimeException( e );
        }
    }
}
