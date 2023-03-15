package chess;

import boardGame.Board;
import boardGame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

    private Board board;

    public ChessMatch(){
        board = new Board(8, 8);
        initialSetup();
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] matrice = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i =0; i < board.getRows(); i++){
            for (int j = 0; j< board.getColumns(); j++){
                matrice[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return matrice;
    }

    private void placeNewPiece (char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }
    private void initialSetup() {
        placeNewPiece('b',6, new Rook(board, Color.WHITE));
        placeNewPiece('d',5, new King(board, Color.WHITE));
    }

}
