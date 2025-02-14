package com.github.sp3wam.baseband.modem.core.basic.blocks;

import java.awt.EventQueue;

import org.jfree.ui.RefineryUtilities;

import com.github.sp3wam.baseband.modem.core.AbstractGraphExample;
import com.github.sp3wam.baseband.modem.core.GraphData;
import com.github.sp3wam.baseband.modem.core.SystemClock;

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
        WhiteNoiseGeneratorBlock gen = new WhiteNoiseGeneratorBlock(100.0);
        SystemClock clock = new SystemClock( 44100 );

        int samplesCount = 100;
        
        double[] values = new double[ samplesCount ];
        for( int q = 0; q < samplesCount; q++ )
        {
            gen.execute( clock, null );
            values[ q ] = gen.getCurrentValue().getValue();
        }

        GraphData gi = new GraphData( "White noise", "White noise", values );

        return new GraphData[]
        { gi };
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
