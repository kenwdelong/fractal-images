/*
 * Created on May 11, 2005
 */
package biz.femtosoft.fractal.app;

import java.awt.image.BufferedImage;
import java.io.IOException;

import junit.framework.TestCase;

import org.easymock.MockControl;

import biz.femtosoft.fractal.domain.CompressionResults;
import biz.femtosoft.fractal.domain.IFractalCompressorFacade;
import biz.femtosoft.fractal.domain.IGui;


/**
 * For the new classes I developed, I tried to go TDD.  Most of the stuff, however, is legacy from
 * 1998 when I first wrote this.
 * @author ken.delong
 */
public class GuiControllerTest extends TestCase
{

	private GuiController controller;
	private IFractalCompressorFacade compressor;
	private MockControl compressorControl;
	private MockControl guiControl;
	private IGui gui;
	private int panelSize;

	protected void setUp() throws Exception
	{		
		compressorControl = MockControl.createControl(IFractalCompressorFacade.class);
		compressor = (IFractalCompressorFacade)compressorControl.getMock();
		
		guiControl = MockControl.createControl(IGui.class);
		gui = (IGui) guiControl.getMock();
		
		controller = new GuiController(compressor, gui);
		panelSize = 4;
		
		guiControl.reset();
	}
	
	public void testOpenFile() throws IOException
	{
		String referenceFileName = "myfile.jpg";
		BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_GRAY);
		
		// Set mock expectations
		compressor.openFile(referenceFileName);
		compressorControl.setReturnValue(bi);
		
		gui.displayReferenceImage(bi);
		gui.enableCompression();
		gui.setCompressedFileName("myfile.fcp");
		
		compressorControl.replay();
		guiControl.replay();
		
		// run the scenario
		controller.openFile(referenceFileName);
		
		compressorControl.verify();
		guiControl.verify();
	}
	
	public void testFileError() throws IOException
	{
		// Set mock expectations
		compressor.openFile("bogusFileName");
		compressorControl.setThrowable(new IOException());
		
		gui.displayWarning("junk");
		guiControl.setMatcher(MockControl.ALWAYS_MATCHER);

		compressorControl.replay();
		guiControl.replay();
		
		// run the scenario
		controller.openFile("bogusFileName");
		
		compressorControl.verify();
		guiControl.verify();
	}
	
	public void testCompress() throws InterruptedException
	{
		String compressedFileName = "myfile.fcp";
		CompressionResults results = new CompressionResults();
		
		// Set mock expectations
		compressor.compress(compressedFileName, panelSize, null);
		compressorControl.setMatcher(MockControl.ALWAYS_MATCHER);
		compressorControl.setReturnValue(results);
		
		gui.displayCompressionResults(results);
		gui.enableDecompression();
		
		compressorControl.replay();
		guiControl.replay();
		
		// run the scenario
		controller.compress(compressedFileName, panelSize);
		
		// The controller uses SwingWorker now, so it takes a little time to start the thread and
		// actually do the work.  200ms seems to be enough to make the test pass on my box.
		Thread.sleep(300);
		
		compressorControl.verify();
		guiControl.verify();
		
	}

	public void testUncompress() throws InterruptedException, IOException
	{
		String compressedFileName = "myfile.fcp";
		
		// Set mock expectations
		compressor.uncompress(compressedFileName, panelSize, null);
		compressorControl.setMatcher(MockControl.ALWAYS_MATCHER);
		compressorControl.setVoidCallable(1);
		
		//gui.displayUncompressionResults(null);
//		guiControl.setMatcher(MockControl.ALWAYS_MATCHER);
//		guiControl.setVoidCallable(FractalCompressorFacade.NUM_UNCOMPRESS_ITERATIONS + 1);
		
		compressorControl.replay();
		//guiControl.replay();
		
		// run the scenario
		controller.uncompress(compressedFileName, panelSize);
		
		//Same note as testCompress
		Thread.sleep(200);
		
		compressorControl.verify();
		//guiControl.verify();
		
	}

}
