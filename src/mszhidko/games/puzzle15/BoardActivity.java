package mszhidko.games.puzzle15;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import mszhidko.games.movingtile.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebSettings.TextSize;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class BoardActivity extends ActionBarActivity {

	static int mDim;
	static PuzzleFragment mBoardFrag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board);
		mDim = getIntent().getIntExtra(GameMenuActivity.PUZZLE_DIMENTION, 3);

		mBoardFrag = new PuzzleFragment();
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, mBoardFrag).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.board, menu);
		return true;
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {


			
			Log.i("Mikhail", "Settings pressed");
			Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
            
		} else if (id == R.id.action_exit) {
			Log.i("Mikhail", "Exit pressed");
			
			exitPuzzle();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void exitPuzzle() {
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 
		// Set title
		alertDialogBuilder.setTitle("Puzzle15");

		// Set dialog message
		alertDialogBuilder
			.setMessage("Are you sure?")
			.setCancelable(false)
			.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog,int id) {
					
					moveTaskToBack(true); 
					finish();
					
				}
			  })
			.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog,int id) {
					
					// Cancelled by user, do nothing here
					
				}
			});

		// Create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// Show it
		alertDialog.show();
			
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PuzzleFragment extends Fragment {

		private RelativeLayout mLayout;
		private TileButton[][] mButtons;
		private Board mBoard;
		TextView mMovesTB;
		
		OnSharedPreferenceChangeListener mListener;
		
		private final static String fileName = "TestFile.txt";
		
		public Board getBoard() {
			return mBoard;
		}
		
		public void setBoard(Board newBoard) {
			mBoard = newBoard;
		}

		protected int mBoardWidth;
		protected int mBoardHeight;
		protected int mBoardTop;
		protected int mBoardLeft;
		private boolean isUiInited;
		private int N;
		MultiTouchListener mTouchListener;
		
		public PuzzleFragment() {

			N = mDim;
			//int initial_board[][] = {{6, 1, 3, 12}, {4, 2, 5, 10}, {7, 8, 0, 9 }, {1, 2, 3, 4}};
			int initial_board[][] = generateBoard(N);
			if (initial_board == null) {
				mBoard = new Board(Board.generate_board(N));
			} else {
				mBoard = new Board(initial_board);
			}
			
			mButtons = new TileButton[N][N];
			
			/*final Handler toastHandler = new Handler();
			
			new Thread( new Runnable() {
				
				@Override
				public void run() {
					Solver solver = new Solver(mBoard);
					
					final StringBuffer body = new StringBuffer("");
					
				    // print solution to standard output
				    if (!solver.isSolvable()) {
				        body.append("No solution possible");
				    } else {
				        body.append("Minimum number of moves = " + solver.moves() + "\n");
				        //for (Board board : solver.solution())
				        //    body += board.toString() + "\n";
				    }
				    
				    Runnable anotherRunnable = new Runnable() {
						
						public void run() {
							Toast.makeText(	getActivity(), 
											body, 
											Toast.LENGTH_LONG).show();
							}
					};
					
				    toastHandler.post(anotherRunnable);
					
				}
			}).start();*/
				
		}

		void resetButton() { /* The button has been moved (with animation) to a new position */
			mTouchListener.resetButton();
		}
		
		int[][] generateBoard(int dim) {
			
			int[][] initial_board = new int[dim][dim];
			
			if (dim == 2) {
				
				int val = (int) (Math.random() * 3);
				
				switch (val) {
				case 0:
					initial_board[0][0] = 0;
					initial_board[0][1] = 1;
					initial_board[1][0] = 3;
					initial_board[1][1] = 2;
					break;
				case 1:
					initial_board[0][0] = 3;
					initial_board[0][1] = 0;
					initial_board[1][0] = 2;
					initial_board[1][1] = 1;
					break;
				default:
					initial_board[0][0] = 0;
					initial_board[0][1] = 3;
					initial_board[1][0] = 2;
					initial_board[1][1] = 1;
				}
				
				
				
				return initial_board;
				
			} else if (dim == 3) {
				
				int val = (int) (Math.random() * 5);
				switch (val) {
				case 0:
					initial_board[0][0]= 1;
					initial_board[0][1]= 2;
					initial_board[0][2]= 3;
					initial_board[1][0]= 4;
					initial_board[1][1]= 5;
					initial_board[1][2]= 6;
					initial_board[2][0]= 7;
					initial_board[2][1]= 0;
					initial_board[2][2]= 8;
					break;
				case 1:
					
					//int[][] blocks1 = {{6, 7, 2}, {4, 0, 5}, {1, 8, 3}}; // 22 moves

					initial_board[0][0]= 6;
					initial_board[0][1]= 7;
					initial_board[0][2]= 2;
					initial_board[1][0]= 4;
					initial_board[1][1]= 0;
					initial_board[1][2]= 5;
					initial_board[2][0]= 1;
					initial_board[2][1]= 8;
					initial_board[2][2]= 3;
					
					break;
					
				case 2:
					//int[][] blocks2 = {{3, 6, 5}, {7, 8, 0}, {1, 4, 2}}; // 21 moves
					
					initial_board[0][0]= 3;
					initial_board[0][1]= 6;
					initial_board[0][2]= 5;
					initial_board[1][0]= 7;
					initial_board[1][1]= 8;
					initial_board[1][2]= 0;
					initial_board[2][0]= 1;
					initial_board[2][1]= 4;
					initial_board[2][2]= 2;
					
					break;
					
				case 3:
					//int[][] blocks3 = {{4, 6, 2}, {5, 3, 1}, {8, 0, 7}}; // 25 moves
					
					initial_board[0][0]= 4;
					initial_board[0][1]= 6;
					initial_board[0][2]= 2;
					initial_board[1][0]= 5;
					initial_board[1][1]= 3;
					initial_board[1][2]= 1;
					initial_board[2][0]= 8;
					initial_board[2][1]= 0;
					initial_board[2][2]= 7;
					
					break;
					
				case 5:
					
					//int[][] blocks4 = {{7, 4, 5}, {8, 0, 2}, {1, 6, 3}}; // 22 moves
					
					initial_board[0][0]= 7;
					initial_board[0][1]= 4;
					initial_board[0][2]= 5;
					initial_board[1][0]= 8;
					initial_board[1][1]= 0;
					initial_board[1][2]= 2;
					initial_board[2][0]= 1;
					initial_board[2][1]= 6;
					initial_board[2][2]= 3;
					
					break;
					
				case 6:
					
					//int[][] blocks5 = {{3, 7, 4}, {6, 0, 1}, {8, 2, 5}}; // 24 moves
					
					initial_board[0][0]= 3;
					initial_board[0][1]= 7;
					initial_board[0][2]= 4;
					initial_board[1][0]= 6;
					initial_board[1][1]= 0;
					initial_board[1][2]= 1;
					initial_board[2][0]= 8;
					initial_board[2][1]= 2;
					initial_board[2][2]= 5;
					
					break;
					
				default:
					
					return null;
						
				}
				
				return initial_board;
				
			} else if (dim == 4) {
				return null;
			} else {
				return null;
			}
		}
		
		@Override
	    public void onAttach(Activity activity) {
	        
			super.onAttach(activity);
	        //test_serializable(activity);
	        
	    }
		
		void test_serializable(Activity a) {
			int b1[][] = {{1, 2, 3}, {4, 5, 6}, {7, 0, 8}};
			int b2[][] = {{3, 3, 3}, {4, 5, 6}, {7, 0, 8}};
			ArrayList<Board> l  = new ArrayList<Board>();
			//l.add(new Board(b1));
			//l.add(new Board(b2));
			
			/*
			try {
				
				//FileOutputStream fos = a.openFileOutput(fileName, MODE_PRIVATE);
				getResources().getAssets().open("Boards3x3.pzl", MODE_PRIVATE);
				//FileOutputStream fos = a.openFileOutput(, MODE_PRIVATE);
				ObjectOutputStream os = new ObjectOutputStream(fos);
			
				os.writeObject(l);
				os.close();
				fos.close();
				
				Log.i("Mikhail", "WRITE: success");
				
			} catch (IOException e) {
				Log.i("Mikhail", "WRITE: error");
			}*/
			
			try {
				
				//InputStream stream = a.getAssets().open("puzzles/Boards3x3.pzl");
				AssetManager am = getResources().getAssets();
				//AssetFileDescriptor descriptor = am.openFd("puzzles/Boards3x3.pzl");
				InputStream fis = am.open("puzzles/Boards3x3.pzl", MODE_PRIVATE);
				//FileInputStream fis = a.openFileInput(fileName);
				ObjectInputStream is = new ObjectInputStream(fis);
				l = (ArrayList<Board>) is.readObject();
				
				for (Board b : l) {
					Log.i("Mikhail", "1");
				}
				
				is.close();
			
			} catch (IOException e) {
				
				Log.i("Mikhail", "READ: error");
				
			} catch (ClassNotFoundException e) {
				
				Log.i("Mikhail", "READ: ClassNotFound error");
				
			}
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_board,
					container, false);
			
			mTouchListener = new MultiTouchListener(this);
			
		    rootView.setOnTouchListener(mTouchListener);
			    
			mLayout = (RelativeLayout) rootView.findViewById(R.id.boardFragment);
			if (mLayout != null) {
				mLayout.setBackgroundColor(0xFF111111);
			}
			
			mLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            @Override
	            public void onGlobalLayout() {
	            	
	            	if (!isUiInited) {
	            		Log.i("Mikhail", "onGlobalLayout called");
	                	initializeUI();
	            	} else {
	            		mLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
	            	}
	            	
	            }
	        });
			
			return rootView;
		}
		
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
		}
		
		protected void initializeUI() {
			if (!isUiInited) {
				
				View v = this.getView();
				mBoardHeight = v.getMeasuredHeight();
		        mBoardWidth = v.getMeasuredWidth();
		        Log.i("Mikhail", "initializeUI; mBoardHeight=" + mBoardHeight + "; mBoardWidth=" + mBoardWidth);
		        
		        int infoPanelHeight = (int) (0.15 * (double) mBoardHeight); // Height of the panel where we show number of performed moves
		        mBoardHeight -= infoPanelHeight;
		        
		        int buttonWidth = mBoardWidth / N;
				int buttonHeight = mBoardHeight / N;
				
				int textSize =  160 / N;
				mBoardLeft = v.getLeft();
				mBoardTop = v.getTop() + infoPanelHeight; // Offset the buttons 
				
				for (int i = 0; i < N; i++) {
					for (int j = 0; j < N; j++) {
						if (mBoard.get(i, j) == 0) { // Empty slot
							continue;
						}
						
						mButtons[i][j] = new TileButton(getActivity());
						mButtons[i][j].setFrame(this);
						int[] tag = {i, j}; // Button's tag is the row, column in Board's class
						mButtons[i][j].setTag(tag);
						mButtons[i][j].setText(String.valueOf(mBoard.get(i, j)));
						mButtons[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
						mButtons[i][j].setBackgroundColor(0xAAFFB10B);
						
						MarginLayoutParams marginParams = new MarginLayoutParams(buttonWidth - 4, buttonHeight - 4);
		                marginParams.setMargins(mBoardLeft + j * buttonWidth, mBoardTop + i * buttonHeight, 0, 0);
		                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
		                
		                mButtons[i][j].setLayoutParams(layoutParams);
		                mLayout.addView(mButtons[i][j]);
			                
					}
				}
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				boolean ret = prefs.getBoolean("moves_count_checkbox", true);
				
						
				mListener =	new OnSharedPreferenceChangeListener() {
					
					@Override
					public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
							String key) {

						if (key.equals("moves_count_checkbox")) {
							boolean v = sharedPreferences.getBoolean("moves_count_checkbox", true);
							if (v) {
								mMovesTB.setVisibility(View.VISIBLE);
							} else {
								mMovesTB.setVisibility(View.INVISIBLE);
							}
						}
					}
				};
				
				prefs.registerOnSharedPreferenceChangeListener(mListener);
				
				// Create text view with number of performed moves
				mMovesTB = new TextView(getActivity());
				
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(15, 30, 0, 0);
				mMovesTB.setLayoutParams(params);
				mMovesTB.setText("Moves: 0");
				mMovesTB.setTextColor(0xFFCCCCCC);
				mMovesTB.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
				mLayout.addView(mMovesTB);
				if (!ret) {
					mMovesTB.setVisibility(View.INVISIBLE);
				}
				
				// New game button
				Button newGame = new Button(getActivity());
				RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				buttonParams.setMargins(15, 30, 15, 0);
				newGame.setLayoutParams(buttonParams);
				newGame.setText("New Game");
				newGame.setId(0x52552552);
				newGame.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						FragmentManager manager = getActivity().getSupportFragmentManager();
						FragmentTransaction trans = manager.beginTransaction();
						trans.remove(PuzzleFragment.this);
						trans.commit();
						
						mBoardFrag = new PuzzleFragment();
						manager.beginTransaction().add(R.id.container, mBoardFrag).commit();
						manager.popBackStack();
					}
				});
				mLayout.addView(newGame);
				
				// Back button
				Button back = new Button(getActivity());
				RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				backParams.addRule(RelativeLayout.LEFT_OF, 0x52552552);
				backParams.setMargins(0, 30, 0, 0);
				back.setLayoutParams(backParams);
				back.setText("Back");
				back.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mTouchListener.back();
					}
				});
				mLayout.addView(back);
		        
				isUiInited = true;
			}
			
		}
		
		protected void updateMoves() {
			mMovesTB.setText("Moves: " + mBoard.getMoves());
		}
		
		public Button[][] getButtons() {
			return mButtons;
		}
		
		void gameOver() {
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
	 
				// Set title
				alertDialogBuilder.setTitle("Game over!");
	 
				// Set dialog message
				alertDialogBuilder
					.setMessage("Play again?")
					.setCancelable(false)
					.setPositiveButton("Yes, let me play again!", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog,int id) {
							
							FragmentManager manager = getActivity().getSupportFragmentManager();
							FragmentTransaction trans = manager.beginTransaction();
							trans.remove(PuzzleFragment.this);
							trans.commit();
							
							mBoardFrag = new PuzzleFragment();
							manager.beginTransaction().add(R.id.container, mBoardFrag).commit();
							
							manager.popBackStack();
						}
					  })
					.setNegativeButton("Exit",new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog,int id) {
							
							getActivity().moveTaskToBack(true); 
							getActivity().finish();
							
						}
					});
	 
					// Create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// Show it
					alertDialog.show();
			
		}
		
		public enum Direction {
		    LEFT, RIGHT, UP, DOWN, NONE
		}

	}

}

