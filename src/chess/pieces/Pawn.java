package chess.pieces;

import boardGame.Board;
import boardGame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

    private ChessMatch chessMatch;
    public Pawn(Board board, Color color, ChessMatch chessMatch) {
        super(board, color);
        this.chessMatch = chessMatch;
    }

    @Override
    public String toString() {
        return "P";
    }

    public boolean[][] possibleMoves() {
        boolean [][] matrix = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position p = new Position(0, 0);

        if (getColor() == Color.WHITE) {
            Position p2 = new Position(position.getRow() - 1, position.getColumn());
            p.setValue(position.getRow() - 2, position.getColumn());
            if (getBoard().positionExists(p)
                    && !getBoard().thereIsAPiece(p)
                    && getBoard().positionExists(p2)
                    && !getBoard().thereIsAPiece(p2)
                    && getMoveCount() == 0) {
                matrix[p.getRow()][p.getColumn()] = true;
            }

            p.setValue(position.getRow() - 1, position.getColumn());
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
                matrix[p.getRow()][p.getColumn()] = true;
            }

            p.setValue(position.getRow() - 1, position.getColumn() - 1);
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
                matrix[p.getRow()][p.getColumn()] = true;
            }

            p.setValue(position.getRow() - 1, position.getColumn() + 1);
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
                matrix[p.getRow()][p.getColumn()] = true;
            }
            //En passant white
            if (position.getRow() == 3) {
                Position left = new Position(position.getRow(), position.getColumn() -1 );
                if (getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()){
                    matrix[left.getRow() -1][left.getColumn()] = true;
                }
            }
            if (position.getRow() == 3) {
                Position right = new Position(position.getRow(), position.getColumn() +1 );
                if (getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()){
                    matrix[right.getRow() -1][right.getColumn()] = true;
                }
            }

        } else {
                Position p2 = new Position(position.getRow() + 1, position.getColumn());
                p.setValue(position.getRow() + 2, position.getColumn());
                if (getBoard().positionExists(p)
                        && !getBoard().thereIsAPiece(p)
                        && getBoard().positionExists(p2)
                        && !getBoard().thereIsAPiece(p2)
                        && getMoveCount() == 0) {
                    matrix[p.getRow()][p.getColumn()] = true;
                }

                p.setValue(position.getRow() + 1, position.getColumn());
                if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
                    matrix[p.getRow()][p.getColumn()] = true;
                }

                p.setValue(position.getRow() + 1, position.getColumn() - 1);
                if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
                    matrix[p.getRow()][p.getColumn()] = true;
                }

                p.setValue(position.getRow() + 1, position.getColumn() + 1);
                if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
                    matrix[p.getRow()][p.getColumn()] = true;
                }

                //En passant white
                if (position.getRow() == 4) {
                    Position left = new Position(position.getRow(), position.getColumn() -1 );
                    if (getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()){
                        matrix[left.getRow() -1][left.getColumn()] = true;
                    }
                }
                if (position.getRow() == 4) {
                    Position right = new Position(position.getRow(), position.getColumn() +1 );
                    if (getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()){
                        matrix[right.getRow() -1][right.getColumn()] = true;
                    }
                }


        }
        return matrix;
    }
}
