
// Klasse zum Speichern von Objekt-Koordinaten
class JObjectVector {
   // Koordinatendaten
   Complex x, y, z;

   public JObjectVector(double  x, double  y, double  z) { this(new Complex(x), new Complex(y), new Complex(z)); }
   public JObjectVector(Complex x, Complex y, Complex z) {
      this.x = x.copy();
      this.y = y.copy();
      this.z = z.copy();
   }

   public JObjectVector copy() {
      // Kopie erstellen
      return new JObjectVector(x, y, z);
   }

   // Ist dies ein realer Punkt
   public boolean isReal() { return x.isReal() && y.isReal() && z.isReal(); }
   
   public JObjectVector onlyReal() { x = new ComplexLin(x.Re(), 0);
                                     y = new ComplexLin(y.Re(), 0);
                                     z = new ComplexLin(z.Re(), 0); return this; }
                                     
   // Vektoroperationen (Addition, Subtration, Negation und Skalierung) [Inplace]
   // -----------------------------------------------------------------------------
   public JObjectVector add(JObjectVector o) { x.add(o.x); y.add(o.y); z.add(o.z); return this; }
   public JObjectVector sub(JObjectVector o) { x.sub(o.x); y.sub(o.y); z.sub(o.z); return this; }
   public JObjectVector neg()                { x.neg();    y.neg();    z.neg();    return this; }

   public JObjectVector mul(Complex c)       { x.mul( c ); y.mul( c ); z.mul( c ); return this; }
   public JObjectVector div(Complex c)       { x.div( c ); y.div( c ); z.div( c ); return this; }

   // Vektoroperationen (Addition, Subtration, Negation und Skalierung) [Statisch]
   // -----------------------------------------------------------------------------
   public static JObjectVector add(JObjectVector o
                                  ,JObjectVector p) { return new JObjectVector(Complex.add(o.x, p.x), Complex.add(o.y, p.y), Complex.add(o.z, p.z)); }
   public static JObjectVector sub(JObjectVector o
                                  ,JObjectVector p) { return new JObjectVector(Complex.sub(o.x, p.x), Complex.sub(o.y, p.y), Complex.sub(o.z, p.z)); }
   
   public static JObjectVector neg(JObjectVector o) { return new JObjectVector(Complex.neg(o.x)     , Complex.neg(o.y)     , Complex.neg(o.z)     ); }

   public static JObjectVector mul(JObjectVector o
                                  ,Complex       c) { return new JObjectVector(Complex.mul(o.x,  c ), Complex.mul(o.y,  c ), Complex.mul(o.z,  c )); }
   public static JObjectVector div(JObjectVector o
                                  ,Complex       c) { return new JObjectVector(Complex.div(o.x,  c ), Complex.div(o.y,  c ), Complex.div(o.z,  c )); }

   // -------------------------------------------------------------------------
   // Funktion zur Berechnung des Skalarprodukts (Rückgabe Complex als Ergebnis)
   public Complex doScalar(JObjectVector b) {
      return Complex.add(Complex.add(Complex.mul(x, b.x), Complex.mul(y, b.y)), Complex.mul(z,b.z));
   }
   // Funktion zur Berechnung des Kreuzprodukts (Rückgabe JObjectVector-Objekt mit Ergebnis)
   public JObjectVector doCross(JObjectVector b) {
      return new JObjectVector(Complex.sub(Complex.mul(y, b.z), Complex.mul(z, b.y))
                              ,Complex.sub(Complex.mul(z, b.x), Complex.mul(x, b.z))
                              ,Complex.sub(Complex.mul(x, b.y), Complex.mul(y, b.x)));
   }

   // Statische Funktion zur Berechnung des Skalarprodukts (Rückgabe Complex als Ergebnis)
   public static Complex doScalar(JObjectVector a, JObjectVector b) {
      return Complex.add(Complex.add(Complex.mul(a.x, b.x), Complex.mul(a.y, b.y)), Complex.mul(a.z,b.z));
   }
   // Statische Funktion zur Berechnung des Kreuzprodukts (Rückgabe JObjectVector-Objekt mit Ergebnis)
   public static JObjectVector doCross(JObjectVector a, JObjectVector b) {
      return new JObjectVector(Complex.sub(Complex.mul(a.y, b.z), Complex.mul(a.z, b.y))
                              ,Complex.sub(Complex.mul(a.z, b.x), Complex.mul(a.x, b.z))
                              ,Complex.sub(Complex.mul(a.x, b.y), Complex.mul(a.y, b.x)));
   }

   // Kombiniere zwei Vektoren und einen Winkel so, daß alle Vektoren berechnet werden können,
   // die auf der Ellipse liegen, die durch die zwei Vektoren beschrieben wird.
   // -----------------------------------------------------------------------------
   public static JObjectVector combine(JObjectVector e1, JObjectVector e2, double dPhi) {
      return add( mul(e1, new Complex(Math.sin(dPhi))), 
                  mul(e2, new Complex(Math.cos(dPhi))) );
   }
   public static JObjectVector roundTo(JObjectVector v, int iFactor) {
      return new JObjectVector( Complex.roundTo(v.x, iFactor)
                              , Complex.roundTo(v.y, iFactor)
                              , Complex.roundTo(v.z, iFactor) );
   }

   // Länge des Vektors berechnen
   public Complex len() {
      return Complex.sqrt( Complex.add( Complex.add( Complex.sqr(x), Complex.sqr(y) ), Complex.sqr(z) ) );
   }

   // Abbilden der homogenen Koordinate auf die Ebene z = 1 (wenn möglich)
   // -----------------------------------------------------------------------------
   public void toLevel() {
      if (!z.isZero()) this.div(z);
   }

   // Abbilden der homogenen Koordinate auf die Einheitskugel x^2+y^2+z^2 = 1
   // -----------------------------------------------------------------------------
   public void toUnit() {
      Complex  l = len();
      if (    !l.isZero()) this.div(l);
   }

   // Funktionen zur Ausgabe des Vektors in der Form "(x, y, z)"
   public String toStringLin() {
      return "("+x.toStringLin()+", "+y.toStringLin()+", "+z.toStringLin()+")";
   }
   public String toStringExp() {
      return "("+x.toStringExp()+", "+y.toStringExp()+", "+z.toStringExp()+")";
   }
}

