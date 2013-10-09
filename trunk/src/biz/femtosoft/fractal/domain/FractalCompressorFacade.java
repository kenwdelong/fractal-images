/*
 * Created on May 11, 2005
 */
package biz.femtosoft.fractal.domain;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;



/**
 * This is the entry point to the domain layer.  It fronts the compressor and decompressor, and
 * manages the workflow (saving and reading files, etc.).
 * @author ken.delong
 */
public class FractalCompressorFacade implements IFractalCompressorFacade
{
	private String refFileName;
	private BufferedImage bi;
	private CompressionResults results;
	public static final int NUM_UNCOMPRESS_ITERATIONS = 16;

	public BufferedImage openFile(String referenceFileName) throws IOException
	{
		refFileName = referenceFileName;
		bi = ImageIO.read(new File(referenceFileName));
		if(bi == null)
			throw new IOException("Cannot open file.");
		return bi;
	}

	public CompressionResults compress(String compressedFileName, int panelSize, IFractalCompressorListener listener)
	{
		DestinationImage di = null;
		di = new DestinationImage(bi);
		FractalCompressor compressor = new FractalCompressor(di, panelSize);
		compressor.setListener(listener);
		long start = System.currentTimeMillis();
		compressor.compress();
		long stop = System.currentTimeMillis();
		results = new CompressionResults();
		results.compressedFileName = compressedFileName;
		results.inputFileName = refFileName;
		try
		{
			writeOutputFile(compressedFileName, compressor.getFractalImageModel());
			File in = new File(refFileName);
			results.inputFileLength = in.length();
			File out = new File(compressedFileName);
			results.compressedFileLength = out.length();
			results.percentCompression = 100.0 - 100.0*results.compressedFileLength/results.inputFileLength;
			results.elapsedTime = (int)(stop - start);
		}
		catch (IOException e1)
		{
			// This would probably indicate a failure to save the file.  Let's just log the
			// error and then return results without a saved filename.
			results.compressedFileName = "File save possibly failed.";
			e1.printStackTrace();
		}
		return results;
	}

	private void writeOutputFile(String compressedFileName, FractalImageModel fractalImageModel) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(compressedFileName);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		GZIPOutputStream gos = new GZIPOutputStream(bos);
		ObjectOutputStream oos = new ObjectOutputStream(gos);
		oos.writeObject(fractalImageModel);
		oos.close();
	}

	public void uncompress(String compressedFileName, int panelSize, IUncompressionListener listener) throws IOException 
	{
		FractalDecompressor decompressor = new FractalDecompressor();
		try
		{
			FractalImageModel fim = loadFractalImageModel(compressedFileName);
			int width = fim.getXPanels()*panelSize;
			int height = fim.getYPanels()*panelSize;
			DestinationImage destImage = new DestinationImage(width, height);
			if(listener != null)
				listener.imageReady(destImage.getImage());
			for(int i = 0; i < NUM_UNCOMPRESS_ITERATIONS; i++)
			{
				decompressor.getNextImage(fim, destImage, panelSize);
				if(listener != null)
				{
					listener.imageReady(destImage.getImage());
					Thread.yield();
				}
			}
		}
		catch (ClassNotFoundException e)
		{
			// This should never happen, all the classes are in the JAR file.
			e.printStackTrace();
		}
	}

	private FractalImageModel loadFractalImageModel(String compressedFileName) throws ClassNotFoundException, IOException
	{
		FileInputStream fis = new FileInputStream(compressedFileName);
		BufferedInputStream bis = new BufferedInputStream(fis);
		GZIPInputStream gis = new GZIPInputStream(bis);
		ObjectInputStream ois = new ObjectInputStream(gis);
		FractalImageModel fim = (FractalImageModel) ois.readObject();
		ois.close();
		return fim;
	}

	public CompressionResults getCompressionResults()
	{
		return results;
	}

}
