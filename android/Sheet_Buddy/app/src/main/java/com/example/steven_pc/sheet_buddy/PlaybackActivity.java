package com.example.steven_pc.sheet_buddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PlaybackActivity extends AppCompatActivity {
    String fileName;

    int[][] actualLocations;
    int[][] sizeData;
    Bitmap bmpPdf;

    // ================ ACTIVITY EVENTS ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("file_name");

        /**
        String[] list = fileList();
        for (int i = 0 ; i < list.length; i++){
            Log.e("Inner file ", list[i]);
        }
         **/

        bmpPdf = getPdfFromMem();
        actualLocations = getDataFromMem(1);
        sizeData = getDataFromMem(2);
    }

    // ========================= MEMORY COLLECTION METHDOS =========================================

    private Bitmap getPdfFromMem(){
        String filePath = fileName;
        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        return bmp;
    }

    private int[][] getDataFromMem(int num){
        String fileLoc = fileName + num;

        int[][] data = null;

        try{
            FileInputStream reader = openFileInput(fileLoc);
            BufferedInputStream buf = new BufferedInputStream(reader);
            byte[] bytes = new byte[4];

            // first read the size
            buf.read(bytes, 0, bytes.length);
            int size = ByteBuffer.wrap(bytes).getInt();
            data = new int[size][2];

            int index = 0;
            int count = 0;
            while(count < 2 * size) {
                buf.read(bytes, 0, bytes.length);
                data[count/2][index] = ByteBuffer.wrap(bytes).getInt();

                count++;
                index = ((index+1) % 2);
            }

            buf.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        Log.e("actual location data", Arrays.deepToString(data));
        return data;
    }

    // ============================= UTILITY METHODS ===============================================

}
