package biz.femtosoft.fractal.domain;


/**
 * Represents a region of an image. This class is basically an offset into a
 * regular image. It maintains the mean as well. 
 * 
 * One of the hotspots of the program is the getPixel() method.  Optimize this and you'll get
 * a 20% increase in speed. 
 * 
 * @author Ken DeLong
 * @version 1.0
 * @date March 21, 1998
 */
public class ImagePanel
{

	private int xOrigin, yOrigin; // The origin of the region

	private int mMean, mPanelSize; // the mean and size

	private MonochromeImage mImage;

	private short[][] originalPixels;
	
	// keeping both of these so that you can have fun trying to optimize this routine.
	private short[] panelPixels;
	private short[][] panelPixels2D;

	/**
	 * Constructor. Sets the region to have an origin of pX, pY, and to have a
	 * size of N X N pixels.
	 * 
	 * @param pX
	 *            The X-origin of the region in the original image.
	 * @param pY
	 *            The Y-origin of the region in the original image.
	 * @param pPanelSize
	 *            The size of the region (along one axis), in pixels.
	 */
	ImagePanel(int pX, int pY, int pPanelSize, MonochromeImage pImage)
	{
		xOrigin = pX;
		yOrigin = pY;
		mPanelSize = pPanelSize;
		mImage = pImage;
		originalPixels = pImage.getPixels();
		panelPixels = new short[mPanelSize*mPanelSize];
		panelPixels2D = new short[mPanelSize][mPanelSize];

		// Get the mean
		int sum = 0;
		int i = 0;
		for (int x = 0; x < mPanelSize; x++)
		{
			for (int y = 0; y < mPanelSize; y++)
			{
				//sum += getPixel(x, y);
				sum += originalPixels[x + xOrigin][y + yOrigin];
				panelPixels[i++] = originalPixels[x + xOrigin][y + yOrigin];
				panelPixels2D[x][y] = originalPixels[x + xOrigin][y + yOrigin];
			}
		}
		mMean = (short)(sum / (mPanelSize * mPanelSize) + 0.49);
	}

	/** Returns region x origin. */
	int getX()
	{
		return xOrigin;
	}

	/** Returns region y origin. */
	int getY()
	{
		return yOrigin;
	}

	/** Returns region size. */
	int getPanelSize()
	{
		return mPanelSize;
	}

	/** Returns intensity mean of all pixels in the region */
	int getMean()
	{
		return mMean;
	}

	/**
	 * Returns the pixel at the specified pixel in the region.
	 * 
	 * @param pX
	 *            The x-coordinate of the specified pixel.
	 * @param pY
	 *            The y-coordinate of the specified pixel.
	 * @returns The pixel value at the specified pixel.
	 */
	public short getPixel(int pX, int pY)
	{
		//return mImage.getPixel(pX + xOrigin, pY + yOrigin);
		// I picked up a 30% increase in speed for the compressor
		// by eliminating the above function call.
		
		// profiler says ~585 u-sec/call, overall time to compress eye is ~40sec
		//return originalPixels[pX + xOrigin][pY + yOrigin];
		
		// profiler says ~575 u-sec/call, overall time ~40 sec
		//return panelPixels[pX + pY*mPanelSize];
		
		// profiler says ~488 u-sec/call
		return panelPixels2D[pX][pY];
	}

	/**
	 * Sets the value of the pixel in the parent image.
	 * 
	 * @param pX
	 *            The x-coordinate in this region of the pixel to set.
	 * @param pY
	 *            The y-coordinate in this region of the pixel to set.
	 * @param pValue
	 *            The value to set the pixel to.
	 */
	void setPixel(int pX, int pY, short pValue)
	{
		mImage.setPixel(pX + xOrigin, pY + yOrigin, pValue);
	}
}