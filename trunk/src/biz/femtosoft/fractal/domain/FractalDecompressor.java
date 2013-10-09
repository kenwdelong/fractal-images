package biz.femtosoft.fractal.domain;


/**
 * Uses a Fractal Image Model to decompress (expand) an image.
 * 
 * @author Ken DeLong
 * @version 1.0
 * @date March 24, 1998
 */

public class FractalDecompressor
{

	/**
	 * Creates a new destination image from the given inputs. This function
	 * should be iterated 8-16 times to totally reconstruct the original image.
	 * 
	 * @param pImageModel
	 *            The fractal image model to be used to decompress this image.
	 * @param pDestImage
	 *            The destination image to use. This image will be updated by
	 *            this routine.
	 * @param pPanelSize
	 *            The reconstruction panel size, in pixels. Does not need to be
	 *            the same as the compression panel size.
	 */
	public void getNextImage(FractalImageModel pImageModel, DestinationImage pDestImage,
			int pPanelSize)
	{
		// Create a reference image from the dest image
		// (like Ning Lu, we fix gamma at 0.75 - this value must be
		// the same as the one in FractalCompressor.prepareImages())
		ReferenceImage refImage = new ReferenceImage(pDestImage, FractalCompressor.GAMMA);

		// Prepare the reference and destination image
		refImage.prepareRefRegions(pPanelSize);
		pDestImage.prepareDestRegions(pPanelSize);

		// Loop over the DestImage regions (panels)
		int totalDestRegions = pDestImage.numberOfDestPanels();
		ImagePanel destRegion, refRegion;
		FractalCode fractalCode;
		SForm sForm;
		int x, y;
		double scaler = (1.0 * pPanelSize) / pImageModel.getPanelSize();
		for (int i = 0; i < totalDestRegions; i++)
		{
			destRegion = pDestImage.getDestPanelAt(i);
			fractalCode = pImageModel.getFractalCode(i);

			// Find the ref region - since the reference regions are
			// addressed by index, we need to figure out which one it is.
			x = (int)(scaler * fractalCode.getX() + 0.49);
			y = (int)(scaler * fractalCode.getY() + 0.49);
			if (x >= refImage.getXPanels())
				x = refImage.getXPanels() - 1;
			if (y >= refImage.getYPanels())
				y = refImage.getYPanels() - 1;
			refRegion = refImage.getRefRegion(x + y * refImage.getXPanels());

			// Get the SForm
			sForm = fractalCode.getSForm();
			sForm.prepare(pPanelSize);

			// Transform the reference image into the DestImage
			short val;
			for (x = 0; x < pPanelSize; x++)
				for (y = 0; y < pPanelSize; y++)
				{
					val = (short)(sForm.getTransformedPixel(x, y, refRegion) + fractalCode
							.getBeta());
					if (val < 0)
						destRegion.setPixel(x, y, (short)0);
					else if (val > 255)
						destRegion.setPixel(x, y, (short)0xFF);
					else
						destRegion.setPixel(x, y, val);
				}
		}
	}

}