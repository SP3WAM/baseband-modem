package com.github.sp3wam.baseband.modem.core.blocks;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.sun.jna.Pointer;

import xt.audio.Enums.XtDeviceCaps;
import xt.audio.Enums.XtEnumFlags;
import xt.audio.Enums.XtSample;
import xt.audio.Enums.XtSystem;
import xt.audio.Structs.XtBuffer;
import xt.audio.Structs.XtBufferSize;
import xt.audio.Structs.XtChannels;
import xt.audio.Structs.XtDeviceStreamParams;
import xt.audio.Structs.XtFormat;
import xt.audio.Structs.XtMix;
import xt.audio.Structs.XtStreamParams;
import xt.audio.XtAudio;
import xt.audio.XtDevice;
import xt.audio.XtDeviceList;
import xt.audio.XtPlatform;
import xt.audio.XtSafeBuffer;
import xt.audio.XtService;
import xt.audio.XtStream;

public class PcmFromXtAudioSignalGeneratorBlock extends PcmSignalGeneratorBlock
{

    // intermediate buffer
    private static byte[] BYTES;
    // dump to file (never do this, see below)
    static CustomInputStream customInputStream;

    private XtStream xtStream;
    private XtSafeBuffer safeBuffer;

    public PcmFromXtAudioSignalGeneratorBlock( double amplitude ) throws IOException
    {
        super( amplitude );
    }

