package smr.sheetmusicreader.dashboard.pdflistfragment;

/// Manager class for the handling of the recycler view

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import smr.sheetmusicreader.dashboard.pdflistfragment.FileListViewModel;
import smr.sheetmusicreader.dashboard.pdflistfragment.PdfListAdapter;

public class PdfListManager {
    //-------------------------------------------------------------
    // INSTANCE VARIABLES
    //-------------------------------------------------------------

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    FileListViewModel mViewModel;

    /// @param ( aRecyclerView ) : RecyclerView that this class should handle
    public PdfListManager( RecyclerView aRecyclerView, Context aContext, FileListViewModel aViewModel ) {
        // Initialize recycler view and vm
        mRecyclerView = aRecyclerView;
        mViewModel = aViewModel;

        // Set the layout manager
        mLayoutManager = new LinearLayoutManager(aContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set the adapter to obtain data from
        mRecyclerViewAdapter = new PdfListAdapter( mViewModel );
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        // The list elements should not change size (as of now)
        mRecyclerView.setHasFixedSize(true);

    }
}
