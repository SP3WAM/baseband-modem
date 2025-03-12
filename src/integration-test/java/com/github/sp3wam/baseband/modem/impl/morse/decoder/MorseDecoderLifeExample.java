package com.github.sp3wam.baseband.modem.impl.morse.decoder;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockListenerIf;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTSignal;
import com.github.sp3wam.baseband.modem.core.fft.FFTSpectrum;

public class MorseDecoderLifeExample extends ApplicationFrame
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseDecoderLifeExample.class );

    private static final long serialVersionUID = 949757038560097481L;

    private final static double Y_MIN = -100.0;
    private final static double Y_MAX = 100.0;
    private final double FFT_SAMPLER_FREQ = 3000.0;
    private final double MORSE_SAMPLER_FREQ = 200.0;
    private long counter = 0;
    private int signalNoiseThreshold = 16;

    private DynamicTimeSeriesCollection signalDataset;
    private XYSeriesCollection spectrumPercentageDataset;
    private XYSeries spectrumPercentageSeries;
    private XYSeries thresholdSeries;

    private MorseDecoder morseDecoder;

    public MorseDecoderLifeExample()
    {
        super( "Morse decoder" );

        createGui();
        initMorseDecoder();
    }

    private void initMorseDecoder()
    {
        morseDecoder = new MorseDecoder();
        try
        {
            morseDecoder.addFftSamplerListener( new BlockListenerIf< FloatingPointSignal >()
            {

                private int divider = (int)(FFT_SAMPLER_FREQ / MORSE_SAMPLER_FREQ);

                @Override
                public void onCurrentValueSet( FloatingPointSignal newValue )
                {
                    counter++;

                    if( counter % divider != 0 )
                    {
                        return;
                    }

                    signalDataset.advanceTime();
                    double value = newValue.getValue();
                    signalDataset.appendData( new float[]
                    { (float)value } );
                }
            } );
            morseDecoder.addFftListener( new BlockListenerIf< FFTSignal >()
            {
                private int divider = (int)(FFT_SAMPLER_FREQ / (2 * MORSE_SAMPLER_FREQ));

                @Override
                public void onCurrentValueSet( FFTSignal newValue )
                {
                    if( counter % (10 * divider) != 0 )
                    {
                        return;
                    }

                    FFTSpectrum fftSpectrum = new FFTSpectrum( newValue );

                    spectrumPercentageSeries.clear();
                    thresholdSeries.clear();
                    for( int index = 0; index < fftSpectrum.getFreqencies().length; index++ )
                    {
                        double frequency = fftSpectrum.getFreqencies()[ index ];
                        double powerPercentage =
                            fftSpectrum.getPowerSpectralDensityPercentageValues()[ index ];

                        spectrumPercentageSeries.add( frequency, powerPercentage );
                        thresholdSeries.add( frequency, signalNoiseThreshold );
                    }
                }
            } );

            morseDecoder.decodeFromJavaxAudioAsync( new MorseDecoderConsumer() );
        }
        catch( IOException e )
        {
            LOGGER.error( e.getMessage(), e );
        }

        morseDecoder.setSignalThreshold( signalNoiseThreshold );
    }

    private void createGui()
    {
        setLayout( new BorderLayout( 0, 0 ) );
        JPanel mainPanel = new JPanel();
        add( mainPanel, BorderLayout.CENTER );

        GridBagLayout layout = new GridBagLayout();
        mainPanel.setLayout( layout );

        GridBagConstraints c = new GridBagConstraints();

        // Signal chart
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;

        signalDataset = new DynamicTimeSeriesCollection( 1, 512, new Second() );
        signalDataset.setTimeBase( new Second() );
        signalDataset.addSeries( new float[]
        {}, 0, 1 );

        JFreeChart signalChart = createTimeSeriesChart( signalDataset, "Input signal" );
        mainPanel.add( new ChartPanel( signalChart ), c );

        // Spectrum percentage chart
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;

        spectrumPercentageDataset = new XYSeriesCollection();
        spectrumPercentageSeries = new XYSeries( "PSD vs total power [%]" );
        spectrumPercentageDataset.addSeries( spectrumPercentageSeries );

        thresholdSeries = new XYSeries( "Signal/Noise threshold" );
        spectrumPercentageDataset.addSeries( thresholdSeries );

        JFreeChart spectrumChart =
            createXYLineChart( spectrumPercentageDataset, "Power spectrum distribution [%]", "Frequency" );
        mainPanel.add( new ChartPanel( spectrumChart ), c );

        // Signal/Noise threshold slider
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.0;

        JSlider slider = new JSlider( JSlider.VERTICAL, 0, 50, signalNoiseThreshold );
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                signalNoiseThreshold = ((JSlider)e.getSource()).getValue();
                morseDecoder.setSignalThreshold( signalNoiseThreshold );
            }
         });
        mainPanel.add( slider, c );
    }

    private JFreeChart createTimeSeriesChart( final DynamicTimeSeriesCollection dataset, String title )
    {
        final JFreeChart result =
            ChartFactory.createTimeSeriesChart( title, "hh:mm:ss", "[%]", dataset, true, true, false );
        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange( true );
        ValueAxis range = plot.getRangeAxis();
        range.setRange( Y_MIN, Y_MAX );
        range.setAutoRange( false );
        return result;
    }

    protected JFreeChart createXYLineChart( final XYDataset dataset, String title, String xAxisLabel )
    {
        String _xAxisLabel = (xAxisLabel == null ? "Samples" : xAxisLabel);
        JFreeChart result = ChartFactory.createXYLineChart( title, _xAxisLabel, "Value", dataset );
        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange( true );
        ValueAxis range = plot.getRangeAxis();
        range.setRange( 0, 50 );
        range.setAutoRange( false );
        return result;
    }

    public static void main( final String[] args )
    {
        EventQueue.invokeLater( new Runnable()
        {

            @Override
            public void run()
            {
                MorseDecoderLifeExample demo = new MorseDecoderLifeExample();
                demo.pack();
                RefineryUtilities.centerFrameOnScreen( demo );
                demo.setVisible( true );
            }
        } );
    }
}
