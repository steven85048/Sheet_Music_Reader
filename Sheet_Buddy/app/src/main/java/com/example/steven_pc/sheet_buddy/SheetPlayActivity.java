package com.example.steven_pc.sheet_buddy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SheetPlayActivity extends AppCompatActivity {
    boolean scrollingLocked = false;

    // Components from XML
    private final int MAIN_CONTAINER_ID = R.id.content_sheet_main_container;
    private final int SCROLLVIEW_ID = R.id.scroll_container;
    private final int SUBMIT_ID = R.id.submitButton;

    LinearLayout mainLayout;
    ScrollView scrollContainer;
    Button submitButton;

    // Global variables for resizing
    View currScaled;
    boolean isScaled = false;
    int[] originalSize;
    float previousScale = (float) 1.0;

    // Other constants
    private final int IMAGE_PADDING = 2;
    private final int SPAN_SLOP = 25;

    // data from CreateActivity.java
    double[][] locations;
    int[][] actualLocations;
    Bitmap pdfImage;
    String fileName;

    // other important components
    private ScaleGestureDetector scaleDetector;

    // Current Bitmaps on Page
    Bitmap[] bitmaps;
    ArrayList<ImageView> imageViews;

    // =================== ACTIVITY EVENTS =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_play);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // get data from previous activity
        // getIntentData();

        // get all the elements here
        mainLayout = (LinearLayout) findViewById(MAIN_CONTAINER_ID);
        scrollContainer = (ScrollView) findViewById(SCROLLVIEW_ID);
        submitButton = (Button) findViewById(SUBMIT_ID);

        addSubmitButtonListener();
        initializeScaleDetector();
        addScrollListener();

        new ParseBitmaps().execute();

        /**
        // use intent data to create list of bitmaps and add to imageviews
        bitmaps = parseBitmap();
        imageViews = new ArrayList<ImageView>();
        for (int i = 0 ; i < bitmaps.length; i++) {
            ImageView newView = createImageView(bitmaps[i]);
            imageViews.add(newView);
            addResizeListener(newView);
            mainLayout.addView(newView);
        }

    }

    // button listener for submit button; saves data and routes to play page
    public void addSubmitButtonListener(){
        submitButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                // save relative positions of images
                saveRelativeLocations();

                // save images size data
                List<int[]> data = getImageSizes();
                saveSizeData(data);

                // Move to next Activity (playback)
                Intent intent = new Intent(v.getContext(), PlaybackActivity.class);
                intent.putExtra("file_name", fileName);

                startActivity(intent);
            }
        });

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

    // Asynctask to not block the main thread
    class ParseBitmaps extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... urls){
            // get data from previous activity
            getIntentData();
            bitmaps = parseBitmap();

            return "";
        }

        protected void onPostExecute(String str){
            imageViews = new ArrayList<ImageView>();
            for (int i = 0 ; i < bitmaps.length; i++) {
                ImageView newView = createImageView(bitmaps[i]);
                imageViews.add(newView);
                addResizeListener(newView);
                mainLayout.addView(newView);
            }
        }
    }

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
        actualLocations = new int[numBitmaps][2];

        Bitmap[] croppedBitmaps = new Bitmap[numBitmaps];

        // first, convert locations into a array with actual array locations, not ratios
        for (int i = 0 ; i < numBitmaps; i++){
            actualLocations[i][0] = (int) Math.ceil(locations[i][0] * bitmapHeight);
            actualLocations[i][1] = (int) Math.ceil(locations[i][1] * bitmapHeight);

            int h1 = actualLocations[i][0];
            int h2 = actualLocations[i][1];

            Log.e("check", h1 + "   " + h2 + "  " + bitmapHeight + "  " + locations[i][0]  + "   " +  bitmapHeight);

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
        fileName = currIntent.getStringExtra("image_filename");

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

    // ================= METHODS FOR STORING THE IMAGE DATA ========================================

    // getting the current image sizes into the list
    public List<int[]> getImageSizes() {
        List<int[]> sizes = new ArrayList<int[]>();

        for (int i = 0 ; i < imageViews.size(); i++){
            ImageView av = imageViews.get(i);
            int[] currSize = {av.getWidth(), av.getHeight()};
            sizes.add(currSize);
        }

        return sizes;
    }

    // put the relative locations of the partitioned pictures into app data
    public void saveRelativeLocations(){
        String fileNameSize = fileName + "1";

        // first create a file at the location
        File file = new File(fileNameSize);

        try {
            FileOutputStream out = openFileOutput(fileNameSize, Context.MODE_PRIVATE);

            // first save the length
            byte[] length = convertIntToByteArray(actualLocations.length);
            out.write(length);

            for (int i = 0; i < actualLocations.length; i++) {
                Log.e("Saving ALocations", fileNameSize + "   " + actualLocations[i][0] + " " + actualLocations[i][1]);

                byte[] aHeight = convertIntToByteArray(actualLocations[i][0]);
                byte[] bHeight = convertIntToByteArray(actualLocations[i][1]);

                out.write(aHeight);
                out.write(bHeight);
            }

            out.write(convertIntToByteArray(-1));
            out.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // put the image sizes into the app data
    public void saveSizeData(List<int[]> data) {
        String fileNameSize = fileName + "2";

        // first create a file at the location
        File file = new File(fileNameSize);

        // open fileoutputstream for internal app data
        try {
            FileOutputStream out = openFileOutput(fileNameSize, Context.MODE_PRIVATE);

            // first save the length
            byte[] length = convertIntToByteArray(data.size());
            out.write(length);

            for (int i = 0; i < data.size(); i++) {
                Log.e("Saving sizeData", fileNameSize + "  " + data.get(i)[0] + " " + data.get(i)[1]);

                byte[] arrWidth = convertIntToByteArray(data.get(i)[0]);
                byte[] arrHeight = convertIntToByteArray(data.get(i)[1]);

                out.write(arrWidth);
                out.write(arrHeight);
            }

            out.write(convertIntToByteArray(-1));
            out.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // ===================== UTILITY METHODS ===============================

    public int convertDipToPixels(int val){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics());
    }

    public byte[] convertIntToByteArray(int val){
        byte[] ret = new byte[4];
        ret[3] = (byte) (val & 0xFF);
        ret[2] = (byte) ((val >> 8) & 0xFF);
        ret[1] = (byte) ((val >> 16) & 0xFF);
        ret[0] = (byte) ((val >> 24) & 0xFF);
        return ret;
    }

}