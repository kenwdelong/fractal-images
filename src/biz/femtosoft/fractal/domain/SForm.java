package biz.femtosoft.fractal.domain;


/**
 * The S-Form matrix of coefficients. These are always 2 X 2.
 * 
 * The getTransformedPixel() method is one of the hotspots.  Optimize if you can.
 * 
 * @author Ken DeLong
 * @version 1.0
 * @date March 21, 1998
 */

public class SForm
{

	private double mContractivity;
	private int mA, mB, mC, mD;
	private int[] mXCoords, mYCoords;
	private int mPanelSize;
	private boolean isPrepared = false;

	/**
	 * Constructor.
	 * 
	 * @param pA
	 *            A coefficient
	 * @param pB
	 *            B coefficient
	 * @param pC
	 *            C coefficient
	 * @param pD
	 *            D coefficient
	 * @param pContractivity
	 *            Contractivity
	 */
	SForm(int pA, int pB, int pC, int pD, double pContractivity)
	{
		mA = pA;
		mB = pB;
		mC = pC;
		mD = pD;
		mContractivity = pContractivity;
	}

	/** Returns A coefficient */
	int getA()
	{
		return mA;
	}

	/** Returns B coefficient */
	int getB()
	{
		return mB;
	}

	/** Returns C coefficient */
	int getC()
	{
		return mC;
	}

	/** Returns D coefficient */
	int getD()
	{
		return mD;
	}

	/** Returns contractivity */
	double getContractivity()
	{
		return mContractivity;
	}

	/**
	 * Returns the pixel value for the reference region pixel that transforms to
	 * the specified coordinates in the destination region. This is done for
	 * performance considerations.
	 * 
	 * @param pX
	 *            The x-coordinate of the destination region pixel.
	 * @param pY
	 *            The y-coordinate of the destination region pixel.
	 * @param pRefRegion
	 *            The reference region to transform.
	 * @returns The pixel value from the transformed reference region.
	 */
	short getTransformedPixel(int pX, int pY, ImagePanel pRefRegion)
	{
		int index = pX + pY * mPanelSize;
		return pRefRegion.getPixel(mXCoords[index], mYCoords[index]);
	}

	/**
	 * Prepares the SForm for a specific panel size.
	 * 
	 * @param pPanelSize
	 *            The size of the panel we are using.
	 */
	// In order to avoid doing the ABCD matrix calculation every time
	// we need another pixel, we store the source pixels in the
	// reference region for a given destination pixel in the destination
	// region. This is not as slick as Ning Lu's fancy pointer
	// manipulation in the book, but hopefully it's more transparent
	// to the novice user. Enthusiastic users can optimize the code.
	// Left as an exercise for the reader.
	void prepare(int pPanelSize)
	{
		if(isPrepared)
			return;
		mXCoords = new int[pPanelSize * pPanelSize];
		mYCoords = new int[pPanelSize * pPanelSize];
		mPanelSize = pPanelSize;

		for (int x = 0; x < pPanelSize; x++)
		{
			for (int y = 0; y < pPanelSize; y++)
			{
				int index = x + y * pPanelSize;
				if (mA == 1)
					mXCoords[index] = x;
				else if (mA == -1)
					mXCoords[index] = pPanelSize - 1 - x;

				if (mB == 1)
					mXCoords[index] = y;
				else if (mB == -1)
					mXCoords[index] = pPanelSize - 1 - y;

				if (mC == 1)
					mYCoords[index] = x;
				else if (mC == -1)
					mYCoords[index] = pPanelSize - 1 - x;

				if (mD == 1)
					mYCoords[index] = y;
				else if (mD == -1)
					mYCoords[index] = pPanelSize - 1 - y;
			}
		}
		isPrepared = true;
	}

}