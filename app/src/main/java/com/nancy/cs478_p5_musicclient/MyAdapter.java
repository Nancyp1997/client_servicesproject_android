package com.nancy.cs478_p5_musicclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    String  songNames[], singers[], songURLs[];
    Bitmap[]  images;
    String bclick;
    Context c;


    public MyAdapter(Context context, String name[], String artist[], Bitmap[] image,String[] url,String click) {
        this.c=context;
        this.songNames=name;
        this.singers=artist;
        this.images=image;
        this.songURLs=url;
        this.bclick=click;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(c);
        View view= inflater.inflate(R.layout.song_list,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songTitleTextView.setText(songNames[position]);
        holder.songArtistTextView.setText(singers[position]);
        holder.songImageImgView.setImageBitmap(images[position]);
    }


    @Override
    public int getItemCount() {
        Log.i("NANCY","d1 is "+songNames.length);
        return songNames.length;
    }


    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView songTitleTextView,songArtistTextView;
        ImageView songImageImgView;
        private View itemView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitleTextView=itemView.findViewById(R.id.textView);
            songArtistTextView=itemView.findViewById(R.id.artist);
            songImageImgView=itemView.findViewById(R.id.imageView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
        }

        //onclick of the list item in RV the song is played
        @Override
        public void onClick(View v) {
            SecondActivity.pauseButton.setEnabled(true);
            try {
                if (MainActivity.mIsBound && MainActivity.serviceState) {
                    if (bclick.equals("getOneSong")) {
                        Log.i("music", "music"+String.valueOf(MainActivity.singleSongID));
                        Bundle bundle = MainActivity.musicaidlobj.getSingleSong(MainActivity.updatedSongID);
                        String songurl = bundle.getString("SONGURL");
                        MainActivity.PlaySong(songurl);
                        Log.i("NANCY", "Song is playing");
                    } else {
                        Bundle bundle = MainActivity.musicaidlobj.getSingleSong(getAdapterPosition());
                        String songurl = bundle.getString("SONGURL");
                        MainActivity.PlaySong(songurl);
                        SecondActivity.pauseButton.setEnabled(true);
                        SecondActivity.resumeButton.setEnabled(false);
                        Log.i("NANCY", "Song is playing");
                    }
                    } else{
                    Log.i("NANCY", "SERVICE NOT BOUND");
                }
            } catch (RemoteException | IOException e) {
                Log.e("NANCY", e.toString());
            }
        }
    }
}