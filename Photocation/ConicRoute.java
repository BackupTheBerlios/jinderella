
import mfc.field.Complex;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Vector;

/**
 * User: mighty_j
 * Date: 05.01.2004
 * Time: 10:21:22
 * To change this template use Options | File Templates.
 */
public class ConicRoute {

    private FileWriter out = null;

    /**
     * Gibt einen Kegelschnitt als WayPoint+ Route aus
     * @param cConic
     */
    private void printRoute( ComplexConic cConic ) {
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
        int checkCoord = isFlat ? 1 : 0;
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
            if ( Math.abs( cPoint.d[checkCoord].im ) < eps ) {
                GPS pos = new GPS( cPoint.d[1].re, cPoint.d[0].re );
                try {
                    out.write( pos.toRoutePoint( !touched ) );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                touched = true;
            }
        }

        // touched = false ?? => Andere Hälfte des KS als extra Route?!

        for ( double p = end; p >= start; p = p - step ) {
            ComplexVector cPoint;
            cPoint = cConic.getPoint( new Complex( p ), true, isFlat ).toCanvas();
            if ( Math.abs( cPoint.d[checkCoord].im ) < eps ) {
                GPS pos = new GPS( cPoint.d[1].re, cPoint.d[0].re );
                try {
                    out.write( pos.toRoutePoint( !touched ) );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                touched = true;
            }
        }


    }


    public ConicRoute( String sFileName ) {
        File outputFile = new File( sFileName );
        try {
            out = new FileWriter( outputFile );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }


    public void writeRoute() {
        try {
            out.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void addRoute( Vector GPSPoints ) {
        if ( GPSPoints.size() != 5 ) return;
        Vector fivePoints = new Vector();
        for ( int i = 0; i < 5; i++ ) {
            GPS p = (GPS) ( GPSPoints.elementAt( i ) );
            fivePoints.add( new ComplexVector( new Complex( p.lon ), new Complex( p.lat ), new Complex( 1 ) ) );
        }
        ComplexConic cConic = new ComplexConic( fivePoints );
        printRoute( cConic );
    }

    /*
    public static void ConicRoute( String sFileName, GPS[] gpsPoints ) {

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

        System.out.println("Wrote "+sFileName+".");
    }
    */

}
