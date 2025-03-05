package com.github.sp3wam.baseband.modem.impl.morse.decoder;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        subject.setSignalThreshold( 9 );
        subject.decodeFromWav( filePath, consumer );

        assertEquals( "q ", consumer.getDecodedString() );
    }

    @Test
    public void testNoisyDigit3_fromWav() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/3_noised_morse_code.wav";
        subject.setSignalThreshold( 9 );
        subject.decodeFromWav( filePath, consumer );

        assertEquals( "3 ", consumer.getDecodedString() );
    }
    
    @Test
    public void testNoisyLetterR_fromWav() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/R_noised_morse_code.wav";
        subject.setSignalThreshold( 10.5 );
        subject.decodeFromWav( filePath, consumer );

        assertEquals( "r ", consumer.getDecodedString() );
    }

    @Test
    public void testRealTransmission_fromMp3() throws IOException
    {
        String filePath =
            "src/test/resources/com/github/sp3wam/baseband/modem/impl/morse/real_transmission.mp3";
        subject.setSignalThreshold( 9 );
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
