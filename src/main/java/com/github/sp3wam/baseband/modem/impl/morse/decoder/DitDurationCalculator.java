package com.github.sp3wam.baseband.modem.impl.morse.decoder;

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
        if( segments.size() == 0 && segment.getType().equals( MorseSegmentType.Silence ) )
        {
            // Silence at the beginning is useless
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

        int minSignalDuration = Integer.MAX_VALUE;
        int maxSignalDuration = 0;
        int signalSumm = 0;
        int signalCount = 0;
        int silenceSumm = 0;
        int silenceCount = 0;
        MorseSegment lastSegment = segments.get( segments.size() - 1 );
        for( MorseSegment segment : segments )
        {
            if( segment.getType().equals( MorseSegmentType.Silence ) )
            {
                if( segment == lastSegment )
                {
                    // don't count silence at end
                    continue;
                }

                silenceCount++;
                silenceSumm += segment.getDuration();

                continue;
            }
            if( segment.getDuration() > maxSignalDuration )
            {
                maxSignalDuration = segment.getDuration();
            }
            if( segment.getDuration() < minSignalDuration )
            {
                minSignalDuration = segment.getDuration();
            }
            signalCount++;
            signalSumm += segment.getDuration();
        }

        if( signalCount < 2 )
        {
            // too few signals, can't calculate dit duration
            return;
        }

        double avgSignalDuration = ((double)signalSumm) / ((double)signalCount);

        if( maxSignalDuration / minSignalDuration >= 1.5 )
        {
            // there are DITs and DAHs
            double ditSumm = 0.0;
            int ditCount = 0;
            for( MorseSegment segment : segments )
            {
                if( segment.getDuration() < avgSignalDuration )
                {
                    // we have DIT
                    ditSumm += segment.getDuration();
                }
                else
                {
                    // we have DAH
                    ditSumm += (segment.getDuration() / 3.0);
                }

                ditCount++;
            }

            ditDuration = (int)(ditSumm / ditCount + 0.5);

            LOGGER.debug( String.format( "Calculated DIT duration is %s ticks", ditDuration ) );

            return;
        }

        // there are either DITs or DAHs
        if( silenceCount == 0 )
        {
            // special case: we have only a DIT or DAH available (E or T character)
            // not possible to find out between DITs or DAHs
            return;
        }

        double avgSilenceDuration = ((double)silenceSumm) / ((double)silenceCount);
        if( avgSignalDuration / avgSilenceDuration >= 1.5 )
        {
            // we have DAHs
            ditDuration = (int)(avgSignalDuration / 3.0 + 0.5);
        }
        else
        {
            // we have DITs
            ditDuration = (int)(avgSignalDuration + 0.5);
        }

        LOGGER.debug( String.format( "Calculated DIT duration is %s ticks", ditDuration ) );
    }
}
