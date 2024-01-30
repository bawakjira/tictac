import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

interface GameObserver {
    void onGameStart();
    void onGameEnd(String winner);
}

class TicTacToe implements ActionListener {
    private JFrame frame;
    private JPanel buttonPanel;
    private JButton[] buttons;
    private JButton startButton;
    private JButton backButton; // Added back button
    private JTextField player1Field;
    private JTextField player2Field;
    private JLabel textField;
    private JLabel player1Label;
    private JLabel player2Label;
    private String player1Name;
    private String player2Name;
    private int player1Score = 0;
    private int player2Score = 0;
    private boolean player1Turn;
    private boolean playersRegistered = false; // Track if players are registered
    private List<GameObserver> observers = new ArrayList<>();

    public TicTacToe() {
        frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setLayout(new BorderLayout());

        textField = new JLabel();
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setHorizontalAlignment(JLabel.CENTER);
        textField.setText("Register players and click 'Play' to start.");
        frame.add(textField, BorderLayout.NORTH);

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(3, 3));
        frame.add(gamePanel, BorderLayout.CENTER);

        buttons = new JButton[9];
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.BOLD, 120));
            buttons[i].setBackground(Color.WHITE);
            buttons[i].setEnabled(false);
            buttons[i].addActionListener(this);
            gamePanel.add(buttons[i]);
        }

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        player1Field = new JTextField(10);
        player2Field = new JTextField(10);
        buttonPanel.add(player1Field);
        buttonPanel.add(player2Field);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player1Name = player1Field.getText();
                player2Name = player2Field.getText();
                if (!player1Name.isEmpty() && !player2Name.isEmpty()) {
                    resetGame(); // Reset the game when registering new players
                    textField.setText("Registered players: " + player1Name + " vs " + player2Name);
                    player1Label.setText(player1Name + ": 0");
                    player2Label.setText(player2Name + ": 0");
                    playersRegistered = true; // Set playersRegistered to true
                    enableGameControls();
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter both player names.");
                }
            }
        });
        buttonPanel.add(registerButton);

        startButton = new JButton("Play");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
                notifyObserversOnGameStart();
            }
        });
        buttonPanel.add(startButton);

        backButton = new JButton("Back"); // Create a new back button
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the current TicTacToe window
                // Add your code here to go back to the previous menu or window
            }
        });
        buttonPanel.add(backButton); // Add the back button to the button panel

        frame.add(buttonPanel, BorderLayout.SOUTH);

        player1Label = new JLabel();
        player1Label.setFont(new Font("Arial", Font.BOLD, 20));
        player1Label.setHorizontalAlignment(JLabel.CENTER);
        frame.add(player1Label, BorderLayout.WEST);

        player2Label = new JLabel();
        player2Label.setFont(new Font("Arial", Font.BOLD, 20));
        player2Label.setHorizontalAlignment(JLabel.CENTER);
        frame.add(player2Label, BorderLayout.EAST);

        // Disable the "Play" button initially
        startButton.setEnabled(false);

        frame.setVisible(true);
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    private void notifyObserversOnGameStart() {
        for (GameObserver observer : observers) {
            observer.onGameStart();
        }
    }

    private void notifyObserversOnGameEnd(String winner) {
        for (GameObserver observer : observers) {
            observer.onGameEnd(winner);
        }
    }

    private void enableGameControls() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setEnabled(true);
        }
        startButton.setEnabled(playersRegistered); // Enable the "Play" button if players are registered
    }

    private void disableGameControls() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setEnabled(false);
        }
        startButton.setEnabled(playersRegistered); // Disable the "Play" button if players are not registered
    }

    private void startGame() {
        player1Turn = true;
        clearBoard();
        enableGameControls();
        textField.setText(player1Name + "'s turn.");
    }

    private void clearBoard() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
            buttons[i].setBackground(Color.WHITE);
        }
    }

    private void updateScores() {
        player1Label.setText(player1Name + ": " + player1Score);
        player2Label.setText(player2Name + ": " + player2Score);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        int buttonIndex = -1;
        for (int i = 0; i < 9; i++) {
            if (buttons[i] == clickedButton) {
                buttonIndex = i;
                break;
            }
        }
        if (buttonIndex != -1) {
            if (buttons[buttonIndex].getText().isEmpty()) {
                if (player1Turn) {
                    buttons[buttonIndex].setText("X");
                    buttons[buttonIndex].setBackground(Color.YELLOW);
                    textField.setText(player2Name + "'s turn.");
                } else {
                    buttons[buttonIndex].setText("O");
                    buttons[buttonIndex].setBackground(Color.CYAN);
                    textField.setText(player1Name + "'s turn.");
                }
                player1Turn = !player1Turn;
                if (checkWin()) {
                    String winner = player1Turn ? player2Name : player1Name;
                    textField.setText(winner + " wins!");
                    if (winner.equals(player1Name)) {
                        player1Score++; // Increment player 1's score
                    } else {
                        player2Score++; // Increment player 2's score
                    }
                    updateScores();
                    notifyObserversOnGameEnd(winner);
                    disableGameControls();
                } else if (checkDraw()) {
                    textField.setText("It's a draw!");
                    disableGameControls();
                }
            }
        }
    }

    private boolean checkWin() {
        String[] board = new String[9];
        for (int i = 0; i < 9; i++) {
            board[i] = buttons[i].getText();
        }
        for (int i = 0; i < 3; i++) {
            if (board[i].equals(board[i + 3]) && board[i].equals(board[i + 6]) && !board[i].isEmpty()) {
                return true;
            }
        }
        for (int i = 0; i < 9; i += 3) {
            if (board[i].equals(board[i + 1]) && board[i].equals(board[i + 2]) && !board[i].isEmpty()) {
                return true;
            }
        }
        if (board[0].equals(board[4]) && board[0].equals(board[8]) && !board[0].isEmpty()) {
            return true;
        }
        if (board[2].equals(board[4]) && board[2].equals(board[6]) && !board[2].isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean checkDraw() {
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void resetGame() {
        clearBoard();
        player1Score = 0;
        player2Score = 0;
        updateScores();
        disableGameControls();
        textField.setText("Register players and click 'Play' to start.");
    }

    // New method to handle the back button
    private void addBackButton() {
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the current frame
                // Create a new instance of the previous menu frame
                TicTacToeType mainMenu = new TicTacToeType();
                mainMenu.showMenu();
            }
        });
        buttonPanel.add(backButton);
    }
}

class TicTacToeGameObserver implements GameObserver {
    public void onGameStart() {
        System.out.println("Game started!");
    }

    public void onGameEnd(String winner) {
        System.out.println("Game ended! Winner: " + winner);
    }
}

class Main {
    public static void main(String[] args) {
        TicTacToe ticTacToe = new TicTacToe();
        ticTacToe.addObserver(new TicTacToeGameObserver());
    }
}
