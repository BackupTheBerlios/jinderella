
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import java.util.Random;

// Klasse zum "double-buffern" der Anzeige
class DoubleBufferPanel extends Panel {

   Image BackBuffer;

   public void update(Graphics g) {

      Graphics gr; 

      if (   BackBuffer == null                              ||
          (!(BackBuffer.getWidth (this) == this.getSize().width &&  
             BackBuffer.getHeight(this) == this.getSize().height) ) ) {

         BackBuffer = this.createImage(getSize().width, getSize().height);
      }
      
      // We need to use our buffer Image as a Graphics object:
            gr = BackBuffer.getGraphics();
      paint(gr);

      g.drawImage(BackBuffer, 0, 0, this);
   }
}

// ******************************************
// * Implementierung der Benutzeroberfläche *
// ******************************************
class JinderellaPanel extends DoubleBufferPanel implements MouseListener, MouseMotionListener {

   Random rnd = new Random();

   // Konstruktor
   public JinderellaPanel() {
      setBackground(Color.white);
      addMouseMotionListener(this);
      addMouseListener(this);
   }
   
   // -----------------------
   // Verschieden Zeichenmodi
   // -----------------------
   public static final int INTERACT = 0;
   public static final int POINT    = 1;
   public static final int SECTION  = 2;
   public static final int LINE     = 3;
   public static final int VERTICAL = 4;
   public static final int PARALLEL = 5;
   public static final int CONIC    = 6;
   public static final int CIRCLE   = 7;
   public static final int CENTER   = 8;
                       int Mode     = INTERACT;
   
   public void setDrawMode(int Mode) {
      this.Mode   = Mode;
      this.aPoint = null;
      this.aLine  = null;
      this.aConic = null;
   }
   // -----------------------
   
   // ---------------------------------------------
   // Verwaltung der Objekte (Linien, Punkte, etc.)
   // ---------------------------------------------
   Vector jObjects = new Vector();
   Vector jSelects = new Vector();

   // Halte Konstruktion immer in schlüssigem Zustand
   public void doUpdate() {
      boolean bRepeat;

      // Der aktuelle Punkt soll immer aktuell sein! (Wir hätten ja auch Intersection-Points verschieben können)
      if (aPoint != null)
          aPoint.doUpdate();
      do {
         bRepeat = false;
         
         // Nur die geänderten betrachten
         for (int i=-3; i<jObjects.size(); i++) {
            JObject  jOb1 = null;

            switch (i) {
            case -3: jOb1 = aPoint;                         break;
            case -2: jOb1 = aLine;                          break;
            case -1: jOb1 = aConic;                         break;
            default: jOb1 = (JObject)jObjects.elementAt(i); break;
            }
            if (     jOb1 == null) continue;

            // Suche geändertes Objekt
            if (    !jOb1.isChanged() &&
                     jOb1.isUp2Date() ) continue;

            // Die geänderten abhängigen Updaten - interne Objekte werden aktualisiert!
            if (    !jOb1.isUp2Date() ) {
                     jOb1.doUpdate(); 
                     jOb1.setChanged(true);
                     bRepeat = true;
            }

            // Ein geändertes Objekt gefunden!!!
            for (int j=-3; j<jObjects.size(); j++) if (j != i) {
               JObject  jOb2 = null;

               switch (j) {
               case -3: jOb2 = aPoint;                         break;
               case -2: jOb2 = aLine;                          break;
               case -1: jOb2 = aConic;                         break;
               default: jOb2 = (JObject)jObjects.elementAt(j); break;
               }
               if (     jOb2 == null) continue;

               if (     jOb2.dependsOn(jOb1, false)) {
                        jOb2.doUpdate();
                        jOb2.setChanged(true);
                        bRepeat = true;
               }
            }
            // Die Änderungen dieses Objekts wurden propagiert!
            jOb1.setChanged(false);
         }
      } while (bRepeat);
   }
   // ---------------------------------------------

   // Zwischenspeicher, zur Kennzeichnung der aktuellen Aktion (je nach Modus)
   JPoint aFirst = null;      // Temporärer Speicher für Geraden
   JPoint aPoint = null;
   JLine  aLine  = null;
   JConic aConic = null;

   // --------------------------------
   // Verwaltung der Bildschirmanzeige
   // --------------------------------
   JMapping  mEuklid = new JMappingEuklid  ( 0.5
                                           , new JObjectVector(-2.5, -2.5, 1.0), new JObjectVector( 2.5, 2.5, 1.0)
                                           , new JCanvasVector(   2,    3     ), new JCanvasVector( 502, 503     ) );
   JMapping  mSphere = new JMappingSphere2D( 0.9
                                           , new JCanvasVector( 512,    3     ), new JCanvasVector(1012, 503     ) );
// JMapping  mSphere = new JMappingSphere3D( 5.0, 12.5
//                                         , new JCanvasVector( 512,    3     ), new JCanvasVector(1012, 503     ) );

   // Abstand im euklidischen Raum zum Einrasten an bspw. vorhandenen Objekten
   static final double dSnapCanvas = 12.50;
   static final double dSnapGrid   =  6.00;

   // *************************************
   // * Realisierung der Einrastfunktion! *
   // *************************************

   // Berechne Punkt, in den man Einrasten kann, wenn ein solcher existieren sollte!
   public JPoint CalcPointSnap(JMapping Map, JCanvasVector cPt) {
      
      // Letzte Punkte zuerst (obere Punkte werden zuletzt gezeichnet -> sichtbar)
      for (int iTyp = 2; iTyp >= 0; iTyp--) {
         for (int i=jObjects.size()-1; i>=0; i--) {
            JObject jOb = (JObject)jObjects.elementAt(i);
         
            // Für den Fall eines Punktes in der Nähe, dann Punkt zurückgeben
            if (!(jOb instanceof JPoint)) continue;

            Complex d      = ((JPoint)jOb).calcDistance(Map, cPt);
            if (    d     != null       &&
                    d.Re() < dSnapCanvas  ) {
               
               // Sorge dafür das freie Punkte bevorzugt werden
               JPoint pNow = (JPoint)jOb;
               if (   pNow.degreeOfFreedom() >= iTyp  ) return pNow;
            }
         }
      }
      // Kein Punkt gefunden -> null zurückgeben
      return null;
   }

