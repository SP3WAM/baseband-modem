package com.github.sp3wam.baseband.modem.core.basic.blocks;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public class NoisedSinusGeneratorBlock extends FloatingPointSinusGeneratorBlock
{
    private WhiteNoiseGeneratorBlock noiseGenerator = null;

    public NoisedSinusGeneratorBlock( double amplitude, double frequencyHz )
    {
        super( amplitude, frequencyHz );
    }

    public void setNoiseAmplitude( double amplitude )
    {
        noiseGenerator = new WhiteNoiseGeneratorBlock( amplitude );
    }

    protected void execute0( SystemClock systemClock )
    {
        super.execute0( systemClock );

        if( noiseGenerator != null )
        {
            noiseGenerator.execute( systemClock, null );

            double value = this.getCurrentValue().getValue() + noiseGenerator.getCurrentValue().getValue();
            currentValue = new FloatingPointSignal( value );
        }
    }

}
