
import java.util.Vector;
import java.awt.Color;

// Klasse zur Definition eines Schnittpunktes zwischen zwei Geraden
class JPointConicCenter extends JPoint {

   // Die Kurve, die den Mittelpunkt definiert!
   JConic c;

   // Konstruktor für INTERSECT-Typ
   // -----------------------------
   public JPointConicCenter(JConic c           ) { this(c, Color.white); }
   public JPointConicCenter(JConic c, Color clr) {
      super(clr);
      // Die Daten speichern
      this.c      = c; 
      // Die Punktkoordinaten updaten...
      doUpdate();
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return !c.isChanged();
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {

      // Berechne Kurvengleichung
      Complex A = c.M.a;              Complex B = Complex.add(c.M.b, c.M.d);   Complex D = Complex.add(c.M.c, c.M.g);
                                      Complex C =             c.M.e;           Complex E = Complex.add(c.M.f, c.M.h);
                                                                               Complex F =             c.M.i;

      if (Complex.add(A, C).isZero()) {
         // Ich habe keine Vorstellung, was für Gebilde diese Konstellation besitzen -> ungültig
         setTo(new JObjectVector(0, 0, 0));
         return;
      }

      // Hier kommt die kritische Stelle, die die obige If-Abfrage motiviert!
      Complex cDet = Complex.div(B, Complex.add(A, C));

      // Berechne cos(Phi) und sin(Phi) um den Kegelschnitt so zu drehen, daß B' nachher 0 wird...
      Complex cPhi = Complex.add( Complex.sub(Complex.One, cDet).sqrt(), Complex.add(Complex.One, cDet).sqrt() ).div(new Complex(2));
      Complex sPhi = Complex.sub( Complex.sub(Complex.One, cDet).sqrt(), cPhi );                   ;

      // Drehe Kegelschnitt nach der Vorschrift ( x' = x*cos(Phi) - y*sin(Phi) und y' = x*sin(Phi) - y*cos(Phi) )
      Complex As =   Complex.mul(A, Complex.mul(cPhi,cPhi));
              As.add(Complex.mul(B, Complex.mul(cPhi,sPhi)));
              As.add(Complex.mul(C, Complex.mul(sPhi,sPhi)));

/* Diese Berechnung kann weggelassen werden (wenn kein Programmierfehler gemacht wurde) -> B' = 0
 * ----------------------------------------------------------------------------------------------
 *    Complex Bs =   Complex.mul(B, Complex.mul(cPhi,cPhi)).neg();
 *            Bs.sub(Complex.mul(A, Complex.mul(cPhi,sPhi)).mul(new Complex(2)));
 *            Bs.sub(Complex.mul(C, Complex.mul(cPhi,sPhi)).mul(new Complex(2)));
 *            Bs.sub(Complex.mul(B, Complex.mul(sPhi,sPhi)));
 * ----------------------------------------------------------------------------------------------
 */
      Complex Cs =   Complex.mul(C, Complex.mul(cPhi,cPhi));
              Cs.add(Complex.mul(B, Complex.mul(cPhi,sPhi)));
              Cs.add(Complex.mul(A, Complex.mul(sPhi,sPhi)));

      Complex Ds =   Complex.mul(D, cPhi);
              Ds.add(Complex.mul(E, sPhi));

      Complex Es =   Complex.mul(D, sPhi).neg();
              Es.sub(Complex.mul(E, cPhi));

      // Transliere entstandenen Kegelschnitt in den Ursprung
      Complex dc = Complex.neg(Ds).div(As).div(new Complex(2));
      Complex dd = Complex.neg(Es).div(Cs).div(new Complex(2));

      // Hier wird nur der Konstante Anteil verändert
      Complex Fs = F.copy();
              Fs.add(Complex.mul(dc, dc).mul(As));
              Fs.add(Complex.mul(dd, dd).mul(Cs));
              Fs.add(Complex.mul(dc, Ds));
              Fs.add(Complex.mul(dd, Es));

/* Diese Berechnungen können ebenfalls weggelassen werden -> D' = E' = 0
 * ----------------------------------------------------------------------------------------------
              Ds.add(Complex.mul(dd, Bs));
              Ds.add(Complex.mul(dc, As).mul(new Complex(2)));
              
              Es.add(Complex.mul(dc, Bs));
              Es.add(Complex.mul(dd, Cs).mul(new Complex(2)));
 * ----------------------------------------------------------------------------------------------
 */

      // Errechneter zentrierter Kegelschnitt hat die Gleichung (A' x^2 + C' y^2 + F' = 0)
      // ------------------------------------------------------------------------------------------
      // Dabei wurde zuerst eine Drehung um Phi und dann eine Translation um (dc, dd) durchgeführt!
      // ------------------------------------------------------------------------------------------
      // => Errechnen des Mittelpunktes durch Rücktransformation des Mittelpunktes (0, 0)

      Complex mx =   Complex.mul(cPhi, dc);
              mx.sub(Complex.mul(sPhi, dd));
      Complex my =   Complex.mul(sPhi, dc);
              my.sub(Complex.mul(cPhi, dd));

      setTo(new JObjectVector(mx, my, Complex.One));
      normalize();      
   };

   // --- Abhängigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return (obj == c) || 
           (bRec && 
             ( (c != null && c.dependsOn(obj, bRec)) ));
   }

   // Welchen Freiheitsgrad besitzt dieses Objekt (2-dim. / 1-dim oder 0-dim (fest definiert))
   int degreeOfFreedom() {
      return 0;            // Es gibt nur 1 Mittelpunkt!
   }
}
