package com.github.sp3wam.baseband.modem.impl.morse.detector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.BlockListenerIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.BitAveragerBlock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.BitSignal;
import com.github.sp3wam.baseband.modem.core.basic.blocks.FloatingPointAveragerBlock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.FloatingPointAvgMagnitudeCalculatorBlock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.SamplerBlock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTBlock;
import com.github.sp3wam.baseband.modem.core.fft.FFTSignal;

/**
 * From the input audio signal samples detects the Morse signal into a stream of {@linkplain BitSignal}:
 * <ul>
 * <li>bit signal of true - means there is a audible beep (dit or dah)</li>
 * <li>bit signal of false - means there is no audible beep (just noise)</li>
 * </ul>
 */
abstract class AbstractMorseSignalDetectorBlock extends AbstractBlock< FloatingPointSignal, BitSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( AbstractMorseSignalDetectorBlock.class );

    private final static double FFT_DESIRED_SAMPLE_FREQ = 3000.0;
    private final static double BIT_DESIRED_SAMPLE_FREQ = 100.0;

    private FloatingPointAveragerBlock floatingPointAveragerBlock;
    private SamplerBlock< FloatingPointSignal, FloatingPointSignal > fftFasterSampler;
    private FloatingPointAvgMagnitudeCalculatorBlock avgMagnitude;
    private FFTBlock fftBlock;
    private BitAveragerBlock bitFasterAveragerBlock;
    private SamplerBlock< BitSignal, BitSignal > fftSlowerSampler;
    private InternalBlock internalBlock;
    private BitAveragerBlock bitSlowerAveragerBlock;

    private double inputSignalSampleRate;
    private int fftWindowSize = 16;
    private double fftDesiredSampleFreq = FFT_DESIRED_SAMPLE_FREQ;
    private double outputDesiredSampleFreq = BIT_DESIRED_SAMPLE_FREQ;

    public AbstractMorseSignalDetectorBlock( double inputSignalSampleRate )
    {
        this.inputSignalSampleRate = inputSignalSampleRate;

        init();
    }

    public void setFftParams( int fftWindowSize, double fftDesiredSampleFreq )
    {
        this.fftWindowSize = fftWindowSize;
        this.fftDesiredSampleFreq = fftDesiredSampleFreq;

        init();
    }

    public void setDesiredOutputSignalSampleFreq( double outputDesiredSampleFreq )
    {
        this.outputDesiredSampleFreq = outputDesiredSampleFreq;

        init();
    }
    
    public void addFftSamplerListener( BlockListenerIf< FloatingPointSignal > fftSamplerListener )
    {
        fftFasterSampler.addListener( fftSamplerListener );
    }

    @Override
    public void setNextBlock( BlockIf< BitSignal, ? > nextBlock )
    {
        super.setNextBlock( null );
        bitSlowerAveragerBlock.setNextBlock( nextBlock );
    }

    protected boolean execute0( SystemClock systemClock, FloatingPointSignal inputSignalValue )
    {
        floatingPointAveragerBlock.execute( systemClock, inputSignalValue );

        setCurrentValue( fftSlowerSampler.getCurrentValue() );

        return false;
    }

    protected abstract BitSignal detectSignal( FFTSignal fftSignal );

    private void init()
    {
        int fftSamplerDivider = (int)(inputSignalSampleRate / fftDesiredSampleFreq);
        int fftSampleFreq = (int)(inputSignalSampleRate / fftSamplerDivider);
        int bitSamplerDivider = (int)(fftSampleFreq / outputDesiredSampleFreq);

        floatingPointAveragerBlock = new FloatingPointAveragerBlock( 1 );
        avgMagnitude = new FloatingPointAvgMagnitudeCalculatorBlock( (int)inputSignalSampleRate );
        fftFasterSampler = new SamplerBlock< FloatingPointSignal, FloatingPointSignal >( fftSamplerDivider );
        fftFasterSampler.setLoggingEnabled( false );
        fftBlock = new FFTBlock( fftSampleFreq, fftWindowSize );
        internalBlock = new InternalBlock();
        // remove two bit length spikes in fast bit stream
        bitFasterAveragerBlock = new BitAveragerBlock( 5 );
        bitFasterAveragerBlock.setLoggingEnabled( false );
        fftSlowerSampler = new SamplerBlock< BitSignal, BitSignal >( bitSamplerDivider );
        fftSlowerSampler.setLoggingEnabled( false );
        // removes 1 bit spikes that sneak during slow sampling
        bitSlowerAveragerBlock = new BitAveragerBlock( 5 );
        bitSlowerAveragerBlock.setLoggingEnabled( true );

        // connect the blocks
        floatingPointAveragerBlock.setNextBlock( avgMagnitude );
        avgMagnitude.setNextBlock( fftFasterSampler );
        fftFasterSampler.setNextBlock( fftBlock );
        fftBlock.setNextBlock( internalBlock );
        internalBlock.setNextBlock( bitFasterAveragerBlock );
        bitFasterAveragerBlock.setNextBlock( fftSlowerSampler );
        fftSlowerSampler.setNextBlock( bitSlowerAveragerBlock );
    }

    private class InternalBlock extends AbstractBlock< FFTSignal, BitSignal >
    {
        @Override
        protected boolean execute0( SystemClock systemClock, FFTSignal inputSignalValue )
        {
            setCurrentValue( detectSignal( inputSignalValue ) );

            return true;
        }
    }
}
