package edu.wm.cs.cs301.slidingpuzzle;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

public class SimplePuzzleState implements PuzzleState {
	//Stores values on the board
	private int[][] board;
	//Stores previous state before current state
	private PuzzleState parent;
	//Provides moves to do on the board
	private Operation operation;
	//stores the history of states the puzzle has been in
	private int pathlength;
	
	/*PuzzleState constructor with not specified parameters
	 * This will be the default constructor when making new puzzle states
	 */
	public SimplePuzzleState() {
		this.board = null;
		this.parent = null;
		this.operation = null;
		this.pathlength = 0;
	}
	
	//Puzzle state purposefully created to help with returning state after performing move operations
	public SimplePuzzleState(int [][]board, PuzzleState parent, Operation operation, int pathlength) {
		this.board = board;
		this.parent = parent;
		this.operation = operation;
		this.pathlength = pathlength;
	}
	
	/*This method returns code to it's first state when the board was newly created
	 * and also creates a new board when starting out
	 */
	@Override
	public void setToInitialState(int dimension, int numberOfEmptySlots) {
		int counter = 1;
		this.board = new int [dimension][dimension];
		//nested loop to populate board
		for (int i = 0; i < dimension; i++) {
			for(int j = 0; j< dimension; j++) {
				if (counter <= (dimension * dimension)- numberOfEmptySlots) {
					board[i][j] = counter;
					counter++;
				}
				else {
					board[i][j] = 0;
					counter++;
				}
			}
		}
					
	}

	//Returns value at given row and column
	@Override
	public int getValue(int row, int column) {
		if ((row >= 0 && row < board.length) && (column >= 0 && column < board.length)) {
			return board[row][column];
		}
		return -1;
		
		
	}

	//Returns previous state the puzzle board was in before the current one
	@Override
	public PuzzleState getParent() {
		return this.parent;
	}

	//Returns the operation the puzzle state has reached
	@Override
	public Operation getOperation() {
		return this.operation;
	}

	//Return the number of states or steps it took to get to current state
	@Override
	public int getPathLength() {
		return this.pathlength;
	}

