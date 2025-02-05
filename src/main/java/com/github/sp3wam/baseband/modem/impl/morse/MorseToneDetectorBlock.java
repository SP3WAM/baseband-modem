package com.github.sp3wam.baseband.modem.impl.morse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.signals.FFTSignal;
import com.github.sp3wam.baseband.modem.core.signals.MorseToneSignal;

public class MorseToneDetectorBlock implements BlockIf< FFTSignal, MorseToneSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseToneDetectorBlock.class );

    private MorseToneSignal currentTone = null;
    private BlockIf< MorseToneSignal, ? > nextBlock;

    @Override
    public void execute( SystemClock systemClock, FFTSignal inputSignalValue )
    {
        execute0( systemClock, inputSignalValue );

        if( nextBlock == null )
        {
            return;
        }

        nextBlock.execute( systemClock, currentTone );
    }

    @Override
    public void setNextBlock( BlockIf< MorseToneSignal, ? > nextBlock )
    {
        this.nextBlock = nextBlock;
    }

    @Override
    public MorseToneSignal getCurrentValue()
    {
        return currentTone;
    }

    protected void execute0( SystemClock systemClock, FFTSignal inputSignalValue )
    {
        if( inputSignalValue.getPeakFrequencies( 100.0 ).size() == 1 )
        {
            currentTone = new MorseToneSignal( inputSignalValue.getPeakFrequencies( 100.0 ).get( 0 ) );
        }
        else
        {
            currentTone = new MorseToneSignal( 0.0 );
        }

        LOGGER.debug( String.format( "Detected tone %s Hz", currentTone.getToneFrequency() ) );
    }
}