   // --------------------------------
   // ---   Einrasten an Geraden   ---
   // --------------------------------

   // Vektorverbund: 
   //        wert[i] ist die Distanz der Gerade line[i] zu einem bestimmten Punkt
   Vector jLWerte = new Vector();                     // Distanz eines Punktes zur unteren Gerade
   Vector jLines  = new Vector();                     // Gerade zur obigen Distanz
   
   // Auffüllen des Vektorverbunds (wert, line) mit Referenzpunkt (x,y) in Objekt-Koordinaten
   public void SortLineDistance(JMapping Map, JCanvasVector cPt) {
      // Alle Daten zurücksetzen
      jLWerte.removeAllElements();
      jLines .removeAllElements();

      // Speichern der Geraden, samt dem Abstand zum Punkt (x,y)
      for (int i=0; i<jObjects.size(); i++) {
         JObject jOb = (JObject)jObjects.elementAt(i);
         if (  !(jOb instanceof JLine)) continue;
         
         // Im Interaktiven Modus sollte die Linie, dessen Punkt(e) verschoben werden, ausgelassen werden
         if (    jOb.dependsOn(aPoint) && Mode == INTERACT ) continue;
         
         Complex d  = ((JLine)jOb).calcDistance(Map, cPt);
         if (    d != null   ) {
            // Werte speichern
            jLines .addElement(jOb);
            jLWerte.addElement(d  );
         }
      }
      // Ordnen der Geraden nach Abstand zur aktuellen Position (quasi-Bubblesort)
      for (int i=0; i<jLWerte.size(); i++) {
         for (int j=i+1; j<jLWerte.size(); j++) {
            Complex d1 = (Complex)jLWerte.elementAt(i);
            Complex d2 = (Complex)jLWerte.elementAt(j);
            JLine   l2 = (JLine  )jLines .elementAt(j);

            // Wann ist der komplexe Abstand am größten?
            if (d1.Re() > d2.Re()) {
               // Einträge tauschen (Pseudotauschen: nur Reihenfolge j vor i)
               jLWerte.removeElementAt(j);   jLWerte.insertElementAt(d2, i);
               jLines .removeElementAt(j);   jLines .insertElementAt(l2, i);
            }
         }
      }
      // nach Abstand geordnete Geraden sind nun in (wert, line) verfügbar
      // => siehe CalcIntersection
   }

   // Berechne Schnittpunkt 2-er Geraden, in den man Einrasten kann
   // ACHTUNG: Der Vektorverbund (wert, line) muß initialisiert sein
   public JPoint CalcLineAndLine(JMapping Map, JCanvasVector cPt) {
      // Suche eine Gerade, mit weniger als "dSnapDelta" Abstand (-> kommt in Frage)
      for (int i=0; i<jLWerte.size(); i++) {
         Complex d1 = (Complex)jLWerte.elementAt(i);
         // Zu großer Abstand -> Schleife beenden
         if (d1.Re() > dSnapCanvas) break;

         // Suche zweite Gerade, mit weniger als "dSnapDelta" Abstand (-> kommt auch in Frage)
         for (int j=i+1; j<jLWerte.size(); j++) {
            Complex d2 = (Complex)jLWerte.elementAt(j);
            // Zu großer Abstand -> Schleife beenden
            if (d2.Re() > dSnapCanvas) break;

            switch (Mode) {
            case INTERACT: 
               // Wenn noch nichts gewählt wurde erstellen wir keine neuen Objekte (-> interaktiver Modus)
               if (aPoint == null) return null;

            default:
               // Nah genug dran? -> Punkt liefern (neuer Punkt wird generiert!!!)
               // Berechne den Schnittpunkt der beiden Geraden
               JPoint pXY = new JPointIntersection((JLine)jLines.elementAt(i)
                                                  ,(JLine)jLines.elementAt(j), getForeground());

               Complex d      = pXY.calcDistance(Map, cPt);
               if (    d     != null &&
                       d.Re() < dSnapCanvas) return pXY;
            }
         }
      }
      // Keinen Schnittpunkt gefunden
      return null;
   }

   // Berechne, ob eine Gerade nahe genug anliegt, so daß man einrasten kann
   // ACHTUNG: Der Vektorverbund (wert, line) muß initialisiert sein
   public JPoint CalcPointOnLine(JMapping Map, JCanvasVector cPt) {
      // Nimm nächste Gerade
      if (jLWerte.size() > 0) {
         Complex d = (Complex)jLWerte.elementAt(0);
         
         // Gerade nah genug dran, dann Streckenpunkt erstellen
         if (d.Re() < dSnapCanvas) {
            JLine         l   = (JLine)jLines.elementAt(0);
            JObjectVector oPt = Map.toObject(cPt);

            switch (Mode) {
            case INTERACT: 
               // Im Interaktiven Modus muß der interne Punkt der Linie zurückgegeben werden, wenn noch kein Objekt gewählt wurde!
               if (aPoint == null) {
                  // Wenn noch nichts gewählt wurde erstellen wir keine neuen Objekte (-> interaktiver Modus)
                  if (l instanceof JLineAscending) return ((JLineAscending)l).pGrad;
                                              else return null;
               }

            default:
               // In allen anderen Fällen wird ein Punkt auf der Gerade erstellt und zurückgegeben!
               return new JPointOnLine(l, oPt, getForeground());
            }
         }
      }
      // Keine passende Gerade gefunden
      return null;
   }
   
   // --------------------------------
   // - Einrasten an Kegelschnitten! -
   // --------------------------------

   // Vektorverbund: 
   //        wert[i] ist die Distanz der Gerade line[i] zu einem bestimmten Punkt
   Vector jCWerte = new Vector();                     // Distanz eines Punktes zum unteren Kegelschnitt
   Vector jConics = new Vector();                     // Kegelschnitt zur obigen Distanz
   
