package com.github.sp3wam.baseband.modem.core;

public class SystemClock
{
    private long clockValue = 0;
    private long clockFrequency;

    public SystemClock( long clockFrequency )
    {
        this.clockFrequency = clockFrequency;
    }

    /***
     * Moves the clock one step forward.
     */
    public void step()
    {
        clockValue++;
    }

    public long getClockValue()
    {
        return clockValue;
    }

    public double getClockValueInSeconds()
    {
        return ((double)clockValue) / ((double)clockFrequency);
    }

    public long getClockFrequency()
    {
        return clockFrequency;
    }
}
