/*
 * Created on May 12, 2005
 */
package biz.femtosoft.fractal.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


/**
 * Convenience for getting panels that display BufferedImages.  Java2D rules!
 * @author ken.delong
 */
public class ImageDisplayingPanel extends JPanel
{
	private BufferedImage bi;
	
	public ImageDisplayingPanel()
	{
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
	}

	public void setImage(BufferedImage bi)
	{
		this.bi = bi;
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if(bi != null)
			g2.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
	}

}
