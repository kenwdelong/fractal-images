/*
 * Created on May 11, 2005
 */
package biz.femtosoft.fractal.domain;


/**
 * Just carries the results of a compression run.  I need this since I don't have a way to 
 * return more than one item.  I could also have just put getters for each of these in the 
 * Facade, but this seems simpler.
 * @author ken.delong
 */
public class CompressionResults
{
	public String inputFileName;
	public String compressedFileName;
	public double percentCompression;
	public int elapsedTime;
	public long inputFileLength;
	public long compressedFileLength;
}
