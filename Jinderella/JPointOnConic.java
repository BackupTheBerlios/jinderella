
import java.awt.Color;

// Klasse zur Definition eines Punktes, der sich auf einer Gerade befindet
class JPointOnConic extends JPoint {

   // Kegelschnitt auf den der Punkt projiziert werden soll!
   JConic c;  

   // Konstruktor
   // -----------
   public JPointOnConic(JConic c, JPoint        pPos           ) { this(c, pPos.xyz, Color.white); }
   public JPointOnConic(JConic c, JPoint        pPos, Color clr) { this(c, pPos.xyz, clr        ); }
   public JPointOnConic(JConic c, JObjectVector oPos           ) { this(c, oPos    , Color.white); }
   public JPointOnConic(JConic c, JObjectVector oPos, Color clr) {
      super(clr);
      // Punkte in den Ausgangsort verschieben und Gerade registrieren
      this.xyz = oPos.copy();
      this.c   = c;                          
      // Koordinaten aktualisieren...
      doUpdate();
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return !c.isChanged();
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {

      this.xyz.toLevel();

      if (xyz.z.isZero()) {
         // Punkt im Unendlichen -> nicht ändern
         return;
      }
      // Dieser Punkt ist der Ausgangspunkt
      Complex P = xyz.x;
      Complex Q = xyz.y;

      // Einige Konstanten
      final Complex _0_ = new Complex(0);
      final Complex _1_ = new Complex(1);
      final Complex _2_ = new Complex(2);

      Complex A = c.M.a;              Complex B = Complex.add(c.M.b, c.M.d);   Complex D = Complex.add(c.M.c, c.M.g);
                                      Complex C =             c.M.e;           Complex E = Complex.add(c.M.f, c.M.h);
                                                                               Complex F =             c.M.i;

      // Suche Punkt auf dem Kegelschnitt, wo die Tangentennormale durch diesen Punkt zeigt
      // =====================================
      // x - P     Gradient_x     2Ax + By + D
      // -----  =  ----------  =  ------------     =>  TXX*x^2 + TXY*xy + TYY*y^2 + TXZ*x + TYZ*y + TZZ = 0    =>   Kegelschnitt
      // y - Q     Gradient_y     2Cy + Bx + E
      // =====================================
      Complex TXX = Complex.div(B,_2_);
      Complex TXY = Complex.sub(C, A );
      Complex TYY = Complex.div(B,_2_).neg();
      Complex TXZ = Complex.mul(A, Q ).sub( Complex.mul(B, P).div(_2_) ).add( Complex.div(E, _2_) );
      Complex TYZ = Complex.mul(C, P ).sub( Complex.mul(B, Q).div(_2_) ).add( Complex.div(D, _2_) ).neg();
      Complex TZZ = Complex.mul(D, Q ).sub( Complex.mul(E, P) ).div(_2_);

      // Constraint-Kegelschnitt
      JConic cConstraint = new JConicSimple( new JObjectMatrix( TXX, TXY, TXZ
                                                              , _0_, TYY, TYZ
                                                              , _0_, _0_, TZZ) );
      // Berechne die 4 in Frage kommenden Punkte
      JPoint p1 = new JPointConicConic(c, cConstraint, 0);
      JPoint p2 = new JPointConicConic(c, cConstraint, 1);
      JPoint p3 = new JPointConicConic(c, cConstraint, 2);
      JPoint p4 = new JPointConicConic(c, cConstraint, 3);

      // Suche den nächsten Punkt (real und nicht im Unendlichen
      JPoint  pNext = null;
      Complex dNext = null; Complex dist = null;

      if (!p1.xyz.z.isZero() && 
           p1.xyz.isReal()  ) dist = Complex.div(p1.xyz.x, p1.xyz.z).sub( this.xyz.x ).sqr().add( Complex.div(p1.xyz.y, p1.xyz.z).sub( this.xyz.y ).sqr() );
                         else dist = null;
      if ( dist  != null && 
          (dNext == null || dist.Len() < dNext.Len())) { dNext = dist; pNext = p1; }

      if (!p2.xyz.z.isZero() && 
           p2.xyz.isReal()  ) dist = Complex.div(p2.xyz.x, p2.xyz.z).sub( this.xyz.x ).sqr().add( Complex.div(p2.xyz.y, p2.xyz.z).sub( this.xyz.y ).sqr() );
                         else dist = null;
      if ( dist  != null && 
          (dNext == null || dist.Len() < dNext.Len())) { dNext = dist; pNext = p2; }

      if (!p3.xyz.z.isZero() && 
           p3.xyz.isReal()  ) dist = Complex.div(p3.xyz.x, p3.xyz.z).sub( this.xyz.x ).sqr().add( Complex.div(p3.xyz.y, p3.xyz.z).sub( this.xyz.y ).sqr() );
                         else dist = null;
      if ( dist  != null && 
          (dNext == null || dist.Len() < dNext.Len())) { dNext = dist; pNext = p3; }

      if (!p4.xyz.z.isZero() && 
           p4.xyz.isReal()  ) dist = Complex.div(p4.xyz.x, p4.xyz.z).sub( this.xyz.x ).sqr().add( Complex.div(p4.xyz.y, p4.xyz.z).sub( this.xyz.y ).sqr() );
                         else dist = null;
      if ( dist  != null && 
          (dNext == null || dist.Len() < dNext.Len())) { dNext = dist; pNext = p4; }

      if ( pNext == null) {
         // Alle Punkte lagen im Unendlichen oder sind imaginär! -> Was nun?! (Momentan: ungültig machen...)
           pNext = new JPointSimple(0,0,0);
      }

      moveTo(pNext);                                     // Punkt in die Zielkoordinaten verschieben
      // Werte nicht aus dem Ruder laufen lassen
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
      return 1;            // Punkt kann 1-dim. (auf dem Kegelschnitt) verschoben werden
   }
}
