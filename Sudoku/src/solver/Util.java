package solver;

import java.util.ArrayList;

public class Util {
    public static ArrayList<int[]> getBoxPositions(int box, int root) {
        //System.out.println(String.valueOf(box));
        ArrayList<int[]> positions = new ArrayList<int[]>();
        for (int i = 0; i < root; i++) {
            //System.out.print('\n');
            for (int j = 0; j < root; j++) {
                int bi = box / root;
                int bj = box % root;
                int[] pos = new int[2];
                pos[0] = root*bi + i;
                pos[1] = root*bj + j;
                //System.out.print(String.valueOf(pos[0]) + ", " + String.valueOf(pos[1]) + " | ");
                positions.add(pos);
            }
        }
        //System.out.println();
        return positions;
    }

}