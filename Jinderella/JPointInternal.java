
import java.awt.*;

// abstrakte Basisklasse für alle veränderbaren (internen) "Punkte" -> bspw. Steigung, Abstand etc.
abstract class JPointInternal extends JPoint {

   // Konstruktor für alle Punkte
   // ---------------------------
   public JPointInternal() { 
      // Farbe speichern
      super(Color.white);
   }

   public void normalize() {
      // Ein interner Punkt enthält keine wirklichen homogenen Koordinaten, und darf daher nicht normalisiert werden
   }

   // Befindet sich der übergebene Punkt nah genug, so daß er einrastbar ist?
   public Complex calcDistance(JMapping Map, JCanvasVector cPt) {
      // Punkt ist nicht real, und somit nicht erfassbar
      return null;
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return true;
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      // Nichts zum Updaten da... (das ubergeordnete Objekt ist für die Änderung zuständig)
   };

   // --- Abhängigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return false;
   }

   // --- Zeichnung des Objekts je nach Darstellungsmodus ---
   void draw(Graphics g, JMapping Map, Color cSelect) {
      // Der Punkt kann auch nicht gezeichnet werden...
   }

   // Welchen Freiheitsgrad besitzt dieses Objekt (2-dim. / 1-dim oder 0-dim (fest definiert))
   int degreeOfFreedom() {
      return 2;            // Interne Punkte sind immer verschiebbar!
   }
}
