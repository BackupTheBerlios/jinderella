
// Mapping f�r Sph�rische Darstellung
public class JMappingSphere3D extends JMappingSphere {

   double dDistance;                            // Distanz zum Mittelpunkt der Kugel 
   double dViewAngle;                           // Blickwinkel zum oberen bzw. unteren Rand (im Verh�ltnis)

   // Konstruktor
   public JMappingSphere3D(double        dDistance , double        dViewAngle ,
                         JCanvasVector cUpperLeft, JCanvasVector cLowerRight) {
      // Canvas definieren
      super(1.0 / dDistance, cUpperLeft, cLowerRight);

      // Abstand zum Ursprung
      this.dDistance  =          dDistance;
      this.dViewAngle = Math.tan(dViewAngle/180*Math.PI);
   }

   // Umwandlung von Bildschirm- in Objektkoordinaten
   public JObjectVector toObject(JCanvasVector cPt) {
      // Sicherheitsabfrage
      if (cPt == null) return null;

      double rx_rz = ((double)(cPt.x - cUpperLeft.x))/(cLowerRight.x-cUpperLeft.x) * dAspectRatio*dViewAngle*2.0 - dViewAngle*dAspectRatio;
      double ry_rz = ((double)(cPt.y - cUpperLeft.y))/(cLowerRight.y-cUpperLeft.y) *              dViewAngle*2.0 - dViewAngle;

                                          double d2 = dDistance * dDistance;
      double x   = dDistance * rx_rz;     double x2 =         x * x;
      double y   = dDistance * ry_rz;     double y2 =         y * y;
      
      double det = d2 * (1.0 - x2 - y2) + x2 + y2;

      // �berpr�fen, ob der Kreis �berhaupt getroffen wird!!!
      if (det < 0.0)
         return new JObjectVector( x, y, 0 );

      // Hole vorderen Schnittpunkt mit der Kugel
      double t   = (x2+y2+Math.sqrt(det))/(x2+y2+d2);

      return new JObjectVector( -t*x + x, -t*y + y,  t*dDistance );
   }

   // Umwandlung von Objekt- in Bildschirmkoordinaten
   public JCanvasVector toCanvas(JObjectVector oPt) {
      // Sicherheitsabfrage
      if (oPt == null) return null;

      // Nicht mit dem Original arbeiten
      oPt = oPt.copy();
            oPt.toUnit();
      
      double rx =  oPt.x.Re() - 0;
      double ry =  oPt.y.Re() - 0;
      double rz = -oPt.z.Re() + dDistance;

      // H�he des Fensters bestimmt die Gr��e der Kugel
      double  x = ((double)(rx/rz + dAspectRatio*dViewAngle))/dAspectRatio/dViewAngle/2.0 * (cLowerRight.x-cUpperLeft.x) + cUpperLeft.x;
      double  y = ((double)(ry/rz +              dViewAngle))/             dViewAngle/2.0 * (cLowerRight.y-cUpperLeft.y) + cUpperLeft.y;

      return new JCanvasVector( (int)x, (int)y );
   }
}
