package solver;

public class DomainSizeEntry extends SizeEntry {
    public int r;
    public int c;
    public boolean isEmpty;
    public DomainSizeEntry(int r, int c, int size, boolean isEmpty) {
        this.r = r;
        this.c = c;
        this.size = size;
        this.isEmpty = isEmpty;
    }
}