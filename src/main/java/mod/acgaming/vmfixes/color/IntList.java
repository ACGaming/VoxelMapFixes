package mod.acgaming.vmfixes.color;

import java.util.LinkedList;

// Courtesy of sblectric
public class IntList extends LinkedList<Integer>
{
    public IntList()
    {
        super();
    }

    public int average()
    {
        int a = 0;
        for (int i : this)
        {
            a += i;
        }
        return a / this.size();
    }
}