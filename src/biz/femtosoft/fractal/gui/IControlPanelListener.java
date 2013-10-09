/*
 * Created on May 12, 2005
 */
package biz.femtosoft.fractal.gui;

import java.io.File;


/**
 * @author ken.delong
 */
public interface IControlPanelListener
{

	public void fileToCompressSelected(File fileToCompress);

	public void compressPressed();

	public void uncompressPressed();

}
