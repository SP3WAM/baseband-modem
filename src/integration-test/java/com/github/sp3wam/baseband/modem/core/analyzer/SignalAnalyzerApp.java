package com.github.sp3wam.baseband.modem.core.analyzer;

import java.awt.EventQueue;

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

    public SignalAnalyzerApp()
    {
        super( "Signal analyzer appication" );
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
            double magnitude = fftSpectrum.getMagnitudeValues()[ index ];
            double powerPercentage = fftSpectrum.getPowerPercentageValues()[index];

            fft8SpectrumSeries.add( frequency, magnitude );
            
            fft8SpectrumPowerSeries.add( frequency, powerPercentage );
        }

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
