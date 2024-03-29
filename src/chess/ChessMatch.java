package chess;

import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ChessMatch {

    private Board board;
    private int turn;
    private Color currentPlayer;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch(){
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
        check = false;
        initialSetup();
    }


    public ChessPiece[][] getPieces() {
        ChessPiece[][] matrix = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i =0; i < board.getRows(); i++){
            for (int j = 0; j< board.getColumns(); j++){
                matrix[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return matrix;
    }

    public boolean [][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        boolean[][] possibleMoviesReturn = board.piece(position).possibleMoves();

        if (testCheck(getCurrentPlayer())) {
            boolean [][] aux = new boolean[possibleMoviesReturn.length][possibleMoviesReturn.length];
            List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == getCurrentPlayer()).collect(Collectors.toList());
            for (Piece p: list) {
                boolean [][] matrix = p.possibleMoves();
                for (int i=0; i<board.getRows(); i++){
                    for (int j=0; j<board.getColumns(); j++){
                        if (matrix[i][j]){
                            Position source = ((ChessPiece)p).getChessPosition().toPosition();
                            Position target = new Position(i, j);
                            Piece capturedPiece = makeMove(source, target);

                            boolean testCheck = testCheck(getCurrentPlayer());
                            undoMove(source, target, capturedPiece);

                            ChessPiece pieceAtual = (ChessPiece) board.piece(sourcePosition.toPosition());
                            if (!testCheck) {
                                if (p.isThereAnyPossibleMove() && pieceAtual.equals(p)){
                                    aux [i][j] = true;
                                }
                            }
                        }
                    }
                }
            }
            return aux;
        }

        return possibleMoviesReturn;

    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }

        ChessPiece movedPiece = (ChessPiece)board.piece(target);

        // SpecialMove Promotion
        promoted = null;
        if (movedPiece instanceof Pawn){
            if (movedPiece.getColor() == Color.WHITE && target.getRow() == 0 || movedPiece.getColor() == Color.BLACK && target.getRow() == 7 ){
                promoted = (ChessPiece)board.piece(target);
                promoted = replacePromotedPiece("Q");
            }
        }

        check = (testCheck(opponent(currentPlayer))) ? true : false;
        
        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        } else{
            nextTurn();
        }

        // SpecialMove en passant
        if (movedPiece instanceof Pawn && (target.getRow() == source.getRow()-2 || target.getRow() == source.getRow() +2)) {
            enPassantVulnerable = movedPiece;
        } else {
            enPassantVulnerable = null;
        }

        return (ChessPiece) capturedPiece;
    }

    public ChessPiece replacePromotedPiece(String type){
        if (promoted == null) {
            throw new IllegalStateException("There is no piece to be promoted");
        }
        if (!type.equals("Q") && !type.equals("R") && !type.equals("N") && !type.equals("B")){
            return promoted;
        }
        Position pos = promoted.getChessPosition().toPosition();
        Piece p = board.removePiece(pos);
        piecesOnTheBoard.remove(p);

        ChessPiece newPiece = newPiece(type, promoted.getColor());
        board.placePiece(newPiece, pos);
        piecesOnTheBoard.add(newPiece);

        return newPiece;
    }

    private ChessPiece newPiece (String type, Color color) {
        if (type.equals("Q")) return new Queen(board, color);
        if (type.equals("R")) return new Rook(board, color);
        if (type.equals("N")) return new Knight(board, color);
        return new Bishop(board, color);
    }



    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);

        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        // Specialmove Kingside rook
        if (p instanceof King && target.getColumn() == source.getColumn() +2) {
            Position sourceRook = new Position(source.getRow(), source.getColumn() +3);
            Position targetRook = new Position(source.getRow(), source.getColumn() +1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceRook);
            board.placePiece(rook, targetRook);
            rook.increaseMoveCount();
        }
        // Specialmove Queenside rook
        if (p instanceof King && target.getColumn() == source.getColumn() -2) {
            Position sourceRook = new Position(source.getRow(), source.getColumn() -4);
            Position targetRook = new Position(source.getRow(), source.getColumn() -1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceRook);
            board.placePiece(rook, targetRook);
            rook.increaseMoveCount();
        }

        // SpecialMove EnPassant
        if (p instanceof Pawn) {
            if (source.getColumn() != target.getColumn() && capturedPiece == null){
                Position pawnPosition;
                if (p.getColor() == Color.WHITE) {
                    pawnPosition = new Position(target.getRow() +1, target.getColumn());
                } else {
                    pawnPosition = new Position(target.getRow() -1, target.getColumn());
                }
                capturedPiece = board.removePiece(pawnPosition);
                capturedPieces.add(capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
            }
        }
        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece p = (ChessPiece) board.removePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);
        if (capturedPiece != null) {
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }

        // Specialmove Kingside rook
        if (p instanceof King && target.getColumn() == source.getColumn() +2) {
            Position sourceRook = new Position(source.getRow(), source.getColumn() +3);
            Position targetRook = new Position(source.getRow(), source.getColumn() +1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetRook);
            board.placePiece(rook, sourceRook);
            rook.decreaseMoveCount();
        }
        // Specialmove Queenside rook
        if (p instanceof King && target.getColumn() == source.getColumn() -2) {
            Position sourceRook = new Position(source.getRow(), source.getColumn() -4);
            Position targetRook = new Position(source.getRow(), source.getColumn() -1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetRook);
            board.placePiece(rook, sourceRook);
            rook.decreaseMoveCount();
        }
        // SpecialMove EnPassant
        if (p instanceof Pawn) {
            if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable){
                ChessPiece pawn = (ChessPiece)board.removePiece(target);
                Position pawnPosition;
                if (p.getColor() == Color.WHITE) {
                    pawnPosition = new Position(3, target.getColumn());
                } else {
                    pawnPosition = new Position(4, target.getColumn());
                }
                board.placePiece(pawn, pawnPosition);
            }
        }
    }

    private void validateSourcePosition (Position position) {
        if (!board.thereIsAPiece(position)){
            throw new ChessException("There is no piece on source position");
        }
        if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
            throw new ChessException("The chosen piece is not yours");
        }
        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible moves for the chosen piece");
        }
    }

    private void validateTargetPosition(Position source, Position target){
        if (!board.piece(source).possibleMove(target)){
            throw new ChessException("The chosen piece can't move to target position");
        };
    }

    private void nextTurn() {
        turn++;
        currentPlayer = currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king (Color color) {
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list) {
            if (p instanceof King) {
                return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("There is no " + color + "king on the board");
    }

    private boolean testCheck (Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            boolean[][] matrix = p.possibleMoves();
            if (matrix[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate (Color color) {
        if (!testCheck(color)) {
            return false;
        }
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p: list) {
            boolean [][] matrix = p.possibleMoves();
            for (int i=0; i<board.getRows(); i++){
                for (int j=0; j<board.getColumns(); j++){
                    if (matrix[i][j]){
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if (!testCheck) {
                            return false;
                        }
                    }
                }
            }

        }
        return true;
    }

    private void placeNewPiece (char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
    private void initialSetup() {

        //Place pawns
        String stringColumns = "abcdefgh";
        for (int i=0; i<board.getRows(); i++){
            placeNewPiece(stringColumns.charAt(i), 2, new Pawn(board, Color.WHITE, this));
            placeNewPiece(stringColumns.charAt(i), 7, new Pawn(board, Color.BLACK, this));
        }
        //Place Rooks
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));

        //Place Bishops
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));

        //Place Knights
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));

        //Place Kings
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));

        //Place Queens
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));


    }

    public boolean getCheckMate() {
        return checkMate;
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean getCheck(){
        return check;
    }

    public ChessPiece getEnPassantVulnerable(){
        return enPassantVulnerable;
    }

    public ChessPiece getPromoted(){
        return promoted;
    }

}
