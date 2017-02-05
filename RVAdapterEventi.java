package com.developer.marcocicala.centrocampania;

/**
 * Created by utente on 10/06/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RVAdapterEventi extends RecyclerView.Adapter<RVAdapterEventi.PersonViewHolder>{

    //List<Person> persons;
    ArrayList<HashMap<String,String>> hashMaps;
    Context mContext;

    RVAdapterEventi(ArrayList<HashMap<String,String>> hashMaps, Context mContext){
        this.hashMaps = hashMaps;
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return hashMaps.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_eventi, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.eventiTitolo.setText(hashMaps.get(i).get("titolo"));
        personViewHolder.eventiDescrizione.setText(hashMaps.get(i).get("descrizione"));
        personViewHolder.eventiLink.setText(hashMaps.get(i).get("link"));
        //new DownloadImageTask(personViewHolder.eventiImage).execute(hashMaps.get(i).get("image"));
        //personViewHolder.negozioLogo.setImageResource(android.R.drawable.ic_delete);

        Glide.with(mContext).load(hashMaps.get(i).get("image"))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(personViewHolder.eventiImage);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView eventiLink;
        TextView eventiTitolo;
        TextView eventiDescrizione;
        ImageView eventiImage;
        CoordinatorLayout coordinatorLayout;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.card_eventi);
            eventiLink = (TextView) itemView.findViewById(R.id.eventi_link);
            eventiTitolo = (TextView) itemView.findViewById(R.id.eventi_titolo);
            eventiDescrizione = (TextView) itemView.findViewById(R.id.eventi_descrizione);
            eventiImage = (ImageView) itemView.findViewById(R.id.eventi_image);

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

