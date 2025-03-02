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
    private Logger LOGGER = LoggerFactory.getLogger( MorseDecoderLifeSimpleExample.class );

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
    public void testNoisyLetterQ_fromWav() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/Q_noised_morse_code.wav";
        subject.setSignalThreshold( 10 );
        subject.decodeFromWav( filePath, consumer );

        assertEquals( "q ", consumer.getDecodedString() );
    }

    @Test
    public void testRealTransmission_fromMp3() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_transmission.mp3";
        subject.decodeFromMp3( filePath, consumer );

        assertEquals( "cq cq cq de g3zrj g3zrj g3zrj cq cq cq de g3zrj ", consumer.getDecodedString() );
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

}
