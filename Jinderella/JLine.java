
import java.awt.*;

// abstrakte Basisklasse von der alle Linienobjekte abgeleitet werden
abstract class JLine extends JObject {
   // Zählen der registrierten Punkte -> Namensgebung!
   static int regLines = 0;

   public void regLine() {
      // Nicht mehrfach benennen
      if (getName() != "") return;

      String  sName = "" + (char)(((char)'a')+regLines);
                                              regLines++;
      setName(sName);
   }

   // Statische Konstanten zur einfachen Benutzung
   public static final JLine lInfinity = new JLineSimple(0,0,1);

   // Liniendaten speichern (ja nach Bildungsmodus)
   protected JObjectVector abc;
   // ----------------------------------------

   // Konstruktor für alle Linien
   // --------------------------
   public JLine(Color c) {
      // Farbe speichern
      super(c);
      // Normalenobjekt niemals null werden lassen
      this.abc = new JObjectVector(0,0,0);
   }

   public void setTo(JObjectVector oTo) {
      // Die Normale anpassen
      abc = oTo.copy();
      // Objekt wurde geändert!!!
      setChanged(true);
   }

   public void normalize() {
      // Die Werte nicht aus dem Ruder laufen lassen...
      abc.toUnit();
   }

   // Berechne die Entfernung der Gerade zur aktuellen Canvas-Position!
   public Complex calcDistance(JMapping Map, JCanvasVector cPt) {
      // Der aktuelle Punkt wird in Objektkoordinaten umgerechnet
      JObjectVector oPt = Map.toObject(cPt);

      if (oPt == null || cPt == null) return null;

      JPoint pPtHere = new JPointSimple(oPt);
      JPoint pOnLine = new JPointOnLine(this, pPtHere);
      // pOnLine wurde nun auf die Gerade projiziert!

      return pOnLine.calcDistance(Map, cPt);
   }

   // --- Zeichnung des Objekts je nach Darstellungsmodus ---
   void draw(Graphics g, JMapping Mapping, Color cSelect) {
      // Zeichenfunktion der Anzeigefläche aufrufen
      Mapping.draw(g, this, cSelect);
   }
}