   // Auffüllen des Vektorverbunds (wert, line) mit Referenzpunkt (x,y) in Objekt-Koordinaten
   public void SortConicDistance(JMapping Map, JCanvasVector cPt) {
      // Alle Daten zurücksetzen
      jCWerte.removeAllElements();
      jConics.removeAllElements();

      // Speichern der Geraden, samt dem Abstand zum Punkt (x,y)
      for (int i=0; i<jObjects.size(); i++) {
         JObject jOb = (JObject)jObjects.elementAt(i);
         if (  !(jOb instanceof JConic)) continue;
         
         // Im Interaktiven Modus sollte die Linie, dessen Punkt(e) verschoben werden, ausgelassen werden
         if (    jOb.dependsOn(aPoint) && Mode == INTERACT ) continue;
         
         Complex d  = ((JConic)jOb).calcDistance(Map, cPt);
         if (    d != null   ) {
            // Werte speichern
            jConics.addElement(jOb);
            jCWerte.addElement(d  );
         }
      }
      // Ordnen der Geraden nach Abstand zur aktuellen Position (quasi-Bubblesort)
      for (int i=0; i<jCWerte.size(); i++) {
         for (int j=i+1; j<jCWerte.size(); j++) {
            Complex d1 = (Complex)jCWerte.elementAt(i);
            Complex d2 = (Complex)jCWerte.elementAt(j);
            JConic  l2 = (JConic )jConics.elementAt(j);

            // Wann ist der komplexe Abstand am größten?
            if (d1.Re() > d2.Re()) {
               // Einträge tauschen (Pseudotauschen: nur Reihenfolge j vor i)
               jCWerte.removeElementAt(j);   jCWerte.insertElementAt(d2, i);
               jConics.removeElementAt(j);   jConics.insertElementAt(l2, i);
            }
         }
      }
      // nach Abstand geordnete Geraden sind nun in (wert, line) verfügbar
      // => siehe CalcIntersection
   }

   // Berechne Schnittpunkt einer Gerade mit einer Kurve
   public JPoint CalcConicAndLine(JMapping Map, JCanvasVector cPt) {
      // Suche einen Kegelschnitt, mit weniger als "dSnapDelta" Abstand (-> kommt in Frage)
      for (int i=0; i<jCWerte.size(); i++) {
         Complex d1 = (Complex)jCWerte.elementAt(i);
         // Zu großer Abstand -> Schleife beenden
         if (d1.Re() > dSnapCanvas) break;

         // Suche eine Gerade, mit weniger als "dSnapDelta" Abstand (-> kommt auch in Frage)
         for (int j=0; j<jLWerte.size(); j++) {
            Complex d2 = (Complex)jLWerte.elementAt(j);
            // Zu großer Abstand -> Schleife beenden
            if (d2.Re() > dSnapCanvas) break;

            JConic jConic = (JConic)jConics.elementAt(i);
            JLine  jLine  = (JLine )jLines .elementAt(j);

            switch (Mode) {
            case INTERACT: 
               // Wenn noch nichts gewählt wurde erstellen wir keine neuen Objekte (-> interaktiver Modus)
               if (aPoint == null) return null;

            default:
               // Nah genug dran? -> Punkt liefern (neuer Punkt wird generiert!!!)
               // Berechne den Schnittpunkt der beiden Geraden
               JPoint pXY1 = new JPointConicLine(jConic, jLine, 0, getForeground());
               JPoint pXY2 = new JPointConicLine(jConic, jLine, 1, getForeground());

               Complex d      = pXY1.calcDistance(Map, cPt);
               if (    d     != null &&
                       d.Re() < dSnapCanvas) return pXY1;

                       d      = pXY2.calcDistance(Map, cPt);
               if (    d     != null &&
                       d.Re() < dSnapCanvas) return pXY2;
            }
         }
      }
      // Keinen Schnittpunkt gefunden
      return null;
   }

   // Berechne Schnittpunkt zweier Kegelschnitte
   public JPoint CalcConicAndConic(JMapping Map, JCanvasVector cPt) {
      // Suche einen Kegelschnitt, mit weniger als "dSnapDelta" Abstand (-> kommt in Frage)
      for (int i=0; i<jCWerte.size(); i++) {
         Complex d1 = (Complex)jCWerte.elementAt(i);
         // Zu großer Abstand -> Schleife beenden
         if (d1.Re() > dSnapCanvas) break;

         // Suche zweiten Kegelschnitt, mit weniger als "dSnapDelta" Abstand (-> kommt auch in Frage)
         for (int j=i+1; j<jCWerte.size(); j++) {
            Complex d2 = (Complex)jCWerte.elementAt(j);
            // Zu großer Abstand -> Schleife beenden
            if (d2.Re() > dSnapCanvas) break;

            JConic jConic1 = (JConic)jConics.elementAt(i);
            JConic jConic2 = (JConic)jConics.elementAt(j);

            switch (Mode) {
            case INTERACT: 
               // Wenn noch nichts gewählt wurde erstellen wir keine neuen Objekte (-> interaktiver Modus)
               if (aPoint == null) return null;

            default:
               // Nah genug dran? -> Punkt liefern (neuer Punkt wird generiert!!!)
               // Berechne den Schnittpunkt der beiden Geraden
               JPoint pXY1 = new JPointConicConic(jConic1, jConic2, 0, getForeground());
               JPoint pXY2 = new JPointConicConic(jConic1, jConic2, 1, getForeground());
               JPoint pXY3 = new JPointConicConic(jConic1, jConic2, 2, getForeground());
               JPoint pXY4 = new JPointConicConic(jConic1, jConic2, 3, getForeground());

               Complex d      = pXY1.calcDistance(Map, cPt);
               if (    d     != null &&
                       d.Re() < dSnapCanvas) return pXY1;

                       d      = pXY2.calcDistance(Map, cPt);
               if (    d     != null &&
                       d.Re() < dSnapCanvas) return pXY2;

                       d      = pXY3.calcDistance(Map, cPt);
               if (    d     != null &&
                       d.Re() < dSnapCanvas) return pXY3;

                       d      = pXY4.calcDistance(Map, cPt);
               if (    d     != null &&
                       d.Re() < dSnapCanvas) return pXY4;
            }
         }
      }
      // Keinen Schnittpunkt gefunden
      return null;
   }

