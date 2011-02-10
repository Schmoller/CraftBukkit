package net.minecraft.server;
import java.util.ArrayList;
import java.util.Arrays;

public class LongHashset<V> extends LongHash<V> {
    long values[][][] = new long[256][][];
    int count = 0;
    
    public boolean isEmpty() {
        return count == 0;
    }
    
    public void add(int msw, int lsw) {
        add(toLong(msw, lsw));
    }

    public void add(long key) {
        int mainIdx = (int) (key & 255);
        int outerIdx = (int) ((key >> 32) & 255);
        long outer[][] = values[mainIdx], inner[];
        if(outer == null) values[mainIdx] = outer = new long[256][];
        inner = outer[outerIdx];
        if(inner == null) {
            outer[outerIdx] = inner = new long[1];
            inner[0] = key;
            count++;
        }
        else {
            int i;
            for(i = 0; i < inner.length; i++) {
                if(inner[i] == key) {
                    return;
                }
            }
            outer[0] = inner = Arrays.copyOf(inner, i+1);
            inner[i] = key;
            count++;
        }
    }
    
    public boolean containsKey(long key) {
        int mainIdx = (int) (key & 255);
        int outerIdx = (int) ((key >> 32) & 255);
        long outer[][] = values[mainIdx], inner[];
        if(outer == null) return false;
        inner = outer[outerIdx];
        if(inner == null) return false;
        else {
            for(long entry : inner) {
                if(entry == key) return true;
            }
            return false;
        }
    }
    
    public void remove(long key) {
        long[][] outer = this.values[(int) (key & 255)];
        if (outer == null) return;

        long[] inner = outer[(int) ((key >> 32) & 255)];
        if (inner == null) return;
        
        int max = inner.length - 1;
        for(int i = 0; i <= max; i++) {
            if(inner[i] == key) {
                count--;
                if(i != max) {
                    inner[i] = inner[max];
                }
                outer[(int) ((key >> 32) & 255)] = (max == 0 ? null : Arrays.copyOf(inner, max));
                return;
            }
        }
    }
    
    public long popFirst() {
        for(long[][] outer : values) {
            if(outer == null) continue;
            for(int i = 0; i < outer.length ; i++) {
                long[] inner = outer[i];
                if(inner == null || inner.length == 0) continue;
                count--;
                long ret = inner[inner.length - 1];
                outer[i] = Arrays.copyOf(inner, inner.length - 1);
                return ret;
            }
        }
        return 0;
    }

    public long[] keys() {
        int index = 0;
        long ret[] = new long[count];
        for(long[][] outer : values) {
            if(outer == null) continue;
            for(long[] inner : outer) {
                if(inner == null) continue;
                for(long entry : inner) {
                    ret[index++] = entry;
                }
            }
        }
        return ret;
    }
}