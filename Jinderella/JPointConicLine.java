
import java.util.Vector;
import java.awt.Color;

// Klasse zur Definition eines Schnittpunktes zwischen zwei Geraden
class JPointConicLine extends JMultiPoint {

   // Die Geraden, die den Schnittpunkt definieren!
   JConic c;
   JLine  l;  

   // Konstruktor für INTERSECT-Typ
   // -----------------------------
   public JPointConicLine(JConic c, JLine l, int iIndex           ) { this(c, l, iIndex, Color.white); }
   public JPointConicLine(JConic c, JLine l, int iIndex, Color clr) {
      super(clr, iIndex);
      // Die Daten speichern
      this.c      = c; 
      this.l      = l;          
      // Die Punktkoordinaten updaten... (Starte mit (0,0,0) -> Index entscheidet)
      setTo( new JObjectVector(0,0,0) );
      doUpdate();
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return !c.isChanged() &&
             !l.isChanged();
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      
      // Key für die Memoization vorbereiten (evtl. noch c.M bzw. l.abc hinzunehmen)
      Vector jKey = new Vector();
             jKey.addElement(c);   if (c != null) jKey.addElement(c.M);      
             jKey.addElement(l);   if (l != null) jKey.addElement(l.abc);
      
      // Überprüfe, ob das Objekt bereits berechnet wurde?
      JPoint jMemo  = getMemo(jKey);
      if (   jMemo != null) {
         // Übernehme Koordinaten!
         moveTo(jMemo);
         normalize();
         return;
      }

      // Gerade im Unendlichen?
      if (l.abc.x.isZero() && l.abc.y.isZero()) {
         // Nur ungültig machen!
         setTo(new JObjectVector(0,0,0));
         return;
      }

      // Degenerierte Kurve?
      if (c.lDeg1 != null) {
         // Kurve ist degeneriert -> Nur die Schnittpunkte mit den Geraden entscheiden!
         Vector jArray = new Vector();
                jArray.addElement( new JPointIntersection(l, c.lDeg1) );
                jArray.addElement( new JPointIntersection(l, c.lDeg2) );

                setMemo(jKey, jArray);
         moveTo(getMemo(jKey));
         normalize();
         return;
      }

      // ******************************************************************************

      Complex xx = c.M.a;              Complex xy = Complex.add(c.M.b, c.M.d);   Complex xz = Complex.add(c.M.c, c.M.g);
                                       Complex yy =             c.M.e;           Complex yz = Complex.add(c.M.f, c.M.h);
                                                                                 Complex zz =             c.M.i;

      Complex  m = l.abc.x;            Complex  n = l.abc.y;                     Complex  o = l.abc.z;
      Complex mm = Complex.mul(m, m);  Complex mn = Complex.mul(m, n);           Complex mo = Complex.mul(m, o);
                                       Complex nn = Complex.mul(n, n);           Complex no = Complex.mul(n, o);
                                                                                 Complex oo = Complex.mul(o, o);
      
      // Definition der Variablen für die quadratische Gleichung (A*x^2 + B*x + C = 0)
      Complex A, B, C;

      if (m.isZero()) {
         // Sonderbehandlung für l.x == 0.0 (horizontale Gerade)
         A  =  Complex.mul(nn, xx);
         B  =  Complex.mul(nn, xz).sub(Complex.mul(no, xy));
         C  =  Complex.mul(nn, zz).sub(Complex.mul(no, yz)).add(Complex.mul(oo, yy));
      } else if (n.isZero()) {
         // Sonderbehandlung für l.y == 0.0 (vertikale Gerade)
         A  =  Complex.mul(mm, yy);
         B  =  Complex.mul(mm, yz).sub(Complex.mul(mo, xy));
         C  =  Complex.mul(mm, zz).sub(Complex.mul(mo, xz)).add(Complex.mul(oo, xx));
      } else {
         // sonstige Geraden
         A  =  Complex.mul(mm, yy).sub(Complex.mul(mn, xy)).add(Complex.mul(nn, xx));
         B  =  Complex.mul(mm, yz).sub(Complex.mul(mn, xz)).sub(Complex.mul(mo, xy)).add(Complex.mul(no, xx).mul(new Complex(2.0)));   
         C  =  Complex.mul(mm, zz).sub(Complex.mul(mo, xz)).add(Complex.mul(oo, xx));
      }

      Vector jArray = new Vector();

      if (A.isZero()) {
         
         // Variablen x und y vertauschen?
         if (m.isZero()) {
            // y-Wert berechnen (x-Wert ist: x = -C/B)
            Complex y = Complex.div(o, n).neg();

            jArray.addElement( new JPointSimple( Complex.div(C, B).neg(), y, Complex.One ) );
            jArray.addElement( new JPointSimple( Complex.Zero, Complex.Zero, Complex.Zero) );      // 2. Element ungültig
         } else if (n.isZero()) {
            // x-Wert berechnen (y-Wert ist: y = p +- q)
            Complex x = Complex.div(o, m).neg();

            jArray.addElement( new JPointSimple( x, Complex.div(C, B).neg(), Complex.One ) );
            jArray.addElement( new JPointSimple( Complex.Zero, Complex.Zero, Complex.Zero) );      // 2. Element ungültig
         } else {
            Complex y = Complex.div(C, B).neg();
            Complex x = Complex.mul(n, y).add(o).div(m).neg(); 

            jArray.addElement( new JPointSimple(            x,            y, Complex.One ) );
            jArray.addElement( new JPointSimple( Complex.Zero, Complex.Zero, Complex.Zero) );      // 2. Element ungültig
         }
      } else {
         Complex p =  Complex.div(B, A).div(new Complex(2)).neg();      // -B/(2A)
         Complex q =  Complex.sqr(p).sub(Complex.div(C, A)).sqrt();     // sqrt(B^2/(4A^2)-C/A)

         // Variablen x und y vertauschen?
         if (m.isZero()) {
            // y-Wert berechnen (x-Wert ist: x = p +- q)
            Complex y = Complex.div(o, n).neg();

            jArray.addElement( new JPointSimple( Complex.add(p, q), y, Complex.One) );
            jArray.addElement( new JPointSimple( Complex.sub(p, q), y, Complex.One) );
         } else if (n.isZero()) {
            // x-Wert berechnen (y-Wert ist: y = p +- q)
            Complex x = Complex.div(o, m).neg();

            jArray.addElement( new JPointSimple( x, Complex.add(p, q), Complex.One) );
            jArray.addElement( new JPointSimple( x, Complex.sub(p, q), Complex.One) );
         } else {
            Complex x, y;
            // 1. Ergebnis
            y = Complex.add(p, q);     x = Complex.mul(n, y).add(o).div(m).neg(); 
            jArray.addElement( new JPointSimple( x, y, Complex.One) );

            // 2. Ergebnis
            y = Complex.sub(p, q);     x = Complex.mul(n, y).add(o).div(m).neg(); 
            jArray.addElement( new JPointSimple( x, y, Complex.One) );
         }
      }
             setMemo(jKey, jArray);
      moveTo(getMemo(jKey));
      normalize();
      return;
   };

   // --- Abhängigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return (obj == c) || (obj == l) || 
           (bRec && 
             ( (c != null && c.dependsOn(obj, bRec)) || 
               (l != null && l.dependsOn(obj, bRec)) ));
   }
   // Welchen Freiheitsgrad besitzt dieses Objekt (2-dim. / 1-dim oder 0-dim (fest definiert))
   int degreeOfFreedom() {
      return 0;   // Punkt kann nicht verschoben werden!
   }

   // Zugriff auf den klassenabhängigen Key (sollte statisch zur Klasse definiert werden)
          Vector getKey()   { return jMemoKey;   }

   // Zugriff auf die klassenabhängigen MemoObjekte (sollten statisch zur Klasse definiert werden)
          Vector getArray() { return jMemoArray; }

   static Vector jMemoKey   = new Vector();        // Zeiger auf die Objekte die für die Erstellung des Memoization-Array verantwortlich waren!
   static Vector jMemoArray = new Vector();        // Die Objekte die erstellt wurden (oder leer)
}
