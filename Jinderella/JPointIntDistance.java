
// Klasse für internes Objekt das den Abstand zwischen einem Referenzobjekt und diesem Objekt bereitstellt
class JPointIntDistance extends JPointInternal {

   // Gewünschter Abstand
   Complex d;

   // Konstruktor
   // --------------------------
   public JPointIntDistance(JPoint  pRef) {
      // Setze Punktkoordinaten -> Objekt wird geändert
      moveTo(pRef);
   }
   public JPointIntDistance(Complex d   ) {
      // Koordinaten speichern
      this.d = d.copy();
   }

   // Berechne neue Steigung aufgrund des aktuellen Objekts und des übergebenen Referenzobjekts (Punkt)
   void recalcDistance(JPoint pFrom) {
      // Nur wenn dieses Objekt geändert wurde, befindet sich ein gültiger Punkt in xyz
      if (!isChanged()) return;

      JObjectVector oAscent = JObjectVector.sub(xyz, pFrom.xyz);
                d = oAscent.len();

      // Änderung übernommen
      setChanged(false);
   }
   
   // Berechne neue Steigung aufgrund des aktuellen Objekts und des übergebenen Referenzobjekts (Gerade)
   void recalcDistance(JLine pFrom) {
      // Nur wenn dieses Objekt geändert wurde, befindet sich ein gültiger Punkt in xyz
      if (!isChanged()) return;

      d = xyz.doScalar(pFrom.abc);

      if (     pFrom.abc.len().Len() != 0)
         d.div(pFrom.abc.len());

      // Änderung übernommen
      setChanged(false);
   }
}
