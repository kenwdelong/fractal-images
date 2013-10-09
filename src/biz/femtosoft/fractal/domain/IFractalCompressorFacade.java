/*
 * Created on May 11, 2005
 */
package biz.femtosoft.fractal.domain;

import java.awt.image.BufferedImage;
import java.io.IOException;



/**
 * @author ken.delong
 */
public interface IFractalCompressorFacade
{

	public BufferedImage openFile(String referenceFileName) throws IOException;

	public CompressionResults compress(String compressedFileName, int panelSize, IFractalCompressorListener listener);

	public void uncompress(String compressedFileName, int panelSize, IUncompressionListener listener) throws IOException;


}
