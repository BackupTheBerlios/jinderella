
// Klasse zum Speichern von Canvas-Koordinaten
class JCanvasVector {
   // Koordinatendaten
   int x, y;

   public JCanvasVector(double x, double y) { this((int)x, (int)y); }
   public JCanvasVector(int    x, int    y) {
      this.x = x;
      this.y = y;
   }
   public JCanvasVector copy() {
      // Kopie erstellen
      return new JCanvasVector(x, y);
   }

   // Vektoroperationen (Addition, Subtration, Negation und Skalierung) [Inplace]
   // -----------------------------------------------------------------------------
   public JCanvasVector add(JCanvasVector c) { x += c.x; y += c.y; return this; }
   public JCanvasVector sub(JCanvasVector c) { x -= c.x; y -= c.y; return this; }
   public JCanvasVector neg()                { x  =  -x; y  =  -y; return this; }

   public JCanvasVector scale(double d)      { x *=   d; y *=   d; return this; }

   // Vektoroperationen (Addition, Subtration, Negation und Skalierung) [Statisch]
   // -----------------------------------------------------------------------------
   public static JCanvasVector add  (JCanvasVector c
                                    ,JCanvasVector d) { return new JCanvasVector( c.x + d.x, c.y + d.y); }
   public static JCanvasVector sub  (JCanvasVector c
                                    ,JCanvasVector d) { return new JCanvasVector( c.x - d.x, c.y - d.y); }
   
   public static JCanvasVector neg  (JCanvasVector c) { return new JCanvasVector(-c.x      ,-c.y      ); }

   public static JCanvasVector scale(JCanvasVector c
                                    ,double        d) { return new JCanvasVector( c.x * d  , c.y * d  ); }

   // Kombiniere zwei Vektoren und einen Winkel so, daß alle Vektoren berechnet werden können,
   // die auf der Ellipse liegen, die durch die zwei Vektoren beschrieben wird.
   // -----------------------------------------------------------------------------
   public static JCanvasVector combine(JCanvasVector e1, JCanvasVector e2, double dPhi) {
      return add( scale(e1, Math.sin(dPhi)), 
                  scale(e2, Math.cos(dPhi)) );
   }

   // Länge des Vektors berechnen
   public double len() {
      return Math.sqrt( x*x + y*y );
   }
}

