package com.guessthegame;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.TargetApi;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;
import android.support.v4.app.NavUtils;
import com.facebook.*;
import com.facebook.model.*;
import com.github.espiandev.showcaseview.ShowcaseView;
import com.github.espiandev.showcaseview.ShowcaseView.ConfigOptions;
import com.github.espiandev.showcaseview.ShowcaseView.OnShowcaseEventListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Game extends Activity {

	static ImageView gImg;
	static RelativeLayout actions;
	static TextView gHint;
	static EditText gAnswer;
	static String close, answer, sound, hint, img, file = "";
	static int correct;
	long startTime;
	boolean hintGiven = false;
	RelativeLayout statusCover;
	TextView status;
	TextView statusExtra;
	RelativeLayout statusTick;
	Handler handler;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		// Show the Up button in the action bar.

		startTime = System.currentTimeMillis();

		final SharedPreferences.Editor editor = MainActivity.prefs.edit();
		
		statusCover = (RelativeLayout) findViewById(R.id.statusCover);
		status  = (TextView) findViewById(R.id.status);
		statusExtra = (TextView) findViewById(R.id.status_extra);
		statusTick = (RelativeLayout) findViewById(R.id.actionss);
		
		updateHints(false);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			file = extras.getString("FILE");
			img = extras.getString("IMG");
			hint = extras.getString("HINT");
			sound = extras.getString("SOUND");
			answer = extras.getString("ANSWER");
			close = extras.getString("CLOSE");

		}

		if (img != "") {

			gImg = (ImageView) findViewById(R.id.game);
			actions = (RelativeLayout) findViewById(R.id.actions);

			correct = MainActivity.prefs.getInt(img, 0);

			if (correct == 1) {

				actions.setVisibility(View.VISIBLE);
			} else {
				actions.setVisibility(View.INVISIBLE);
			}

			gImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Perform action on key press
					InputMethodManager inputManager = (InputMethodManager) Game.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(Game.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

				}

			});

			gAnswer = (EditText) findViewById(R.id.answer);
			
			String[] ansArr = answer.split(",");
			
			if (correct == 1)
				gAnswer.setText((ansArr.length > 0 ? ansArr[0] : answer));

			gAnswer.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if ((event.getAction() == KeyEvent.ACTION_DOWN)
							&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
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
			
			final Button facebookBtn = (Button) findViewById(R.id.facebookBtn);
			
			facebookBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// start Facebook Login
					
					facebookBtn.setVisibility(View.INVISIBLE);
					
					Session.openActiveSession(Game.this, true, new Session.StatusCallback() {

						// callback when session changes state
						@Override
						public void call(Session session, SessionState state, Exception exception) {
							
							Log.d("SESSION","" + session);
							
							if (session.isOpened()) publishStory();
							
							/*
							if (session.isOpened()) {

								// make request to the /me API
								Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

									// callback after Graph API response with user object
									@Override
									public void onCompleted(GraphUser user, Response response) {

										if (user != null) {
											//TextView welcome = (TextView) findViewById(R.id.welcome);
											//welcome.setText("Hello " + user.getName() + "!");
											
											Log.d("USER","" + user);
											
											//
											
										}
									}
								});
							}
							*/
						}
					});
					
				}

			});
			
			Button checkBtn = (Button) findViewById(R.id.check);

			checkBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					checkAnswer(gAnswer.getText().toString());
				}

			});

			gAnswer.setOnEditorActionListener(new OnEditorActionListener() {

				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
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
			
			Boolean firstTime = MainActivity.prefs.getBoolean("firstTime", true);
			
			if(firstTime == true) {
				ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
				co.hideOnClickOutside = true;
				ShowcaseView sv = ShowcaseView.insertShowcaseView(R.id.hintBtn, this, "Stuck? Use a hint.", "If you get stuck click here to use a hint. You will get a new hint for every 3 correct guesses in a row.", co);
			
				editor.putBoolean("firstTime",false);

				editor.commit();
			}
			
			hintBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int hintCntC = MainActivity.prefs.getInt("hintCnt", 0);
					if (gHint.getVisibility() == View.INVISIBLE && ( hintCntC > 0 || hintGiven == true )) {
						InputMethodManager inputManager = (InputMethodManager) Game.this
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						inputManager.hideSoftInputFromWindow(Game.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
						
						if(hintGiven == false) {
						
							int hintCnts = MainActivity.prefs.getInt("hintCnt", 0);
	
							int hintCntss = (hintCnts - 1);
	
							editor.putInt("hintCnt",
									(hintCntss > 0 ? hintCntss : 0));
	
							editor.commit();
	
							updateHints(true);
							hintGiven = true;
						
						}
						gHint.setVisibility(View.VISIBLE);
						
						
					} else {
						if(gHint.getVisibility() == View.VISIBLE) gHint.setVisibility(View.INVISIBLE);
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
		// getMenuInflater().inflate(R.menu.activity_game, menu);
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

		if (correct == 0) {

			int fuzzy;
			int lowestFuzzy = 100;
			boolean correctAnswer = false;

			for (String answer : answerArr) {

				fuzzy = LevenshteinDistance.computeDistance(text, answer);

				if (fuzzy < lowestFuzzy)
					lowestFuzzy = fuzzy;

				if (text.toLowerCase().equals(answer) || fuzzy < 2) {

					correctAnswer = true;

					status.setText("Correct!");
					
					gAnswer.setText(answer);

					break;
				}

			}

			if (correctAnswer == true) {
				
				gHint.setVisibility(View.INVISIBLE);
				
				long difference = System.currentTimeMillis() - startTime;

				int correctCnt = MainActivity.prefs.getInt(file
						+ "_correct_cnt", 0);

				long totalTime = MainActivity.prefs.getLong("totalTime", 0);
				long totalTimeF = MainActivity.prefs.getLong(file
						+ "_totalTime", 0);

				int inaRow = MainActivity.prefs.getInt("inaRow", 0);

				int hintCnt = MainActivity.prefs.getInt("hintCnt", 0);

				int currentCorrect = (correctCnt + 1);

				int inaRowCnt = (inaRow + 1);
				
				statusExtra.setText(inaRowCnt + " in a row!");
				statusExtra.setTextColor(Color.parseColor("#e2ef65"));
				
				Long newtotalTime = (totalTime + difference);
				Long newtotalTimeF = (totalTimeF + difference);

				editor.putLong("totalTime", newtotalTime);
				editor.putLong(file + "_totalTime", newtotalTimeF);
				editor.putInt(file + "_correct_cnt", currentCorrect);
				editor.putInt("inaRow", inaRowCnt);
				editor.putInt(img, 1);

				editor.commit();

				int timeout = 1000;

				if (inaRowCnt % 3 == 0) {
					
					statusExtra.setText(inaRowCnt + " in a row! you got a hint!");
					
					int hintCnts = (hintCnt + 1);

					editor.putInt("hintCnt", hintCnts);

					editor.commit();

					timeout = 2000;

					updateHints(true);

				}
				
				handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {

						Game.super.onBackPressed();
					}
				}, timeout);

			} else if (lowestFuzzy < 4) {

				status.setText("Nearly!");
				statusExtra.setText("Have another go!");
				statusExtra.setTextColor(Color.GRAY);

				handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {

						statusCover.setVisibility(View.INVISIBLE);
					}
				}, 2000);
				
			} else {

				status.setText("Wrong!");
				statusExtra.setText("Oops, Try again!");
				statusExtra.setTextColor(Color.RED);
				
				gAnswer.setText("");

				Animation shake = AnimationUtils.loadAnimation(this,
						R.anim.shake);
				findViewById(R.id.game).startAnimation(shake);

				editor.putInt("inaRow", 0);

				editor.commit();
				
				if (handler != null) {
					handler.getLooper().quit();
				}
				
				handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {

						statusCover.setVisibility(View.INVISIBLE);
					}
				}, 2000);

			}
			
			statusCover.setVisibility(View.VISIBLE);
		}

	}

	public void updateHints(Boolean animation) {

		int hintCnt = MainActivity.prefs.getInt("hintCnt", 0);

		Log.i("hintCNT", "" + hintCnt);

		TextView hintText = (TextView) Game.this.findViewById(R.id.hints);

		if (animation == true) {
			Animation fadeOut = AnimationUtils.loadAnimation(this,
					R.anim.fadeout);
			hintText.startAnimation(fadeOut);
		}

		hintText.setText("" + hintCnt);

		if (animation == true) {
			Animation fadeIn = AnimationUtils
					.loadAnimation(this, R.anim.fadein);
			hintText.startAnimation(fadeIn);
		}

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	
	}
	
	private void publishStory() {
		Session session = Session.getActiveSession();
		
		Log.d("session","" + session);
		
		if (session != null){

			// Check for publish permissions    
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session
						.NewPermissionsRequest(this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			Bundle postParams = new Bundle();
			postParams.putString("name", "Guess The Game");
			postParams.putString("caption", "The best game guessing game on Android!");
			postParams.putString("description", "Help me guess this game!");
			//postParams.putString("link", "http://www.blucreation.co.uk");
			postParams.putString("picture", "http://blucreation.co.uk/GTG/" + img);

			Request.Callback callback= new Request.Callback() {
				public void onCompleted(Response response) {
					JSONObject graphResponse = response
							.getGraphObject()
							.getInnerJSONObject();
					String postId = null;
					try {
						postId = graphResponse.getString("id");
					} catch (JSONException e) {
						Log.i("DEBUG",
								"JSON error "+ e.getMessage());
					}
					FacebookRequestError error = response.getError();
					if (error != null) {
						Toast.makeText(getBaseContext(),
								error.getErrorMessage(),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getBaseContext(), 
								"Posted to Facebook",
								Toast.LENGTH_LONG).show();
					}
				}
			};

			Request request = new Request(session, "me/feed", postParams, 
					HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
			
		}

	}

	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

}
