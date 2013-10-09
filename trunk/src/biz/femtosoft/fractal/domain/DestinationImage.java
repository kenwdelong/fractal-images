package biz.femtosoft.fractal.domain;

/**
 * Represents the destination image. The destination image is the image that is
 * being compressed (in the compression cycle), or the reconstructed image in
 * the expansion cycle.
 * 
 * This is the class that actually converts between BufferedImages and the program's 
 * MonochromeImage.
 * 
 * @author Ken DeLong
 * @version 2.0
 * @date May 11, 2005
 */

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;


public class DestinationImage extends MonochromeImage
{

	/**
	 * Constructs a DestinationImage from a java.awt.Image.
	 * 
	 * @param bufferedImage
	 *            The awt.Image from which to construct this image.
	 * @exception InterrupedException
	 *                Thrown by the java.awt.PixelGrabber if it's interrupted
	 *                when getting the pixels from the image.
	 */
	public DestinationImage(BufferedImage bufferedImage)
	{
		
		mWidth = bufferedImage.getWidth();
		mHeight = bufferedImage.getHeight();
		mImage = new short[mWidth][mHeight];

		for (int x = 0; x < mWidth; x++)
			for (int y = 0; y < mHeight; y++)
			{
				if(bufferedImage.getRaster().getNumBands() == 1)
				{
					short val = (short)bufferedImage.getRaster().getSample(x, y, 0);
					mImage[x][y] = val;
				}
				if(bufferedImage.getRaster().getNumBands() == 3)
				{
					// We can read color images, but we turn them into monochrome to compress
					// them. It shouldn't be too hard here to compress the red, green, and blue
					// channels separately.  I'd move this constructor logic into a static 
					// factory method and return an array of DestinationImages.
					int red, green, blue;
					red = bufferedImage.getRaster().getSample(x, y, 0);
					green = bufferedImage.getRaster().getSample(x, y, 1);
					blue = bufferedImage.getRaster().getSample(x, y, 2);
					mImage[x][y] = (short)((red + green + blue) / 3 + 0.49);					
				}

			}
	}

	/**
	 * Constructs a DestinationImage with all grey pixels (128) with the given
	 * width and height. This is used as the first image in the expansion cycle.
	 * 
	 * @param pWidth
	 *            The width of the image.
	 * @param pHeight
	 *            The height of the image.
	 */
	public DestinationImage(int pWidth, int pHeight)
	{
		mWidth = pWidth;
		mHeight = pHeight;
		mImage = new short[mWidth][mHeight];

		for (int x = 0; x < mWidth; x++)
			for (int y = 0; y < mHeight; y++)
				mImage[x][y] = (short)0x0080;
	}

	/**
	 * Gets the specified destination region. Image regions are numbered
	 * starting in the top left and going across and then down, so use i = x +
	 * y*width.
	 * 
	 * @param i
	 *            The index of the specified destination region.
	 * @returns The destination region.
	 */
	ImagePanel getDestPanelAt(int i)
	{
		return mPanels[i];
	}

	/** Returns the number of image regions. */
	int numberOfDestPanels()
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
	 * Creates the set of destination regions. Destination image regions do not
	 * overlap.
	 * 
	 * @param pPanelSize
	 *            The size of the panels to be used.
	 */
	void prepareDestRegions(int pPanelSize)
	{
		// First, make sure the size is an multiple of the panel size.
		adjustImageSize(pPanelSize);

		// How many panels?
		mXPanels = mWidth / pPanelSize;
		mYPanels = mHeight / pPanelSize;
		int numRegions = mXPanels * mYPanels;
		mPanels = new ImagePanel[numRegions];

		// Set up the image regions. Destination image regions do not
		// overlap. Loop over y in the outer loop so that the regions
		// go left to right, then top to bottom.
		int i = 0;
		for (int y = 0; y < mHeight; y += pPanelSize)
			for (int x = 0; x < mWidth; x += pPanelSize)
				mPanels[i++] = new ImagePanel(x, y, pPanelSize, this);
	}

	/**
	 * Assures that the image size is a multiple of the panel size, and that it
	 * has an even number of pixels.
	 * 
	 * @param pPanelSize
	 *            The panel size.
	 */
	private void adjustImageSize(int pPanelSize)
	{
		int newWidth = mWidth;
		while ((newWidth % pPanelSize != 0) || (newWidth % 2 != 0))
			newWidth--;
		int newHeight = mHeight;
		while ((newHeight % pPanelSize != 0) || (newHeight % 2 != 0))
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

	/**
	 * Gets the awt.Image associated with this destination image.
	 * 
	 * @returns The java.awt.Image constructed from this destination image.
	 */
	public BufferedImage getImage()
	{
		BufferedImage bi = new BufferedImage(mWidth, mHeight, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = bi.getRaster();

		// Set the pixels to the current image
		for (int x = 0; x < mWidth; x++)
			for (int y = 0; y < mHeight; y++)
				raster.setSample(x, y, 0, mImage[x][y]);

		return bi;
	}
}