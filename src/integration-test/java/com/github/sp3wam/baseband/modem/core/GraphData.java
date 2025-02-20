package com.github.sp3wam.baseband.modem.core;

public class GraphData
{

    private String title = null;
    private String seriesName = null;
    private String xAxisLabel = null;
    private double[] yValues;
    private double[] xValues = null;

    public GraphData( String title, String seriesName, double[] yValues )
    {
        this.title = title;
        this.seriesName = seriesName;
        this.yValues = yValues;
    }

    public GraphData( String title, String seriesName, double[] yValues, double[] xValues )
    {
        this( title, seriesName, yValues );
        this.xValues = xValues;
    }

    public String getTitle()
    {
        return title;
    }

    public String getSeriesName()
    {
        return seriesName;
    }

    public String getxAxisLabel()
    {
        return xAxisLabel;
    }

    public void setxAxisLabel( String xAxisLabel )
    {
        this.xAxisLabel = xAxisLabel;
    }

    public double[] getYValues()
    {
        return yValues;
    }

    public double[] getxValues()
    {
        return xValues;
    }
}
