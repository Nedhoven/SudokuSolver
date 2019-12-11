package solver;

import java.io.*;
import java.util.Arrays;

public class IO {

    private final String folder;
    private final String out;
    private final Integer size;

    public IO(String inputPath, String outputPath, int n) {
        folder = inputPath;
        out = outputPath;
        size = n;
    }

    private int getIndex(char c) {
        return (c - '1') < 9 ? (c - '1') : (c - 'A' + 9);
    }

    private int getNum(String in) {
        return Integer.parseInt(in);
    }

    private char getChar(String in) {
        int temp = Integer.parseInt(in);
        return temp < 9 ? (char) (temp + '1') : (char) (temp + 'A' - 9);
    }

    private int[] convert(char[] arr) {
        int[] ans = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            ans[i] = getIndex(arr[i]);
        }
        return ans;
    }

    public char[][] readChar(String name) throws IOException {
        File file = new File(folder + name);
        BufferedReader br = new BufferedReader(new FileReader(file));
        char[][] board = new char[size][size];
        int row = 0;
        int col = 0;
        String line;
        while ((line = br.readLine()) != null) {
            String[] text = line.split(" ");
            for (String num : text) {
                board[row][col] = getChar(num);
                col++;
                if (col == size) {
                    row++;
                    col = 0;
                }
            }
            if (row == size) {
                break;
            }
        }
        return board;
    }

    public int[][] readInt(String name) throws IOException {
        File file = new File(folder + name);
        BufferedReader br = new BufferedReader(new FileReader(file));
        int[][] board = new int[size][size];
        int row = 0;
        int col = 0;
        String line;
        while ((line = br.readLine()) != null) {
            String[] text = line.split(" ");
            for (String num : text) {
                board[row][col] = getNum(num);
                col++;
                if (col == size) {
                    row++;
                    col = 0;
                }
            }
            if (row == size) {
                break;
            }
        }
        return board;
    }

    public void write(char[][] board, String name) throws IOException {
        File file = new File(out + name);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
        for (char[] arr : board) {
            int[] temp = convert(arr);
            bw.append(Arrays.toString(temp));
            bw.append('\n');
        }
        bw.close();
    }

    public void write(int[][] board, String name) throws IOException {
        File file = new File(out + name);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
        for (int[] arr : board) {
            bw.append(Arrays.toString(arr));
            bw.append('\n');
        }
        bw.close();
    }
    
    public void writeSerial(char[][] board, String name) throws IOException {
        File file = new File(out + name);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
        for (char[] arr : board) {
            int[] temp = convert(arr);
            for (int num : temp) {
                String txt = num + " ";
                bw.append(txt);
            }
        }
        bw.close();
    }

    public void writeSerial(int[][] board, String name) throws IOException {
        File file = new File(out + name);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
        for (int[] arr : board) {
            for (int num : arr) {
                String txt = num + " ";
                bw.append(txt);
            }
        }
        bw.close();
    }

}
