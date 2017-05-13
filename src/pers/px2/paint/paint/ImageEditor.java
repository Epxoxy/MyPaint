package pers.px2.paint.paint;

import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

public class ImageEditor {
	
	/*
	 * Rotate one BufferedImage with degrees 90, use Graphics2D.translate
	 * */
	public static BufferedImage rotateImageNinetyDegrees(BufferedImage src) {
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		// calculate the new image size
		Rectangle rect = new Rectangle(srcHeight, srcWidth);
		BufferedImage rotateAfter = null;
		rotateAfter = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = rotateAfter.createGraphics();
		// transform
		g2.translate((rect.width - srcWidth) / 2, (rect.height - srcHeight) / 2);
		g2.rotate(Math.toRadians(90), srcWidth / 2, srcHeight / 2);

		g2.drawImage(src, null, null);
		g2.dispose();
		return rotateAfter;
	}

	/*
	 * Resize the image
	 * */
	public static BufferedImage resizeImage(BufferedImage src, int width, int height) {
		BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = target.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawImage(src, 0, 0, width, height, null);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.dispose();
		return target;
	}
	
	/*
	 * Print JComponent to bufferImage.
	 * */
	public static BufferedImage widgetToImage(JComponent c) {
		if (c.getWidth() < 0 || c.getHeight() < 0)
			return null;
		BufferedImage img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		
		//Print JComponent method printAll();
		c.printAll(g2d);
		g2d.dispose();
		return img;
	}

	/*
	 * Cut image of Rectangle area 
	 * */
	public static BufferedImage cutImage(BufferedImage src, Rectangle rect) {
		BufferedImage buf = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.SCALE_SMOOTH);
		BufferedImage target;
		Graphics gbuf = buf.createGraphics();
		gbuf.drawImage(src, 0, 0, null);
		gbuf.dispose();
		target = buf.getSubimage(rect.x, rect.y, rect.width, rect.height);
		return target;
	}
	
	public static void fillAreaWithScanLine(BufferedImage image, int x, int y, int newColor, int oldColor){
		ScanLine scan = new ScanLine(image);
		scan.trunOnScanLine(x, y,newColor, oldColor);
	}
}

class ScanLine {
	private int height;//scan image's height
	private int width;//scan image's width
	private int[] rgbArray;//save the agb value
	private int stackSize;//save the stackSize
	private int maxStackSize = 480;//save the maxStackSize, will add when on much to before
	private int[] xstack = new int[maxStackSize];//the x stack
	private int[] ystack = new int[maxStackSize];//the y stack
	private BufferedImage scanImg;//scan image

	public ScanLine(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		rgbArray = new int[width * height];
		scanImg = image;
	}

	/*
	 * start scan line from (x,y) with old color and new color
	 * */
	public void trunOnScanLine(int x, int y, int newColor, int oldColor){
		getRGB(scanImg, 0, 0, width, height, rgbArray);
		scanLineWith(x, y, newColor, oldColor);
		setRGB(scanImg, 0, 0, width, height, rgbArray);
		
	}

	/*
	 * main scan method
	 * get the color on one point and scan the whole line
	 * put the near point into stack if it is old color then will scan those near line
	 * */
	public void scanLineWith(int x, int y, int newColor, int oldColor) {
		if (oldColor == newColor) {
			return;
		}
		emptyStack();

		int y1;
		boolean splitLeft, splitRight;
		push(x, y);

		while ((x = popx()) != -1) {
			y1 = popy();
			//Go to the line top
			while (y1 >= 0 && getColor(x, y1) == oldColor){
				y1--; 
			}
			y1++; // start from line starting point pixel
			splitLeft = splitRight = false;
			boolean oldColorOnPixel = false;
			while (y1 < height && getColor(x, y1) == oldColor) {
				setColor(x, y1, newColor);
				if(x > 0){
					oldColorOnPixel = (getColor(x - 1, y1) == oldColor);
					if (!splitLeft && oldColorOnPixel) {
						push(x - 1, y1);
						splitLeft = true;
					} else if (splitLeft && (!oldColorOnPixel)) {
						splitLeft = false;
					}
				}
				if(x < width - 1){
					oldColorOnPixel = (getColor(x + 1, y1) == oldColor);
					if (!splitRight && oldColorOnPixel) {
						push(x + 1, y1);
						splitRight = true;
					} else if (splitRight && (!oldColorOnPixel)) {
						splitRight = false;
					}
				}
				y1++;
			}
		}

	}

	/*
	 * Get or set color on rgbArray
	 *  pixel = rgbArray[offset + (y-startY)*scansize + (x-startX)]; 
	 *  offset = startY = startX = 0
	 * */
	public int getColor(int x, int y) {
		int index = y * width + x;
		return rgbArray[index];
	}

	public void setColor(int x, int y, int newColor) {
		int index = y * width + x;
		rgbArray[index] = newColor;
	}

	/*
	 * Empty stack who save the line need to scan
	 * */
	private void emptyStack() {
		while (popx() != -1) {
			popy();
		}
		stackSize = 0;
	}

	/*
	 * Put the point into stack
	 * */
	void push(int x, int y) {
		stackSize++;
		if (stackSize == maxStackSize) {
			int[] newXStack = new int[maxStackSize * 2];
			int[] newYStack = new int[maxStackSize * 2];
			System.arraycopy(xstack, 0, newXStack, 0, maxStackSize);
			System.arraycopy(ystack, 0, newYStack, 0, maxStackSize);
			xstack = newXStack;
			ystack = newYStack;
			maxStackSize *= 2;
		}
		xstack[stackSize - 1] = x;
		ystack[stackSize - 1] = y;
	}

	/*
	 * Pop the x value of the top point
	 * */
	int popx() {
		if (stackSize == 0)
			return -1;
		else
			return xstack[stackSize - 1];
	}

	/*
	 * Pop the y value of the top point
	 * */
	int popy() {
		int value = ystack[stackSize - 1];
		stackSize--;
		return value;
	}

	/*
	 * Set the rgb value on image with rgbArray
	 * */
	public void setRGB(BufferedImage image, int x, int y, int width, int height, int[] rgbArray) {
		int type = image.getType();
		if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)
			image.getRaster().setDataElements(x, y, width, height, rgbArray);
		else
			image.setRGB(x, y, width, height, rgbArray, 0, width);
	}

	/*
	 * Get the rgb value on image with rgbArray
	 * */
	public int[] getRGB(BufferedImage image, int x, int y, int width, int height, int[] rgbArray) {
		int type = image.getType();
		if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)
			return (int[]) image.getRaster().getDataElements(x, y, width, height, rgbArray);
		return image.getRGB(x, y, width, height, rgbArray, 0, width);
	}
}