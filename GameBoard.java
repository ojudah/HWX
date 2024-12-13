import java.util.Random;

public class GameBoard {
    private static final char WATER = '-';
    private static final char SHIP = 'S';
    private static final char HIT = 'X';
    private static final char MISS = 'O';

    private final char[][] gameBoard;

    public GameBoard(int size, int totalShips) {
        gameBoard = new char[size][size];
        for (char[] row : gameBoard) {
            java.util.Arrays.fill(row, WATER);
        }
        placeShips(totalShips);
    }

    public char getCell(int row, int col) {
        return gameBoard[row][col];
    }

    public boolean attackCell(int row, int col) {
        if (gameBoard[row][col] == SHIP) {
            gameBoard[row][col] = HIT;
            return true;
        } else if (gameBoard[row][col] == WATER) {
            gameBoard[row][col] = MISS;
        }
        return false;
    }

    // Checks if game over
    public boolean allShipsSunk() {
        for (char[] row : gameBoard) {
            for (char cell : row) {
                if (cell == SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    // Random ship placement
    private void placeShips(int totalShips) {
        Random random = new Random();
        int placedShips = 0;
        int[] shipLengths = {5, 4, 3, 3, 2};

        while (placedShips < totalShips) {
            int shipLength = shipLengths[placedShips];
            int row = random.nextInt(gameBoard.length);
            int col = random.nextInt(gameBoard.length);
            boolean isHorizontal = random.nextBoolean();

            if (canPlaceShip(row, col, shipLength, isHorizontal)) {
                placeShip(row, col, shipLength, isHorizontal);
                placedShips++;
            }
        }
    }

    // Checks if ship is placed in valid spot
    private boolean canPlaceShip(int row, int col, int length, boolean isHorizontal) {
        if (isHorizontal) {
            if (col + length > gameBoard.length) return false;
            for (int i = 0; i < length; i++) {
                if (gameBoard[row][col + i] != WATER) return false;
            }
        } else {
            if (row + length > gameBoard.length) return false;
            for (int i = 0; i < length; i++) {
                if (gameBoard[row + i][col] != WATER) return false;
            }
        }
        return true;
    }
    
    private void placeShip(int row, int col, int length, boolean isHorizontal) {
        for (int i = 0; i < length; i++) {
            if (isHorizontal) {
                gameBoard[row][col + i] = SHIP;
            } else {
                gameBoard[row + i][col] = SHIP;
            }
        }
    }

    public char[][] getBoardCopy() {
        char[][] boardCopy = new char[gameBoard.length][gameBoard[0].length];
        for (int i = 0; i < gameBoard.length; i++) {
            System.arraycopy(gameBoard[i], 0, boardCopy[i], 0, gameBoard[i].length);
        }
        return boardCopy;
    }
}
