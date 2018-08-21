package smr.sheetmusicreader;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import smr.sheetmusicreader.databinding.PdfListElementBinding;

public class PdfListAdapter extends RecyclerView.Adapter<PdfListAdapter.ViewHolder> {

    //-------------------------------------------------------------
    // INSTANCE VARIABLES AND TYPES
    //-------------------------------------------------------------

    // Data class holding the necessary data
    FileListViewModel mViewModel;
    List< PdfListItemViewModel > mPdfListItemViewModels;
    private LayoutInflater mLayoutInflater;

    // Contains the views that are rendered for each list element of the recycler view
    // Also contains a generic binding class for xml binding
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private PdfListElementBinding mBinding;

        public ViewHolder(final PdfListElementBinding aBinding) {
            super(aBinding.getRoot());
        };
    }

    public PdfListAdapter( FileListViewModel aViewModel ) {
        mViewModel = aViewModel;
        initializePdfList();

        // TODO: Also set this class to observe changes in the view model
    }

    // Initializes the view model for each list item using the url arraylist from the enclosing view model
    public List< PdfListItemViewModel > initializePdfList() {
        ArrayList<String> theUrlList = mViewModel.getFileUrls();
        mPdfListItemViewModels = new ArrayList< PdfListItemViewModel >();

        for ( String theUrl : theUrlList) {
            PdfListItemViewModel theItem = new PdfListItemViewModel();
            theItem.setItemName(theUrl);

            mPdfListItemViewModels.add( theItem );
        }

        return mPdfListItemViewModels;
    }

    //-------------------------------------------------------------
    // RECYCLER VIEW ADAPTER OVERRIDES
    //-------------------------------------------------------------

    @Override
    public PdfListAdapter.ViewHolder onCreateViewHolder(ViewGroup aParent, int aViewType) {
        // Initialize the inflater that will be used to display the xml in the parent viewgroup
        if ( mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(aParent.getContext());
        }

        // Inflate the layout and retrieve the associated binding class; this class does not have the correct data until the onBindViewHolder call
        PdfListElementBinding theBinding = DataBindingUtil.inflate( mLayoutInflater, R.layout.pdf_list_element, aParent, false );

        // Pass the binding class to the
        return new ViewHolder(theBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder aViewHolder, int aPosition) {

        // Attach the data class to the binding so that the view uses the correct data
        aViewHolder.mBinding.setViewmodel( mPdfListItemViewModels.get(aPosition) );

        // TODO: define onClick functionality here
    }

    @Override
    public int getItemCount() {
        return mPdfListItemViewModels.size();
    }
}
