
import java.util.Vector;
import java.awt.Color;

// Klasse zur Definition eines Schnittpunktes zwischen zwei Geraden
class JPointConicConic extends JMultiPoint {

   // Die Kegelschnitte, die den Schnittpunkt definieren!
   JConic c1;
   JConic c2;  

   // Konstruktor für INTERSECT-Typ
   // -----------------------------
   public JPointConicConic(JConic c1, JConic c2, int iIndex           ) { this(c1, c2, iIndex, Color.white); }
   public JPointConicConic(JConic c1, JConic c2, int iIndex, Color clr) {
      super(clr, iIndex);
      // Die Daten speichern (versuchen, 2 Kegelschnitte immer in derselben Reihenfolge zu berechnen)
      this.c1     = (""+c1).compareTo(""+c2) < 0 ? c1 : c2; 
      this.c2     = (""+c1).compareTo(""+c2) < 0 ? c2 : c1;          
      // Die Punktkoordinaten updaten... (Starte mit (0,0,0) -> Index entscheidet)
      setTo( new JObjectVector(0,0,0) );
      doUpdate();
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return !c1.isChanged() &&
             !c2.isChanged();
   }

   private Vector buildKey() {
      Vector jKey = new Vector();
             jKey.addElement(c1);   if (c1 != null) jKey.addElement(c1.M);      
             jKey.addElement(c2);   if (c2 != null) jKey.addElement(c2.M);
      return jKey;
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      
      // Key für die Memoization vorbereiten (evtl. noch c1.M bzw. c2.M hinzunehmen)
      Vector jKey = buildKey();
      
      // Überprüfe, ob das Objekt bereits berechnet wurde?
      JPoint jMemo  = getMemo(jKey);
      if (   jMemo != null) {
         // Übernehme Koordinaten!
         moveTo(jMemo);
         normalize();
         return;
      }

      Vector jArray = new Vector();

      // Degenerierte Kurven abfangen
      if (c1.lDeg1 != null || c2.lDeg1 != null) {
         JConic cDeg = c1.lDeg1 != null ? c1 : c2;
         JConic cTwo = c1.lDeg1 != null ? c2 : c1;
         
         if (cTwo.lDeg1 != null) {
            // Beide Kegelschnitt degeneriert -> betrachte Geraden
            jArray.addElement(new JPointIntersection(cDeg.lDeg1, cTwo.lDeg1));
            jArray.addElement(new JPointIntersection(cDeg.lDeg1, cTwo.lDeg2));
            jArray.addElement(new JPointIntersection(cDeg.lDeg2, cTwo.lDeg1));
            jArray.addElement(new JPointIntersection(cDeg.lDeg2, cTwo.lDeg2));
         } else {
            // Nur ein degenerierter Kegelschnitt
            jArray.addElement(new JPointConicLine(cTwo, cDeg.lDeg1, 0));
            jArray.addElement(new JPointConicLine(cTwo, cDeg.lDeg1, 1));
            jArray.addElement(new JPointConicLine(cTwo, cDeg.lDeg2, 0));
            jArray.addElement(new JPointConicLine(cTwo, cDeg.lDeg2, 1));
         }
                setMemo(jKey, jArray);
         moveTo(getMemo(jKey));
         normalize();
         return;
      }

      // einige benötigte Konstanten
      final Complex _0_ = new Complex(0);
      final Complex _1_ = new Complex(1);
      final Complex _2_ = new Complex(2);

      Complex A1 = c1.M.a;              Complex B1 = Complex.add(c1.M.b, c1.M.d);   Complex D1 = Complex.add(c1.M.c, c1.M.g);
                                        Complex C1 =             c1.M.e;            Complex E1 = Complex.add(c1.M.f, c1.M.h);
                                                                                    Complex F1 =             c1.M.i;

      Complex A2 = c2.M.a;              Complex B2 = Complex.add(c2.M.b, c2.M.d);   Complex D2 = Complex.add(c2.M.c, c2.M.g);
                                        Complex C2 =             c2.M.e;            Complex E2 = Complex.add(c2.M.f, c2.M.h);
                                                                                    Complex F2 =             c2.M.i;

      // Bestimmung der Resolvente der beiden Kegelschnitte (Eliminierung von y) => TX4*x^4 + TX3*x^3 + TX2*x^2 + TX1*x + TX0 = 0
      // ------------------------------------------------------------------------------------------------------------------------
      Complex TX4 =   Complex.mul(A2, B1).mul(B2).mul(C1);
              TX4.sub(Complex.mul(A1, B2).mul(B2).mul(C1));
              TX4.sub(Complex.mul(A2, A2).mul(C1).mul(C1));
              TX4.sub(Complex.mul(A2, B1).mul(B1).mul(C2));
              TX4.add(Complex.mul(A1, B1).mul(B2).mul(C2));
              TX4.add(Complex.mul(A1, A2).mul(C1).mul(C2).mul(_2_));
              TX4.sub(Complex.mul(A1, A1).mul(C2).mul(C2));

      Complex TX3 =   Complex.mul(B1, B2).mul(C2).mul(D1);
              TX3.sub(Complex.mul(B2, B2).mul(C1).mul(D1));
              TX3.add(Complex.mul(A2, C1).mul(C2).mul(D1).mul(_2_));
              TX3.sub(Complex.mul(A1, C2).mul(C2).mul(D1).mul(_2_));
              TX3.add(Complex.mul(B1, B2).mul(C1).mul(D2));
              TX3.sub(Complex.mul(A2, C1).mul(C1).mul(D2).mul(_2_));
              TX3.sub(Complex.mul(B1, B1).mul(C2).mul(D2));
              TX3.add(Complex.mul(A1, C1).mul(C2).mul(D2).mul(_2_));
              TX3.add(Complex.mul(A2, B2).mul(C1).mul(E1));
              TX3.sub(Complex.mul(A2, B1).mul(C2).mul(E1).mul(_2_));
              TX3.add(Complex.mul(A1, B2).mul(C2).mul(E1));
              TX3.add(Complex.mul(A2, B1).mul(C1).mul(E2));
              TX3.sub(Complex.mul(A1, B2).mul(C1).mul(E2).mul(_2_));
              TX3.add(Complex.mul(A1, B1).mul(C2).mul(E2));

      Complex TX2 =   Complex.mul(B2, C2).mul(D1).mul(E1); 
              TX2.sub(Complex.mul(C2, C2).mul(D1).mul(D1));
              TX2.add(Complex.mul(C1, C2).mul(D1).mul(D2).mul(_2_));
              TX2.sub(Complex.mul(C1, C1).mul(D2).mul(D2));
              TX2.add(Complex.mul(B2, C1).mul(D2).mul(E1)); 
              TX2.sub(Complex.mul(B1, C2).mul(D2).mul(E1).mul(_2_));
              TX2.sub(Complex.mul(A2, C2).mul(E1).mul(E1));
              TX2.sub(Complex.mul(B2, C1).mul(D1).mul(E2).mul(_2_));
              TX2.add(Complex.mul(B1, C2).mul(D1).mul(E2)); 
              TX2.add(Complex.mul(B1, C1).mul(D2).mul(E2)); 
              TX2.add(Complex.mul(A2, C1).mul(E1).mul(E2)); 
              TX2.add(Complex.mul(A1, C2).mul(E1).mul(E2)); 
              TX2.sub(Complex.mul(A1, C1).mul(E2).mul(E2));
              TX2.sub(Complex.mul(B2, B2).mul(C1).mul(F1)); 
              TX2.add(Complex.mul(B1, B2).mul(C2).mul(F1)); 
              TX2.add(Complex.mul(A2, C1).mul(C2).mul(F1).mul(_2_));
              TX2.sub(Complex.mul(A1, C2).mul(C2).mul(F1).mul(_2_));
              TX2.add(Complex.mul(B1, B2).mul(C1).mul(F2)); 
              TX2.sub(Complex.mul(A2, C1).mul(C1).mul(F2).mul(_2_));
              TX2.sub(Complex.mul(B1, B1).mul(C2).mul(F2)); 
              TX2.add(Complex.mul(A1, C1).mul(C2).mul(F2).mul(_2_));

      Complex TX1 =   Complex.mul(C2, D1).mul(E1).mul(E2); 
              TX1.sub(Complex.mul(C2, D2).mul(E1).mul(E1)); 
              TX1.add(Complex.mul(C1, D2).mul(E1).mul(E2)); 
              TX1.sub(Complex.mul(C1, D1).mul(E2).mul(E2));
              TX1.sub(Complex.mul(C2, C2).mul(D1).mul(F1).mul(_2_));
              TX1.add(Complex.mul(C1, C2).mul(D2).mul(F1).mul(_2_));
              TX1.add(Complex.mul(B2, C2).mul(E1).mul(F1)); 
              TX1.sub(Complex.mul(B2, C1).mul(E2).mul(F1).mul(_2_));
              TX1.add(Complex.mul(B1, C2).mul(E2).mul(F1)); 
              TX1.add(Complex.mul(C1, C2).mul(D1).mul(F2).mul(_2_));
              TX1.sub(Complex.mul(C1, C1).mul(D2).mul(F2).mul(_2_));
              TX1.add(Complex.mul(B2, C1).mul(E1).mul(F2)); 
              TX1.sub(Complex.mul(B1, C2).mul(E1).mul(F2).mul(_2_));
              TX1.add(Complex.mul(B1, C1).mul(E2).mul(F2));

      Complex TX0 =   Complex.mul(C2, E1).mul(E2).mul(F1); 
              TX0.sub(Complex.mul(C1, E2).mul(E2).mul(F1)); 
              TX0.sub(Complex.mul(C2, C2).mul(F1).mul(F1)); 
              TX0.sub(Complex.mul(C2, E1).mul(E1).mul(F2)); 
              TX0.add(Complex.mul(C1, E1).mul(E2).mul(F2)); 
              TX0.add(Complex.mul(C1, C2).mul(F1).mul(F2).mul(_2_)); 
              TX0.sub(Complex.mul(C1, C1).mul(F2).mul(F2));

      // Bestimmung der Lösungen dieser Gleichung 4. Grades
      // --------------------------------------------------
      Vector vXs    = Solver.solveQuartic(TX4, TX3, TX2, TX1, TX0);

      for (int i=0; i<vXs.size(); i++) {
         Complex x = (Complex)vXs.elementAt(i);

         // Vertikale Gerade mit x = x_i
         JLine  lVer = new JLineSimple(new JObjectVector(_1_, _0_, Complex.neg(x)), Color.white);

         // Berechne die Schnittpunkte mit den beiden Kurve
         // -----------------------------------------------
         JPoint p1_1 = new JPointConicLine(c1, lVer, 0);
         JPoint p1_2 = new JPointConicLine(c1, lVer, 1);

         JPoint p2_1 = new JPointConicLine(c2, lVer, 0);
         JPoint p2_2 = new JPointConicLine(c2, lVer, 1);

         // Die 4 Punkte enthalten einen (manchmal mehrere entscheidende Punkte) -> ordentlich aussortieren
         analysePoints(jArray, p1_1, p1_2, p2_1, p2_2);
      }
             setMemo(jKey, jArray);
      moveTo(getMemo(jKey));
      normalize();
      return;
   };

