package mszhidko.games.puzzle15;

import mszhidko.games.movingtile.R;
import mszhidko.games.puzzle15.adapter.NavDrawerListAdapter;
import mszhidko.games.puzzle15.model.NavDrawerItem;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class BoardActivity extends ActionBarActivity {

    private static final int COLOR_MOVES_TEXT = 0xFF000000;
    private static final int COLOR_GAME_BACKGROUND = 0xFFEBEBEB;
    private static final int COLOR_GAME_TILE = 0xFFADC7B0;
    private static final int COLOR_TILE_TEXT = 0xFF000000;


	private static int mDim; // TODO: get rid of static keyword here
	private static Board mStartBoard; // TODO: get rid of static keyword
    private static Solution mSolution; // TODO: Why this should be static?
	private PuzzleFragment mBoardFrag;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    //private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board);

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        //navMenuIcons = getResources()
        //        .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], 0));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], 0));
        // Photos
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], 0));
        // Communities, Will add a counter here
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], 0));
        // Pages
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], 0));
        // What's hot, We  will add a counter here
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], 0));

        // Recycle the typed array
        //navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_launcher,  //TODO: should be ic_drawer ! nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            //displayView(0);
        }


        mDim = getIntent().getIntExtra(GameMenuActivity.PUZZLE_DIMENTION, 3);
		mStartBoard = (Board) getIntent().getSerializableExtra(GameMenuActivity.PUZZLE);
        mSolution = (Solution) getIntent().getSerializableExtra(GameMenuActivity.SOLUTION);

		mBoardFrag = new PuzzleFragment();
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, mBoardFrag).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_context_menu, menu);
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
		} else if (id == R.id.action_new_game) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // Set title
            alertDialogBuilder.setTitle("Start new game");

            // Set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure? You current game status will be lost...")
                    .setCancelable(false)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,int id) {
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

            return true;
        }

		return super.onOptionsItemSelected(item);
	}
	
	private void exitPuzzle() {
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 
		// Set title
		alertDialogBuilder.setTitle("Exit the game");

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
	 * A placeholder fragment containing main game puzzle board.
	 */
	public static class PuzzleFragment extends Fragment {

		private RelativeLayout mLayout;
		private TileButton[][] mButtons;
		private Board mBoard;
		TextView mMovesTB;
		
		OnSharedPreferenceChangeListener mListener;

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
		private int mTileSpeed = 0;
		
		public int getTileSpeed() {
			
			if (mTileSpeed == 0) {
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				String ret = prefs.getString("tile_speed_list", "Normal");
				setTileSpeed(ret);
				Log.i("Mikhail", "speed = " + ret);
				
			}
			
			return mTileSpeed;
		}

		public void setTileSpeed(String speed) {
			
			if (speed.equals("-1")) {
				mTileSpeed = 180;
			} else if (speed.equals("0")) {
				mTileSpeed = 120;
			} else if (speed.equals("1")) {
				mTileSpeed = 60;
			} else {
				mTileSpeed = 60;	
			}
			
		}

		public PuzzleFragment() {

			N = mDim;
			
			mBoard = mStartBoard;
			
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
		
		@Override
	    public void onAttach(Activity activity) {
	        
			super.onAttach(activity);
	        
	    }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_board,
					container, false);

			mTouchListener = new MultiTouchListener(this, mSolution);
			
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
				mBoardTop = v.getTop() + infoPanelHeight/* + 100*/; // Offset the buttons
				mLayout.setBackgroundColor(COLOR_GAME_BACKGROUND);
				
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
						mButtons[i][j].setTextColor(COLOR_TILE_TEXT);
						mButtons[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
						mButtons[i][j].setBackgroundColor(COLOR_GAME_TILE);
						
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
						} else if (key.equals("tile_speed_list")) {
							
							String speed = sharedPreferences.getString("tile_speed_list", "1");
							setTileSpeed(speed);
							Log.i("Mikhail", "Speed has changed to " + speed);
							
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
				mMovesTB.setText("Moves: 0/" + mBoard.getOptimalSolutionMoves());
				mMovesTB.setTextColor(COLOR_MOVES_TEXT);
				mMovesTB.setBackgroundColor(COLOR_GAME_BACKGROUND);
				mMovesTB.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				mLayout.addView(mMovesTB);
				if (!ret) {
					mMovesTB.setVisibility(View.INVISIBLE);
				}
				
				// Back button
				Button back = new Button(getActivity());
				RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                int backButtonId;
                if(android.os.Build.VERSION.SDK_INT >= 17){
                    backButtonId = View.generateViewId();
                } else {
                    backButtonId = Utils.generateViewId();
                }
                back.setId(backButtonId);
				backParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				backParams.setMargins(15, 30, 15, 0);
				back.setLayoutParams(backParams);
				back.setText("Back");
				back.setBackgroundResource(R.drawable.custom_button);
				back.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mTouchListener.back();
					}
				});
				mLayout.addView(back);

                // Hint button
                Button hint = new Button(getActivity());
                RelativeLayout.LayoutParams hintParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                hintParams.addRule(RelativeLayout.LEFT_OF, backButtonId);
                hintParams.setMargins(0, 30, 15, 0);
                hint.setLayoutParams(hintParams);
                hint.setText("Hint");
                hint.setBackgroundResource(R.drawable.custom_button);
                hint.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        mTouchListener.hint();

                    }
                });
                mLayout.addView(hint);
		        
				isUiInited = true;
			}
			
		}
		
		protected void updateMoves() {
			mMovesTB.setText("Moves: " + mBoard.getMoves() + "/"+ mBoard.getOptimalSolutionMoves());
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
							
							/*
							FragmentManager manager = getActivity().getSupportFragmentManager();
							FragmentTransaction trans = manager.beginTransaction();
							trans.remove(PuzzleFragment.this);
							trans.commit();
							
							mBoardFrag = new PuzzleFragment();
							manager.beginTransaction().add(R.id.container, mBoardFrag).commit();
							
							manager.popBackStack();
							*/
							getActivity().finish();
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