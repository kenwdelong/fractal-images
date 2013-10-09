/*
 * Created on May 12, 2005
 */
package biz.femtosoft.fractal;

import javax.swing.JFrame;

import biz.femtosoft.fractal.app.GuiController;
import biz.femtosoft.fractal.domain.FractalCompressorFacade;
import biz.femtosoft.fractal.gui.FractalGuiPanel;


/**
 * This is the main class that starts and wires up the application. You could replace this with
 * something like the Spring Framework, but there's really no need.
 * @author ken.delong
 */
public class FractalMain
{

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		FractalGuiPanel panel = new FractalGuiPanel();
		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		FractalCompressorFacade facade = new FractalCompressorFacade();
		
		GuiController controller = new GuiController(facade, panel);
		
		frame.pack();
		frame.show();

	}
}
