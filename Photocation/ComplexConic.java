/*
 * Created on 24.11.2003
 */

import java.util.Vector;

import mfc.field.Complex;

public class ComplexConic {
    protected Vector fivePoints = new Vector();
    protected ComplexMatrix conicMatrix = new ComplexMatrix();

    /**
     * Constructor
     * @param p
     */
    public ComplexConic( Vector p ) {
        if ( p.size() != 5 ) ; // Fehler handling?!
        fivePoints = (Vector) p.clone();
        calcConic();
    }

    /**
     * Berechnet eine Kegelschnitt-ComplexMatrix aus 5 Punkten
     */
    public void calcConic() {
        ComplexMatrix A, B;
        ComplexVector p1 = (ComplexVector) fivePoints.elementAt( 0 );
        ComplexVector p2 = (ComplexVector) fivePoints.elementAt( 1 );
        ComplexVector p3 = (ComplexVector) fivePoints.elementAt( 2 );
        ComplexVector p4 = (ComplexVector) fivePoints.elementAt( 3 );
        ComplexVector p5 = (ComplexVector) fivePoints.elementAt( 4 );

        // Degenerierter Kegelschnitt A
        ComplexVector p12 = p1.cross( p2 );
        ComplexVector p34 = p3.cross( p4 );
        A = new ComplexMatrix( p12, p34 );

        // Degenerierter Kegelschnitt B
        ComplexVector p13 = p1.cross( p3 );
        ComplexVector p24 = p2.cross( p4 );
        B = new ComplexMatrix( p13, p24 );

        // Auf p5 Strecken
        Complex lamda = B.calcConic( p5 );
        Complex mue = Complex.neg( A.calcConic( p5 ) );

        A.scalarMul( lamda );
        B.scalarMul( mue );

        // Kegelschbitt berechnen
        conicMatrix = ComplexMatrix.add( A, B );
        conicMatrix.makeSymmetric();
        System.out.println( conicMatrix );
        System.out.println( conicMatrix.toStringEQ() );
    }



    /**
     * Liefert die x Schnittpunkte der ComplexMatrix bei y zurueck
     * @param y
     * @param sign
     * @return
     */
    public ComplexVector getPoint( Complex y, boolean sign, boolean dir ) {
        // ax^2+by^2+cxy+dx+ey+f = 0 <=>
        // x_[1,2] = -(cy+d) +- ( (cy+d)^2 - 4a(by^2+ey+f) )^1/2   / 2a
        //      |  a  c' d' |
        //  m = |  c' b  e' |
        //      |  d' e' f  |
        Complex a,b,c,d,e,f,x;


        if ( dir ) {
            b = conicMatrix.d[0][0];
            a = conicMatrix.d[1][1];
            f = conicMatrix.d[2][2];

            c = conicMatrix.d[0][1].times( 2 );
            e = conicMatrix.d[0][2].times( 2 );
            d = conicMatrix.d[1][2].times( 2 );
        }
        else {
            a = conicMatrix.d[0][0];
            b = conicMatrix.d[1][1];
            f = conicMatrix.d[2][2];

            c = conicMatrix.d[0][1].times( 2 );
            d = conicMatrix.d[0][2].times( 2 );
            e = conicMatrix.d[1][2].times( 2 );
        }

        Complex a1, a2,a3;

        a1 = c.times( y ).plus( d );
        a2 = a.times( 4 );
        a3 = b.times( y.sqr() ).plus( e.times( y ) ).plus( f );

        a2 = a1.sqr().minus( a2.times( a3 ) ).sqrt();

        if ( sign ) {
            x = a1.neg().minus( a2 );
        } else {
            x = a1.neg().plus( a2 );
        }

        x = x.divide( a.times( 2 ) );

        if ( dir ) {
            return new ComplexVector( y, x, new Complex( 1 ) );
        }
        else {
            return new ComplexVector( x, y, new Complex( 1 ) );
        }

    }

    /**
     * Prueft ob der Kegelschnitt eine groessere y Steigung hat
     * @return
     */
    public boolean isFlat() {
        return Math.abs( conicMatrix.d[0][0].re ) < Math.abs( conicMatrix.d[1][1].re );
    }

}
