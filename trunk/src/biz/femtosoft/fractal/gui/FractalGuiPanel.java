/*
 * Created on May 12, 2005
 */
package biz.femtosoft.fractal.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import biz.femtosoft.fractal.app.GuiController;
import biz.femtosoft.fractal.domain.CompressionResults;
import biz.femtosoft.fractal.domain.IGui;


/**
 * The container panel for all the gui panels.  This impls IGui so it's known by the controller.
 * @author ken.delong
 */
public class FractalGuiPanel extends JPanel implements IGui, IControlPanelListener
{
	
	private ImageDisplayingPanel refImagePanel;
	private GuiController controller;
	private ControlPanel controlPanel;
	private ImageDisplayingPanel retrievedImagePanel;


	public FractalGuiPanel()
	{
		refImagePanel = new ImageDisplayingPanel();
		retrievedImagePanel = new ImageDisplayingPanel();
		Dimension d = new Dimension(300,300);
		refImagePanel.setPreferredSize(d);
		retrievedImagePanel.setPreferredSize(d);
		controlPanel = new ControlPanel(this);
		this.setLayout(new BorderLayout());
		this.add(controlPanel, BorderLayout.SOUTH);
				
		Box box = Box.createHorizontalBox();
		box.add(refImagePanel);
		box.add(retrievedImagePanel);
		this.add(box, BorderLayout.CENTER);		
	}
	
	public void setController(GuiController controller)
	{
		this.controller = controller;
	}
	
	public void displayReferenceImage(BufferedImage bi)
	{
		refImagePanel.setImage(bi);
		refImagePanel.repaint();
	}

	public void enableCompression()
	{
		controlPanel.enableCompression();
	}

	public void setCompressedFileName(String string)
	{
		controlPanel.setCompressedFileName(string);
	}

	public void displayWarning(String message)
	{
		JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	public void displayCompressionResults(CompressionResults results)
	{
		controlPanel.updateCompressionProgress(0);
		controlPanel.displayCompressionResults(results);
	}

	public void enableDecompression()
	{
		controlPanel.enableDecompression();
	}

	public void displayUncompressionResults(BufferedImage image)
	{
		retrievedImagePanel.setImage(image);
		retrievedImagePanel.repaint();
	}

	public void fileToCompressSelected(File fileToCompress)
	{
		controller.openFile(fileToCompress.getAbsolutePath());		
	}

	public void compressPressed()
	{
		controller.compress(controlPanel.getCompressedFileName(), controlPanel.getCompressPanelSize());
	}

	public void uncompressPressed()
	{
		controller.uncompress(controlPanel.getUncompressFileName(), controlPanel.getUncompressPanelSize());		
	}

	public void updateCompressionProgress(double percent)
	{
		controlPanel.updateCompressionProgress(percent);
	}

	

	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		FractalGuiPanel panel = new FractalGuiPanel();
		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.show();
	}

}