   private void analysePoints(Vector jArray, JPoint p1_1, JPoint p1_2, JPoint p2_1, JPoint p2_2) {
      // 1 Punkt aus {p1_1, p1_2} muß mit 1 Punkt aus {p2_1, p2_2} identisch sein!!!
      boolean bNoDoubles = true;
      JPoint  pFound     = null;
      do {

         if ( pFound == null && p1_1.equals(p2_1)) {  
            // Die Kombination p1_1 = p2_1 scheint eine Kombination zu sein => falls gewünscht nachprüfen ob schon vorhanden
            if (!bNoDoubles || isNewPoint(jArray, p1_1)) pFound = new JPointSimple( p1_1.xyz );
         }
         if ( pFound == null && p1_1.equals(p2_2)) {  
            // Die Kombination p1_1 = p2_2 scheint eine Kombination zu sein => falls gewünscht nachprüfen ob schon vorhanden
            if (!bNoDoubles || isNewPoint(jArray, p1_1)) pFound = new JPointSimple( p1_1.xyz );
         }
         if ( pFound == null && p1_2.equals(p2_1)) {  
            // Die Kombination p1_2 = p2_1 scheint eine Kombination zu sein => falls gewünscht nachprüfen ob schon vorhanden
            if (!bNoDoubles || isNewPoint(jArray, p1_2)) pFound = new JPointSimple( p1_2.xyz );
         }
         if ( pFound == null && p1_2.equals(p2_2)) {  
            // Die Kombination p1_2 = p2_2 scheint eine Kombination zu sein => falls gewünscht nachprüfen ob schon vorhanden
            if (!bNoDoubles || isNewPoint(jArray, p1_2)) pFound = new JPointSimple( p1_2.xyz );
         }
         // Wurde nichts gefunden, dann die Beschränkung auf nicht doppelte aufheben
         if (pFound == null && !bNoDoubles) {
            // Bereits der 2. Durchgang, und immer noch nichts gefunden???
            System.out.println("Punktanalyse ohne Ergebnis!!!");
            return;
         }

         if   (pFound == null) bNoDoubles = false;
      } while (pFound == null);
      
      // gefundenes Element einfügen
      jArray.addElement(pFound);
   }

   private boolean isNewPoint(Vector jArray, JPoint p) {
      // Suche im Array, ob ein Punkt dem übergebenen Punkt entspricht
      for (int i=0; i<jArray.size(); i++) {
         JPoint q = (JPoint)jArray.elementAt(i);
         if (q.equals(p)) return false;
      }
      return true;
   }

   // --- Abhängigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return (obj == c1) || (obj == c2) || 
           (bRec && 
             ( (c1 != null && c1.dependsOn(obj, bRec)) || 
               (c2 != null && c2.dependsOn(obj, bRec)) ));
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
