package com.eecs592.kt;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

class Square{
	int row, col;
	int fixedDegree, dynamicDegree;
	boolean isVisited;
	public Square(int x, int y) {
		row = x;
		col = y;
		isVisited = false;
	}
}

public class KnightsTour {

	private static int sizeOfBoard = 8;
	private static int numOfMoves = 1000;
	private static Square[][] board = new Square[sizeOfBoard + 1][sizeOfBoard + 1];	// Start from 1.
	
	private static final int[][] MOVES = { {1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2} };
	
	// Initialize the board.
	private static void generateBoard(int sizeOfBoard, Square[][] board) {
		for(int i = 1; i <= sizeOfBoard; i++) {
			for(int j = 1; j <= sizeOfBoard; j++) {
				board[i][j] = new Square(i,j);
			}
		}
	}
	
	private static void computeFixedDegrees() {
		for(int i = 1; i <= sizeOfBoard; i++) {
			for(int j = 1; j <= sizeOfBoard; j++) {
				int numOfFixedDegree = 0;
				for(int k = 0; k < MOVES.length; k++) {
					if(i + MOVES[k][0] <= sizeOfBoard && i - MOVES[k][0] >= 1 && 
							j + MOVES[k][1] <= sizeOfBoard && j - MOVES[k][1] >= 1)
						numOfFixedDegree++;
				}
				board[i][j].fixedDegree = numOfFixedDegree;
				board[i][j].dynamicDegree = numOfFixedDegree;
			}
		}
		//printDegreeMatrix("fixed");
	}	
	
	private static List<int[]> computeDynamicDegree(int row, int col, int pathLength) {
		List<int[]> validMoves = new LinkedList<>();
		for(int k = 0; k < MOVES.length; k++) {
			int rowNew = row + MOVES[k][0], colNew = col + MOVES[k][1];
			if(rowNew <= sizeOfBoard && rowNew >= 1 && 
					colNew <= sizeOfBoard && colNew >= 1) {
				if(!board[rowNew][colNew].isVisited || (pathLength > 10 && rowNew == 2 && colNew == 3)) {
					validMoves.add(MOVES[k]);
				}
			}
		}	
		return validMoves;
	}
	
	private static void updateDynamicDegree(int row, int col) {
		for(int k = 0; k < MOVES.length; k++) {
			if(row + MOVES[k][0] <= sizeOfBoard && row + MOVES[k][0] >= 1 && 
					col + MOVES[k][1] <= sizeOfBoard && col + MOVES[k][1] >= 1) {
				if(!board[row + MOVES[k][0]][col + MOVES[k][1]].isVisited) {
					board[row + MOVES[k][0]][col + MOVES[k][1]].dynamicDegree--;
				}
			}
		}	
		//printDegreeMatrix("dynamic");
	}
	
	private static void strategies() {
		System.out.println("KT: " + sizeOfBoard + " x " + sizeOfBoard + " strategy = 1, start = 2,3");
		List<String> res = new LinkedList<>();
		board[2][3].isVisited = true;
		
		Stack<Square> stack = new Stack<>();
		Stack<StringBuilder>	path = new Stack<>();
		stack.push(board[2][3]);
		path.push(new StringBuilder("2,3"));
		numOfMoves--;
		updateDynamicDegree(2, 3);	
		
		int row = 2, col = 3;
		while(!stack.isEmpty()){
			while(stack.peek().dynamicDegree == 0 || computeDynamicDegree(stack.peek().row, stack.peek().col, path.peek().length()).size() == 0) {
				System.out.println("stack.peek().dynamicDegree == 0");
				Square s = stack.pop();
				path.pop();
				s.isVisited = false;
				updateDynamicDegree(row, col);
				numOfMoves++;
				if(stack.isEmpty())	break;
			}
			
			if(stack.isEmpty())	break;
			List<int[]> validMoves = new LinkedList<>();
			Square currentSquare = stack.pop();
			StringBuilder currentPath = path.pop();
			row = currentSquare.row;
			col = currentSquare.col;
			//System.out.println("current square = " + row + " " + col);
			//System.out.println("current path = " + currentPath);
			validMoves = computeDynamicDegree(row, col, currentPath.length());
			List<int[]> minFixedDegreePos = new LinkedList<>();
			int minFixedDegree = board[row + validMoves.get(0)[0]][col + validMoves.get(0)[1]].fixedDegree;
			
			for(int i = 1; i < validMoves.size(); i++) {
				minFixedDegree = Math.min(minFixedDegree, board[row + validMoves.get(i)[0]][col + validMoves.get(i)[1]].fixedDegree);
			}
			//System.out.println("minFixedDegree = " + minFixedDegree);
			for(int i = 0; i < validMoves.size(); i++) {
				if(board[row + validMoves.get(i)[0]][col + validMoves.get(i)[1]].fixedDegree == minFixedDegree)
					minFixedDegreePos.add(new int[] {row + validMoves.get(i)[0], col + validMoves.get(i)[1]});
			}
			
			for(int i = 0; i < minFixedDegreePos.size(); i++) {
				row = minFixedDegreePos.get(i)[0];	
				col = minFixedDegreePos.get(i)[1];
				//System.out.println("The square with the minumum fixed degree: " + row + " " + col);
				numOfMoves--;
				updateDynamicDegree(row, col);
				board[row][col].isVisited = true;
				StringBuilder newPath = new StringBuilder(currentPath + " " + row + "," + col);
				
				if(row == 2 && col == 3) {
					res.add(newPath.toString());
					System.out.println(newPath.toString());
					System.out.println(newPath.length());
					stack.pop();
					path.pop();
				}else {
				stack.push(board[row][col]);
				path.push(newPath);
				}
				
			}
		}
	}
	
	
	public static void main(String[] args) {
		generateBoard(sizeOfBoard, board);
		computeFixedDegrees();
		strategies();
	}
	
	private static void printDegreeMatrix(String s) {
		// Print 2-d array numOfFixedDegree.
		System.out.println("The " + sizeOfBoard + " * " + sizeOfBoard + " " + s + " degree matrix is:");
		for(int i = 1; i <= sizeOfBoard; i++) {
			for(int j = 1; j <= sizeOfBoard; j++) {
				if(s == "fixed")		System.out.print(board[i][j].fixedDegree + " ");
				if(s == "dynamic")		System.out.print(board[i][j].dynamicDegree + " ");
			}
			System.out.println();
		}
	}

}
