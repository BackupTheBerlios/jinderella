
import java.awt.*;

// Mapping für die euklidische Darstellung
public class JMappingEuklid extends JMapping {

   double        dGridSize;                        // Abstand der Gitternetzlinien

   JObjectVector oUpperLeft;
   JObjectVector oLowerRight;

   JPoint pUpperLeft, pUpperRight;                 // Eckpunkte für Euklidische Darstellung
   JPoint pLowerLeft, pLowerRight;  

   JLine  lTop, lBottom, lLeft, lRight;            // Randgeraden für Euklidische Darstellung

   // Konstruktor
   public JMappingEuklid(double        dGridSize,
                         JObjectVector oUpperLeft, JObjectVector oLowerRight,
                         JCanvasVector cUpperLeft, JCanvasVector cLowerRight) {
      // Canvas definieren
      super(cUpperLeft, cLowerRight);

      // Beachte Aspect-Ratio (die Darstellung soll nicht verzerrt werden)
      JObjectVector oMid = JObjectVector.div(JObjectVector.add(oUpperLeft,oLowerRight), new Complex(2));
      if (dAspectRatio  >= 1.0) {
         // Breiter als Hoch -> passe x-Bereich an y-Bereich an
         Complex     w = Complex.div(     Complex.mul(     Complex.sub(oLowerRight.y, oUpperLeft.y)
                                                     , new Complex(dAspectRatio))
                                    , new Complex(2.0));
         oUpperLeft .x = Complex.sub( oMid.x, w );
         oLowerRight.x = Complex.add( oMid.x, w );
      } else {
         // Höher als Breit -> passe y-Bereich an x-Bereich an
         Complex     h = Complex.div(     Complex.div(     Complex.sub(oLowerRight.x, oUpperLeft.x)
                                                     , new Complex(dAspectRatio))
                                    , new Complex(2.0));
         oUpperLeft .y = Complex.sub( oMid.y, h );
         oLowerRight.y = Complex.add( oMid.y, h );
      }

      this.dGridSize   = dGridSize;
      this.oUpperLeft  = oUpperLeft .copy();
      this.oLowerRight = oLowerRight.copy();

      // Bildschirmeckpunkte zur einfachen Verwendung konstruieren
      this.pUpperLeft  = new JPointSimple(               oUpperLeft                );     
      this.pUpperRight = new JPointSimple(oLowerRight.x, oUpperLeft.y , Complex.One);
      this.pLowerRight = new JPointSimple(               oLowerRight               );
      this.pLowerLeft  = new JPointSimple(oUpperLeft.x , oLowerRight.y, Complex.One);

      // Bildschirmränder zur einfachen Verwendung konstruieren
      this.lTop    = new JLineConnected(this.pUpperLeft , this.pUpperRight);
      this.lRight  = new JLineConnected(this.pUpperRight, this.pLowerRight);
      this.lBottom = new JLineConnected(this.pLowerRight, this.pLowerLeft );
      this.lLeft   = new JLineConnected(this.pLowerLeft , this.pUpperLeft );
   }

   // Versuche die Bildschirmkoordinate in das Raster einzuklinken
   public JCanvasVector snapToGrid(JCanvasVector cPt, double dSnapGrid) {
      // Wird Grid überhaupt benutzt
      if (    dGridSize <= 0   ) return cPt;

      JObjectVector oPt  = toObject(    cPt );
      if (          oPt == null) return cPt;

      // Punkt in Objekt-Koordinaten umrechnen
      Complex x = oPt.x;
      Complex y = oPt.y;

      double dSnapObjectX = 2*dSnapGrid / ((double)cLowerRight.x-cUpperLeft.x) * (oLowerRight.x.Re() - oUpperLeft.x.Re());
      double dSnapObjectY = 2*dSnapGrid / ((double)cLowerRight.y-cUpperLeft.y) * (oLowerRight.y.Re() - oUpperLeft.y.Re());

      // *********************************
      // Abstand zu den Gitternetzlinien
      double dx = Math.floor(x.Re()/dGridSize+0.5)*dGridSize - x.Re();
      double dy = Math.floor(y.Re()/dGridSize+0.5)*dGridSize - y.Re(); 

      if (   dx >= -dSnapObjectX && 
             dx <=  dSnapObjectX) x = new Complex(x.Re() + dx);
      if (   dy >= -dSnapObjectY && 
             dy <=  dSnapObjectY) y = new Complex(y.Re() + dy);
      // *********************************
      
      // "Neue" Koordinaten zurückliefern
      return toCanvas(new JObjectVector(x, y, Complex.One));
   }

