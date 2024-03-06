package conwaygame;

import java.util.ArrayList;

/**
 * Conway's Game of Life Class holds various methods that will
 * progress the state of the game's board through it's many
 * iterations/generations.
 *
 * Rules
 * Alive cells with 0-1 neighbors die of loneliness.
 * Alive cells with >=4 neighbors die of overpopulation.
 * Alive cells with 2-3 neighbors survive.
 * Dead cells with exactly 3 neighbors become alive by reproduction.
 * 
 * @author Seth Kelley
 * @author Maxwell Goldberg
 */
public class GameOfLife {

    // Instance variables
    private static final boolean ALIVE = true;
    private static final boolean DEAD = false;

    private boolean[][] grid; // The board has the current generation of cells
    private int totalAliveCells; // Total number of alive cells in the grid (board)

    /**
     * Default Constructor which creates a small 5x5 grid with five alive cells.
     * This variation does not exceed bounds and dies off after four iterations.
     */
    public GameOfLife() {
        grid = new boolean[5][5];
        totalAliveCells = 5;
        grid[1][1] = ALIVE;
        grid[1][3] = ALIVE;
        grid[2][2] = ALIVE;
        grid[3][2] = ALIVE;
        grid[3][3] = ALIVE;
    }

    /**
     * Constructor used that will take in values to create a grid with a given
     * number
     * of alive cells
     * 
     * @param file is the input file with the initial game pattern formatted as
     *             follows:
     *             An integer representing the number of grid rows, say r
     *             An integer representing the number of grid columns, say c
     *             Number of r lines, each containing c true or false values (true
     *             denotes an ALIVE cell)
     */
    public GameOfLife(String file) {
        StdIn.setFile(file);
        totalAliveCells = 0;
        int rows = Integer.parseInt(StdIn.readLine());
        int cols = Integer.parseInt(StdIn.readLine());
        grid = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = StdIn.readBoolean();
                if (grid[i][j])
                    totalAliveCells++;
            }
        }
    }

    /**
     * Returns grid
     * 
     * @return boolean[][] for current grid
     */
    public boolean[][] getGrid() {
        return grid;
    }

    /**
     * Returns totalAliveCells
     * 
     * @return int for total number of alive cells in grid
     */
    public int getTotalAliveCells() {
        return totalAliveCells;
    }

    /**
     * Returns the status of the cell at (row,col): ALIVE or DEAD
     * 
     * @param row row position of the cell
     * @param col column position of the cell
     * @return true or false value "ALIVE" or "DEAD" (state of the cell)
     */
    public boolean getCellState(int row, int col) {
        if (grid[row][col])
            return ALIVE;
        return DEAD;
    }

    /**
     * Returns true if there are any alive cells in the grid
     * 
     * @return true if there is at least one cell alive, otherwise returns false
     */
    public boolean isAlive() {
        for (boolean[] i : grid)
            for (boolean j : i)
                if (j)
                    return true;
        return false;
    }

    /**
     * Determines the number of alive cells around a given cell.
     * Each cell has 8 neighbor cells which are the cells that are
     * horizontally, vertically, or diagonally adjacent.
     * 
     * @param col column position of the cell
     * @param row row position of the cell
     * @return neighboringCells, the number of alive cells (at most 8).
     */
    public int numOfAliveNeighbors(int row, int col) {

        int[] rowCheck = { row - 1, row, row + 1 };
        int[] colCheck = { col - 1, col, col + 1 };
        int count = 0;

        if (row == 0)
            rowCheck[0] = grid.length - 1;
        if (row == grid.length - 1)
            rowCheck[2] = 0;
        if (col == 0)
            colCheck[0] = grid[row].length - 1;
        if (col == grid[row].length - 1)
            colCheck[2] = 0;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (grid[rowCheck[i]][colCheck[j]])
                    count++;
        if (grid[row][col])
            count--;

        return count;
    }

    /**
     * Creates a new grid with the next generation of the current grid using
     * the rules for Conway's Game of Life.
     * 
     * @return boolean[][] of new grid (this is a new 2D array)
     */
    public boolean[][] computeNewGrid() {
        boolean[][] temp = new boolean[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                int alive = numOfAliveNeighbors(i, j);

                if (alive <= 1) {
                    temp[i][j] = DEAD;
                    totalAliveCells--;
                } else if ((alive == 2 || alive == 3) && grid[i][j] == ALIVE)
                    temp[i][j] = ALIVE;
                else if (alive == 3 && grid[i][j] == DEAD) {
                    temp[i][j] = ALIVE;
                    totalAliveCells++;
                } else {
                    temp[i][j] = DEAD;
                    totalAliveCells--;
                }
            }
        }

        return temp;
    }

    /**
     * Updates the current grid (the grid instance variable) with the grid denoting
     * the next generation of cells computed by computeNewGrid().
     * 
     * Updates totalAliveCells instance variable
     */
    public void nextGeneration() {
        grid = computeNewGrid();
        totalAliveCells = 0;
        for (boolean[] i : grid)
            for (boolean j : i)
                if (j == ALIVE)
                    totalAliveCells++;
    }

    /**
     * Updates the current grid with the grid computed after multiple (n)
     * generations.
     * 
     * @param n number of iterations that the grid will go through to compute a new
     *          grid
     */
    public void nextGeneration(int n) {
        for (int i = 0; i < n; i++)
            nextGeneration();
    }

    /**
     * Determines the number of separate cell communities in the grid
     * 
     * @return the number of communities in the grid, communities can be formed from
     *         edges
     */
    public int numOfCommunities() {
        ArrayList<Integer> x = new ArrayList<Integer>();
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(grid.length, grid[0].length);
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (getCellState(row, col)) {
                    // same code as numOfAliveNeighbors
                    int[] rowCheck = { row - 1, row, row + 1 };
                    int[] colCheck = { col - 1, col, col + 1 };
                    if (row == 0)
                        rowCheck[0] = grid.length - 1;
                    if (row == grid.length - 1)
                        rowCheck[2] = 0;
                    if (col == 0)
                        colCheck[0] = grid[row].length - 1;
                    if (col == grid[row].length - 1)
                        colCheck[2] = 0;
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (getCellState(rowCheck[i], colCheck[j]))
                                uf.union(rowCheck[i], colCheck[j], row, col);
                        }
                    }
                }
            }
        }
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (getCellState(row, col)) {
                    if (x.contains(uf.find(row, col)))
                        continue;
                    else
                        x.add(uf.find(row, col));
                }
            }
        }
        return x.size();
    }
}