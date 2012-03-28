import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * The SuDoKuSolver class represents a Sudoku board, and publicly allows 
 * methods such as printing out the board and solving the board (or at 
 * least attempting to solve it). This is Ryan Seys' submission to the 
 * 3rd Assignment in SYSC 2100 (Winter 2012). 
 * 
 * To run this, compile by calling "javac SuDoKuSolver.java"
 * and then run by calling "java SuDoKuSolver filename_of_board.txt"
 * where filename_of_board.txt is the name of the file which contains
 * the sudoku board you are trying to solve.
 * 
 * If you don't pass a filename in, it will assume a filename as
 * specified in the main method below. This may result in a 
 * failure to read file message due to a failure to find the file.
 * 
 * To change the filename it assumes, adjust the value specified in
 * the main method below.
 * 
 * @author Ryan Seys - 100817604
 */
public class SuDoKuSolver {
	private int[][] board; //the 2-dimensional array that represents the sudoku board
	private int numOfSolns = 0; //the number of solutions that the board has.
	SuDoKuSolver(String filename) {
		board = readFile(filename); //read in the board from a file.
	}
	
	/**
	 * Prints the board in an easy to read format.
	 */
	public void printBoard() {
		for(int i = 0; i < 9; i++) {
			System.out.print("\n");
			for(int j = 0; j < 9; j++) {
				if(board[i][j] == 0) {
					System.out.print(" X "); //print this if the number isn't included.
				}
				else System.out.print(" " + board[i][j] + " ");
			}
		}
		System.out.print("\n");
	}
	
