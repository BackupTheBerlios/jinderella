
import java.awt.*;

// Mapping für Sphärische Darstellung
abstract class JMappingSphere extends JMapping {

   double dViewableZ;                           // Maximaler Wert der z-Koordinate, für die die Punkte 
                                                // auf der Kugel sichtbar sind (vom Viewpoint V(0,0,dDistance))
   // Konstruktor
   public JMappingSphere(double        dViewableZ,
                         JCanvasVector cUpperLeft, JCanvasVector cLowerRight) {
      // Canvas definieren
      super(cUpperLeft, cLowerRight);

      // dViewableZ berechnen
      this.dViewableZ = dViewableZ;
   }

   // Vorbereitung der Anzeigefläche
   public void prepareCanvas(Graphics g) {
      // Bildfläche löschen
      g.setColor(Color.gray);
      g.fillRect(cUpperLeft.x, cUpperLeft.y, cLowerRight.x, cLowerRight.y);

      // Umriss zeichnen
      double dZ = dViewableZ;
      double dY = Math.sqrt(1.0 - dZ*dZ);
      double dX = dY;

      JCanvasVector cM = toCanvas(new JObjectVector(  0,   0,  0));
      JCanvasVector cA = toCanvas(new JObjectVector(-dX,   0, dZ));
      JCanvasVector cB = toCanvas(new JObjectVector(  0, -dY, dZ));
      
      g.setColor(Color.lightGray);
      g.fillOval(cA.x, cB.y, (cM.x-cA.x)*2, (cM.y-cB.y)*2);
                 cA = null;

      // Äquator darstellen
      g.setColor(Color.white);
      for (double phi=0.0; phi<=360.0; phi+=5.0) {
             cB  = toCanvas(new JObjectVector(Math.sin(phi/180*Math.PI) * dX
                                             ,Math.cos(phi/180*Math.PI) * dY, dZ));

         if (cA != null) g.drawLine(cA.x, cA.y, cB.x, cB.y);
             cA  = cB;
      }
   }

   // Funktion zum Zeichnen von Punkten im jeweilige Anzeigemodus
   public void draw(Graphics g, JPoint jPoint, Color cSelect) {
      // Punkte in Bildschirmkoordinaten umrechnen
      int iIndex = 1;
      do {
         Color         cColor  = jPoint.getColor();
         JObjectVector oPt     = jPoint.xyz.copy();

         // Im Spärischen Modus soll der Index = 1 den hinteren Punkt (z-Wert < 0) liefern
         if ((iIndex == 0 && oPt.z.Re()< 0) ||
             (iIndex == 1 && oPt.z.Re()>=0)) oPt.neg();
                                             oPt.toUnit();
         // Punkte hinten sind dunkler
         if (oPt.z.Re() < dViewableZ)
            cColor  = cColor .darker();

         JCanvasVector cPt  = toCanvas(oPt);
         if (          cPt == null) return;

         if (jPoint.degreeOfFreedom() > 0) {
            // freie Punkte kenntlich machen
            g.setColor(iIndex == 0 ? cColor : Color.gray);            // Index == 0 -> farbig
            g.fillOval(cPt.x - 8, cPt.y - 8, 16, 16);
         }
         
         if (jPoint.degreeOfFreedom() > 0 || cSelect != null) {
            // Punkt selektiert
            g.setColor(cSelect != null ?  cSelect : Color.lightGray); // Markierung mit cColor oder übermalen mit Hintergrund
            g.fillOval(cPt.x - 7, cPt.y - 7, 14, 14);
         }

            // Punkt einzeichnen
            g.setColor(iIndex == 0 ? cColor : Color.gray);            // Index == 0 -> farbig
            g.fillOval(cPt.x - 5, cPt.y - 5, 10, 10);

         // Imaginäre Koordinaten? -> Not Yet Implemented!
         // ...

            g.setColor(Color.black);
            g.drawString(jPoint.getName(), cPt.x+5, cPt.y+15);
         
         // Noch "Punkte" zu zeichnen
         iIndex--;
      } while (iIndex >= 0);
   }

