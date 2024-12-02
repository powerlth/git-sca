package kr.jbnu.se.std;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

import static kr.jbnu.se.std.Money.subtractMoney;

public class Shop2 {

    // 아이템 정보 (아이템 이름과 가격)
    static final String[] shopItems = {"Item 1", "Item 2", "Item 3"};
    private static final int[] itemPrices = {50, 100, 200};
    static int selectedItem = -1; // 선택된 아이템 인덱스
    private static BufferedImage[] itemImage = new BufferedImage[shopItems.length];
    static String shopMessage = "";
    private static final String FONTNAME = ("Arial");

    public static void loadItemImages() throws IOException {
            itemImage[0] = ImageIO.read(Objects.requireNonNull(Shop2.class.getResource("/images/back1.jpg"))); // Item 1 이미지
            itemImage[1] = ImageIO.read(Objects.requireNonNull(Shop2.class.getResource("/images/back2.jpg"))); // Item 2 이미지
            itemImage[2] = ImageIO.read(Objects.requireNonNull(Shop2.class.getResource("/images/back3.jpg"))); // Item 3 이미지

    }

    public static String[] getshopItems() {
        return shopItems.clone();
    }
    public static int getSelectedItems() {
        return selectedItem;
    }
    public static void setSelectedItem(int index) {
        if (index >= 0 && index < shopItems.length) {
            selectedItem = index;
        } else {
            throw new IllegalArgumentException("Invalid item index");
        }
    }
    public static String getShopMessage() {
        return shopMessage;
    }
    public static int[] getItemPrices(){
        return itemPrices.clone();
    }

    public static void setShopMessage(String message) {
        shopMessage = message;
    }

    public static void drawShopUI(Graphics2D g2d, int frameWidth, int frameHeight, Component component) throws IOException {
        // 상점 배경 그리기
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.fillRect(0, 0, frameWidth  , frameHeight  );
        component.setCursor(Cursor.getDefaultCursor());
        loadItemImages();
        // 상점 제목
        GraphicsUtils.setFont(g2d, FONTNAME, 30);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Shop", frameWidth / 2 - 50, 150);
        g2d.drawString(String.valueOf(Money.getMoney()), frameWidth / 2 + 350, 80);
        GraphicsUtils.setFont(g2d, FONTNAME, 20);
        for (int i = 0; i < shopItems.length; i++) {
            int x = 100 + i * 200; // 각 아이템의 X 좌표
            int y = 200; // 아이템의 Y 좌표

            if (itemImage[i] != null) {
                g2d.drawImage(itemImage[i], x, y, 200, 200, null);
            } else {
                g2d.setColor(Color.RED);
                g2d.drawString("Image not found", x, y);
            }
            g2d.drawString("Price: " + itemPrices[i], x, y + 30);

            // 선택된 아이템을 표시
            if (i == selectedItem) {
                g2d.setColor(Color.RED); // 선택된 아이템은 빨간색 테두리로 표시
                g2d.drawRect(x , y , 200, 200);
                g2d.setColor(Color.WHITE); // 다시 기본 색상으로 돌림
            }
        }
        //g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        GraphicsUtils.setFont(g2d, FONTNAME, 20);
        g2d.setColor(Color.YELLOW);
        g2d.drawString(shopMessage, frameWidth / 2 - 100, frameHeight - 50); // 하단에 메시지 출력
    }

    /*public void handleShopMouseClick(MouseEvent e) {
        Point clickPoint = e.getPoint();
        shopMessage = "";
        //System.out.println("Mouse clicked at: " + clickPoint);
        for (int i = 0; i < shopItems.length; i++) {
            int x = 150 + i * 200; // 아이템의 X 좌표
            int y = 200; // 아이템의 Y 좌표

            // 아이템의 영역을 확인하여 클릭된 아이템을 선택
            if (clickPoint.x >= x && clickPoint.x <= x + 100 && clickPoint.y >= y && clickPoint.y <= y + 100) {
                selectedItem = i;
                shopMessage = "Selected item: " + shopItems[i];
            }
        }

        // 선택된 아이템을 구매
        if (selectedItem != -1 && Money.getMoney() >= itemPrices[selectedItem]) {
            Money.subtractMoney(itemPrices[selectedItem]);
            shopMessage = shopItems[selectedItem] + " purchased! Remaining money: " + Money.getMoney();
            game.setSelectedMenuImage(selectedItem);
        } else {
            shopMessage = "Not enough money to buy ";
        }
    }*/
}
