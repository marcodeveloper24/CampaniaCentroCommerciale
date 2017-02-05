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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.filter;

public class RVAdapterNegozi extends RecyclerView.Adapter<RVAdapterNegozi.PersonViewHolder>{

    //List<Person> persons;
    ArrayList<HashMap<String,String>> hashMaps;
    Context mContext;

    RVAdapterNegozi(ArrayList<HashMap<String,String>> hashMaps, Context mContext){
        this.hashMaps = hashMaps;
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return hashMaps.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_negozi, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.textNomeNegozio.setText(hashMaps.get(i).get("nome"));
        personViewHolder.textLinkNegozio.setText(hashMaps.get(i).get("link"));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeNegozio;
        TextView textLinkNegozio;
        CoordinatorLayout coordinatorLayout;

        PersonViewHolder(View itemView) {
            super(itemView);
            textNomeNegozio = (TextView) itemView.findViewById(R.id.text_nome_negozio);
            textLinkNegozio = (TextView) itemView.findViewById(R.id.text_link_negozio);

        }
    }


}

