/*
* Jinderella.java (orig. @(#)DrawTest.java  1.7 99/05/28)
*
* Copyright (c) 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
*
* Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
* modify and redistribute this software in source and binary code form,
* provided that i) this copyright notice and license appear on all copies of
* the software; and ii) Licensee does not utilize the software in a manner
* which is disparaging to Sun.
*
* This software is provided "AS IS," without a warranty of any kind. ALL
* EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
* IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
* NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
* LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
* OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
* LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
* INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
* CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
* OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGES.
*
* This software is not designed or intended for use in on-line control of
* aircraft, air traffic, aircraft navigation or aircraft communications; or in
* the design, construction, operation or maintenance of any nuclear
* facility. Licensee represents and warrants that it will not use or
* redistribute the Software for such purposes.
*/

import java.awt.*;
import java.applet.*;

// =========================================
// === Externe Klassen (leicht angepaßt) ===
// =========================================
public class Jinderella extends Applet {
   JinderellaButtons  buttons;
   JinderellaPanel    panel;
   JinderellaControls controls;
   
   public void init() {
      setLayout( new BorderLayout() );
      panel    = new JinderellaPanel();
      buttons  = new JinderellaButtons(panel);
      controls = new JinderellaControls(panel);
      
      add("North" , buttons );
      add("Center", panel   );
      add("South" , controls);

      JPoint.regPoints = 0;
      JLine .regLines  = 0;
      JConic.regConics = 0;
   }
   
   public void destroy() {
      remove(panel);
      remove(controls);
   }
   
   public static void main(String args[]) {
      Frame f = new Frame("Jinderella");
      Jinderella jinderella = new Jinderella();
      jinderella.init();
      jinderella.start();
      
      f.add("Center", jinderella);
      f.setSize(500, 400);
      f.show();
   }
   public String getAppletInfo() {
      return "Cinderella für Arme!!!";                // ---
   }
}



