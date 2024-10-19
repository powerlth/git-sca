package kr.jbnu.se.std;

public class Money {
    public static int money;
    public static void addMoney(int amount) {
        money += amount;
    }

    public static void subtractMoney(int amount) {
        if (money >= amount) {
            money -= amount;
        }
    }

    public static int getMoney() {
        return money;
    }

    public static void setMoney(int amount) {
        money = amount;
    }
}
