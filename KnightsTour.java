package com.eecs592.kt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
	
	private static List<int[]> computeValidMoves(int row, int col, int pathLength) {
		List<int[]> validMoves = new ArrayList<>();
		for(int k = 0; k < MOVES.length; k++) {
			int rowNew = row + MOVES[k][0], colNew = col + MOVES[k][1];
			if(rowNew <= sizeOfBoard && rowNew >= 1 && 
					colNew <= sizeOfBoard && colNew >= 1) {
				if(pathLength == 63 && rowNew == 2 && colNew == 3) {
					validMoves.add(MOVES[k]);
					System.out.println("Find one end node.");
					break;
				}
				else if(!board[rowNew][colNew].isVisited) 	validMoves.add(MOVES[k]);
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
	
	private static List<int[]> decideTheNextPosition(List<int[]> validMoves, int row, int col){
		List<int[]> minFixedDegreePos = new ArrayList<>();
		int minFixedDegree = board[row + validMoves.get(0)[0]][col + validMoves.get(0)[1]].fixedDegree;
		
		for(int i = 1; i < validMoves.size(); i++) {
			minFixedDegree = Math.min(minFixedDegree, board[row + validMoves.get(i)[0]][col + validMoves.get(i)[1]].fixedDegree);
		}
		
		for(int i = 0; i < validMoves.size(); i++) {
			if(board[row + validMoves.get(i)[0]][col + validMoves.get(i)[1]].fixedDegree == minFixedDegree)
				minFixedDegreePos.add(new int[] {row + validMoves.get(i)[0], col + validMoves.get(i)[1]});
		}
		return minFixedDegreePos;
	}
	
	private static List<List<int[]>> strategies() {
		
		
		List<List<int[]>> res = new ArrayList<>();
		List<int[]>	path = new ArrayList<>();
		Stack<Square> stack = new Stack<>();
		
		int row = 2, col = 3;
		Square curNode = board[2][3];
		Square preNode = null;
		updateDynamicDegree(2, 3);
		
		while(!stack.isEmpty() || numOfMoves > 0){
			
			//System.out.println("Enter the strategy function.");
			while(path.size() < 64 && curNode != null) {
				row = curNode.row;
				col = curNode.col;
				stack.push(board[row][col]);
				path.add(new int[] {row, col});
				for(int i = 0; i < path.size(); i++)		System.out.print(path.get(i)[0] + "," + path.get(i)[1] + " ");
				System.out.println();
				System.out.println("path size == " + path.size());
				board[row][col].isVisited = true;
				updateDynamicDegree(row, col);
				numOfMoves--;
				
				List<int[]> validMoves = computeValidMoves(row, col, path.size());
				if(validMoves.size() == 0)	break;
				//System.out.println("validMoves.size == " + validMoves.size());
				
				List<int[]> minFixedDegreePos = new ArrayList<>();
				int[] randomNextNodePos = new int[2];
				Square ramdomNextNode;
				
				do {
					System.out.println("Enter the decide part.");
					minFixedDegreePos = decideTheNextPosition(validMoves, row, col);
					randomNextNodePos = minFixedDegreePos.get(new Random().nextInt(minFixedDegreePos.size()));
					//System.out.println("Temp value == " + randomNextNodePos[0] + "," + randomNextNodePos[1]);
					ramdomNextNode = new Square(randomNextNodePos[0], randomNextNodePos[1]);
				}while(ramdomNextNode == preNode);
				curNode = ramdomNextNode;
			}
			
			if(curNode == null) {
				if(stack.isEmpty()) {
					System.out.println("There is no solution.");
					break;
				}
				curNode = stack.peek();
				row = curNode.row;
				col = curNode.col;
				
				List<int[]> validMoves = computeValidMoves(row, col, path.size());
				if(validMoves.size() == 0)	break;
				//System.out.println("validMoves.size == " + validMoves.size());
				
				List<int[]> minFixedDegreePos = new ArrayList<>();
				int[] randomNextNodePos = new int[2];
				Square ramdomNextNode;
				int count = 0;
				
				do {
					//System.out.println("Enter the decide part.");
					minFixedDegreePos = decideTheNextPosition(validMoves, row, col);
					randomNextNodePos = minFixedDegreePos.get(new Random().nextInt(minFixedDegreePos.size()));
					//System.out.println("Temp value == " + randomNextNodePos[0] + "," + randomNextNodePos[1]);
					ramdomNextNode = new Square(randomNextNodePos[0], randomNextNodePos[1]);
					count ++;
				}while(ramdomNextNode.row == preNode.row && ramdomNextNode.col != preNode.col && count < 30);
				
				if(ramdomNextNode.row != preNode.row && ramdomNextNode.col != preNode.col) {
					System.out.println("preNode:" + preNode.row + "," + preNode.col);
					System.out.println("Choose a new node: " + ramdomNextNode.row + "," + ramdomNextNode.col);
					System.out.println("The second solution.");
					curNode = ramdomNextNode;
					continue;
				}
			}
			
			curNode = stack.peek();
			row = curNode.row;
			col = curNode.col;
				
			if(path.size() == 64 && row == 2 && col == 3) {
				System.out.println("This is a right solution!");
				res.add(new ArrayList<int[]>(path));
				
				for(int i = 0; i < path.size(); i++)		System.out.print(path.get(i) + " ");
				System.out.println();
				
			}
			
			curNode.isVisited = false;
			preNode = curNode;
			stack.pop();
			path.remove(path.size() - 1);
			System.out.println("Pop this node: " + curNode.row + "," + curNode.col);
			numOfMoves++;
			//updateDynamicDegree(row, col);
			curNode = null; 			
		}
		return res;
	}
	
	
	public static void main(String[] args) {
		List<List<int[]>> res = new ArrayList<>();
		generateBoard(sizeOfBoard, board);
		computeFixedDegrees();
		res = strategies();
		for(int i = 0; i < res.size(); i++) {
			System.out.println("KT: " + sizeOfBoard + " x " + sizeOfBoard + " strategy = 1, start = 2,3");
			System.out.print(i);
			System.out.println(res.get(i));
		}
	}
	
	/*
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
	*/

}
