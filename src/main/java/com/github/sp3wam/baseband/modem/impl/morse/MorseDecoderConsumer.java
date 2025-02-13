package com.github.sp3wam.baseband.modem.impl.morse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.AbstractConsumerBlock;
import com.github.sp3wam.baseband.modem.core.basic.signals.StringSignal;

public class MorseDecoderConsumer extends AbstractConsumerBlock< StringSignal, StringSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseDecoderConsumer.class );

    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void execute( SystemClock systemClock, StringSignal inputSignalValue )
    {
        for( int index = 0; index < inputSignalValue.getValue().length(); index++ )
        {
            char charAtIndex = inputSignalValue.getValue().charAt( index );
            LOGGER.info( String.format( "Detected Morse character: %s", charAtIndex ) );

            stringBuilder.append( charAtIndex );
        }
    }

    public String getDecodedString()
    {
        return stringBuilder.toString();
    }
}
