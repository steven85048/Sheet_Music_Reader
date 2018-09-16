package smr.sheetmusicreader.dashboard;

/// Parent class that handles fragment transactions and facilitate data passage between
/// those fragments

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;

import smr.sheetmusicreader.R;
import smr.sheetmusicreader.dashboard.pdflistfragment.FileListFragment;
import smr.sheetmusicreader.dashboard.pdflistfragment.FileListInterface;
import smr.sheetmusicreader.dashboard.pdflistfragment.ListHandlerFactory;
import smr.sheetmusicreader.dashboard.pdfselectionfragment.PdfSelectionUtils;

public class DashboardActivity extends FragmentActivity {

    FragmentManager mFragmentManager;

    static {
        System.loadLibrary( "sheetmusicreader");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize the fragment manager and populate fragments
        mFragmentManager = getSupportFragmentManager();
        showRawPdfListFragment();
        showMobilefiedPdfListfragment();
    }

    //-----------------------------------------------------
    // Fragment Manager Methods
    //-----------------------------------------------------

    private void showRawPdfListFragment() {
        FragmentTransaction theFragmentTransaction = mFragmentManager.beginTransaction();

        ListHandlerFactory mListHandlerFactory = new ListHandlerFactory();

        FileListInterface mFileListStrategy = mListHandlerFactory.createRawPdfListStrategy( this );
        Fragment theRawPdfListFragment = FileListFragment.newInstance(new ArrayList<String>(Arrays.asList("Raw1", "Raw2", "Raw3")), "Raw Media", mFileListStrategy);
        theFragmentTransaction.replace(R.id.mRawPdfList, theRawPdfListFragment );

        theFragmentTransaction.commit();
    }

    private void showMobilefiedPdfListfragment() {
        FragmentTransaction theFragmentTransaction = mFragmentManager.beginTransaction();

        ListHandlerFactory mListHandlerFactory = new ListHandlerFactory();

        FileListInterface mFileListStrategy = mListHandlerFactory.createMobilefiedPdfListStrategy();
        Fragment theMobilefiedPdfListFragment = FileListFragment.newInstance(new ArrayList<String>(Arrays.asList("Mob1", "Mob2", "Mob3")), "Processed Media", mFileListStrategy);
        theFragmentTransaction.replace(R.id.mMobilefiedPdfList, theMobilefiedPdfListFragment);

        theFragmentTransaction.commit();
    }

}
