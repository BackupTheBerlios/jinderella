
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.Enumeration;

class JinderellaButtons extends Panel {
   JinderellaPanel target;
   
   public JinderellaButtons(JinderellaPanel target) {
      
      this.target = target;
      setLayout(new FlowLayout());

      ButtonGroup btnGroup = new ButtonGroup();
      
      btnGroup.add(new JToggleButton(new AbstractAction("", new ImageIcon(getClass().getResource("Interact.gif"))) { 
                                          public void actionPerformed(ActionEvent e) { 
                                             Interact_chosen(e); 
                                       }  }                                                                      ));
      btnGroup.add(new JToggleButton(new AbstractAction("", new ImageIcon(getClass().getResource("Point.gif"))) { 
                                          public void actionPerformed(ActionEvent e) { 
                                             Point_chosen(e); 
                                       }  }                                                                      ));
      btnGroup.add(new JToggleButton(new AbstractAction("", new ImageIcon(getClass().getResource("Section.gif"))) { 
                                          public void actionPerformed(ActionEvent e) { 
                                             Section_chosen(e); 
                                       }  }                                                                      ));
      btnGroup.add(new JToggleButton(new AbstractAction("", new ImageIcon(getClass().getResource("Line.gif"))) { 
                                          public void actionPerformed(ActionEvent e) { 
                                             Line_chosen(e); 
                                       }  }                                                                      ));
      btnGroup.add(new JToggleButton(new AbstractAction("", new ImageIcon(getClass().getResource("Parallel.gif"))) { 
                                          public void actionPerformed(ActionEvent e) { 
                                             Parallel_chosen(e); 
                                       }  }                                                                      ));
      btnGroup.add(new JToggleButton(new AbstractAction("", new ImageIcon(getClass().getResource("Vertical.gif"))) { 
                                          public void actionPerformed(ActionEvent e) { 
                                             Vertical_chosen(e); 
                                       }  }                                                                      ));
      btnGroup.add(new JToggleButton(new AbstractAction("", new ImageIcon(getClass().getResource("Circle.gif"))) { 
                                          public void actionPerformed(ActionEvent e) { 
                                             Circle_chosen(e); 
                                       }  }                                                                      ));
      btnGroup.add(new JToggleButton(new AbstractAction("", new ImageIcon(getClass().getResource("Conic.gif"))) { 
                                          public void actionPerformed(ActionEvent e) { 
                                             Conic_chosen(e); 
                                       }  }                                                                      ));
      btnGroup.add(new JToggleButton(new AbstractAction("", new ImageIcon(getClass().getResource("Center.gif"))) { 
                                          public void actionPerformed(ActionEvent e) { 
                                             Center_chosen(e); 
                                       }  }                                                                      ));
      
      Enumeration eBtns = btnGroup.getElements();
      while (eBtns.hasMoreElements()) 
         add((JToggleButton)eBtns.nextElement());
   }
   
   public void paint(Graphics g) {
      Rectangle r = getBounds();
      g.setColor(Color.darkGray);
      g.draw3DRect(0, 0, r.width, r.height, true);
      
      int n = getComponentCount();
      for(int i=0; i<n; i++) {
         Component comp = getComponent(i);
         if (comp instanceof JToggleButton) {
            ((JToggleButton)comp).repaint();
         }
      }
   }

   public void Interact_chosen(ActionEvent e) {  target.setDrawMode(JinderellaPanel.INTERACT);  }
   public void    Point_chosen(ActionEvent e) {  target.setDrawMode(JinderellaPanel.POINT   );  }
   public void  Section_chosen(ActionEvent e) {  target.setDrawMode(JinderellaPanel.SECTION );  }
   public void     Line_chosen(ActionEvent e) {  target.setDrawMode(JinderellaPanel.LINE    );  }
   public void Vertical_chosen(ActionEvent e) {  target.setDrawMode(JinderellaPanel.VERTICAL);  }
   public void Parallel_chosen(ActionEvent e) {  target.setDrawMode(JinderellaPanel.PARALLEL);  }
   public void    Conic_chosen(ActionEvent e) {  target.setDrawMode(JinderellaPanel.CONIC   );  }
   public void   Circle_chosen(ActionEvent e) {  target.setDrawMode(JinderellaPanel.CIRCLE  );  }
   public void   Center_chosen(ActionEvent e) {  target.setDrawMode(JinderellaPanel.CENTER  );  }
}

