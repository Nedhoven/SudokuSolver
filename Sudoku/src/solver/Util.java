package solver;

import java.util.ArrayList;

public class Util {
    // Convert a grid character into an integer value
    public static int getNum(char c) {
        return (c - '1') < 9 ? (c - '1') : (c - 'A' + 9);
    }

    // Convert an integer value into a grid character
    public static char getChar(int num) {
        return num < 9 ? (char) (num + '1') : (char) (num + 'A' - 9);
    }


    public static int getBox(int r, int c, int root) {
        return root*(r/root) + (c/root);
    }

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