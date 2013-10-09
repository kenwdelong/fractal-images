package biz.femtosoft.fractal.domain;

/**
 * A superclass for the reference and destination images. This class assumes
 * 8-bit monochrome images.
 * 
 * This superclass is really only here for code-sharing; there's no polymorphic behavior in the
 * subclasses (ReferenceImage and DestinationImage).  The one that actually creates the pixels
 * and fills them in is DestinationImage.
 * 
 * @author Ken DeLong
 * @version 1.0
 * @date March 22, 1998
 */

abstract class MonochromeImage
{

	protected int mXPanels;
	protected int mYPanels;
	protected ImagePanel[] mPanels;
	protected short[][] mImage;
	protected int mWidth, mHeight;
	public static final int MAX_PIXEL_DEPTH = 256;

	/** Returns the width in pixels. */
	int getWidth()
	{
		return mWidth;
	}

	/** Returns the height in pixels */
	int getHeight()
	{
		return mHeight;
	}

	/**
	 * Gets the image value at the specified pixel.
	 * 
	 * @param pX
	 *            The x-coordinate of the specified pixel.
	 * @param pY
	 *            The y-coordinate of the specified pixel.
	 * @returns The image value at the specified pixel.
	 */
	short getPixel(int pX, int pY)
	{
		return mImage[pX][pY];
	}

	/**
	 * Sets the value of the given pixel.
	 * 
	 * @param pX
	 *            The x-coordinate of the pixel to change.
	 * @param pY
	 *            The y-coordinate of the pixel to change.
	 * @param pValue
	 *            The value to set the pixel to.
	 */
	void setPixel(int pX, int pY, short pValue)
	{
		mImage[pX][pY] = pValue;
	}

	/**
	 * Returns a reference to the image's pixel array. This breaks encapsulation
	 * but is necessary for speed's sake. If this were Eiffel we could export it
	 * only to ImageRegion class.
	 */
	short[][] getPixels()
	{
		// had to do this for speed's sake, although it's not good design.
		return mImage;
	}

}