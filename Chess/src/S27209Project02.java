import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class S27209Project02 {

    public static class Main {

        public static void main(String[] args) {

            Game chess = new Game( new Board(8, 8) );
            chess.play();

        }

    }

    public static class Bishop extends Figure {
        Bishop(int color, int positionX, int positionY) {
            super();
            char chr = (color==1) ? '♝' : '♗';
            super.color = color;
            super.positionX = positionX;
            super.positionY = positionY;
            super.chr = chr;
        }

        @Override
        public boolean checkMove(int positionX, int positionY, List<Figure> figures) {
            if (this.positionX == positionX && this.positionY == positionY) {
                return false;
            }
            if (Math.abs(this.positionX - positionX) != Math.abs(this.positionY - positionY)) {
                // bishop can only move diagonally
                return false;
            }

            int xDiff = (positionX - this.positionX > 0) ? 1 : -1;
            int yDiff = (positionY - this.positionY > 0) ? 1 : -1;

            int x = this.positionX + xDiff;
            int y = this.positionY + yDiff;
            while (x != positionX && y != positionY) {
                for (Figure f : figures) {
                    if (f.positionX == x && f.positionY == y) {
                        // there is a figure blocking the path
                        return false;
                    }
                }
                x += xDiff;
                y += yDiff;
            }

            for (Figure f : figures) {
                if (f.positionX == positionX && f.positionY == positionY) {
                    if (f.color == this.color) {
                        // there is a figure of the same color at the destination
                        return false;
                    } else {
                        // capture the opponent's piece
                        return true;
                    }
                }
            }
            // no pieces blocking the path and the destination is empty
            return true;
        }



    }

    public static class Board {

        int n;
        int m;
        char[][] cells;
        List<Figure> figures = new ArrayList<>();
        public char empty = '□';
        public char possible = '■';

        public Board(int n, int m) {
            this.n = n;
            this.m = m;
            cells = new char[n][m];
        }

        public void set() {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    cells[i][j] = empty;
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    for (Figure f: figures) {
                        if (f.positionX == i &&
                                f.positionY == j) {
                            cells[i][j] = f.chr;
                        }
                    }
                }
            }
        }

        public void setDefault() {
            figures.clear();

            for (int i = 0; i < 8; i++) figures.add( new Pawn( 0, 1, i));   //black pawns
            for (int i = 0; i < 8; i++) figures.add( new Pawn( 1, 6, i));   //white pawns
            figures.add( new Rook(0, 0, 0));    //black rook
            figures.add( new Rook(0, 0, 7));    //black rook
            figures.add( new Rook(1, 7, 0));    //white rook
            figures.add( new Rook(1, 7, 7));    //white rook
            figures.add( new Knight(0, 0, 1));    //black knight
            figures.add( new Knight(0, 0, 6));    //black knight
            figures.add( new Knight(1, 7, 1));    //white knight
            figures.add( new Knight(1, 7, 6));    //white knight
            figures.add( new Bishop(0, 0, 2));    //black bishop
            figures.add( new Bishop(0, 0, 5));    //black bishop
            figures.add( new Bishop(1, 7, 2));    //white bishop
            figures.add( new Bishop(1, 7, 5));    //white bishop
            figures.add( new Queen(0, 0, 3));    //black queen
            figures.add( new Queen(1, 7, 3));    //white queen
            figures.add( new King(0, 0, 4));    //black king
            figures.add( new King(1, 7, 4));    //white king

            set();
        }

        public void printMoves(Figure figure) {
            set();

            System.out.println('\n');

            for (int i = 0; i < n; i++) {
    //            System.out.print(i + "\t\t");
                System.out.print((n - i) + "\t\t");   // 8 to 1 column
                for (int j = 0; j < m; j++) {
                    if (figure.checkMove(i, j, figures) && cells[i][j]==empty)
                        System.out.print(possible);
                    else
                        System.out.print(cells[i][j]);
                    System.out.print('\t');
                }
                System.out.print('\n');
            }

            System.out.print("\n \t\t");
            for (int i = 0; i < n; i++) {
    //            System.out.print(i);
                System.out.print( (char)(65 + i) );     //A to H row
                System.out.print('\t');
            }

            System.out.println('\n');
        }

        public void print() {
            set();

            System.out.println('\n');

            for (int i = 0; i < n; i++) {
    //            System.out.print(i + "\t\t");
                System.out.print((n - i) + "\t\t");   // 8 to 1 column
                for (int j = 0; j < m; j++) {
                    System.out.print(cells[i][j]);
                    System.out.print('\t');
                }
                System.out.print('\n');
            }

            System.out.print("\n \t\t");
            for (int i = 0; i < n; i++) {
    //            System.out.print(i);
                System.out.print( (char)(65 + i) );     //A to H row
                System.out.print('\t');
            }

            System.out.println('\n');
        }

        public int movePlayer(int color) {
            print();
            int[] from = requestPosition();

            if (Arrays.stream(from).sum() == -1) {  //SAVE
                return 1;
            } else if (Arrays.stream(from).sum() == -2) {   //LOAD
                return -1;
            }

            if (checkRequestPosition(from, color)) {
                printMoves(
                        figures.stream()
                                .filter(f -> f.positionX == from[0]
                                          && f.positionY == from[1])
                                .findAny()
                                .orElse(null)
                );
                int[] to = requestPosition();
                try {
                     Figure figure = figures.stream()
                            .filter(f -> f.positionX == from[0]
                                    && f.positionY == from[1])
                            .findAny()
                            .orElse(null);
                     figure.move(to[0], to[1], figures);
                } catch (IllegalMoveException e) {
                    Game.printTitle("Illegal move! Try again.");
                    movePlayer(color);
                }
            } else {
                Game.printTitle("Incorrect position! Try again.");
                movePlayer(color);
            }
            return 0;
        }

        public boolean isCheckmate(int color) {
            return (isInCheck(color) && leftMoves(color) == 0);
        }

        public int leftMoves(int color) {
            int moves = 0;
            for (Figure figure : figures) {
                if (figure instanceof King && figure.color == color) {
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < m; j++) {
                            if (figure.checkMove(i, j, figures)) {
                                if (((King) figure).checkMate(i, j, figures)) moves++;
                            }
                        }
                    }
                }
            }
            return moves;
        }

        public boolean isInCheck(int color) {
            for (Figure figure : figures) {
                if (figure instanceof King && figure.color == color)
                    if (((King) figure).checkMate(figure.positionX, figure.positionY, figures))
                        return true;
            }
            return false;
        }

        public boolean checkRequestPosition(int[] position, int color) {
            for (Figure f: figures) {
                if (f.positionX == position[0] && f.positionY == position[1])
                    return f.color == color;
            }
            return false;
        }

        public static int[] requestPosition() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter position:\n");
            String input = scanner.nextLine().toUpperCase();

            if (input.equals("SAVE")) {
                return new int[]{-1, 0};
            } else if (input.equals("LOAD")) {
                return new int[]{-1, -1};
            }

            if (input.length() != 2) {
                System.out.println("Error, try again!");
                return requestPosition();
            }

            int i, j;

            if (Character.isLetter(input.charAt(0))) {
                i = 8 - (input.charAt(1) - 48);
                j =  (input.charAt(0) - 65);
            } else if (Character.isLetter(input.charAt(1))){
                System.out.println("Error, try again!");
                return requestPosition();
            } else {
                i = Integer.parseInt(input.charAt(0)+"");
                j = Integer.parseInt(input.charAt(1)+"");
            }

            if (i > 7 || i < 0 || j > 7 || j < 0) {
                System.out.println("Error, try again!");
                return requestPosition();
            }

            return new int[]{i, j};
        }

    }

    public abstract static class Figure implements Movable {
        public int color;   // 0-black; 1-white
        public int positionX;
        public int positionY;
        public char chr;


        @Override
        public String toString() {
            return "S27209Project02.Figure{" +
                    "color=" + ((color==1) ? "white" : "black") +
                    ", position=" + "[" + positionX + ", " + positionY + "]" +
                    ", chr=" + chr +
                    ", " + this.getClass() +
                    '}';
        }

        @Override
        public void move(int positionX, int positionY, List<Figure> figures) throws IllegalMoveException {
            if (this.checkMove(positionX, positionY, figures)) {
                figures.removeIf( (Figure f) -> {
                    return f.positionX == positionX && f.positionY == positionY;
                });
                this.positionX = positionX;
                this.positionY = positionY;
            } else {
                throw new IllegalMoveException("Illegal");
            }
        }
    }

    public static class FileManager {

        public static void save(FileOutputStream file, List<Figure> figures) throws IOException {
            for (int i = 1; i <= 32; i++) {
                writeFigure(file, figures.get(i-1));
            }
        }

        public static List<Figure> load(FileInputStream file) throws IOException {
            List<Figure> figures = new ArrayList<>();
            int n = file.available()/2;
            for (int i = 1; i <= n; i++)
                figures.add(readFigure(file));
            return figures;
        }

        public static String intToBinary(int num, int size) {
            String binaryString = Integer.toBinaryString(num);
            int padding = size - binaryString.length();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < padding; i++) {
                sb.append('0');
            }
            sb.append(binaryString);
            return sb.toString();
        }

        public static void writeBinaryToFile(FileOutputStream file, String binaryString) throws IOException {
            int numBytes = binaryString.length() / 8;
            byte[] bytes = new byte[numBytes];
            for (int i = 0; i < numBytes; i++) {
                String byteString = binaryString.substring(i*8, (i+1)*8);
                bytes[i] = (byte) Integer.parseInt(byteString, 2);
            }
            file.write(bytes);
        }

        private static void writeFigure(FileOutputStream file, Figure figure) throws IOException {
            int type = -1;
            if (figure instanceof Pawn) type = 0;
            if (figure instanceof King) type = 1;
            if (figure instanceof Queen) type = 2;
            if (figure instanceof Rook) type = 3;
            if (figure instanceof Bishop) type = 4;
            if (figure instanceof Knight) type = 5;
            int positionX = figure.positionX;
            int positionY = figure.positionY;
            int color = figure.color;

            String data = "0000";
            data += color;
            data += intToBinary(positionX+1, 4);
            data += intToBinary(positionY+1, 4);
            data += intToBinary(type, 3);
            writeBinaryToFile(file, data.substring(0, 8));
            writeBinaryToFile(file, data.substring(8, 16));
        }

        private static Figure readFigure(FileInputStream file) throws IOException {

            String info = "";

            String read = "";
            read += Integer.toBinaryString(file.read());
            int size = read.length();
            for (int i = 0; i < 8 - size; i++)
                info += "0";
            info += read;

            read = "";
            read += Integer.toBinaryString(file.read());
            size = read.length();
            for (int i = 0; i < 8 - size; i++)
                info += "0";
            info += read;

            int type = Integer.parseInt(info.substring(13),2);
            int positionY = Integer.parseInt(info.substring(9, 13),2) - 1;
            int positionX = Integer.parseInt(info.substring(5, 9),2) - 1;
            int color = Integer.parseInt(info.substring(4, 5),2);

            switch (type) {
                case 0 -> {
                    return new Pawn(color, positionX, positionY);
                }
                case 1 -> {
                    return new King(color, positionX, positionY);
                }
                case 2 -> {
                    return new Queen(color, positionX, positionY);
                }
                case 3 -> {
                    return new Rook(color, positionX, positionY);
                }
                case 4 -> {
                    return new Bishop(color, positionX, positionY);
                }
                case 5 -> {
                    return new Knight(color, positionX, positionY);
                }
                default -> {
                    return null;
                }
            }
        }

    }

    public static class Game {

        public Board board;

        public Game(Board board) {
            this.board = board;
        }

        public void play() {
            board.setDefault();
            printTitle("START");
            printText("Welcome to the terminal Chess game created by Kyrylo Kunytskyi. In order to make a move, first enter the initial position and then the destination. Enter 'save' or 'load' depending on your need.", 40);
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                int color = i % 2;
                if (!board.isCheckmate(color)) {
                    printTitle("Turn: " + ((color == 1) ? "White" : "Black"));
                    switch (board.movePlayer(color)) {
                        case 1 -> {i--; save();}
                        case -1 -> {i--; load();}
                    }
                } else {
                    printTitle("CHECKMATE!");
                    printTitle("Winner: " + ((color == 1) ? "Black" : "White"));
                    break;
                }
            }
            board.print();
        }


        public static void printTitle(String title) {
            for (int i = 0; i < 40; i++) System.out.print('-');
            System.out.println();
            for (int i = 0; i < 20 - (title.length()/2); i++) System.out.print(' ');
            System.out.print(title + "\n");
            for (int i = 0; i < 40; i++) System.out.print('-');
            System.out.println();
        }

        public static void printText(String text, int number) {
            int index = 0;
            while (index < text.length()) {
                int endIndex = Math.min(index + number, text.length());
                String line = text.substring(index, endIndex);
                System.out.println(line);
                index += number;
            }
        }

        public void save() {
            try {
                FileOutputStream file = new FileOutputStream("save.bin");
                FileManager.save(file, board.figures);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            printTitle("GAME SAVED");
        }

        public void load() {
            try {
                FileInputStream file = new FileInputStream("save.bin");
                board.figures = FileManager.load(file);
                printTitle("GAME LOADED");
            } catch (IOException e) {
                System.out.println("No saved file! Please, save game first\nor add the saved file to the project.");
            }
        }

    }

    public static class IllegalMoveException extends Exception{
        public IllegalMoveException(String message) {
            super(message);
        }
    }

    public static class King extends Figure {
        King(int color, int positionX, int positionY) {
            super();
            char chr = (color==1) ? '♚' : '♔';
            super.color = color;
            super.positionX = positionX;
            super.positionY = positionY;
            super.chr = chr;
        }
        @Override
        public boolean checkMove(int positionX, int positionY, List<Figure> figures) {
            if (this.positionX == positionX && this.positionY == positionY) return false;
            // Check if the new position is a valid move for the king
            if (Math.abs(positionX - this.positionX) <= 1 && Math.abs(positionY - this.positionY) <= 1) {
                // Check if there's already a figure in the new position
                for (Figure figure : figures) {
                    if (figure.positionX == positionX && figure.positionY == positionY) {
                        // There's a figure in the new position
                        return figure.color != this.color;
                    }
                }
                return !checkMate(positionX, positionY, figures);
            }
            return false;
        }

        public boolean checkMate(int positionX, int positionY, List<Figure> figures) {
            for (Figure figure : figures) {
                if (figure.color != this.color) {
                    if (figure instanceof Bishop) {
                        Bishop bishop = (Bishop) figure;
                        if (bishop.checkMove(positionX, positionY, figures)) {
                            return true; // The position is attacked by a bishop
                        }
                    } if (figure instanceof Rook) {
                        Rook rook = (Rook) figure;
                        if (rook.checkMove(positionX, positionY, figures)) {
                            return true; // The position is attacked by a rook
                        }
                    }  if (figure instanceof Queen) {
                        Queen queen = (Queen) figure;
                        if (queen.checkMove(positionX, positionY, figures)) {
                            return true; // The position is attacked by a queen
                        }
                    }  if (figure instanceof Knight) {
                        Knight knight = (Knight) figure;
                        if (knight.checkMove(positionX, positionY, figures)) {
                            return true; // The position is attacked by a knight
                        }
                    }  if (figure instanceof Pawn) {
                        Pawn pawn = (Pawn) figure;
                        if (pawn.checkMove(positionX, positionY, figures)) {
                            return true; // The position is attacked by a pawn
                        }
                    }
                }
            }
            return false; // The position is not attacked by any figure
        }

    }

    public static class Knight extends Figure {

        Knight(int color, int positionX, int positionY) {
            super();
            char chr = (color==1) ? '♞' : '♘';
            super.color = color;
            super.positionX = positionX;
            super.positionY = positionY;
            super.chr = chr;
        }

        @Override
        public boolean checkMove(int positionX, int positionY, List<Figure> figures) {
            // Check if the move is valid for a knight
            int dx = Math.abs(positionX - this.positionX);
            int dy = Math.abs(positionY - this.positionY);
            if (dx == 1 && dy == 2 || dx == 2 && dy == 1) {
                for (Figure figure : figures) {
                    if (figure.positionX == positionX && figure.positionY == positionY) {
                        return figure.color != this.color;
                    }
                }
                return true;
            }
            return false;
        }

    }

    public static interface Movable {

        boolean checkMove(int positionX, int positionY, List<Figure> figures);
        void move(int positionX, int positionY, List<Figure> figures) throws IllegalMoveException;

    }

    public static class Pawn extends Figure {
        Pawn(int color, int positionX, int positionY) {
            super();
            char chr = (color==1) ? '♟' : '♙';
            super.color = color;
            super.positionX = positionX;
            super.positionY = positionY;
            super.chr = chr;
        }

        @Override
        public void move(int positionX, int positionY, List<Figure> figures) throws IllegalMoveException {
            if (this.checkMove(positionX, positionY, figures)) {
                figures.removeIf( (Figure f) -> {
                    return f.positionX == positionX && f.positionY == positionY;
                });
                this.positionX = positionX;
                this.positionY = positionY;
            } else {
                throw new IllegalMoveException("Illegal");
            }
            switch (color) {
                case 0 -> {
                    if (positionX == 7) {
                        figures.remove( this );
                        figures.add( new Queen( this.color,
                                positionX,
                                positionY) );
                    }
                } case 1 -> {
                    if (positionX == 0) {
                        figures.remove( this );
                        figures.add( new Queen( this.color,
                                positionX,
                                positionY) );
                    }
                }
            }
        }

        @Override
        public boolean checkMove(int positionX, int positionY, List<Figure> figures) {
            int direction = color == 1 ? -1 : 1;
            int startingRow = color == 1 ? 6 : 1;

            // Check if pawn is moving forward one or two squares
            if (positionY == this.positionY) {
                if (positionX == this.positionX + direction) {
                    // S27209Project02.Pawn is moving forward one square
                    return !figures.stream().anyMatch(f -> f.positionX == positionX && f.positionY == positionY);
                } else if (positionX == this.positionX + 2 * direction && this.positionX == startingRow) {
                    // S27209Project02.Pawn is moving forward two squares on its first move
                    return !figures.stream().anyMatch(f -> f.positionX == positionX && f.positionY == positionY)
                            && !figures.stream().anyMatch(f -> f.positionX == positionX - direction && f.positionY == positionY);
                }
            }
            // Check if pawn is capturing diagonally
            else if (Math.abs(positionY - this.positionY) == 1 && positionX == this.positionX + direction) {
                return figures.stream().anyMatch(f -> f.positionX == positionX && f.positionY == positionY && f.color != this.color);
            }

            // If none of the above cases are satisfied, the move is invalid
            return false;
        }

    }

    public static class Queen extends Figure {
        Queen(int color, int positionX, int positionY) {
            super();
            char chr = (color==1) ? '♛' : '♕';
            super.color = color;
            super.positionX = positionX;
            super.positionY = positionY;
            super.chr = chr;
        }

        @Override
        public boolean checkMove(int positionX, int positionY, List<Figure> figures) {
            return ( new Bishop(this.color, this.positionX, this.positionY).checkMove(positionX, positionY, figures)
                    || new Rook(this.color, this.positionX, this.positionY).checkMove(positionX, positionY, figures));
        }

    }

    public static class Rook extends Figure {

        Rook(int color, int positionX, int positionY) {
            super();
            char chr = (color==1) ? '♜' : '♖';
            super.color = color;
            super.positionX = positionX;
            super.positionY = positionY;
            super.chr = chr;
        }

        @Override
        public boolean checkMove(int positionX, int positionY, List<Figure> figures) {
            if (this.positionX == positionX && this.positionY == positionY) {
                return false;
            }
            if (positionX == this.positionX || positionY == this.positionY) { // S27209Project02.Rook can only move along its row or column
                int minX = Math.min(positionX, this.positionX);
                int minY = Math.min(positionY, this.positionY);
                int maxX = Math.max(positionX, this.positionX);
                int maxY = Math.max(positionY, this.positionY);

                for (Figure figure : figures) {
                    if (figure.positionX == this.positionX && figure.positionY == this.positionY) {
                        continue; // Skip the rook itself
                    }
                    if (figure.positionX == positionX && figure.positionY == positionY) {
                        if (figure.color == this.color) {
                            return false; // Cannot capture own piece
                        } else {
                            return true; // Can capture opponent's piece
                        }
                    }
                    if (figure.positionX == this.positionX && figure.positionY > minY && figure.positionY < maxY) {
                        return false; // There is a piece blocking the way
                    }
                    if (figure.positionY == this.positionY && figure.positionX > minX && figure.positionX < maxX) {
                        return false; // There is a piece blocking the way
                    }
                }

                return true; // The path is clear
            } else {
                return false; // S27209Project02.Rook cannot move diagonally
            }
        }

    }
}
