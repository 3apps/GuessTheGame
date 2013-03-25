package com.guessthegame;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class Game extends Activity {
	
	static ImageView gImg;
	static RelativeLayout actions;
	static TextView gHint;
	static EditText gAnswer;
	static String close, answer, sound, hint, img, file = "";
	static int correct;	
	long startTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		// Show the Up button in the action bar.
		
		startTime = System.currentTimeMillis();
		
		final SharedPreferences.Editor editor = MainActivity.prefs.edit();
		
		updateHints();
		
		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			file 	= extras.getString("FILE");
			img 	= extras.getString("IMG");
			hint	= extras.getString("HINT");
			sound	= extras.getString("SOUND");
			answer	= extras.getString("ANSWER");
			close	= extras.getString("CLOSE");
			
		}
		
		if(img != "") {
			
			gImg = (ImageView) findViewById(R.id.game);
			actions = (RelativeLayout) findViewById(R.id.actions);
			
			correct = MainActivity.prefs.getInt(img, 0);
			
			if(correct == 1) {
				
				actions.setVisibility(View.VISIBLE);
			} else {
				actions.setVisibility(View.INVISIBLE);
			}
			
			gAnswer = (EditText) findViewById(R.id.answer);
			
			if(correct == 1) gAnswer.setText(answer);
			
			gAnswer.setOnKeyListener(new OnKeyListener() {
			    public boolean onKey(View v, int keyCode, KeyEvent event) {		        
			        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
			              // Perform action on key press			              
			              
			              checkAnswer(gAnswer.getText().toString());
			              
			              return true;
			            }
			        
			        return false;
			    }
			});
			
			gHint = (TextView) findViewById(R.id.hint);
			
			gHint.setText(hint);
			
			new loadImage(this, gImg, "images/" + img).execute();
			
			Button checkBtn = (Button) findViewById(R.id.check);
			
			checkBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					checkAnswer(gAnswer.getText().toString());
				}
				
			});
			
			gAnswer.setOnEditorActionListener(new OnEditorActionListener() {

				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_GO) {
						// your additional processing... 
						checkAnswer(gAnswer.getText().toString());
						return true;
					} else {
						return false;
					}
				}

			});
			
			Button hintBtn = (Button) findViewById(R.id.hintBtn);
			
			hintBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int hintCntC = MainActivity.prefs.getInt("hintCnt",0);
					if(gHint.getVisibility() == View.INVISIBLE && hintCntC > 0) {
						InputMethodManager inputManager = 
						        (InputMethodManager) Game.this.
						            getSystemService(Context.INPUT_METHOD_SERVICE); 
						inputManager.hideSoftInputFromWindow(
						        Game.this.getCurrentFocus().getWindowToken(),
						        InputMethodManager.HIDE_NOT_ALWAYS); 
						
						int hintCnts = MainActivity.prefs.getInt("hintCnt",0);
						
						int hintCntss = (hintCnts-1);
						
						editor.putInt("hintCnt", (hintCntss > 0 ? hintCntss : 0 ));
						
						editor.commit();
						
						updateHints();
						
						gHint.setVisibility(View.VISIBLE);
					}
				}
				
			});

		}
	
		
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void checkAnswer(String text) {
		
		SharedPreferences.Editor editor = MainActivity.prefs.edit();
		
		String[] answerArr = answer.split(",");
		
		if(correct == 0) {
			
			int fuzzy;
			int lowestFuzzy = 100;
			boolean correctAnswer = false;
			
			for (String answer : answerArr) {
				
				fuzzy = LevenshteinDistance.computeDistance(text, answer);
				
				if(fuzzy < lowestFuzzy) lowestFuzzy = fuzzy;
				
				if(text.toLowerCase().equals(answer) || fuzzy < 2) {
					
					correctAnswer = true;
					
					if(fuzzy > 0) {
						Toast.makeText(Game.this, "Ok we will let you have that one!", Toast.LENGTH_LONG).show();
						
					}
					
					gAnswer.setText(answer);
					
					break;
				}
				
			}
			
			if(correctAnswer == true) {
								
				long difference = System.currentTimeMillis() - startTime;
				
				int correctCnt = MainActivity.prefs.getInt(file+"_correct_cnt", 0);
				
				long totalTime = MainActivity.prefs.getLong("totalTime", 0);
				long totalTimeF = MainActivity.prefs.getLong(file+"_totalTime", 0);
				
				int inaRow = MainActivity.prefs.getInt("inaRow",0);
				
				int hintCnt = MainActivity.prefs.getInt("hintCnt",0);
				
				int currentCorrect = (correctCnt+1);
				
				int inaRowCnt = (inaRow+1);
				
				Long newtotalTime = (totalTime + difference);
				Long newtotalTimeF= (totalTimeF + difference);

				editor.putLong("totalTime", newtotalTime);
				editor.putLong(file+"_totalTime", newtotalTimeF);
				editor.putInt(file+"_correct_cnt", currentCorrect);
				editor.putInt("inaRow", inaRowCnt);
				editor.putInt(img, 1);
				
				actions.setVisibility(View.VISIBLE);
				
				if(inaRowCnt%3 == 0) {
				
					Toast.makeText(Game.this, inaRowCnt + " in a row! Free Hint!", Toast.LENGTH_LONG).show();
					
					int hintCnts = (hintCnt+1);
					
					editor.putInt("hintCnt", hintCnts);
					
				}
				
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
				    public void run() {
				    	
				    	Game.super.onBackPressed();
				    }
				}, 1000);
				
			} else if(lowestFuzzy < 4) {
				
				Toast.makeText(Game.this, "Soo close, try again!", Toast.LENGTH_LONG).show();
				
			} else {
				Toast.makeText(Game.this, "Wrong, try again!", Toast.LENGTH_LONG).show();
				gAnswer.setText("");
				
				editor.putInt("inaRow", 0);
				
			}
		}
		
		editor.commit();
		
		updateHints();
		
	}
	
	public void updateHints() {
		
		TextView hintText =  (TextView) this.findViewById(R.id.hints);
		
		int hintCnt = MainActivity.prefs.getInt("hintCnt",0);
		
		hintText.setText("" + hintCnt);
		
	}

}
