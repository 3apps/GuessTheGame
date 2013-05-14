package com.guessthegame;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.model.*;

public class FacebookPost extends Activity {

	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	String img;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_post);
		
		Bundle extras = getIntent().getExtras();

		if (extras != null) {			
			img = extras.getString("IMG");

		}

		
		/*CODE TO GET APP HASH
	    try {
	        PackageInfo info = getPackageManager().getPackageInfo(
	                "com.guessthegame", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {
	
	    } catch (NoSuchAlgorithmException e) {
	
	    }
		 */

		// start Facebook Login
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {

				if (session.isOpened()) {

					// make request to the /me API
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

						// callback after Graph API response with user object
						@Override
						public void onCompleted(GraphUser user, Response response) {

							if (user != null) {
								//TextView welcome = (TextView) findViewById(R.id.welcome);
								//welcome.setText("Hello " + user.getName() + "!");
								
								publishStory();
							}
						}
					});
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	private void publishStory() {
		Session session = Session.getActiveSession();

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
			
			FacebookPost.super.onBackPressed();
			
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