package mszhidko.games.puzzle15;

import java.util.Arrays;
import java.util.Comparator;


public class Solver {
//	  public Solver(Board initial)            // find a solution to the initial board (using the A* algorithm)
//    public boolean isSolvable()             // is the initial board solvable?
//    public int moves()                      // min number of moves to solve initial board; -1 if no solution
//    public Iterable<Board> solution()       // sequence of boards in a shortest solution; null if no solution
//    public static void main(String[] args)  // solve a slider puzzle (given below)
	private boolean solvable=true;
	private int moves=0;
	private BoardContainter finalBoard=null;
	private Board[] steps;
	private class BoardComparator implements Comparator<BoardContainter>{
		@Override
		public int compare(BoardContainter o1, BoardContainter o2) {
			if (o1.assesment()>o2.assesment()) {
				return 1;
			}else if (o1.assesment()<o2.assesment()) {
				return -1;
			}else{
			return 0;
			}
		}
	}
	private class BoardContainter{
		private Board board;
		private int stepMoved;
		private BoardContainter rootBoardContainter;
		public BoardContainter(int stepMoved, Board board,BoardContainter root) {
			this.stepMoved=stepMoved;
			this.board=board;
			this.rootBoardContainter=root;
		}
		public int assesment() {
			return stepMoved+board.manhattan();
		}
		public Board getBoard() {
			return board;
		}
		public int getStepMoved() {
			return stepMoved;
		}
		public BoardContainter getRootBoardContainter() {
			return rootBoardContainter;
		}
	}
	public Solver(Board initial){
		AstarAlgorithm(initial);
	}
	private void AstarAlgorithm(Board initial){
		MinPQ<BoardContainter> statusQueue1=new MinPQ<BoardContainter>(new BoardComparator());
		MinPQ<BoardContainter> statusQueue2=new MinPQ<BoardContainter>(new BoardComparator());
		BoardContainter currBoard1=new BoardContainter(0, initial, null);
		BoardContainter currBoard2=new BoardContainter(0, initial.twin(), null);
		Board previousBoard1=null;
		Board previousBoard2=null;
		while (true) {
			if (currBoard1.getBoard().isGoal()) {
				finalBoard=currBoard1;
				solvable=true;
				break;
			}
			if (currBoard2.getBoard().isGoal()) {
				solvable=false;
				break;
			}
			try {
				previousBoard1=currBoard1.getRootBoardContainter().getBoard();
				previousBoard2=currBoard2.getRootBoardContainter().getBoard();
			} catch (Exception e) {
			}
			for (Board board : currBoard1.getBoard().neighbors()) {
				if (!board.equals(previousBoard1)) {
					statusQueue1.insert(new BoardContainter(currBoard1.getStepMoved()+1, board, currBoard1));
				}
			}
			for (Board board : currBoard2.getBoard().neighbors()) {
				if (!board.equals(previousBoard2)) {
					statusQueue2.insert(new BoardContainter(currBoard2.getStepMoved()+1, board,currBoard1));
				}
			}
			currBoard1=statusQueue1.delMin();
			currBoard2=statusQueue2.delMin();
			moves=currBoard1.getStepMoved();
		}
		steps=new Board[moves+1];
		for (BoardContainter boardContainter=finalBoard; boardContainter!=null; boardContainter=boardContainter.getRootBoardContainter()) {
			steps[boardContainter.getStepMoved()]=boardContainter.getBoard();
		}
	}
	public int moves(){
		if (!solvable) {
			return -1;
		}
		return moves;
	}
	public Iterable<Board> solution(){
		if (!solvable) {
			return null;
		}
		return Arrays.asList(steps);
	}
	public boolean isSolvable() {
		return solvable;
	}
	
	/*public static void main(String[] args){
		// create initial board from file
	    //In in = new In(args[0]);
	    int N = StdIn.readInt();
	    int[][] blocks = new int[N][N];
	    for (int i = 0; i < N; i++)
	        for (int j = 0; j < N; j++)
	            blocks[i][j] = StdIn.readInt();
	    Board initial = new Board(blocks);

	    // solve the puzzle
	    Solver solver = new Solver(initial);

	    // print solution to standard output
	    if (!solver.isSolvable())
	        StdOut.println("No solution possible");
	    else {
	        StdOut.println("Minimum number of moves = " + solver.moves());
	        for (Board board : solver.solution())
	            StdOut.println(board);
	    }
	}*/
}
