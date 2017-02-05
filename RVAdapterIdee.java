package com.developer.marcocicala.centrocampania;

/**
 * Created by utente on 10/06/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class RVAdapterIdee extends RecyclerView.Adapter<RVAdapterIdee.PersonViewHolder>{

    //List<Person> persons;
    ArrayList<HashMap<String,String>> hashMaps;
    Context mContext;

    RVAdapterIdee(ArrayList<HashMap<String,String>> hashMaps, Context mContext){
        this.hashMaps = hashMaps;
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return hashMaps.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_idee, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.ideeTitolo.setText(hashMaps.get(i).get("titolo"));
        personViewHolder.ideeSpan.setText(hashMaps.get(i).get("span"));
        personViewHolder.ideeLink.setText(hashMaps.get(i).get("link"));
        //new DownloadImageTask(personViewHolder.eventiImage).execute(hashMaps.get(i).get("image"));
        //personViewHolder.negozioLogo.setImageResource(android.R.drawable.ic_delete);

        Glide.with(mContext).load(hashMaps.get(i).get("image"))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(personViewHolder.ideeImage);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView ideeLink;
        TextView ideeTitolo;
        TextView ideeSpan;
        ImageView ideeImage;
        CoordinatorLayout coordinatorLayout;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.card_idee);
            ideeLink = (TextView) itemView.findViewById(R.id.idee_link);
            ideeTitolo = (TextView) itemView.findViewById(R.id.idee_titolo);
            ideeSpan = (TextView) itemView.findViewById(R.id.idee_span);
            ideeImage = (ImageView) itemView.findViewById(R.id.idee_image);

        }
    }


    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
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

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            bmImage.setImageBitmap(result);
        }
    }


}

