package seaBattle.ui;

import javax.swing.*;
import java.awt.*;

public class HintTextField extends JTextField {
    
    private final String hint;

    public HintTextField(int columns, String hint) {
        super(columns);
        this.hint = hint;
        setFont(SeaBattleStyle.BODY_FONT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && hint != null && !hint.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(SeaBattleStyle.TEXT_MUTED);
            g2.setFont(getFont());
            int y = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
            g2.drawString(hint, 6, y);
        }
    }
}
