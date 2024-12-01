import java.util.Scanner;

abstract class Chess {
    protected String color;
    protected int x, y;

    public Chess(String color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public abstract boolean isValidMove(int newX, int newY, Chess[][] board);
    public abstract char getSymbol();

    public String getColor() {
        return color;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected boolean canMoveToPosition(int newX, int newY) {
        return newX >= 0 && newX < 8 && newY >= 0 && newY < 8;
    }
}

class Pawn extends Chess {
    private boolean isFirstMove;

    public Pawn(String color, int x, int y) {
        super(color, x, y);
        this.isFirstMove = true;
    }

    @Override
    public boolean isValidMove(int newX, int newY, Chess[][] board) {
        if (!canMoveToPosition(newX, newY)) {
            return false;
        }

        int direction = color.equals("White") ? 1 : -1;
        int startRow = color.equals("White") ? 1 : 6;

        // Ход на 1 поле вперед
        if (newX == x && newY == y + direction) {
            return board[newX][newY] == null;
        }
        // Ход на 2 поля вперед
        else if (isFirstMove && newX == x && newY == y + 2 * direction) {
            return board[newX][newY] == null && board[x + direction][y] == null;
        }
        // Атака по диагонали
        else if (Math.abs(newX - x) == 1 && newY == y + direction) {
            return board[newX][newY] != null && !board[newX][newY].getColor().equals(color);
        }

        return false;
    }

    @Override
    public char getSymbol() {
        return color.equals("White") ? 'P' : 'p';
    }
}

class Horse extends Chess {
    public Horse(String color, int x, int y) {
        super(color, x, y);
    }

    @Override
    public boolean isValidMove(int newX, int newY, Chess[][] board) {
        if (!canMoveToPosition(newX, newY)) {
            return false;
        }
        // Проверка на допустимые движения коня
        if ((Math.abs(newX - x) == 2 && Math.abs(newY - y) == 1) ||
                (Math.abs(newX - x) == 1 && Math.abs(newY - y) == 2)) {
            // Проверка на возможность атаки
            return board[newX][newY] == null || !board[newX][newY].getColor().equals(color);
        }
        return false;
    }

    @Override
    public char getSymbol() {
        return color.equals("White") ? 'N' : 'n';
    }
}

class Bishop extends Chess {
    public Bishop(String color, int x, int y) {
        super(color, x, y);
    }

    @Override
    public boolean isValidMove(int newX, int newY, Chess[][] board) {
        if (!canMoveToPosition(newX, newY)) {
            return false;
        }
        if (Math.abs(newX - x) != Math.abs(newY - y)) {
            return false; // Слон ходит по диагонали
        }
        return isPathClear(newX, newY, board);
    }

    private boolean isPathClear(int newX, int newY, Chess[][] board) {
        int deltaX = newX > x ? 1 : -1;
        int deltaY = newY > y ? 1 : -1;
        int stepX = x + deltaX;
        int stepY = y + deltaY;

        while (stepX != newX && stepY != newY) {
            if (board[stepX][stepY] != null) {
                return false; // Путь заблокирован
            }
            stepX += deltaX;
            stepY += deltaY;
        }
        // Проверка на возможность атаки
        return board[newX][newY] == null || !board[newX][newY].getColor().equals(color);
    }

    @Override
    public char getSymbol() {
        return color.equals("White") ? 'B' : 'b';
    }
}

class Rook extends Chess {
    public Rook(String color, int x, int y) {
        super(color, x, y);
    }

    @Override
    public boolean isValidMove(int newX, int newY, Chess[][] board) {
        if (!canMoveToPosition(newX, newY)) {
            return false;
        }
        if (newX != x && newY != y) {
            return false; // Ладья ходит по линии
        }
        return isPathClear(newX, newY, board);
    }

    private boolean isPathClear(int newX, int newY, Chess[][] board) {
        if (newX == x) {
            int stepY = newY > y ? 1 : -1;
            for (int i = y + stepY; i != newY; i += stepY) {
                if (board[x][i] != null) {
                    return false; // Путь заблокирован
                }
            }
        } else {
            int stepX = newX > x ? 1 : -1;
            for (int i = x + stepX; i != newX; i += stepX) {
                if (board[i][y] != null) {
                    return false; // Путь заблокирован
                }
            }
        }
        // Проверка на возможность атаки
        return board[newX][newY] == null || !board[newX][newY].getColor().equals(color);
    }

    @Override
    public char getSymbol() {
        return color.equals("White") ? 'R' : 'r';
    }
}

class Queen extends Chess {
    public Queen(String color, int x, int y) {
        super(color, x, y);
    }

    @Override
    public boolean isValidMove(int newX, int newY, Chess[][] board) {
        if (!canMoveToPosition(newX, newY)) {
            return false;
        }
        if (newX == x || newY == y || Math.abs(newX - x) == Math.abs(newY - y)) {
            return isPathClear(newX, newY, board);
        }
        return false;
    }

    private boolean isPathClear(int newX, int newY, Chess[][] board) {
        if (newX == x) {
            return new Rook(color, x, y).isValidMove(newX, newY, board);
        } else if (newY == y) {
            return new Rook(color, x, y).isValidMove(newX, newY, board);
        } else {
            return new Bishop(color, x, y).isValidMove(newX, newY, board);
        }
    }

    @Override
    public char getSymbol() {
        return color.equals("White") ? 'Q' : 'q';
    }
}

class King extends Chess {
    public King(String color, int x, int y) {
        super(color, x, y);
    }

    @Override
    public boolean isValidMove(int newX, int newY, Chess[][] board) {
        if (!canMoveToPosition(newX, newY)) {
            return false;
        }
        // Проверка на допустимые движения короля
        if (Math.abs(newX - x) <= 1 && Math.abs(newY - y) <= 1) {
            // Проверка на возможность атаки
            return board[newX][newY] == null || !board[newX][newY].getColor().equals(color);
        }
        return false;
    }

    @Override
    public char getSymbol() {
        return color.equals("White") ? 'K' : 'k';
    }
}

class ChessGame {
    private Chess[][] board;
    private String currentPlayer;

    public ChessGame() {
        board = new Chess[8][8];
        setupBoard();
        currentPlayer = "White";
    }

    private void setupBoard() {
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn("White", 1, i);
            board[6][i] = new Pawn("Black", 6, i);
        }
        board[0][0] = new Rook("White", 0, 0);
        board[0][1] = new Horse("White", 0, 1);
        board[0][2] = new Bishop("White", 0, 2);
        board[0][3] = new Queen("White", 0, 3);
        board[0][4] = new King("White", 0, 4);
        board[0][5] = new Bishop("White", 0, 5);
        board[0][6] = new Horse("White", 0, 6);
        board[0][7] = new Rook("White", 0, 7);

        board[7][0] = new Rook("Black", 7, 0);
        board[7][1] = new Horse("Black", 7, 1);
        board[7][2] = new Bishop("Black", 7, 2);
        board[7][3] = new Queen("Black", 7, 3);
        board[7][4] = new King("Black", 7, 4);
        board[7][5] = new Bishop("Black", 7, 5);
        board[7][6] = new Horse("Black", 7, 6);
        board[7][7] = new Rook("Black", 7, 7);
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printBoard();
            System.out.println(currentPlayer + "'s turn. Enter move (e.g. e2 e4): ");
            String move = scanner.nextLine();
            String[] parts = move.split(" ");
            if (parts.length != 2) {
                System.out.println("Invalid input. Please enter a valid move.");
                continue;
            }
            int[] from = parsePosition(parts[0]);
            int[] to = parsePosition(parts[1]);

            if (isValidMove(from[0], from[1], to[0], to[1])) {
                // Выполнение хода
                board[to[0]][to[1]] = board[from[0]][from[1]];
                board[from[0]][from[1]] = null;
                currentPlayer = currentPlayer.equals("White") ? "Black" : "White";
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
    }

    private boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        Chess piece = board[fromX][fromY];
        if (piece == null || !piece.getColor().equals(currentPlayer)) {
            return false;
        }
        return piece.isValidMove(toX, toY, board);
    }

    private int[] parsePosition(String pos) {
        int x = 8 - Character.getNumericValue(pos.charAt(1));
        int y = pos.charAt(0) - 'a';
        return new int[]{x, y};
    }

    private void printBoard() {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(board[i][j].getSymbol() + " ");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        game.play();
    }
}