
import java.util.regex.*;

/**
 * GPS Parser fuer berliner-stadtplan.com
 */
public class GPS {

    public double lat;
    public double lon;
    public boolean invalid;

    /**
     * Construtor erstellt GPS Koordinate aus DEG latitude und longitude
     * @param newlat
     * @param newlon
     */
    GPS( double newlat, double newlon ) {
        lat = newlat;
        lon = newlon;
        invalid = false;
    }

    /**
     * Parst einen String im Format "N52°40.55' E12°55.14'" wie bei berliner-stadtplan.com "gpswert"
     * @param s
     */
    GPS( String s ) {
        Pattern p = Pattern.compile( "^([NS])(\\d+)°(\\d+)\\.(\\d+)' ([WE])(\\d+)°(\\d+)\\.(\\d+)'.*$" );
        Matcher m = p.matcher( s );
        if ( m.find() ) {
            invalid = false;
            lat = (double) Integer.parseInt( m.group( 2 ) );
            lat += (double) Integer.parseInt( m.group( 3 ) ) / 60;
            lat += (double) Integer.parseInt( m.group( 4 ) ) / 3600;
            if ( m.group( 1 ).equals( "S" ) ) lat *= -1;

            lon = (double) Integer.parseInt( m.group( 6 ) );
            lon += (double) Integer.parseInt( m.group( 7 ) ) / 60;
            lon += (double) Integer.parseInt( m.group( 8 ) ) / 3600;
            if ( m.group( 5 ).equals( "W" ) ) lat *= -1;
            // System.out.println( s + " => " + lat + "," + lon );

        } else {
            invalid = true;
        }
    }

    /**
     * Liefert aus einer Grad Koordinate eine Zeit Koordinate
     * @param d
     * @return
     */
    private String toSingleString( double d ) {
        double deg, min, sec;
        deg = Math.floor( d );
        min = ( d - deg ) * 60;
        sec = ( min - Math.floor( min ) ) * 60;
        min = Math.floor( min );
        return (int) deg + "°" + (int) min + "." + (int) sec + "'";
    }

    /**
     * Liefert einen WayPoint+ Routenpunkt
     * @return
     */
    public String toRoutePoint() {
        return toRoutePoint( false );
    }

    /**
     * Liefert einen WayPoint+ Routenpunkt
     * @param start der route - boolean
     * @return
     */
    public String toRoutePoint( boolean start ) {
        return "RoutePoint,D," + lat + "," + lon + ",00/00/00,00:00:00," + ( start ? "1" : "0" ) + "\n";
    }

    /**
     * Liefert die GPS Koordinate als String
     * @return
     */
    public String toString() {
        if ( invalid ) return "invalid";
        return ( ( lat < 0 ) ? "S" : "N" ) + toSingleString( lat ) + " " +
                ( ( lon < 0 ) ? "W" : "E" ) + toSingleString( lon );
    }

}
