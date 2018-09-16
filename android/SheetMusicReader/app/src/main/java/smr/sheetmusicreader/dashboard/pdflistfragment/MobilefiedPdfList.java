package smr.sheetmusicreader.dashboard.pdflistfragment;

/// Specific List Functionality for the list of mobilefied pdfs

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import smr.sheetmusicreader.dashboard.pdflistfragment.viewmodels.PdfListItemViewModel;

public class MobilefiedPdfList implements FileListInterface {

    //----------------------------------------------------------
    // EVENT HANDLERS
    //----------------------------------------------------------

    @Override
    public void listItemClicked( PdfListItemViewModel aViewModel  ) {
        Log.e("Item Clicked", aViewModel.getItemName());
    }

    @Override
    public void itemAddClicked() {

    }

}
