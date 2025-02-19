package com.github.sp3wam.baseband.modem.core;

public class GraphData
{

    private String title = null;
    private String seriesName = null;
    private String xAxisLabel = null;
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

    public String getxAxisLabel()
    {
        return xAxisLabel;
    }

    public void setxAxisLabel( String xAxisLabel )
    {
        this.xAxisLabel = xAxisLabel;
    }

    public double[] getValues()
    {
        return values;
    }
}
