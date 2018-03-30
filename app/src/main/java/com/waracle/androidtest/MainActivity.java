package com.waracle.androidtest;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.waracle.androidtest.Models.CakeModel;
import com.waracle.androidtest.Utils.Constants;
import com.waracle.androidtest.Utils.ImageLoader;
import com.waracle.androidtest.Utils.StreamUtils;
import com.waracle.androidtest.ViewHolders.CakeListItemViewHolder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragment is responsible for loading in some JSON and
     * then displaying a list of cakes with images.
     * Fix any crashes
     * Improve any performance issues
     * Use good coding practices to make code more secure
     */
    public static class PlaceholderFragment extends ListFragment {

        private static final String TAG = PlaceholderFragment.class.getSimpleName();

        private ListView mListView;
        private MyAdapter mAdapter;

        public PlaceholderFragment() { /**/ }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mListView = rootView.findViewById(android.R.id.list);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Create and set the list adapter.
            mAdapter = new MyAdapter();
            mListView.setAdapter(mAdapter);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    // Load data from net.
                    try {
                        JSONArray array = loadData();

                        ArrayList<CakeModel> cakeModels = new ArrayList<>();

                        for (int i = 0; i < array.length(); i++) {
                            try {
                                cakeModels.add(new CakeModel(array.getJSONObject(i)));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }

                        mAdapter.setItems(cakeModels);

                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        };
                        mainHandler.post(runnable);

                    } catch (IOException | JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }


        private JSONArray loadData() throws IOException, JSONException {
            URL url = new URL(Constants.contentJSONUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // Also, Do you trust any utils thrown your way????

                byte[] bytes = StreamUtils.readUnknownFully(in);

                // Read in charset of HTTP content.
                String charset = parseCharset(urlConnection.getRequestProperty("Content-Type"));

                // Convert byte array to appropriate encoded string.
                String jsonText = new String(bytes, charset);

                // Read string as JSON.
                return new JSONArray(jsonText);
            } finally {
                urlConnection.disconnect();
            }
        }

        /**
         * Returns the charset specified in the Content-Type of this header,
         * or the HTTP default (ISO-8859-1) if none can be found.
         */
        public static String parseCharset(String contentType) {
            if (contentType != null) {
                String[] params = contentType.split(",");
                for (int i = 1; i < params.length; i++) {
                    String[] pair = params[i].trim().split("=");
                    if (pair.length == 2) {
                        if (pair[0].equals("charset")) {
                            return pair[1];
                        }
                    }
                }
            }
            return "UTF-8";
        }

        private class MyAdapter extends BaseAdapter {

            private ArrayList<CakeModel> mItems;
            private ImageLoader mImageLoader;

            MyAdapter() {
                this(new ArrayList<CakeModel>());
            }

            MyAdapter(ArrayList<CakeModel> items) {
                mItems = items;
                mImageLoader = new ImageLoader();
            }

            @Override
            public int getCount() {
                return mItems.size();
            }

            @Override
            public CakeModel getItem(int position) {

                return mItems.get(position);

            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @SuppressLint("ViewHolder")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                CakeListItemViewHolder cakeListItemViewHolder;

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
                    cakeListItemViewHolder = new CakeListItemViewHolder();
                    cakeListItemViewHolder.setTitleTextView((TextView) convertView.findViewById(R.id.title));
                    cakeListItemViewHolder.setDescriptionTextView((TextView) convertView.findViewById(R.id.desc));
                    cakeListItemViewHolder.setImageView((ImageView) convertView.findViewById(R.id.image));

                    convertView.setTag(cakeListItemViewHolder);
                } else {
                    cakeListItemViewHolder = (CakeListItemViewHolder) convertView.getTag();
                }


                CakeModel cakeModel = getItem(position);

                if(cakeModel != null) {
                    cakeListItemViewHolder.getTitleTextView().setText(cakeModel.getTitle());
                    cakeListItemViewHolder.getDescriptionTextView().setText(cakeModel.getDescription());
                    mImageLoader.load(cakeModel.getImagePath(), cakeListItemViewHolder.getImageView(), cakeModel.getPlaceHolderImage());
                }

                return convertView;
            }

            public void setItems(ArrayList<CakeModel> items) {
                mItems = items;
            }


        }
    }
}

