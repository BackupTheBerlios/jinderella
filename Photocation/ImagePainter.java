
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePainter extends Panel implements MouseListener, ActionListener {
    private Image cachedImage;
    private BufferedImage sourceImage;
    private double scale;
    private JPopupMenu popup;
    private int pressedX;
    private int pressedY;

    public static class Point {
        public Point( int x, int y ) {
            this.x = x;
            this.y = y;
        }

        int x;
        int y;
    }

    private Point[] points;

    public ImagePainter() {
        JMenuItem mi;
        points = new Point[5];
        scale = 1;
        addMouseListener( this );
        popup = new JPopupMenu( "Point" );
        for ( int i = 0; i <= 4; i++ ) {
            mi = new JMenuItem( new Integer( i + 1 ).toString() );
            mi.addActionListener( this );
            popup.add( mi );
        }
        mi = new JMenuItem( "Cancel" );
        mi.addActionListener( this );
        popup.add( mi );

        add( popup );
        setCursor( new Cursor( Cursor.CROSSHAIR_CURSOR ) );

    }

    public void loadImage( String s ) {
        try {
            sourceImage = ImageIO.read( new File( s ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        updateCache();

        repaint();
    }


    public void paint( Graphics g ) {
        if ( cachedImage == null || getSize().width != cachedImage.getWidth( null ) ) {
            updateCache();
        }

        if ( cachedImage != null ) {
            g.drawImage( cachedImage, 0, 0, this );
        }

        for ( int i = 0; i <= 4; i++ ) {
            if ( points[i] != null ) {
                int x = (int) ( points[i].x * scale );
                int y = (int) ( points[i].y * scale );
                DescriptionPoint.paintPoint( g, x, y, i + 1 );
            }
        }
    }

    private void updateCache() {
        if ( sourceImage != null ) {
            // Beste Skalierung
            double scaleX = (double) getSize().width / (double) sourceImage.getWidth();
            double scaleY = (double) getSize().height / (double) sourceImage.getHeight();
            if ( scaleX < scaleY ) {
                scale = scaleX;
            } else {
                scale = scaleY;
            }

            cachedImage = sourceImage.getScaledInstance( (int) ( sourceImage.getWidth() * scale ), (int) ( sourceImage.getHeight() * scale ), Image.SCALE_DEFAULT );
        }
    }

    private void setPoint( int num, int x, int y ) {
        if ( num <= 4 ) {
            points[num] = new Point( x, y );
        }
    }

    public boolean hasAllPoints() {
        for ( int i = 0; i <= 4; i++ )
            if ( points[i] == null ) return false;
        return true;
    }

    public int getPointX( int i ) {
        if ( points[i] == null ) return -1;
        return points[i].x;
    }

    public int getPointY( int i ) {
        if ( points[i] == null ) return -1;
        return points[i].y;
    }


    public void update( Graphics g ) {
        paint( g );
    }

    public void mouseClicked( MouseEvent e ) {
        if ( sourceImage == null ) return;
        pressedX = e.getX();
        pressedY = e.getY();
        if ( ( (int) ( pressedX / scale ) <= sourceImage.getWidth() ) && ( (int) ( pressedY / scale ) <= sourceImage.getHeight() ) ) {
            popup.show( e.getComponent(), pressedX, pressedY );
        }
    }

    public void mousePressed( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    public void actionPerformed( ActionEvent e ) {
        String command = e.getActionCommand();
        for ( int i = 0; i <= 4; i++ ) {
            if ( command.equals( new Integer( i + 1 ).toString() ) ) {
                setPoint( i, (int) ( pressedX / scale ), (int) ( pressedY / scale ) );
            }
        }
        repaint();
    }


}
