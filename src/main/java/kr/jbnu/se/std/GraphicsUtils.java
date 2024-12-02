package kr.jbnu.se.std;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class GraphicsUtils {
    protected static void setCommonGraphicsSettings(Graphics2D g2d) {
    g2d.setFont(new Font("monospaced", Font.BOLD, 30));
    g2d.setColor(Color.RED);
    }
    protected static void setFont(Graphics2D g2d, String font, int size) {
        g2d.setFont(new Font(font, Font.BOLD, size));
    }
    protected static Font newFont(String font, int size) {
        return new Font(font, Font.BOLD, size);
    }
}
