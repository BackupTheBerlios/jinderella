
import java.awt.*;

// abstrakte Basisklasse von der alle Linienobjekte abgeleitet werden
abstract class JConic extends JObject {
   // Zählen der registrierten Punkte -> Namensgebung!
   static int regConics = 0;

   public void regConic() {
      // Nicht mehrfach benennen
      if (getName() != "") return;

                            regConics++;
      String  sName = "C" + regConics;
      setName(sName);
   }

   // Liniendaten speichern (ja nach Bildungsmodus)
   protected JObjectMatrix M;

   // Wenn Kurve degeneriert ist, enthalten die beiden folgenden Objekt die Geraden (ansonsten sind sie NULL)
   protected JLine lDeg1;
   protected JLine lDeg2;

   // ----------------------------------------

   // Konstruktor für alle Linien
   // --------------------------
   public JConic(Color c) {
      // Farbe speichern
      super(c);
      // Normalenobjekt niemals null werden lassen
      this.M = new JObjectMatrix( 0, 0, 0
                                , 0, 0, 0
                                , 0, 0, 0);
      // Geraden löschen
      lDeg1 = null;
      lDeg2 = null;
   }

   public void setTo(JObjectMatrix oTo) {
      // Die Normale anpassen
      M = oTo.copy();
      // Objekt wurde geändert!!!
      setChanged(true);
   }

   public void normalize() {
      // Die Werte nicht aus dem Ruder laufen lassen...
      M.toUnit();

      // Versuche lDeg1 und lDeg2 zu bestimmen
      handleLineCase();
   }

