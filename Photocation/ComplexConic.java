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

    public ComplexConic() {
        conicMatrix = new ComplexMatrix();
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

            // Möglichst verschiedene Faktoren nehmen
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

    public ComplexVector[] intersectLine( ComplexVector l ) {
        ComplexVector[] result = new ComplexVector[]{ new ComplexVector(), new ComplexVector() };
        ComplexMatrix crossOp = new ComplexMatrix();
        crossOp.createCrossOperator(l);
        ComplexMatrix S = new ComplexMatrix(crossOp);
        S.transpose();
        S.assignTimes( S, conicMatrix );
        S.assignTimes( S, crossOp );
        S.makeRank1( crossOp );
        S.assignVecsFromRank1Matrix( result[0], result[1] );
        return result;
    }


    public static double crossRatio( double a, double b, double c, double d ) {
        if ( ( a - d ) * ( b -c ) == 0 ) return 0;
        return ( a - c ) * ( b - d ) / ( ( a - d ) * ( b -c ) );
    }

    public ComplexConic( ComplexVector A, ComplexVector B,ComplexVector C, ComplexVector D, double alphaE  ) {
        double Ax, Bx, Cx, Dx, Ay, By, Cy, Dy, Az, Bz, Cz, Dz;
        conicMatrix = new ComplexMatrix();
        // ax^2+by^2+2cxy+2dx+2ey+f = 0 <=>
        //      |  a  c' d' |
        //  m = |  c' b  e' |
        //      |  d' e' f  |


        Ax = A.d[0].re;
        Ay = A.d[1].re;
        Az = A.d[2].re;

        Bx = B.d[0].re;
        By = B.d[1].re;
        Bz = B.d[2].re;

        Cx = C.d[0].re;
        Cy = C.d[1].re;
        Cz = C.d[2].re;

        Dx = D.d[0].re;
        Dy = D.d[1].re;
        Dz = D.d[2].re;


    conicMatrix.d[2][2]= new Complex
     (Ay*(-(Bx*Cx*Dy) -
         By*Cx*Dx*(-1 + alphaE) +
         Bx*Cy*Dx*alphaE) +
      Ax*(-(Bx*Cy*Dy*
           (-1 + alphaE)) +
         By*
          (-(Cy*Dx) +
           Cx*Dy*alphaE)));

     conicMatrix.d[1][2]= new Complex
     (Az*
       (By*Cx*Dx*(-1 + alphaE) +
         Bx*(Cx*Dy - Cy*Dx*alphaE)
         ) +
      Ay*(Bz*Cx*Dx*
          (-1 + alphaE) +
         Bx*(Cx*Dz - Cz*Dx*alphaE)
         ) +
      Ax*(Bx*(Cz*Dy + Cy*Dz)*
          (-1 + alphaE) +
         Bz*
          (Cy*Dx - Cx*Dy*alphaE)
          + By*
          (Cz*Dx - Cx*Dz*alphaE)));

    conicMatrix.d[1][1] = new Complex
    (Az*(-(Bx*Cx*Dz) -
         Bz*Cx*Dx*(-1 + alphaE) +
         Bx*Cz*Dx*alphaE) +
      Ax*(-(Bx*Cz*Dz*
           (-1 + alphaE)) +
         Bz*
          (-(Cz*Dx) +
           Cx*Dz*alphaE)));


   conicMatrix.d[0][0] = new Complex
    (Az*(-(By*Cy*Dz) -
         Bz*Cy*Dy*(-1 + alphaE) +
         By*Cz*Dy*alphaE) +
      Ay*(-(By*Cz*Dz*
           (-1 + alphaE)) +
         Bz*
          (-(Cz*Dy) +
           Cy*Dz*alphaE)));


    conicMatrix.d[0][2] = new Complex
    (Ay*
          (Bz*Cx*Dy +
            Bx*Cz*Dy +
            By*
            (Cz*Dx + Cx*Dz)*
            (-1 + alphaE) -
            Bz*Cy*Dx*alphaE -
            Bx*Cy*Dz*alphaE) +
         Az*
          (Bx*Cy*Dy*
           (-1 + alphaE) +
            By*
            (Cy*Dx - Cx*Dy*alphaE)
            ) +
         Ax*
          (Bz*Cy*Dy*
           (-1 + alphaE) +
            By*
            (Cy*Dz - Cz*Dy*alphaE)
            ));

      conicMatrix.d[0][1] = new Complex
      (Az*
          (By*Cx*Dz +
            Bx*Cy*Dz +
            Bz*
            (Cy*Dx + Cx*Dy)*
            (-1 + alphaE) -
            By*Cz*Dx*alphaE -
            Bx*Cz*Dy*alphaE) +
         Ay*
          (Bx*Cz*Dz*
           (-1 + alphaE) +
            Bz*
            (Cz*Dx - Cx*Dz*alphaE)
            ) +
         Ax*
          (By*Cz*Dz*
           (-1 + alphaE) +
            Bz*
            (Cz*Dy - Cy*Dz*alphaE)
            ));

        conicMatrix.makeSymmetric();

    }




    public ComplexVector[] extractLines() {
        // ax^2 + by^2 + cxy + dx + ey + f = 0
        //      |  a  c' d' |
        //  m = |  c' b  e' |
        //      |  d' e' f  |
        if ( ! isDegenrated() ) return null;
        ComplexVector[] result = new ComplexVector[]{ new ComplexVector(), new ComplexVector() };
        ComplexVector cross1 = new ComplexVector();
        ComplexVector cross2 = new ComplexVector();
        ComplexVector cross = new ComplexVector();
        ComplexMatrix crossMatrix = new ComplexMatrix();
        cross1 = new ComplexVector( conicMatrix.d[0][1], conicMatrix.d[1][1], conicMatrix.d[2][1] );
        cross2 = new ComplexVector( conicMatrix.d[0][0], conicMatrix.d[1][0], conicMatrix.d[2][0] );
        cross = cross1.cross( cross2 );
        crossMatrix.createCrossOperator( cross );
        ComplexMatrix rank1 = new ComplexMatrix( conicMatrix );
        rank1.makeRank1( crossMatrix );
        rank1.assignVecsFromRank1Matrix( result[0], result[1] );
        return result;
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
