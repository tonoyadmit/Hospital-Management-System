package com.admit.hospitalapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import in.digitechlab.hospitalapp.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HistoryAdmissionsActivity extends ListActivity {
	
	private Button back;

	// Progress Dialog
	private ProgressDialog pDialog;

	// php read comments script

	// localhost :
	// testing on your device
	// put your local ip instead, on windows, run CMD > ipconfig
	// or in mac's terminal type ifconfig and look for the ip under en0 or en1
	// private static final String READ_COMMENTS_URL =
	// "http://xxx.xxx.x.x:1234/webservice/comments.php";

	// testing on Emulator:
	private static final String ALL_PATIENTS_URL = "http://www.digitechlab.in/hospitallogin/historyadmissions.php";

	// testing from a real server:
	// private static final String READ_COMMENTS_URL =
	// "http://www.mybringback.com/webservice/comments.php";

	// JSON IDS:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_POSTS = "posts";
	private static final String TAG_POST_ID = "post_id";
	private static final String TAG_PATIENT_NAME = "patient_name";
	private static final String TAG_PATIENT_ID = "patient_id";
	public static final String TAG_ADMISSION_ID = "admission_id";
	// it's important to note that the message is both in the parent branch of
	// our JSON tree that displays a "Post Available" or a "No Post Available"
	// message,
	// and there is also a message for each individual post, listed under the
	// "posts"
	// category, that displays what the user typed as their message.

	// An array of all of our comments
	private JSONArray mComments = null;
	// manages all of our comments in a list.
	private ArrayList<HashMap<String, String>> mCommentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// note that use read_comments.xml instead of our single_post.xml
		setContentView(R.layout.activity_all_admissions);
		

//        back = (Button) findViewById(R.id.back);
//                      
//            back.setOnClickListener(new View.OnClickListener()
//            {
//              public void onClick(View paramAnonymousView)
//              {
//            		
//                 	Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
//  					startActivity(intent);
//              }
//            });   
            
		
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// loading the comments via AsyncTask
		new LoadComments().execute();
	}

	public void addComment(View v) {
		Intent i = new Intent(HistoryAdmissionsActivity.this, CopyAdminActivity.class);
		startActivity(i);
	}

	/**
	 * Retrieves recent post data from the server.
	 */
	public void updateJSONdata() {

		// Instantiate the arraylist to contain all the JSON data.
		// we are going to use a bunch of key-value pairs, referring
		// to the json element name, and the content, for example,
		// message it the tag, and "I'm awesome" as the content..

		mCommentList = new ArrayList<HashMap<String, String>>();

		// Bro, it's time to power up the J parser
		JSONParser jParser = new JSONParser();
		// Feed the beast our comments url, and it spits us
		// back a JSON object. Boo-yeah Jerome.
		JSONObject json = jParser.getJSONFromUrl(ALL_PATIENTS_URL);

		// when parsing JSON stuff, we should probably
		// try to catch any exceptions:
		try {

			// I know I said we would check if "Posts were Avail." (success==1)
			// before we tried to read the individual posts, but I lied...
			// mComments will tell us how many "posts" or comments are
			// available
			mComments = json.getJSONArray(TAG_POSTS);

			// looping through all posts according to the json object returned
			for (int i = 0; i < mComments.length(); i++) {
				JSONObject c = mComments.getJSONObject(i);

				// gets the content of each tag
				String patient_id = c.getString(TAG_PATIENT_ID);
				String patient_name = c.getString(TAG_PATIENT_NAME);
				String admission_id = c.getString(TAG_ADMISSION_ID);

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				map.put(TAG_PATIENT_ID, patient_id);
				map.put(TAG_PATIENT_NAME, patient_name);
				map.put(TAG_ADMISSION_ID, admission_id);

				// adding HashList to ArrayList
				mCommentList.add(map);

				// annndddd, our JSON data is up to date same with our array
				// list
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts the parsed data into the listview.
	 */
	private void updateList() {
		// For a ListActivity we need to set the List Adapter, and in order to do
		//that, we need to create a ListAdapter.  This SimpleAdapter,
		//will utilize our updated Hashmapped ArrayList, 
		//use our single_post xml template for each item in our list,
		//and place the appropriate info from the list to the
		//correct GUI id.  Order is important here.
		ListAdapter adapter = new SimpleAdapter(this, mCommentList,
				R.layout.admission_single_post, new String[] {TAG_PATIENT_NAME ,
				TAG_PATIENT_ID, TAG_ADMISSION_ID }, new int[] {R.id.ad_patient_name,R.id.ad_patient_id, R.id.ad_admission_id
				});

		// I shouldn't have to comment on this one:
		setListAdapter(adapter);
		
		// Optional: when the user clicks a list item we 
		//could do something.  However, we will choose
		//to do nothing...
		ListView lv = getListView();	
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				
			    TextView c = (TextView) view.findViewById(R.id.ad_admission_id);
			    String aid = c.getText().toString();
				
			    
            	Intent i = new Intent ("com.admit.hospitalapp.SINGLEADMISSIONACTIVITY");
            	i.putExtra(TAG_ADMISSION_ID, aid);
    			startActivity(i);


			}
		});
	}

	
    
//    myListView.setOnItemClickListener(new OnItemClickListener(){
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
//					long arg3) {
//				value =	(String)BTArrayAdapter.getItem(pos);
//				if(value.equals("HC-05")){
//				Intent intent = new Intent(getApplicationContext(), Control.class);
//				intent.putExtra(KEY_DEVICE, value);
//				startActivity(intent); 
//				}else{
//			         Toast.makeText(getApplicationContext(), "WRONG SELECTION!" + "\n" + "Try Again!!" ,
//		        		 Toast.LENGTH_SHORT).show();	
//				}
//			}
//	    	  
//	      });
//	
//	
	
	public class LoadComments extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(HistoryAdmissionsActivity.this);
			pDialog.setMessage("Loading Admissions...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			updateJSONdata();
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			updateList();
		}
	}
}
