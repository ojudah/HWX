public class Battleship {
    private final Player player1;
    private final Player player2;
    private boolean isPlayer1Turn;
    private BattleshipGUI gui;

    public Battleship(int boardSize, int totalShips) {
        player1 = new Player("Player 1", boardSize, totalShips);
        player2 = new Player("Player 2", boardSize, totalShips);
        isPlayer1Turn = true;
    }

    public void start() {
        gui = new BattleshipGUI(this);
        gui.show();
    }

    public Player getCurrentPlayer() {
        return isPlayer1Turn ? player1 : player2;
    }

    public Player getOpponentPlayer() {
        return isPlayer1Turn ? player2 : player1;
    }

    public void switchTurn() {
        isPlayer1Turn = !isPlayer1Turn;
        gui.updateForNextTurn();
    }

    public boolean isGameOver() {
        return getOpponentPlayer().getBoard().allShipsSunk();
    }

    public static void main(String[] args) {
        Battleship game = new Battleship(10, 5); // Default size and number of ships
        game.start();
    }
}
