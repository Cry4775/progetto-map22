/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package di.uniba.map.b.adventure;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

/**
 *
 * @author Pierdamiano
 */
public class NoiseEffectPanel extends JComponent implements Runnable {
    byte[] data;

    BufferedImage image;

    Random random;

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    public void initialize() {
        int w = getSize().width, h = getSize().height;
        int length = ((w + 7) * h) / 8;
        data = new byte[length];
        DataBuffer db = new DataBufferByte(data, length);
        WritableRaster wr = Raster.createPackedRaster(db, w, h, 1, null);
        ColorModel cm = new IndexColorModel(1, 2,
                new byte[] { (byte) 0, (byte) 255 }, // R
                new byte[] { (byte) 0, (byte) 255 }, // G
                new byte[] { (byte) 0, (byte) 255 }, // B
                new byte[] { (byte) 20, (byte) 10 }); // A
        image = new BufferedImage(cm, wr, false, null);
        random = new Random();
        new Thread(this).start();
    }

    public void run() {
        while (true) {
            random.nextBytes(data);
            repaint();
            try {
                Thread.sleep(1000 / 24);
            } catch (InterruptedException e) { /* die */
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        if (image == null)
            initialize();
        g.drawImage(image, 0, 0, this);
    }

    public static void main(String[] args) throws IOException {
        JFrame f = new JFrame();
        JLayeredPane panel = new JLayeredPane();
        panel.setLayout(new javax.swing.OverlayLayout(panel));
        f.add(panel);
        f.setSize(600, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        panel.add(new JLabel(
                new javax.swing.ImageIcon(ImageIO.read(new File(
                        "C:\\Users\\Pierdamiano\\Documents\\progetto-map22\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\img\\bussola_03.png")))),
                new Integer(0));
        panel.add(new NoiseEffectPanel(), new Integer(1));

    }
}
