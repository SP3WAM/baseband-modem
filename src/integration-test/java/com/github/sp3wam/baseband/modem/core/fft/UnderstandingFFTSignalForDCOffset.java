package com.github.sp3wam.baseband.modem.core.fft;

import java.awt.EventQueue;

import org.apache.commons.math3.complex.Complex;
import org.jfree.ui.RefineryUtilities;

import com.github.sp3wam.baseband.modem.core.AbstractGraphExample;
import com.github.sp3wam.baseband.modem.core.GraphData;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.FloatingPointSinusGeneratorBlock;

/***
 * An example to help understand the meaning of {@linkplain FFTSignal#getResult()} array, which holds the
 * result data of the FFT calculations.
 * <p>
 * The result stored under index 0 relates to the frequency of 0 Hz (zero Hz) and - as shown in the example -
 * holds the DC offset of the examined signal.
 */
public class UnderstandingFFTSignalForDCOffset extends AbstractGraphExample
{
    private static final long serialVersionUID = 6540971832319619540L;

    final static String WINDOW_TITLE = "Sinus generator";

    public UnderstandingFFTSignalForDCOffset()
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

        FloatingPointSinusGeneratorBlock sinusGenerator =
            new FloatingPointSinusGeneratorBlock( 100.0, frequency );
        FFTBlock fft32Block = new FFTBlock( samplingFreq, 4 * fftWindow );
        sinusGenerator.setNextBlock( fft32Block );

        FloatingPointSinusGeneratorBlock sinusDCOffsetGenerator =
            new FloatingPointSinusGeneratorBlock( 100.0, frequency );
        sinusDCOffsetGenerator.setDCOffset( 100.0 );
        FFTBlock fftDCOffset32Block = new FFTBlock( samplingFreq, 4 * fftWindow );
        sinusDCOffsetGenerator.setNextBlock( fftDCOffset32Block );

        double[] sinusValues = new double[ samplesCount ];
        double[] sinusDCOffsetValues = new double[ samplesCount ];
        for( int q = 0; q < samplesCount; q++ )
        {
            sinusGenerator.execute( clock, null );
            sinusValues[ q ] = sinusGenerator.getCurrentValue().getValue();

            sinusDCOffsetGenerator.execute( clock, null );
            sinusDCOffsetValues[ q ] = sinusDCOffsetGenerator.getCurrentValue().getValue();

            clock.step();
        }

        // Sinus data
        String text = String.format( "Sinus %s Hz...", frequency );
        GraphData sinusGraphData = new GraphData( text, text, sinusValues );

        // Sinus FFT 32 data
        Complex[] fft32Result = fft32Block.getCurrentValue().getResult();
        double[] fft32Values = new double[ fft32Result.length ];
        for( int i = 0; i < fft32Result.length; i++ )
        {
            fft32Values[ i ] = fft32Result[ i ].abs();
        }
        GraphData fft32GraphData = new GraphData( "... and its FFT 32 result", "FFT 32", fft32Values );
        fft32GraphData.setxAxisLabel( "Index of FFT result array" );

        // FFT 32 spectrum data
        FFTSpectrum fft32Spectrum = new FFTSpectrum( fft32Block.getCurrentValue() );
        GraphData fft32FreqGraphData = new GraphData( "... and its FFT 32 spectrum", "FFT 32",
            fft32Spectrum.getPowerSpectralDensityValues(), fft32Spectrum.getFreqencies() );
        fft32FreqGraphData.setxAxisLabel( "Frequency [Hz]" );

        // Sinus with offset data
        String textOffset = String.format( "Sinus %s Hz with DC offset...", frequency );
        GraphData sinusDCOffsetGraphData = new GraphData( textOffset, textOffset, sinusDCOffsetValues );

        // Sinus with DC offset FFT 32 data
        Complex[] fft32DCOffsetResult = fftDCOffset32Block.getCurrentValue().getResult();
        double[] fft32DCOffsetValues = new double[ fft32DCOffsetResult.length ];
        for( int i = 0; i < fft32DCOffsetResult.length; i++ )
        {
            fft32DCOffsetValues[ i ] = fft32DCOffsetResult[ i ].abs();
        }
        GraphData fft32DCOffsetGraphData = new GraphData(
            "... and its FFT 32 result. DC offset is under sample index 0.", "FFT 32", fft32DCOffsetValues );
        fft32DCOffsetGraphData.setxAxisLabel( "Index of FFT result array" );

        // FFT 32 spectrum data
        FFTSpectrum fft32FreqDCOffsetSpectrum = new FFTSpectrum( fftDCOffset32Block.getCurrentValue() );
        GraphData fft32FreqDCOffsetGraphData = new GraphData( "... and its FFT 32 spectrum", "FFT 32",
            fft32FreqDCOffsetSpectrum.getPowerSpectralDensityValues(),
            fft32FreqDCOffsetSpectrum.getFreqencies() );
        fft32FreqDCOffsetGraphData.setxAxisLabel( "Frequency [Hz]" );

        return new GraphData[]
        { sinusGraphData, fft32GraphData, fft32FreqGraphData, sinusDCOffsetGraphData, fft32DCOffsetGraphData,
            fft32FreqDCOffsetGraphData };
    }

    public static void main( final String[] args )
    {
        EventQueue.invokeLater( new Runnable()
        {

            @Override
            public void run()
            {
                UnderstandingFFTSignalForDCOffset demo = new UnderstandingFFTSignalForDCOffset();
                demo.pack();
                RefineryUtilities.centerFrameOnScreen( demo );
                demo.setVisible( true );
            }
        } );
    }
}
