package com.github.sp3wam.baseband.modem.impl.morse;

/***
 * Represents a Morse channel state which can be either a Signal (an audible tone signal) or a Silence (no
 * audible tone available). The segment has its duration expressed in 'ticks', where the tick is a duration of
 * channel sampling period. Example: a letter A will be expressed as two segments
 * <ul>
 * <li>Signal segment with the duration of X ticks</li>
 * <li>Silence segment with the duration of X ticks</li>
 * <li>Signal segment with the duration of 3X ticks</li>
 * <li>Silence segment with the duration of X ticks</li>
 * </ul>
 * The value of X variable (the duration in ticks) depends on:
 * <ul>
 * <li>channel sampling rate, i.e. 100 samples per second. The higher sampling rate, the duration value is
 * higher.</li>
 * <li>speed of Morse transmition in WPM (words per minute). The higher WPM value, the duration X is
 * smaller.</li>
 * </ul>
 * 
 * @see DitDurationCalculator
 */
class MorseSegment
{
    private MorseSegmentType type = null;
    private int duration = 0;

    public MorseSegment( MorseSegmentType type )
    {
        this.type = type;
    }

    public MorseSegmentType getType()
    {
        return type;
    }

    /***
     * Increases the duration by one 'tick'.
     */
    public void increaseDuration()
    {
        duration++;
    }

    /***
     * Get the segment duration in 'ticks'.
     * 
     * @return
     */
    public int getDuration()
    {
        return duration;
    }

    @Override
    public String toString()
    {
        return "MorseSegment [type=" + type + ", duration=" + duration + "]";
    }
}