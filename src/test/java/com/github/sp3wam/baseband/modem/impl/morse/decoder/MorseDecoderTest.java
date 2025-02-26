package com.github.sp3wam.baseband.modem.impl.morse.decoder;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.EnumSet;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.impl.morse.decoder.MorseDecoder;
import com.github.sp3wam.baseband.modem.impl.morse.decoder.MorseDecoderConsumer;

import xt.audio.Enums.XtEnumFlags;
import xt.audio.Enums.XtSystem;
import xt.audio.XtAudio;
import xt.audio.XtDeviceList;
import xt.audio.XtPlatform;
import xt.audio.XtService;

public class MorseDecoderTest
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseDecoderTest.class );

    private MorseDecoder subject;
    private MorseDecoderConsumer consumer;

    @Before
    public void init()
    {
        subject = new MorseDecoder();
        consumer = new MorseDecoderConsumer();
    }

    @Test
    public void testLetterC_fromMp3() throws IOException
    {
        String filePath = "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/C_morse_code.mp3";
        subject.decodeFromMp3( filePath, consumer );

        // there is a trailing space character at the end
        assertEquals( "c ", consumer.getDecodedString() );
    }

    @Test
    public void testLetterC_fromWav() throws IOException
    {
        String filePath = "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/C_morse_code.wav";
        subject.decodeFromWav( filePath, consumer );

        assertEquals( "c ", consumer.getDecodedString() );
    }

    @Test
    public void testNoisyLetterC_fromMp3() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/C_noised_morse_code.mp3";
        subject.decodeFromMp3( filePath, consumer );

        assertEquals( "c ", consumer.getDecodedString() );
    }

    @Test
    public void testRealTransmission_fromMp3() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_transmission.mp3";
        subject.decodeFromMp3( filePath, consumer );

        assertEquals( "c ", consumer.getDecodedString() );
    }
    
    @Test
    public void testWikipedia_fromMp3() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/Wikipedia-Morse.mp3";
        subject.decodeFromMp3( filePath, consumer );

        assertEquals( "welcome to wikipedia, the free encyclopedia that anyone can edit. ",
            consumer.getDecodedString() );
    }

    // This is more like an integration test
    @Test
    public void openJavaxSound() throws IOException, LineUnavailableException
    {
        subject.decodeFromJavaxAudio( consumer );
    }

    // This is more like an integration test
    @Test
    public void testXtAudioPrintDevices() throws IOException, LineUnavailableException
    {
        try (XtPlatform platform = XtAudio.init( null, null ))
        {
            for( XtSystem system : platform.getSystems() )
            {
                XtService service = platform.getService( system );
                try (XtDeviceList list = service.openDeviceList( EnumSet.of( XtEnumFlags.ALL ) ))
                {
                    for( int d = 0; d < list.getCount(); d++ )
                    {
                        String id = list.getId( d );
                        System.out.println( system + ": " + list.getName( id ) );
                    }
                }
            }
        }
    }

    @Test
    public void openXtAudioLoopback() throws IOException, InterruptedException
    {
        subject.decodeFromXtAudio( consumer );

        Thread.sleep( 100000 );
        // stream.stop();

        System.currentTimeMillis();
    }
}
