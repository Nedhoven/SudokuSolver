
#include <iostream>
#include <vector>
#include <sstream>
#include <chrono>
#include "example.cpp"

using namespace std;
using namespace std::chrono;

int size = 9;
int root = 3;
const char empty = '0';

void show(const vector< vector<char> > &board)
{
    for (int i = 0; i < size; i++)
    {
        cout << "[";
        for (int j = 0; j < size - 1; j++)
        {
            cout << board[i][j] << ", ";
        }
        cout << board[i][size - 1] << "]" << endl;
    }
}

void show(const vector< vector<int> > &board)
{
    for (int i = 0; i < size; i++)
    {
        cout << "[";
        for (int j = 0; j < size - 1; j++)
        {
            cout << board[i][j] << ", ";
        }
        cout << board[i][size - 1] << "]" << endl;
    }
}

void show(const vector< vector<bool> > &board)
{
    for (int i = 0; i < size; i++)
    {
        cout << "[";
        for (int j = 0; j < size - 1; j++)
        {
            cout << board[i][j] << " ,";
        }
        cout << board[i][size - 1] << "]" << endl;
    }
}

void show(const vector<int> &arr)
{
    cout << "[";
    for (int i = 0; i < size - 1; i++)
    {
        cout << arr[i] << " ,";
    }
    cout << arr[size - 1] << "]" << endl;
}

int getIndex(const char c)
{
    return (c - '1') < 9 ? c - '1' : c - 'A' + 9;
}

char getChar(const int num)
{
   return num < 9 ? (char) (num + '1') : (char) (num + 'A' - 9);
}

void add(vector< vector<bool> > &row, vector< vector<bool> > &col,
         vector< vector<bool> > &box, int x, int y, int num, bool f)
{
    int b = root * (x / root) + (y / root);
    row[x][num] = f;
    col[y][num] = f;
    box[b][num] = f;
}

bool check(const vector< vector<bool> > &row, const vector< vector<bool> > &col,
           const vector< vector<bool> > &box, int x, int y, int num) {
    int b = root * (x / root) + (y / root);
    return !(row[x][num] || col[y][num] || box[b][num]);
}

int getRow(const vector<int> &rowVal)
{
    int max = -1;
    int ans = -1;
    // cout << "in getting row" << endl;
    for (int i = 0; i < size; i++)
    {
        // cout << "looking at row #" << i << " with row number = " << rowVal[i] << endl;
        if (rowVal[i] != size && rowVal[i] > max)
        {
            // cout << "in for row #" << i << endl;
            ans = i;
            max = rowVal[i];
        }
    }
    return ans;
}

int getCol(const vector< vector<char> > &board, const vector<int> &colVal, int r)
{
    int max = -1;
    int ans = -1;
    if (r == -1)
    {
        return ans;
    }
    // cout << "in getting col with row = " << r << endl;
    for (int j = 0; j < size; j++)
    {
        // cout << "checking " << board[r][j] << " current max = " << max << endl;
        if (board[r][j] == empty && colVal[j] > max)
        {
            ans = j;
            max = colVal[j];
        }
    }
    return ans;
}

bool dfs(vector< vector<char> > &board, vector< vector<bool> > &row,
           vector< vector<bool> > &col, vector< vector<bool> > &box, vector<int> &rowVal, vector<int> &colVal, int r, int c)
{
    if (r == -1 || c == -1) {
        return true;
    }
    // cout << "(" << r << "," << c << ")" << endl;
    for (int num = 0; num < size; num++) {
        // cout << "checking " << num + 1 << endl;
        if (check(row, col, box, r, c, num)) {
            // cout << "set " << num + 1 << " at (" << r << "," << c << ")" << endl;
            add(row, col, box, r, c, num, true);
            rowVal[r]++;
            colVal[c]++;
            board[r][c] = getChar(num);
            int new_r = getRow(rowVal);
            int new_c = getCol(board, colVal, new_r);
            // cout << "new row: " << new_r << ", new col: " << new_c << endl;
            if (dfs(board, row, col, box, rowVal, colVal, new_r, new_c)) {
                return true;
            }
            rowVal[r]--;
            colVal[c]--;
            board[r][c] = empty;
            add(row, col, box, r, c, num, false);
        }
    }
    // cout << "return false for (" << r << "," << c << ")" << endl;
    return false;
}

bool init(const vector< vector<char> > &board, vector< vector<bool> > &row,
          vector< vector<bool> > &col, vector< vector<bool> > &box, vector<int> &rowVal, vector<int> &colVal)
{
    bool flag = true;
    for (int i = 0; i < size; i++)
    {
        for (int j = 0; j < size; j++)
        {
            if (board[i][j] != empty)
            {
                rowVal[i]++;
                colVal[j]++;
                int num = getIndex(board[i][j]);
                if (row[i][num] || col[j][num])
                {
                    flag = false;
                }
                row[i][num] = true;
                col[j][num] = true;
            }
            int x = root * (i / root) + (j / root);
            int y = ((root * i) + (j % root)) % size;
            if (board[x][y] != empty)
            {
                int num = getIndex(board[x][y]);
                if (box[i][num])
                {
                    flag = false;
                }
                box[i][num] = true;
            }
        }
    }
    return flag;
}

int main() {
    size = 16;
    root = 4;
    vector< vector<char> > board = emptyGrid(size);
    auto startTime = high_resolution_clock::now();
    vector< vector<bool> > row(size, vector<bool>(size));
    vector< vector<bool> > col(size, vector<bool>(size));
    vector< vector<bool> > box(size, vector<bool>(size));
    vector<int> rowVal(size);
    vector<int> colVal(size);
    if (!init(board, row, col, box, rowVal, colVal))
    {
        cout << "NO SOLUTION FOUND!" << endl;
        return 0;
    }
    int r = getRow(rowVal);
    int c = getCol(board, colVal, r);
    if (!dfs(board, row, col, box, rowVal, colVal, r, c))
    {
        cout << "NO SOLUTION FOUND!" << endl;
        return 0;
    }
    auto endTime = high_resolution_clock::now();
    auto d = duration_cast<microseconds>(endTime - startTime);
    cout << "running time: " << d.count() << " us!" << endl;
    show(board);
    return 0;
}
