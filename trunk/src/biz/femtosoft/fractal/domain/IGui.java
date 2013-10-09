/*
 * Created on May 11, 2005
 */
package biz.femtosoft.fractal.domain;

import java.awt.image.BufferedImage;

import biz.femtosoft.fractal.app.GuiController;



/**
 * @author ken.delong
 */
public interface IGui
{

	public void displayReferenceImage(BufferedImage bi);

	public void enableCompression();

	public void setCompressedFileName(String string);

	public void displayWarning(String message);

	public void displayCompressionResults(CompressionResults results);

	public void enableDecompression();

	public void displayUncompressionResults(BufferedImage image);

	public void setController(GuiController controller);

	public void updateCompressionProgress(double percent);

}
