
import java.util.Random;
import java.util.Vector;
import java.lang.Math;

public class Solver {

   // Häufig verwendete Konstanten
   public static Complex   _0_ = new Complex(  0);
   public static Complex   _1_ = new Complex(  1);
   public static Complex   _2_ = new Complex(  2);
   public static Complex   _3_ = new Complex(  3);
   public static Complex   _4_ = new Complex(  4);

   public static Complex   _8_ = new Complex(  8);
   public static Complex   _9_ = new Complex(  9);
   public static Complex  _16_ = new Complex( 16);
   public static Complex  _64_ = new Complex( 64);
   public static Complex _256_ = new Complex(256);

   /*****************************************************************************/

   static Vector Cardanian_Formula(double r, double s, double t) {
      
      Vector vRes = new Vector();

      // Normalize the equation to obtain: x^3 + r*x^2 + s*x + t = 0.
      double p = s - r*r/3.0;
      double q = 2.0*r*r*r/27.0 - r*s/3.0 +t;

      // Reduzierte Funktion (Substitution x = y - r/3) => y^3 + p*y + q = 0
      double R = q*q/4.0+p*p*p/27.0;

      if (R >= 0)
      {
         // Cardanische Formel anwenden
         double T = Math.sqrt(R);
         double z = -q/2.0;
         double u = (z + T >= 0) ? Math.pow(z + T, 1.0/3.0) : -Math.pow(-z - T, 1.0/3.0);
         double v = (z - T >= 0) ? Math.pow(z - T, 1.0/3.0) : -Math.pow(-z + T, 1.0/3.0);

         vRes.addElement(new ComplexLin( (u+v)     - r/3.0, 0));
         vRes.addElement(new ComplexLin(-(u+v)/2.0 - r/3.0, -(u-v)/2.0*Math.sqrt(3.0)));
         vRes.addElement(new ComplexLin(-(u+v)/2.0 - r/3.0, +(u-v)/2.0*Math.sqrt(3.0)));
      }
      else
      {
         // 3 verschiedene reelle Lösungen
         double l = Math.sqrt(-p*p*p/27.0);
         double w = Math.acos(-q/(2.0*l));

         vRes.addElement(new Complex( 2*Math.pow(l, 1.0/3.0)*Math.cos(w/3 + 0*2*Math.PI/3.0) - r/3.0));
         vRes.addElement(new Complex( 2*Math.pow(l, 1.0/3.0)*Math.cos(w/3 + 1*2*Math.PI/3.0) - r/3.0));
         vRes.addElement(new Complex( 2*Math.pow(l, 1.0/3.0)*Math.cos(w/3 + 2*2*Math.PI/3.0) - r/3.0));
      }
      return vRes;
   }
   
   // *************************************************************************
   // Lösen der linearen Gleichung a*x + b = 0 -> Rückgabe der Lösung im Vektor
   // *************************************************************************
   public static Vector solveLinear(Complex a, Complex b) {
      
      Vector vRes = new Vector();

      // triviale Gleichung oder keine Lösung -> keine Lösungen zurückgeben
      if (a.isZero()) return vRes;

      if (b.isZero()) vRes.addElement(new Complex(0));
                 else vRes.addElement(Complex.div(b, a).neg());
      
      return vRes;
   }

   // ****************************************************************************************
   // Lösen der quadratischen Gleichung a*x^2 + b*x + c = 0 -> Rückgabe der Lösungen im Vektor
   // ****************************************************************************************
   public static Vector solveQuadratic(Complex a, Complex b, Complex c) {
      
      Vector vRes = new Vector();

      // Nur lineare Gleichung -> berechne Lösungen Extra
      if (a.isZero()) return solveLinear(b, c);

      if (c.isZero()) {
         if (b.isZero()) {
            // a*x^2 = 0
            vRes.addElement(new Complex(+0));
            vRes.addElement(new Complex(-0));
         } else {
            // a*x^2 + b*x = 0;
            vRes.addElement(new Complex(0));
            vRes.addAll(solveLinear(a, b));
         }
      } else {
         // a*x^2 + b*x + c = 0;
         Complex p = Complex.div(b, a).div(new Complex(2)).neg();
         Complex q = Complex.sqr(p).sub(Complex.div(c, a)).sqrt();

         vRes.addElement(Complex.add(p, q));
         vRes.addElement(Complex.sub(p, q));
      }
      return vRes;
   }

