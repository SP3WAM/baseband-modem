package com.github.sp3wam.baseband.modem.core.fft;

import java.awt.EventQueue;

import org.apache.commons.math3.complex.Complex;
import org.jfree.ui.RefineryUtilities;

import com.github.sp3wam.baseband.modem.core.AbstractGraphExample;
import com.github.sp3wam.baseband.modem.core.GraphData;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.FloatingPointSinusGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.correlation.AutocorrelationBlock;
import com.github.sp3wam.baseband.modem.core.fft.FFTBlock;
import com.github.sp3wam.baseband.modem.core.fft.FFTSpectrum;

public class UnderstandingSlowSignalAndFastSamplingFFT extends AbstractGraphExample
{
    private static final long serialVersionUID = 6540971832319619540L;

    final static String WINDOW_TITLE = "Slow signal vs fastsampling rate FFT";

    public UnderstandingSlowSignalAndFastSamplingFFT()
    {
        super( WINDOW_TITLE );
    }

    @Override
    protected GraphData[] getGraphData()
    {
        int frequency = 100;
        int samplingFreq = 3200;
        int fftWindow = 8;
        int samplesCount = 64;

        SystemClock clock = new SystemClock( samplingFreq );

        FloatingPointSinusGeneratorBlock generator = new FloatingPointSinusGeneratorBlock( 100.0, frequency );
        FFTBlock fft8Block = new FFTBlock( samplingFreq, fftWindow );
        FFTBlock fft16Block = new FFTBlock( samplingFreq, 2 * fftWindow );
        FFTBlock fft32Block = new FFTBlock( samplingFreq, 4 * fftWindow );
        FFTBlock fft64Block = new FFTBlock( samplingFreq, 8 * fftWindow );

        generator.setNextBlock( fft8Block );

        double[] sinusValues = new double[ samplesCount ];
        for( int q = 0; q < samplesCount; q++ )
        {
            generator.execute( clock, null );
            fft16Block.execute( clock, generator.getCurrentValue() );
            fft32Block.execute( clock, generator.getCurrentValue() );
            fft64Block.execute( clock, generator.getCurrentValue() );

            sinusValues[ q ] = generator.getCurrentValue().getValue();

            clock.step();
        }

        String text = String.format( "Sinus %s Hz", frequency );
        GraphData sinusGraphData = new GraphData( text, text, sinusValues );

        // FFT 8 data
        FFTSpectrum fft8FreqSpectrum = new FFTSpectrum( fft8Block.getCurrentValue() );
        GraphData fft8GraphData =
            new GraphData( String.format( "FFT 8 spectrum (of quarter signal period). %s s/s", samplingFreq ),
                "FFT 8", fft8FreqSpectrum.getPowerSpectralDensityValues(), fft8FreqSpectrum.getFreqencies() );
        fft8GraphData.setxAxisLabel( "Frequency [Hz]" );

        // FFT 16 data
        FFTSpectrum fft16FreqSpectrum = new FFTSpectrum( fft16Block.getCurrentValue() );
        GraphData fft16GraphData = new GraphData(
            String.format( "FFT 16 spectrum (of half signal period). %s s/s", samplingFreq ), "FFT 16",
            fft16FreqSpectrum.getPowerSpectralDensityValues(), fft16FreqSpectrum.getFreqencies() );
        fft16GraphData.setxAxisLabel( "Frequency [Hz]" );

        // FFT 32 data
        FFTSpectrum fft32FreqSpectrum = new FFTSpectrum( fft32Block.getCurrentValue() );
        GraphData fft32GraphData = new GraphData(
            String.format( "FFT 32 spectrum (of one signal period). %s s/s", samplingFreq ), "FFT 32",
            fft32FreqSpectrum.getPowerSpectralDensityValues(), fft32FreqSpectrum.getFreqencies() );
        fft32GraphData.setxAxisLabel( "Frequency [Hz]" );

        // FFT 64 data
        FFTSpectrum fft64FreqSpectrum = new FFTSpectrum( fft64Block.getCurrentValue() );
        GraphData fft64GraphData = new GraphData(
            String.format( "FFT 64 spectrum (of two signal period). %s s/s", samplingFreq ), "FFT 64",
            fft64FreqSpectrum.getPowerSpectralDensityValues(), fft64FreqSpectrum.getFreqencies() );
        fft64GraphData.setxAxisLabel( "Frequency [Hz]" );

        return new GraphData[]
        { sinusGraphData, fft8GraphData, fft16GraphData, fft32GraphData, fft64GraphData };
    }

    public static void main( final String[] args )
    {
        EventQueue.invokeLater( new Runnable()
        {

            @Override
            public void run()
            {
                UnderstandingSlowSignalAndFastSamplingFFT demo =
                    new UnderstandingSlowSignalAndFastSamplingFFT();
                demo.pack();
                RefineryUtilities.centerFrameOnScreen( demo );
                demo.setVisible( true );
            }
        } );
    }
}
