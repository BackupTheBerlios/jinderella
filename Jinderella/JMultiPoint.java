
import java.util.Vector;
import java.awt.*;

// abstrakte Basisklasse von der alle Punktobjekte abgeleitet werden
abstract class JMultiPoint extends JPoint {

   public static final double EPS = 0.025;

   private int     iIndex;         // Welcher der Schnittpunkte
   private boolean bForceIndex;    // Wahl des Schnittpunkts auf Index beschränken

   public JMultiPoint(Color c, int iIndex) { 
      // Farbe speichern
      super(c);
      // Index abspeichern
      this.iIndex      = iIndex;
      this.bForceIndex = true;
   }

   // Zugriff auf den klassenabhängigen Key (sollte statisch zur Klasse definiert werden)
   abstract Vector getKey();

   // Zugriff auf die klassenabhängigen MemoObjekte (sollten statisch zur Klasse definiert werden)
   abstract Vector getArray();

   JPoint getMemo(Vector jKey) {
      Vector jMemoKey   = getKey();
      Vector jMemoArray = getArray();

      // Schlüssel muß übereinstimmen
      if (jKey.size() != jMemoKey.size()) return null;

      // Key vergleichen
      for (int i=0; i<jKey.size(); i++) 
         if (jMemoKey.elementAt(i) != jKey.elementAt(i)) return null;

      // Key stimmt -> Index außerhalb?!
      //            -> Ungültigen Punkt zurückgeben (Bsp.: Schnittpunkt 2er Kreise -> 2 Schnittpunkte statt 4)
      if (iIndex < 0 || iIndex >= jMemoArray.size()) return jNoPoint;

      // Punkt ungültig -> Wahl per Index!
      if (xyz.x.isZero() && xyz.y.isZero() && xyz.z.isZero())
         return (JPoint)jMemoArray.elementAt(iIndex);

      // Suche Punkt, der am wahrscheinlichsten ist!
      Vector vDist = new Vector();       // Distanz p_i - this
      Vector vIndx = new Vector();       // Index des Punktes mit der obigen Distanz (in jMemoArray)

      JObjectVector v_t = JObjectVector.roundTo( this.xyz, 100 ).onlyReal();

      for (int i=0; i<jMemoArray.size(); i++) {
         JPoint        p_i = (JPoint)jMemoArray.elementAt(i);
         JObjectVector v_i = JObjectVector.roundTo( p_i.xyz, 100 ).onlyReal();
         JObjectVector v_d = JObjectVector.doCross( v_i    , v_t );

         vDist.addElement( v_d );
         vIndx.addElement( new Integer(i) );
      }
      // Vektoren nach der Distanz sortieren
      for (int i=0; i<vDist.size(); i++) {
         for (int j=i+1; j<vDist.size(); j++) {
            JObjectVector d1 = (JObjectVector)vDist.elementAt(i);
            JObjectVector d2 = (JObjectVector)vDist.elementAt(j);
            Integer       i2 = (Integer      )vIndx.elementAt(j);

            if (d1.len().Len() > d2.len().Len()) {
               // Einträge tauschen (Pseudotauschen: nur Reihenfolge j vor i)
               vDist.removeElementAt(j);   vDist.insertElementAt(d2, i);
               vIndx.removeElementAt(j);   vIndx.insertElementAt(i2, i);
            }
         }
      }
/*      System.out.println("xxx Punkt "+getName()+" mit Index "+iIndex+" xxx");
      for (int i=0; i<vDist.size(); i++) {
         JObjectVector d1 = (JObjectVector)vDist.elementAt(i  );
         System.out.println("Objekt "+i+" ("+((Integer)vIndx.elementAt(i)).intValue()+"): "+d1.toStringLin());
      }
      System.out.println("");
 */     // Vektoren nach der Distanz sortieren
      for (int i=0; i<vDist.size()-1; i++) {
         JObjectVector d1 = (JObjectVector)vDist.elementAt(i  );
         JObjectVector d2 = (JObjectVector)vDist.elementAt(i+1);

         if (Math.abs(d1.x.Len() - d2.x.Len()) > EPS ||
             Math.abs(d1.y.Len() - d2.y.Len()) > EPS || 
             Math.abs(d1.z.Len() - d2.z.Len()) > EPS ) {
//               System.out.println(" d1x = "+d1.x.Len()+" <-> "+d2.x.Len()+" = d2x");
  //             System.out.println(" d1y = "+d1.y.Len()+" <-> "+d2.y.Len()+" = d2y");
    //           System.out.println(" d1z = "+d1.z.Len()+" <-> "+d2.z.Len()+" = d2z");
            // Ab  i+1. ist der Unterschied zu groß
            while (i+1 < vDist.size()) {
               vDist.removeElementAt(i+1);
               vIndx.removeElementAt(i+1);
            }
            break;
         }
      }
/*      System.out.println("       --- Punkt "+getName()+" mit Index "+iIndex+" ---");
      for (int i=0; i<vDist.size(); i++) {
         JObjectVector d1 = (JObjectVector)vDist.elementAt(i  );
         System.out.println("       Objekt "+i+" ("+((Integer)vIndx.elementAt(i)).intValue()+"): "+d1.toStringLin());
      }
      System.out.println("");
*/
      // Alle Möglichkeiten sind in den Vektoren vDist und vIndx zu finden!
      if (vDist.size() > 1) {
         // Noch mehrere Möglichkeiten -> Versuche Index zu verifizieren
         for (int i=0; i<vIndx.size(); i++) {
            Integer iInt = (Integer)vIndx.elementAt(i);
            
            // Index wurde gefunden?! -> behalte Index bei und liefere entsprechenden Punkt
            if (iInt.intValue() == iIndex) {
               // Punkt(e) werden auf eine multiple Lösung gemapped -> nächstes mal entscheidet der Index
               bForceIndex = true;
               return (JPoint)jMemoArray.elementAt(iIndex);
            }
         }
         // Index war nicht dabei -> wähle erstes Element
      }
      // Wähle wahrscheinlichste Möglichkeit und passe Index an
      JPoint jReturn;
      
      if (!bForceIndex) {
         // Index darf verändert werden
         iIndex = ((Integer)vIndx.elementAt(0)).intValue();
      }

      // Passe Flag an...
      if (vDist.size() > 1) {
         // Punkte liegen nah beieinander -> Index entscheidet nächstes mal
         bForceIndex = true;
      } else {
         // Punkt sind unterscheidbar -> normale Behandlung
         bForceIndex = false;
      }
      return (JPoint)jMemoArray.elementAt(iIndex);
   }

   void setMemo(Vector jKey, Vector jArray) {
      Vector jMemoKey   = getKey();
      Vector jMemoArray = getArray();

      // Key löschen
      jMemoKey.removeAllElements();

      // Key abspeichern
      for (int i=0; i<jKey.size(); i++) 
         jMemoKey.addElement(jKey.elementAt(i));

      // Objekte löschen
      jMemoArray.removeAllElements();

      // Objekte abspeichern
      for (int i=0; i<jArray.size(); i++) 
         jMemoArray.addElement(jArray.elementAt(i));
   }

   static final JPoint jNoPoint = new JPointSimple(0,0,0);
}
