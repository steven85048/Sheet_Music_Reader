package smr.sheetmusicreader;

/// Specific List Functionality for the list of raw pdfs

import android.os.Bundle;

import java.util.ArrayList;

public class RawPdfList extends FileListFragment implements FileListInterface {

    public static RawPdfList newInstance( ArrayList<String> aListUrls ) {
        RawPdfList theRawPdfListFragment = new RawPdfList();

        Bundle theBundleArgs = new Bundle();
        theBundleArgs.putStringArrayList( "aRawPdfList", aListUrls );
        theRawPdfListFragment.setArguments(theBundleArgs);

        return theRawPdfListFragment;
    }

    //----------------------------------------------------------
    // EVENT HANDLERS
    //----------------------------------------------------------

    @Override
    public void listItemClicked() {

    }

    @Override
    public void itemAddClicked() {

    }
}
