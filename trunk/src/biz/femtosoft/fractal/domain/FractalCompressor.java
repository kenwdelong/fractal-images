package biz.femtosoft.fractal.domain;


/**
 * Compresses the images: it finds the FractalCodes that best characterize the
 * destination image. This implements the simplest compressor, detailed in
 * Chapter 3 of Ning Lu's book.
 * 
 * @author Ken DeLong
 * @version 1.0
 * @date March 22, 1998
 */

public class FractalCompressor implements Runnable
{

	private int mPanelSize;
	private ReferenceImage mRefImage;
	private DestinationImage mDestImage;
	private SFormList mSFormList = new SFormList();
	private FractalImageModel mFractalImageModel;
	private double mPercentDone;
	private boolean mCompressorRunning;
	private IFractalCompressorListener listener;
	
	public static final double GAMMA = 0.75;

	public FractalCompressor(DestinationImage dimage, int panelSize)
	{
		mDestImage = dimage;
		mPanelSize = panelSize;
	}
	
	/**
	 * Sets the panel size. Panel size must be set before the compression
	 * begins.
	 */
	public synchronized void setPanelSize(int pPanelSize)
	{
		mPanelSize = pPanelSize;
	}

	/** Sets destination image */
	public synchronized void setDestImage(DestinationImage pDestImage)
	{
		mDestImage = pDestImage;
	}

	/** The compressor can run in its own thread. */
	public void run()
	{
		System.out.println("Compressor thread started.");
		compress();
	}

	/**
	 * Begins the compression to find the fractal image model. The panel size
	 * and destination image must be set first.
	 */
	synchronized void compress()
	{
		int error, beta, s, x, y, diff;
		ImagePanel destRegion, refRegion, bestRegion;
		SForm tSForm;
		Metric tMetric;
		int bestError, bestBeta, bestSForm;

		mCompressorRunning = true;

		// Get an L2 metric
		tMetric = new Metric(2, mDestImage.MAX_PIXEL_DEPTH);
		bestSForm = bestBeta = 0;
		bestRegion = null;

		// initialization
		prepareImages();
		prepareSForms();
		int numDestRegions = mDestImage.numberOfDestPanels();
		int numRefRegions = mRefImage.numberOfRefPanels();
		mFractalImageModel = new FractalImageModel(mDestImage.getXPanels(),
				mDestImage.getYPanels(), mDestImage.getDestPanelAt(0).getPanelSize());

		int numSForms = mSFormList.getNumberOfSForms();
		SForm[] sforms = mSFormList.getSFormArray();
		// Loop over destination regions
		for (int numDest = 0; numDest < numDestRegions; numDest++)
		{
			destRegion = mDestImage.getDestPanelAt(numDest);
			bestError = 0x7FFFFFFF;

			// Loop over reference regions
			for (int numRef = 0; numRef < numRefRegions; numRef++)
			{
				refRegion = mRefImage.getRefRegion(numRef);

				// Difference in means
				beta = destRegion.getMean() - refRegion.getMean();

				// Loop over SForms
				for (s = 0; s < numSForms; s++)
				{
					error = 0;
					//tSForm = mSFormList.getSForm(s);
					tSForm = sforms[s];
					for (x = 0; x < mPanelSize; x++)
					{
						for (y = 0; y < mPanelSize; y++)
						{
							diff = destRegion.getPixel(x, y)
									- tSForm.getTransformedPixel(x, y, refRegion);
							error += tMetric.getDistance(Math.abs(diff - beta));
						}
					}
					if (error < bestError)
					{
						bestError = error;
						bestSForm = s;
						bestBeta = beta;
						bestRegion = refRegion;
					}
				} //  for s
				if (bestError == 0)
					break;
			} // for refRegion

			// Now we have the best Fractal Code for the destRegion.
//			System.out.println(bestRegion.getX() + " " + bestRegion.getY() + " " + bestSForm + " "
//					+ bestBeta);
			mFractalImageModel.addFractalCode(new FractalCode(bestRegion, bestSForm, bestBeta));
			mPercentDone = (100.0 * (numDest + 1)) / numDestRegions;
			if(listener != null)
				listener.updateProgress(mPercentDone);
		} // for destRegion

		mCompressorRunning = false;
	}

	/**
	 * Prepares the images for the compression routine. This creates the
	 * Reference Image from the Destination Image, and tells the images to set
	 * up their ImageRegions. A default value of 3/4 is used for Gamma, the
	 * contrast factor.
	 */
	private synchronized void prepareImages()
	{
		mDestImage.prepareDestRegions(mPanelSize);
		mRefImage = new ReferenceImage(mDestImage, GAMMA);
		mRefImage.prepareRefRegions(mPanelSize);
	}

	/** Returns the fractal image model for the just-compressed image. */
	public synchronized FractalImageModel getFractalImageModel()
	{
		return mFractalImageModel;
	}

	/** Prepare the SForms for the compression. */
	synchronized void prepareSForms()
	{
		mSFormList.prepareAllSForms(mPanelSize);
	}

	/**
	 * Returns the percent of destination regions matched. Not synchronized so
	 * that we can monitor progress of the compression.
	 */
	public double getPercentDone()
	{
		return mPercentDone;
	}

	/**
	 * Checks if the compressor is running.
	 * 
	 * @returns True if compressor is running.
	 */
	public boolean compressorRunning()
	{
		return mCompressorRunning;
	}
	
	public void setListener(IFractalCompressorListener listener)
	{
		this.listener = listener;
	}

}