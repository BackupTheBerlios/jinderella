
import java.awt.*;

// Klasse zur Definition einer Gerade durch zwei Punkte
class JConicConnected extends JConic {

   // Die Punkte, die die Kurve definieren!
   JPoint p1, p2, p3, p4, p5; 


   // Konstruktor für CONNECTED-Typ
   // -----------------------------
   public JConicConnected(JPoint p1, JPoint p2, JPoint p3, JPoint p4, JPoint p5         ) { this(p1, p2, p3, p4, p5, Color.white); }
   public JConicConnected(JPoint p1, JPoint p2, JPoint p3, JPoint p4, JPoint p5, Color c) {
      super(c);
      // Die Punkte speichern
      this.p1 = p1; 
      this.p2 = p2;
      this.p3 = p3;
      this.p4 = p4;
      this.p5 = p5;
      // Die Normale updaten
      doUpdate();
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return !p1.isChanged() &&
             !p2.isChanged() &&
             !p3.isChanged() &&
             !p4.isChanged() &&
             !p5.isChanged();
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      // Matrizen berechnen
      JObjectMatrix oM1 = JObjectMatrix.mul(JObjectVector.doCross(p1.xyz, p2.xyz)
                                           ,JObjectVector.doCross(p3.xyz, p4.xyz));
      JObjectMatrix oM2 = JObjectMatrix.mul(JObjectVector.doCross(p1.xyz, p3.xyz)
                                           ,JObjectVector.doCross(p2.xyz, p4.xyz));

      // Faktoren bestimmen
      Complex cD1 = JObjectVector.doScalar(p5.xyz, oM2.mul(p5.xyz));
      Complex cD2 = JObjectVector.doScalar(p5.xyz, oM1.mul(p5.xyz));

      if (cD1.isZero() && cD2.isZero()) {
         // Es gibt nur 4 bestimmende Punkte -> ungültig
         setTo(new JObjectMatrix(0,0,0,0,0,0,0,0,0));
         normalize();                                    
         return;
      }
      // Matrizen skalieren
      oM1.mul(cD1);
      oM2.mul(cD2);
      
      // Neue Matrix setzen
      setTo(oM1.sub(oM2));
      // Werte nicht aus dem Ruder laufen lassen
      normalize();
   };

   // --- Abhängigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return (obj == p1) || (obj == p2) || (obj == p3) || (obj == p4) || (obj == p5) || 
           (bRec && 
             ( (p1 != null && p1.dependsOn(obj, bRec)) || 
               (p2 != null && p2.dependsOn(obj, bRec)) || 
               (p3 != null && p3.dependsOn(obj, bRec)) || 
               (p4 != null && p4.dependsOn(obj, bRec)) || 
               (p5 != null && p5.dependsOn(obj, bRec)) ));
   }
}

