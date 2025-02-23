package com.github.sp3wam.baseband.modem.core.analyzer;

import java.awt.EventQueue;

import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignalAnalyzerApp extends AbstractSignalAnalyzerApp
{
    private Logger LOGGER = LoggerFactory.getLogger( SignalAnalyzerApp.class );

    private static final long serialVersionUID = -379340225450115350L;

    public SignalAnalyzerApp()
    {
        super( "Signal analyzer appication" );
    }

    @Override
    protected void analyzeSignal( int selectedDataIndex )
    {
        // here do the analyze of the input signal
        // calculate FFTs and update the graphs
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
