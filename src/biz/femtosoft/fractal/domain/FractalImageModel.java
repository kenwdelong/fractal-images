package biz.femtosoft.fractal.domain;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * Holds the list of FractalCodes discovered by the compressor. This is an
 * ordered list, in that the the destination image region is implicit in the
 * order of the FractalCodes. The first FractalCode is for the upper left, the
 * second for the first row, second element, etc.
 * 
 * Updated with custom serialization in May 2005.
 * 
 * @author Ken DeLong
 * @version 2.0
 * @date May 11, 2005
 */

public class FractalImageModel implements java.io.Serializable
{
	static final long serialVersionUID = -4820615096422645149L;
	
	private transient List list;
	private int mXPanels, mYPanels, mPanelSize;

	/**
	 * Constructor. The number of panels in each direction is needed to
	 * reconstruct the image, so they are required here.
	 * 
	 * @param pXPanels
	 *            The total number of panels in the x direction.
	 * @param pYPanels
	 *            The total number of panels in the y direction.
	 */
	FractalImageModel(int pXPanels, int pYPanels, int pPanelSize)
	{
		mXPanels = pXPanels;
		mYPanels = pYPanels;
		mPanelSize = pPanelSize;
		list = new ArrayList(mXPanels * mYPanels);
	}

	/**
	 * Add a FractalCode to the ImageModel.
	 * 
	 * @param pFractalCode
	 *            The new FractalCode to add.
	 */
	void addFractalCode(FractalCode pFractalCode)
	{
		list.add(pFractalCode);
	}

	/** Gets the total capacity of the FractalImageModel. */
	public int getCapacity()
	{
		return mXPanels * mYPanels;
	}

	/** Gets the current number of FractalCodes in the model. */
	public int getSize()
	{
		return list.size();
	}

	/**
	 * Gets the specified fractal code.
	 * 
	 * @param i
	 *            The index of the fractal code.
	 */
	public FractalCode getFractalCode(int i)
	{
		return (FractalCode)list.get(i);
	}

	/** Returns the number of panels in the x direction. */
	public int getXPanels()
	{
		return mXPanels;
	}

	/** Returns the number of panels in the y direction. */
	public int getYPanels()
	{
		return mYPanels;
	}

	/** Returns the panel size used to do the compression. */
	public int getPanelSize()
	{
		return mPanelSize;
	}
	
	
	//  SERIALIZATION ROUTINES
	private static final String VERSION_1 = "Version 1";
	private void writeObject(ObjectOutputStream oos) throws IOException
	{
		// ** V1 serialized form **
		oos.writeObject(VERSION_1);
		oos.defaultWriteObject();
		oos.writeInt(list.size());
		Iterator iter = list.iterator();
		while (iter.hasNext())
		{
			FractalCode code = (FractalCode)iter.next();
			oos.writeObject(code);			
		}
	}
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
	{
		// ** V1 serialized form **
		String version = (String) ois.readObject();
		if(!VERSION_1.equals(version))
			throw new InvalidObjectException("Version was " + version);
		ois.defaultReadObject();
		int size = ois.readInt();
		list = new ArrayList(size);
		for(int i = 0; i < size; i++)
		{
			FractalCode code = (FractalCode) ois.readObject();
			list.add(code);
		}
	}

}