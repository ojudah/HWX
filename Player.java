public class Player {
    private final String name;
    private final GameBoard board;

    public Player(String name, int boardSize, int totalShips) {
        this.name = name;
        this.board = new GameBoard(boardSize, totalShips);
    }

    public String getName() {
        return name;
    }

    public GameBoard getBoard() {
        return board;
    }
}
