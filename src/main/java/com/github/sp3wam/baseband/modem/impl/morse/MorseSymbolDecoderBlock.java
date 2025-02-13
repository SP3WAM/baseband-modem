package com.github.sp3wam.baseband.modem.impl.morse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.StringSignal;

class MorseSymbolDecoderBlock implements BlockIf< MorseSymbolSignal, StringSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseSymbolDecoderBlock.class );

    private final static char DIT_CHAR = '.';
    private final static char DAH_CHAR = '-';
    private final static char SPACE_CHAR = ' ';

    private StringSignal currentValue;
    private BlockIf< StringSignal, ? > nextBlock;

    private MorseTable morseTable = null;
    private StringBuilder stringBuilder = new StringBuilder();

    public MorseSymbolDecoderBlock()
    {
        morseTable = MorseTable.readFromResources();
    }

    @Override
    public void execute( SystemClock systemClock, MorseSymbolSignal inputSignalValue )
    {
        boolean result = execute0( systemClock, inputSignalValue );

        if( nextBlock == null )
        {
            return;
        }

        if( result == false )
        {
            return;
        }

        nextBlock.execute( systemClock, currentValue );
    }

    @Override
    public void setNextBlock( BlockIf< StringSignal, ? > nextBlock )
    {
        this.nextBlock = nextBlock;
    }

    @Override
    public StringSignal getCurrentValue()
    {
        return currentValue;
    }

    protected boolean execute0( SystemClock systemClock, MorseSymbolSignal inputSignalValue )
    {
        MorseSymbol inputSignal = inputSignalValue.getValue();

        if( inputSignal == MorseSymbol.SHORT_GAP || inputSignal == MorseSymbol.MEDIUM_GAP )
        {
            // end of letter (or word) detected
            String morseString = stringBuilder.toString();
            if(morseString.isEmpty())
            {
                return false;
            }
            
            String decodedLetter = morseTable.decode( morseString );
            stringBuilder.setLength( 0 );

            if( decodedLetter == null )
            {
                LOGGER.warn( String.format( "Not able to decode Morse sequence: %s", morseString ) );

                return false;
            }

            if( inputSignal == MorseSymbol.SHORT_GAP )
            {
                // end of letter
                currentValue = new StringSignal( decodedLetter );

                return true;
            }

            if( inputSignal == MorseSymbol.MEDIUM_GAP )
            {
                // end of word detected.
                currentValue = new StringSignal( decodedLetter + SPACE_CHAR );

                return true;
            }
        }

        if( inputSignal == MorseSymbol.DIT )
        {
            stringBuilder.append( MorseSymbolDecoderBlock.DIT_CHAR );
        }
        else
        {
            stringBuilder.append( MorseSymbolDecoderBlock.DAH_CHAR );
        }

        return false;
    }

}