   // Berechne, ob eine Gerade nahe genug anliegt, so daß man einrasten kann
   // ACHTUNG: Der Vektorverbund (wert, line) muß initialisiert sein
   public JPoint CalcPointOnConic(JMapping Map, JCanvasVector cPt) {
      // Nimm nächste Gerade
      if (jCWerte.size() > 0) {
         Complex d = (Complex)jCWerte.elementAt(0);
         
         // Gerade nah genug dran, dann Streckenpunkt erstellen
         if (d.Re() < dSnapCanvas) {
            JConic        c   = (JConic)jConics.elementAt(0);
            JObjectVector oPt = Map.toObject(cPt);

            switch (Mode) {
            case INTERACT: 
               // Im Interaktiven Modus muß der interne Punkt der Linie zurückgegeben werden, wenn noch kein Objekt gewählt wurde!
               if (aPoint == null) return null;

            default:
               // In allen anderen Fällen wird ein Punkt auf der Gerade erstellt und zurückgegeben!
               return new JPointOnConic(c, oPt, getForeground());
            }
         }
      }
      // Keine passende Gerade gefunden
      return null;
   }
   
   // --------------------------------
   // - Hauptfunktion zum Einrasten! -
   // --------------------------------
   public JPoint SnapToObject(JMapping Map, JCanvasVector cPt) {
      // Sicherheitsabfrage
      if (cPt == null) return null;

      // Eventuelles Objekt fürs Einrasten (z.B. Punkt oder Schnittpunkt)
      JPoint jSnap = null;

      // *********************************
      // Übereinstimmung mit Punkten
      if (jSnap == null) jSnap = CalcPointSnap    (Map, cPt);

      // Schnittpunkte mit Geraden und Kurven
      if (jSnap == null)         SortLineDistance (Map, cPt);
      if (jSnap == null)         SortConicDistance(Map, cPt);

      // Schnittpunkte
      if (jSnap == null) jSnap = CalcLineAndLine  (Map, cPt);
      if (jSnap == null) jSnap = CalcConicAndConic(Map, cPt);
      if (jSnap == null) jSnap = CalcConicAndLine (Map, cPt);

      // Projektionen
      if (jSnap == null) jSnap = CalcPointOnLine  (Map, cPt);
      if (jSnap == null) jSnap = CalcPointOnConic (Map, cPt);
      // *********************************

      // Objekt zum Einrasten (oder null) zurückliefern
      return jSnap;
   }

   public boolean switchSelection(JObject pOb) {
      if (pOb == null) return false;

      // Alle leeren
      if (jSelects.contains(pOb))
          jSelects.removeElement(pOb);
      else 
          jSelects.addElement(pOb);
      return true;
   }

   public JObject hitSelection(JMapping Map, JCanvasVector cPt) {
      // Sicherheitsabfrage
      if (cPt == null) return null;

      JPoint pSel  = CalcPointSnap(Map, cPt);
      if (   pSel != null ) 
         return pSel;

      // Geraden und Kegelschnitte nach Abstand sortieren
      SortLineDistance (Map, cPt);
      SortConicDistance(Map, cPt);

      // Keine Gerade da, dann kann man auch nicht selektieren
      if (jLWerte.size() != 0) {
         // Hole Abstand der nächsten Geraden
         Complex d = (Complex)jLWerte.elementAt(0);
         
         // Gerade nah genug dran, dann Gerade (de)selektieren
         if (d.Re() < dSnapCanvas) 
            return (JLine )jLines .elementAt(0);
      }
      if (jCWerte.size() != 0) {
         // Hole Abstand des nächsten Kegelschnitts
         Complex d = (Complex)jCWerte.elementAt(0);
         
         // Gerade nah genug dran, dann Gerade (de)selektieren
         if (d.Re() < dSnapCanvas) 
            return (JConic)jConics.elementAt(0);
      }
      return null;
   }

   // *************************************
   // *** Behandlung der Mausbewegungen ***
   // *************************************

   // Hilfsfunktionen zur Behandlung der Bewegungen im Modus (INTERACT)
   // -----------------------------------------------------------------
   public JPoint pushInteract(JPoint jSnap) {
      // Kein Objekt getroffen -> nichts machen
      if (jSnap == null) return null;

      // Bereits bekannte Punkte werden aus der Liste herausgelöst und zurückgegeben
      if (jObjects.contains(jSnap)) 
          jObjects.removeElement(jSnap);

      // Dieses Objekt zurückgeben (es ist ja nah genug an der Maus gewesen -> SnapToObject)
      return jSnap;
   }

   public JPoint dragInteract(JPoint jSnap, JObjectVector oPt, JPoint jMove, boolean bRelease) {
      // jSnap : Objekt in der Nähe...
      //       : 1. Kein Objekt gefunden     ->  null
      //       : 2. Bereits bekannter Punkt  -> !null && in Objektliste
      //       : 3. Intern verwendeter Punkt -> instanceof JPointInternal
      //       : 4. Erstellter Punkt         -> z.B. Schnittpunkt 2er Geraden etc.
      // oPt   : Aktuelle Position der Maus in Objekt-Koordinaten
      // jMove : Objekt das verändert werden soll...
      //       : 1. normaler Punkt           -> verschieben
      //       : 2. Intern verwendeter Punkt -> verschieben (wird in der Klasse gelöst)

      // Kein Objekt in Bewegung?
      if (jMove == null) return null;

      // Kein Objekt als Vorlage?
      if (jSnap == null) {
         // Keine Vorlage gefunden -> übernehmen der Mausposition...
         if (!bRelease) ((JPoint)jMove).setTo(oPt);
      } else {
         // Vorlage gefunden -> übernehmen der Position des Vorlageobjekts
         if (!bRelease) ((JPoint)jMove).moveTo(jSnap);
      }
      
      // Die normalen Punkte sollen am Ende wieder zur Liste hinzugefügt werden!
      if (bRelease && !(jMove instanceof JPointInternal)) 
          jObjects.addElement(jMove);

      // Bearbeitetes Objekt zurückgeben (in diesem Fall immer dasselbe wie am Anfang der Funktion)
      return jMove;
   }


