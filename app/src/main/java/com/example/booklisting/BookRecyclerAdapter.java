package com.example.booklisting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BookRecyclerAdapter extends RecyclerView.Adapter<BookRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private ItemClickListener itemClickListener;

        TextView titleTextView, authorTextView, dateTextView;
        ImageView bookImageView;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.tv_title);
            authorTextView = itemView.findViewById(R.id.tv_author);
            dateTextView = itemView.findViewById(R.id.tv_date);
            bookImageView = itemView.findViewById(R.id.img_thump);
            ratingBar = itemView.findViewById(R.id.rb_rating);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    Context context;
    ArrayList<Book>books;

    public BookRecyclerAdapter(Context context,  ArrayList<Book> books){
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public BookRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookRecyclerAdapter.ViewHolder viewHolder, int i) {

        Book currentBook = books.get(i);

        viewHolder.titleTextView.setText(currentBook.getTitle());
        viewHolder.authorTextView.setText(currentBook.getAuthor());
        viewHolder.dateTextView.setText(currentBook.getDate());
        viewHolder.ratingBar.setRating(currentBook.getAverageRating().floatValue());

        new DownloadImageTask(viewHolder.bookImageView).execute(currentBook.getImageUrl());

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                Book book = books.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String link = book.getBuyUrl();
                if(link != ""){
                    intent.setData(Uri.parse(link));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return books.size();
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
