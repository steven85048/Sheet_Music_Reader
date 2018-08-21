package smr.sheetmusicreader;

/// View Model for list related activities; used by the fragment as a bindable source

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.ArrayList;

public class FileListViewModel extends BaseObservable {

    // DATA
    private ArrayList<String> mFileUrls;
    private String mFileListName;

    public FileListViewModel(String aFileListName, ArrayList<String> aFileUrls) {
        mFileUrls = aFileUrls;
        mFileListName = aFileListName;
    }

    // BINDABLES

    @Bindable
    public String getFileListName() {
        if ( mFileListName == null ) {
            mFileListName = "";
        }

        return mFileListName;
    }

    @Bindable
    public ArrayList<String> getFileUrls() {
        if ( mFileUrls == null ) {
            mFileUrls = new ArrayList<String>();
        }

        return mFileUrls;
    }

    // SETTERS
    public void setFileListName( final String aFileListName ) {
        mFileListName = aFileListName;
        notifyPropertyChanged(smr.sheetmusicreader.BR.fileListName);
    }

    public void setFileUrls( ArrayList<String> aFileUrls) {
        mFileUrls = aFileUrls;
        notifyPropertyChanged(smr.sheetmusicreader.BR.fileUrls);
    }
}
