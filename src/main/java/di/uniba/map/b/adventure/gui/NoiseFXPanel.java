/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this
 * license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package di.uniba.map.b.adventure.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Random;
import javax.swing.JComponent;

public class NoiseFXPanel extends JComponent implements Runnable {
    private byte[] data;

    private BufferedImage image;

    private Random random;

    Thread thread;

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
                new byte[] {(byte) 0, (byte) 255}, // R
                new byte[] {(byte) 0, (byte) 255}, // G
                new byte[] {(byte) 0, (byte) 255}, // B
                new byte[] {(byte) 20, (byte) 10}); // A
        image = new BufferedImage(cm, wr, false, null);
        random = new Random();
        thread = new Thread(this, "GUI-NoiseEffect");
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            random.nextBytes(data);
            repaint();
            try {
                Thread.sleep(30);
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
}
