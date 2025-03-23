package com.github.sp3wam.baseband.modem.core.pcm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockingCustomInputStream extends InputStream
{
    private Logger LOGGER = LoggerFactory.getLogger( BlockingCustomInputStream.class );

    private List< Byte > bytes = new ArrayList< Byte >();
    private boolean closed = false;

    @Override
    public int read() throws IOException
    {
        while( bytes.size() == 0 )
        {
            // wait for some bytes
            if( closed )
            {
                return -1;
            }

            try
            {
                Thread.currentThread().sleep( 10 );
            }
            catch( InterruptedException e )
            {
                LOGGER.warn( e.getMessage(), e );
            }
        }

        Byte result = 0;
        synchronized( bytes )
        {
            result = bytes.get( 0 );
            bytes.remove( 0 );
        }

        return result;
    }

    public int available() throws IOException
    {
        synchronized( bytes )
        {
            return bytes.size();
        }
    }

    public void appendByte( byte aByte )
    {
        if( closed )
        {
            // stream is closed, don't accept any new data
            return;
        }

        synchronized( bytes )
        {
            bytes.add( aByte );
        }
    }

    public void close() throws IOException
    {
        closed = true;
    }
}
