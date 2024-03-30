import java.rmi.UnexpectedException;
import java.util.Scanner;
//import java.util.concurrent.TimeUnit;

public class ConnectFour {
    private final int ROWS = 6;
    private final int COLUMNS = 7;
    private Token[][] board = new Token[ROWS][COLUMNS];
    private int turn = 0;
    public ConnectFour(){
    }
    public void startGame(){
        fillBoard();
        for (int i = 0; i < ROWS * COLUMNS; i++) {
            gameLoop();
            if(hasWon() != Token.emptyToken){
                render();
                System.out.printf("%s has won!\n", hasWon());
                break;
            }
        }
        if(hasWon() == Token.emptyToken){
            System.out.println("The game ended in a tie.");
        }
        System.out.println("The game will close in 5 seconds.");
        try {
            //TimeUnit.SECONDS.sleep(5);
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }
    private void gameLoop(){
        MiniMaxBot bot = new MiniMaxBot();
        System.out.printf("%dth turn:\n", ++turn);
        render();
        if(turn % 2 == 1){
            getUserInput();
        }else{
            bot.bestMove();
        }
    }
    private void render(){
        for (int i = 0; i < ROWS; i++) {
            System.out.print("| ");
            for (int j = 0; j < COLUMNS; j++) {
                System.out.printf("%s | ", board[i][j].getLabel());
            }
            System.out.println();
        }
        System.out.println("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
    }
    private void fillBoard(){
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = new Token();
            }
        }
    }
    private void getUserInput(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Where do you want to put your token? insert the number of the column from 1 to " + COLUMNS);
        addToken(sc.nextInt() - 1);
    }
    private void addToken(int column){
        for (int i = ROWS - 1; i >= 0; i--) {
            if(board[i][column].getLabel() == Token.emptyToken){ //     Se è vuoto
                board[i][column] = new Token(turn % 2 == 0); // sui turni pari da un simbolo e sui dispari l'altro
                return;
            }
        }
        System.out.println("The chosen column is full!");
        turn--;
    }
    private void removeLastToken(int column){
        for (int i = 0; i < ROWS; i++) {
            if(board[i][column].getLabel() != Token.emptyToken){ //     Se è vuoto
                board[i][column] = new Token();
                return;
            }
        }
    }

    private char hasWon(){ //restituisce il simbolo del vincitore, '=' se è patta o il simbolo vuoto se non è finita
        char winner = '=';
        // 4 in verticale
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS - 3; j++) { // - 4 perchè ne servono 4 in fila
                if(board[j][i].getLabel() == board[j + 1][i].getLabel() &&
                   board[j][i].getLabel() == board[j + 2][i].getLabel() &&
                   board[j][i].getLabel() == board[j + 3][i].getLabel() &&
                   board[j][i].getLabel() != Token.emptyToken){ // se ce ne sono 4 di fila
                    return board[j][i].getLabel();
                }
            }
        }

        for (int i = 0; i < ROWS; i++) {

            for (int j = 0; j < COLUMNS - 3; j++) { // - 3 perchè ne servono 4 in fila
                // 4 in orizzontale
                if(board[i][j].getLabel() == board[i][j + 1].getLabel() &&
                   board[i][j].getLabel() == board[i][j + 2].getLabel() &&
                   board[i][j].getLabel() == board[i][j + 3].getLabel() &&
                   board[i][j].getLabel() != Token.emptyToken){ // se ce ne sono 4 di fila
                    return board[i][j].getLabel();
                }
                //diagonali \
                if(board[i][j].getLabel() == board[i - 1][j + 1].getLabel() &&
                        board[i][j].getLabel() == board[i - 2][j + 2].getLabel() &&
                        board[i][j].getLabel() == board[i - 3][j + 3].getLabel() &&
                        board[i][j].getLabel() != Token.emptyToken){ // se ce ne sono 4 di fila
                    return board[i][j].getLabel();
                }
            }
        }
        // diagonali /
        for (int i = 3; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS - 3; j++) {

            }
        }
        // diagonali \
        for (int i = 0; i < ROWS - 3; i++) {
            for (int j = 0; j < COLUMNS - 3; j++) {
                if(board[i][j].getLabel() == board[i + 1][j + 1].getLabel() &&
                        board[i][j].getLabel() == board[i + 2][j + 2].getLabel() &&
                        board[i][j].getLabel() == board[i + 3][j + 3].getLabel() &&
                        board[i][j].getLabel() != Token.emptyToken){ // se ce ne sono 4 di fila
                    return board[i][j].getLabel();
                }
            }
        }
        for (int i = 0; i < COLUMNS; i++) {
            if(board[0][i].getLabel() == Token.emptyToken){
                winner = Token.emptyToken; // se la partita non è finita
            }
        }
        return winner;
    }
    public class MiniMaxBot{
        public void bestMove() {
            // AI to make its turn
            double bestScore = Double.NEGATIVE_INFINITY;
            int move = 1;
            for (int i = 0; i < COLUMNS; i++) {
                // Is the spot available?
                if (board[0][i].getLabel() == Token.emptyToken) {
                    addToken(i);
                    double score = minimax(0, false);
                    removeLastToken(i);
                    if (score > bestScore) {
                        bestScore = score;
                        move = i;
                    }
                }
            }
            addToken(move);
        }

        public double minimax(int depth, boolean isMaximizing){
            double bestScore;
            char winner = hasWon();
            switch(winner){
                case Token.darkToken /*giocatore*/ -> {
                    return -10;
                }
                case Token.lightToken /*robot*/ -> {
                    return 10;
                }
                case '=' -> {
                    return 0; //patta
                }
            }
            if(isMaximizing){
                bestScore = Double.NEGATIVE_INFINITY; // mettiamo il minimo punteggio possibile in modo da cercare il punteggio maggiore
                for (int i = 0; i < COLUMNS; i++) { // controlliamo ogni colonna
                    if(board[0][i].getLabel() == Token.emptyToken){ //se non è piena
                        addToken(i);
                        double score = minimax(depth + 1, false);
                        removeLastToken(i);
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }else{
                bestScore = Double.POSITIVE_INFINITY; // mettiamo il massimo punteggio possibile in modo da cercare il punteggio minore
                for (int i = 0; i < COLUMNS; i++) { // controlliamo ogni colonna
                    if(board[0][i].getLabel() == Token.emptyToken){ //se non è piena
                        addToken(i);
                        double score = minimax(depth + 1, true);
                        removeLastToken(i);
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }
}
