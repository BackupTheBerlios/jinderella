
import java.awt.*;

// Klasse zur Definition einer Gerade durch zwei Punkte
class JCircleConnected extends JConic {

   // Die Punkte, die den Kreis definieren!
   JPoint p1, p2, p3; 


   // Konstruktor für CONNECTED-Typ
   // -----------------------------
   public JCircleConnected(JPoint p1, JPoint p2, JPoint p3         ) { this(p1, p2, p3, Color.white); }
   public JCircleConnected(JPoint p1, JPoint p2, JPoint p3, Color c) {
      super(c);
      // Die Punkte speichern
      this.p1 = p1; 
      this.p2 = p2;
      this.p3 = p3;
      // Die Normale updaten
      doUpdate();
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return !p1.isChanged() &&
             !p2.isChanged() &&
             !p3.isChanged();
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      // Matrizen berechnen
      JConicConnected jConic = new JConicConnected(p1, JPoint.I, 
                                                   p2, JPoint.J, p3);
      // Neue Matrix setzen
      setTo(jConic.M);
      // Werte nicht aus dem Ruder laufen lassen
      normalize();
   };

   // --- Abhängigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return (obj == p1) || (obj == p2) || (obj == p3) || 
           (bRec && 
             ( (p1 != null && p1.dependsOn(obj, bRec)) || 
               (p2 != null && p2.dependsOn(obj, bRec)) || 
               (p3 != null && p3.dependsOn(obj, bRec)) ));
   }
}

