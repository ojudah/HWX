import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.sampled.*;

public class BattleshipGUI {
    private final Battleship game;
    private final JFrame frame;
    private final JPanel playerBoardPanel;
    private final JPanel opponentBoardPanel;
    private final JLabel statusLabel;
    private boolean cellSelected; // Flag to allow only one cell selection

    public BattleshipGUI(Battleship game) {
        this.game = game;
        frame = new JFrame("BATTLESHIP");
        statusLabel = new JLabel();
        playerBoardPanel = new JPanel();
        opponentBoardPanel = new JPanel();
        cellSelected = false;
        initialize();
    }

    private void initialize() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Display the start screen
        showStartScreen();

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Game Rules");
        JMenuItem rulesMenuItem = new JMenuItem("Game Rules");
        rulesMenuItem.addActionListener(e -> showGameRules());
        helpMenu.add(rulesMenuItem);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);

        // Player Status
        statusLabel.setText(game.getCurrentPlayer().getName() + ": Take Your Shot!");
        statusLabel.setFont(new Font("Elephant", Font.PLAIN, 20));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(statusLabel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        boardPanel.setBackground(new Color(0, 0, 139));
        setupBoard(playerBoardPanel, game.getCurrentPlayer(), true);
        setupBoard(opponentBoardPanel, game.getOpponentPlayer(), false);

        boardPanel.add(playerBoardPanel);
        boardPanel.add(opponentBoardPanel);
        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton endTurnButton = new JButton("End Turn");
        endTurnButton.addActionListener(e -> handleEndTurn());
        controlPanel.add(endTurnButton);

        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }

    // Welcome page so players can get ready
    private void showStartScreen() {
        ImageIcon ship = new ImageIcon("ship.png");

        JOptionPane.showMessageDialog(frame, 
            "Welcome to Battleship!\nGet ready for BATTLE.\nPlayer 1 will start.\nPress OK when ready.\n(Make sure turn on your volume for the full experience!)", 
            "Welcome", 
            JOptionPane.INFORMATION_MESSAGE, ship);
    }

    // For menu
    private void showGameRules() {
        String rules = "Battleship Rules:\n\n"
                + "1. Each player has a 10x10 grid with randomized ship placements (...).\n"
                + "    -The ... symbol for your ship will disappear when your opponent hits your ship.\n"
                + "2. Players take turns to shoot at the opponent's grid - select a cell to do so. \n"
                + "3. The goal is to sink all of the opponent's ships.\n"
                + "4. There are 5 ships of size 5, 4, 3, 3, 2.\n"
                + "4. Red means a miss, and green means a hit.\n"
                + "5. The game ends when all ships of a player are sunk.\n";
        
        JOptionPane.showMessageDialog(frame, rules, "Game Rules", JOptionPane.INFORMATION_MESSAGE);
    }

    // Sets up next players board
    public void updateForNextTurn() {
        cellSelected = false;
        setupBoard(playerBoardPanel, game.getCurrentPlayer(), true);
        setupBoard(opponentBoardPanel, game.getOpponentPlayer(), false);
    }

    public void show() {
        frame.setVisible(true);
    }

    private void setupBoard(JPanel panel, Player player, boolean isPlayerBoard) {
        int size = player.getBoard().getBoardCopy().length;
        panel.removeAll();
        panel.setLayout(new GridLayout(size, size));

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40, 40));

                if (isPlayerBoard) {
                    char cell = player.getBoard().getCell(row, col);
                    button.setText(cell == 'S' ? "S" : "");
                } else {
                    char cell = player.getBoard().getCell(row, col);
                    if (cell == 'X') {
                        ImageIcon explosion = new ImageIcon("explosion.gif");
                        button.setIcon(explosion);
                        button.setText("X");
                        button.setBackground(Color.GREEN);
                    } else if (cell == 'O') {
                        button.setText("O");
                        button.setBackground(Color.RED);
                    } else {
                        button.addActionListener(new OpponentBoardClickListener(row, col));
                    }
                }
                panel.add(button);
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    private void handleEndTurn() {
        if (game.isGameOver()) {
            JOptionPane.showMessageDialog(frame, "Game Over! " + game.getOpponentPlayer().getName() + " lost.");
        } else {
            // Hide boards while switching
            playerBoardPanel.setVisible(false);
            opponentBoardPanel.setVisible(false);

            JOptionPane.showMessageDialog(frame, "Switching turns! Press OK when ready.");

            // Show boards again
            playerBoardPanel.setVisible(true);
            opponentBoardPanel.setVisible(true);

            game.switchTurn();
            statusLabel.setText(game.getCurrentPlayer().getName() + ": Take Your Shot!");
            updateForNextTurn();
        }
    }

    // Handles cell actions and listens
    private class OpponentBoardClickListener implements ActionListener {
        private final int row;
        private final int col;

        public OpponentBoardClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (cellSelected) {
                JOptionPane.showMessageDialog(frame, "You have already made your move this turn!");
                return;
            }

            Player opponent = game.getOpponentPlayer();
            JButton button = (JButton) e.getSource();

            if (opponent.getBoard().attackCell(row, col)) {
                playSoundEffect("explosion-01.wav");
                ImageIcon explosion = new ImageIcon("explosion.gif");
                button.setIcon(explosion);
                button.setBackground(Color.GREEN);
                button.setText("X");
            } else {
                playSoundEffect("water-splash-3.wav");
                button.setText("O");
                button.setBackground(Color.RED);
            }
            button.setEnabled(false);

            cellSelected = true;

            if (game.isGameOver()) {
                JOptionPane.showMessageDialog(frame, "Game Over! " + opponent.getName() + " lost.");
            }
        }
    }

    private void playSoundEffect(String soundFile) {
        try {
            // Load the sound file (make sure the file path is correct)
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(soundFile));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // Play the sound
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
