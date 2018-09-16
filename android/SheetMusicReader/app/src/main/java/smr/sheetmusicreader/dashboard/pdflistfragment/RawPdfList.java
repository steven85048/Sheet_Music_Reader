package smr.sheetmusicreader.dashboard.pdflistfragment;

/// Specific List Functionality for the list of raw pdfs
// NOTE that this list type is only for making pdfs visible from the file system

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.ArrayList;

import smr.sheetmusicreader.R;
import smr.sheetmusicreader.dashboard.pdflistfragment.viewmodels.PdfListItemViewModel;
import smr.sheetmusicreader.dashboard.pdfselectionfragment.PdfSelectorFragment;

public class RawPdfList implements FileListInterface, PdfSelectorFragment.OnFragmentInteractionListener {

    //----------------------------------------------------------
    // DATA
    //----------------------------------------------------------

    FragmentActivity mFragmentActivity;

    public RawPdfList(FragmentActivity aFragmentActivity) {
        mFragmentActivity = aFragmentActivity;
    }

    //----------------------------------------------------------
    // EVENT HANDLERS
    //----------------------------------------------------------

    @Override
    public void listItemClicked( PdfListItemViewModel aViewModel ) {
        Log.e("Item Clicked", aViewModel.getItemName());
    }

    @Override
    public void itemAddClicked() {
        // Add allows the user to select an item from their library to have it visible on the dashboard
        swapAddItemFragment();
    }

    //-------------------------------------------------------------
    // PdfSelectorFragment.OnFragmentInteractionListener OVERRIDES
    //-------------------------------------------------------------

    @Override
    public void onUrlSelected(String aSelectedString) {
        Log.e("stevens debug",  aSelectedString);

        // TODO: pass this url to the measureselectionactivity
    }

    @Override
    public void onPdfOpenError() {

    }

    public void swapAddItemFragment() {
        // Initialize the transaction item
        FragmentManager mFragmentManager = mFragmentActivity.getSupportFragmentManager();
        FragmentTransaction theFragmentTransaction = mFragmentManager.beginTransaction();

        // replace the item with the fragment
        PdfSelectorFragment thePdfSelectorFragment = new PdfSelectorFragment();
        theFragmentTransaction.replace(R.id.add_file_fragment , thePdfSelectorFragment );

        theFragmentTransaction.commit();
    }
}
