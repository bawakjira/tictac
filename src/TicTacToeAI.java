public class TicTacToeAI {
    /* the board */
    private int board[][];
    /* empty */
    public static final int EMPTY = 0;
    /* human player */
    public static final int HUMAN = 1;
    /* computer player */
    public static final int COMPUTER = 2;
    
    public TicTacToeAI() {
        board = new int[3][3];
    }
    
    /* get the board value for position (i,j) */
    public int getBoardValue(int i,int j) {
        if(i < 0 || i >= 3) return EMPTY;
        if(j < 0 || j >= 3) return EMPTY;
        return board[i][j];
    }
    
    /* set the board value for position (i,j) */
    public void setBoardValue(int i,int j,int token) {
        if(i < 0 || i >= 3) return;
        if(j < 0 || j >= 3) return;
        board[i][j] = token;
    }
    
    /* check if a player has won */
    public boolean isWin(int token) {
        for(int i=0;i<3;i++) {
            if(board[i][0] == token && board[i][1] == token && board[i][2] == token) return true;
            if(board[0][i] == token && board[1][i] == token && board[2][i] == token) return true;
        }
        if(board[0][0] == token && board[1][1] == token && board[2][2] == token) return true;
        if(board[0][2] == token && board[1][1] == token && board[2][0] == token) return true;
        return false;
    }
    
    /* calculate the winning move for current token */
    public int []nextWinningMove(int token) {
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                if(getBoardValue(i, j)==EMPTY) {
                    board[i][j] = token;
                    boolean win = isWin(token);
                    board[i][j] = EMPTY;
                    if(win) return new int[]{i,j};
                }
        return null;
    }
    
    /* calculate the best move for current token */
    public int []nextMove(int token) {
        /* lucky position in the center of board*/
        if(getBoardValue(1, 1)==EMPTY) return new int[]{1,1};
        
        /* if we can win on the next turn */
        int winMove[] = nextWinningMove(token);
        if(winMove!=null) return winMove;
        
        /* if human can win on the next turn, block them */
        int inverseToken = (token == HUMAN) ? COMPUTER : HUMAN;
        int blockMove[] = nextWinningMove(inverseToken);
        if(blockMove!=null) return blockMove;
        
        /* choose available move */
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                if(getBoardValue(i, j)==EMPTY)
                    return new int[]{i,j};
        
        return null;
    }
}