   // *********************************************************************************************
   // Lösen der kubischen Gleichung a*x^3 + b*x^2 + c*x + d = 0 -> Rückgabe der Lösungen im Vektor
   // *********************************************************************************************
   public static Vector solveCubic(Complex a, Complex b, Complex c, Complex d) {

      Vector vRes = new Vector();

      // Nur quadratische Gleichung -> berechne Lösungen Extra
      if (a.isZero()) return solveQuadratic(b, c, d);

      if (d.isZero()) {
         if (c.isZero()) {
            if (b.isZero()) {
               // a*x^3 = 0
               vRes.addElement(new Complex(0));
               vRes.addElement(new Complex(0));
               vRes.addElement(new Complex(0));
            } else {
               // a*x^3 + b*x^2 = 0
               vRes.addElement(new Complex(0));
               vRes.addElement(new Complex(0));
               vRes.addAll(solveLinear(a, b));
            }
         } else {
            // a*x^3 + b*x^2 + c*x = 0
            vRes.addElement(new Complex(0));
            vRes.addAll(solveQuadratic(a, b, c));
         }
      } else {
         // a*x^3 + b*x^2 + c*x + d = 0      =>    x^3 + p*x^2 + q*x + r = 0
         Complex p = Complex.div(b, a);
         Complex q = Complex.div(c, a);
         Complex r = Complex.div(d, a);

         // ************* incl. from CGAL ****************
         // Check whether the equation has multiple roots.
         // If we write: 
         //  p(x) = x^3 + p*x^2 + q*x + r = 0
         //
         // Then:
         //  p'(x) = 3*x^2 + 2*p*x + q
         //
         // We know that there are multiple roots iff GCD(p(x), p'(x)) != 0.
         // In order to check the GCD, let us denote:
         //  p(x) mod p'(x) = A*x + B
         //
         // Then:
         Complex A = Complex.sub(q, Complex.sqr(p).div(_3_)).mul(_2_).div(_3_);
         Complex B = Complex.sub(r, Complex.mul(p, q).div(_9_));
 
         if (A.isZero()) {
            if (B.isZero()) {
               // In case A,B == 0, then p'(x) divides p(x).
               // This means the equation has one solution with multiplicity of 3.
               // We can obtain this root using the fact that -p is the sum of p(x)'s 
               // roots.
               vRes.addElement(Complex.div(p, _3_).neg());
               vRes.addElement(Complex.div(p, _3_).neg());
               vRes.addElement(Complex.div(p, _3_).neg());
               return vRes;
            }
         } else {
            // Check whether A*x + B divides p'(x).
            Complex x0 = Complex.div(B, A).neg();
            
            if ( Complex.mul(_2_, p).add(Complex.mul(_3_, x0)).mul(x0).add(q).isZero() ) {
               // Since GCD(p(x), p'(x)) = (x - x0), then x0 is a root of p(x) with
               // multiplicity 2. The other root is obtained from p.
               vRes.addElement(x0);
               vRes.addElement(x0);
               vRes.addElement(Complex.mul(_2_, x0).add(p).neg());
               return vRes;
            }
         }

         // Was passiert hier mit den imaginären Teilen???
         // Funktioniert eigentlich immer, solange man mit realen Zahlen arbeitet!
         return Cardanian_Formula(p.Re(), q.Re(), r.Re());
      }
      return vRes;
   }

