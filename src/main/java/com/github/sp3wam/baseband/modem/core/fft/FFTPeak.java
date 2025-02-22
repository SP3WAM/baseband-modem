package com.github.sp3wam.baseband.modem.core.fft;

public class FFTPeak
{
    private double frequency;
    private double magnitude;

    public FFTPeak( double frequency, double magnitude )
    {
        this.frequency = frequency;
        this.magnitude = magnitude;
    }

    public double getFrequency()
    {
        return frequency;
    }

    public double getMagnitude()
    {
        return magnitude;
    }
}
