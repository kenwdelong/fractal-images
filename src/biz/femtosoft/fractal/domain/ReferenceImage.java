package biz.femtosoft.fractal.domain;


/**
 * Represents the reference image in the compression cycle. I decided to make
 * this a separate class from the destination image because the behaviors are
 * quite different.
 * 
 * @author Ken DeLong
 * @version 1.0
 * @date March 22, 1998
 */

class ReferenceImage extends MonochromeImage
{

	/**
	 * Creates the reference image from the given destination image.
	 * 
	 * @param pDestImage
	 *            The destination image serving as the source.
	 * @pGamma The contrast factor gamma to apply to the reference image.
	 */
	ReferenceImage(DestinationImage pDestImage, double pGamma)
	{
		mWidth = pDestImage.getWidth() / 2;
		mHeight = pDestImage.getHeight() / 2;
		mImage = new short[mWidth][mHeight];

		// The reference image gets shrunk by a factor of two, because
		// we are limiting ourselves to SForms with a contractivity of 2.
		// Also, we pre-multiply by the Gamma factor to save time.
		int val, xx, yy;
		for (int x = 0; x < mWidth; x++)
		{
			for (int y = 0; y < mHeight; y++)
			{
				xx = 2 * x;
				yy = 2 * y;
				val = pDestImage.getPixel(xx, yy) + pDestImage.getPixel(xx + 1, yy)
						+ pDestImage.getPixel(xx, yy + 1) + pDestImage.getPixel(xx + 1, yy + 1);
				
				mImage[x][y] = (short)((val * pGamma / 4) + 0.49);
			}
		}
	}

	/** Returns the number of image regions. */
	int numberOfRefPanels()
	{
		return mXPanels * mYPanels;
	}

	/** Returns the number of panels (regions) in the x direction. */
	public int getXPanels()
	{
		return mXPanels;
	}

	/** Returns the number of panels (regions) in the y direction. */
	public int getYPanels()
	{
		return mYPanels;
	}

	/**
	 * Creates the set of reference regions. All possible regions of the
	 * specified size are used.
	 * 
	 * @param pPanelSize
	 *            The size of the panels to be used.
	 */
	void prepareRefRegions(int pPanelSize)
	{
		// Make sure it's even.
		//adjustImageSize(); --> It doesn't need to be even!!!!

		// Calculate the number of reference regions
		mXPanels = mWidth - (pPanelSize - 1);
		mYPanels = mHeight - (pPanelSize - 1);
		int numRegions = mXPanels * mYPanels;
		mPanels = new ImagePanel[numRegions];

		// Create the reference regions.
		int i = 0;
		for (int y = 0; y < mYPanels; y++)
			for (int x = 0; x < mXPanels; x++)
				mPanels[i++] = new ImagePanel(x, y, pPanelSize, this);
	}

	/** Returns the selected reference region. */
	ImagePanel getRefRegion(int i)
	{
		return mPanels[i];
	}

	/** Makes sure the image is an even size. */
	private void adjustImageSize()
	{
		int newWidth = mWidth;
		if (newWidth % 2 != 0)
			newWidth--;
		int newHeight = mHeight;
		if (newHeight % 2 != 0)
			newHeight--;

		// If it's different, truncate the image. I like this better than
		// padding with zeros. A few pixels shouldn't matter.
		if ((newWidth != mWidth) || (newHeight != mHeight))
		{
			short[][] newImage = new short[newWidth][newHeight];
			for (int x = 0; x < newWidth; x++)
				for (int y = 0; y < newHeight; y++)
					newImage[x][y] = mImage[x][y];

			// feed the old image to the garbage collector.
			mImage = newImage;
			mWidth = newWidth;
			mHeight = newHeight;
		}
	}

}