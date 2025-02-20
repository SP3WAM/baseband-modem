package com.github.sp3wam.baseband.modem.core;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

public abstract class AbstractGraphExample extends ApplicationFrame
{
    private static final long serialVersionUID = -4387401639664932160L;

    private static final float Y_MAX = 100;
    private static final float Y_MIN = -100;

    private JScrollPane scrollPane;
    private JPanel graphsPanel;

    private List< DefaultXYDataset > datasets = new ArrayList< DefaultXYDataset >();

    public AbstractGraphExample( String windowTitle )
    {
        super( windowTitle );

        init();
    }

    protected abstract GraphData[] getGraphData();

    private void init()
    {
        createContents();

        GraphData[] graphInfos = getGraphData();

        for( GraphData graphInfo : graphInfos )
        {
            DefaultXYDataset dataset = new DefaultXYDataset();
            datasets.add( dataset );

            double[][] xyValues = new double[ 2 ][ graphInfo.getYValues().length ];
            for( int index = 0; index < graphInfo.getYValues().length; index++ )
            {
                if( graphInfo.getxValues() != null )
                {
                    xyValues[ 0 ][ index ] = graphInfo.getxValues()[ index ];
                }
                else
                {
                    xyValues[ 0 ][ index ] = index;
                }
                xyValues[ 1 ][ index ] = graphInfo.getYValues()[ index ];
            }
            dataset.addSeries( graphInfo.getSeriesName(), xyValues );

            JFreeChart chart = createChart( dataset, graphInfo.getTitle(), graphInfo.getxAxisLabel() );
            getGraphsPanel().add( new ChartPanel( chart ) );
        }
    }

    private JFreeChart createChart( final XYDataset dataset, String title, String xAxisLabel )
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
    }

    private JScrollPane getScrollPane()
    {
        if( scrollPane == null )
        {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView( getGraphsPanel() );
        }
        return scrollPane;
    }

    private JPanel getGraphsPanel()
    {
        if( graphsPanel == null )
        {
            graphsPanel = new JPanel();
            graphsPanel.setLayout( new BoxLayout( graphsPanel, BoxLayout.Y_AXIS ) );
        }
        return graphsPanel;
    }
}
