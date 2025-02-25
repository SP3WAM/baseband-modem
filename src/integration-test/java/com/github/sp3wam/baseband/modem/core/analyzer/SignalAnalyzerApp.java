package com.github.sp3wam.baseband.modem.core.analyzer;

import java.awt.EventQueue;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTBlock;
import com.github.sp3wam.baseband.modem.core.fft.FFTSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTSpectrum;

public class SignalAnalyzerApp extends AbstractSignalAnalyzerApp
{
    private Logger LOGGER = LoggerFactory.getLogger( SignalAnalyzerApp.class );

    private static final long serialVersionUID = -379340225450115350L;
    private final static long DESIRED_FFT_SAMPLE_RATE = 6000;

    /** FFT 8 objects */
    private long fftSampleRate;
    private XYSeriesCollection fft8SpectrumDataset;
    private XYSeries fft8SpectrumSeries;
    private XYSeries fft8SpectrumPowerSeries;
    private JFreeChart fft8SpectrumChart;
    private ChartPanel fft8SpectrumChartPanel;

    public SignalAnalyzerApp()
    {
        super( "Signal analyzer appication" );
    }

    @Override
    protected long getDesiredFftSampleRate()
    {
        return DESIRED_FFT_SAMPLE_RATE;
    }

    @Override
    protected void analyzeSignal( int selectedDataIndex )
    {
        int fftWindowSize = 8;

        // here do the analyze of the input signal
        // calculate FFTs and update the graphs
        FFTBlock fftBlock = new FFTBlock( (int)getFftSampleRate(), fftWindowSize );
        SystemClock systemClock = new SystemClock( getFftSampleRate() );

        for( int q = 0; q < fftWindowSize; q++ )
        {
            int dataIndex = selectedDataIndex - fftWindowSize + q;
            if( dataIndex < 0 )
            {
                continue;
            }
            FloatingPointSignal dataSignal = new FloatingPointSignal( signalData[ dataIndex ] );
            fftBlock.execute( systemClock, dataSignal );
        }

        FFTSignal fftSignal = fftBlock.getCurrentValue();
        FFTSpectrum fftSpectrum = new FFTSpectrum( fftSignal );

        fft8SpectrumSeries.clear();
        fft8SpectrumPowerSeries.clear();
        for( int index = 0; index < fftSpectrum.getFreqencies().length; index++ )
        {
            double frequency = fftSpectrum.getFreqencies()[ index ];
            double magnitude = fftSpectrum.getPowerSpectralDensityValues()[ index ];
            double powerPercentage = fftSpectrum.getPowerSpectralDensityPercentageValues()[ index ];

            fft8SpectrumSeries.add( frequency, magnitude );

            fft8SpectrumPowerSeries.add( frequency, powerPercentage );
        }

    }

    @Override
    protected void createAdditionalContent()
    {
        fft8SpectrumDataset = new XYSeriesCollection();
        fft8SpectrumSeries = new XYSeries( "FFT 8 spectrum" );
        fft8SpectrumPowerSeries = new XYSeries( "PSD vs total power [%]" );
        fft8SpectrumDataset.addSeries( fft8SpectrumSeries );
        fft8SpectrumDataset.addSeries( fft8SpectrumPowerSeries );
        fft8SpectrumChart = createChart( fft8SpectrumDataset, "FFT 8 spectrum", "Frequency [Hz]" );
        fft8SpectrumChartPanel = new ChartPanel( fft8SpectrumChart );
        getMainPanel().add( fft8SpectrumChartPanel );
    }

    public static void main( final String[] args )
    {
        EventQueue.invokeLater( new Runnable()
        {

            @Override
            public void run()
            {
                SignalAnalyzerApp demo = new SignalAnalyzerApp();
                demo.pack();
                RefineryUtilities.centerFrameOnScreen( demo );
                demo.setVisible( true );
            }
        } );
    }
}
