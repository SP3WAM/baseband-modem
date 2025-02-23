package com.github.sp3wam.baseband.modem.core.analyzer;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.AbstractConsumerBlock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.SamplerBlock;
import com.github.sp3wam.baseband.modem.core.basic.signals.FloatingPointSignal;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromFileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromMp3FileSignalGeneratorBlock;
import com.github.sp3wam.baseband.modem.core.pcm.PcmFromWavFileSignalGeneratorBlock;

public class SignalAnalyzerApp extends ApplicationFrame
{
    private Logger LOGGER = LoggerFactory.getLogger( SignalAnalyzerApp.class );

    private static final float Y_MAX = 100;
    private static final float Y_MIN = -100;

    private static final long serialVersionUID = 7811675069620687839L;

    private JScrollPane scrollPane;
    private JPanel mainPanel;
    private JButton openSoundFileButton;

    private Double[] signalData;

    public SignalAnalyzerApp()
    {
        super( "Signal analyzer appication" );

        LOGGER.info( "Creating applicatin context." );

        init();
    }

    private void init()
    {
        createContents();
    }

    private void readSignalFromFile( File file ) throws IOException
    {
        PcmFromFileSignalGeneratorBlock signalGenerator = null;

        String filePath = file.getAbsolutePath();
        if( filePath.endsWith( ".wav" ) )
        {
            signalGenerator = new PcmFromWavFileSignalGeneratorBlock( 100, filePath );
            signalGenerator.init();
        }
        else if( filePath.endsWith( ".mp3" ) )
        {
            signalGenerator = new PcmFromMp3FileSignalGeneratorBlock( 100, filePath );
            signalGenerator.init();
        }
        else
        {
            LOGGER.error( String.format( "Unsupported signal file extension %s", file.getName() ) );
        }

        long inputSignalSampleRate = signalGenerator.getSampleRate();
        int fftSamplerDivider = (int)(inputSignalSampleRate / 6000.0);

        SamplerBlock< FloatingPointSignal, FloatingPointSignal > fftSampler =
            new SamplerBlock< FloatingPointSignal, FloatingPointSignal >( fftSamplerDivider );
        signalGenerator.setNextBlock( fftSampler );

        ArrayList< Double > data = new ArrayList< Double >();
        fftSampler.setNextBlock( new AbstractConsumerBlock< FloatingPointSignal, FloatingPointSignal >()
        {
            @Override
            protected boolean execute0( SystemClock systemClock, FloatingPointSignal inputSignalValue )
            {
                data.add( inputSignalValue.getValue() );

                return false;
            }
        } );

        SystemClock systemClock = new SystemClock( signalGenerator.getSampleRate() );
        while( signalGenerator.hasMoreSamples() )
        {
            signalGenerator.execute( systemClock, null );

            systemClock.step();
        }
        signalData = data.toArray( new Double[]
        {} );

        DefaultXYDataset dataset = new DefaultXYDataset();
        double[][] xyValues = new double[ 2 ][ signalData.length ];
        for( int index = 0; index < signalData.length; index++ )
        {
            xyValues[ 0 ][ index ] = index;
            xyValues[ 1 ][ index ] = signalData[ index ];
        }
        dataset.addSeries( "Input signal", xyValues );

        JFreeChart chart = createChart( dataset, "Input signal", "Samples" );
        getMainPanel().add( new ChartPanel( chart ) );

        this.pack();
    }

    protected JFreeChart createChart( final XYDataset dataset, String title, String xAxisLabel )
    {
        String _xAxisLabel = (xAxisLabel == null ? "Samples" : xAxisLabel);
        JFreeChart result = ChartFactory.createXYLineChart( title, _xAxisLabel, "Value", dataset );
        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange( true );
        ValueAxis range = plot.getRangeAxis();
        range.setRange( Y_MIN, Y_MAX );
        range.setAutoRange( true );
        return result;
    }

    private void createContents()
    {
        setLayout( new BorderLayout( 0, 0 ) );
        add( getScrollPane(), BorderLayout.CENTER );

        getMainPanel().add( getOpenSoundFileButton() );
    }

    private JScrollPane getScrollPane()
    {
        if( scrollPane == null )
        {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView( getMainPanel() );
        }
        return scrollPane;
    }

    private JPanel getMainPanel()
    {
        if( mainPanel == null )
        {
            mainPanel = new JPanel();
            mainPanel.setLayout( new BoxLayout( mainPanel, BoxLayout.Y_AXIS ) );
        }

        return mainPanel;
    }

    private JButton getOpenSoundFileButton()
    {
        if( openSoundFileButton == null )
        {
            openSoundFileButton = new JButton( "Open sound file" );
            openSoundFileButton.addActionListener( new ActionListener()
            {

                @Override
                public void actionPerformed( ActionEvent e )
                {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter( new FileFilter()
                    {

                        @Override
                        public boolean accept( File f )
                        {
                            if( f.isDirectory() )
                            {
                                return true;
                            }
                            if( f.getName().endsWith( ".wav" ) )
                            {
                                return true;
                            }
                            if( f.getName().endsWith( ".mp3" ) )
                            {
                                return true;
                            }

                            return false;
                        }

                        @Override
                        public String getDescription()
                        {
                            return "Sound files (*.wav, *.mp3)";
                        }
                    } );
                    int result = fileChooser.showOpenDialog( SignalAnalyzerApp.this );
                    if( result == JFileChooser.APPROVE_OPTION )
                    {
                        File file = fileChooser.getSelectedFile();
                        LOGGER.info( String.format( "File %s chosen", file.toString() ) );

                        try
                        {
                            readSignalFromFile( file );
                        }
                        catch( IOException ex )
                        {
                            LOGGER.error( ex.getMessage(), ex );
                        }
                    }
                }
            } );
        }

        return openSoundFileButton;
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
