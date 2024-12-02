package kr.jbnu.se.std;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphicsManager {
    private static BufferedImage shootTheDuckMenuImg;

    public void drawImage(Graphics2D g2d, BufferedImage img, int x, int y) {
        g2d.drawImage(img, x, y, null);
    }
    public static void loadImage() {
        try {
            URL shootTheDuckMenuImgUrl = GraphicsManager.class.getResource("/images/menu.jpg");
            shootTheDuckMenuImg = ImageIO.read(shootTheDuckMenuImgUrl);
        } catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
