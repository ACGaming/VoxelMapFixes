package mod.acgaming.vmfixes.color;

import java.awt.*;
import java.util.List;

// Courtesy of sblectric
public class ColorHelper
{
    public static int blueFromColor(int col)
    {
        return col & 0xFF;
    }

    public static int greenFromColor(int col)
    {
        return (col >> 8) & 0xFF;
    }

    public static int redFromColor(int col)
    {
        return (col >> 16) & 0xFF;
    }

    public static int averageColors(List<Integer> cols)
    {
        IntList reds = new IntList();
        IntList greens = new IntList();
        IntList blues = new IntList();

        for (int col : cols)
        {
            reds.add(redFromColor(col));
            greens.add(greenFromColor(col));
            blues.add(blueFromColor(col));
        }

        return new Color(reds.average(), greens.average(), blues.average()).getRGB();
    }
}