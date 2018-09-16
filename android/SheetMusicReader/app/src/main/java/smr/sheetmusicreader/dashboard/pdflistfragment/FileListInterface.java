package smr.sheetmusicreader.dashboard.pdflistfragment;

/// Defines the methods that must be implemented to use the FileListFragment

import smr.sheetmusicreader.dashboard.pdflistfragment.viewmodels.PdfListItemViewModel;

public interface FileListInterface {
    void listItemClicked( PdfListItemViewModel aViewModel );
    void itemAddClicked();
}
