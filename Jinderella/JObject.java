
import java.awt.Color;
import java.awt.Graphics;

// abstrakte Basisklasse von der alle Jinderella-Objekte abgeleitet werden
abstract class JObject {
   // Objektmerkmale
   private Color   cColor;          public Color   getColor()  { return cColor;   }
   private boolean bChanged;        public boolean isChanged() { return bChanged; }
   private String  sName;           public String  getName()   { return sName;    }

   public JObject(Color cColor) { 
      this.cColor   = cColor;
      this.bChanged = false;
      this.sName    = "";
   };

   // Zugriff auf �nderungsbit
   void setChanged(boolean bSet) {
      this.bChanged = bSet;
   }
   // Zugriff auf Objektnamen
   void setName(String sName) {
      this.sName = sName;
   }

   // --- Sind bestimmende Objekte unver�ndert geblieben? ---
   abstract boolean isUp2Date();

   // --- Objekt auf den neuesten Stand bringen!!!        ---
   abstract void doUpdate();

   // --- Abh�ngigkeiten der Objekte untereinander merken ---
   abstract boolean dependsOn(JObject obj, boolean bRec);
            boolean dependsOn(JObject obj) { return dependsOn(obj, true); }      // Standardm��ig rekursive Arbeitsweise

   // --- Zeichnung des Objekts je nach Darstellungsmodus ---
   abstract void draw(Graphics g, JMapping Map, Color cSelect);
}

