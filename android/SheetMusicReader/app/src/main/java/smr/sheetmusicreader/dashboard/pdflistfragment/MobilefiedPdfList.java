package smr.sheetmusicreader.dashboard.pdflistfragment;

/// Specific List Functionality for the list of mobilefied pdfs

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MobilefiedPdfList implements FileListInterface {

    //----------------------------------------------------------
    // EVENT HANDLERS
    //----------------------------------------------------------

    @Override
    public void listItemClicked() {
        Log.e("Item Clicked", "Mobilefied Item");
    }

    @Override
    public void itemAddClicked() {

    }

}
