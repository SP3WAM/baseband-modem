package com.github.sp3wam.baseband.modem.core.basic.blocks;

import java.awt.EventQueue;

import org.apache.commons.math3.complex.Complex;
import org.jfree.ui.RefineryUtilities;

import com.github.sp3wam.baseband.modem.core.AbstractGraphExample;
import com.github.sp3wam.baseband.modem.core.GraphData;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.correlation.AutocorrelationBlock;
import com.github.sp3wam.baseband.modem.core.fft.FFTBlock;
import com.github.sp3wam.baseband.modem.core.fft.FFTSpectrum;

public class SinusGraphExample extends AbstractGraphExample
{
    private static final long serialVersionUID = 6540971832319619540L;

    final static String WINDOW_TITLE = "Sinus generator";

    public SinusGraphExample()
    {
        super( WINDOW_TITLE );
    }

    @Override
    protected GraphData[] getGraphData()
    {
        int frequency = 800;
        int samplingFreq = 8 * frequency;
        int fftWindow = 8;
        int samplesCount = 512;

        SystemClock clock = new SystemClock( samplingFreq );

        FloatingPointSinusGeneratorBlock generator = new FloatingPointSinusGeneratorBlock( 100.0, frequency );
        FloatingPointAveragerBlock avgBlock = new FloatingPointAveragerBlock( samplesCount );
        FFTBlock fftBlock = new FFTBlock( samplingFreq, fftWindow );
        FFTBlock fft16Block = new FFTBlock( samplingFreq, 2 * fftWindow );
        FFTBlock fft32Block = new FFTBlock( samplingFreq, 4 * fftWindow );
        FFTBlock fft64Block = new FFTBlock( samplingFreq, 8 * fftWindow );
        FFTBlock fft128Block = new FFTBlock( samplingFreq, 16 * fftWindow );
        FFTBlock fft256Block = new FFTBlock( samplingFreq, 32 * fftWindow );
        AutocorrelationBlock acor8Block = new AutocorrelationBlock( fftWindow );
        AutocorrelationBlock acor16Block = new AutocorrelationBlock( 2 * fftWindow );
        AutocorrelationBlock acor32Block = new AutocorrelationBlock( 4 * fftWindow );
        AutocorrelationBlock acor64Block = new AutocorrelationBlock( 8 * fftWindow );
        AutocorrelationBlock acor128Block = new AutocorrelationBlock( 16 * fftWindow );
        AutocorrelationBlock acor256Block = new AutocorrelationBlock( 32 * fftWindow );
        AutocorrelationBlock acor512Block = new AutocorrelationBlock( 64 * fftWindow );

        generator.setNextBlock( fftBlock );

        double[] noiseValues = new double[ samplesCount ];
        double[] sinusAvgValues = new double[ samplesCount ];
        for( int q = 0; q < samplesCount; q++ )
        {
            generator.execute( clock, null );
            avgBlock.execute( clock, generator.getCurrentValue() );
            fft16Block.execute( clock, generator.getCurrentValue() );
            fft32Block.execute( clock, generator.getCurrentValue() );
            fft64Block.execute( clock, generator.getCurrentValue() );
            fft128Block.execute( clock, generator.getCurrentValue() );
            fft256Block.execute( clock, generator.getCurrentValue() );
            acor8Block.execute( clock, generator.getCurrentValue() );
            acor16Block.execute( clock, generator.getCurrentValue() );
            acor32Block.execute( clock, generator.getCurrentValue() );
            acor64Block.execute( clock, generator.getCurrentValue() );
            acor128Block.execute( clock, generator.getCurrentValue() );
            acor256Block.execute( clock, generator.getCurrentValue() );
            acor512Block.execute( clock, generator.getCurrentValue() );

            noiseValues[ q ] = generator.getCurrentValue().getValue();
            sinusAvgValues[ q ] = avgBlock.getCurrentValue().getValue();

            clock.step();
        }

        String text = String.format( "Sinus %s Hz", frequency );
        GraphData noiseGraphData = new GraphData( text, text, noiseValues );

        // Sinus average data
        GraphData avgGraphData =
            new GraphData( "Sinus average of N samples", "Sinus average of N samples", sinusAvgValues );

        // FFT 8 data
        FFTSpectrum fft8FreqSpectrum = new FFTSpectrum( fftBlock.getCurrentValue() );
        Complex[] fft8Result = fft8FreqSpectrum.getComplexValues();
        double[] fft8Values = new double[ fft8Result.length ];
        for( int i = 0; i < fft8Result.length; i++ )
        {
            fft8Values[ i ] = fft8Result[ i ].abs();
        }
        GraphData fft8GraphData =
            new GraphData( "FFT 8 spectrum", "FFT 8", fft8Values, fft8FreqSpectrum.getFreqencies() );
        fft8GraphData.setxAxisLabel( "Frequency [Hz]" );

        // FFT 16 data
        FFTSpectrum fft16FreqSpectrum = new FFTSpectrum( fft16Block.getCurrentValue() );
        Complex[] fft16Result = fft16FreqSpectrum.getComplexValues();
        double[] fft16Values = new double[ fft16Result.length ];
        for( int i = 0; i < fft16Result.length; i++ )
        {
            fft16Values[ i ] = fft16Result[ i ].abs();
        }
        GraphData fft16GraphData =
            new GraphData( "FFT 16 spectrum", "FFT 16", fft16Values, fft16FreqSpectrum.getFreqencies() );
        fft16GraphData.setxAxisLabel( "Frequency [Hz]" );

        // FFT 32 data
        FFTSpectrum fft32FreqSpectrum = new FFTSpectrum( fft32Block.getCurrentValue() );
        Complex[] fft32Result = fft32FreqSpectrum.getComplexValues();
        double[] fft32Values = new double[ fft32Result.length ];
        for( int i = 0; i < fft32Result.length; i++ )
        {
            fft32Values[ i ] = fft32Result[ i ].abs();
        }
        GraphData fft32GraphData =
            new GraphData( "FFT 32 spectrum", "FFT 32", fft32Values, fft32FreqSpectrum.getFreqencies() );
        fft32GraphData.setxAxisLabel( "Frequency [Hz]" );

        // FFT 64 data
        FFTSpectrum fft64FreqSpectrum = new FFTSpectrum( fft64Block.getCurrentValue() );
        Complex[] fft64Result = fft64FreqSpectrum.getComplexValues();
        double[] fft64Values = new double[ fft64Result.length ];
        for( int i = 0; i < fft64Result.length; i++ )
        {
            fft64Values[ i ] = fft64Result[ i ].abs();
        }
        GraphData fft64GraphData =
            new GraphData( "FFT 64 spectrum", "FFT 64", fft64Values, fft64FreqSpectrum.getFreqencies() );
        fft64GraphData.setxAxisLabel( "Frequency [Hz]" );

        // FFT 128 data
        FFTSpectrum fft128FreqSpectrum = new FFTSpectrum( fft128Block.getCurrentValue() );
        Complex[] fft128Result = fft128FreqSpectrum.getComplexValues();
        double[] fft128Values = new double[ fft128Result.length ];
        for( int i = 0; i < fft128Result.length; i++ )
        {
            fft128Values[ i ] = fft128Result[ i ].abs();
        }
        GraphData fft128GraphData =
            new GraphData( "FFT 128 spectrum", "FFT 128", fft128Values, fft128FreqSpectrum.getFreqencies() );
        fft128GraphData.setxAxisLabel( "Frequency [Hz]" );

        // FFT 256 data
        FFTSpectrum fft256FreqSpectrum = new FFTSpectrum( fft256Block.getCurrentValue() );
        Complex[] fft256Result = fft256FreqSpectrum.getComplexValues();
        double[] fft256Values = new double[ fft256Result.length ];
        for( int i = 0; i < fft256Result.length; i++ )
        {
            fft256Values[ i ] = fft256Result[ i ].abs();
        }
        GraphData fft256GraphData =
            new GraphData( "FFT 256 spectrum", "FFT 256", fft256Values, fft256FreqSpectrum.getFreqencies() );
        fft256GraphData.setxAxisLabel( "Frequency [Hz]" );

        GraphData acor8GraphData = new GraphData( "Autocorrelation of 8 samples",
            "Autocorrelation of 8 samples", acor8Block.getCurrentValue().getResult() );
        GraphData acor16GraphData = new GraphData( "Autocorrelation of 16 samples",
            "Autocorrelation of 16 samples", acor16Block.getCurrentValue().getResult() );
        GraphData acor32GraphData = new GraphData( "Autocorrelation of 32 samples",
            "Autocorrelation of 32 samples", acor32Block.getCurrentValue().getResult() );
        GraphData acor64GraphData = new GraphData( "Autocorrelation of 64 samples",
            "Autocorrelation of 64 samples", acor64Block.getCurrentValue().getResult() );
        GraphData acor128GraphData = new GraphData( "Autocorrelation of 128 samples",
            "Autocorrelation of 128 samples", acor128Block.getCurrentValue().getResult() );
        GraphData acor256GraphData = new GraphData( "Autocorrelation of 256 samples",
            "Autocorrelation of 256 samples", acor256Block.getCurrentValue().getResult() );
        GraphData acor512GraphData = new GraphData( "Autocorrelation of 512 samples",
            "Autocorrelation of 512 samples", acor512Block.getCurrentValue().getResult() );

        return new GraphData[]
        { noiseGraphData, avgGraphData, fft8GraphData, fft16GraphData, fft32GraphData, fft64GraphData,
            fft128GraphData, fft256GraphData, acor8GraphData, acor16GraphData, acor32GraphData,
            acor64GraphData, acor128GraphData, acor256GraphData, acor512GraphData };
    }

    public static void main( final String[] args )
    {
        EventQueue.invokeLater( new Runnable()
        {

            @Override
            public void run()
            {
                SinusGraphExample demo = new SinusGraphExample();
                demo.pack();
                RefineryUtilities.centerFrameOnScreen( demo );
                demo.setVisible( true );
            }
        } );
    }
}
