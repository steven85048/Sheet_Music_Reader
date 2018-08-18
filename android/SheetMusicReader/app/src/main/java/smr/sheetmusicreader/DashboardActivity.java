package smr.sheetmusicreader;

/// Parent class that handles fragment transactions and facilitate data passage between
/// those fragments

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public class DashboardActivity extends FragmentActivity {

    //private smr.sheetmusicreader.MeasureReader cppApi;

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
        showPdfListFragment();

        //cppApi = cppApi.Create();
        //cppApi.DetectMeasure();
    }

    //-----------------------------------------------------
    // Fragment Manager Methods
    //-----------------------------------------------------

    private void showPdfListFragment() {
        FragmentTransaction theFragmentTransaction = mFragmentManager.beginTransaction();

        Fragment thePdfListFragment = new FileListFragment();
        theFragmentTransaction.replace(R.id.mListFragment, thePdfListFragment );
        theFragmentTransaction.commit();
    }

}
