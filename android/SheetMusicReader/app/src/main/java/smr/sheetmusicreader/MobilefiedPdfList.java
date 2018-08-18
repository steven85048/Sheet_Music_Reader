package smr.sheetmusicreader;

/// Specific List Functionality for the list of mobilefied pdfs

import android.os.Bundle;

import java.util.ArrayList;

public class MobilefiedPdfList extends FileListFragment implements FileListInterface {

    public static MobilefiedPdfList newInstance( ArrayList<String> aListUrls ) {
        MobilefiedPdfList theMobilefiledPdfList = new MobilefiedPdfList();

        Bundle theBundleArgs = new Bundle();
        theBundleArgs.putStringArrayList( "aMobilefiedPdfList", aListUrls );
        theMobilefiledPdfList.setArguments(theBundleArgs);

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
