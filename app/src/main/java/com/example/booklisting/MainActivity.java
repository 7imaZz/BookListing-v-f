package com.example.booklisting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ProgressBar loadingBar;
    EditText searchEditText;
    RecyclerView recyclerView;
    String URL_REQ;
    TextView emptyTextView;
    int c = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.ed_search);
        loadingBar = findViewById(R.id.pb_loading);
        recyclerView = findViewById(R.id.rv_books);
        emptyTextView = findViewById(R.id.tv_empty);

        loadingBar.setVisibility(View.GONE);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(networkInfo != null && networkInfo.isConnected()){
                    if (actionId == EditorInfo.IME_ACTION_SEARCH){
                        URL_REQ = "https://www.googleapis.com/books/v1/volumes?q="+searchEditText.getText().toString();
                        new BooksAsyncTask().execute(URL_REQ);
                        return true;
                    }}
                    else {
                        emptyTextView.setText("No Network Connection");
                    }
                    return false;
                }
            });

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    hideKeyboard(v);
            }
        });

    }

    public class BooksAsyncTask extends AsyncTask<String, ArrayList<Book>, String>{

        @Override
        protected void onPreExecute() {
            loadingBar.setVisibility(View.VISIBLE);
            searchEditText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            ArrayList<Book> books;
            String text;
            try {
                URL url = new URL(strings[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                text = stream2String(in);

                books = extractBooksFromJson(text);

                publishProgress(books);

                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(ArrayList<Book>... values) {

            ArrayList<Book> books = values[0];

            final BookRecyclerAdapter adapter = new BookRecyclerAdapter(getApplicationContext(), books);

            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            recyclerView.setAdapter(adapter);

            int count = 0;
            if (recyclerView.getAdapter() != null) {
                count = recyclerView.getAdapter().getItemCount();
            }

            if (count == 0){
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText(searchEditText.getText().toString()+" Not Found!");
            }
            else {
                emptyTextView.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            searchEditText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            loadingBar.setVisibility(View.GONE);
        }
    }

    public String stream2String(InputStream inputStream){

        String line;
        String text = "";

        BufferedReader reader =  new BufferedReader(new InputStreamReader(inputStream));

        try{
            while((line = reader.readLine()) != null){
                text += line;
            }
        }catch (IOException e){}

        return text;
    }



    public ArrayList<Book> extractBooksFromJson(String json){

        ArrayList<Book> books = new ArrayList<>();

        try {

            JSONObject root = new JSONObject(json);
            JSONArray items = root.getJSONArray("items");

            for (int i=0; i<items.length(); i++){

                JSONObject current = items.getJSONObject(i);

                JSONObject volumeInfo = current.getJSONObject("volumeInfo");

                String title = volumeInfo.getString("title");

                String date = volumeInfo.getString("publishedDate");

                JSONArray authorsArray = volumeInfo.getJSONArray("authors");

                String authors = "";

                for (int j=0; j<authorsArray.length(); j++){
                    if(j!=authorsArray.length()-1)
                        authors += authorsArray.getString(j)+", ";
                    else
                        authors += authorsArray.getString(j);
                }

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String thumbnail = imageLinks.getString("thumbnail");

                Double averageRating;
                if(volumeInfo.toString().contains("averageRating")) {
                    averageRating = volumeInfo.getDouble("averageRating");
                } else {
                    averageRating = 0.0;
                }

                JSONObject saleInfo = current.getJSONObject("saleInfo");
                String buyLink;
                if(saleInfo.toString().contains("buyLink")) {
                    buyLink = saleInfo.getString("buyLink");
                }
                else {
                    buyLink = "";
                }
                books.add(new Book(title, authors, date, thumbnail, averageRating, buyLink));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return books;

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
