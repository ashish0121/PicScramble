package com.example.ashishrmehta.flickr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashishrmehta.flickr.R;
import com.example.ashishrmehta.json.FlickrClass;
import com.example.ashishrmehta.json.Photo;
import com.google.gson.Gson;

@SuppressLint("NewApi")
public class FlickrImageSearchActivity extends ActionBarActivity {
	private final static int GRID_MARGIN = 10;
	private final static int GRID_HORIZONTALSPACING = 10;
	private final static int GRID_VERTICALSPACING = 10;
	private static final String FLICK_URL = "\n" +
			"https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=b3380a67070b4cb848414a17c9b58433&format=json&extras=url_n";
	private GridView gvImageList;
	protected TextView mTextView;
	protected TextView mFinishView;
	private ImageView mImageView;
	private Button mButton;
	private FlickrImageSearchAdapter mAdapter;
	private List<Photo> mFlickrSearchResults;
	private SearchAsyncTask mSearchTask;
	private int previousTotal = 0;
	private boolean loading = true;
	private int visibleThreshold = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		findViewById();
		initData(savedInstanceState);
		callNew();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	protected int getLayoutId() {

		return R.layout.activity_flickr_image_search;
	}

	protected void findViewById() {

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int margin = GRID_MARGIN;
		int horizontalSpacing = GRID_HORIZONTALSPACING;
		int verticalSpacing = GRID_VERTICALSPACING;
		int gridWidth = (width - margin * 2 - horizontalSpacing * 2) / 3;

		mTextView = (TextView) findViewById(R.id.search_view);
		gvImageList = (GridView) findViewById(R.id.image_list_gridview);
		mButton = (Button) findViewById(R.id.buttonRestart);

		gvImageList.setHorizontalSpacing(horizontalSpacing);
		gvImageList.setVerticalSpacing(verticalSpacing);
		gvImageList.setPadding(margin, margin, margin, margin);

		mAdapter = new FlickrImageSearchAdapter(this, gridWidth);
		mAdapter.setTextView(mTextView);
		mAdapter.setImageView(mImageView);
		mAdapter.setButtonView(mButton);

		gvImageList.setAdapter(mAdapter);

		gvImageList.setOnScrollListener(new AbsListView.OnScrollListener(){
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				if (loading) {
					if (totalItemCount > previousTotal) {
						loading = false;
						previousTotal = totalItemCount;
					}
				}
				if (!loading
						&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
					// End has been reached
					Log.i("Count", String.valueOf(firstVisibleItem) + " + "
							+ String.valueOf(visibleItemCount) + " >= "
							+ String.valueOf(totalItemCount));
					callNext();
					loading = true;
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState){

			}
		});

	}

	public void restartGame(View v) {
		mTextView.setVisibility(View.VISIBLE);
		mFinishView.setVisibility(View.INVISIBLE);
		mImageView.setVisibility(View.INVISIBLE);
		mButton.setVisibility(View.INVISIBLE);
		callNew();
	}

	protected void initData(Bundle savedInstanceState) {

		mFlickrSearchResults = new ArrayList<Photo>();

		gvImageList.setOnItemClickListener(new OnItemClickListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (isConnected() && mAdapter.getCount() > position) {
				}
				else {
					Toast.makeText(FlickrImageSearchActivity.this, R.string.internet_unavailable, Toast.LENGTH_SHORT);
				}
			}
		});
		callNew();
	}
	
	protected void callRepeated() {
		mAdapter.setData(mFlickrSearchResults);
		mAdapter.notifyDataSetChanged();
	}
	
	protected void callNew() {
		mFlickrSearchResults.clear();
		mAdapter.setData(mFlickrSearchResults);
		mAdapter.notifyDataSetChanged();
		mAdapter.resetAdapter();
		mSearchTask = new SearchAsyncTask("");
		mSearchTask.execute();
	}

	protected void callNext() {
		mSearchTask = new SearchAsyncTask("");
		mSearchTask.execute();
	}


	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//callRepeated();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
//		callNew();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public class SearchAsyncTask extends AsyncTask<Void, Void, Boolean> {
		private final static String USER_AGENT = "Mozilla/5.0";
		private String mSearchStr;
		private ProgressDialog dialog;

		public SearchAsyncTask(String searchStr) {
			mSearchStr = searchStr;
		}
		
		private String sendGet(String url) throws Exception {

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			dialog = new ProgressDialog(FlickrImageSearchActivity.this);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCancelable(false);
			dialog.show();
			dialog.setContentView(R.layout.layout_progress_bar);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String res1 = null;

				res1 = sendGet(FLICK_URL);

				Gson gson = new Gson();
				res1 = res1.substring(14, res1.length()-1);
				Log.d("JSON" , res1);
				FlickrClass flc = gson.fromJson(res1, FlickrClass.class);

				mFlickrSearchResults = flc.getPhotos().getPhoto().subList(0, 99);
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}

		@SuppressLint({ "NewApi", "ShowToast" })
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mAdapter.setData(mFlickrSearchResults);
			Log.d("JSON", "Set Data called from async task");
			mAdapter.notifyDataSetChanged();
			if (dialog != null)
				dialog.dismiss();

			if (!result) {
				Toast.makeText(FlickrImageSearchActivity.this, R.string.scan_loadfail, Toast.LENGTH_SHORT);
				return;
			}
		}
	}

	private boolean isConnected() {
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mSearchTask != null && !mSearchTask.isCancelled()) {
			mSearchTask.cancel(true);
		}
	}
}