   // *********************************************************************************************
   // Lösen der Gleichung a*x^4 + b*x^3 + c*x^2 + d*x + e = 0 -> Rückgabe der Lösungen im Vektor
   // *********************************************************************************************
   public static Vector solveQuartic(Complex a, Complex b, Complex c, Complex d, Complex e) {

      Vector vRes = new Vector();

      // Nur quadratische Gleichung -> berechne Lösungen Extra
      if (a.isZero()) return solveCubic(b, c, d, e);

      if (e.isZero()) {
         if (d.isZero()) {
            if (c.isZero()) {
               if (b.isZero()) {
                  // a*x^4 = 0
                  vRes.addElement(new Complex(0));
                  vRes.addElement(new Complex(0));
                  vRes.addElement(new Complex(0));
                  vRes.addElement(new Complex(0));
               } else {
                  // a*x^4 + b*x^3 = 0
                  vRes.addElement(new Complex(0));
                  vRes.addElement(new Complex(0));
                  vRes.addElement(new Complex(0));
                  vRes.addAll(solveLinear(a, b));
               }
            } else {
               // a*x^4 + b*x^3 + c*x^2 = 0
               vRes.addElement(new Complex(0));
               vRes.addElement(new Complex(0));
               vRes.addAll(solveQuadratic(a, b, c));
            }
         } else {
            // a*x^4 + b*x^3 + c*x^2 + d*x = 0
            vRes.addElement(new Complex(0));
            vRes.addAll(solveCubic(a, b, c, d));
         }
      } else {
         // a*x^4 + b*x^3 + c*x^2 + d*x + e = 0      =>    x^4 + p*x^3 + q*x^2 + r*x + s = 0
         Complex p = Complex.div(b, a);
         Complex q = Complex.div(c, a);
         Complex r = Complex.div(d, a);
         Complex s = Complex.div(e, a);

         if (p.isZero() && r.isZero()) {
            // x^4 + q*x^2 + s = 0    =>   Substitution u = x^2
            Vector vRoots = solveQuadratic(_1_, q, s);
            
            for (int i=0; i<vRoots.size(); i++) {
                               Complex      cSol = (Complex)vRoots.elementAt(i);
               vRes.addElement(Complex.sqrt(cSol));                     // x1 = +sqrt(u)
               vRes.addElement(Complex.sqrt(cSol).neg());               // x2 = -sqrt(u)
            }
            return vRes;
         }

         // Entferne kubisches Element   =>   y = x - a/4    =>   y^4 + P*y^2 + Q*y + R = 0
         Complex P =    Complex.sub(q, Complex.sqr(p).mul(_3_).div(_8_));
         Complex Q =    Complex.mul(p, p).mul(p).div(_8_).sub( Complex.mul(p,q).div(_2_) ).add(r);
         Complex R =    Complex.sqr(p).sqr().mul(_3_);
                 R.sub( Complex.sqr(p).mul(q).mul(_16_) );
                 R.add( Complex.mul(p, r).mul(_64_) );
                 R.sub( Complex.mul(s, _256_) );
                 R.div( _256_ ).neg();

         Vector vSolve = solveCubic(_1_, 
                                    Complex.mul(P, _2_).neg(),
                                    Complex.sqr(P).sub( Complex.mul(_4_, R) ),
                                    Complex.sqr(Q));

         if (vSolve.size() != 3) {
            // Erwarten 3 Lösungen
            System.out.println("Fehlende Nullstellen: solveQuartic ("
                  +"("+ a.toStringLin() + ")*x^4 + "
                  +"("+ b.toStringLin() + ")*x^3 + "
                  +"("+ c.toStringLin() + ")*x^2 + "
                  +"("+ d.toStringLin() + ")*x^1 + "
                  +"("+ e.toStringLin() + ")");
            return vRes;
         }
         Complex z1 = (Complex)vSolve.elementAt(0);   z1.neg().sqrt();
         Complex z2 = (Complex)vSolve.elementAt(1);   z2.neg().sqrt();
         Complex z3 = (Complex)vSolve.elementAt(2);   z3.neg().sqrt();

         // Wähle Vorzeichen für's erste Element, so daß z1*z2*z3 = -Q
         if (!Complex.mul(z1, z2).mul(z3).add(Q).isZero()) z1.neg();
         
         z1.div(_2_);         z2.div(_2_);         z3.div(_2_);

         vRes.addElement( Complex.add(z1, z2).add(z3).sub(Complex.div(p, _4_)) );
         vRes.addElement( Complex.sub(z1, z2).sub(z3).sub(Complex.div(p, _4_)) );
         vRes.addElement( Complex.sub(z2, z3).sub(z1).sub(Complex.div(p, _4_)) );
         vRes.addElement( Complex.sub(z3, z2).sub(z1).sub(Complex.div(p, _4_)) );
      }
      return vRes;
   }

