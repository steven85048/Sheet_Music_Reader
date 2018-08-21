package smr.sheetmusicreader;

/// Parent class that handles fragment transactions and facilitate data passage between
/// those fragments

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;

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
        showRawPdfListFragment();
        showMobilefiedPdfListfragment();

        //cppApi = cppApi.Create();
        //cppApi.DetectMeasure();
    }

    //-----------------------------------------------------
    // Fragment Manager Methods
    //-----------------------------------------------------

    private void showRawPdfListFragment() {
        FragmentTransaction theFragmentTransaction = mFragmentManager.beginTransaction();

        Fragment theRawPdfListFragment = RawPdfList.newInstance(new ArrayList<String>(Arrays.asList("Raw1", "Raw2", "Raw3")), "Raw Media");
        theFragmentTransaction.replace(R.id.mRawPdfList, theRawPdfListFragment );

        theFragmentTransaction.commit();
    }

    private void showMobilefiedPdfListfragment() {
        FragmentTransaction theFragmentTransaction = mFragmentManager.beginTransaction();

        Fragment theMobilefiedPdfListFragment = MobilefiedPdfList.newInstance(new ArrayList<String>(Arrays.asList("Mob1", "Mob2", "Mob3")), "Processed Media");
        theFragmentTransaction.replace(R.id.mMobilefiedPdfList, theMobilefiedPdfListFragment);

        theFragmentTransaction.commit();
    }

}