    @Override
    protected AudioInputStream createAudioInputStream() throws IOException
    {
        try
        {
            openLoopback();

            AudioFormat audioFormat = new AudioFormat( 44100, 16, 1, true, false );
            return new AudioInputStream( customInputStream, audioFormat, -1 );
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    protected boolean execute0( SystemClock systemClock )
    {
        try
        {
            if( audioStream.available() == 0 )
            {
                return false;
            }
        }
        catch( IOException e )
        {
            throw new RuntimeException( e );
        }

        return super.execute0( systemClock );
    }

    // audio streaming callback
    static int onBuffer( XtStream stream, XtBuffer buffer, Object user ) throws Exception
    {
        XtSafeBuffer safe = XtSafeBuffer.get( stream );
        if( safe == null )
        {
            return 0;
        }
        // lock buffer from native into java
        safe.lock( buffer );
        // short[] because we specified INT16 below
        // this is the captured audio data
        short[] audio = (short[])safe.getInput();
        // you want a spectrum analyzer, i dump to a file
        // but actually never dump to a file in any serious app
        // see
        // http://www.rossbencina.com/code/real-time-audio-programming-101-time-waits-for-nothing
        processAudio( audio, buffer.frames );
        // unlock buffer from java into native
        safe.unlock( buffer );
        return 0;
    }

    private static void processAudio( short[] audio, int frames ) throws Exception
    {
        // convert from short[] to byte[]
        for( int frame = 0; frame < frames; frame++ )
        {
            // for 2 channels
            for( int channel = 0; channel < 1; channel++ )
            {
                // 2 = channels again
                int sampleIndex = frame * 2 + channel;
                // 2 = 2 bytes for each short
                int byteIndex0 = sampleIndex * 2;
                int byteIndex1 = sampleIndex * 2 + 1;
                // probably some library method for this, somewhere
                // BYTES[byteIndex0] = (byte) (audio[sampleIndex] & 0x000000FF);
                // BYTES[byteIndex1] = (byte) ((audio[sampleIndex] & 0x0000FF00) >> 8);

                customInputStream.appendByte( (byte)(audio[ sampleIndex ] & 0x000000FF) );
                customInputStream.appendByte( (byte)((audio[ sampleIndex ] & 0x0000FF00) >> 8) );
            }
        }

        // by now BYTES contains the data you want,
        // but be sure to account for frame count
        // (i.e. not all off BYTES may contain useful data,
        // might be some unused garbage at the end)

        // compute total bytes this round
        // = frame count * 2 channels * 2 bytes per short (INT16)
        int byteCount = frames * 2 * 2;

        // // write to file - again, never do this in a real app
        // fos.write(BYTES, 0, byteCount);
    }

    private void openLoopback() throws Exception
    {
        // this initializes platform dependent stuff like COM
        try (XtPlatform platform = XtAudio.init( null, Pointer.NULL ))
        {
            // works on windows only, obviously
            XtService service = platform.getService( XtSystem.WASAPI );
            // list input devices (this includes loopback)
            try (XtDeviceList list = service.openDeviceList( EnumSet.of( XtEnumFlags.INPUT ) ))
            {
                for( int i = 0; i < list.getCount(); i++ )
                {
                    String deviceId = list.getId( i );
                    EnumSet< XtDeviceCaps > caps = list.getCapabilities( deviceId );
                    // filter loopback devices
                    if( !caps.contains( XtDeviceCaps.LOOPBACK ) )
                    {
                        continue;
                    }

                    String deviceName = list.getName( deviceId );
                    // just to check what output we're recording
                    System.out.println( deviceName );

                    if( !deviceName.contains( "Realtek HD Audio 2nd output (Realtek(R) Audio)" ) )
                    {
                        continue;
                    }

                    // open device
                    try (XtDevice device = service.openDevice( deviceId ))
                    {
                        // 16 bit 48khz
                        XtMix mix = new XtMix( 48000, XtSample.INT16 );
                        // 2 channels input, no masking
                        XtChannels channels = new XtChannels( 2, 0, 0, 0 );
                        // final audio format
                        XtFormat format = new XtFormat( mix, channels );
                        // query min/max/default buffer sizes
                        XtBufferSize bufferSize = device.getBufferSize( format );
                        // true->interleaved, onBuffer->audio stream callback
                        XtStreamParams streamParams = new XtStreamParams( true,
                            PcmFromXtAudioSignalGeneratorBlock::onBuffer, null, null );
                        // final initialization params with default buffer size
                        XtDeviceStreamParams deviceParams =
                            new XtDeviceStreamParams( streamParams, format, bufferSize.current );
                        // run stream
                        // safe buffer allows you to get java short[] instead on jna Pointer in the
                        // callback
                        xtStream = device.openStream( deviceParams, null );
                        safeBuffer = XtSafeBuffer.register( xtStream );

                        // max frames to enter onBuffer * channels * bytes per sample
                        // BYTES = new byte[stream.getFrames() * 2 * 2];
                        BYTES = new byte[ xtStream.getFrames() * 1 * 2 ]; // one channel
                        // // make filename valid
                        // String fileName = deviceName.replaceAll("[\\\\/:*?\"<>|]", "");
                        // try (FileOutputStream fos0 = new FileOutputStream(fileName + ".raw")) {
                        // // make filestream accessible to the callback
                        // // could also be done by passsing as userdata to openStream
                        // fos = fos0;
                        // // run for 1 second
                        // stream.start();
                        // Thread.sleep(1000);
                        // stream.stop();
                        // }
                        customInputStream = new CustomInputStream();
                        xtStream.start();
                        // Thread.sleep(10000);
                        // stream.stop();
                    }
                }
            }
        }
    }

    private void close()
    {
        if( xtStream != null )
        {
            xtStream.close();
        }
        if( safeBuffer != null )
        {
            safeBuffer.close();
        }

        xtStream = null;
        safeBuffer = null;
    }

    private class CustomInputStream extends InputStream
    {

        private List< Byte > bytes = new ArrayList< Byte >();

        @Override
        public int read() throws IOException
        {

            Byte result = 0;

            synchronized( bytes )
            {
                if( bytes.size() == 0 )
                {
                    return -1;
                }

                result = bytes.get( 0 );
                bytes.remove( 0 );
            }

            return result;
        }

        public int available() throws IOException
        {

            synchronized( bytes )
            {
                return bytes.size();
            }
        }

        public void appendByte( byte aByte )
        {
            // System.out.println(aByte);

            synchronized( bytes )
            {
                bytes.add( aByte );
            }
        }
    }
}
