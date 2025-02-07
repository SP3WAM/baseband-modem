package com.github.sp3wam.baseband.modem.impl.morse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MorseTable
{
    private Logger LOGGER = LoggerFactory.getLogger( MorseTable.class );

    private final static String FILE = "src/main/resources/com/github/sp3wam/baseband/modem/impl/morse/morse_table.txt";

    private Map< String, String > table = new HashMap< String, String >();

    private MorseTable( String file )
    {
        super();

        readFromFile( file );
    }

    public static MorseTable readFromResources()
    {
        return new MorseTable( FILE );
    }

    public String decode( String ditDahSequence )
    {
        if( !table.containsValue( ditDahSequence ) )
        {
            return null;
        }

        for( String key : table.keySet() )
        {
            String value = table.get( key );
            if( value.equals( ditDahSequence ) )
            {
                return key;
            }
        }

        return null;
    }

    private void readFromFile( String file )
    {
        try
        {
            List< String > allLines = Files.readAllLines( Paths.get( file ) );

            for( String line : allLines )
            {
                String[] split = line.split( " " );
                if( split.length != 2 )
                {
                    // something wrong; throw an exception
                    throw new RuntimeException(
                        String.format( "Wrong format of morse table in line: %s", line ) );
                }

                String key = split[ 0 ].toLowerCase().trim();
                String value = split[ 1 ].trim();

                table.put( key, value );
            }
        }
        catch( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
}
