package easteregg.chess;

import easteregg.chess.piece.Piece;

public class GameMaster {


    private Color currentTurn;

    public static void main(String[] args) {
        GameMaster gameMaster = new GameMaster();
        gameMaster.startGame("A", Color.WHITE, "B", Color.BLACK);
    }

    public void startGame(String player1, Color cP1, String player2, Color cP2) {
        Player p1 = new Player(player1, cP1);
        Player p2 = new Player(player2, cP2);
        Board board = new Board();
        board.initBoard();
        currentTurn = Color.WHITE;
        Piece selectedPiece = board.get("B", 1);
        board.select("B", 1);
        if (selectedPiece.getColor() == currentTurn) {
            //board.moveTo("B", 1, "C", 3);
        }  else {

        }
        board.showBoard();
    }

    private void moveTo(String c, int i1) {

    }


}
