package com.github.sp3wam.baseband.modem.core.basic.blocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.SignalIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;

public class SamplerBlock< I extends SignalIf, O extends SignalIf >extends AbstractBlock< I, I >
{
    private Logger LOGGER = LoggerFactory.getLogger( SamplerBlock.class );

    private long samplingDivider;
    private long currentSampleNumber = 0;
    private long inputSamplesCounter = -1;

    public SamplerBlock( long samplingDivider )
    {
        this.samplingDivider = samplingDivider;
    }

    public long getSamplesTaken()
    {
        return currentSampleNumber;
    }

    @Override
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
