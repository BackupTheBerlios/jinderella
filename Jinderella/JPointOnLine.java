
import java.awt.Color;

// Klasse zur Definition eines Punktes, der sich auf einer Gerade befindet
class JPointOnLine extends JPoint {

   // Gerade auf die der Punkt projiziert werden soll!
   JLine l;  

   // Konstruktor für STRAIGHT-Typ
   // -----------------------------
   public JPointOnLine(JLine l, JPoint        pPos         ) { this(l, pPos.xyz, Color.white); }
   public JPointOnLine(JLine l, JPoint        pPos, Color c) { this(l, pPos.xyz, c          ); }
   public JPointOnLine(JLine l, JObjectVector oPos         ) { this(l, oPos    , Color.white); }
   public JPointOnLine(JLine l, JObjectVector oPos, Color c) {
      super(c);
      // Punkte in den Ausgangsort verschieben und Gerade registrieren
      this.xyz = oPos.copy();
      this.l   = l;                          
      // Koordinaten aktualisieren...
      doUpdate();
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return !l.isChanged();
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      JLine  lVert = new JLineVertical     (l, this );   // Vertikale zu l1 durch diesen Punkt
      JPoint pSect = new JPointIntersection(l, lVert);   // Beide Geraden schneiden

      moveTo(pSect);                                     // Punkt in die Zielkoordinaten verschieben
      // Werte nicht aus dem Ruder laufen lassen
      normalize();
   };

   // --- Abhängigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return (obj == l) || 
           (bRec && 
             ( (l != null && l.dependsOn(obj, bRec)) ));
   }

   // Welchen Freiheitsgrad besitzt dieses Objekt (2-dim. / 1-dim oder 0-dim (fest definiert))
   int degreeOfFreedom() {
      return 1;            // Punkt kann 1-dim. (auf der Gerade) verschoben werden
   }
}
