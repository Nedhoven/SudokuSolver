
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

    private int getNum(String in) {
        switch (in) {
            case "-1":
                return -1;
            case "0":
                return 0;
            case "1":
                return 1;
            case "2":
                return 2;
            case "3":
                return 3;
            case "4":
                return 4;
            case "5":
                return 5;
            case "6":
                return 6;
            case "7":
                return 7;
            case "8":
                return 8;
            case "9":
                return 9;
            case "10":
                return 10;
            case "11":
                return 11;
            case "12":
                return 12;
            case "13":
                return 13;
            case "14":
                return 14;
            case "15":
                return 15;
            case "16":
                return 16;
            case "17":
                return 17;
            case "18":
                return 18;
            case "19":
                return 19;
            case "20":
                return 20;
            case "21":
                return 21;
            case "22":
                return 22;
            case "23":
                return 23;
            case "24":
                return 24;
            default:
                System.err.println("ERROR ON FINDING THE NUMBER!");
                return -2;
        }
    }

    private char getChar(String in) {
        switch (in) {
            case "-1":
                return '0';
            case "0":
                return '1';
            case "1":
                return '2';
            case "2":
                return '3';
            case "3":
                return '4';
            case "4":
                return '5';
            case "5":
                return '6';
            case "6":
                return '7';
            case "7":
                return '8';
            case "8":
                return '9';
            case "9":
                return 'A';
            case "10":
                return 'B';
            case "11":
                return 'C';
            case "12":
                return 'D';
            case "13":
                return 'E';
            case "14":
                return 'F';
            case "15":
                return 'G';
            case "16":
                return 'H';
            case "17":
                return 'I';
            case "18":
                return 'J';
            case "19":
                return 'K';
            case "20":
                return 'L';
            case "21":
                return 'M';
            case "22":
                return 'N';
            case "23":
                return 'O';
            case "24":
                return 'P';
            default:
                System.err.println("ERROR ON FINDING THE CHARACTER!");
                return '-';
        }
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
            bw.append(Arrays.toString(arr));
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

}
