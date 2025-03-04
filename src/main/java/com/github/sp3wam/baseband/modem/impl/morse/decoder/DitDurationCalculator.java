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
    private MorseSegment lastAddedSegment = null;

    public void addSegment( MorseSegment segment )
    {
        if( segments.size() == 0 && segment.getType().equals( MorseSegmentType.Silence ) )
        {
            // Silence at the beginning is useless
            return;
        }

        if( segments.size() > 0 )
        {
            MorseSegmentType lastSegmentType = lastAddedSegment.getType();
            if( lastSegmentType.equals( segment.getType() ) )
            {
                throw new IllegalArgumentException( String.format(
                    "Can't add segment of type %s because the last segment is of the same type.",
                    segment.getType() ) );
            }
        }

        segments.add( segment );
        lastAddedSegment = segment;

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

        // 1. first pass: calculate coarse dit duration based on Signal segments only
        Double ditCoarseDuration = calculateDitCoarseDuration();

        if( ditCoarseDuration == null )
        {
            return;
        }

        // 2. second pass: remove too long silences
        double maxSilenceDuration = 1.5 * ditCoarseDuration;
        for( int q = 0; q < segments.size(); q++ )
        {
            if( MorseSegmentType.Signal.equals( segments.get( q ).getType() ) )
            {
                continue;
            }

            if( segments.get( q ).getDuration() > maxSilenceDuration )
            {
                segments.remove( q );
                q--;
            }
        }

        // 3. we need to have at least one Silence available
        if( segments.size() < 3 )
        {
            // at least Signal, Silence, Signal expected
            return;
        }

        boolean silencePresent = false;
        for( int q = 1; q < segments.size() - 1; q++ )
        {
            if( segments.get( q ).getType().equals( MorseSegmentType.Silence ) )
            {
                silencePresent = true;
                break;
            }
        }
        if( !silencePresent )
        {
            return;
        }

        // 4. third pass: calculate fine dit duration based on Signal segments and short duration Silence
        // segments
        ditDuration = (int)(calculateDitFineDuration() + 0.5);

        LOGGER.debug( String.format( "Calculated DIT duration is %s ticks", ditDuration ) );
    }

    /***
     * Calculates dit coarse duration based on Signal segments only; because signals must follow the time
     * restrictions of Morse signal, while silence can be indefinitely long (i.e. between words).
     * 
     * @return
     */
    private Double calculateDitCoarseDuration()
    {
        // 1. first pass: calculate average signal duration
        double summ = 0;
        int count = 0;

        for( MorseSegment segment : segments )
        {
            if( MorseSegmentType.Silence.equals( segment.getType() ) )
            {
                continue;
            }

            summ += segment.getDuration();
            count++;
        }

        if( count == 0 )
        {
            return null;
        }

        double avgSignalDuration = summ / ((double)count);

        // 2. second pass: collect dit and dah statistics
        double ditSumm = 0;
        int ditCount = 0;
        double dahSumm = 0;
        int dahCount = 0;
        for( MorseSegment segment : segments )
        {
            if( MorseSegmentType.Silence.equals( segment.getType() ) )
            {
                continue;
            }

            if( segment.getDuration() < avgSignalDuration )
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

        if( ditCount == 0 && dahCount == 0 )
        {
            return null;
        }

        if( ditCount == 0 || dahCount == 0 )
        {
            // unknown actually if we have dits or dahs
            // do an average
            double byDitDuration = (ditSumm + dahSumm) / (ditCount + dahCount);
            double byDahDuration = (ditSumm + dahSumm) / (ditCount + dahCount) / 3;

            return (byDitDuration + byDahDuration) / 2;
        }

        // 3. calculate dit coarse duration
        return ditSumm / ((double)ditCount) / 2.0 + dahSumm / ((double)dahCount) / 6.0;
    }

    private Double calculateDitFineDuration()
    {
        // 1. first pass: calculate average signal and silence duration
        // Assumption: we have only short silences available (with the duration around of dit)
        double summ = 0;
        int count = 0;

        for( MorseSegment segment : segments )
        {
            summ += segment.getDuration();
            count++;
        }

        if( count == 0 )
        {
            return null;
        }

        double avgDitDuration = summ / ((double)count);

        // 2. second pass: collect dit and dah statistics
        double ditSumm = 0;
        int ditCount = 0;
        double dahSumm = 0;
        int dahCount = 0;
        for( MorseSegment segment : segments )
        {
            if( segment.getDuration() < avgDitDuration )
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

        // 3. calculate dit duration
        double ditDuration = ditSumm / ((double)ditCount) / 2.0 + dahSumm / ((double)dahCount) / 6.0;

        return ditDuration;
    }

}
