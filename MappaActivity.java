package com.developer.marcocicala.centrocampania;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import uk.co.senab.photoview.PhotoViewAttacher;

public class MappaActivity extends AppCompatActivity {

    PhotoViewAttacher photoViewAttacher;
    private boolean isVisible;
    FrameLayout frame, framepp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Piano Terra");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cosa succede se clicco sul fab button
                if (isVisible){
                    showMap(R.drawable.mappabeta5, frame);
                    setTitle("Piano Terra");
                    isVisible = false;
                } else {
                    showMap(R.drawable.mappapp, framepp);
                    setTitle("Primo Piano");
                    isVisible = true;
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        frame = (FrameLayout) findViewById(R.id.frame);
        frame.getBackground().setAlpha(128);
        framepp = (FrameLayout) findViewById(R.id.framepp);
        framepp.getBackground().setAlpha(128);

        showMap(R.drawable.mappabeta5, frame);
        isVisible = false;

        frame.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                frame.setVisibility(View.INVISIBLE);
                return true;
            }
        });
        framepp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                framepp.setVisibility(View.INVISIBLE);
                return true;
            }
        });

        Toast.makeText(getApplicationContext(), "Tieni premuto sulla mappa per mostrare la legenda!", Toast.LENGTH_LONG).show();


    }

    public void showFrame(final FrameLayout frame){
        frame.setVisibility(View.VISIBLE);
        frame.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                frame.setVisibility(View.INVISIBLE);
                return  true;
            }
        });
    }

    private void showMap(int id, final FrameLayout frame){
        Drawable drawable = getResources().getDrawable(id);
        ImageView imageView = (ImageView) findViewById(R.id.img);
        imageView.setImageDrawable(drawable);
        photoViewAttacher = new PhotoViewAttacher(imageView);
        photoViewAttacher.update();

        photoViewAttacher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                frame.setVisibility(View.VISIBLE);
                return true;
            }
        });
    }

}
