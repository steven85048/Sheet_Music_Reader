package smr.sheetmusicreader;

/// Specific List Functionality for the list of mobilefied pdfs

import android.os.Bundle;

import java.util.ArrayList;

public class MobilefiedPdfList extends FileListFragment implements FileListInterface {

    // We use a static initialization method so that we maintain the fragment state despite
    // fragment reinitalization ( explained here: https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment)
    public static MobilefiedPdfList newInstance( ArrayList<String> aListUrls, String aListName ) {
        MobilefiedPdfList theMobilefiledPdfList = new MobilefiedPdfList();

        Bundle theBundle = FileListFragment.createDataBundle( aListUrls, aListName );
        theMobilefiledPdfList.setArguments( theBundle );

        return theMobilefiledPdfList;
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
