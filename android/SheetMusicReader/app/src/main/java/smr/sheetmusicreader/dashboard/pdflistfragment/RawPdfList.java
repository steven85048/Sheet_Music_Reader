package smr.sheetmusicreader.dashboard.pdflistfragment;

/// Specific List Functionality for the list of raw pdfs

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class RawPdfList implements FileListInterface {

    //----------------------------------------------------------
    // EVENT HANDLERS
    //----------------------------------------------------------

    @Override
    public void listItemClicked() {
        Log.e("Item Clicked", "Raw Item");
    }

    @Override
    public void itemAddClicked() {

    }
}