   // Funktion zum Zeichnen von Geraden im jeweilige Anzeigemodus
   public void draw(Graphics g, JLine  jLine , Color cSelect) {
      // Zwei Punkte der Gerade, die senkrecht aufeinander stehen
      JPoint pOnLine1 = new JPointOnLine(jLine, new JPointSimple(0,0,1));
      JPoint pOnLine2 = new JPointSimple(JObjectVector.doCross(pOnLine1.xyz, jLine.abc));

      JObjectVector oA = pOnLine1.xyz.copy(); oA.toUnit();
      JObjectVector oB = pOnLine2.xyz.copy(); oB.toUnit();

      JCanvasVector cA = null;
      JCanvasVector cB = null;

      // Nimm die Punkte auf (keine eigentlichen Polygone, da sowohl Anfangs- als auch Endpunkt gespeichert wird)
      Polygon pOnePoly = new Polygon();
      Polygon pTwoPoly = new Polygon();

      for (double phi=0.0; phi<=360.0; phi+=5.0) {
         JObjectVector oV = JObjectVector.combine(oA, oB, phi/180.0*Math.PI);
                       cB = toCanvas(oV);

         Polygon pTarget = (oV.z.Re() >= dViewableZ) ? pOnePoly : pTwoPoly;

         if (cA != null) {
            pTarget.addPoint(cA.x, cA.y); 
            pTarget.addPoint(cB.x, cB.y); 
         }
             cA  = cB;
      }

      if (cSelect != null) {
         // Terrible, is'nt it?
         g.setColor(cSelect.darker());
         drawMyPolyline(g, pTwoPoly, -1, -1);
         drawMyPolyline(g, pTwoPoly, +0, -1);
         drawMyPolyline(g, pTwoPoly, +1, -1);
         drawMyPolyline(g, pTwoPoly, -1, +0);
         drawMyPolyline(g, pTwoPoly, +1, +0);
         drawMyPolyline(g, pTwoPoly, -1, +1);
         drawMyPolyline(g, pTwoPoly, +0, +1);
         drawMyPolyline(g, pTwoPoly, +1, +1);

         g.setColor(cSelect);
         drawMyPolyline(g, pOnePoly, -1, -1);
         drawMyPolyline(g, pOnePoly, +0, -1);
         drawMyPolyline(g, pOnePoly, +1, -1);
         drawMyPolyline(g, pOnePoly, -1, +0);
         drawMyPolyline(g, pOnePoly, +1, +0);
         drawMyPolyline(g, pOnePoly, -1, +1);
         drawMyPolyline(g, pOnePoly, +0, +1);
         drawMyPolyline(g, pOnePoly, +1, +1);
      }
      g.setColor(jLine.getColor().darker());
      drawMyPolyline(g, pTwoPoly, +0, +0);

      g.setColor(jLine.getColor());
      drawMyPolyline(g, pOnePoly, +0, +0);      
   }