   // Hilfsfunktionen zur Behandlung der Bewegungen im Modus (POINT)
   // -----------------------------------------------------------------
   public JPoint pushPoint(JPoint jSnap, JObjectVector oPt) {
      // jSnap : Objekt in der Nähe...
      //       : 1. Kein Objekt gefunden     ->  null
      //       : 2. Bereits bekannter Punkt  -> !null && in Objektliste
      //       : 3. Intern verwendeter Punkt -> instanceof JPointInternal
      //       : 4. Erstellter Punkt         -> z.B. Schnittpunkt 2er Geraden etc.
      // oPt   : Aktuelle Position der Maus in Objekt-Koordinaten
      
      JPoint jPush = null;    // Rückgabe-Objekt

             if (jSnap == null) {
         // ---------------------------------------------------------------------
         // Keine Vorlage gefunden -> erstelle neuen Punkt an der Mausposition...
         jPush = new JPointSimple(oPt, getForeground());

      } else if (jObjects.contains(jSnap)) {
         // ---------------------------------------------------------------------
         // Bereits bekannter Punkt -> Zeiger des bekannten Punkts zurückgeben (aber: später nicht verschieben!)
         jPush = jSnap;  //new JPointSimple(jSnap.xyz, jSnap.getColor());

      } else if (jSnap instanceof JPointInternal) {
         // ---------------------------------------------------------------------
         // Intern verwendeter Punkt (sollte eigentlich nie vorkommen) -> Auf Nummer sicher gehen - neuen Punkt erstellen!
         java.lang.System.err.println("DrawPanel.pushPoint: Unerwarteter Punkttyp!!!");
         jPush = new JPointSimple(oPt, getForeground());

      } else {
         // ---------------------------------------------------------------------
         // Anderweitig erstellter Punkt (z.B. Schnittpunkt) -> Zeiger übernehmen
         jPush = jSnap;
      }
      return jPush;     
   }

   public JPoint dragPoint(JPoint jSnap, JObjectVector oPt, JPoint jDrag, boolean bRelease) {
      // jSnap : Objekt in der Nähe...
      //       : 1. Kein Objekt gefunden     ->  null
      //       : 2. Bereits bekannter Punkt  -> !null && in Objektliste
      //       : 3. Intern verwendeter Punkt -> instanceof JPointInternal
      //       : 4. Erstellter Punkt         -> z.B. Schnittpunkt 2er Geraden etc.
      // oPt   : Aktuelle Position der Maus in Objekt-Koordinaten
      // jDrag : Objekt das verändert werden soll... (normaler Punkt)

      // Kein Objekt in Bearbeitung?
      if (jDrag == null) return null;

             if (jSnap == null) {
         // ---------------------------------------------------------------------
         // Keine Vorlage gefunden -> verschiebe aktives Objekt an die Mausposition...

         // Achtung: Bereits vorhandene Objekte oder abhängige Objekte sollen nicht verschoben werden! -> Kopie erstellen...
         if (jObjects.contains(jDrag) || jDrag.degreeOfFreedom() < 2) 
             jDrag = new JPointSimple(jDrag.xyz, getForeground());

         // Nun kann das Objekt in die neuen Koordinaten verschoben werden
         jDrag.setTo(oPt);
         
         // Im Falle des Endes der Operation, speichere den Punkt ab und gib ihm einen Namen!
         if (bRelease) {
                                jDrag.regPoint();
            jObjects.addElement(jDrag);
         }

      } else if (jObjects.contains(jSnap)) {
         // ---------------------------------------------------------------------
         // Bereits bekannter Punkt -> Zeiger des bekannten Punkts zurückgeben (aber: nicht in die Liste einfügen)
         jDrag = jSnap;

      } else if (jSnap instanceof JPointInternal) {
         // ---------------------------------------------------------------------
         // Intern verwendeter Punkt (sollte eigentlich nie vorkommen) -> Auf Nummer sicher gehen - Punkt verschieben!
         java.lang.System.err.println("DrawPanel.dragPoint: Unerwarteter Punkttyp!!!");
         jDrag.setTo(oPt);

      } else {
         // ---------------------------------------------------------------------
         // Anderweitig erstellter Punkt (z.B. Schnittpunkt) -> Zeiger übernehmen
         jDrag = jSnap; 

         // Im Falle des Endes der Operation, speichere den Punkt ab und gib ihm einen Namen!
         if (bRelease) {
                                jDrag.regPoint();
            jObjects.addElement(jDrag);
         }
      }
      return jDrag;
   }

   // *************************************

   // Zwischenspeicher
   private JCanvasVector cPt;       // aktuelle Mausposition in Bildschirmkoordinaten
   private JObjectVector oPt;       // aktuelle Mausposition in Objektkoordinaten (evtl. eingerastet am Gitternetz)
   
   private JPoint        jSnap;     // Objekt zum Einrasten, oder null!

   private JMapping      mActive;   // aktuelle Darstellung, die bearbeitet wird
   private boolean       bShift;    // ist die Umschalttaste während der Operation gedrückt
   private boolean       bUpdate;   // muß die Darstellung aktualisiert werden

   // Setzen der Zwischenspeichervariablen
   private boolean fillTemporary(MouseEvent e) {
      bShift  = e.isShiftDown();
      bUpdate = false;

      // Bildschirmposition
      cPt     = new JCanvasVector(e.getX(), e.getY());
      mActive = null;
      
      if (mEuklid.contains(cPt)) mActive = mEuklid;
      if (mSphere.contains(cPt)) mActive = mSphere;
      if (mActive == null) return false;

      // unterliegendes Objekt bzw. Gridposition ermitteln
      jSnap =     SnapToObject(mActive,           cPt);
      oPt   = mActive.toObject(mActive.snapToGrid(cPt, dSnapGrid));
      return true;
   }

