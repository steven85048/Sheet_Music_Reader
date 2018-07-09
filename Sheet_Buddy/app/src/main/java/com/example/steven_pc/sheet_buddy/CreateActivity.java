package com.example.steven_pc.sheet_buddy;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

public class CreateActivity extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 0;
    private static final int DPI_BUTTON_SIZE = 50;

    private static final float HIGHLIGHT_TRANSPARENCY = (float) 0.1;
    private static final float NON_SELECTED_TRANSPARENCY = (float) 0.4;
    private static final float SELECTED_TRANSPARENCY = (float) 1.0;

    private static final int imageViewId = R.id.pdfView;
    private static final int addButtonLoc = R.id.add_button;
    private static final int globalLayout = R.id.global_layout;
    private static final int mainContainer = R.id.main_container;
    private static final int submitButtonId = R.id.submitButton;
    private static final int spinnerId = R.id.spinner;

    private ScrollView scrollView;
    private Button addButton;
    private ImageView pdfView;
    private Button submitButton;
    private ProgressBar spinner;

    private static final String CACHE_FILE_NAME = "currFile.pdf";

    Bitmap pdfImage;

    static int currImageHeight;
    static int currImageWidth;

    RelativeLayout.LayoutParams lp;

    static boolean checkAddButton = false;
    static boolean scrollingLocked = false;

    HashMap<Integer, ImageView> hmStart = new HashMap<Integer, ImageView>();
    HashMap<Integer, ImageView> hmEnd = new HashMap<Integer, ImageView>();

    String name = "";

    ArrayList<ImageView[]> buttonSet = new ArrayList<ImageView[]>();
    int buttCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // get permanent elements from xml
        scrollView = (ScrollView) findViewById(globalLayout);
        addButton = (Button) findViewById(addButtonLoc);
        pdfView = (ImageView) findViewById(imageViewId);
        submitButton = (Button) findViewById(submitButtonId);
        spinner = (ProgressBar) findViewById(spinnerId);

        // Listen whenever the dimension of the image is changed
        addAddButtonListener();
        addGlobalTouchListener();
        addSubmitButtonListener();

        // open content chooser when activity loads
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try{
            startActivityForResult(intent, FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "File Manager cannot be opened", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    try {
                        // get the name of the file
                        if (uri != null && "content".equals(uri.getScheme())) {
                            Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()){
                                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                name = displayName;
                                Log.i("DISPLAY NAME", displayName);
                            }
                            cursor.close();
                        }

                        // Set the name of the file to the new file we are going to save
                        storageFileName = name;
                        Toast.makeText(this, "Opening  " + name, Toast.LENGTH_SHORT).show();

                        String ret = "";

                        // create input stream to read pdf
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        File file = new File(getCacheDir(), CACHE_FILE_NAME);

                        try {
                            OutputStream output = new FileOutputStream(file);
                            try {
                                try {
                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                    int read;

                                    while ((read = inputStream.read(buffer)) != -1) {
                                        output.write(buffer, 0, read);
                                    }
                                    output.flush();
                                } finally {
                                    output.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace(); // handle exception, define IOException and others
                            }
                        } finally {
                            inputStream.close();
                        }

                        Log.e("PATH", file.getAbsolutePath());
                        findMeasures(file);

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // -------------------- METHODS FOR LISTENING FOR TOUCHES --------------------------------------

    private void addGlobalTouchListener() {
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (checkAddButton) {
                    Drawable d = getResources().getDrawable(R.drawable.buttonshape);
                    addButton.setBackground(d);

                    checkAddButton = false;
                    addButtonSet(getSVLocation((int) event.getY()));

                    Log.e("LOCATION ADD", (int) event.getX() + "  " + (int) event.getY());
                }

                return scrollingLocked;
            }
        });
    }

    // ------------------- METHODS FOR ADDING/MANAGING DIVISIONS --------------------------------------------

    private void addAddButtonListener() {
        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                checkAddButton = true;
                Drawable d = getResources().getDrawable(R.drawable.buttonshapeselected);
                addButton.setBackground(d);
            }
        });
    }

    private void addSubmitButtonListener() {
        submitButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), SheetPlayActivity.class);

                // send location data
                Bundle bundle = new Bundle();
                double[][] locations = convertButtonsToArray();
                bundle.putSerializable("location_data", locations);
                intent.putExtras(bundle);

                // Compress image and put in byte array
                intent.putExtra("image_filename", name);


                startActivity(intent);
            }
        });
    }

    // places the button location tuples into a sorted integer list
    public double[][] convertButtonsToArray() {
        ArrayList<Double[]> locationsUnsorted = new ArrayList<Double[]>();

        for (int i = 0 ; i < buttonSet.size(); i++){
            ImageView[] set = buttonSet.get(i);
            ImageView buttonStart = set[0];
            ImageView buttonEnd = set[1];

            RelativeLayout.LayoutParams startParams = (RelativeLayout.LayoutParams) buttonStart.getLayoutParams();
            RelativeLayout.LayoutParams endParams = (RelativeLayout.LayoutParams) buttonEnd.getLayoutParams();

            int totalStart = startParams.topMargin + convertDipToPixels(DPI_BUTTON_SIZE/2);
            int totalEnd = endParams.topMargin + convertDipToPixels(DPI_BUTTON_SIZE/2);

            double startFrac = (double) totalStart / currImageHeight;
            double endFrac = (double) totalEnd / currImageHeight;

            Log.e("image height", "" + currImageHeight);

            Double[] newLocation = {startFrac, endFrac};
            locationsUnsorted.add(newLocation);
        }

        // sorting by start position
        Collections.sort(locationsUnsorted, new Comparator<Object[]>(){
            @Override
            public int compare(Object[] o1, Object[] o2){
                Double d1 = (Double) (o1[0]);
                Double d2 = (Double) (o2[0]);

                return d1.compareTo(d2);
            }
        });

        double[][] locations = new double[buttonSet.size()][2];
        for (int i = 0 ; i < locationsUnsorted.size();i ++){
            locations[i][0] = locationsUnsorted.get(i)[0];
            locations[i][1] = locationsUnsorted.get(i)[1];
        }

        return locations;
    }

    // ------------------- METHODS FOR MANAGING THE PDF IMAGE --------------------------------------

    private void addPdfViewDimensionListener(){
        final ImageView pdfView = (ImageView) findViewById(imageViewId);
        int finalHeight, finalWidth;
        ViewTreeObserver vto = pdfView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                pdfView.getViewTreeObserver().removeOnPreDrawListener(this);

                currImageHeight = pdfView.getMeasuredHeight();
                currImageWidth = pdfView.getMeasuredWidth();
                Log.e("dimensions", "Height: " + currImageWidth + " Width: " + currImageHeight);
                return true;
            }
        });
    }

    protected void findMeasures(File file) throws Exception {
        // RUN ON A SEPARATE THREAD IN ORDER TO PREVENT UI LAG
        final File mFile = file;
        new RenderBitmap(pdfView, mFile).execute();
    }

    // AsyncTask for rendering bitmap onto imageview
    class RenderBitmap extends AsyncTask<String, Void, Bitmap> {
        ImageView img;
        File f;

        public RenderBitmap(ImageView img, File f){
            this.img = img;
            this.f = f;
        }

        protected Bitmap doInBackground(String... urls){
            Bitmap bmp = combineBitmaps(f);
            saveBitmap(name, bmp);
            return bmp;
        }

        protected void onPostExecute(Bitmap result){
            img.setImageBitmap(result);
            addButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);

            // lol this shits not working so i moved it here
            addPdfViewDimensionListener();
        }
    }

    private void saveBitmap(String fileName, Bitmap bitmap){
        FileOutputStream out = null;
        try {
            File f = new File(fileName);
            f.delete();
            out = openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // COMBINES ALL PAGES IN A PDF INTO A SINGLE BITMAP FOR EASIER USE
    public Bitmap combineBitmaps (File file){
        try {
            // pdf renderer only works on lollipop or higher
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Bitmap enclosingBitmap;

                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
                int pageCount = renderer.getPageCount();
                Log.e("PAGE COUNT ", "" + pageCount);

                Bitmap[] bitmapCollection = new Bitmap[pageCount];

                // accumulate height and find max width
                int totalWidth = 0;
                int totalHeight = 0;

                // render each of the pages in the pdf and add to canvas
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);

                    int currWidth = page.getWidth();
                    int currHeight = page.getHeight();

                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    // save and update data
                    if (currWidth > totalWidth)
                        totalWidth = currWidth;
                    totalHeight += currHeight;
                    bitmapCollection[i] = bitmap;

                    page.close();
                }

                enclosingBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(enclosingBitmap);

                int paintHeight = 0;
                for (int i = 0 ; i < bitmapCollection.length; i++) {
                    Bitmap currBitmap = bitmapCollection[i];
                    canvas.drawBitmap(currBitmap, 0, paintHeight, null);
                    paintHeight += currBitmap.getHeight();
                }

                return enclosingBitmap;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("VERSION", "ANDROID VERSION TOO LOW");
        return null;
    }

    // ----------------------------- MODULES FOR ADDING BUTTONS DYNAMICALLY ------------------------

    public void addButtonSet (int height){
        RelativeLayout sv = (RelativeLayout) findViewById(mainContainer);

        // first, add rectangle to denote area
        int randColor = generateRandomColor();

        ImageView iv = createRectangleImage(height, randColor);
        ImageView buttonStart = createNewButton( height, randColor, true);
        ImageView buttonEnd = createNewButton(height, randColor, false);

        sv.addView(iv);
        sv.addView(buttonStart);
        sv.addView(buttonEnd);

        addButtonMoveListener(buttonStart);
        addButtonMoveListener(buttonEnd);

        ImageView[] buttons = {buttonStart, buttonEnd};

        hmStart.put(buttonStart.hashCode(), iv);
        hmEnd.put(buttonEnd.hashCode(), iv);

        buttonSet.add(buttons);
    }

    public ImageView createRectangleImage(int height, int color){
        ImageView iv = new ImageView(this);

        // create layout params for the rectangular image view
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, convertDipToPixels(3));
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.topMargin = height + convertDipToPixels(DPI_BUTTON_SIZE/2);
        iv.setLayoutParams(lp);
        iv.setImageResource(R.drawable.rectangle);

        iv.setBackgroundColor(color);
        iv.setAlpha(HIGHLIGHT_TRANSPARENCY);

        return iv;
    }

    public void addButtonMoveListener (ImageView button){

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { 
                int currY = (int) event.getRawY();
                int totalY = getSVLocation(currY);

                ImageView currButton = (ImageView) v;

                switch(event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        currButton.setAlpha(SELECTED_TRANSPARENCY);

                        scrollView.requestDisallowInterceptTouchEvent(true);
                        scrollingLocked = true;
                        buttCount++;
                        break;
                    case MotionEvent.ACTION_MOVE:

                        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) currButton.getLayoutParams();
                        lp2.topMargin = totalY - convertDipToPixels(DPI_BUTTON_SIZE/2);
                        currButton.setLayoutParams(lp2);


                        // update rectangle highlight
                        int currHash = currButton.hashCode();
                        if (hmStart.containsKey(currHash)){
                            ImageView currView = hmStart.get(currHash);
                            RelativeLayout.LayoutParams ivParams = (RelativeLayout.LayoutParams) currView.getLayoutParams();
                            int prevY = ivParams.topMargin;
                            ivParams.topMargin = totalY;

                            // updated size is (newmargin - topmargin + previous size)
                            int diff = (prevY-totalY) + ivParams.height;
                            ivParams.height = diff;
                            currView.setLayoutParams(ivParams);

                        } else if (hmEnd.containsKey(currHash)){
                            ImageView currView = hmEnd.get(currHash);
                            RelativeLayout.LayoutParams ivParams2 = (RelativeLayout.LayoutParams) currView.getLayoutParams();
                            int prevY = ivParams2.topMargin + ivParams2.height;

                            // updated size is (newmargin - topmargin + previous size)
                            int diff = (totalY-prevY) + ivParams2.height;
                            ivParams2.height = diff;
                            currView.setLayoutParams(ivParams2);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        currButton.setAlpha(NON_SELECTED_TRANSPARENCY);
                        buttCount--;
                        if (buttCount <= 0) {
                            scrollView.requestDisallowInterceptTouchEvent(false);
                            scrollingLocked = false;
                            buttCount = 0;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                }

                return true;
            }
        });
    }

    public ImageView createNewButton (int height, int color, boolean start){
        ImageView button = new ImageView(this);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(convertDipToPixels(DPI_BUTTON_SIZE), convertDipToPixels(DPI_BUTTON_SIZE));
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        button.setLayoutParams(lp);

        if (start) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            button.setImageResource(R.drawable.triangle_right);
        }
        else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            button.setImageResource(R.drawable.triangle_left);
        }

        button.setAlpha(NON_SELECTED_TRANSPARENCY);
        button.setColorFilter(color);

        lp.topMargin = height;

        return button;
    }

    //-------------------------------- UTILITY METHODS ---------------------------------------------

    public int convertDipToPixels(int val){
       return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics());
    }

    public int getSVLocation(int height){
        return (int) scrollView.getScrollY() + height - convertDipToPixels(DPI_BUTTON_SIZE / 2);
    }

    public int generateRandomColor(){
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        return color;
    }

    // ==================== MEASURE FINDING ALGORITHM ===============================================


}
