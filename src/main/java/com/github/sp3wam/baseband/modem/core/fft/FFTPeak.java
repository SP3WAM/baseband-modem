package com.github.sp3wam.baseband.modem.core.fft;

public class FFTPeak
{
    private double frequency;
    private double magnitude;
    private double powerPercentage;

    public FFTPeak( double frequency, double magnitude, double powerPercentage )
    {
        this.frequency = frequency;
        this.magnitude = magnitude;
        this.powerPercentage = powerPercentage;
    }

    public double getFrequency()
    {
        return frequency;
    }

    public double getMagnitude()
    {
        return magnitude;
    }

    public double getPowerPercentage()
    {
        return powerPercentage;
    }

    @Override
    public String toString()
    {
        return "FFTPeak [frequency=" + frequency + ", magnitude=" + magnitude + ", powerPercentage="
            + powerPercentage + "]";
    }

}
