
import java.awt.Color;

// Klasse zur Definition eines Punktes über seine homogene Koordinate
class JPointSimple extends JPoint {

   // Konstruktor für SIMPLE-Typ
   // --------------------------
   public JPointSimple(JObjectVector o                         ) { this(          o.x ,           o.y ,           o.z , Color.white); }
   public JPointSimple(JObjectVector o                , Color c) { this(          o.x ,           o.y ,           o.z , c          ); }
   public JPointSimple(double  x, double  y, double  z         ) { this(new Complex(x), new Complex(y), new Complex(z), Color.white); }
   public JPointSimple(double  x, double  y, double  z, Color c) { this(new Complex(x), new Complex(y), new Complex(z), c          ); }
   public JPointSimple(Complex x, Complex y, Complex z         ) { this(            x ,             y ,             z , Color.white); }
   public JPointSimple(Complex x, Complex y, Complex z, Color c) {
      super(c);
      // Koordinaten speichern
      this.xyz = new JObjectVector(x,y,z);
      // Punkte auf den neuesten Stand bringen
      doUpdate();
   }

   // --- Sind bestimmende Objekte unverändert geblieben? ---
   boolean isUp2Date() {
      // Sie die zugrundeliegenden Daten unverändert?
      return true;
   }

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   public void doUpdate() {
      // Werte nicht aus dem Ruder laufen lassen
      normalize();
   };

   // --- Abhängigkeiten der Objekte untereinander merken ---
   boolean dependsOn(JObject obj, boolean bRec) {
      return false;
   }

   // Welchen Freiheitsgrad besitzt dieses Objekt (2-dim. / 1-dim oder 0-dim (fest definiert))
   int degreeOfFreedom() {
      return 2;            // Punkt ist frei verschiebbar!
   }
}
