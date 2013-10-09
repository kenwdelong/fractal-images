package biz.femtosoft.fractal.domain;

/**
 * Contains the list of the SForms being used in this compression. Also knows a
 * list of default SForms.
 * 
 * @author Ken DeLong
 * @version 1.0
 * @date March 22, 1998
 */

public class SFormList
{

	private int mNumberOfSForms;

	private SForm[] mSFormList;

	/** Default constructor sets list of SForms to the default. */
	SFormList()
	{
		setDefaultList();
	}

	/**
	 * Sets a default list of SForms. For the simple fractal compressor, use the
	 * 8 isometric SForms.
	 */
	private void setDefaultList()
	{
		mNumberOfSForms = 8;
		mSFormList = new SForm[8];

		// From p.37 of Ning Lu's book
		mSFormList[0] = new SForm(1, 0, 0, 1, 0.5);
		mSFormList[1] = new SForm(-1, 0, 0, 1, 0.5);
		mSFormList[2] = new SForm(1, 0, 0, -1, 0.5);
		mSFormList[3] = new SForm(-1, 0, 0, -1, 0.5);
		mSFormList[4] = new SForm(0, 1, 1, 0, 0.5);
		mSFormList[5] = new SForm(0, -1, 1, 0, 0.5);
		mSFormList[6] = new SForm(0, 1, -1, 0, 0.5);
		mSFormList[7] = new SForm(0, -1, -1, 0, 0.5);
	}

	/** Gets the number of SForms in the list */
	int getNumberOfSForms()
	{
		return mNumberOfSForms;
	}

	/** Gets the specified SForm. */
	SForm getSForm(int i)
	{
		return mSFormList[i];
	}
	
	public void prepareAllSForms(int panelSize)
	{
		for (int i = 0; i < mSFormList.length; i++)
		{
			SForm sform = mSFormList[i];
			sform.prepare(panelSize);
		}
	}

	public SForm[] getSFormArray()
	{
		return mSFormList;
	}
}