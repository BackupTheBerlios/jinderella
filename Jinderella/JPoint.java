
import java.awt.*;

// abstrakte Basisklasse von der alle Punktobjekte abgeleitet werden
abstract class JPoint extends JObject {
   // Zählen der registrierten Punkte -> Namensgebung!
   static int regPoints = 0;

   public void regPoint() {
      // Nicht mehrfach benennen
      if (getName() != "") return;

      String  sName = "" + (char)(((char)'A')+regPoints);
                                              regPoints++;
      setName(sName);
   }

   // Punktkonstanten
   public static final JPoint I = new JPointSimple(Complex.i, new Complex(-1), Complex.Zero);
   public static final JPoint J = new JPointSimple(Complex.i, new Complex(+1), Complex.Zero);

   // Punktdaten speichern (je nach Bildungsmodus)
   protected JObjectVector xyz;
   // ----------------------------------------

   // Konstruktor für alle Punkte
   // ---------------------------
   public JPoint(Color c) { 
      // Farbe speichern
      super(c);
      // Koordinatenobjekt niemals null werden lassen
      this.xyz = new JObjectVector(0,0,0);
   }

   // verschiebe Punktkoordinate
   public void moveTo(JPoint pTo) {
      // Die Koordinaten anpassen...
      xyz = pTo.xyz.copy();
      // Objekt wurde geändert!!!
      setChanged(true);
   }

   // setze Punktkoordinate
   public void setTo(JObjectVector oTo) {
      // Die Koordinaten anpassen...
      xyz = oTo.copy();
      // Objekt wurde geändert!!!
      setChanged(true);
   }

   public boolean equals(JPoint p) {
      // Beide Punkte im Unendlichen oder keiner von beiden
      if ( p.xyz.z.isZero() && !this.xyz.z.isZero()) return false;
      if (!p.xyz.z.isZero() &&  this.xyz.z.isZero()) return false;

      JObjectVector v_t = JObjectVector.roundTo(this.xyz, 10);
      JObjectVector v_p = JObjectVector.roundTo(   p.xyz, 10);

      // Kreuzprodukt muß 0 sein, wenn die Punkte identisch sind
      JObjectVector d = JObjectVector.doCross(v_t, v_p);
      return        d.len().isZero();
   }

   public void normalize() {
      // Die Werte nicht aus dem Ruder laufen lassen...
      xyz.toLevel();
   }

   // Berechne die Entfernung des Punktes zur aktuellen Canvas-Position!
   public Complex calcDistance(JMapping Map, JCanvasVector cPt) {
      // Einrasten in Bildschirmkoordinaten (für Spärischen Modus nur "vorderen" Punkt)
      JCanvasVector cOb = Map.toCanvas(xyz.z.Re() < 0 ? JObjectVector.neg(xyz) : xyz);

      if (cPt == null || cOb == null) return null;

      // Teste ob der Punkt nahegenug am übergebenen Canvaspunkt liegt
      return new Complex(Math.sqrt((cOb.x-cPt.x)*(cOb.x-cPt.x) +
                                   (cOb.y-cPt.y)*(cOb.y-cPt.y)));
   }

   // --- Zeichnung des Objekts je nach Darstellungsmodus ---
   void draw(Graphics g, JMapping Mapping, Color cSelect) {
      // Zeichenfunktion der Anzeigefläche aufrufen
      Mapping.draw(g, this, cSelect);
   }

   // Welchen Freiheitsgrad besitzt dieses Objekt (2-dim. / 1-dim oder 0-dim (fest definiert))
   abstract int degreeOfFreedom();
}
