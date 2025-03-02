package com.github.sp3wam.baseband.modem.core.analyzer;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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
    private final static long DESIRED_FFT_SAMPLE_RATE = 3000;

    /** FFT 8 objects */
    // a dataset for Power Spectral Density
    private XYSeriesCollection fft8SpectrumDataset;
    private XYSeries fft8SpectrumSeries;
    // a dataset for percentage Power Spectral Density
    private XYSeriesCollection fft8PSDPercentageDataset;
    private XYSeries fft8PSDPercentageSeries;
    // and the chart itself
    private JFreeChart fft8SpectrumChart;
    private ChartPanel fft8SpectrumChartPanel;

    /** FFT 16 objects */
    // a dataset for Power Spectral Density
    private XYSeriesCollection fft16SpectrumDataset;
    private XYSeries fft16SpectrumSeries;
    // a dataset for percentage Power Spectral Density
    private XYSeriesCollection fft16PSDPercentageDataset;
    private XYSeries fft16PSDPercentageSeries;
    // and the chart itself
    private JFreeChart fft16SpectrumChart;
    private ChartPanel fft16SpectrumChartPanel;

    /** FFT 32 objects */
    // a dataset for Power Spectral Density
    private XYSeriesCollection fft32SpectrumDataset;
    private XYSeries fft32SpectrumSeries;
    // a dataset for percentage Power Spectral Density
    private XYSeriesCollection fft32PSDPercentageDataset;
    private XYSeries fft32PSDPercentageSeries;
    // and the chart itself
    private JFreeChart fft32SpectrumChart;
    private ChartPanel fft32SpectrumChartPanel;

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
        analyzeFFT8( selectedDataIndex );
        analyzeFFT16( selectedDataIndex );
        analyzeFFT32( selectedDataIndex );
    }

    private void analyzeFFT8( int selectedDataIndex )
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
        fft8PSDPercentageSeries.clear();
        for( int index = 0; index < fftSpectrum.getFreqencies().length; index++ )
        {
            double frequency = fftSpectrum.getFreqencies()[ index ];
            double magnitude = fftSpectrum.getPowerSpectralDensityValues()[ index ];
            double powerPercentage = fftSpectrum.getPowerSpectralDensityPercentageValues()[ index ];

            fft8SpectrumSeries.add( frequency, magnitude );
            fft8PSDPercentageSeries.add( frequency, powerPercentage );
        }
    }

    private void analyzeFFT16( int selectedDataIndex )
    {
        int fftWindowSize = 16;

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

        fft16SpectrumSeries.clear();
        fft16PSDPercentageSeries.clear();
        for( int index = 0; index < fftSpectrum.getFreqencies().length; index++ )
        {
            double frequency = fftSpectrum.getFreqencies()[ index ];
            double magnitude = fftSpectrum.getPowerSpectralDensityValues()[ index ];
            double powerPercentage = fftSpectrum.getPowerSpectralDensityPercentageValues()[ index ];

            fft16SpectrumSeries.add( frequency, magnitude );
            fft16PSDPercentageSeries.add( frequency, powerPercentage );
        }
    }
    
    private void analyzeFFT32( int selectedDataIndex )
    {
        int fftWindowSize = 32;

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

        fft32SpectrumSeries.clear();
        fft32PSDPercentageSeries.clear();
        for( int index = 0; index < fftSpectrum.getFreqencies().length; index++ )
        {
            double frequency = fftSpectrum.getFreqencies()[ index ];
            double magnitude = fftSpectrum.getPowerSpectralDensityValues()[ index ];
            double powerPercentage = fftSpectrum.getPowerSpectralDensityPercentageValues()[ index ];

            fft32SpectrumSeries.add( frequency, magnitude );
            fft32PSDPercentageSeries.add( frequency, powerPercentage );
        }
    }

    @Override
    protected void createAdditionalContent()
    {
        JPanel graphsPanel = new JPanel();
        graphsPanel.setLayout( new BoxLayout( graphsPanel, BoxLayout.Y_AXIS ) );
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView( graphsPanel );

        prepareFFT8Objects();
        graphsPanel.add( fft8SpectrumChartPanel );

        prepareFFT16Objects();
        graphsPanel.add( fft16SpectrumChartPanel );

        prepareFFT32Objects();
        graphsPanel.add( fft32SpectrumChartPanel );

        getMainPanel().add( scrollPane );
    }

    private void prepareFFT8Objects()
    {
        // create a default chart with main axis for power spectral data
        fft8SpectrumDataset = new XYSeriesCollection();
        fft8SpectrumSeries = new XYSeries( "FFT 8 spectrum" );
        fft8SpectrumDataset.addSeries( fft8SpectrumSeries );
        fft8SpectrumChart = createChart( fft8SpectrumDataset, "FFT 8 spectrum", "Frequency [Hz]" );
        fft8SpectrumChartPanel = new ChartPanel( fft8SpectrumChart );

        // create a secondary axis for percentage power spectral data
        XYPlot plot = (XYPlot)fft8SpectrumChart.getPlot();
        NumberAxis secondaryAxis = new NumberAxis( "PSD [%]" );
        secondaryAxis.setRange( 0.0, 100.0 );
        plot.setRangeAxis( 1, secondaryAxis ); // Add the secondary axis at position 1

        // create a dataset for percentage Power Spectral Density
        fft8PSDPercentageDataset = new XYSeriesCollection();
        fft8PSDPercentageSeries = new XYSeries( "PSD vs total power [%]" );
        fft8PSDPercentageDataset.addSeries( fft8PSDPercentageSeries );

        // add the second dataset and map it to the secondary axis
        plot.setDataset( 1, fft8PSDPercentageDataset );
        plot.mapDatasetToRangeAxis( 1, 1 );

        // set custom renderer to change the color to blue
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint( 0, Color.BLUE );
        // renderer.setSeriesShapesVisible( 0, false );
        renderer.setBaseToolTipGenerator( ( xyDataset, series, item ) -> "X: "
            + xyDataset.getX( series, item ) + " , Y: " + xyDataset.getY( series, item ) );
        plot.setRenderer( 1, renderer );
    }

    private void prepareFFT16Objects()
    {
        // create a default chart with main axis for power spectral data
        fft16SpectrumDataset = new XYSeriesCollection();
        fft16SpectrumSeries = new XYSeries( "FFT 16 spectrum" );
        fft16SpectrumDataset.addSeries( fft16SpectrumSeries );
        fft16SpectrumChart = createChart( fft16SpectrumDataset, "FFT 16 spectrum", "Frequency [Hz]" );
        fft16SpectrumChartPanel = new ChartPanel( fft16SpectrumChart );

        // create a secondary axis for percentage power spectral data
        XYPlot plot = (XYPlot)fft16SpectrumChart.getPlot();
        NumberAxis secondaryAxis = new NumberAxis( "PSD [%]" );
        secondaryAxis.setRange( 0.0, 100.0 );
        plot.setRangeAxis( 1, secondaryAxis ); // Add the secondary axis at position 1

        // create a dataset for percentage Power Spectral Density
        fft16PSDPercentageDataset = new XYSeriesCollection();
        fft16PSDPercentageSeries = new XYSeries( "PSD vs total power [%]" );
        fft16PSDPercentageDataset.addSeries( fft16PSDPercentageSeries );

        // add the second dataset and map it to the secondary axis
        plot.setDataset( 1, fft16PSDPercentageDataset );
        plot.mapDatasetToRangeAxis( 1, 1 );

        // set custom renderer to change the color to blue
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint( 0, Color.BLUE );
        // renderer.setSeriesShapesVisible( 0, false );
        renderer.setBaseToolTipGenerator( ( xyDataset, series, item ) -> "X: "
            + xyDataset.getX( series, item ) + " , Y: " + xyDataset.getY( series, item ) );
        plot.setRenderer( 1, renderer );
    }

    private void prepareFFT32Objects()
    {
        // create a default chart with main axis for power spectral data
        fft32SpectrumDataset = new XYSeriesCollection();
        fft32SpectrumSeries = new XYSeries( "FFT 32 spectrum" );
        fft32SpectrumDataset.addSeries( fft32SpectrumSeries );
        fft32SpectrumChart = createChart( fft32SpectrumDataset, "FFT 32 spectrum", "Frequency [Hz]" );
        fft32SpectrumChartPanel = new ChartPanel( fft32SpectrumChart );

        // create a secondary axis for percentage power spectral data
        XYPlot plot = (XYPlot)fft32SpectrumChart.getPlot();
        NumberAxis secondaryAxis = new NumberAxis( "PSD [%]" );
        secondaryAxis.setRange( 0.0, 100.0 );
        plot.setRangeAxis( 1, secondaryAxis ); // Add the secondary axis at position 1

        // create a dataset for percentage Power Spectral Density
        fft32PSDPercentageDataset = new XYSeriesCollection();
        fft32PSDPercentageSeries = new XYSeries( "PSD vs total power [%]" );
        fft32PSDPercentageDataset.addSeries( fft32PSDPercentageSeries );

        // add the second dataset and map it to the secondary axis
        plot.setDataset( 1, fft32PSDPercentageDataset );
        plot.mapDatasetToRangeAxis( 1, 1 );

        // set custom renderer to change the color to blue
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint( 0, Color.BLUE );
        // renderer.setSeriesShapesVisible( 0, false );
        renderer.setBaseToolTipGenerator( ( xyDataset, series, item ) -> "X: "
            + xyDataset.getX( series, item ) + " , Y: " + xyDataset.getY( series, item ) );
        plot.setRenderer( 1, renderer );
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
