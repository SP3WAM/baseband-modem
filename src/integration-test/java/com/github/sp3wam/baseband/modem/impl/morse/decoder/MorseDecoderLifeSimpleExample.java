package com.github.sp3wam.baseband.modem.impl.morse.decoder;

import java.io.IOException;
import java.util.EnumSet;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xt.audio.Enums.XtEnumFlags;
import xt.audio.Enums.XtSystem;
import xt.audio.XtAudio;
import xt.audio.XtDeviceList;
import xt.audio.XtPlatform;
import xt.audio.XtService;

public class MorseDecoderLifeSimpleExample
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
    public void openJavaxSound() throws IOException, LineUnavailableException
    {
        subject.decodeFromJavaxAudioSync( consumer );
    }

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
        subject.decodeFromXtAudioSync( consumer );

        Thread.sleep( 100000 );
        // stream.stop();

        System.currentTimeMillis();
    }
}