   // Vorbereitung der Anzeigefläche
   public void prepareCanvas(Graphics g) {
      // Bildfläche löschen
      g.setColor(Color.lightGray);
      g.fillRect(cUpperLeft.x, cUpperLeft.y, cLowerRight.x, cLowerRight.y);

      // Gitternetz-Linien Zeichnen?!
      if (dGridSize <= 0) return;

      double aX = oUpperLeft .x.Re()/dGridSize; aX = (aX < 0) ? Math.ceil(aX) : Math.floor(aX); aX *= dGridSize;
      double eX = oLowerRight.x.Re()/dGridSize; eX = (eX < 0) ? Math.ceil(eX) : Math.floor(eX); eX *= dGridSize;
      double aY = oUpperLeft .y.Re()/dGridSize; aY = (aY < 0) ? Math.ceil(aY) : Math.floor(aY); aY *= dGridSize;
      double eY = oLowerRight.y.Re()/dGridSize; eY = (eY < 0) ? Math.ceil(eY) : Math.floor(eY); eY *= dGridSize;

      for (double dY=aY; dY<=eY; dY+=dGridSize) {
         JPoint jPt1  = new JPointSimple(aX, dY, 1);
         JPoint jPt2  = new JPointSimple(eX, dY, 1);
         JLine  jLine = new JLineConnected(jPt1, jPt2, dY == 0 ? Color.darkGray : Color.gray);
         jLine.draw(g, this, dY == 0 ? Color.gray : null);
      }
      for (double dX=aX; dX<=eX; dX+=dGridSize) {
         JPoint jPt1  = new JPointSimple(dX, aY, 1);
         JPoint jPt2  = new JPointSimple(dX, eY, 1);
         JLine  jLine = new JLineConnected(jPt1, jPt2, dX == 0 ? Color.darkGray : Color.gray);

         jLine.draw(g, this, dX == 0 ? Color.gray : null);
      }
   }

   // Funktion zum Zeichnen von Punkten im jeweilige Anzeigemodus
   public void draw(Graphics g, JPoint jPoint, Color cSelect) {
      // Punkte in Bildschirmkoordinaten umrechnen
      JCanvasVector cPt  = toCanvas(jPoint.xyz);
      if (          cPt == null) return;

      if (jPoint.degreeOfFreedom() > 0) {
         // freie Punkte kenntlich machen
         g.setColor(jPoint.getColor());
         g.fillOval(cPt.x - 8, cPt.y - 8, 16, 16);
      }
      
      if (jPoint.degreeOfFreedom() > 0 || cSelect != null) {
         // Punkt selektiert
         g.setColor(cSelect != null ? cSelect : Color.lightGray); // Markierung mit cSelect oder übermalen mit Hintergrund
         g.fillOval(cPt.x - 7, cPt.y - 7, 14, 14);
      }

         // Punkt einzeichnen
         g.setColor(jPoint.getColor()); 
         g.fillOval(cPt.x - 5, cPt.y - 5, 10, 10);

      // Imaginäre Koordinaten? -> Not Yet Implemented!
      // ...

         g.setColor(Color.black);
         g.drawString(jPoint.getName(), cPt.x+5, cPt.y+15);
   }