   // Funktion zum Zeichnen von Kurven im jeweilige Anzeigemodus
   public void draw(Graphics g, JConic jConic, Color cSelect) {

      if (jConic.lDeg1 != null) {
         // Kurve ist degeneriert -> nur die Geraden zeichnen!
         jConic.lDeg1.draw(g, this, cSelect);
         jConic.lDeg2.draw(g, this, cSelect);
         return;
      }

      // Aktueller und voriger Punkt
      JPoint         o1 = null;  JPoint         p1 = null;
      JPoint         o2 = null;  JPoint         p2 = null;

      JCanvasVector cO1 = null;  JCanvasVector cP1 = null;
      JCanvasVector cO2 = null;  JCanvasVector cP2 = null;

      boolean  bFirstPrevOK = false;
      boolean bSecondPrevOK = false;

      // Raster von Links nach Rechts abarbeiten wenn Kurve unangenehm flach!!!
      boolean bLeft2Right = Math.abs(jConic.M.a.Re() + jConic.M.c.Re() + jConic.M.g.Re()) < 
                            Math.abs(jConic.M.e.Re() + jConic.M.h.Re() + jConic.M.f.Re());

      JObjectVector oOne = bLeft2Right ? new JObjectVector(-1,  0,  0) : new JObjectVector( 0, -1,  0);
      JObjectVector oTwo =               new JObjectVector( 0,  0, -1);

      // Nimm die Punkte auf (keine eigentlichen Polygone, da sowohl Anfangs- als auch Endpunkt gespeichert wird)
      Polygon pOnePoly = new Polygon();
      Polygon pTwoPoly = new Polygon();

      // Standardschrittweite
      double  dStep  = 8;
      double  dZVal  = 1;
      for (double v  = 0; v < 180.0 + dStep; v += dStep*dZVal) {
         // "Ebene" mit entsprechender Normale berechnen
         JLine lLine = new JLineSimple(JObjectVector.combine(oOne, oTwo, Math.PI*Math.min(v, 180)/180.0),Color.gray);

         // Beide Schnittpunkte berechnen (könnte noch optimiert werden -> doppelte Berechnung!)
              p1  = new JPointConicLine(jConic, lLine, 0);
              p2  = new JPointConicLine(jConic, lLine, 1);

         // Schnittpunkte in Bildschirmkoordinaten
             cP1  = toCanvas( p1.xyz );
             cP2  = toCanvas( p2.xyz );

         double dD1 = (cO1 == null) ? 0 : JCanvasVector.sub(cP1, cO1).len();
         double dD2 = (cO2 == null) ? 0 : JCanvasVector.sub(cP1, cO2).len();

         if (dD2 < dD1) {
            // Vertauschung erfolgt!!! -> Rechenungenauigkeiten
             JCanvasVector cTp = cP2;     JPoint oTp =  p2;
                           cP2 = cP1;             p2 =  p1;
                           cP1 = cTp;             p1 = oTp;
         }
             
         // Kurvenabschnitt (wenn gewünscht) zeichnen
         boolean  bFirstLineOK = cO1 != null && cP1 != null && contains(cP1) && p1.xyz.isReal() && !p1.xyz.len().isZero();
         boolean bSecondLineOK = cO1 != null && cP2 != null && contains(cP2) && p2.xyz.isReal() && !p2.xyz.len().isZero();

         // Versuche zuerst die Schritte zu verkleinern
         if (( bFirstPrevOK ^  bFirstLineOK) || 
             (bSecondPrevOK ^ bSecondLineOK)) {
            if (dStep > 0.001) {
               v -= dStep * dZVal;
                    dStep = java.lang.Math.max(0.001, dStep/2);
               continue;
            }
         } else {
                    dStep = java.lang.Math.min(8.000, dStep*2);
         }

         if ( bFirstLineOK &&  bFirstPrevOK) {
            pOnePoly.addPoint(cO1.x, cO1.y);
            pOnePoly.addPoint(cP1.x, cP1.y);
         }
         if (bSecondLineOK && bSecondPrevOK) {
            pTwoPoly.addPoint(cO2.x, cO2.y);
            pTwoPoly.addPoint(cP2.x, cP2.y);
         }

         // Letzten Kurvenabschnittspunkt merken (für nächsten Abschnitt)
             cO1  = cP1;     o1 = p1;
             cO2  = cP2;     o2 = p2;

          bFirstPrevOK =  bFirstLineOK;
         bSecondPrevOK = bSecondLineOK;

         // Die Schrittweite ist auch abhängig von der Position auf der Kugel (vorderes Gitter ist sonst zu weit)
             dZVal  = lLine.abc.z.Re()*lLine.abc.z.Re();
             dZVal  = Math.max(0.1, dZVal*dZVal);
      }

      if (cSelect != null) {
         // Terrible, is'nt it?
         g.setColor(cSelect);
         drawMyPolyline(g, pOnePoly, -1, -1);      drawMyPolyline(g, pTwoPoly, -1, -1);
         drawMyPolyline(g, pOnePoly, +0, -1);      drawMyPolyline(g, pTwoPoly, +0, -1);
         drawMyPolyline(g, pOnePoly, +1, -1);      drawMyPolyline(g, pTwoPoly, +1, -1);
         drawMyPolyline(g, pOnePoly, -1, +0);      drawMyPolyline(g, pTwoPoly, -1, +0);
         drawMyPolyline(g, pOnePoly, +1, +0);      drawMyPolyline(g, pTwoPoly, +1, +0);
         drawMyPolyline(g, pOnePoly, -1, +1);      drawMyPolyline(g, pTwoPoly, -1, +1);
         drawMyPolyline(g, pOnePoly, +0, +1);      drawMyPolyline(g, pTwoPoly, +0, +1);
         drawMyPolyline(g, pOnePoly, +1, +1);      drawMyPolyline(g, pTwoPoly, +1, +1);
      }
      g.setColor(jConic.getColor());
      drawMyPolyline(g, pOnePoly, +0, +0);      drawMyPolyline(g, pTwoPoly, +0, +0);
   }

   // Umwandlung von Bildschirm- in Objektkoordinaten
   abstract public JObjectVector toObject(JCanvasVector cPt);

   // Umwandlung von Objekt- in Bildschirmkoordinaten
   abstract public JCanvasVector toCanvas(JObjectVector oPt);
}
