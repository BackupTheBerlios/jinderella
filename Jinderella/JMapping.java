
import java.awt.*;

// abstrakte Basisklasse zum Umwandeln von Objekt- in Bildschirmkoordinaten und vice versa
abstract class JMapping {

   JCanvasVector cUpperLeft;           // Bildschirmecke links oben
   JCanvasVector cLowerRight;          // Bildschirmecke rechts unten

   double dAspectRatio;                // Verhältnis der x- zur y-Achse

   // Konstruktoren für die verschieden Mappingmodi
   public JMapping(JCanvasVector cUpperLeft, JCanvasVector cLowerRight) {
      // Euklidischer Mappingmodus
      this.cUpperLeft  = cUpperLeft.copy();
      this.cLowerRight = cLowerRight.copy();

      // Aspect-Ratio
      this.dAspectRatio = ((double)cLowerRight.x-cUpperLeft.x)/(cLowerRight.y-cUpperLeft.y);
   }

   // Prüfung ob ein Bildschirmpunkt innerhalb des Anzeigebereichs liegt
   public boolean contains(JCanvasVector cPt) {
      return (cPt.x >= cUpperLeft.x && cPt.x <= cLowerRight.x && 
              cPt.y >= cUpperLeft.y && cPt.y <= cLowerRight.y);
   }

   // Versuche die Bildschirmkoordinate in das Raster einzuklinken
   public JCanvasVector snapToGrid(JCanvasVector cPt, double dSnapGrid) {
      // Standardmäßig ist das Einrasten deaktiviert
      return cPt;
   }

   // Vorbereitung der Anzeigefläche
   public void prepareCanvas(Graphics g) {
      // Standardmäßig nur die Bildfläche löschen
      g.setColor(Color.lightGray);
      g.fillRect(cUpperLeft.x, cUpperLeft.y, cLowerRight.x, cLowerRight.y);
   }

   // Funktion zum Zeichnen von Punkten im jeweilige Anzeigemodus
   public void draw(Graphics g, JPoint jPoint, Color cSelect) {
      // Keine Standardmethode
   }

   // Funktion zum Zeichnen von Geraden im jeweilige Anzeigemodus
   public void draw(Graphics g, JLine  jLine , Color cSelect) {
      // Keine Standardmethode
   }

   // Funktion zum Zeichnen von Geraden im jeweilige Anzeigemodus
   public void draw(Graphics g, JConic jConic, Color cSelect) {
      // Keine Standardmethode
   }

   // Umwandlung von Bildschirm- in Objektkoordinaten
   abstract public JObjectVector toObject(JCanvasVector cPt);

   // Umwandlung von Objekt- in Bildschirmkoordinaten
   abstract public JCanvasVector toCanvas(JObjectVector oPt);

   // ******************************************************************************************************************
   // Hilfsfunktion zur Zeichnung von Polylinien-Abschnitten (jeweils Anfangs- und Endpunkt der Linie ist abgespeichert)
   protected void drawMyPolyline(Graphics g, Polygon pPoly, int dx, int dy) {
      for (int i=0; i<pPoly.npoints-1; i+=2) {
         g.drawLine(pPoly.xpoints[i  ]+dx, pPoly.ypoints[i  ]+dy
                   ,pPoly.xpoints[i+1]+dx, pPoly.ypoints[i+1]+dy);
      }
   }
}