	/**
	 * This method reads in the Sudoku board from a file, given the filename.
	 * It returns the array representation of the Sudoku board. 
	 * All zeros (0) in the array returned are unsolved spaces.
	 * 
	 * @param filename the name of the file that contains the Sudoku board format.
	 * @return the Sudoku board in an 2-dimensional integer array.
	 */
	private int[][] readFile(String filename) {
		BufferedReader bf;
		int[][] array = new int[9][9]; //9x9 sudoku board.
		
		try {
			bf = new BufferedReader(new FileReader(filename));
			try {
				int numoflines = Integer.valueOf(bf.readLine()); //get the number of lines in the file
				//use the number of lines in the file for the number of iterations of the loop
				for(int i = 0; i < numoflines; i++) {
					//split by spaces into an array of numbers.
					String temp[] = bf.readLine().split("\\s+");
					int currentRow = Integer.valueOf(temp[0]); //get row
					int currentCol = Integer.valueOf(temp[1]); //get column

					//get value at that row and column
					int currentVal = Integer.valueOf(temp[2]);
					array[currentRow][currentCol] = currentVal; //push onto array
				}
				return array; //return the board once file has been read.
			} catch (IOException e) {
				System.out.println("Cannot read file " + filename);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find file " + filename);
		}
		return null;
	}
	
	/**
	 * Solves the Sudoku board. Runs the private driver method using
	 * parameters that the public user does not have access to.
	 * @return the number of solutions the board has.
	 */
	public int solve() {
		numOfSolns = 0; //reset the number of solutions.
		this.solve(board);
		return numOfSolns; //the number of solutions found.
	}
	
	/**
	 * Solves the Sudoku board. A recursive back-tracking method.
	 * Also increments the number of solutions when a solution is
	 * found. Private method because there is another driver method
	 * for this, which the user can interact with. We don't want to
	 * give access to the board to the user.
	 * 
	 * @param board the Sudoku board which we are solving
	 * @return The Sudoku board which has been solved so far. 
	 * The return value at the end of solving is the original board.
	 */
	private int[][] solve(int[][] board) {
		//find an empty spot
		int last = 1; //the last value we checked at that spot.
		int i = 0;	//i index on the board
		int j = 0;	//j index on the board
		
		//we return from the function within this loop
		//so it will not be an infinite loop
		while(true) {
			if(board[i][j] == 0) {
				//while we haven't tried every value at that spot
				//note that when we backtrack farther than this spot,
				//the "last" variable will be reset to 1. (this is a good thing).
				while(last <= 9) {
					//check the next the number at this spot
					if(possibleValue(i,j,last)) {
						//if it's a possible value, set the board to that spot
						board[i][j] = last;
						//then do it all over again, getting a new spot
						//with the newly changed board and trying values.
						solve(board);
					}
					//when we are finished trying to solve with that number combination
					//change the number to a new one, i.e. increment it.
					last++;
				}
				//set the value back to unsolved if no values work
				board[i][j] = 0; //the key to it all!
				
				//return to the previous function call stack to try
				//new numbers with other spaces to be solved
				return board;
			}
			
			//how to iterate through the 2-dimensional array
			else if(i < 8) {
				i++; //next row
			}
			
			//if you're at the bottom of the rows...
			else if((i == 8) && (j < 8)){
				i = 0; //go back to top of the rows
				j++; //skip to the next column
			}
			
			//you're at the last row and last column and are 
			//looking for more unfilled spots. You won't find 
			//anymore because you've found a solution! congratulations!
			else {
				//System.out.println("\n **** SOLUTION FOUND ****");
				numOfSolns++;
				if(numOfSolns % 1000000 == 0) {
					System.out.println("Solution: " + numOfSolns);
				}
				//this.printBoard(); //prints out the solution found
				return board;	//keep looking for solutions!
			}
		}
	}
	
	/**
	 * Returns whether the specified value can possibly go at the spot specified.
	 * 
	 * @param i the y coordinate to be checking
	 * @param j the x coordinate to be checking
	 * @param val the value we are trying to put at the point.
	 * @return True if the value can be placed there on the board. False if it cannot.
	 * @throws IndexOutOfBoundsException if the (i,j) combination is out of range.
	 */
	private boolean possibleValue(int i, int j, int val) {
		int iter;
		int jiter;
		
		//check row
		for(jiter = 0; jiter < 9; jiter++) {
			if(board[i][jiter] == val) {
				return false;
			}
		}
		
		//check column
		for(iter = 0; iter < 9; iter++) {
			if(board[iter][j] == val) {
				return false;
			}
		}
		
		//check the square the number is in.
		int ilower,iupper,jlower,jupper;
		
		//get the values of the range so we can check the right square
		if((i >= 0) && (i < 3)) {
			ilower = 0;
			iupper = 3;
		}
		else if ((i >= 3) && (i < 6)) {
			ilower = 3;
			iupper = 6;
		}
		else if ((i >= 6) && (i < 9)) {
			ilower = 6;
			iupper = 9;
		}
		else ilower = iupper = -1;

		if((j >= 0) && (j < 3)) {
			jlower = 0;
			jupper = 3;
		}
		else if ((j >= 3) && (j < 6)) {
			jlower = 3;
			jupper = 6;
		}
		else if ((j >= 6) && (j < 9)) {
			jlower = 6;
			jupper = 9;
		}
		else jlower = jupper = -1;

		//if invalid range is passed into this function, throw an exception.
		if((ilower == -1) || (iupper == -1) || (jlower == -1) || (jupper == -1)) {
			System.out.println("This index is out of bounds. " +
					"Must be between 0 and 9 (exclusively).");
			throw new IndexOutOfBoundsException();
		}

		//use the ranges retrieved above to check the appropriate square.
		for(iter = ilower; iter < iupper; iter++) {
			for(jiter = jlower; jiter < jupper; jiter++) {
				if(board[iter][jiter] == val) {
					return false;
				}
			}
		}
		return true; //if it passes all the tests above, return true!
	}

	/**
	 * The main method. Solves one puzzle from file at a time
	 * and outputs the results to the terminal. Gets the filename
	 * from the arguments passed to it from the command line. 
	 * Alternatively if no filename argument is given, use the 
	 * name of the file given at the start of this method.
	 * 
	 * @param args The filename of the puzzle you wish to run.
	 */
	public static void main(String[] args) {
		//put the file here you would like to test.
		final String DEFAULT_FILE_TO_SOLVE = "in1.txt";
		//if the user doesn't pass a filename through the command line,
		//then solve the file as listed above
		if(args.length == 0) {
			args = new String[1];
			args[0] = DEFAULT_FILE_TO_SOLVE;
		}
		//make the sudoku board given the filename.
		SuDoKuSolver sb = new SuDoKuSolver(args[0]);
		if(sb.board != null) {
			System.out.println("Successfully loaded " + args[0]);
			System.out.println("\nSolving:");
			int[][] a = new int[9][9];
			sb.board = a;
			sb.printBoard();
			int num = sb.solve();
			System.out.println("\nThere were " + num + " solution(s) found.");
			System.out.println("Done!");
		}
	}
}
