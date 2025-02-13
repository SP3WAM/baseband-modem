package com.github.sp3wam.baseband.modem.core.pcm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import javazoom.spi.mpeg.sampled.file.MpegEncoding;

public class PcmFromMp3FileSignalGeneratorBlock extends PcmFromFileSignalGeneratorBlock
{
    private final static Logger LOGGER = LoggerFactory.getLogger( PcmFromMp3FileSignalGeneratorBlock.class );

    public PcmFromMp3FileSignalGeneratorBlock( double amplitude, String mp3FilePath ) throws IOException
    {
        super( amplitude, mp3FilePath );
    }

    protected AudioInputStream createAudioInputStream( String filePath ) throws IOException
    {
        try
        {
            MpegAudioFileReader mpegAudioFileReader = new MpegAudioFileReader();

            AudioFileFormat audioFileFormat = mpegAudioFileReader.getAudioFileFormat( new File( filePath ) );
            AudioFormat originalAudioFormat = audioFileFormat.getFormat();

            LOGGER.info(
                String.format( "Original audio format of MP3 file is: %s", originalAudioFormat.toString() ) );

            AudioFormat audioFormat = new AudioFormat( originalAudioFormat.getSampleRate(), 16,
                originalAudioFormat.getChannels(), true, false );

            LOGGER.info(
                String.format( "Original MP3 format will be converted into: %s", audioFormat.toString() ) );

            if( !(originalAudioFormat.getEncoding() instanceof MpegEncoding) )
            {
                throw new RuntimeException( String.format( "Files with %s encoding are not supported.",
                    originalAudioFormat.getEncoding().toString() ) );
            }

            // create stream which decodes MP3 into PCM
            InputStream inputStream = new FileInputStream( filePath );
            InputStream inputStream2 = new BufferedInputStream( inputStream );
            AudioInputStream audioInputStream = mpegAudioFileReader.getAudioInputStream( inputStream2 );

            return new DecodedMpegAudioInputStream( audioFormat, audioInputStream );
        }
        catch( UnsupportedAudioFileException e )
        {
            throw new RuntimeException( e );
        }
    }
}
