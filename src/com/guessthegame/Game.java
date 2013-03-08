package com.guessthegame;

import java.io.IOException;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.method.KeyListener;

public class Game extends Activity {
	
	static ImageView gImg;
	static TextView gHint;
	static EditText gAnswer;
	static String close, answer, sound, hint, img, file = "";
	static int correct;	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		

		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			file 	= extras.getString("FILE");
			img 	= extras.getString("IMG");
			hint	= extras.getString("HINT");
			sound	= extras.getString("SOUND");
			answer	= extras.getString("ANSWER");
			close	= extras.getString("CLOSE");
			
			Log.i("FILE",""+file);
			
		}
		
		if(img != "") {
			
			gImg = (ImageView) findViewById(R.id.game);
			
			correct = MainActivity.prefs.getInt(img, 0);
			
			if(correct == 1) gImg.setBackgroundColor(Color.GREEN);
			
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
		
		if(correct == 0) {
		
			SharedPreferences.Editor editor = MainActivity.prefs.edit();
			
			if(text.toLowerCase().equals(answer)) {
				
				int correctCnt = MainActivity.prefs.getInt(file+"_correct_cnt", 0);
				
				int currentCorrect = (correctCnt+1);
								
				editor.putInt(file+"_correct_cnt", currentCorrect);
				editor.putInt(img, 1);
				editor.commit();
				
				Log.i("correct_cnt","" + MainActivity.prefs.getInt(file+"_correct_cnt", 0));
				
				Toast.makeText(Game.this, "CORRECT!", Toast.LENGTH_LONG).show();
				gImg.setBackgroundColor(Color.GREEN);
				
			} else if(close.toLowerCase().contains(text)) {
				
				Toast.makeText(Game.this, "Soo close, try again!", Toast.LENGTH_LONG).show();
				
			} else {
				Toast.makeText(Game.this, "Wrong, try again!", Toast.LENGTH_LONG).show();
				gAnswer.setText("");
			}
		}
		
	}

}
