/*
 * Created on May 12, 2005
 */
package biz.femtosoft.fractal.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import biz.femtosoft.fractal.domain.CompressionResults;


/**
 * This is the panel that has the widgets on.  I didn't try hard to make this nice, it's late
 * and I want to go to bed.  You can glorify this if you want.  For example, you might want to
 * lock out the GUI while a compression is going on, so the user doesn't start two.  Or you might
 * want to put on a cancel button!
 * @author ken.delong
 */
public class ControlPanel extends JPanel
{
	private File fileToCompress;
	private JTextField compressedFileName;
	private IControlPanelListener listener;
	private JButton compressButton;
	private JFormattedTextField compressedPanelSize;
	private JTextField uncompressFileName;
	private JLabel originalSize;
	private JLabel compressedSize;
	private JLabel percentCompression;
	private JLabel elapsedTime;
	private JFormattedTextField uncompressPanelSize;
	private JButton uncompressButton;
	private JProgressBar progressBar;
	private File currentDir;
	
	public ControlPanel(IControlPanelListener list)
	{
		listener = list;
		init();
	}
	
	private void init()
	{
		Box box = Box.createVerticalBox();
		this.add(box);
		
		createFileBox(box);		
		createCompressedFileNameBox(box);
		createCompressPanelSizeBox(box);
		createCompressButton(box);
		createProgressBar(box);
		createResults(box);
		createUncompressFileBox(box);
		createUncompressPanelSizeBox(box);
		createUncompressButton(box);
	}

	private void createProgressBar(Box box)
	{
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setVisible(true);
		progressBar.setPreferredSize(new Dimension(100, 30));
		box.add(progressBar);
	}

	private void createUncompressButton(Box box)
	{
		uncompressButton = new JButton("Uncompress");
		box.add(uncompressButton);
		
		uncompressButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(listener != null)
					listener.uncompressPressed();
			}
			
		});
		uncompressButton.setEnabled(false);
	}

	private void createUncompressPanelSizeBox(Box box)
	{
		Box tBox = Box.createHorizontalBox();
		JLabel lable = new JLabel("Panel Size: ");
		uncompressPanelSize = new JFormattedTextField(new Integer(4));
		tBox.add(lable);
		tBox.add(uncompressPanelSize);
		box.add(tBox);
	}

	private void createUncompressFileBox(Box box)
	{
		Box fileBox = Box.createHorizontalBox();
		JLabel fileLabel = new JLabel("File to uncompress: ");
		uncompressFileName = new JTextField(20);
		JButton browse = new JButton("Browse...");
		fileBox.add(fileLabel);
		fileBox.add(uncompressFileName);
		fileBox.add(browse);
		box.add(fileBox);
		
		browse.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser(getStartDirectory());
				int retval = chooser.showOpenDialog(ControlPanel.this);
				if(retval == JFileChooser.APPROVE_OPTION)
				{
					fileToCompress = chooser.getSelectedFile();
					uncompressFileName.setText(fileToCompress.getAbsolutePath());
					uncompressButton.setEnabled(true);
					currentDir = chooser.getCurrentDirectory();
				}
			}
		});

	}

	private void createResults(Box box)
	{
		Box tbox = Box.createVerticalBox();
		JLabel lTitle = new JLabel("Compression results:");
		originalSize = new JLabel("Original Size: ");
		compressedSize = new JLabel("Compressed size: ");
		percentCompression = new JLabel("Compression: 0%");
		elapsedTime = new JLabel("0 milliseconds");
		tbox.add(lTitle);
		tbox.add(originalSize);
		tbox.add(compressedSize);
		tbox.add(percentCompression);
		tbox.add(elapsedTime);
		
		box.add(tbox);
	}

	private void createCompressButton(Box box)
	{
		compressButton = new JButton("Compress");
		box.add(compressButton);
		
		compressButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(listener != null)
					listener.compressPressed();
			}			
		});
		compressButton.setEnabled(false);
	}

	private void createCompressPanelSizeBox(Box box)
	{
		Box tBox = Box.createHorizontalBox();
		JLabel lable = new JLabel("Panel Size: ");
		compressedPanelSize = new JFormattedTextField(new Integer(4));
		tBox.add(lable);
		tBox.add(compressedPanelSize);
		box.add(tBox);
	}

	private void createCompressedFileNameBox(Box box)
	{
		Box compFileBox = Box.createHorizontalBox();
		JLabel compFileLabel = new JLabel("Compressed file name: ");
		compressedFileName = new JTextField(20);
		compFileBox.add(compFileLabel);
		compFileBox.add(compressedFileName);
		box.add(compFileBox);
	}

	private void createFileBox(Box box)
	{
		Box fileBox = Box.createHorizontalBox();
		JLabel fileLabel = new JLabel("File to compress: ");
		final JTextField fileName = new JTextField(15);
		JButton browse = new JButton("Browse...");
		fileBox.add(fileLabel);
		fileBox.add(fileName);
		fileBox.add(browse);
		box.add(fileBox);
		
		browse.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser(getStartDirectory());
				int retval = chooser.showOpenDialog(ControlPanel.this);
				if(retval == JFileChooser.APPROVE_OPTION)
				{
					fileToCompress = chooser.getSelectedFile();
					fileName.setText(fileToCompress.getName());
					currentDir = chooser.getCurrentDirectory();
					if(listener != null)
						listener.fileToCompressSelected(fileToCompress);
				}
			}
		});

	}

	public void setCompressedFileName(String string)
	{
		compressedFileName.setText(string);
		uncompressFileName.setText(string);
	}

	public String getCompressedFileName()
	{
		return compressedFileName.getText();
	}

	public int getCompressPanelSize()
	{
		return ((Number)(compressedPanelSize.getValue())).intValue();
	}

	public void displayCompressionResults(CompressionResults results)
	{
		originalSize.setText("Original file: " + results.inputFileLength + " bytes");
		compressedSize.setText("Compressed file: " + results.compressedFileLength + " bytes");
		percentCompression.setText("Compression: " + results.percentCompression + "%");
		elapsedTime.setText(results.elapsedTime + " milliseconds");
	}

	public int getUncompressPanelSize()
	{
		return ((Number)(uncompressPanelSize.getValue())).intValue();
	}

	public String getUncompressFileName()
	{
		return uncompressFileName.getText();
	}

	public void enableCompression()
	{
		compressButton.setEnabled(true);		
	}

	public void enableDecompression()
	{
		uncompressButton.setEnabled(true);
	}

	public void updateCompressionProgress(double percent)
	{
		progressBar.setValue((int)percent);
	}

	private File getStartDirectory()
	{
		return (currentDir == null ? new File(System.getProperty("user.dir")) : currentDir);
	}
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		ControlPanel panel = new ControlPanel(null);
		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.show();
	}


}
