
import mfc.field.Complex;

import java.util.Vector;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The determination of a Photographer Location by Conic-Conic-Sections
 * Generates a WayPoint+ File of two Conics with Conics as Routes and Section as Waypoint
 */
public class Photocation {

    /**
     * Gibt einen Kegelschnitt als WayPoint+ Route aus
     * @param cConic
     */
    public static void printRoute( FileWriter out, ComplexConic cConic ) {
        // Eckpunkge von berliner-stadtplan.com Karte
        //GPS ul = new GPS( 52.57, 13.33 );
        //GPS lr = new GPS( 52.55, 13.36 );
        GPS ul = new GPS( "N52°40.55' E12°55.14'" ); // Etwa die Eckpunkte der Karte
        GPS lr = new GPS( "N52°19.73' E13°45.85'" );
        double startY = Math.min( lr.lat, ul.lat );
        double endY = Math.max( lr.lat, ul.lat );
        double startX = Math.min( lr.lon, ul.lon );
        double endX = Math.max( lr.lon, ul.lon );
        double eps = 4E-4;
        double step = 0.0001;
        boolean isFlat = cConic.isFlat();
        double start, end;
        int checkCoord = isFlat ? 0 : 1;
        boolean touched = false;

        if ( isFlat ) {
            start = startX;
            end = endX;
        } else {
            start = startY;
            end = endY;
        }
        // Abtasten erst in eine, dann die entgegengesetzte Richtung
        for ( double p = start; p <= end; p = p + step ) {
            ComplexVector cPoint;
            cPoint = cConic.getPoint( new Complex( p ), false, isFlat ).toCanvas();
            if ( Math.abs( cPoint.d[ 1-checkCoord ].im ) < eps ) {
                GPS pos = new GPS( cPoint.d[1].re, cPoint.d[0].re );
                try {
                    out.write( pos.toRoutePoint( ! touched ) );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                touched = true;
            }
        }


        for ( double p = end; p >= start; p = p - step ) {
            ComplexVector cPoint;
            cPoint = cConic.getPoint( new Complex( p ), true, isFlat ).toCanvas();
            if ( Math.abs( cPoint.d[ 1-checkCoord ].im ) < eps ) {
                GPS pos = new GPS( cPoint.d[1].re, cPoint.d[0].re );
                try {
                    out.write( pos.toRoutePoint( ! touched ) );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                touched = true;
            }
        }


    }


    public static void makeRoute( FileWriter out, GPS p[] ) {
        Vector fivePoints = new Vector();
        for ( int i = 0; i < 5; i++ ) {
            fivePoints.add( new ComplexVector( new Complex( p[i].lon ), new Complex( p[i].lat ), new Complex( 1 ) ) );
        }
        ComplexConic cConic = new ComplexConic( fivePoints );
        printRoute( out, cConic );
    }



    public static void createRouteFile( String sFileName ) {
        GPS p[] = new GPS[5];

        File outputFile = new File( sFileName );
        FileWriter out = null;
        try {
            out = new FileWriter(outputFile);
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        p[0] = new GPS( "N52°34.26' E13°20.34' (WGS84)" );
        p[1] = new GPS( "N52°33.93' E13°19.94' (WGS84)" );
        p[2] = new GPS( "N52°33.66' E13°20.65' (WGS84)" );
        p[3] = new GPS( "N52°33.85' E13°21.51' (WGS84)" );
        p[4] = new GPS( "N52°34.21' E13°21.67' (WGS84)" );

        makeRoute(out, p);

        p[0] = new GPS( 52.5527, 13.3473 );
        p[1] = new GPS( 52.5563, 13.3568 );
        p[2] = new GPS( 52.5628, 13.35 );
        p[3] = new GPS( 52.5603, 13.3373 );
        p[4] = new GPS( 52.5545, 13.34 );

        makeRoute(out, p);

        try {
            out.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        System.out.println("Wrote "+sFileName+".");
    }

    public static void main( String[] args ) {
        createRouteFile( "route.wp" );
    }
}

