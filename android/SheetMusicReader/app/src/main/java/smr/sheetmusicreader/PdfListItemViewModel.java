package smr.sheetmusicreader;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

public class PdfListItemViewModel extends BaseObservable {
    String mItemName;

    public PdfListItemViewModel() {
        mItemName = "";
    }

    @Bindable
    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String aImageName) {
        this.mItemName = aImageName;
    }
}