   public void handleLineCase() {
      // Erstmal testen, ob die Kurve überhaupt degeneriert ist! 
      Complex xx = M.a;    Complex xy = Complex.add(M.b, M.d);       Complex xz = Complex.add(M.c, M.g);
                           Complex yy =             M.e;             Complex yz = Complex.add(M.f, M.h);
                                                                     Complex zz =             M.i;
      
      // Degeneriert wenn gilt: 4 * xx*yy*zz - xx*yz^2 - xz^2*yy + xy*xz*yz - xy^2*zz == 0
      Complex cTest = new Complex(4);
              cTest.mul(Complex.mul(xx, yy).mul(zz));
              cTest.sub(Complex.mul(xx, yz).mul(yz));
              cTest.sub(Complex.mul(xz, xz).mul(yy));
              cTest.add(Complex.mul(xy, xz).mul(yz));
              cTest.sub(Complex.mul(xy, xy).mul(zz));
      
      if (   !cTest.isZero()  ) {
         // Kurve ist nicht degeneriert -> Geraden löschen
         lDeg1 = null;
         lDeg2 = null;
         return;
      }

      // Berechnung der bestimmenden Geraden
      // -----------------------------------
      if (M.a.isZero() &&
          M.e.isZero() &&
          M.i.isZero()) {
         
         // Kurve degeneriert mit der Form XY*x*y + XZ*x + YZ*y = 0  ->  (r1*x+q1) * (r2*y+q2)
                if (M.len().isZero()) {
            // Kurve ist ungültig -> Geraden löschen
            lDeg1 = null;
            lDeg2 = null;
            return;
         } else if (xy.isZero()) {
            // XZ*x + YZ*y = 0  ->  nur eine Gerade?!
            lDeg1 = new JLineSimple(          xz,           yz, Complex.Zero, Color.yellow); //getColor());
            lDeg2 = new JLineSimple(Complex.Zero, Complex.Zero, Complex.Zero, Color.yellow); //getColor());
            return;
         }

         if (yz.isZero()) {
            // Kurve: XY*x*y + XZ*x = 0 => x*(XY*y + XZ)
            lDeg1 = new JLineSimple(Complex.One , Complex.Zero, Complex.Zero, Color.yellow); //getColor());
            lDeg2 = new JLineSimple(Complex.Zero,           xy,           xz, Color.yellow); //getColor());
         } else {
            // Kurve: XY*x*y + YZ*y = 0 => y*(XY*x + YZ)
            lDeg1 = new JLineSimple(          xy, Complex.Zero,           yz, Color.yellow); //getColor());
            lDeg2 = new JLineSimple(Complex.Zero, Complex.One , Complex.Zero, Color.yellow); //getColor());
         }
         return;
      }

      // Suche Ansatzpunkt auf der Diagonale
      int iSpalte = 0;        if (!M.a.isZero()) iSpalte = 0;
                        else  if (!M.e.isZero()) iSpalte = 1;
                        else                     iSpalte = 2;

      JObjectMatrix N = M.copy();

             if (iSpalte == 0) {
         // Matrix ist in Ordnung
         N = M.copy();
      } else if (iSpalte == 1) {
         // Vertausche x und y in N
         N = new JObjectMatrix(M.e, M.d, M.f, 
                               M.b, M.a, M.c,
                               M.h, M.g, M.i);
      } else if (iSpalte == 2) {
         // Vertausche x und z in N
         N = new JObjectMatrix(M.i, M.h, M.g, 
                               M.f, M.e, M.d,
                               M.c, M.b, M.a);
      }

      // Bilde Gleichungsform x^2 + A*xy + B*x + C*y^2 + D*y + E (beachte evtl. obige Vertauschungen)
      N.div(N.a.copy());

      Complex A = Complex.add(N.b, N.d);
      Complex B = Complex.add(N.c, N.g);
      Complex C =             N.e.copy();
      Complex D = Complex.add(N.f, N.h);
      Complex E =             N.i.copy();

      Complex DetA = Complex.sub(Complex.div(Complex.sqr(A), new Complex(4)), C);
      Complex DetB = Complex.sub(Complex.div(Complex.sqr(B), new Complex(4)), E);

      Complex q1, r1;         // Erste  Gerade der Form x + q1*y + r1 
      Complex q2, r2;         // Zweite Gerade der Form x + q2*y + r1

      // 2-Möglichkeiten, je nach Wert der Determinante unter der Wurzel
      if (!DetA.isZero()) {
         // Nur ein Wert interessiert
         q1 = Complex.add(Complex.div(A, new Complex(2)), Complex.sqrt(DetA));
         q2 = Complex.sub(A, q1);

         r1 = Complex.neg(Complex.div(Complex.sub(D, Complex.mul(B, q1)), Complex.sub(q1, q2)));
         r2 = Complex.neg(Complex.div(Complex.sub(Complex.mul(B, q2), D), Complex.sub(q1, q2)));
      } else {
         // Nur ein Wert interessiert
         r1 = Complex.add(Complex.div(B, new Complex(2)), Complex.sqrt(DetB));
         r2 = Complex.sub(B, r1);

         q1 = Complex.neg(Complex.div(Complex.sub(D, Complex.mul(A, r1)), Complex.sub(r1, r2)));
         q2 = Complex.neg(Complex.div(Complex.sub(Complex.mul(A, r2), D), Complex.sub(r1, r2)));
      }

             if (iSpalte == 0) {
         lDeg1 = new JLineSimple(Complex.One, q1, r1, Color.yellow); //getColor());
         lDeg2 = new JLineSimple(Complex.One, q2, r2, Color.yellow); //getColor());
      } else if (iSpalte == 1) {
         lDeg1 = new JLineSimple(q1, Complex.One, r1, Color.yellow); //getColor());
         lDeg2 = new JLineSimple(q2, Complex.One, r2, Color.yellow); //getColor());
      } else {
         lDeg1 = new JLineSimple(r1, q1, Complex.One, Color.yellow); //getColor());
         lDeg2 = new JLineSimple(r2, q2, Complex.One, Color.yellow); //getColor());
      } 
   }

   // Berechne die Entfernung der Kurve zur aktuellen Canvas-Position!
   public Complex calcDistance(JMapping Map, JCanvasVector cPt) {
      // Der aktuelle Punkt wird in Objektkoordinaten umgerechnet
      JObjectVector oPt = Map.toObject(cPt);

      if (oPt == null || cPt == null) return null;

      JPoint pPtHere = new JPointSimple(oPt);
      JPoint pOnLine = new JPointOnConic(this, pPtHere);
      // pOnLine wurde nun auf den Kegelschnitt projiziert!

      return pOnLine.calcDistance(Map, cPt);
   }

   // --- Zeichnung des Objekts je nach Darstellungsmodus ---
   void draw(Graphics g, JMapping Mapping, Color cSelect) {
      // Zeichenfunktion der Anzeigefläche aufrufen
      Mapping.draw(g, this, cSelect);
   }
}

