package com.github.sp3wam.baseband.modem.core.basic.blocks;

import java.awt.EventQueue;

import org.apache.commons.math3.complex.Complex;
import org.jfree.ui.RefineryUtilities;

import com.github.sp3wam.baseband.modem.core.AbstractGraphExample;
import com.github.sp3wam.baseband.modem.core.GraphData;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.correlation.AutocorrelationBlock;
import com.github.sp3wam.baseband.modem.core.fft.FFTBlock;

public class WhiteNoiseGraphExample extends AbstractGraphExample
{
    private static final long serialVersionUID = 6540971832319619540L;

    final static String WINDOW_TITLE = "White noise generator";

    public WhiteNoiseGraphExample()
    {
        super( WINDOW_TITLE );
    }

    @Override
    protected GraphData[] getGraphData()
    {
        int samplingFreq = 44100;
        int fftWindow = 8;
        int samplesCount = 512;

        SystemClock clock = new SystemClock( samplingFreq );

        WhiteNoiseGeneratorBlock noiseGenerator = new WhiteNoiseGeneratorBlock( 100.0 );
        FloatingPointAveragerBlock avgBlock = new FloatingPointAveragerBlock( samplesCount );
        FFTBlock fft8Block = new FFTBlock( samplingFreq, fftWindow );
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

        double[] noiseValues = new double[ samplesCount ];
        double[] noiseAvgValues = new double[ samplesCount ];
        for( int q = 0; q < samplesCount; q++ )
        {
            noiseGenerator.execute( clock, null );
            avgBlock.execute( clock, noiseGenerator.getCurrentValue() );
            fft8Block.execute( clock, noiseGenerator.getCurrentValue() );
            fft16Block.execute( clock, noiseGenerator.getCurrentValue() );
            fft32Block.execute( clock, noiseGenerator.getCurrentValue() );
            fft64Block.execute( clock, noiseGenerator.getCurrentValue() );
            fft128Block.execute( clock, noiseGenerator.getCurrentValue() );
            fft256Block.execute( clock, noiseGenerator.getCurrentValue() );
            acor8Block.execute( clock, noiseGenerator.getCurrentValue() );
            acor16Block.execute( clock, noiseGenerator.getCurrentValue() );
            acor32Block.execute( clock, noiseGenerator.getCurrentValue() );
            acor64Block.execute( clock, noiseGenerator.getCurrentValue() );
            acor128Block.execute( clock, noiseGenerator.getCurrentValue() );
            acor256Block.execute( clock, noiseGenerator.getCurrentValue() );
            acor512Block.execute( clock, noiseGenerator.getCurrentValue() );

            noiseValues[ q ] = noiseGenerator.getCurrentValue().getValue();
            noiseAvgValues[ q ] = avgBlock.getCurrentValue().getValue();
        }

        GraphData noiseGraphData = new GraphData( "White noise", "White noise", noiseValues );

        // Noise average data
        GraphData avgGraphData =
            new GraphData( "Noise average of N samples", "Noise average of N samples", noiseAvgValues );

        // FFT 8 data
        Complex[] fft8Result = fft8Block.getCurrentValue().getResult();
        double[] fft8Values = new double[ fft8Result.length ];
        for( int i = 0; i < fft8Result.length; i++ )
        {
            fft8Values[ i ] = fft8Result[ i ].abs();
        }
        GraphData fft8GraphData = new GraphData( "FFT 8", "FFT 8", fft8Values );

        // FFT 16 data
        Complex[] fft16Result = fft16Block.getCurrentValue().getResult();
        double[] fft16Values = new double[ fft16Result.length ];
        for( int i = 0; i < fft16Result.length; i++ )
        {
            fft16Values[ i ] = fft16Result[ i ].abs();
        }
        GraphData fft16GraphData = new GraphData( "FFT 16", "FFT 16", fft16Values );

        // FFT 32 data
        Complex[] fft32Result = fft32Block.getCurrentValue().getResult();
        double[] fft32Values = new double[ fft32Result.length ];
        for( int i = 0; i < fft32Result.length; i++ )
        {
            fft32Values[ i ] = fft32Result[ i ].abs();
        }
        GraphData fft32GraphData = new GraphData( "FFT 32", "FFT 32", fft32Values );

        // FFT 64 data
        Complex[] fft64Result = fft64Block.getCurrentValue().getResult();
        double[] fft64Values = new double[ fft64Result.length ];
        for( int i = 0; i < fft64Result.length; i++ )
        {
            fft64Values[ i ] = fft64Result[ i ].abs();
        }
        GraphData fft64GraphData = new GraphData( "FFT 64", "FFT 64", fft64Values );

        // FFT 128 data
        Complex[] fft128Result = fft128Block.getCurrentValue().getResult();
        double[] fft128Values = new double[ fft128Result.length ];
        for( int i = 0; i < fft128Result.length; i++ )
        {
            fft128Values[ i ] = fft128Result[ i ].abs();
        }
        GraphData fft128GraphData = new GraphData( "FFT 128", "FFT 128", fft128Values );

        // FFT 256 data
        Complex[] fft256Result = fft256Block.getCurrentValue().getResult();
        double[] fft256Values = new double[ fft256Result.length ];
        for( int i = 0; i < fft256Result.length; i++ )
        {
            fft256Values[ i ] = fft256Result[ i ].abs();
        }
        GraphData fft256GraphData = new GraphData( "FFT 256", "FFT 256", fft256Values );

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
                WhiteNoiseGraphExample demo = new WhiteNoiseGraphExample();
                demo.pack();
                RefineryUtilities.centerFrameOnScreen( demo );
                demo.setVisible( true );
            }
        } );
    }
}
