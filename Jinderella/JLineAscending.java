
import java.awt.*;

// Klasse zur Definition einer Gerade durch einen Punkt mit bestimmter (aber ver�nderbarer) Steigung
class JLineAscending extends JLine {

   // Der Punkt p und der Anstieg pGrad, die die Gerade beschreiben
   JPoint          p;
   JPointIntAscent pGrad;

   // Konstruktor f�r ASCENDING-Typ
   // -----------------------------
   public JLineAscending(JPoint p, JPointIntAscent pGrad         ) { this(p, pGrad, Color.white); }
   public JLineAscending(JPoint p, JPointIntAscent pGrad, Color c) {
      super(c);
      // Die Daten speichern
      this.p     = p; 
      this.pGrad = pGrad;
      // Die Normale updaten
      doUpdate();
   }

   // --- Sind bestimmende Objekte unver�ndert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unver�ndert?
      return !p    .isChanged() && 
             !pGrad.isChanged();
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      // Anstieg vorbereiten...
      if (pGrad.isChanged()) 
          pGrad.recalcAscent(p);

      // Gerade durch p1 und pInfinity (=> (pGrad x lInfinity)) -> gesuchte Gerade
      setTo(JObjectVector.doCross(p.xyz, JObjectVector.doCross(pGrad.getLine(), JLine.lInfinity.abc)));
      // Werte nicht aus dem Ruder laufen lassen
      normalize();
   };

   // --- Abh�ngigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return (obj == p) || (obj == pGrad) || 
           (bRec && 
             ( (p     != null && p    .dependsOn(obj, bRec)) || 
               (pGrad != null && pGrad.dependsOn(obj, bRec)) ));
   }
}

