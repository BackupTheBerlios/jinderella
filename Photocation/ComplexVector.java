
import mfc.field.Complex;

/**
 * User: mighty_j
 * Date: 30.12.2003
 * Time: 10:16:40
 * To change this template use Options | File Templates.
 */
public class ComplexVector {
    public Complex d[];
    public int dim;

    /**
     * Constructor erstellt Vektor mit dim=3 und Komponenten x,y,z
     * @param x
     * @param y
     * @param z
     */
    public ComplexVector( Complex x, Complex y, Complex z ) {
        this( 3 );
        d[0] = x;
        d[1] = y;
        d[2] = z;
    }

    /**
     * Constructor erstellt Vektor mit dim=2 und Komponenten x,y
     * @param x
     * @param y
     */
    public ComplexVector( Complex x, Complex y ) {
        this( 2 );
        d[0] = x;
        d[1] = y;
    }

    /**
     * Default Constructor erstellt Vektor mit dim=3
     */
    public ComplexVector() {
        this( 3 );
    }

    /**
     * Constructor
     * @param newdim
     */
    public ComplexVector( int newdim ) {
        dim = newdim;
        d = new Complex[dim];
    }

    /**
     * Kreuzprodukt
     * @param v
     * @return
     */
    public ComplexVector cross( ComplexVector v ) {
        return new ComplexVector(
                d[1].times( v.d[2] ).minus( d[2].times( v.d[1] ) ),
                d[2].times( v.d[0] ).minus( d[0].times( v.d[2] ) ),
                d[0].times( v.d[1] ).minus( d[1].times( v.d[0] ) )
        );
    }

    public ComplexVector times( ComplexMatrix A ) {
        ComplexVector res = new ComplexVector( dim );
        for ( int i = 0 ; i <= dim ; i++ ) {
            for ( int j = 0 ; i <= dim ; i++ ) {

            }
       }
        return res;
    }


    /**
     * Projeziert ein Vektor mit Homogenen Koordinaten
     * @return ComplexVector
     */
    public ComplexVector toCanvas() {
        return new ComplexVector(
                d[0].divide( d[2] ),
                d[1].divide( d[2] )
        );
    }

    /**
     * Ueberladene String umwandlung
     * @return String
     */
    public String toString() {
        String result = new String();
        result = "[ ";
        for ( int i = 0; i < dim; i++ ) {
            result = result + d[i];
            if ( i == dim - 1 ) {
                result = result + " ]";
            } else {
                result = result + ", ";
            }
        }
        return result;
    }

    public Complex norm() {
        Complex result = new Complex(0);
        for ( int i = 0; i < dim; i++ ) {
            result.assignPlus( d[i].sqr() );
        }
        return result.sqrt();
    }


}
