package biz.femtosoft.fractal.domain;

/**
 * A metric for measuring the distance between two pixels in an image. You can
 * select an L1 or L2 metric in the constructor.
 * 
 * @author Ken DeLong
 * @version 1.0
 * @date March 22, 1998
 */

public class Metric
{

	private int[] mMetricTable;

	/**
	 * Contructor. The constructor needs to know what metric to use, L1 or L2,
	 * as well as the maximum value of the pixels in the image.
	 * 
	 * @param pL
	 *            Select an L1 or L2 metric (pass 1 or 2). Default is 2
	 * @param pMaxDiff
	 *            The maximum value of the image pixels.
	 */
	Metric(int pL, int pMaxDiff)
	{
		mMetricTable = new int[(int)(1.51 * pMaxDiff)];
		int top = mMetricTable.length - 1;

		switch (pL)
		{
		case 1:
			for (int i = 0; i < top; i++)
				mMetricTable[i] = i;
			break;
		case 2:
		default:
			for (int i = 0; i < top; i++)
				mMetricTable[i] = i * i;
		}
	}

	/**
	 * Gets distance, using proper metric.
	 * 
	 * @param pDifference
	 *            The absolute difference in the pixels.
	 */
	int getDistance(int pDifference)
	{
		return mMetricTable[pDifference];
	}
}