package gui;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

/**
 * Allows customization of a caret.
 */
public class CustomCaret extends DefaultCaret {
    private String mark;

    /**
     * Instantiates a caret with the requested mark and with a default blink rate of {@code 300}.
     * 
     * @param mark the string to display as caret
     */
    public CustomCaret(String mark) {
        this.mark = mark;
        setBlinkRate(300);
    }

    /**
     * Instantiates a caret with the requested mark and with the requested blink rate.
     * 
     * @param mark the string to display as caret
     * @param blinkRate the rate (in milliseconds) at which the caret blinks.
     *        {@code 0} means it doesn't blink.
     */
    public CustomCaret(String mark, int blinkRate) {
        this.mark = mark;
        setBlinkRate(blinkRate);
    }

    @Override
    protected synchronized void damage(Rectangle r) {

        if (r != null) {
            JTextComponent comp = getComponent();
            FontMetrics fm = comp.getFontMetrics(comp.getFont());
            int textWidth = fm.stringWidth("_");
            int textHeight = fm.getHeight();
            x = r.x;
            y = r.y;
            width = textWidth;
            height = textHeight;
            repaint(); // calls getComponent().repaint(x, y, width, height)
        }
    }

    @Override
    public void paint(Graphics g) {
        JTextComponent comp = getComponent();

        if (comp != null) {
            int dot = getDot();
            Rectangle r = null;
            try {
                r = comp.modelToView(dot);
            } catch (BadLocationException e) {
                return; // can't render
            }

            if (r != null) {
                if ((x != r.x) || (y != r.y)) {
                    repaint(); // erase previous location of caret
                    damage(r);
                }

                if (isVisible()) {
                    FontMetrics fm = comp.getFontMetrics(comp.getFont());

                    g.setColor(comp.getCaretColor());
                    g.drawString(mark, x, y + fm.getAscent());
                }
            }
        }
    }

}
