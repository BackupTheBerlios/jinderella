
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.Enumeration;

class JinderellaControls extends Panel implements ItemListener {
   JinderellaPanel target;
   
   public JinderellaControls(JinderellaPanel target) {
      
      this.target = target;
      setLayout(new FlowLayout());

      setBackground(Color.lightGray);
      target.setForeground(Color.red);
      CheckboxGroup group = new CheckboxGroup();
      Checkbox b;
      add(b = new Checkbox(null, group, false));
      b.addItemListener(this);
      b.setForeground(Color.red);
      add(b = new Checkbox(null, group, false));
      b.addItemListener(this);
      b.setForeground(Color.green.darker());
      add(b = new Checkbox(null, group, false));
      b.addItemListener(this);
      b.setForeground(Color.blue);
      add(b = new Checkbox(null, group, false));
      b.addItemListener(this);
      b.setForeground(Color.cyan);
      add(b = new Checkbox(null, group, false));
      b.addItemListener(this);
      b.setForeground(Color.yellow);
      add(b = new Checkbox(null, group, true ));
      b.addItemListener(this);
      b.setForeground(Color.black);

      group.setSelectedCheckbox(b);
      target.setForeground(Color.black);
   }
   
   public void paint(Graphics g) {
      Rectangle r = getBounds();
      g.setColor(Color.darkGray);
      g.draw3DRect(0, 0, r.width, r.height, false);
      
      int n = getComponentCount();
      for(int i=0; i<n; i++) {
         Component comp = getComponent(i);
         if (comp instanceof Checkbox) {
            Point loc = comp.getLocation();
            Dimension d = comp.getSize();
            g.setColor(comp.getForeground());
            g.drawRect(loc.x-1, loc.y-1, d.width+1, d.height+1);
         }
      }
   }
   
   public void itemStateChanged(ItemEvent e) {
      if (e.getSource() instanceof Checkbox) {
         target.setForeground(((Component)e.getSource()).getForeground());
      }
   }
}

