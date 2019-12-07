package solver;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;


public class Places {
    private ArrayList<ArrayList<PlacesSizeEntry>> rowPlacesSize;
    private ArrayList<ArrayList<PlacesSizeEntry>> colPlacesSize;
    private ArrayList<ArrayList<PlacesSizeEntry>> boxPlacesSize;
    private PriorityQueue<PlacesSizeEntry> placesQueue;
    private Comparator<PlacesSizeEntry> placesSizeComparator;
    private final char empty = '0';


    public Places(int size) {
        placesSizeComparator = new Comparator<PlacesSizeEntry>() {
            public int compare(PlacesSizeEntry e, PlacesSizeEntry f) {
                if (!e.isFilledIn && f.isFilledIn) {
                    return -1;
                }
                else if (e.isFilledIn && !f.isFilledIn) {
                    return 1;
                }
                return e.size - f.size;
            }
        };
        placesQueue = new PriorityQueue<PlacesSizeEntry>(size*size, placesSizeComparator);
        fillPlaces(size);
    }

    public PlacesSizeEntry peek() {
        return placesQueue.peek();
    }


    public int[] getPosFromPlaces(PlacesSizeEntry pe, char[][] grid, int size, Domains domains) {
        int[] placepos = new int[2];
        int num = pe.num;
        if (pe.indexType == PlacesSizeEntry.ROW) {
            placepos[0] = pe.index;
            for (int j = 0; j < size; j++) {
                if (domains.isInDomain(pe.index, j, num)) {
                    placepos[1] = j;
                    if (grid[placepos[0]][placepos[1]] != empty) {
                        System.out.println("0 OOPS V BAD");
                        System.out.println(pe.isFilledIn);
                        System.out.println(pe.index);
                        System.out.println(pe.indexType);
                        System.out.println(pe.num);
                        System.out.println(pe);
                        //show(grid);
                    }
                    return placepos;
                }
            }
        }
        else if (pe.indexType == PlacesSizeEntry.COL) {
            placepos[1] = pe.index;
            for (int i = 0; i < size; i++) {
                if (domains.isInDomain(i, pe.index, num)) {
                    placepos[0] = i;
                    if (grid[placepos[0]][placepos[1]] != empty) {
                        System.out.println("1 OOPS V BAD");
                        System.out.println(pe.isFilledIn);
                    }
                    return placepos;
                }
            }
        }
        else if (pe.indexType == PlacesSizeEntry.BOX) {
            int root = (int)(Math.sqrt(size));
            int box = pe.index;
            for (int[] boxpos : Util.getBoxPositions(box, root)) {
                int i = boxpos[0];
                int j = boxpos[1];
                if (domains.isInDomain(i, j, num)) {
                    placepos[0] = i;
                    placepos[1] = j;
                    if (grid[placepos[0]][placepos[1]] != empty) {
                        System.out.println("2 OOPS V BAD");
                        System.out.println(pe.isFilledIn);
                    }

                    return placepos;
                }
            }
        }
        System.out.println("3 OOPS V BAD");

        placepos[0] = -1;
        placepos[1] = -1;
        return placepos;
    }

    private void fillPlaces(int size) {
        rowPlacesSize = new ArrayList<ArrayList<PlacesSizeEntry>>();
        colPlacesSize = new ArrayList<ArrayList<PlacesSizeEntry>>();
        boxPlacesSize = new ArrayList<ArrayList<PlacesSizeEntry>>();

        for (int i = 0; i < size; i++) {
            rowPlacesSize.add(new ArrayList<PlacesSizeEntry>());
            colPlacesSize.add(new ArrayList<PlacesSizeEntry>());
            boxPlacesSize.add(new ArrayList<PlacesSizeEntry>());

            for (int num = 0; num < size; num++) {
                PlacesSizeEntry e = new PlacesSizeEntry(i, num, size, PlacesSizeEntry.ROW);
                PlacesSizeEntry f = new PlacesSizeEntry(i, num, size, PlacesSizeEntry.COL);
                PlacesSizeEntry g = new PlacesSizeEntry(i, num, size, PlacesSizeEntry.BOX);
                placesQueue.add(e);
                placesQueue.add(f);
                placesQueue.add(g);
                rowPlacesSize.get(i).add(e);
                colPlacesSize.get(i).add(f);
                boxPlacesSize.get(i).add(g);
            }
        }
    }

    public void fillIn(int i, int j, int box, int num) {
        PlacesSizeEntry re = rowPlacesSize.get(i).get(num);
        PlacesSizeEntry ce = colPlacesSize.get(j).get(num);
        PlacesSizeEntry be = boxPlacesSize.get(box).get(num);
        re.isFilledIn = true;
        ce.isFilledIn = true;
        be.isFilledIn = true;
        placesQueue.remove(re);
        placesQueue.remove(ce);
        placesQueue.remove(be);
        placesQueue.add(re);
        placesQueue.add(ce);
        placesQueue.add(be);
    }

    public void remove(int r, int c, int box, int num) {
        PlacesSizeEntry re = rowPlacesSize.get(r).get(num);
        re.size--;
        PlacesSizeEntry ce = colPlacesSize.get(c).get(num);
        ce.size--;
        PlacesSizeEntry be = boxPlacesSize.get(box).get(num);
        be.size--;

        placesQueue.remove(re);
        placesQueue.remove(ce);
        placesQueue.remove(be);
        placesQueue.add(re);
        placesQueue.add(ce);
        placesQueue.add(be);


    }

    public void add(int r, int c, int box, int num) {
        PlacesSizeEntry re = rowPlacesSize.get(r).get(num);
        PlacesSizeEntry ce = colPlacesSize.get(c).get(num);
        PlacesSizeEntry be = boxPlacesSize.get(box).get(num);

        re.size++;
        ce.size++;
        be.size++;
        placesQueue.remove(re);
        placesQueue.remove(ce);
        placesQueue.remove(be);
        placesQueue.add(re);
        placesQueue.add(ce);
        placesQueue.add(be);
    }

    public void unfill(int r, int c, int box, int num) {
        PlacesSizeEntry ore = rowPlacesSize.get(r).get(num);
        PlacesSizeEntry oce = colPlacesSize.get(c).get(num);
        PlacesSizeEntry obe = boxPlacesSize.get(box).get(num);
        ore.isFilledIn = false;
        oce.isFilledIn = false;
        obe.isFilledIn = false;
        placesQueue.remove(ore);
        placesQueue.remove(oce);
        placesQueue.remove(obe);
        placesQueue.add(ore);
        placesQueue.add(oce);
        placesQueue.add(obe);

    }

    public void updateFromChange(int r, int c, int box, ArrayList<Integer> addedNums, ArrayList<Integer> removedNums) {
        for (int num : addedNums) {
            add(r, c, box, num);
        }
        for (int num : removedNums) {
            remove(r, c, box, num);
        }
    }

}