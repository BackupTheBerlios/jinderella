
import mfc.field.Complex;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.awt.*;
import java.util.Vector;

public class Photocation extends java.awt.Frame {

    public Photocation() {
        initComponents();
    }

    private void initComponents() {
        openButton = new javax.swing.JButton();
        generateButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        jCanvas = new ImagePainter();
        descrFields = new DescriptionPoint[5];
        coordinateFields = new javax.swing.JTextField[5];

        GridBagConstraints c = new GridBagConstraints();

        setLayout( new java.awt.GridBagLayout() );
        setTitle( "Photocation" );

        addWindowListener( new java.awt.event.WindowAdapter() {
            public void windowClosing( java.awt.event.WindowEvent evt ) {
                exitForm( evt );
            }
        } );


        openButton.setText( "Open Image..." );
        openButton.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( java.awt.event.ActionEvent evt ) {
                openButtonActionPerformed( evt );
            }
        } );

        generateButton.setText( "Generate..." );
        generateButton.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( java.awt.event.ActionEvent evt ) {
                generateButtonActionPerformed( evt );
            }
        } );

        exitButton.setText( "Exit" );
        exitButton.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( java.awt.event.ActionEvent evt ) {
                exitButtonActionPerformed( evt );
            }
        } );

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 1;
        c.gridheight = 8;
        add( jCanvas, c );

        c.weightx = 0.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridheight = 1;

        for ( int i = 0; i <= 4; i++ ) {
            c.gridy = i;

            c.ipadx = 15;
            c.ipady = 15;
            c.gridx = 1;
            descrFields[i] = new DescriptionPoint( i + 1 );
            add( descrFields[i], c );

            c.ipadx = 0;
            c.ipady = 0;
            c.gridx = 2;
            coordinateFields[i] = new javax.swing.JTextField();
            coordinateFields[i].setMaximumSize( new Dimension( 200, 20 ) );
            coordinateFields[i].setPreferredSize( new Dimension( 200, 20 ) );
            add( coordinateFields[i], c );
        }


        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 0.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 3;
        c.gridheight = 1;
        add( openButton, c );

        c.gridx = 1;
        c.gridy = 6;
        c.weightx = 0.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridheight = 1;
        add( generateButton, c );

        c.gridx = 1;
        c.gridy = 7;
        c.weightx = 0.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridheight = 1;
        add( exitButton, c );

        setSize( 600, 500 );
    }

    private void generateButtonActionPerformed( java.awt.event.ActionEvent evt ) {
        Vector GPSPoints = new Vector();
        ComplexVector mapPoints[] = new ComplexVector[5];
        for ( int i = 0; i <= 4; i++ ) {
            GPS GPSPoint = new GPS( coordinateFields[i].getText() );
            if ( GPSPoint.invalid ) return;
            GPSPoints.add( GPSPoint );
            mapPoints[i] = new ComplexVector( new Complex( GPSPoint.lon ), new Complex( GPSPoint.lat ), new Complex(1) );
        }

        double cr = ComplexConic.crossRatio( jCanvas.points[0].x,  jCanvas.points[1].x,  jCanvas.points[2].x,  jCanvas.points[3].x );
        System.out.println("mapPoints0 = " + mapPoints[0]);
        System.out.println("mapPoints1 = " + mapPoints[1]);
        System.out.println("mapPoints2 = " + mapPoints[2]);
        System.out.println("mapPoints3 = " + mapPoints[3]);
        ComplexConic conic = new ComplexConic( mapPoints[0], mapPoints[1], mapPoints[2], mapPoints[3], cr );
        System.out.println("conic = " + conic);
        System.out.println("conic = " + conic.conicMatrix.toStringEQ());



        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog( this );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File selFile = fc.getSelectedFile();
            ConicRoute croute = new ConicRoute( selFile.toString() );
            //croute.addRoute( GPSPoints );
            croute.printRoute( conic );
            croute.writeRoute();
        }
    }

    private void openButtonActionPerformed( java.awt.event.ActionEvent evt ) {
        JFileChooser fc = new JFileChooser();

        FileFilter ff = new FileFilter() {
            public boolean accept( File f ) {
                if ( f.isDirectory() ) {
                    return true;
                }
                String filename = f.getName().toLowerCase();
                return ( filename.endsWith( ".jpg" ) || filename.endsWith( ".jpeg" ) );
            }

            public String getDescription() {
                return "*.jpg";
            }
        };
        fc.addChoosableFileFilter( ff );
        int returnVal = fc.showOpenDialog( this );

        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File selFile = fc.getSelectedFile();
            jCanvas.loadImage( selFile.toString() );
        }
    }

    /** Exit the Application */
    private void exitButtonActionPerformed( java.awt.event.ActionEvent evt ) {
        System.exit( 0 );
    }

    /** Exit the Application */
    private void exitForm( java.awt.event.WindowEvent evt ) {
        System.exit( 0 );
    }

    /**
     * @param args the command line arguments
     */
    public static void main( String args[] ) {
       new Photocation().show();
//       ConicRoute.sectionTest();
    }

    private javax.swing.JButton exitButton;
    private javax.swing.JButton openButton;
    private javax.swing.JButton generateButton;
    private DescriptionPoint[] descrFields;
    private javax.swing.JTextField[] coordinateFields;
    private ImagePainter jCanvas;

}
