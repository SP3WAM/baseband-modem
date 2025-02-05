package com.github.sp3wam.baseband.modem.impl.morse;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * From the stream of {@linkplain MorseSegment} objects calculates the current DIT duration.
 * 
 * @author wmarkowski
 */
class DitDurationCalculator
{
    private Logger LOGGER = LoggerFactory.getLogger( DitDurationCalculator.class );

    private final static int SEGMENTS_MAX_SIZE = 8;
    private List< MorseSegment > segments = new ArrayList<>();
    private Integer ditDuration;

    public void addSegment( MorseSegment segment )
    {
        if( segment.getType() == MorseSegmentType.Silence )
        {
            // only Signal segments are of interest
            return;
        }

        segments.add( segment );

        if( segments.size() > SEGMENTS_MAX_SIZE )
        {
            segments.remove( 0 );
        }

        calculateDitDuration();
    }

    /***
     * Get the current DIT duration in 'ticks' unit.
     * 
     * @return a dit duration in 'ticks' or null if duration is not yet known
     */
    public Integer getDitDuration()
    {
        return ditDuration;
    }

    private void calculateDitDuration()
    {
        if( segments.size() < 2 )
        {
            return;
        }

        // 1. first pass: calculate average signal duration
        double summ = 0;
        for( MorseSegment segment : segments )
        {
            summ += segment.getDuration();
        }

        double avgDuration = summ / ((double)segments.size());

        // 2. second pass: collect dit and dah statistics
        double ditSumm = 0;
        int ditCount = 0;
        double dahSumm = 0;
        int dahCount = 0;
        for( MorseSegment segment : segments )
        {
            if( segment.getDuration() < avgDuration )
            {
                // consider this segment as dit
                ditSumm += segment.getDuration();
                ditCount++;
            }
            else
            {
                // consider this segment as dah
                dahSumm += segment.getDuration();
                dahCount++;
            }
        }

        if( ditCount == 0 || dahCount == 0 )
        {
            // only dits (but no dahs) or dahs (but no dits) are present
            // can't calculate the dit duration
            return;
        }

        // 3. calculate dit duration
        ditDuration = (int)(ditSumm / ((double)ditCount) / 2.0 + dahSumm / ((double)dahCount) / 6.0);

        LOGGER.debug( String.format( "Calculated DIT duration is %s ticks", ditDuration ) );
    }
}
