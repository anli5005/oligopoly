package dev.anli.oligopoly.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

/**
 * Various drawing-related utility functions.
 */
public class Utils {
    private Utils() {
        // Do nothing.
    }

    /**
     * Creates a {@link DocumentListener} from a runnable that gets executed only when the document
     * changes.
     */
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

    /**
     * Draws a centered string with the given bounds, wrapping if needed.
     * @param text text to draw
     * @param graphics graphics context to draw into
     * @param x left X of the text
     * @param y top Y of the text
     * @param width maximum width of the text
     */
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