package com.github.sp3wam.baseband.modem.impl.morse;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.BlockIf;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.blocks.ToneToBitConverterBlock;
import com.github.sp3wam.baseband.modem.core.signals.BitSignal;

public class BitStreamMorseDecoderBlock implements BlockIf< BitSignal, MorseSymbolSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( ToneToBitConverterBlock.class );

    private MorseSymbolSignal currentValue;
    private BlockIf< MorseSymbolSignal, ? > nextBlock;
    private List< MorseSegment > segments = new ArrayList< MorseSegment >();
    private DitDurationCalculator ditDurationCalculator = new DitDurationCalculator();

    @Override
    public void execute( SystemClock systemClock, BitSignal inputSignalValue )
    {
        boolean result = execute0( systemClock, inputSignalValue );

        if( nextBlock == null )
        {
            return;
        }

        if( result == false )
        {
            return;
        }

        nextBlock.execute( systemClock, currentValue );
    }

    @Override
    public void setNextBlock( BlockIf< MorseSymbolSignal, ? > nextBlock )
    {
        this.nextBlock = nextBlock;
    }

    @Override
    public MorseSymbolSignal getCurrentValue()
    {
        return currentValue;
    }

    protected boolean execute0( SystemClock systemClock, BitSignal inputSignalValue )
    {
        if( segments.size() == 0 )
        {
            currentValue = null;
        }

        // 1. append input signal to segments
        appendBitSignalToSegments( inputSignalValue );

        // 2. check if DIT duration is available
        if( ditDurationCalculator.getDitDuration() == null )
        {
            return false;
        }

        int ditDuration = ditDurationCalculator.getDitDuration();

        // 3. check for longer gap after sentence is over
        if( currentValue != null && segments.size() == 1
            && segments.get( 0 ).getType() == MorseSegmentType.Silence )
        {
            MorseSegment silenceSegment = segments.get( 0 );
            double maxMediumgGapDuration = 9.0 * ditDuration;
            if( silenceSegment.getDuration() >= maxMediumgGapDuration )
            {
                // end of sentence detected
                currentValue = new MorseSymbolSignal( MorseSymbol.MEDIUM_GAP );
                segments.clear();

                return true;
            }
        }

        // 4. to correctly decode the DIT, DAH or gaps we need at least two segments
        if( segments.size() < 2 )
        {
            return false;
        }

        // 5. first detect gaps
        MorseSegment first = segments.get( 0 );
        MorseSegment second = segments.get( 1 );

        if( first.getType() == MorseSegmentType.Silence && second.getType() == MorseSegmentType.Signal )
        {
            double minShortGapDuration = 2.0 * ditDuration;
            double maxShortGapDuration = 4.0 * ditDuration;
            double minMediumgGapDuration = 5.0 * ditDuration;
            double maxMediumgGapDuration = 9.0 * ditDuration;

            if( first.getDuration() < minShortGapDuration )
            {
                // inter-element gap (between the dits and dahs within a character ) detected
                segments.remove( 0 );
            }
            else if( first.getDuration() >= minShortGapDuration
                && first.getDuration() <= maxShortGapDuration )
            {
                // short gap detected (between letters)
                currentValue = new MorseSymbolSignal( MorseSymbol.SHORT_GAP );
                segments.remove( 0 );

                return true;
            }
            else if( first.getDuration() >= minMediumgGapDuration
                && first.getDuration() <= maxMediumgGapDuration )
            {
                // medium gap detected (between words)
                currentValue = new MorseSymbolSignal( MorseSymbol.MEDIUM_GAP );
                segments.remove( 0 );

                return true;
            }
            else if( first.getDuration() >= maxMediumgGapDuration )
            {
                // very long medium gap detected
                currentValue = new MorseSymbolSignal( MorseSymbol.MEDIUM_GAP );
                segments.remove( 0 );

                return true;
            }
        }

        // 6. to correctly decode the DIT or DAHwe need at least two segments
        if( segments.size() < 2 )
        {
            return false;
        }

        // 7. detect DIT or DAH
        first = segments.get( 0 );
        second = segments.get( 1 );
        if( first.getType() == MorseSegmentType.Signal && second.getType() == MorseSegmentType.Silence )
        {
            MorseSegment segment = segments.get( 0 );
            if( segment.getDuration() < 2.0 * ditDuration )
            {
                // we have a DIT
                currentValue = new MorseSymbolSignal( MorseSymbol.DIT );
            }
            else
            {
                // we have a DAT
                currentValue = new MorseSymbolSignal( MorseSymbol.DAH );
            }

            segments.remove( 0 );

            return true;
        }

        return false;
    }

    private void appendBitSignalToSegments( BitSignal inputSignalValue )
    {
        MorseSegmentType inputSegmentType =
            inputSignalValue.getBitValue() == true ? MorseSegmentType.Signal : MorseSegmentType.Silence;

        if( segments.size() == 0 )
        {
            MorseSegment segment = new MorseSegment( inputSegmentType );
            segment.increaseDuration();
            segments.add( segment );

            return;
        }

        MorseSegment lastSegment = segments.get( segments.size() - 1 );
        if( lastSegment.getType().equals( inputSegmentType ) )
        {
            // input signal matches the last segment
            lastSegment.increaseDuration();

            return;
        }

        if( inputSegmentType == MorseSegmentType.Silence )
        {
            // we have a new segment which is a type of Silence...
            // ... and the last known segment is a type of Signal
            // Let's update the dit duration calculator with last Signal segment
            ditDurationCalculator.addSegment( lastSegment );
        }

        // input signal doesn't match the last segment; need to create a new segment
        MorseSegment newSegment = new MorseSegment( inputSegmentType );
        newSegment.increaseDuration();
        segments.add( newSegment );
    }
}
