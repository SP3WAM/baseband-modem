package com.github.sp3wam.baseband.modem.impl.morse.decoder;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockListenerIf;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;

public class MorseDecoderLifeExample extends ApplicationFrame
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseDecoderLifeExample.class );

    private static final long serialVersionUID = 949757038560097481L;

    private final static double Y_MIN = -100.0;
    private final static double Y_MAX = 100.0;
    private final double FFT_SAMPLER_FREQ = 3000.0;
    private final double MORSE_SAMPLER_FREQ = 200.0;
    private long counter = 0;

    private DynamicTimeSeriesCollection dataset;
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

                private int divider = (int)(FFT_SAMPLER_FREQ / (2 * MORSE_SAMPLER_FREQ));

                @Override
                public void onCurrentValueSet( FloatingPointSignal newValue )
                {
                    counter++;

                    if( counter % divider != 0 )
                    {
                        return;
                    }

                    dataset.advanceTime();
                    double value = newValue.getValue();
                    dataset.appendData( new float[]
                    { (float)value } );
                }
            } );
            morseDecoder.decodeFromJavaxAudioAsync( new MorseDecoderConsumer() );
        }
        catch( IOException e )
        {
            LOGGER.error( e.getMessage(), e );
        }
        morseDecoder.setSignalThreshold( FFT_SAMPLER_FREQ );
    }

    private void createGui()
    {
        setLayout( new BorderLayout( 0, 0 ) );
        JPanel mainPanel = new JPanel();
        add( mainPanel, BorderLayout.CENTER );

        GridBagLayout layout = new GridBagLayout();
        mainPanel.setLayout( layout );

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;

        dataset = new DynamicTimeSeriesCollection( 1, 240, new Second() );
        dataset.setTimeBase( new Second() );
        dataset.addSeries( new float[]
        {}, 0, 1 );

        JFreeChart chart = createChart( dataset, "Input signal" );
        mainPanel.add( new ChartPanel( chart ) );
    }

    private JFreeChart createChart( final XYDataset dataset, String title )
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
