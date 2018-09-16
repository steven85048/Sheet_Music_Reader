package smr.sheetmusicreader.dashboard.pdflistfragment;

import android.support.v4.app.FragmentActivity;

public class ListHandlerFactory {
    public FileListInterface createMobilefiedPdfListStrategy() {
        return new MobilefiedPdfList();
    }

    public FileListInterface createRawPdfListStrategy( FragmentActivity aFragmentActivity ) {
        return new RawPdfList( aFragmentActivity );
    }
}
