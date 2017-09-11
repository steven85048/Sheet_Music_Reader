package com.example.steven_pc.sheet_buddy;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class CreateActivity extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 0;
    private static final int DPI_BUTTON_SIZE = 50;

    private static final int imageViewId = R.id.pdfView;
    private static final int addButtonLoc = R.id.add_button;
    private static final int globalLayout = R.id.global_layout;
    private static final int mainContainer = R.id.main_container;

    private ScrollView scrollView;
    private Button addButton;
    private ImageView pdfView;

    static int currImageHeight;
    static int currImageWidth;

    static float previousY;
    RelativeLayout.LayoutParams lp;

    static boolean checkAddButton = false;
    static boolean scrollingLocked = false;

    ArrayList<Button[]> buttonSet = new ArrayList<Button[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // get permanent elements from xml
        scrollView = (ScrollView) findViewById(globalLayout);
        addButton = (Button) findViewById(addButtonLoc);
        pdfView = (ImageView) findViewById(imageViewId);

        // Listen whenever the dimension of the image is changed
        addPdfViewDimensionListener();
        addAddButtonListener();
        addGlobalTouchListener();

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
                        // get the name of the file to ensure it works
                        String name = "";
                        if (uri != null && "content".equals(uri.getScheme())) {
                            Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()){
                                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                name = displayName;
                                Log.i("DISPLAY NAME", displayName);
                            }
                            cursor.close();
                        }

                        Toast.makeText(this, "Opening  " + name, Toast.LENGTH_SHORT).show();

                        String ret = "";

                        // create input stream to read pdf
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        File file = new File(getCacheDir(), "currFile.pdf");

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

    // -------------------- METHODS FOR LISTENING FOR TOUCHES ------------------------

    private void addGlobalTouchListener() {
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (checkAddButton) {
                    Toast.makeText(getApplicationContext(), "CLICKED", Toast.LENGTH_SHORT);
                    checkAddButton = false;
                    addButtonSet(getSVLocation((int) event.getY()));
                    Log.e("LOCATION ADD", (int) event.getX() + "  " + (int) event.getY());
                }

                return scrollingLocked;
            }
        });
    }

    // ------------------- METHODS FOR ADDING DIVISIONS -------------------------------

    private void addAddButtonListener() {
        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                checkAddButton = true;
                // add animation to highlight left side
            }
        });
    }

    // ------------------- METHODS FOR MANAGING THE PDF IMAGE --------------------------

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

    protected void findMeasures(File file) throws Exception{
        Bitmap enclosingBitmap = combineBitmaps(file);
        pdfView.setImageBitmap(enclosingBitmap);

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

    // ----------------------------- MODULES FOR ADDING BUTTONS DYNAMICALLY ----------------------------

    public void addButtonSet (int height){
        RelativeLayout sv = (RelativeLayout) findViewById(mainContainer);

        // first, add rectangle to denote area


        Button buttonStart = createNewButton( height, true);
        Button buttonEnd = createNewButton(height, false);

        sv.addView(buttonStart);
        sv.addView(buttonEnd);

        addButtonMoveListener(buttonStart);
        addButtonMoveListener(buttonEnd);

        Button[] buttons = {buttonStart, buttonEnd};
        buttonSet.add(buttons);
    }

    public ImageView createRectangleImage(){
        ImageView iv = new ImageView(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(convertDipToPixels(DPI_BUTTON_SIZE), convertDipToPixels(DPI_BUTTON_SIZE));
    }

    public void addButtonMoveListener (Button button){
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int currY = (int) event.getRawY();

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        scrollView.requestDisallowInterceptTouchEvent(true);

                        previousY = currY - lp.topMargin;
                        scrollingLocked = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        lp.topMargin = currY - (int) previousY;
                        v.setLayoutParams(lp);

                        break;
                    case MotionEvent.ACTION_UP:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        scrollingLocked = false;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                }
                return true;
            }
        });
    }

    public Button createNewButton (int height, boolean start){
        Button button = new Button(this);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 1);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        button.setLayoutParams(lp);

        if (start) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            button.setBackgroundColor(Color.CYAN);
        }
        else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            button.setBackgroundColor(Color.RED);
        }

        lp.topMargin = height;

        return button;
    }

    public int convertDipToPixels(int val){
       return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics());
    }

    public int getSVLocation(int height){
        return (int) scrollView.getScrollY() + height - convertDipToPixels(DPI_BUTTON_SIZE / 2);
    }

    // ==================== MEAURE FINDING ALGORITHM =======================================


}
