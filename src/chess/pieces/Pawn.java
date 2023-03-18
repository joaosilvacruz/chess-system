package chess.pieces;

import boardGame.Board;
import boardGame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

    public Pawn(Board board, Color color) {
        super(board, color);
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
        }
        return null;
    }
}
