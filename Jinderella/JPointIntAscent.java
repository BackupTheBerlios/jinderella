
// Klasse f�r internes Objekt das die Steigung zwischen einem Referenzpunkt und diesem Objekt bereitstellt
class JPointIntAscent extends JPointInternal {

   // Gew�nschter Anstieg
   Complex dx, dy;

   // Konstruktor
   // --------------------------
   public JPointIntAscent(JPoint  pRef          ) {
      // Setze Punktkoordinaten -> Objekt wird ge�ndert
      moveTo(pRef);
   }
   public JPointIntAscent(Complex dx, Complex dy) {
      // Koordinaten speichern
      this.dx = dx.copy();
      this.dy = dy.copy();
   }

   // Berechne neue Steigung aufgrund des aktuellen Objekts und des �bergebenen Referenzobjekts
   void recalcAscent(JPoint pFrom) {
      // Nur wenn dieses Objekt ge�ndert wurde, befindet sich ein g�ltiger Punkt in xyz
      if (!isChanged()) return;
      
      if (xyz.z.Re() == 0.0) {
         // Steigung direkt �bernehmen
         dx = Complex.neg(xyz.y);
         dy =             xyz.x.copy();
      } else {
         // Der Anstiegsvektor mu� nach euklidischen Ma�st�ben berechnet werden -> auf Ebene projizieren
                                                      xyz.toLevel();    pFrom.normalize();
         JObjectVector    oAscent = JObjectVector.sub(xyz,              pFrom.xyz);
         // Normale aus Steigung berechnen
         dx = Complex.neg(oAscent.y);
         dy =             oAscent.x.copy();
      }
      // �nderung �bernommen
      setChanged(false);
   }

   // Gerade durch den Ursprung mit Steigung dx, dy
   JObjectVector getLine() {
      return new JObjectVector(dx, dy, Complex.Zero);
   }
}
