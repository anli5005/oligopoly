package dev.anli.oligopoly;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.CSS;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class Utils {
    private Utils() {

    }

    public static DocumentListener changeListener(Runnable onChange) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange.run();
            }
        };
    }

    public static void drawStringWrapped(
        String text,
        Graphics2D graphics,
        int x, int y, int width
    ) {
        FontRenderContext frc = graphics.getFontRenderContext();
        AttributedString string = new AttributedString(text);
        string.addAttribute(TextAttribute.FONT, graphics.getFont());
        AttributedCharacterIterator iterator = string.getIterator();
        LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, frc);

        while (measurer.getPosition() < iterator.getEndIndex()) {
            TextLayout layout = measurer.nextLayout(width);
            y += layout.getAscent();
            layout.draw(
                graphics,
                x + width / 2.0F +
                    (layout.isLeftToRight() ? -1 : 1) * layout.getVisibleAdvance() / 2,
                y
            );
            y += layout.getDescent() + layout.getLeading();
        }
    }
}