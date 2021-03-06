package org.societies;

import com.google.inject.Singleton;
import gnu.trove.map.hash.TCharIntHashMap;
import order.format.WidthProvider;
import org.bukkit.ChatColor;

/**
 * Represents a MinecraftWidthProvider
 */
//optimize
@Singleton
public class MinecraftWidthProvider implements WidthProvider {

    private final TCharIntHashMap widths = new TCharIntHashMap();

    @Override
    public double widthOf(String string) {
        return widthOf(string.toCharArray());
    }

    private double widthOf(char[] string) {
        int width = 0;

        for (int i = 0, length = string.length; i < length; i++) {
            char c = string[i];

            int next = i + 1;

            if (c == ChatColor.COLOR_CHAR && next < length) {
                ChatColor color = ChatColor.getByChar(string[next]);
                if (color != null) {
                    i++;
                    continue;
                }
            }

            width += widthOf(c);
        }

        return width;
    }

    @Override
    public double widthOf(StringBuilder string) {
        int count = string.length();
        char[] result = new char[count];
        string.getChars(0, count, result, 0);
        return widthOf(result);
    }

    @Override
    public double widthOf(char c) {
        int width = widths.get(c);
        return width == 0 ? 6 : width;
    }

    {
        addWidths("i.:,;|!", 2);
        addWidths("l'", 3);
        addWidths("tI[]", 4);
        addWidths("fk{}<>\"*()", 5);
        addWidths("abcdeghjmnopqrsuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890\\/#?$%-=_+&^", 6);
        addWidths("@~", 7);
        addWidths(" ", 4);
    }

    public void addWidths(String characters, int width) {
        for (char c : characters.toCharArray()) {
            widths.put(c, width);
        }
    }

}
