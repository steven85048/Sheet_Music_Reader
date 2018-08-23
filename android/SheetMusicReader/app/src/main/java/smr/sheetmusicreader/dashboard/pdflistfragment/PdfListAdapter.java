package smr.sheetmusicreader.dashboard.pdflistfragment;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import smr.sheetmusicreader.R;
import smr.sheetmusicreader.dashboard.pdflistfragment.viewmodels.FileListViewModel;
import smr.sheetmusicreader.dashboard.pdflistfragment.viewmodels.PdfListItemViewModel;
import smr.sheetmusicreader.databinding.PdfListElementBinding;

public class PdfListAdapter extends RecyclerView.Adapter<PdfListAdapter.ViewHolder> {

    //-------------------------------------------------------------
    // INSTANCE VARIABLES AND TYPES
    //-------------------------------------------------------------

    // Data class holding the necessary data
    FileListViewModel mViewModel;
    List<PdfListItemViewModel> mPdfListItemViewModels;

    private LayoutInflater mLayoutInflater;

    // Provides the specific implementation of the list events
    private FileListInterface mListHandlerStrategy;

    // Contains the views that are rendered for each list element of the recycler view
    // Also contains a generic binding class for xml binding
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private PdfListElementBinding mBinding;

        public ViewHolder(final PdfListElementBinding aBinding) {
            super(aBinding.getRoot());
            mBinding = aBinding;
        };
    }

    public PdfListAdapter( FileListViewModel aViewModel, FileListInterface aListHandlerStrategy ) {

        mViewModel = aViewModel;
        mListHandlerStrategy = aListHandlerStrategy;

        initializePdfList();

        // TODO: Also set this class to observe changes in the view model
    }

    // Initializes the view model for each list item using the url arraylist from the enclosing view model
    public void initializePdfList() {
        ArrayList<String> theUrlList = mViewModel.getFileUrls();
        mPdfListItemViewModels = new ArrayList<PdfListItemViewModel>();

        for ( String theUrl : theUrlList) {
            System.out.println(theUrl);

            PdfListItemViewModel theItem = new PdfListItemViewModel();
            theItem.setItemName(theUrl);

            mPdfListItemViewModels.add( theItem );
        }
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
        if( aViewHolder.mBinding != null)
            aViewHolder.mBinding.setViewmodel( mPdfListItemViewModels.get(aPosition) );

        // TODO: define onClick functionality here
        aViewHolder.mBinding.cardView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View aView) {
                mListHandlerStrategy.listItemClicked();
                Log.e("View clicked", "POSITION: aPosition");
            };
        });
    }

    @Override
    public int getItemCount() {
        return mPdfListItemViewModels.size();
    }
}
