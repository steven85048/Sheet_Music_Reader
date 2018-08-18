package smr.sheetmusicreader;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FileListFragment extends Fragment {

    //-------------------------------------------------------------
    // INSTANCE VARIABLES
    //-------------------------------------------------------------

    // For communication to the parent activity
    private OnFragmentInteractionListener mListener;

    ArrayList<String> mListUrls;
    String mListName;

    //-------------------------------------------------------------
    // FRAGMENT LIFECYCLE METHODS
    //-------------------------------------------------------------

    // Empty constructor; does nothing but is required for the fragment
    public FileListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the necessary arguments from the bundle
        if (getArguments() != null) {
            mListUrls = getArguments().getStringArrayList("aListUrls");
            mListName = getArguments().getString("aListName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_file_list, container, false);
    }

    public void fragmentEventListener(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //-------------------------------------------------------------
    // UTILITY METHODS
    //-------------------------------------------------------------

    public static Bundle createDataBundle( ArrayList<String> aListUrls, String aListName ) {
        Bundle theBundleArgs = new Bundle();

        // Populate the bundle with the necessary data
        theBundleArgs.putStringArrayList( "aListUrls", aListUrls );
        theBundleArgs.putString( "aListName", aListName );

        return theBundleArgs;
    }
}
