package com.github.sp3wam.baseband.modem.core.basic.blocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SignalIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;

public class SamplerBlock< I extends SignalIf, O extends SignalIf > implements BlockIf< I, I >
{
    private Logger LOGGER = LoggerFactory.getLogger( SamplerBlock.class );

    private long samplingDivider;
    private long currentSampleNumber = 0;
    private long inputSamplesCounter = -1;
    private BlockIf< I, ? > nextBlock;
    private I currentValue = null;

    public SamplerBlock( long samplingDivider )
    {
        this.samplingDivider = samplingDivider;
    }

    @Override
    public void execute( SystemClock systemClock, I inputSignalValue )
    {
        boolean result = execute0( systemClock, inputSignalValue );

        if( result == false )
        {
            // sample not taken, just return
            return;
        }

        if( nextBlock == null )
        {
            return;
        }

        nextBlock.execute( systemClock, currentValue );
    }

    @Override
    public void setNextBlock( BlockIf< I, ? > nextBlock )
    {
        this.nextBlock = nextBlock;
    }

    @Override
    public I getCurrentValue()
    {
        return currentValue;
    }

    public long getSamplesTaken()
    {
        return currentSampleNumber;
    }

    protected boolean execute0( SystemClock systemClock, I inputSignalValue )
    {
        inputSamplesCounter++;

        if( inputSamplesCounter % samplingDivider != 0 )
        {
            // not yet the right time to take a sample
            return false;
        }

        LOGGER.debug( String.format( "Sampler with sampling divider %s. Processing sample nr %s",
            samplingDivider, currentSampleNumber ) );

        currentValue = inputSignalValue;
        currentSampleNumber++;

        return true;
    }
}