   public void mousePressed(MouseEvent e) {
                         e.consume();
      if (!fillTemporary(e)) 
         return;
      
      // Selektionsbearbeitung (für verschiedene Modi kann das Selektionsverfahren verändert werden)
      JObject jHitSel = hitSelection(mActive, cPt);

      switch (Mode) {
         // Normalerweise alle Selektionen löschen
      case INTERACT:
              if (bShift || 
                 (jHitSel != null && !jSelects.contains(jHitSel))) bUpdate  |= switchSelection(jHitSel);
         else if (jHitSel == null)                               { bUpdate  |= jSelects.size() != 0;    
                                                                               jSelects.removeAllElements(); }
         break;

      case CIRCLE:
      case CONIC :
         // Befinden uns nun im Selektionsmodus -> nicht jedesmal die Selektion leeren
         bUpdate  |= switchSelection(jHitSel);
         break;
         
      default:
         // Normalerweise alle Selektionen löschen
         if (bShift) {
               bUpdate  |= switchSelection(jHitSel);
         } else {
               bUpdate  |= jSelects.size() != 0;
                           jSelects.removeAllElements();
               bUpdate  |= switchSelection(jHitSel);
         }
      }

      // Mausdruckbearbeitung - bestimmen des Arbeitsobjekts (für verschieden Modi)
      switch (Mode) {
      case CENTER  : aPoint = null;
                     break;

      case INTERACT: aPoint = pushInteract(jSnap);    
                     break;
      case SECTION :
      case LINE    : // Simuliere Setzen eines Punktes -> speichert Punkt
                     aPoint = pushPoint(jSnap, oPt);
                     aPoint = dragPoint(jSnap, oPt, aPoint, true );
                     aFirst = aPoint;                                // ersten Punkt sichern!
                     break;

      default:       aPoint = pushPoint(jSnap, oPt); 
                     break;
      }

      // Kein Objekt zur Behandlung -> abrechen
      if (aPoint == null) {
         if (bUpdate) repaint();
         return;
      }

      // Mausdruckbearbeitung - arbeiten mit dem gegebenen Objekt
      switch (Mode) {
      
      case CIRCLE  :
         // Nur vier Punkte und der aktuelle bestimmen die Kurve!
         if (jSelects.size() >  2)
             jSelects.removeElement(aPoint);

         // 4 Punkte müssen markiert sein
         if (jSelects.size() != 2                                 ||
            !(((JObject)jSelects.elementAt(0)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(1)) instanceof JPoint)) {
            // Das 2 selektierten Objekte müssen Punkte sein -> sonst Bearbeitung abbrechen
                aPoint = null;
            if (bUpdate) repaint();
            return;
         }
         aConic = new JCircleConnected((JPoint)jSelects.elementAt(0)
                                      ,(JPoint)jSelects.elementAt(1), aPoint, getForeground());
         break;

      case CONIC   :
         // Nur vier Punkte und der aktuelle bestimmen die Kurve!
         if (jSelects.size() >  4)
             jSelects.removeElement(aPoint);

         // 4 Punkte müssen markiert sein
         if (jSelects.size() != 4                                 ||
            !(((JObject)jSelects.elementAt(0)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(1)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(2)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(3)) instanceof JPoint)) {
            // Das 4 selektierten Objekte müssen Punkte sein -> sonst Bearbeitung abbrechen
                aPoint = null;
            if (bUpdate) repaint();
            return;
         }
         aConic = new JConicConnected((JPoint)jSelects.elementAt(0)
                                     ,(JPoint)jSelects.elementAt(1)
                                     ,(JPoint)jSelects.elementAt(2)
                                     ,(JPoint)jSelects.elementAt(3), aPoint, getForeground());
         break;

      case PARALLEL:
      case VERTICAL:
         // Die Gerade muß markiert sein
         if (jSelects.size() != 1 ||
            !(((JObject)jSelects.elementAt(0)) instanceof JLine)) {
            // Das einzige selektierte Objekt muß eine Gerade sein -> sonst Bearbeitung abbrechen
                aPoint = null;
            if (bUpdate) repaint();
            return;
         }
         if (Mode == VERTICAL) 
            aLine = new JLineVertical((JLine)jSelects.elementAt(0), aPoint, getForeground());
         else 
            aLine = new JLineParallel((JLine)jSelects.elementAt(0), aPoint, getForeground());
         break;

      case SECTION:
      case LINE:
         // Gerade durch 2 Punkte definieren (evtl. Modus ignorieren)
         if (Mode == SECTION || jObjects.contains(aPoint)) 
            aLine = new JLineConnected(aFirst,                     aPoint , getForeground());
         else
            aLine = new JLineAscending(aFirst, new JPointIntAscent(aPoint), getForeground());
         break;
      }

      // Konstruktion und Darstellung aktuell halten...
      doUpdate();
      repaint();
   }
   