	//Determines what move operations to perform to empty slots
	@Override
	public PuzzleState move(int row, int column, Operation op) {
		int[][] newBoard = new int [board.length][board[0].length];
		for(int i = 0; i < newBoard.length; i++) {
			for(int j = 0; j < newBoard[0].length; j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		
		//Switch statement helps determine what actions to take for each move based on circumstances
		switch(op) {
		case MOVERIGHT:
			if(column+1 < board.length) {
				if(isEmpty(row, column+1)) {
					int valueToMove = board[row][column];
					newBoard[row][column] = 0;
					newBoard[row][column+1] = valueToMove;
					break;
				}
				else {
					return null;
				}
			}
			else {
				return null;
			}
		case MOVELEFT:
			if(column-1 >= 0) {
				if(isEmpty(row, column-1)) {
					int valueToMove = board[row][column];
					newBoard[row][column] = 0;
					newBoard[row][column-1] = valueToMove;
					break;
				}
				else {
					return null;
				}
			}
			else {
				return null;
			}
		case MOVEUP:
			if(row-1 < board.length) {
				if(isEmpty(row-1, column)) {
					int valueToMove = board[row][column];
					newBoard[row][column] = 0;
					newBoard[row-1][column] = valueToMove;
					break;
				}
				else {
					return null;
				}
			}
			else {
				return null;
			}
		case MOVEDOWN:
			if(row+1 < board.length) {
				if(isEmpty(row+1, column)) {
					int valueToMove = board[row][column];
					newBoard[row][column] = 0;
					newBoard[row+1][column] = valueToMove;
					break;
				}
				else {
					return null;
				}
			}
			else {
				return null;
			}
		default:
			break;
		}
	/*return new state with current changes in the board, what operation was made
	 *  and count path length since a move was made
	 */
	return new SimplePuzzleState(newBoard, this, op, this.pathlength+1);	
	}

	//Allows interaction with GUI to perform move operations
	@Override
	public PuzzleState drag(int startRow, int startColumn, int endRow, int endColumn) {
		PuzzleState newPuzzleState = new SimplePuzzleState();
		newPuzzleState = this;
		
		//checking the distance between where we are and where we want to move
		int rowDistance = endRow - startRow;
		int colDistance = endColumn - startColumn;
		
		while (rowDistance != 0 || colDistance != 0) {
			//making sure we aren't trying to move and empty slot
			if(isEmpty(startRow, startColumn) == true) {
				return null;
			}
			
			//making sure where we are moving is empty
			if(isEmpty(endRow, endColumn) == false) {
				return null;
			}
			
			/*Since we are under the assumption that there is a bunch of empty 
			 * slots between where we are and the row above we want to get to, try moving
			 * to first empty slot above if possible
			 */
			if(isEmpty(startRow-1,startColumn)== true && rowDistance < 0) {
				newPuzzleState = newPuzzleState.move(startRow, startColumn, Operation.MOVEUP);
				startRow = startRow - 1;
				return newPuzzleState.drag(startRow, startColumn, endRow, endColumn);
			}
			
			/*Since we are under the assumption that there is a bunch of empty 
			 * slots between where we are and the row below we want to get to, try moving
			 * to first empty slot below if possible
			 */
			if(isEmpty(startRow+1, startColumn)== true && rowDistance > 0) {
				newPuzzleState = newPuzzleState.move(startRow, startColumn, Operation.MOVEDOWN);
				startRow = startRow + 1;
				return newPuzzleState.drag(startRow, startColumn, endRow, endColumn);
			}
			
			/*Since we are under the assumption that there is a bunch of empty 
			 * slots between where we are and the column to the right we want to get to, try moving
			 * to first empty slot on your right if possible
			 */
			if(isEmpty(startRow, startColumn+1)==true && colDistance > 0) {
				newPuzzleState = newPuzzleState.move(startRow, startColumn, Operation.MOVERIGHT);
				startColumn = startColumn +1;
				return newPuzzleState.drag(startRow, startColumn, endRow, endColumn);
			}
			
			/*Since we are under the assumption that there is a bunch of empty 
			 * slots between where we are and the column to the left we want to get to, try moving
			 * to first empty slot on your left if possible
			 */
			if(isEmpty(startRow, startColumn-1)== true && colDistance < 0) {
				newPuzzleState = newPuzzleState.move(startRow, startColumn, Operation.MOVELEFT);
				startColumn = startColumn -1;
				return newPuzzleState.drag(startRow, startColumn, endRow, endColumn);
			}
		}
		//Update state we have reached to whichever recursive drag method called it
		return this;
	}

	/*This method randomly moves board pieces around to get to different states
	 * based on the length we want
	 */
	@Override
	public PuzzleState shuffleBoard(int pathLength) {
		PuzzleState newPuzzleState = new SimplePuzzleState();
		newPuzzleState = this;
		Random rand = new Random();
		/*Checking to make sure we are shuffling to a different state because
		 * there would be no point in passing it a pathlengh of 0 since it won't do anything
		 */
		while (pathLength > 0) {
			while(true) {
				//These arraylists store the positions of the empty rows 
				ArrayList<Integer> emptyRows = new ArrayList<Integer>();
				ArrayList<Integer> emptyColumns = new ArrayList<Integer>();
				for(int i = 0; i < board.length; i++) {
					for(int j = 0; j < board[0].length; j++) {
						if (isEmpty(i,j)== true) {
							emptyRows.add(i);
							emptyColumns.add(j);
						}
					}
				}
				
				int getRandomNumber = rand.nextInt(emptyColumns.size());
				int randomRow = emptyRows.get(getRandomNumber);
				int randomColumn = emptyColumns.get(getRandomNumber);
				int randomDirection = rand.nextInt(4);
				Operation randomOperation = null;
				
				/*
				 * The if and else of statements below are checking to see what 
				 * random direction we can move from into the empty slot
				 * based on the random int we generated
				 */
				if(randomDirection == 0) {
					randomOperation = Operation.MOVERIGHT;
					randomColumn = randomColumn - 1;
				}
				else if(randomDirection == 1) {
					randomOperation = Operation.MOVELEFT;
					randomColumn = randomColumn +  1;
				}
				else if(randomDirection == 2) {
					randomOperation = Operation.MOVEUP;
					randomRow = randomRow + 1;
				}
				else if(randomDirection == 3) {
					randomOperation = Operation.MOVEDOWN;
					randomRow = randomRow - 1;
				}
				
				/*
				 * The if and else ifs statements below check to make sure we are not
				 * moving in a cycle
				 */
				
				if(randomOperation == Operation.MOVELEFT && this.getOperation() == Operation.MOVERIGHT) {
					continue;
				}
				else if(randomOperation == Operation.MOVERIGHT && this.getOperation() == Operation.MOVELEFT) {
					continue;
				}
				else if(randomOperation == Operation.MOVEDOWN && this.getOperation() == Operation.MOVEUP) {
					continue;
				}
				else if(randomOperation == Operation.MOVEUP && this.getOperation() == Operation.MOVEDOWN) {
					continue;
				}
				
				if(isEmpty(randomRow, randomColumn)== true) {
					continue;
				}
				
				if(randomRow < 0 || randomRow >= board.length || randomColumn < 0 || randomColumn >= board[0].length) {
					continue;
				}
				
				PuzzleState shuffle = this.move(randomRow, randomColumn, randomOperation);
				newPuzzleState = shuffle;
				break;
			}
			//decrease states so we fulfill number of states we want to achieve
			return newPuzzleState.shuffleBoard(pathLength-1);
			}
		//return final shuffle state
		return this;
	}

	//make sure given coordinates are empty slots
	@Override
	public boolean isEmpty(int row, int column) {
		if ((row >= 0 && row < board.length) && (column >= 0 && column < board.length)) {
			if(board[row][column] == 0)
			return true;
		}
		return false;
	}

	//Returns initial state
	@Override
	public PuzzleState getStateWithShortestPath() {
		return this;
	}

	//Eclipse has an auto generate for the hashcode and equals and I used that
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(board);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePuzzleState other = (SimplePuzzleState) obj;
		if (!Arrays.deepEquals(board, other.board))
			return false;
		return true;
	}

	
}
