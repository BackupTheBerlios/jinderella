
import java.awt.Color;

// Klasse zur Definition eines Schnittpunktes zwischen zwei Geraden
class JPointIntersection extends JPoint {

   // Die Geraden, die den Schnittpunkt definieren!
   JLine  l1, l2;  

   // Konstruktor für INTERSECT-Typ
   // -----------------------------
   public JPointIntersection(JLine l1, JLine l2         ) { this(l1, l2, Color.white); }
   public JPointIntersection(JLine l1, JLine l2, Color c) {
      super(c);
      // Die Geraden speichern
      this.l1  = l1; 
      this.l2  = l2;          
      // Die Punktkoordinaten updaten...
      doUpdate();
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return !l1.isChanged() &&
             !l2.isChanged();
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      setTo(JObjectVector.doCross(l1.abc, l2.abc));   // Schnittpunkt neu berechnen
      // Werte nicht aus dem Ruder laufen lassen
      normalize();
   };

   // --- Abhängigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return (obj == l1) || (obj == l2) || 
           (bRec && 
             ( (l1 != null && l1.dependsOn(obj, bRec)) || 
               (l2 != null && l2.dependsOn(obj, bRec)) ));
   }

   // Welchen Freiheitsgrad besitzt dieses Objekt (2-dim. / 1-dim oder 0-dim (fest definiert))
   int degreeOfFreedom() {
      return 0;            // Eindeutig bestimmt durch die 2 Geraden!
   }
}
