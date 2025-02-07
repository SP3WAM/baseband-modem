package com.github.sp3wam.baseband.modem.impl.morse;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.TargetDataLine;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.blocks.FFTBlock;
import com.github.sp3wam.baseband.modem.core.blocks.PcmFromMp3FileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.blocks.PcmFromWavFileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.blocks.SamplerBlock;
import com.github.sp3wam.baseband.modem.core.blocks.ToneToBitConverterBlock;
import com.github.sp3wam.baseband.modem.core.signals.BitSignal;
import com.github.sp3wam.baseband.modem.core.signals.FloatingPointSignal;
import com.github.sp3wam.baseband.modem.core.signals.StringSignal;

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

    @Test
    public void testRealSound() throws IOException, LineUnavailableException
    {
        subject.decodeFromSystemAudio( consumer );
    }
}