   public void mouseDragged(MouseEvent e) {
                         e.consume();
      if (!fillTemporary(e)) 
         return;
      
      // Mausdragbearbeitung - bestimmen des Arbeitsobjekts (für verschieden Modi)
      switch (Mode) {
      case CENTER  : aPoint = null;
                     break;

      case INTERACT: aPoint = dragInteract(jSnap, oPt, aPoint, false);
                     break;

      default:       aPoint = dragPoint   (jSnap, oPt, aPoint, false);
                     break;
      }

      // Kein Objekt zur Behandlung -> abrechen
      if (aPoint == null) {
         if (bUpdate) repaint();
         return;
      }

      // Mausdragbearbeitung - arbeiten mit dem gegebenen Objekt (neue Objekte definieren?!)
      switch (Mode) {
      
      case CIRCLE  :
         // 4 Punkte müssen markiert sein
         if (jSelects.size() != 2                                 ||
            !(((JObject)jSelects.elementAt(0)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(1)) instanceof JPoint)) {
            // Das 2 selektierten Objekte müssen Punkte sein -> sonst Bearbeitung abbrechen
                aPoint = null;
            if (bUpdate) repaint();
            return;
         }
         aConic = new JCircleConnected((JPoint)jSelects.elementAt(0)
                                      ,(JPoint)jSelects.elementAt(1), aPoint, getForeground());
         break;

      case CONIC   :
         // 4 Punkte müssen markiert sein
         if (jSelects.size() != 4                                 ||
            !(((JObject)jSelects.elementAt(0)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(1)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(2)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(3)) instanceof JPoint)) {
            // Das 4 selektierten Objekte müssen Punkte sein -> sonst Bearbeitung abbrechen
                aPoint = null;
            if (bUpdate) repaint();
            return;
         }
         aConic = new JConicConnected((JPoint)jSelects.elementAt(0)
                                     ,(JPoint)jSelects.elementAt(1)
                                     ,(JPoint)jSelects.elementAt(2)
                                     ,(JPoint)jSelects.elementAt(3), aPoint, getForeground());
         break;

      case PARALLEL:
      case VERTICAL:
         // Die Gerade muß markiert sein
         if (jSelects.size() != 1 ||
            !(((JObject)jSelects.elementAt(0)) instanceof JLine)) {
            // Das einzige selektierte Objekt muß eine Gerade sein -> sonst Bearbeitung abbrechen
                aPoint = null;
            if (bUpdate) repaint();
            return;
         }
         if (Mode == VERTICAL) 
            aLine = new JLineVertical((JLine)jSelects.elementAt(0), aPoint, getForeground());
         else 
            aLine = new JLineParallel((JLine)jSelects.elementAt(0), aPoint, getForeground());
         break;

      case SECTION:
      case LINE:
         // Gerade durch 2 Punkte definieren (evtl. Modus ignorieren)
         if (Mode == SECTION || jObjects.contains(aPoint)) 
            aLine = new JLineConnected(aFirst,                     aPoint , getForeground());
         else
            aLine = new JLineAscending(aFirst, new JPointIntAscent(aPoint), getForeground());
         break;
      }

      // Konstruktion und Darstellung aktuell halten...
      doUpdate();
      repaint();
   }
   
   public void mouseReleased(MouseEvent e) {
                         e.consume();
      if (!fillTemporary(e)) 
         return;
      
      // Mausreleasebearbeitung - bestimmen des Arbeitsobjekts (für verschieden Modi) + evtl. Speicherung
      switch (Mode) {
      case CENTER  : aPoint = null;
                     break;

      case INTERACT: aPoint = dragInteract(jSnap, oPt, aPoint, true );
                     break;

      case LINE    : aPoint = dragPoint   (jSnap, oPt, aPoint, false);
                     break;

      default:       aPoint = dragPoint   (jSnap, oPt, aPoint, true );
                     break;
      }

      // Kein Objekt zur Behandlung -> abrechen
      if (aPoint == null && Mode != CENTER) {
         if (!bShift && Mode == INTERACT) {
                             jSelects.removeAllElements();
             bUpdate |= switchSelection(hitSelection(mActive, cPt));
         }
         if (bUpdate) repaint();
         return;
      }

      // Mausreleasebearbeitung - arbeiten mit dem gegebenen Objekt (neue Objekte definieren?!)
      switch (Mode) {
      case CENTER  :
         // 1 Kegelschnitt muss markiert sein
         if (jSelects.size() != 1                                 ||
            !(((JObject)jSelects.elementAt(0)) instanceof JConic)) {
            // Das 2 selektierten Objekte müssen Punkte sein -> sonst Bearbeitung abbrechen
                aPoint = null;
            if (bUpdate) repaint();
            return;
         }
         aPoint = new JPointConicCenter((JConic)jSelects.elementAt(0), getForeground());
         
         jObjects.addElement(aPoint);
                             aPoint.regPoint();
         break;

      case CIRCLE  :
         // 2 Punkte müssen markiert sein
         if (jSelects.size() != 2                                 ||
            !(((JObject)jSelects.elementAt(0)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(1)) instanceof JPoint)) {
            // Das 2 selektierten Objekte müssen Punkte sein -> sonst Bearbeitung abbrechen
                aPoint = null;
            if (bUpdate) repaint();
            return;
         }
         aConic = new JCircleConnected((JPoint)jSelects.elementAt(0)
                                      ,(JPoint)jSelects.elementAt(1), aPoint, getForeground());
         
         // Kreis nur abspeichern wenn gültiger Kreis generiert wird
         if (aPoint != (JPoint)jSelects.elementAt(0) &&
             aPoint != (JPoint)jSelects.elementAt(1)) {
            
            jObjects.addElement(aConic);
                                aConic.regConic();
         } else {
            // Sonst beenden
            aPoint = null;
            aConic = null;
            if (bUpdate) repaint();
            return;
         }
         break;

      case CONIC   :
         // 4 Punkte müssen markiert sein
         if (jSelects.size() != 4                                 ||
            !(((JObject)jSelects.elementAt(0)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(1)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(2)) instanceof JPoint) ||
            !(((JObject)jSelects.elementAt(3)) instanceof JPoint)) {
            // Das 4 selektierten Objekte müssen Punkte sein -> sonst Bearbeitung abbrechen
                aPoint = null;
            if (bUpdate) repaint();
            return;
         }
         aConic = new JConicConnected((JPoint)jSelects.elementAt(0)
                                     ,(JPoint)jSelects.elementAt(1)
                                     ,(JPoint)jSelects.elementAt(2)
                                     ,(JPoint)jSelects.elementAt(3), aPoint, getForeground());
         
         // Kurve nur abspeichern wenn gültige Kurve generiert wird
         if (aPoint != (JPoint)jSelects.elementAt(0) &&
             aPoint != (JPoint)jSelects.elementAt(1) &&
             aPoint != (JPoint)jSelects.elementAt(2) &&
             aPoint != (JPoint)jSelects.elementAt(3)) {
            
            jObjects.addElement(aConic);
                                aConic.regConic();
         } else {
            // Sonst beenden
            aPoint = null;
            aConic = null;
            if (bUpdate) repaint();
            return;
         }
         break;

      case PARALLEL:
      case VERTICAL:
         // Die Gerade muß markiert sein
         if (jSelects.size() != 1 ||
            !(((JObject)jSelects.elementAt(0)) instanceof JLine)) {
            // Das einzige selektierte Objekt muß eine Gerade sein -> sonst Bearbeitung abbrechen
                aPoint = null;
            if (bUpdate) repaint();
            return;
         }
         if (Mode == VERTICAL) 
            aLine = new JLineVertical((JLine)jSelects.elementAt(0), aPoint, getForeground());
         else 
            aLine = new JLineParallel((JLine)jSelects.elementAt(0), aPoint, getForeground());

         // Gerade abspeichern
         jObjects.addElement(aLine);
                             aLine.regLine();
         break;

      case SECTION:
      case LINE:
         // Gerade durch 2 Punkte definieren (evtl. Modus ignorieren)
         if (Mode == SECTION || jObjects.contains(aPoint)) 
            aLine = new JLineConnected(aFirst,                     aPoint , getForeground());
         else
            aLine = new JLineAscending(aFirst, new JPointIntAscent(aPoint), getForeground());

         // Gerade abspeichern
         jObjects.addElement(aLine);
                             aLine.regLine();
         break;
      }

      // Selektionsbehandlung
      switch (Mode) {
      case CENTER  : 
      case INTERACT: if (!bShift && !(aPoint instanceof JPointInternal)) {
                          jSelects.removeAllElements();
                          jSelects.addElement   (aPoint);
                     }
                     break;

      case POINT   : if (!bShift) 
                          jSelects.removeAllElements();
                     if (!jSelects.contains     (aPoint))
                          jSelects.addElement   (aPoint);
                     break;

      case CIRCLE  :
      case CONIC   :      jSelects.removeAllElements();
                          jSelects.addElement   (aConic);
                     break;

      case PARALLEL:
      case VERTICAL:      jSelects.removeAllElements();
                          jSelects.addElement   (aLine );
                     break;

      case SECTION :
      case LINE    : if (!bShift) 
                          jSelects.removeAllElements();
                          jSelects.removeElement(aFirst);
                          jSelects.removeElement(aPoint); 
                          jSelects.addElement   (aLine );
                     break;
      }
      // Konstruktion und Darstellung aktuell halten...
      doUpdate();
      repaint();

      // Bearbeitung beenden
      aFirst = null;
      aPoint = null;
      aLine  = null;
      aConic = null;
   }
   
