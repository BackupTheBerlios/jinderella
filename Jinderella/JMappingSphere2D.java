
// Mapping für Sphärische Darstellung
public class JMappingSphere2D extends JMappingSphere {

   double dViewZoom;                            // Blickwinkel zum oberen bzw. unteren Rand (im Verhältnis)

   // Konstruktor
   public JMappingSphere2D(double        dViewZoom ,
                           JCanvasVector cUpperLeft, JCanvasVector cLowerRight) {
      // Canvas definieren
      super(0.0, cUpperLeft, cLowerRight);
      
      // Zoom-Einstellung speichern
      this.dViewZoom = 1.0 / dViewZoom;
   }

   // Umwandlung von Bildschirm- in Objektkoordinaten
   public JObjectVector toObject(JCanvasVector cPt) {
      // Sicherheitsabfrage
      if (cPt == null) return null;

      double x   = ((double)(cPt.x - cUpperLeft.x))/(cLowerRight.x-cUpperLeft.x) * dAspectRatio*dViewZoom*2.0 - dViewZoom*dAspectRatio;
      double y   = ((double)(cPt.y - cUpperLeft.y))/(cLowerRight.y-cUpperLeft.y) *              dViewZoom*2.0 - dViewZoom;

      double det = 1.0 - x*x - y*y;

      // Überprüfen, ob der Kreis überhaupt getroffen wird!!!
      if (det < 0.0)
         return new JObjectVector( x, y, 0 );

      // Hole vorderen Schnittpunkt mit der Kugel
      double z   = Math.sqrt(det);

      return new JObjectVector( x, y, z );
   }

   // Umwandlung von Objekt- in Bildschirmkoordinaten
   public JCanvasVector toCanvas(JObjectVector oPt) {
      // Sicherheitsabfrage
      if (oPt == null) return null;

      // Nicht mit dem Original arbeiten
      oPt = oPt.copy();
            oPt.toUnit();
      
      // Höhe des Fensters bestimmt die Größe der Kugel
      double  x = ((double)(oPt.x.Re() + dAspectRatio*dViewZoom))/dAspectRatio/dViewZoom/2.0 * (cLowerRight.x-cUpperLeft.x) + cUpperLeft.x;
      double  y = ((double)(oPt.y.Re() +              dViewZoom))/             dViewZoom/2.0 * (cLowerRight.y-cUpperLeft.y) + cUpperLeft.y;

      return new JCanvasVector( (int)x, (int)y );
   }
}
