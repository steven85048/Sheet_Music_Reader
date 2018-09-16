package smr.sheetmusicreader.dashboard.pdflistfragment;

/// Handles fragment lifecycle methods and passes viewmodel to relevant areas

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import smr.sheetmusicreader.R;
import smr.sheetmusicreader.dashboard.pdflistfragment.viewmodels.FileListViewModel;
import smr.sheetmusicreader.dashboard.pdfselectionfragment.PdfSelectorFragment;
import smr.sheetmusicreader.databinding.FragmentFileListBinding;

public class FileListFragment extends Fragment {

    //-------------------------------------------------------------
    // INSTANCE VARIABLES
    //-------------------------------------------------------------

    // Implementation of the list action for this list; does not change so setting through
    // bundle **should** be okay
    FileListInterface mFileListStrategy;

    // Data class for list data
    FileListViewModel mViewModel;

    //-------------------------------------------------------------
    // FRAGMENT LIFECYCLE METHODS
    //-------------------------------------------------------------

    // Empty constructor; does nothing but is required for the fragment
    public FileListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the saved state and pass to view model
        ArrayList<String> mListUrls = new ArrayList<String>();
        String mListName = "";

        // Obtain the necessary arguments from the bundle
        if (getArguments() != null) {
             mListUrls = getArguments().getStringArrayList("aListUrls");
             mListName = getArguments().getString("aListName");
        }

        mViewModel = new FileListViewModel( mListName, mListUrls );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Show the fragment xml and retrieve the binding class instance
        FragmentFileListBinding theBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_file_list, container, false);

        // Expose the necessary data to the binding class
        theBinding.setViewmodel(mViewModel);

        // Initialize the recycler view with the correct data (view model)
        PdfListManager thePdfListManager = new PdfListManager( theBinding.pdfList, getContext(), mViewModel, mFileListStrategy );

        View view = theBinding.getRoot();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setFileListStrategy( FileListInterface aFileListStrategy ) {
        mFileListStrategy = aFileListStrategy;
    }

    //-------------------------------------------------------------
    // UTILITY METHODS
    //-------------------------------------------------------------

    // We use a static initialization method so that we maintain the fragment state despite
    // fragment reinitalization ( explained here: https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment)
    public static FileListFragment newInstance( ArrayList<String> aListUrls, String aListName, FileListInterface aFileListStrategy ) {
        FileListFragment theFileListFragment = new FileListFragment();

        Bundle theBundle = FileListFragment.createDataBundle( aListUrls, aListName );
        theFileListFragment.setArguments( theBundle );

        theFileListFragment.setFileListStrategy(aFileListStrategy);

        return theFileListFragment;
    }

    public static Bundle createDataBundle( ArrayList<String> aListUrls, String aListName ) {
        Bundle theBundleArgs = new Bundle();

        // Populate the bundle with the necessary data
        theBundleArgs.putStringArrayList( "aListUrls", aListUrls );
        theBundleArgs.putString( "aListName", aListName );

        return theBundleArgs;
    }
}
