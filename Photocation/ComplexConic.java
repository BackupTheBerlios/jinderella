/*
 * Created on 24.11.2003
 */

import java.util.Vector;

import mfc.field.Complex;

public class ComplexConic {
    private Vector fivePoints = new Vector();
    public ComplexMatrix conicMatrix = new ComplexMatrix();
    public double eps = 1E-40;

    /**
     * Constructor
     * @param p
     */
    public ComplexConic( Vector p ) {
        if ( p.size() != 5 ) ; // Fehler handling?!
        fivePoints = (Vector) p.clone();
        calcConic();
    }

    public ComplexConic( ComplexMatrix c ) {
        conicMatrix = c;
    }

    public Object clone() {
        return new ComplexConic( fivePoints );
    }

    public ComplexVector[] intersect( ComplexConic b ) {
        ComplexConic conic1, conic2;
        Complex factor1, factor2;
        Complex[] factors;

        if ( isDegenrated() && b.isDegenrated() ) {
            conic1 = this;
            conic2 = b;
        }
        else {

            ComplexMatrix m1, m2;

            m1 = b.conicMatrix;
            m2 = conicMatrix;

            /*
            boolean degenerated = false;

            if ( isDegenrated() ) {
                degenerated = true;
                m1 = conicMatrix;
                m2 = b.conicMatrix;
                conic2 = this;
            }
            else if ( b.isDegenrated() ) {
                degenerated = true;
                conic2 = b;
            }
            */

            factors = m1.detIsZero( m2 );

            // M�glichst verschiedene Faktoren nehemn
            if ( factors[0].equals( factors[1] , eps ) ) {
                factor1 = factors[1];
                factor2 = factors[2];
            }
            else if ( factors[1].equals( factors[2] , eps ) ) {
                factor1 = factors[0];
                factor2 = factors[1];
            }
            else {
                factor1 = factors[0];
                factor2 = factors[2];
            }

            conic1 = new ComplexConic( m1.plus( m2.times( factor1 ) ) );
            conic2 = new ComplexConic( m1.plus( m2.times( factor2 ) ) );

        }
        conic1.conicMatrix.normalize();
        conic2.conicMatrix.normalize();

        ComplexVector[] conicLines1 = conic1.extractLines();
        ComplexVector[] conicLines2 = conic2.extractLines();

        if ( conicLines1 != null && conicLines2 != null ) {
            ComplexVector[] result = new ComplexVector[4];
            result[0] = conicLines1[0].cross( conicLines2[0] );
            result[1] = conicLines1[1].cross( conicLines2[1] );
            result[2] = conicLines1[1].cross( conicLines2[0] );
            result[3] = conicLines1[0].cross( conicLines2[1] );
            return result;
        }

        return null;
    }


    public boolean isDegenrated() {
        return conicMatrix.isSingular();
    }

    public ComplexVector[] extractLines() {
        // ax^2 + by^2 + cxy + dx + ey + f = 0
        //      |  a  c' d' |
        //  m = |  c' b  e' |
        //      |  d' e' f  |
        if ( ! isDegenrated() ) return null;
        ComplexVector[] result = new ComplexVector[2];
        Complex a,b,c,d,e,f;
        a = conicMatrix.d[0][0];
        b = conicMatrix.d[1][1];
        f = conicMatrix.d[2][2];
        c = conicMatrix.d[0][1].times( 2 );
        d = conicMatrix.d[0][2].times( 2 );
        e = conicMatrix.d[1][2].times( 2 );
        return null;
    }

    /**
     * Berechnet eine Kegelschnitt-ComplexMatrix aus 5 Punkten
     */
    private void calcConic() {
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
        Complex mue = A.calcConic( p5 ).neg();

        // Kegelschbitt berechnen
        conicMatrix =  A.times( lamda ).plus( B.times( mue )  );
        conicMatrix.makeSymmetric();
        conicMatrix.normalize();
        //System.out.println( conicMatrix.toString() );
    }

    public String toString() {
        return conicMatrix.toString();
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
        } else {
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
        } else {
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
