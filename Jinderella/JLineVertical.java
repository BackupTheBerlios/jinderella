
import java.awt.*;

// Klasse zur Definition einer Gerade, die orthogonal zu einer anderen Gerade durch einen Punkt geht
class JLineVertical extends JLine {

   // Die Gerade und der Punkt, die die Gerade definieren!
   JLine  l;
   JPoint p; 

   // Konstruktor für VERTICAL-Typ
   // -----------------------------
   public JLineVertical(JLine l, JPoint p         ) { this(l, p, Color.white); }
   public JLineVertical(JLine l, JPoint p, Color c) {
      super(c);
      // Die Objekte speichern
      this.l = l; 
      this.p = p;
      // Die Normale updaten
      doUpdate();
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return !l.isChanged() &&
             !p.isChanged();
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      // Gerade durch p mit Anstieg der Normalen von l
      JPointIntAscent pGrad = new JPointIntAscent(Complex.neg(l.abc.y), l.abc.x);
      JLine           lVert = new JLineAscending(p, pGrad);

      setTo(lVert.abc);
      // Werte nicht aus dem Ruder laufen lassen
      normalize();
   };

   // --- Abhängigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return (obj == l) || (obj == p) || 
           (bRec && 
             ( (l != null && l.dependsOn(obj, bRec)) || 
               (p != null && p.dependsOn(obj, bRec)) ));
   }
}

