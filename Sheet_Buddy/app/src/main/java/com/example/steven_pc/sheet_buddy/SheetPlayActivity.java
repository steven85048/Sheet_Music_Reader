package com.example.steven_pc.sheet_buddy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Arrays;

public class SheetPlayActivity extends AppCompatActivity {
    private final int IMAGEVIEWID = R.id.imageTest;

    double[][] locations;
    Bitmap pdfImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_play);
        getIntentData();
    }

    private Bitmap[] parseBitmap(){
        // first, convert locations into a array with actual array locations, not numbers
        for (int i = 0 ; i < locations.length; i++){

        }

        return null;
    }

    // gets relevant data from the passed intent
    private void getIntentData(){
        Intent currIntent = getIntent();

        // get location data
        Bundle bundle = currIntent.getExtras();
        locations = (double[][]) bundle.getSerializable("location_data");

        // get bitmap data
        pdfImage = (Bitmap) currIntent.getParcelableExtra("image");

        ImageView iv = (ImageView) findViewById(IMAGEVIEWID);
        iv.setImageBitmap(pdfImage);

        Log.e("testing data", Arrays.deepToString(locations));
    }

}
