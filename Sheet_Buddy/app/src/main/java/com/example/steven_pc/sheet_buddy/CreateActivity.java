package com.example.steven_pc.sheet_buddy;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import android.view.View;
import android.widget.ImageView;
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

public class CreateActivity extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

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

    protected void findMeasures(File file) throws Exception{


        // PdfRenderer only works on Lollipop or higher
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ImageView pdfView;

            try {
                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
                int pageCount = renderer.getPageCount();
                Log.e("PAGE COUNT ", "" + pageCount);

                Bitmap[] bitmapCollection = new Bitmap[pageCount];

                // accumulate height and find max width
                int totalWidth = 0;
                int totalHeight = 0;

                // render each of the pages in the pdf and add to canvas
                for (int i = 0 ; i < pageCount; i++) {
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

                // compile into enclosing bitmap
                Bitmap enclosingBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(enclosingBitmap);

                int paintHeight = 0;
                for (int i = 0 ; i < bitmapCollection.length; i++){
                    Bitmap currBitmap = bitmapCollection[i];
                    canvas.drawBitmap(currBitmap, 0, paintHeight, null);
                    paintHeight += currBitmap.getHeight();
                }

            } catch (FileNotFoundException e){
                Toast.makeText(this, "FILE IS NOT FOUND", Toast.LENGTH_SHORT).show();
            } catch (IOException e){
                Toast.makeText(this, "ERROR WITH IO", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
