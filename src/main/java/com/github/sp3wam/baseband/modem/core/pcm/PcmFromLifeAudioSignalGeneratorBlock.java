package com.github.sp3wam.baseband.modem.core.pcm;

import java.io.IOException;

import com.github.sp3wam.baseband.modem.core.SystemClock;

public abstract class PcmFromLifeAudioSignalGeneratorBlock extends PcmSignalGeneratorBlock
{

    public PcmFromLifeAudioSignalGeneratorBlock( double amplitude )
    {
        super( amplitude );
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
