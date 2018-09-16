package smr.sheetmusicreader.dashboard.pdfselectionfragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import smr.sheetmusicreader.R;

import static android.app.Activity.RESULT_OK;

public class PdfSelectorFragment extends Fragment {

    final static int FILE_SELECT_CODE = 0;

    private OnFragmentInteractionListener mParentFragmentListener;
    String mFileName;

    public PdfSelectorFragment() { };

    // =========================================================
    // LIFECYCLE METHODS
    // ==========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Start communication channel with parent fragment
        initParentFragmentListener();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf_selector, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Button theButton = (Button) getView().findViewById(R.id.file_selector_button);
        theButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mParentFragmentListener = null;
    }

    // =========================================================
    // Parent ragment communication
    // ==========================================================

    public interface OnFragmentInteractionListener {
        void onUrlSelected(String aSelectedString);
        void onPdfOpenError();
    }

    public void initParentFragmentListener() {
        mParentFragmentListener = ( OnFragmentInteractionListener ) getParentFragment();
    }

    // =========================================================
    // PDF Selector Methods
    // ==========================================================

    public void selectFile() {
        // Define the intent to open the file system
        Intent theIntent = new Intent(Intent.ACTION_GET_CONTENT);
        theIntent.setType("application/pdf");
        theIntent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(theIntent, "Select a File to Track"), FILE_SELECT_CODE);
        } catch (Exception aException) {
            aException.printStackTrace();
        }
    }

    /// NOTE: Boilerplate File IO Copied from
    /// https://stackoverflow.com/questions/36128077/android-opening-a-file-with-action-get-content-results-into-different-uris
    /// dont flag me ty
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();

                    String mimeType = getActivity().getContentResolver().getType(uri);
                    if (mimeType == null) {
                        String path = PdfSelectionUtils.getPath(getContext(), uri);
                        if (path == null) {
                            mFileName = uri.toString();
                        } else {
                            File file = new File(path);
                            mFileName = file.getName();
                        }
                    } else {
                        Uri returnUri = data.getData();
                        Cursor returnCursor = getActivity().getContentResolver().query(returnUri, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        mFileName = returnCursor.getString(nameIndex);
                        String size = Long.toString(returnCursor.getLong(sizeIndex));
                    }
                    File fileSave = getActivity().getExternalFilesDir(null);
                    String sourcePath = getActivity().getExternalFilesDir(null).toString();
                    try {
                        copyFileStream(new File(sourcePath + "/" + mFileName), uri, getContext());

                    } catch (Exception e) {
                        mParentFragmentListener.onPdfOpenError();
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    mParentFragmentListener.onPdfOpenError();
                    e.printStackTrace();
                }
            }
        }
    }

    private void copyFileStream(File dest, Uri uri, Context context)
            throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            // should be completed here
            mParentFragmentListener.onUrlSelected( dest.getPath() );

        } catch (Exception e) {
            mParentFragmentListener.onPdfOpenError();
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }
}
