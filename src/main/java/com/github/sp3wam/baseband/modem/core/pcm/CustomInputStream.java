package com.github.sp3wam.baseband.modem.core.pcm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomInputStream extends InputStream
{
    private List< Byte > bytes = new ArrayList< Byte >();

    @Override
    public int read() throws IOException
    {
        Byte result = 0;

        synchronized( bytes )
        {
            if( bytes.size() == 0 )
            {
                return -1;
            }

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
        synchronized( bytes )
        {
            bytes.add( aByte );
        }
    }
}
