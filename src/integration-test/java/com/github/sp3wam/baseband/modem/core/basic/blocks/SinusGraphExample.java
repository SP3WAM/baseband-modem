package com.github.sp3wam.baseband.modem.core.basic.blocks;

import java.awt.EventQueue;

import org.apache.commons.math3.complex.Complex;
import org.jfree.ui.RefineryUtilities;

import com.github.sp3wam.baseband.modem.core.AbstractGraphExample;
import com.github.sp3wam.baseband.modem.core.GraphData;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.fft.FFTBlock;

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
        int samplingFreq = 16 * frequency;
        int fftWindow = 8;
        int samplesCount = 256;

        SystemClock clock = new SystemClock( samplingFreq );

        FloatingPointSinusGeneratorBlock generator = new FloatingPointSinusGeneratorBlock( 100.0, frequency );
        FFTBlock fftBlock = new FFTBlock( samplingFreq, fftWindow );
        FFTBlock fft16Block = new FFTBlock( samplingFreq, 2 * fftWindow );
        FFTBlock fft32Block = new FFTBlock( samplingFreq, 4 * fftWindow );
        FFTBlock fft64Block = new FFTBlock( samplingFreq, 8 * fftWindow );
        FFTBlock fft128Block = new FFTBlock( samplingFreq, 16 * fftWindow );
        FFTBlock fft256Block = new FFTBlock( samplingFreq, 32 * fftWindow );

        generator.setNextBlock( fftBlock );

        double[] noiseValues = new double[ samplesCount ];
        for( int q = 0; q < samplesCount; q++ )
        {
            generator.execute( clock, null );
            fft16Block.execute( clock, generator.getCurrentValue() );
            fft32Block.execute( clock, generator.getCurrentValue() );
            fft64Block.execute( clock, generator.getCurrentValue() );
            fft128Block.execute( clock, generator.getCurrentValue() );
            fft256Block.execute( clock, generator.getCurrentValue() );

            noiseValues[ q ] = generator.getCurrentValue().getValue();

            clock.step();
        }

        String text = String.format( "Sinus %s Hz", frequency );
        GraphData noiseGraphData = new GraphData( text, text, noiseValues );

        // FFT 8 data
        Complex[] fft8Result = fftBlock.getCurrentValue().getResult();
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

        return new GraphData[]
        { noiseGraphData, fft8GraphData, fft16GraphData, fft32GraphData, fft64GraphData, fft128GraphData,
            fft256GraphData };
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