   public void mouseMoved(MouseEvent e) {
   }
   
   public void mouseEntered(MouseEvent e) {
   }
   
   public void mouseExited(MouseEvent e) {
   }
   
   public void mouseClicked(MouseEvent e) {
   }

   public void paintCanvas(Graphics g, JMapping Map) {
      // Bildfläche vorbereiten
      Map.prepareCanvas(g);

      // Die Konstruktionsobjekte zeichnen (zuletzt die Punkte)
      for (int i=0; i < jObjects.size(); i++) {
         JObject jOb = (JObject)jObjects.elementAt(i);
         if (  !(jOb instanceof JPoint)   )  
                 jOb.draw(g, Map, jSelects.contains(jOb) ? Color.white : null);         
      }
      for (int i=0; i < jObjects.size(); i++) {
         JObject jOb = (JObject)jObjects.elementAt(i);
         if (    jOb instanceof JPoint   )  
                 jOb.draw(g, Map, jSelects.contains(jOb) ? Color.white : null);         
      }
      
      // Aktuelle Aktion darstellen
      JCanvasVector cStatus = null;
      JObjectVector oStatus = null;
      switch (Mode) {
      case INTERACT:
      case POINT   : if (aPoint != null) cStatus   =    Map.toCanvas(aPoint.xyz);
                     if (aPoint != null) oStatus   =                 aPoint.xyz.copy();
                     if (aPoint != null) aPoint.draw(g, Map, jSelects.contains(aPoint) ? Color.white : null);   break;
      
      case CIRCLE  :
      case CONIC   : if (aPoint != null) cStatus   =    Map.toCanvas(aPoint.xyz);
                     if (aPoint != null) oStatus   =                 aPoint.xyz.copy();
                     if (aPoint != null) aPoint.draw(g, Map, jSelects.contains(aPoint) ? Color.white : null); 
                     if (aConic != null) aConic.draw(g, Map, jSelects.contains(aConic) ? Color.white : null);   break;

      case PARALLEL:
      case VERTICAL:
      case SECTION : if (aPoint != null) cStatus   =    Map.toCanvas(aPoint.xyz);
                     if (aPoint != null) oStatus   =                 aPoint.xyz.copy();
                     if (aPoint != null) aPoint.draw(g, Map, jSelects.contains(aPoint) ? Color.white : null); 
                     if (aLine  != null) aLine .draw(g, Map, jSelects.contains(aLine ) ? Color.white : null);   break;
      
      case LINE    : if (aPoint != null) cStatus   =    Map.toCanvas(aPoint.xyz);
                     if (aPoint != null) oStatus   =                 aPoint.xyz.copy();
                     if (aLine  != null) aLine .draw(g, Map, jSelects.contains(aLine ) ? Color.white : null);   break;
      }
      // Status anzeigen (bspw. Koordinaten)
      if (aPoint  != null && 
          cStatus != null &&
          oStatus != null) {

         String sStatus;
         if (Map instanceof JMappingEuklid) {
            sStatus = "(" + oStatus.x.toStringLin() + 
                      "/" + oStatus.y.toStringLin() + ")"; 
         } else {
            oStatus = aPoint.xyz.z.Re() != 0 ? Map.toObject(Map.toCanvas(aPoint.xyz)) : oStatus;

            sStatus = "(" + oStatus.x.toStringLin() + 
                      "/" + oStatus.y.toStringLin() +
                      "/" + oStatus.z.toStringLin() + ")"; 
         }

         // Status anzeigen
         g.setColor(Color.darkGray);
         g.drawString(sStatus, cStatus.x+5, cStatus.y-5); 
      }
   }

   // *************************************
   // *    Aktualisierung der Ausgabe!    *
   // *************************************
   public void paint(Graphics g) {
      // Backbuffer löschen
      g.setColor(Color.lightGray);

      // Linke Seite
                  g.setClip(  2, 2, 501, 501);
      paintCanvas(g, mEuklid);

      // Rechte Seite
                  g.setClip(512, 2, 501, 501);
      paintCanvas(g, mSphere);
   }
}


