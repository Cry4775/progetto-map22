package di.uniba.map.b.adventure;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class CustomCaret extends DefaultCaret {

    private String mark = "_";

    public CustomCaret() {
        setBlinkRate(300);
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
