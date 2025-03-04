package com.github.sp3wam.baseband.modem.impl.morse.decoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class DitDurationCalculatorTest
{

    private DitDurationCalculator subject;

    @Before
    public void init()
    {
        subject = new DitDurationCalculator();
    }

    @Test
    public void testGetDitDuration_forSilence()
    {
        subject.addSegment( new MorseSegment( MorseSegmentType.Silence, 20 ) );

        assertNull( subject.getDitDuration() );
    }

    @Test
    public void testGetDitDuration_forSignal()
    {
        subject.addSegment( new MorseSegment( MorseSegmentType.Signal, 20 ) );

        assertNull( subject.getDitDuration() );
    }

    @Test
    public void testGetDitDuration_forSilenceAndSignal()
    {
        subject.addSegment( new MorseSegment( MorseSegmentType.Silence, 10 ) );
        subject.addSegment( new MorseSegment( MorseSegmentType.Signal, 20 ) );

        assertNull( subject.getDitDuration() );
    }

    @Test
    public void testGetDitDuration_forSilenceAndSilence()
    {
        subject.addSegment( new MorseSegment( MorseSegmentType.Silence, 10 ) );
        subject.addSegment( new MorseSegment( MorseSegmentType.Silence, 20 ) );

        assertNull( subject.getDitDuration() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetDitDuration_forSignalAndSignal()
    {
        subject.addSegment( new MorseSegment( MorseSegmentType.Signal, 21 ) );
        subject.addSegment( new MorseSegment( MorseSegmentType.Signal, 24 ) );

        assertNull( subject.getDitDuration() );
    }

    @Test
    public void testGetDitDuration_forSignalAndSilence()
    {
        subject.addSegment( new MorseSegment( MorseSegmentType.Signal, 21 ) );
        subject.addSegment( new MorseSegment( MorseSegmentType.Silence, 24 ) );

        assertNull( subject.getDitDuration() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetDitDuration_forSignalAndSilenceAndSilence()
    {
        subject.addSegment( new MorseSegment( MorseSegmentType.Signal, 21 ) );
        subject.addSegment( new MorseSegment( MorseSegmentType.Silence, 7 ) );
        subject.addSegment( new MorseSegment( MorseSegmentType.Silence, 7 ) );

        assertNull( subject.getDitDuration() );
    }

    @Test
    public void testGetDitDuration_forSignalAndSilenceAndSignal_1()
    {
        subject.addSegment( new MorseSegment( MorseSegmentType.Signal, 12 ) );
        subject.addSegment( new MorseSegment( MorseSegmentType.Silence, 4 ) );
        subject.addSegment( new MorseSegment( MorseSegmentType.Signal, 12 ) );

        assertEquals( 4, subject.getDitDuration().intValue() );
    }
    
    @Test
    public void testGetDitDuration_forSignalAndSilenceAndSignal_2()
    {
        subject.addSegment( new MorseSegment( MorseSegmentType.Signal, 21 ) );
        subject.addSegment( new MorseSegment( MorseSegmentType.Silence, 4 ) );
        subject.addSegment( new MorseSegment( MorseSegmentType.Signal, 24 ) );

        assertEquals( 6, subject.getDitDuration().intValue() );
    }
}
