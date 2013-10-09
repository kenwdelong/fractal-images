package biz.femtosoft.fractal.domain;


/**
 * The fractal code for an image transformation holds the ReferenceRegion, the
 * SForm index, and the Beta value. For this compressor, the Gamma value is
 * assumed at 0.75. It's easy to generalize the class to change this. The
 * destination region isn't included, as the FractalCodes are stored in order,
 * so that the destination region is implicit in the FractalImageModel.
 * 
 * @author Ken DeLong
 * @version 1.0
 * @date March 22, 1998
 */

class FractalCode implements java.io.Serializable
{

	// mX and mY could (should) be bytes (thus limiting the images
	// to 512X512 - an noone would use this implementation on anything
	// larger!), but bytes are interpreted as two's-complement in Java,
	// and I was too lazy to do the math conversion. Using byte would
	// result in much smaller fractal image model files.
	private short mX, mY;
	private byte mS;
	private short mBeta;
	private transient SFormList mSFormList;

	/**
	 * Standard constructor. A reference region is always constructed from a
	 * destination region.
	 * 
	 * @param pReferenceRegion
	 *            The reference region associated with this code
	 * @param pSForm
	 *            The SForm transformation matrix index.
	 * @param pBeta
	 *            The intensity offset Beta.
	 */
	FractalCode(ImagePanel pReferenceRegion, int pS, int pBeta)
	{
		mX = (short)pReferenceRegion.getX();
		mY = (short)pReferenceRegion.getY();
		mS = (byte)pS;
		mBeta = (short)pBeta;
	}

	/** Gets the x coordinate of the Reference Region. */
	int getX()
	{
		return mX;
	}

	/** Gets the y coordinate of the Reference Region. */
	int getY()
	{
		return mY;
	}

	/** Gets SForm. */
	SForm getSForm()
	{
		if (mSFormList == null)
			mSFormList = new SFormList();
		return mSFormList.getSForm((int)mS);
	}

	/** Gets Beta value. */
	int getBeta()
	{
		return mBeta;
	}
}