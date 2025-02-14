package com.github.sp3wam.baseband.modem.core;

public class GraphData
{

    private String title;
    private String seriesName;
    private double[] values;

    public GraphData( String title, String seriesName, double[] values )
    {
        this.title = title;
        this.seriesName = seriesName;
        this.values = values;
    }

    public String getTitle()
    {
        return title;
    }

    public String getSeriesName()
    {
        return seriesName;
    }

    public double[] getValues()
    {
        return values;
    }
}
