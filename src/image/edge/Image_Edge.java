/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.edge;

import java.util.ArrayList;

import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.*;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.WindowManager;

public class Image_Edge implements PlugIn {
	static final int BYTE=0, SHORT=1, FLOAT=2, RGB=3;
	public void run(String arg) {
		if(arg.equals("about")){
			showAbout();return;
		}
		
		ImagePlus img = WindowManager.getCurrentImage();
		if(img == null){
			IJ.error("No images open");
			return;
		}
		
		if(arg.equals("deriche")){
			if(!greyImage(img)){
				IJ.showMessage("greyscale images required");
				return;
			} 
			deriche(img);return;
		}
		else if(arg.equals("hyst")){
			hysteresis(img);return;
		}
		
		else if(arg.equals("edge")){
			if(!greyImage(img)){
				IJ.showMessage("greyscale images required");
				return;
			} 
			EdgeDetection(img);return;
		}
		
	}

	private boolean greyImage(ImagePlus img) {
		if (img.getType() < ImagePlus.COLOR_256)return true;
		else return false;
	}

	private void showAbout() {
		IJ.showMessage("About ImageEdge",
				"This plugin collects some tools for edge detection based on\n"+
				"Canny-Deriche filtering, double treshold and hysteresis treshold.\n \n"+
				"Outline detection combines these filters and the circular median filter to find \n"+
				"the outlines of areas in greyscale pictures with a high granularity,\n" +
				"like confocal pictures from FRAP analysis with high magnification.\n" +
				"The plugin Conn_tres adjusts the lower and upper threshold levels of the\n" +
				"active image. It is modified for hysteresis by Thomas Boudier.\n \n"+
				"The JAR file contains a class ImageEdge with static methods that can\n"+
				"be used to implement these filters in other plugins.\n \n"+
				"Authors : Thomas Boudier, Joris Meys (modification ImageEdge for static access)\n"+
				"Original code for deriche filtering, double treshold and hysteresis\n"+
				"were first implemented in the plugins deriche_(Boudier, 2003)\n" +
				"and hysteresis_(Boudier, 2007)\n \n"+
				"More info : jorismeys@gmail.com, Thomas.Boudier@snv.jussieu.fr");
		
	}

	public ImagePlus EdgeDetection(ImagePlus img) {
		// Set parameters
		/**
                GenericDialog gd = new GenericDialog("Parameters");
		gd.addNumericField("median filter radius",3,2);
		gd.addNumericField("Deriche alpha value",1,2);
		gd.addNumericField("Hysteresis High threshold", 100, 2);
		gd.addNumericField("Hysteresis Low threshold", 50, 2);
		gd.showDialog();
		if(gd.wasCanceled())return;
		
                double radius = gd.getNextNumber();
		float alpha = (float) gd.getNextNumber();
		float upper = (float) gd.getNextNumber();
		float lower = (float) gd.getNextNumber();
		*/
		double radius = 3.0;
                float alpha = (float) 1.0;
                float upper = (float) 100;
                float lower = (float) 90;
            
                ImageStack stack = img.getStack();
		ImageStack result = new ImageStack(stack.getWidth(), stack.getHeight());
		ImageProcessor tmp1;
		
		for (int s = 1; s <= stack.getSize(); s++) {
			tmp1 = ImageEdge.areaEdge(stack.getProcessor(s), radius, alpha, upper, lower);
			result.addSlice("", tmp1);
			}
		//new ImagePlus("Area Outline", result).show();
                return new ImagePlus("Area Outline", result);
	}

	private void hysteresis(ImagePlus img) {
		boolean show_trin=false;
		GenericDialog gd = new GenericDialog("Parameters");
		gd.addNumericField("High threshold", 100, 2);
		gd.addNumericField("Low threshold", 50, 2);
		gd.addCheckbox("display trinirisation", show_trin);
		//gd.showDialog();
		if(gd.wasCanceled())return;
		float T1 = (float) gd.getNextNumber();
		float T2 = (float) gd.getNextNumber();
		show_trin= gd.getNextBoolean();

		ImageStack stack = img.getStack();
		ImageStack res_trin = new ImageStack(stack.getWidth(), stack.getHeight());
		ImageStack res_hyst = new ImageStack(stack.getWidth(), stack.getHeight());

		ImageProcessor tmp1;
		ImageProcessor tmp2;

		for (int s = 1; s <= stack.getSize(); s++) {
			tmp1 = ImageEdge.trin(stack.getProcessor(s), T1, T2);
			res_trin.addSlice("", tmp1);
			tmp2 = ImageEdge.hyst(tmp1);
			res_hyst.addSlice("", tmp2);
		
		}
		if(show_trin)new ImagePlus("Trinarisation", res_trin).show();
		new ImagePlus("Hysteresis", res_hyst).show();
	}
		
/**
 * Implements the deriche filter. */
	public ImagePlus deriche(ImagePlus img) {
		double[] norm_deriche;
		double[] angle_deriche;
		ArrayList<double[]> arrays;
		ImageStack stack = img.getStack();
		
		boolean show_angle = false;
		
                /*
                GenericDialog gd = new GenericDialog("Parameters");
		gd.addNumericField("alpha", 1.0, 2);
		gd.addCheckbox("display angle", show_angle);
		gd.showDialog();
		if(gd.wasCanceled())return;
		
                
                float alphaD = (float) gd.getNextNumber();
		show_angle = gd.getNextBoolean();
                **/
                float alphaD = (float) 2.0;
                show_angle = false;
                
		ImageStack res_norm = new ImageStack(stack.getWidth(), stack.getHeight());
		ImageStack res_angle = new ImageStack(stack.getWidth(), stack.getHeight());
		ImageStack suppr = new ImageStack(stack.getWidth(), stack.getHeight());

		FloatProcessor tmp = new FloatProcessor(stack.getWidth(), stack.getHeight());

		for (int s = 1; s <= stack.getSize(); s++) {
			arrays = ImageEdge.dericheCalc(stack.getProcessor(s), alphaD);
			norm_deriche = arrays.get(0);
			angle_deriche = arrays.get(1);
			tmp = new FloatProcessor(stack.getWidth(), stack.getHeight(), norm_deriche);
			tmp.resetMinAndMax();
			res_norm.addSlice("", tmp);
			tmp = new FloatProcessor(stack.getWidth(), stack.getHeight(), angle_deriche);
			tmp.resetMinAndMax();
			res_angle.addSlice("", tmp);
			suppr.addSlice("", ImageEdge.nonMaximalSuppression(res_norm.getProcessor(s), tmp));
		}
		//new ImagePlus("Canny-Deriche norm " + alphaD, res_norm).show();
		return new ImagePlus("Canny-Deriche suppr " + alphaD, suppr);
		//if (show_angle) {
		//	new ImagePlus("Canny-Deriche angle " + alphaD, res_angle).show();
		//}
	} // end deriche
	

}
