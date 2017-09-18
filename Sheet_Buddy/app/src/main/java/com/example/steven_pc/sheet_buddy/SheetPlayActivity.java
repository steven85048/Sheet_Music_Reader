package com.example.steven_pc.sheet_buddy;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;

public class SheetPlayActivity extends AppCompatActivity {
    boolean scrollingLocked = false;

    // Components from XML
    private final int MAIN_CONTAINER_ID = R.id.content_sheet_main_container;
    private final int SCROLLVIEW_ID = R.id.scroll_container;

    LinearLayout mainLayout;
    ScrollView scrollContainer;

    // Global variables for resizing
    View currScaled;
    boolean isScaled = false;
    int[] originalSize;
    float previousScale = (float) 1.0;

    private final int SPAN_SLOP = 25;

    // Other constants
    private final int IMAGE_PADDING = 2;

    // data from CreateActivity.java
    double[][] locations;
    Bitmap pdfImage;

    // other important components
    private ScaleGestureDetector scaleDetector;

    // =================== ACTIVITY EVENTS =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_play);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // get data from previous activity
        getIntentData();

        // get all the elements here
        mainLayout = (LinearLayout) findViewById(MAIN_CONTAINER_ID);
        scrollContainer = (ScrollView) findViewById(SCROLLVIEW_ID);

        initializeScaleDetector();

        // use intent data to create list of bitmaps and add to imageviews
        Bitmap[] bitmaps = parseBitmap();
        for (int i = 0 ; i < bitmaps.length; i++) {
            ImageView newView = createImageView(bitmaps[i]);
            addResizeListener(newView);
            mainLayout.addView(newView);
        }

    }

    // ================= METHODS FOR RESIZING THE IMAGEVIEWS =======================================

    private void addResizeListener(ImageView iv){
        iv.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                ImageView currview = (ImageView) v;
                int hc = currview.hashCode();

                if (isScaled) {
                    currScaled = v;
                    int[] original = {currview.getMeasuredWidth(), currview.getMeasuredHeight()};
                    originalSize = original;
                }

                scaleDetector.onTouchEvent(event);

                return true;
            }
        });
    }

    private void initializeScaleDetector(){
        scaleDetector = new ScaleGestureDetector (getApplicationContext(), new ScaleGestureDetector.OnScaleGestureListener(){
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                isScaled = false;
                scrollingLocked = false;
                scrollContainer.requestDisallowInterceptTouchEvent(false);
                previousScale = (float) 1.0;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                isScaled = true;
                scrollingLocked = true;
                scrollContainer.requestDisallowInterceptTouchEvent(true);

                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (gestureTolerance(detector)) {
                    if (originalSize == null)
                        return false;

                    // scale image
                    float scaleFactor = detector.getScaleFactor();
                    ImageView currView = (ImageView) currScaled;
                    currView.getLayoutParams().width = Math.round(originalSize[0] + (originalSize[0] * (scaleFactor - previousScale)));
                    currView.getLayoutParams().height = Math.round(originalSize[1] + (originalSize[1] * (scaleFactor - previousScale)));

                    previousScale = scaleFactor;

                    currView.requestLayout();

                    Log.d("zoom scale", "" + scaleFactor);
                }

                return false;

            }
        });
    }

    // prevent pinches that are too small
    private boolean gestureTolerance(@NonNull ScaleGestureDetector detector) {
        final float spanDelta = Math.abs(detector.getCurrentSpan() - detector.getPreviousSpan());
        return spanDelta > SPAN_SLOP;
    }

    // for locking the scrollview when scaling
    private void addScrollListener() {
        scrollContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return scrollingLocked;
            }
        });
    }

    // ================= METHODS FOR PARSING BITMAPS AND PLACING IN IMAGEVIEWS =====================

    // method for creating image view dynamically
    private ImageView createImageView(Bitmap bmp){
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(bmp);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, convertDipToPixels(100));
        lp.gravity = Gravity.CENTER;
        lp.bottomMargin = 25;
        lp.topMargin = 25;

        iv.setLayoutParams(lp);

        return iv;
    }

    // gets array of cropped bitmaps based on locations
    private Bitmap[] parseBitmap(){
        int numBitmaps = locations.length;

        int bitmapHeight = pdfImage.getHeight();
        int[][] actualLocations = new int[numBitmaps][2];

        Bitmap[] croppedBitmaps = new Bitmap[numBitmaps];

        // first, convert locations into a array with actual array locations, not ratios
        for (int i = 0 ; i < numBitmaps; i++){
            actualLocations[i][0] = (int) Math.ceil(locations[i][0] * bitmapHeight);
            actualLocations[i][1] = (int) Math.ceil(locations[i][1] * bitmapHeight);

            int h1 = actualLocations[i][0];
            int h2 = actualLocations[i][1];

            Log.e("check", h1 + "   " + h2 + "  " + bitmapHeight + "  " + (locations[i][0] * bitmapHeight));

            Bitmap newBitmap = Bitmap.createBitmap(pdfImage, 0, h1, pdfImage.getWidth(), h2- h1);
            croppedBitmaps[i] = newBitmap;
        }

        return croppedBitmaps;
    }

    // gets relevant data from the passed intent
    private void getIntentData(){
        Intent currIntent = getIntent();

        // get location data
        Bundle bundle = currIntent.getExtras();
        locations = (double[][]) bundle.getSerializable("location_data");

        // get bitmap data
        String fileName = currIntent.getStringExtra("image_filename");

        File filePath = this.getFileStreamPath(fileName);
        FileInputStream fi;
        try {
            fi = new FileInputStream(filePath);
            pdfImage = BitmapFactory.decodeStream(fi);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // ===================== UTILITY METHODS ===============================

    public int convertDipToPixels(int val){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics());
    }

}