   // Funktion zum Zeichnen von Geraden im jeweilige Anzeigemodus
   public void draw(Graphics g, JLine  jLine , Color cSelect) {
      // Linie auf Bildschirm einordnen
      int iMask = classifyRect(jLine, pUpperLeft, pUpperRight, pLowerRight, pLowerLeft);

      // Nur die erforderlichen Schnittpunkte berechnen
      JPoint pUp = ((iMask & (fUP | sUP)) != 0) ? new JPointIntersection(lTop   , jLine) : null;
      JPoint pLo = ((iMask & (fLO | sLO)) != 0) ? new JPointIntersection(lBottom, jLine) : null;
      JPoint pLe = ((iMask & (fLE | sLE)) != 0) ? new JPointIntersection(lLeft  , jLine) : null;
      JPoint pRi = ((iMask & (fRI | sRI)) != 0) ? new JPointIntersection(lRight , jLine) : null;

      // Schnittpunkte in Canvas-Koordinaten umrechnen
      JCanvasVector cUp = (pUp != null) ? toCanvas(pUp.xyz) : null;
      JCanvasVector cLo = (pLo != null) ? toCanvas(pLo.xyz) : null;
      JCanvasVector cLe = (pLe != null) ? toCanvas(pLe.xyz) : null;
      JCanvasVector cRi = (pRi != null) ? toCanvas(pRi.xyz) : null;

      // Die 2 Punkte, die nicht (null) sind herausfiltern
      JCanvasVector c1  = (cUp != null) ? cUp : ((cRi != null) ? cRi : cLo);
      JCanvasVector c2  = (cLe != null) ? cLe : ((cLo != null) ? cLo : cRi);

      if (c1 != null && c2 != null) {
         // Linie zeichnen
         if (cSelect != null) { 
            // Nicht sehr effektiv, aber einfach!!!
            g.setColor(cSelect); 
            g.drawLine(c1.x+1, c1.y+1, c2.x+1, c2.y+1); 
            g.drawLine(c1.x+1, c1.y  , c2.x+1, c2.y  ); 
            g.drawLine(c1.x+1, c1.y-1, c2.x+1, c2.y-1); 
            g.drawLine(c1.x  , c1.y+1, c2.x  , c2.y+1); 
            g.drawLine(c1.x  , c1.y-1, c2.x  , c2.y-1); 
            g.drawLine(c1.x-1, c1.y+1, c2.x-1, c2.y+1); 
            g.drawLine(c1.x-1, c1.y  , c2.x-1, c2.y  ); 
            g.drawLine(c1.x-1, c1.y-1, c2.x-1, c2.y-1); 
         }
            g.setColor(jLine.getColor() ); 
            g.drawLine(c1.x  , c1.y  , c2.x  , c2.y  ); 
         
         // Wenn die Gerade einen Namen hat, diesen ausgeben
         if (jLine.getName() != "") {
            // Position für den Namen berechnen
            JCanvasVector  cn  = calcNamePos(g, jLine);
            if (           cn != null  ) {
               FontMetrics fm  = g.getFontMetrics();
                           cn  = new JCanvasVector( cn.x - fm.stringWidth(jLine.getName()) / 2
                                                  , cn.y + fm.getHeight()                  / 2);
               g.setColor(Color.black);
               g.drawString(jLine.getName(), cn.x, cn.y);
            }
         }
      }
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
      boolean bLeft2Right = Math.abs(jConic.M.a.Len()) < Math.abs(jConic.M.e.Len());

      // Nimm die Punkte auf (keine eigentlichen Polygone, da sowohl Anfangs- als auch Endpunkt gespeichert wird)
      Polygon pOnePoly = new Polygon();
      Polygon pTwoPoly = new Polygon();

      // Standardschrittweite
      int  iStep  = 8;
      for (int v  = (bLeft2Right ? cUpperLeft .x : cUpperLeft .y); 
               v <  (bLeft2Right ? cLowerRight.x : cLowerRight.y) + iStep; 
               v += iStep) {

         JObjectVector oP1 = toObject(new JCanvasVector(bLeft2Right ? v : cUpperLeft .y, 
                                                        bLeft2Right ? cUpperLeft .x : v));
         JObjectVector oP2 = toObject(new JCanvasVector(bLeft2Right ? v : cLowerRight.y, 
                                                        bLeft2Right ? cLowerRight.x : v));
         // Rasterlinie mit Kurve schneiden
         JLine lLine = new JLineConnected(new JPointSimple(oP1), new JPointSimple(oP2));

         // Beide Schnittpunkte berechnen (könnte noch optimiert werden -> doppelte Berechnung!)
              p1  = new JPointConicLine(jConic, lLine, 0);
              p2  = new JPointConicLine(jConic, lLine, 1);

         // Schnittpunkte in Bildschirmkoordinaten
             cP1  = toCanvas( p1.xyz );
             cP2  = toCanvas( p2.xyz );
             
         // Kurvenabschnitt (wenn gewünscht) zeichnen
         boolean  bFirstLineOK = cO1 != null && cP1 != null && contains(cP1) && p1.xyz.isReal();
         boolean bSecondLineOK = cO1 != null && cP2 != null && contains(cP2) && p2.xyz.isReal();

         // Versuche zuerst die Schritte zu verkleinern
         if (( bFirstPrevOK ^  bFirstLineOK) || 
             (bSecondPrevOK ^ bSecondLineOK)) {
            if (iStep > 1) {
               v -= iStep;
                    iStep = java.lang.Math.max(1, iStep/2);
               continue;
            }
         } else {
                    iStep = java.lang.Math.min(8, iStep*2);
         }

         if ( bFirstLineOK ||  bFirstPrevOK) {
            pOnePoly.addPoint(cO1.x, cO1.y);
            pOnePoly.addPoint(cP1.x, cP1.y);
         }
         if (bSecondLineOK || bSecondPrevOK) {
            pTwoPoly.addPoint(cO2.x, cO2.y);
            pTwoPoly.addPoint(cP2.x, cP2.y);
         }

         // Letzten Kurvenabschnittspunkt merken (für nächsten Abschnitt)
             cO1  = cP1;     o1 = p1;
             cO2  = cP2;     o2 = p2;

          bFirstPrevOK =  bFirstLineOK;
         bSecondPrevOK = bSecondLineOK;
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
   public JObjectVector toObject(JCanvasVector cPt) {
      // Sicherheitsabfrage
      if (cPt == null) return null;

      // Koordinaten umrechnen
      double x = ((double)(cPt.x-cUpperLeft.x)) / (cLowerRight.x     -cUpperLeft.x     ) 
                                                * (oLowerRight.x.Re()-oUpperLeft.x.Re()) + oUpperLeft.x.Re();

      double y = ((double)(cPt.y-cUpperLeft.y)) / (cLowerRight.y     -cUpperLeft.y     ) 
                                                * (oLowerRight.y.Re()-oUpperLeft.y.Re()) + oUpperLeft.y.Re();

      return new JObjectVector( x, y, 1 );
   }

   // Umwandlung von Objekt- in Bildschirmkoordinaten
   public JCanvasVector toCanvas(JObjectVector oPt) {
      // Sicherheitsabfrage
      if (oPt == null) return null;

      // Punkt auf die Ebene kürzen (nicht mit dem Original arbeiten)
      oPt = oPt.copy();
            oPt.toLevel();

      // Punkt liegt im Unendlichen oder ist ungültig?!...
      // ...
      if (oPt.z.Len() == 0.0) return null;
      
      // Koordinaten befinden sich in oPt.x und oPt.y
      // Koordinaten umrechnen (??? Was passiert bei imaginären Anteilen ???)
      double x = ((double)(oPt.x.Re()-oUpperLeft.x.Re())) / (oLowerRight.x.Re()-oUpperLeft.x.Re()) 
                                                          * (cLowerRight.x     -cUpperLeft.x     ) + cUpperLeft.x;
      double y = ((double)(oPt.y.Re()-oUpperLeft.y.Re())) / (oLowerRight.y.Re()-oUpperLeft.y.Re()) 
                                                          * (cLowerRight.y     -cUpperLeft.y     ) + cUpperLeft.y;

      return new JCanvasVector( (int)x, (int)y );
   }

   // *********************************************************************************
   // ***************                Private Hilfsfunktionen            ***************
   // *********************************************************************************

   // Hilfskonstanten zur Linienzeichnung
   final int fLE = 1;   final int sLE =  16;
   final int fUP = 2;   final int sUP =  32;
   final int fLO = 4;   final int sLO =  64;
   final int fRI = 8;   final int sRI = 128;

   // Berechne die Position des Geraden-Namens im Euklidischen Modus
   private JCanvasVector calcNamePos(Graphics g, JLine jLine) {

      // Bildschirmrechteck um 10 Pixel verkleinern
      JPoint pUL  = new JPointSimple(toObject(new JCanvasVector(cUpperLeft .x+10, cUpperLeft .y+10)));      
      JPoint pUR  = new JPointSimple(toObject(new JCanvasVector(cLowerRight.x-10, cUpperLeft .y+10)));      
      JPoint pLR  = new JPointSimple(toObject(new JCanvasVector(cLowerRight.x-10, cLowerRight.y-10)));      
      JPoint pLL  = new JPointSimple(toObject(new JCanvasVector(cUpperLeft .x+10, cLowerRight.y-10)));       

      // Einen Punkt auf der Gerade auswählen und Position auf dem Schirm bestimmen
      JPoint        pOn  = new JPointOnLine(jLine, pUL);
      JCanvasVector cOn  = toCanvas(pOn.xyz);
      if (          cOn == null) return null;

      // Erstelle einen Vector, der die Koordinate cOn um 10 Pixel entlang der Normale verschiebt
      JObjectVector oPl = new JObjectVector(jLine.abc.x, jLine.abc.y, Complex.Zero);
      if (          oPl.len().Len() != 0   )
                    oPl.div(oPl.len());
                    cOn.add(new JCanvasVector((int)(oPl.x.Re()*10), 
                                              (int)(oPl.y.Re()*10)));
      
      // Der neue Punkt liegt 10 Pixel neben der Geraden (hoffentlich ;-)
      JPoint pOff = new JPointSimple(toObject(cOn));

      // Parallele zur aktuellen Gerade durch den neuberechneten Punkt pOff
      JLine  lOff = new JLineParallel(jLine, pOff);

      // Linie auf Bildschirm einordnen
      int   iMask = classifyRect(lOff, pUL, pUR, pLR, pLL);

      // Nur die erforderliche Bildschirmseite berechnen (nur eine wird benötigt)
      JLine  lUp  = ((iMask & fUP) != 0) ? new JLineConnected(pUL, pUR) : null;
      JLine  lRi  = ((iMask & fRI) != 0) ? new JLineConnected(pUR, pLR) : null;
      JLine  lLo  = ((iMask & fLO) != 0) ? new JLineConnected(pLR, pLL) : null;
      JLine  lLe  = ((iMask & fLE) != 0) ? new JLineConnected(pLL, pUL) : null;

      // Berechne Schnittpunkt(e) der Geraden mit der Bildschirmkante, die nötig ist
      JPoint pUp  = (lUp != null) ? new JPointIntersection(lUp, lOff) : null;
      JPoint pRi  = (lRi != null) ? new JPointIntersection(lRi, lOff) : null;
      JPoint pLo  = (lLo != null) ? new JPointIntersection(lLo, lOff) : null;
      JPoint pLe  = (lLe != null) ? new JPointIntersection(lLe, lOff) : null;

      // Berechne die Bildschirmkoordinate des berechneten Schnittpunkts
      JCanvasVector cUp = (pUp != null) ? toCanvas(pUp.xyz) : null;
      JCanvasVector cRi = (pRi != null) ? toCanvas(pRi.xyz) : null;
      JCanvasVector cLo = (pLo != null) ? toCanvas(pLo.xyz) : null;
      JCanvasVector cLe = (pLe != null) ? toCanvas(pLe.xyz) : null;

      // Wähle aktiven Punkt und gib die berechnete Position des Namens zurück
           if (cUp != null) return cUp;
      else if (cRi != null) return cRi;
      else if (cLo != null) return cLo;
      else if (cLe != null) return cLe;
      else                  return null;
   }

   // Wie liegt die übergebene Gerade im übergebenen Viereck
   // Die Rückgabe sind die 2 entscheidenden Ränder
   // Der "erste"  Rand durch die f.. Konstanten
   // Der "zweite" Rand durch die s.. Konstanten
   private int classifyRect(JLine l, JPoint pUL, JPoint pUR, JPoint pLR, JPoint pLL) {

      // Berechne Schnittpunkt(e) der Geraden mit den Bildschirmkanten
      boolean bUL = JObjectVector.doScalar(pUL.xyz, l.abc).Re() >= 0;
      boolean bUR = JObjectVector.doScalar(pUR.xyz, l.abc).Re() >= 0;
      boolean bLR = JObjectVector.doScalar(pLR.xyz, l.abc).Re() >= 0;
      boolean bLL = JObjectVector.doScalar(pLL.xyz, l.abc).Re() >= 0;
      
      int iMask = 0;

           if (!bUL && !bUR && !bLL && !bLR) iMask = 0;     // außerhalb
      else if ( bUL &&  bUR &&  bLL &&  bLR) iMask = 0;     // außerhalb
      else if (!bUL &&  bUR &&  bLL && !bLR) iMask = 0;     // nicht möglich
      else if ( bUL && !bUR && !bLL &&  bLR) iMask = 0;     // nicht möglich

      else if ( bUL && !bUR && !bLL && !bLR) iMask = fLE | sUP;
      else if ( bUL &&  bUR && !bLL && !bLR) iMask = fLE | sRI;
      else if ( bUL &&  bUR && !bLL &&  bLR) iMask = fLE | sLO;
      
      else if (!bUL &&  bUR && !bLL && !bLR) iMask = fUP | sRI;
      else if (!bUL &&  bUR && !bLL &&  bLR) iMask = fUP | sLO;
      else if (!bUL &&  bUR &&  bLL &&  bLR) iMask = fUP | sLE;
      
      else if (!bUL && !bUR &&  bLL && !bLR) iMask = fLO | sLE;
      else if ( bUL && !bUR &&  bLL && !bLR) iMask = fLO | sUP;
      else if ( bUL &&  bUR &&  bLL && !bLR) iMask = fLO | sRI;
      
      else if (!bUL && !bUR && !bLL &&  bLR) iMask = fRI | sLO;
      else if (!bUL && !bUR &&  bLL &&  bLR) iMask = fRI | sLE;
      else if ( bUL && !bUR &&  bLL &&  bLR) iMask = fRI | sUP;

      return iMask;
   }
}