   public static void test4(Random rnd) {
      double a = rnd.nextDouble()*2.0 - 1.0;     a = ((double)((int)(a*100)))/100;
      double b = rnd.nextDouble()*2.0 - 1.0;     b = ((double)((int)(b*100)))/100;
      double c = rnd.nextDouble()*2.0 - 1.0;     c = ((double)((int)(c*100)))/100;
      double d = rnd.nextDouble()*2.0 - 1.0;     d = ((double)((int)(d*100)))/100;
      double e = rnd.nextDouble()*2.0 - 1.0;     e = ((double)((int)(e*100)))/100;

      int                                                  iExpected = 4;
           if (a == 0.0 && b == 0.0 && c == 0.0 && d == 0) iExpected = 0;
      else if (a == 0.0 && b == 0.0 && c == 0.0          ) iExpected = 1;
      else if (a == 0.0 && b == 0.0                      ) iExpected = 2;
      else if (a == 0.0                                  ) iExpected = 3;
      else                                                 iExpected = 4;

      Vector vSolve = solveQuartic(new Complex(a), new Complex(b), new Complex(c), new Complex(d), new Complex(e));
      if (vSolve.size() != iExpected) {
         System.out.println("Falsche Lösungszahl: "+ vSolve.size() +" statt "+ iExpected +" erwartet!");
         System.out.println("("+a+")*x^4 + "
                           +"("+b+")*x^3 + "
                           +"("+c+")*x^2 + "
                           +"("+d+")*x + "
                           +"("+e+")");
         return;
      }
      // Test
      boolean bFailure = false;
      for (int i=0; i<iExpected; i++) {
         Complex x = (Complex)vSolve.elementAt(i);

         Complex f =    Complex.sqr(x).sqr() .mul(   new Complex(a));
                 f.add( Complex.sqr(x).mul(x).mul(   new Complex(b)));
                 f.add( Complex.sqr(x)       .mul(   new Complex(c)));
                 f.add( Complex              .mul(x, new Complex(d)));
                 f.add(                              new Complex(e));

         if (!f.isZero()) {
            System.out.println("Fehler: "+f.toStringLin()+" statt 0.0!!!");
            System.out.println("("+a+")*x^4 + "
                              +"("+b+")*x^3 + "
                              +"("+c+")*x^2 + "
                              +"("+d+")*x + "
                              +"("+e+") => x = "+x.toStringLin());
            bFailure = true;
         }
      }
      if (!bFailure) System.out.println("Test passed!");
   }

   public static void test3(Random rnd) {
      double a = rnd.nextDouble()*2.0 - 1.0;     a = ((double)((int)(a*10)))/10;
      double b = rnd.nextDouble()*2.0 - 1.0;     b = ((double)((int)(b*10)))/10;
      double c = rnd.nextDouble()*2.0 - 1.0;     c = ((double)((int)(c*10)))/10;
      double d = rnd.nextDouble()*2.0 - 1.0;     d = ((double)((int)(d*10)))/10;

      int                                                  iExpected = 4;
           if (a == 0.0 && b == 0.0 && c == 0.0) iExpected = 0;
           if (a == 0.0 && b == 0.0            ) iExpected = 1;
      else if (a == 0.0                        ) iExpected = 2;
      else                                       iExpected = 3;

      Vector vSolve = solveCubic(new Complex(a), new Complex(b), new Complex(c), new Complex(d));
      if (vSolve.size() != iExpected) {
         System.out.println("Falsche Lösungszahl: "+ vSolve.size() +" statt "+ iExpected +" erwartet!");
         System.out.println("("+a+")*x^3 + "
                           +"("+b+")*x^2 + "
                           +"("+c+")*x + "
                           +"("+d+")");
         return;
      }
      // Test
      boolean bFailure = false;
      for (int i=0; i<iExpected; i++) {
         Complex x = (Complex)vSolve.elementAt(i);

         Complex f =    Complex.sqr(x).mul(x).mul(   new Complex(a));
                 f.add( Complex.sqr(x)       .mul(   new Complex(b)));
                 f.add( Complex              .mul(x, new Complex(c)));
                 f.add(                              new Complex(d));

         if (!f.isZero()) {
            System.out.println("Fehler: "+f.toStringLin()+" statt 0.0!!!");
            System.out.println("("+a+")*x^3 + "
                              +"("+b+")*x^2 + "
                              +"("+c+")*x + "
                              +"("+d+") => x = "+x.toStringLin());
            bFailure = true;
         }
      }
      if (!bFailure) System.out.println("Test passed!");
   }
}

