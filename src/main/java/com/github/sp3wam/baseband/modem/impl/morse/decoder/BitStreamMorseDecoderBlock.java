package com.github.sp3wam.baseband.modem.impl.morse.decoder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sp3wam.baseband.modem.core.AbstractBlock;
import com.github.sp3wam.baseband.modem.core.SystemClock;
import com.github.sp3wam.baseband.modem.core.basic.blocks.BitSignal;

class BitStreamMorseDecoderBlock extends AbstractBlock< BitSignal, MorseSymbolsSignal >
{
    private Logger LOGGER = LoggerFactory.getLogger( BitStreamMorseDecoderBlock.class );

    private List< MorseSegment > segments = new ArrayList< MorseSegment >();
    private DitDurationCalculator ditDurationCalculator = new DitDurationCalculator();

    protected boolean execute0( SystemClock systemClock, BitSignal inputSignalValue )
    {
        LOGGER.debug( String.format( "Processing input value %s", inputSignalValue.toString() ) );

        if( segments.size() == 0 )
        {
            currentValue = null;

            if( !inputSignalValue.getBitValue() )
            {
                // leading silence is useless
                return false;
            }
        }

        // 1. check if DIT duration is available
        Integer ditDuration = ditDurationCalculator.getDitDuration();
        if( ditDuration == null )
        {
            appendBitSignalToSegments( inputSignalValue );

            return false;
        }

        // 2. check for MEDIUM_GAP at the end
        double minMediumgGapDuration = 5.0 * ditDuration;
        MorseSegment lastSegment = segments.get( segments.size() - 1 );

        if( lastSegment.getType().equals( MorseSegmentType.Silence )
            && lastSegment.getDuration() >= minMediumgGapDuration )
        {
            // there is a MEDIUM_GAP, decode the DITs and DAHs
            currentValue = detectSymbols( segments );
            currentValue.addSymbol( MorseSymbol.MEDIUM_GAP );
            segments.clear();

            appendBitSignalToSegments( inputSignalValue );

            return true;
        }

        // 3. check for SHORT_GAP at the end but only when a new input data is a signal
        double minShortGapDuration = 2.0 * ditDuration;
        if( lastSegment.getType().equals( MorseSegmentType.Silence )
            && lastSegment.getDuration() >= minShortGapDuration && inputSignalValue.getBitValue() )
        {
            // there is a SHORT_GAP, decode DITs and DAHs
            currentValue = detectSymbols( segments );
            currentValue.addSymbol( MorseSymbol.SHORT_GAP );
            segments.clear();

            appendBitSignalToSegments( inputSignalValue );

            return true;
        }

        // 4. still in phase of receiving input signal data
        appendBitSignalToSegments( inputSignalValue );

        return false;
    }

    private MorseSymbolsSignal detectSymbols( List< MorseSegment > segments )
    {
        int minSignalDuration = Integer.MAX_VALUE;
        int maxSignalDuration = 0;
        int signalSumm = 0;
        int signalCount = 0;
        int silenceSumm = 0;
        int silenceCount = 0;
        for( MorseSegment segment : segments )
        {
            if( segment.getType().equals( MorseSegmentType.Silence ) )
            {
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

        double avgSignalDuration = ((double)signalSumm) / ((double)signalCount);
        double avgSilenceDuration = ((double)silenceSumm) / ((double)silenceCount);

        if( maxSignalDuration / minSignalDuration >= 1.5 )
        {
            // there are DITs and DAHs
            MorseSymbolsSignal morseSymbolsSignal = new MorseSymbolsSignal();
            for( MorseSegment segment : segments )
            {
                if( segment.getType().equals( MorseSegmentType.Silence ) )
                {
                    continue;
                }

                if( segment.getDuration() < avgSignalDuration )
                {
                    // we have DIT
                    morseSymbolsSignal.addSymbol( MorseSymbol.DIT );
                }
                else
                {
                    // we have DAH
                    morseSymbolsSignal.addSymbol( MorseSymbol.DAH );
                }
            }

            LOGGER.debug( String.format( "Detected symbols %s", morseSymbolsSignal.toString() ) );

            return morseSymbolsSignal;
        }

        // there are either DITs or DAHs
        MorseSymbolsSignal morseSymbolsSignal = new MorseSymbolsSignal();
        for( int q = 0; q < signalCount; q++ )
        {
            if( avgSignalDuration / avgSilenceDuration >= 1.5 )
            {
                // we have DAHs
                morseSymbolsSignal.addSymbol( MorseSymbol.DAH );
            }
            else
            {
                // we have DITs
                morseSymbolsSignal.addSymbol( MorseSymbol.DIT );
            }
        }

        LOGGER.debug( String.format( "Detected symbols %s", morseSymbolsSignal.toString() ) );

        return morseSymbolsSignal;
    }

    private void appendBitSignalToSegments( BitSignal inputSignalValue )
    {
        MorseSegmentType inputSegmentType =
            inputSignalValue.getBitValue() == true ? MorseSegmentType.Signal : MorseSegmentType.Silence;

        if( segments.size() == 0 && inputSegmentType.equals( MorseSegmentType.Silence ) )
        {
            // leading silence is useless
            return;
        }

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

        // we have a new different segment...
        // ... update the dit duration calculator with last known segment
        ditDurationCalculator.addSegment( lastSegment );

        // input signal doesn't match the last segment; need to create a new segment
        MorseSegment newSegment = new MorseSegment( inputSegmentType );
        newSegment.increaseDuration();
        segments.add( newSegment );
    }
}
