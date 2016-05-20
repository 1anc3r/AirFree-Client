package me.lancer.airfree.util;

public class GestureUtil {

    private static final String[] KEY = { "a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z" };

    private static final String[] VALUE = { "", "11", "1", "5", "3", "", "",
            "9", "", "7", "", "11", "", "8", "", "9", "", "6", "4", "2",
            "", "", "10", "10", "", "3" };

    public static String getCommand(String value) {
        for (int i = 0; i < KEY.length && i < VALUE.length; i++) {
            if (value.equals(KEY[i])) {
                return VALUE[i];
            }
        }
        return null;
    }
}
