package smr.sheetmusicreader;

/// Specific List Functionality for the list of raw pdfs

import android.os.Bundle;

import java.util.ArrayList;

public class RawPdfList extends FileListFragment implements FileListInterface {

    // We use a static initialization method so that we maintain the fragment state despite
    // fragment reinitalization ( explained here: https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment)
    public static RawPdfList newInstance( ArrayList<String> aListUrls, String aListName ) {
        RawPdfList theRawPdfListFragment = new RawPdfList();

        Bundle theBundle = FileListFragment.createDataBundle( aListUrls, aListName );
        theRawPdfListFragment.setArguments( theBundle );

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
