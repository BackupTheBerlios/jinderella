
// Klasse f�r internes Objekt das den Abstand zwischen einem Referenzobjekt und diesem Objekt bereitstellt
class JPointIntDistance extends JPointInternal {

   // Gew�nschter Abstand
   Complex d;

   // Konstruktor
   // --------------------------
   public JPointIntDistance(JPoint  pRef) {
      // Setze Punktkoordinaten -> Objekt wird ge�ndert
      moveTo(pRef);
   }
   public JPointIntDistance(Complex d   ) {
      // Koordinaten speichern
      this.d = d.copy();
   }

   // Berechne neue Steigung aufgrund des aktuellen Objekts und des �bergebenen Referenzobjekts (Punkt)
   void recalcDistance(JPoint pFrom) {
      // Nur wenn dieses Objekt ge�ndert wurde, befindet sich ein g�ltiger Punkt in xyz
      if (!isChanged()) return;

      JObjectVector oAscent = JObjectVector.sub(xyz, pFrom.xyz);
                d = oAscent.len();

      // �nderung �bernommen
      setChanged(false);
   }
   
   // Berechne neue Steigung aufgrund des aktuellen Objekts und des �bergebenen Referenzobjekts (Gerade)
   void recalcDistance(JLine pFrom) {
      // Nur wenn dieses Objekt ge�ndert wurde, befindet sich ein g�ltiger Punkt in xyz
      if (!isChanged()) return;

      d = xyz.doScalar(pFrom.abc);

      if (     pFrom.abc.len().Len() != 0)
         d.div(pFrom.abc.len());

      // �nderung �bernommen
      setChanged(false);
   }
}
