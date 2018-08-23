package smr.sheetmusicreader.dashboard.pdflistfragment;

public class ListHandlerFactory {
    public FileListInterface createMobilefiedPdfListStrategy() {
        return new MobilefiedPdfList();
    }

    public FileListInterface createRawPdfListStrategy() {
        return new RawPdfList();
    }
}
