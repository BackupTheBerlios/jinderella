import mfc.polynomial.ComplexPolynomial;
import mfc.field.Complex;

/*
 * Created on 24.11.2003
 */

public class ComplexMatrix {
    Complex d[][];
    int dim;
    public double eps = 1E-40;

    /**
     * Default Constructor erstellt Matrix mit dim=3.
     */
    public ComplexMatrix() {
        this( 3 );
    }

    public ComplexMatrix( ComplexMatrix M ) {
        dim = M.dim;
        d = new Complex[dim][dim];
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                d[i][j] = new Complex( M.d[i][j] );
            }
        }
    }

    /**
     * Constructor.
     * @param dim
     */
    public ComplexMatrix( int dim ) {
        this.dim = dim;
        d = new Complex[dim][dim];
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                d[i][j] = new Complex( 0 );
            }
        }

    }


    /**
     * Erstellt eine ComplexMatrix aus den ComplexVector p1 * p2^T.
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
                d[j][i] = d[i][j] = d[i][j].plus( d[j][i] );
            }
        }
    }

    /**
     * Liefert die Transformations Matrix Lp für das Kreuzprodukt von p
     * @param p
     */

    public void createCrossOperator( ComplexVector p ) {
        d[0][0] = new Complex(0);
        d[1][0] = p.d[2];
        d[2][0] = p.d[1].neg();

        d[0][1] = p.d[2].neg();
        d[1][1] = new Complex(0);
        d[2][1] = p.d[0];

        d[0][2] = p.d[1];
        d[1][2] = p.d[0].neg();
        d[2][2] = new Complex(0);
    }

    /**
     * Liefert ein Polynom der Funktion det( this + b*x ) zurück
     * @param b
     * @return
     */

    public ComplexPolynomial getDetPolynom( ComplexMatrix b ) {
        if ( dim != 3 || b.dim != 3 ) return new ComplexPolynomial();
        ComplexPolynomial pMatrix[][];
        pMatrix = new ComplexPolynomial[3][3];
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                Complex coef[] = new Complex[2];
                coef[0] = d[i][j];
                coef[1] = b.d[i][j];
                pMatrix[i][j] = new ComplexPolynomial( coef );
            }
        }
        ComplexPolynomial det = new ComplexPolynomial();
        det = pMatrix[0][0].times( pMatrix[1][1] ).times( pMatrix[2][2] ).plus(
                pMatrix[0][1].times( pMatrix[1][2] ).times( pMatrix[2][0] ).plus(
                        pMatrix[0][2].times( pMatrix[1][0] ).times( pMatrix[2][1] ).minus(
                                pMatrix[2][2].times( pMatrix[1][1] ).times( pMatrix[0][0] ).minus(
                                        pMatrix[2][1].times( pMatrix[1][2] ).times( pMatrix[0][0] ).minus(
                                                pMatrix[2][2].times( pMatrix[1][0] ).times( pMatrix[0][1] )
                                        )
                                )
                        )
                )
        );
        return det;
    }


    /**
     * Liefert die Nullstellen der Funktion det( this + b*x )
     * @param b
     * @return
     */
    public Complex[] detIsZero( ComplexMatrix b ) {
        return getDetPolynom( b ).getRoots();
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
     * Uberlandene Stringausgabe der ComplexMatrix.
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
     * Uberlandene Stringausgabe der ComplexMatrix.
     */
    public String toStringEQ() {
        // ax^2+by^2+2cxy+2dx+2ey+f = 0 <=>
        // x_[1,2] = -(cy+d) +- ( (cy+d)^2 - 4a(by^2+ey+f) )^1/2   / 2a
        //      |  a  c' d' |
        //  m = |  c' b  e' |
        //      |  d' e' f  |
        String out = new String();
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

    public ComplexMatrix times( Complex lamda ) {
        ComplexMatrix result = new ComplexMatrix(dim);
        result.assignTimes( this, lamda );
        return result;
    }

    /**
     * Skalare Multiplikation mit einer Komplexen Zahl
     */
    public void assignTimes( ComplexMatrix A, Complex lamda ) {
        ComplexMatrix tmp = new ComplexMatrix(A.dim);
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                tmp.d[i][j] = A.d[i][j].times( lamda );
            }
        }
        assign(tmp);
    }

    public ComplexMatrix times( ComplexMatrix A ) {
        ComplexMatrix result = new ComplexMatrix(dim);
        result.assignTimes( this, A );
        return result;
    }

    public void makeRank1( ComplexMatrix l ) {
        Complex tauh, tau;
        tau = d[0][0].times( d[1][1] );
        tauh = d[1][0].times( d[0][1] );
        tauh.assignMinus( tau );
        tauh.assignSqrt();
        tauh.assignDivide( l.d[0][1] );
        l = l.times( tauh );
        assignPlus( l );
    }

    public void assignVecsFromRank1Matrix( ComplexVector v1, ComplexVector v2 ) {
        double max = 0;
        int maxi = 0;
        int maxj = 0;
        for (int i = 0; i < d.length; i++) {
            Complex[] complexes = d[i];
            for (int j = 0; j < complexes.length; j++) {
                Complex complex = complexes[j];
                if ( max <= complex.abs() ) {
                    max = complex.abs();
                    maxi = i;
                    maxj = j;
                }
            }
        }
        v1.assign( d[maxi][0], d[maxi][1], d[maxi][2] );
        v2.assign( d[0][maxj], d[1][maxj], d[2][maxj] );
    }



    /**
     * Skalare Multiplikation mit einer Komplexen Zahl
     */
    public void assignTimes( ComplexMatrix A, ComplexMatrix B ) {
        ComplexMatrix tmp = new ComplexMatrix(3);
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                tmp.d[i][j] = new Complex(0);
                for ( int s = 0; s < dim; s++ ) {
                    tmp.d[i][j].assignPlus( A.d[i][s].times( B.d[s][j] ) );
                }
            }
        }
        assign(tmp);
    }

    private void assign(ComplexMatrix tmp) {
        for (int i = 0; i < d.length; i++) {
            Complex[] complexes = d[i];
            for (int j = 0; j < complexes.length; j++) {
                d[i][j].assign(tmp.d[i][j]);
            }
        }
    }

    /**
     * Liefert die Determinante einer 3x3 Matrix nach Cramer
     * dim <> 3 => fehlt :).
     * @return
     */
    public Complex det() {
        if ( dim == 3 ) {
            return d[0][0].times( d[1][1] ).times( d[2][2] ).plus(
                    d[0][1].times( d[1][2] ).times( d[2][0] ).plus(
                            d[0][2].times( d[1][0] ).times( d[2][1] ).minus(
                                    d[2][2].times( d[1][1] ).times( d[0][0] ).minus(
                                            d[2][1].times( d[1][2] ).times( d[0][0] ).minus(
                                                    d[2][2].times( d[1][0] ).times( d[0][1] )
                                            )
                                    )
                            )
                    )
            );


        } else {
            return new Complex( 0 );
        }
    }

    /**
     * Liefert ein boolean ob dcie Matrix singulär ist.
     * @return
     */
    public boolean isSingular() {
        return det().isZero();
    }


    public void normalize() {
        Complex sum = new Complex(0);
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                sum.assignPlus( d[i][j].sqr() );
            }
        }
        Complex lamda = new Complex(dim*dim).divide( sum.sqrt() );
        assignTimes(this,lamda);
    }

    /**
     * Addition zweier Matrizen.
     */
    public ComplexMatrix plus( ComplexMatrix A ) {
        ComplexMatrix C = new ComplexMatrix();
        if ( A.dim != dim ) return new ComplexMatrix( A.dim ); // Fehler handling?!

        for ( int i = 0; i < A.dim; i++ ) {
            for ( int j = 0; j < A.dim; j++ ) {
                C.d[i][j] = A.d[i][j].plus( d[i][j] );
            }
        }
        return C;
    }

    public void assignPlus( ComplexMatrix A ) {
        ComplexMatrix C = new ComplexMatrix();
        for ( int i = 0; i < A.dim; i++ ) {
            for ( int j = 0; j < A.dim; j++ ) {
                C.d[i][j] = A.d[i][j].plus( d[i][j] );
            }
        }
        assign(C);
    }


}
