
import java.awt.*;

// Klasse zur Definition einer Gerade durch zwei Punkte
class JLineConnected extends JLine {

   // Die Punkte, die die Gerade definieren!
   JPoint p1, p2; 


   // Konstruktor für CONNECTED-Typ
   // -----------------------------
   public JLineConnected(JPoint p1, JPoint p2         ) { this(p1, p2, Color.white); }
   public JLineConnected(JPoint p1, JPoint p2, Color c) {
      super(c);
      // Die Punkte speichern
      this.p1 = p1; 
      this.p2 = p2;
      // Die Normale updaten
      doUpdate();
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return !p1.isChanged() &&
             !p2.isChanged();
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      setTo(JObjectVector.doCross(p1.xyz, p2.xyz));      // Normale neu berechnen
      // Werte nicht aus dem Ruder laufen lassen
      normalize();
   };

   // --- Abhängigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return (obj == p1) || (obj == p2) || 
           (bRec && 
             ( (p1 != null && p1.dependsOn(obj, bRec)) || 
               (p2 != null && p2.dependsOn(obj, bRec)) ));
   }
}

