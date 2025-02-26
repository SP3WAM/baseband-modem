package com.github.sp3wam.baseband.modem.impl.morse.detector;

import com.github.sp3wam.baseband.modem.core.SignalIf;

/***
 * Contains the information about Morse audible tone:
 * <ul>
 * <li>if frequency is 0 - means no audible signal available (noise)</li>
 * <li>if frequency different than 0 - means there is an audible tone available (dit or dah)</li>
 * </ul>
 */
class MorseToneSignal implements SignalIf
{
    private double toneFrequency;

    public MorseToneSignal( double frequency )
    {
        this.toneFrequency = frequency;
    }

    public double getToneFrequency()
    {
        return toneFrequency;
    }
}
