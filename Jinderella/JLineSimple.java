
import java.awt.*;

// Klasse zur Definition einer Gerade �ber den homogenen Normalenvektor
class JLineSimple extends JLine {

   // Konstruktor f�r SIMPLE-Typ
   // --------------------------
   public JLineSimple(JObjectVector o                         ) { this(          o.x ,           o.y ,           o.z , Color.white); }
   public JLineSimple(JObjectVector o                , Color c) { this(          o.x ,           o.y ,           o.z , c          ); }
   public JLineSimple(double  x, double  y, double  z         ) { this(new Complex(x), new Complex(y), new Complex(z), Color.white); }
   public JLineSimple(double  x, double  y, double  z, Color c) { this(new Complex(x), new Complex(y), new Complex(z), c          ); }
   public JLineSimple(Complex x, Complex y, Complex z         ) { this(            x ,             y ,             z , Color.white); }
   public JLineSimple(Complex x, Complex y, Complex z, Color c) {
      super(c); 
      // Normale speichern
      this.abc = new JObjectVector(x,y,z);
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

