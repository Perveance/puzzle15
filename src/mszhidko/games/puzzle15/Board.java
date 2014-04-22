package mszhidko.games.puzzle15;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {

	private int N;
	private int moves;
	
	private int[][] blocks;
	private int[][] coordinates; 
	
	// construct a board from an N-by-N array of blocks
    public Board(int[][] blocks) {
        // (where blocks[i][j] = block in row i, column j)
    	
    	N = blocks.length;
    	
    	this.blocks = new int [N][N];
    	for (int i = 0; i < N; i++) {
    		for (int j = 0; j < N; j++) {
    			this.blocks[i][j] = blocks[i][j];
    		}
    	}
    	
    	coordinates = new int [N * N][2];
    	int k = 1;
    	for (int i = 1; i <= N; i++) {
    		for (int j = 1; j <= N; j++) {
    			coordinates[k - 1][0] = i;
    			coordinates[k - 1][1] = j;
    			k++;
    		}
    	}
    }
    
    static public int[][] generate_board() {
    	int initial_board[][] = {{0, 1, 3}, {4, 2, 5}, {7, 8, 6 }};
    	Set<Integer> s = new HashSet<Integer>();
    	for (int i = 0; i < 3; i++) {
    		for (int j = 0; j < 3; j++) {
    			int val = (int)(Math.random() * 9);
    			while (s.add(Integer.valueOf(val)) == false) {
    				val = (int) (Math.random() * 9);
    			}
    			initial_board[i][j] = val;
    			
    		}
    	}
    	
    	
    	return initial_board;
    }
    
    public int get(int i, int j) {
    	if (i < 0 || i >= N || j < 0 || j >= N) {
    		return -1;
    	} else {
    		return blocks[i][j];
    	}
    }
    // board dimension N
    public int dimension() {
    	return N;
    }
    
    // number of blocks out of place
    public int hamming() {
    	//System.out.println("moves = " + moves);
    	
    	return moves + numberOfWrongBlocks();
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
    	int distance = 0;
    	for (int i = 1; i <= N; i++) {
    		for (int j = 1; j <= N; j++) {
    			int v = blocks[i - 1][j - 1];
    			if (v == 0)
    				continue;
    			
    			distance += Math.abs(coordinates[v - 1][0] - i);
    			distance += Math.abs(coordinates[v - 1][1] - j);
    		}
    	}
    	return moves + distance;
    }
    
    // is this board the goal board?
    public boolean isGoal() {
    	if (numberOfWrongBlocks() == 0)
    		return true;
    	return false;
    }
    
    // a board obtained by exchanging two adjacent blocks in the same row
    public Board twin() {
    	Board twin = new Board(blocks);
    	for (int i = N-1; i >= 0; i--) {
    		if ((twin.blocks[i][0] != 0) && (twin.blocks[i][1]) != 0) {
    			int temp = twin.blocks[i][0];
    			twin.blocks[i][0] = twin.blocks[i][1];
    			twin.blocks[i][1] = temp;
    			//System.out.println("Twin is generated!");
    			break;
    		}
    	}
    	
    	return twin;
    }
    
    private void swap(int row1, int column1, int row2, int column2) {
    	int temp = blocks[row1][column1];
		blocks[row1][column1] = blocks[row2][column2];
		blocks[row2][column2] = temp;
    }
    
    public boolean move(int i, int j) {
		if (i != 0) {
			if (blocks[i - 1][j] == 0) {
				moves++;
				swap(i, j, i - 1, j);
				return true;
			}
		}
		
		if (i != N - 1) {
			if (blocks[i + 1][j] == 0) {
				moves++;
				swap(i, j, i + 1, j);
				return true;
			}
		}
		
		if (j != 0) {
			if (blocks[i][j - 1] == 0) {
				moves++;
				swap(i, j, i, j - 1);
				return true;
			}
		}
		
		if (j != N - 1) {
			if (blocks[i][j + 1] == 0) {
				moves++;
				swap(i, j, i, j + 1);
				return true;
			}
		}
		
    	return false;
    }

    private int numberOfWrongBlocks() {
    	int k = 1;
    	int num = 0;
    	for (int i = 0; i < N; i++) {
    		for (int j = 0; j < N; j++) {
    			
    			if ((blocks[i][j] != k) && (blocks[i][j] != 0))
    				num++; 
    			
    			k++;
    		}
    	}
    	
    	return num;
    }
    
    // does this board equal y?
    public boolean equals(Object y)
    {
    	Board second;
    	if (y == null)
    		return false;
    	
    	if (y instanceof String) {
    		return this.toString().equals(y);
    	}else if (y instanceof Board) {
    		second = (Board) y;
    	} else {
    		return false;
    	}
    	
    	if (second.dimension() != this.dimension())
    		return false;
    			
    	for (int i = 0; i < N; i++) {
    		for (int j = 0; j < N; j++) {
    			if (blocks[i][j] != second.blocks[i][j])
    				return false;
    		}
    	}
    	
    	return true;
    }
    
    // all neighboring boards
    public Iterable<Board> neighbors() {
    	List<Board> l = new ArrayList<Board>();
    	
    	for (int i = 0; i < N; i++) {
    		for (int j = 0; j < N; j++) {
    			if (blocks[i][j] == 0) {
    				if (i != 0) {
    					Board n = new Board(blocks);
    					n.moves = this.moves + 1;
    					n.swap(i, j, i - 1, j);
    					l.add(n);
    				}
    				
    				if (i != N - 1) {
    					Board n = new Board(blocks);
    					n.moves = this.moves + 1;
    					n.swap(i, j, i + 1, j);
    					l.add(n);
    				}
    				
    				if (j != 0) {
    					Board n = new Board(blocks);
    					n.moves = this.moves + 1;
    					n.swap(i, j, i, j - 1);
    					l.add(n);
    				}
    				
    				if (j != N - 1) {
    					Board n = new Board(blocks);
    					n.moves = this.moves + 1;
    					n.swap(i, j, i, j + 1);
    					l.add(n);
    				}
    			}
    		}
    	}

    	return l;
    }
    
    // string representation of the board (in the output format specified below)
    public String toString() {
    	String s = Integer.toString(N) + "\n ";
    	
    	for (int i = 0; i < N; i++) {
    		for (int j = 0; j < N; j++) {
    			s += blocks[i][j] + " ";
    		}
    		s += "\n ";
    	}
    	
    	return s;
    }

    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int[][] new_blocks = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
		
		Board b = new Board(new_blocks);
		
		System.out.println(b.manhattan());
		b.neighbors();
		System.out.println(b.manhattan());
		b.neighbors();
		System.out.println(b.manhattan());
		b.neighbors();
		System.out.println(b.manhattan());
	}
}
