
import mfc.field.Complex;

/*
 * Created on 24.11.2003
 */

public class ComplexMatrix {
    Complex d[][];
    int dim;

    /**
     * Default Constructor erstellt Matrix mit dim=3
     */
    public ComplexMatrix() {
        this(3);
    }


    /**
     * Constructor
     * @param dim
     */
    public ComplexMatrix( int dim ) {
        this.dim = dim;
        d = new Complex[dim][dim];
    }


    /**
     * Erstellt eine ComplexMatrix aus den ComplexVector p1 * p2^T
     * @param p1
     * @param p2
     */
    public ComplexMatrix( ComplexVector p1, ComplexVector p2 ) {
        if ( p1.dim != p2.dim ) return; // Fehlerhandling?!
        dim = p1.dim;
        d = new Complex[dim][dim];
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                d[i][j] = p1.d[i].times( p2.d[j] );
            }
        }
    }

    /**
     * Makes the Matrix symmetric
     */
    public void makeSymmetric() {
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = i; j < dim; j++ ) {
                System.out.println( i+","+j );
                d[j][i] = d[i][j] = d[i][j].plus( d[j][i] );
            }
        }
    }


    /**
     * Berechnet p^T * M * p mit M \in M^{3x3} und p \in V^3
     * @param p
     * @return
     */
    public Complex calcConic( ComplexVector p ) {
        Complex a1, a2, a3, res;
        a1 = d[0][0].times( p.d[0] ).plus( d[1][0].times( p.d[1] ) ).plus( d[2][0].times( p.d[2] ) );
        a2 = d[0][1].times( p.d[0] ).plus( d[1][1].times( p.d[1] ) ).plus( d[2][1].times( p.d[2] ) );
        a3 = d[0][2].times( p.d[0] ).plus( d[1][2].times( p.d[1] ) ).plus( d[2][2].times( p.d[2] ) );
        res = a1.times( p.d[0] ).plus( a2.times( p.d[1] ) ).plus( a3.times( p.d[2] ) );
        return res;
    }


    /**
     * Uberlandene Stringausgabe der ComplexMatrix
     */
    public String toString() {
        String out = new String( "" );
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                out = out + d[i][j] + "\t\t\t";
            }
            out = out + "\n";
        }
        return out;

    }

    /**
     * Uberlandene Stringausgabe der ComplexMatrix
     */
    public String toStringEQ() {
        // ax^2+by^2+2cxy+2dx+2ey+f = 0 <=>
        // x_[1,2] = -(cy+d) +- ( (cy+d)^2 - 4a(by^2+ey+f) )^1/2   / 2a
        //      |  a  c' d' |
        //  m = |  c' b  e' |
        //      |  d' e' f  |
        String out = new String( "" );
        out = d[0][0] + " x^2+  " + d[1][1] + " y^2+  ";
        out = out + "2 * " + d[0][1] + " xy+  ";
        out = out + "2 * " + d[0][2] + " x+  ";
        out = out + "2 * " + d[1][2] + " y+  ";
        out = out + d[2][2];
        return out;

    }

    /**
     * Transponiert die ComplexMatrix
     */
    public void transpose() {
        ComplexMatrix M = new ComplexMatrix();
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                M.d[i][j] = d[j][i];
            }
        }
        d = M.d;
    }

    /**
     * Skalare Multiplikation mit einer Komplexen Zahl
     */
    public void scalarMul( Complex lamda ) {
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                d[i][j] = d[i][j].times( lamda );
            }
        }
    }

    /**
     * Addition zweier Matrizen
     */
    public static ComplexMatrix add( ComplexMatrix A, ComplexMatrix B ) {
        ComplexMatrix C = new ComplexMatrix();
        if ( A.dim != B.dim ) return new ComplexMatrix( A.dim ); // Fehler handling?!

        for ( int i = 0; i < A.dim; i++ ) {
            for ( int j = 0; j < A.dim; j++ ) {
                C.d[i][j] = A.d[i][j].plus( B.d[i][j] );
            }
        }
        return C;
    }


}
