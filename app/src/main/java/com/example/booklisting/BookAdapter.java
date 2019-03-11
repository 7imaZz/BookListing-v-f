package com.example.booklisting;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;


public class BookAdapter extends ArrayAdapter <Book>{

    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View currentView = convertView;

        if(currentView == null){
            currentView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }


        Book currentBook = getItem(position);

        TextView title = currentView.findViewById(R.id.tv_title);
        title.setText(currentBook.getTitle());

        TextView author = currentView.findViewById(R.id.tv_author);
        author.setText(currentBook.getAuthor());

        TextView date = currentView.findViewById(R.id.tv_date);
        date.setText(currentBook.getDate());

        RatingBar ratingBar = currentView.findViewById(R.id.rb_rating);
        ratingBar.setRating(currentBook.getAverageRating().floatValue());


        ImageView imageView = currentView.findViewById(R.id.img_thump);
        new DownloadImageTask(imageView).execute(currentBook.getImageUrl());

        return currentView;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageTask(ImageView bmImage) {
            imageView = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}