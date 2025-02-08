package com.github.sp3wam.baseband.modem.core.blocks;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import com.github.sp3wam.baseband.modem.core.SystemClock;

public class PcmFromJavaxAudioSignalGeneratorBlock extends PcmFromFileSignalGeneratorBlock
{

    public PcmFromJavaxAudioSignalGeneratorBlock( double amplitude, String filePath ) throws IOException
    {
        super( amplitude, filePath );
    }

    @Override
    protected AudioInputStream createAudioInputStream( String filePath ) throws IOException
    {
        Mixer mixer = null;
        for( Mixer.Info mixerInfo : AudioSystem.getMixerInfo() )
        {
            if( mixerInfo.getName().startsWith( "Stereomix (Realtek(R) Audio)" ) ) // it works, but is a bit
                                                                                 // silent
            {
                mixer = AudioSystem.getMixer( mixerInfo );
                break;
            }
        }
        try
        {
            AudioFormat audioFormat = new AudioFormat( 44100, 16, 1, true, false );
            TargetDataLine line = (TargetDataLine)mixer.getLine( mixer.getTargetLineInfo()[ 0 ] );
            line.open( audioFormat );
            line.start();

            return new AudioInputStream( (TargetDataLine)line );
        }
        catch( LineUnavailableException e )
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

}
