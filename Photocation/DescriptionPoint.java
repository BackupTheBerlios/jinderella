
import java.awt.*;

public class DescriptionPoint extends Panel {

    private int number;

    public DescriptionPoint( int number ) {
        this.number = number;
        setSize( 20, 20 );
    }

    public static void paintPoint( Graphics g, int x, int y, int i ) {
        x = x - 10;
        y = y - 10;
        g.setColor( new Color( 255, 255, 255 ) );
        g.fillOval( x, y, 20, 20 );
        g.setColor( new Color( 0, 0, 0 ) );
        g.fillOval( x + 1, y + 1, 18, 18 );
        g.setFont( new Font( "Arial", Font.BOLD, 14 ) );
        g.setColor( new Color( 255, 255, 255 ) );
        g.drawString( new Integer( i ).toString(), x + 6, y + 15 );
    }

    public void paint( Graphics g ) {
        paintPoint( g, 10, 10, number );
    }

}
