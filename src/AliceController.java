import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class AliceController {
	//do the list thing
	//check if the number of pieces is the number in the list and check if they are at the top.. if so, its solved
	private List<Piece> pieces1;
    public static void main(String[] args) {
        AliceController controller = new AliceController();
        controller.go();
    }
   
    public void go() {
        List<Board> boards = readBoardsFromFile();
        if (boards == null || boards.isEmpty()) {
            System.out.println("No valid boards found.");
            return;
        }

        int boardNumber = 1;
        for (Board board : boards) {
            List<String> solution = solveChessboard(board);
            if (solution != null && !solution.isEmpty()) {
                System.out.println("Board " + boardNumber + ": Alice should capture in this order: " + String.join("", solution));
            } else {
                System.out.println("Board " + boardNumber + ": Alice is stuck!");
            }
            boardNumber++;
        }
    }

    public List<String> solveChessboard(Board board) {
        int startRow = board.getHeight() - 1;
        int startCol = findAliceStartCol(board);
        if (startCol == -1) {
            return null; 
        }

        List<String> solution = new ArrayList<>();
        if (solveHelper(board, startRow, startCol, solution)) {
            return solution;
        } else {
        	// no solution found
            return null; 
        }
    }

    private int findAliceStartCol(Board board) {
        for (int j = 0; j < board.getWidth(); j++) {
            if (board.getPiece(board.getHeight() - 1, j) != null) {
                return j;
            }
        }
     // Alice's starting column not found
        return -1; 
    }

    private boolean solveHelper(Board board, int row, int col, List<String> solution) {
        Piece piece = board.getPiece(row, col);
       
      
        if (row == 0 && board.getPieces().size() == 1) {
        	solution.add(board.getPiece(row,col).getSymbol());
            return true;
        }
        List<int[]> possibleMoves = canPieceMoveToPosition(board, row, col);
        for (int[] move : possibleMoves) {
            int endRow = move[0];
            int endCol = move[1];
          
            		String pieceSymbol = piece.getSymbol();
                    solution.add(pieceSymbol);
                    board.setPiece(row, col, null); 
                    if(solveHelper(board,endRow,endCol,solution)==false) {
                    	 // Backtrack
                        solution.remove(solution.size() - 1);
                        // put the piece back on the board
                        board.setPiece(row, col, piece); 
                    //    System.out.println("Backtracked: " + piece.getSymbol() + " at position (" + row + ", " + col + ")");
                   
                    }else {
                    	// solution.add(piece.getSymbol());
                       //  System.out.println("Captured: " + piece.getSymbol() + " at position (" + row + ", " + col + ")");
                    	
                    	return true;
                    }
         
        }
      
       
        return false; 
    }
    
    public List<int[]> canPieceMoveToPosition(Board board, int startRow, int startCol) {
        Piece piece = board.getPiece(startRow, startCol);
     //   System.out.println("Piece: "+piece);
        List<int[]> moves = new ArrayList<>();
      //  System.out.println("Position:" + startRow + " , " + startCol);
        if (piece != null) {
        	
        //	System.out.println("Got into pieces not null loop");
            for (int row = 0; row < board.getHeight(); row++) {
                for (int col = 0; col < board.getWidth(); col++) {
                	Piece pp = board.getPiece(row, col);
                	//System.out.println("PP:"+ pp);
                	if(pp!=null) {
                		//System.out.println("PP not null:"+ pp);
                    if (board.isValidMove(startRow, startCol, row, col)==true) {
                    	//System.out.println("Moves: " + moves);
                        moves.add(new int[]{row, col}); 
                    }
                	}
                }
            }
        }
        
        return moves;
    } 
    
    public List<Board> readBoardsFromFile() {
        File file = getBoardFileFromUser();
        if (file == null) {
            System.out.println("No file selected.");
            return null;
        }

        List<Board> boards = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            int numBoards = scanner.nextInt();
            scanner.nextLine(); 

            for (int tb = 1; tb <= numBoards; tb++) {
                Board board = new Board();
                List<Piece> pieces = new ArrayList<>();
                for (int row = 0; row < 8; row++) {
                	
                    String line = scanner.nextLine();
                    for (int column = 0; column < 8; column++) {
                        char pieceSymbol = line.charAt(column);
                        if(convertCharToPiece(pieceSymbol)!= null) {
                        pieces.add(convertCharToPiece(pieceSymbol));
                        }
                        
                        board.setPiece(row, column, convertCharToPiece(pieceSymbol));
                        
                    }
                }
                pieces1 = board.getPieces(); 
                System.out.println("Pieces size for board " + tb + ": " + pieces1.size());
                
                boards.add(board); 
         
                board.print();
                
                
               
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.getAbsolutePath());
            return null;
        }

        return boards;
    }

    private Piece convertCharToPiece(char pieceSymbol) {
        switch (pieceSymbol) {
            case 'P':
                return new Pawn(null);
            case 'B':
                return new Bishop(null);
            case 'R':
                return new Rook(null);
            case 'N':
                return new Knight(null);
            case 'Q':
                return new Queen(null);
            case 'K':
                return new King(null);
            case '-':
                return null;
            default:
                return null;
        }
    }

    public File getBoardFileFromUser() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }
}
