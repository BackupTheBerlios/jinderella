
import java.awt.*;

// Klasse zur Definition einer Gerade �ber den homogenen Normalenvektor
class JConicSimple extends JConic {

   // Konstruktor f�r SIMPLE-Typ
   // --------------------------
   public JConicSimple(JObjectMatrix M         ) { this(M, Color.white); }
   public JConicSimple(JObjectMatrix M, Color c) {
      super(c); 
      // Normale speichern
      this.M = M.copy();
      // Normale auf den neuesten Stand bringen
      doUpdate();
   }

   // --- Sind bestimmende Objekte unver�ndert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unver�ndert?
      return true;
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      // Werte nicht aus dem Ruder laufen lassen
      normalize();
   };

   // --- Abh�ngigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return false;
   }
}

