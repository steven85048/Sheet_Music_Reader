package smr.sheetmusicreader.measureselection;

import android.app.Activity;
import android.os.Bundle;

/// This class provides the interface for the auto-detection of measures and their editing
public class MeasureSelectionActivity extends Activity {

    // DATA

    private smr.sheetmusicreader.MeasureReader cppApi;

    private String mFilePath;

    // LIFECYCLE METHODS

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate( aSavedInstanceState );

        // Retrieve the necessary data from the passed bundle
        mFilePath = ( String ) aSavedInstanceState.getString( "mRawItemPath");

        cppApi = cppApi.Create();
        cppApi.DetectMeasure();

        // TODO: Set the content view to the measure selection activity xml
    }


}
