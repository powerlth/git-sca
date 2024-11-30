package kr.jbnu.se.std;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class GraphicsUtils {
    protected static void setCommonGraphicsSettings(Graphics2D g2d) {
    g2d.setFont(new Font("monospaced", Font.BOLD, 30));
    g2d.setColor(Color.RED);
    }
}
