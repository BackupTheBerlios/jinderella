
import java.lang.Math;

public class Complex {
   // --------------------------------------------------------
   public static final Complex One  = new Complex   ( 1.0     );
   public static final Complex Zero = new Complex   ( 0.0     );
   public static final Complex i    = new ComplexLin( 0.0, 1.0);

   // Werte zur Speicherung Komplexer Zahlen (nicht verändern)
   // --------------------------------------------------------
   private double a, b;       // LIN: Komplexe Zahl im Format: a+ib
   private double r, phi;     // EXP: Komplexe Zahl im Format: r+exp(i*phi)

   // Werte abrufen
   public double Re()  { return a;   }
   public double Im()  { return b;   }
   
   public double Len() { return r;   }
   public double Phi() { return phi; }

   // Test auf 0
   public boolean isZero() { return              r <= 1e-8; }
   public boolean isReal() { return b >=-1e-8 && b <= 1e-8; }

   // Ausgabe
   // Formatierung der Double-Werte auf 2-Nachkommastellen
   private static double toFixPnt(double d, int iFactor) { 
      double dF = d*(double)iFactor; 
      double dL = Math.floor(dF);

      if (dF - dL < 0.5) return (dL    )/((double)iFactor);
                    else return (dL+1.0)/((double)iFactor);
   }
   private static double toFixPnt(double d             ) { return toFixPnt(d, 100);                             }

   public  String toStringLin()     { 
      double fA = toFixPnt(         a );
      double fB = toFixPnt(Math.abs(b));

           if (fA == 0.0 && fB == 0.0) return "0.0";
      else if (fA == 0.0 && fB != 0.0) return         (b<0.0 ? "-" :  "")+ fB +"i";
      else if (fA != 0.0 && fB == 0.0) return ""+fA;
      else                             return ""+fA + (b<0.0 ? "-" : "+")+"i*"+ fB;
   }
   public String toStringExp() {
      double fR = toFixPnt(r);
      double fP = toFixPnt(phi);

           if (fR == 0.0             ) return "0.0";
      else if (fR == 1.0 && fP == 0.0) return "1.0";
      else if (fR == 1.0 && fP != 0.0) return        "exp(i*"+fP+")";
      else if (fR != 1.0 && fP == 0.0) return ""+fR;
      else                             return ""+fR+"*exp(i*"+fP+")";
   }

   // Konstruktor (Standardmäßig: lineare Definition)
   // --------------------------------------------------------
   public Complex() { 
      a = 0.0;    r   = 0.0; 
      b = 0.0;    phi = 0.0;
   }
   // double-Wert in Complex-Objekt umwandeln
   public Complex(double d) { setLin(d, 0.0); }

   public Complex copy() {
      // Kopie erstellen (Exp. Darstellung, da hier nur cos() und sin() in Berechung auftauchen)
      return new ComplexExp(r, phi);
   }

   // --------------------------------------------------------
   // Addition im linearen (Inplace)
   public Complex add (Complex c) { setLin(       a+c.a,   b+c.b  ); return this; }
   public Complex sub (Complex c) { setLin(       a-c.a,   b-c.b  ); return this; }
   public Complex neg ()          { setLin(      -a    ,  -b      ); return this; }

   // Bestimme das Konjugiert-Komplexe
   public Complex conj()          { setLin(       a    ,  -b      ); return this; }

   // Multiplikation im exponentiellen (Inplace)
   public Complex mul (Complex c) { setExp(       r*c.r, phi+c.phi); return this; }
   public Complex div (Complex c) { setExp(       r/c.r, phi-c.phi); return this; }
   public Complex rez ()          { setExp(   1.0/r    ,-phi      ); return this; }

   public Complex sqr ()          { setExp(        r*r ,  phi*2.0 ); return this; }
   public Complex sqrt()          { setExp(Math.sqrt(r),  phi/2.0 ); return this; }
   public Complex pow(double p)   { setExp(Math.pow(r,p), phi*p   ); return this; }

   // --------------------------------------------------------
   // Addition im linearen (Statisch)
   public static Complex add (Complex c, Complex d) { return new ComplexLin( c.Re() +d.Re(),  c.Im() +d.Im()); }
   public static Complex sub (Complex c, Complex d) { return new ComplexLin( c.Re() -d.Re(),  c.Im() -d.Im()); }
   public static Complex neg (Complex c           ) { return new ComplexLin(-c.Re()        , -c.Im()        ); }
   
   // Bestimme das Konjugiert-Komplexe
   public static Complex conj(Complex c           ) { return new ComplexLin( c.Re()        , -c.Im()        ); }

   // Multiplikation im exponentiellen (Statisch)
   public static Complex mul (Complex c, Complex d) { return new ComplexExp(    c.Len() * d.Len() ,  c.Phi()+d.Phi()); }
   public static Complex div (Complex c, Complex d) { return new ComplexExp(    c.Len() / d.Len() ,  c.Phi()-d.Phi()); }
   public static Complex rez (Complex c           ) { return new ComplexExp(1.0/c.Len()           , -c.Phi()        ); }

   public static Complex sqr (Complex c)            { return new ComplexExp(    c.Len() * c.Len() ,  c.Phi()*2.0    ); }
   public static Complex sqrt(Complex c)            { return new ComplexExp( Math.sqrt(   c.Len()),  c.Phi()/2.0    ); }
   public static Complex pow (Complex c, double  p) { return new ComplexExp( Math.pow ( c.Len(),p),  c.Phi()*p      ); }

   // --------------------------------------------------------

   public static Complex roundTo(Complex c, int iFactor) {
      return new ComplexLin( toFixPnt(c.Re(), iFactor)
                           , toFixPnt(c.Im(), iFactor) );
   }
   // Private und Protected Klassenmember zum Umgang mit den beiden Zahlenformaten
   // --------------------------------------------------------
   protected void setLin(double a, double b) {
      this.a = a; 
      this.b = b;       
      
      updateExp();      // Exponentielle Darstellung konsistent halten
   }
   protected void setExp(double r, double phi) {
      this.r   =              r; 
      this.phi = normalizePhi(phi); 
      
      updateLin();      // Lineare Darstellung konsistent halten

      if (isZero()) {   // Fehler nicht kumulieren
         this.r   = 0.0;
         this.phi = 0.0;
         updateLin();
      }
   }

   // Phi zwischen 0 und 2xPi belassen
   private double normalizePhi(double p) {
      return (p<0 ? 2*Math.PI : 0.0) 
           + (p<0 ?      -1.0 : 1.0) * Math.IEEEremainder((p<0 ? -p : p), 2*Math.PI);
   }

   // Die Member-Daten konsistent halten
   private void updateLin() {
      a   = r * Math.cos(phi);
      b   = r * Math.sin(phi);
   }
   private void updateExp() {
      r   = Math.sqrt(a*a+b*b);
      phi = (r == 0.0 ? 0 : Math.max( Math.acos(a/r), Math.asin(b/r) ));

      // Winkel ggf. anpassen
      if (b < 0) 
         phi = Math.PI*2 - phi;
      
      // Berechnungsungenauigkeiten (???)
      if (     Math.PI*2 - phi < 1e-8) phi = 0.0;
   }
}

// Zur Definition von komplexen Zahlen über Realteil a und Imaginärteil b
class ComplexLin extends Complex {

   public ComplexLin(double a, double b  ) { setLin(a,  b ); }
}

// Zur Definition von komplexen Zahlen über Radius r und Winkel phi
class ComplexExp extends Complex {

   public ComplexExp(double r, double phi) { setExp(r, phi); }
}
