package solver;

public class PlacesSizeEntry extends SizeEntry {
    public static int numCreated = 0;
    public static final int ROW = 0;
    public static final int COL = 1;
    public static final int BOX = 2;
    public int index;
    public int num;
    public int indexType;
    public boolean isFilledIn;
    public PlacesSizeEntry(int index, int num, int esize, int indexType) {
        this.index = index;
        this.num = num;
        this.indexType = indexType;
        this.size = esize;
        this.isFilledIn = false;
        numCreated++;
    }
}