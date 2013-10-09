/*
 * Created on May 11, 2005
 */
package biz.femtosoft.fractal.domain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.easymock.MockControl;

import junit.framework.TestCase;


/**
 * @author ken.delong
 */
public class FractalCompressorFacadeTest extends TestCase
{

	private FractalCompressorFacade compressor;
	private String outFileName;
	private int panelSize;

	protected void setUp() throws Exception
	{
		compressor = new FractalCompressorFacade();
		outFileName = "test.fcp";
		panelSize = 4;
	}

	public void testOpenFile() throws IOException
	{
		BufferedImage bi = compressor.openFile("images/Michelle.jpg");
		assertNotNull("Bi is null", bi);
		assertEquals("Image not BW", bi.getType(), BufferedImage.TYPE_BYTE_GRAY);
		System.out.println(bi.getRaster().getNumBands());
	}
	
	public void testCompress() throws IOException
	{
		String inFileName = "images/eye.jpg";
		compressor.openFile(inFileName);
		compressor.compress(outFileName, panelSize, null);
		CompressionResults results = compressor.getCompressionResults();
		assertNotNull("No results!", results);
		assertEquals("Wrong infile", inFileName, results.inputFileName);
		assertEquals("wrong outfile", outFileName, results.compressedFileName);
		File outFile = new File(outFileName);
		assertTrue("No file written", outFile.exists());
		assertTrue("no input lenght", results.inputFileLength > 0);
		assertTrue("no output length", results.compressedFileLength > 0);
		assertTrue("Bad compression", (results.percentCompression > 0) && (results.percentCompression < 100));
	}
	
	public void testUncompress() throws IOException
	{
		MockControl control = MockControl.createStrictControl(IUncompressionListener.class);
		IUncompressionListener listener = (IUncompressionListener) control.getMock();
		listener.imageReady(null);
		control.setMatcher(MockControl.ALWAYS_MATCHER);
		control.setVoidCallable(FractalCompressorFacade.NUM_UNCOMPRESS_ITERATIONS + 1);
		control.replay();
		
		compressor.uncompress(outFileName, panelSize, listener);
		
		control.verify();
	}

}
