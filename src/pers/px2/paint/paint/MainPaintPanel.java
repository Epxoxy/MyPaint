package pers.px2.paint.paint;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import pers.px2.paint.window.CallBack;
import java.util.ArrayList;

public class MainPaintPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage originImage;// originImage save the load image.
	private BufferedImage imgBuffer;// save the file will change actually
	private BufferedImage dragCache;// use to show effect when drag
	private int widthShow = 800;// width of show
	private int heightShow = 480;// height of show
	private double zoomSize = 1;
	private boolean isDraged = false;// It will be true when mouse dragging
	private boolean isDashMode = false;
	private boolean dotMode = false;
	private boolean seriesDotMode = true;
	private Point pOrigin;// save the point when press every time
	private DragVaries drag = new DragVaries();
	private JTextArea textPane = null;
	private Font textFont = new Font("", Font.PLAIN, 16);
	
	// do action to paint  which graphics  going the paint
	private GraphicsAction doPaint = new GraphicsAction();
	
	// box  of  undo and redo, both just  can save  less than 50 object
	private ArrayList<GraphicsAction> redoBoxSave = new ArrayList<GraphicsAction>();
	private ArrayList<GraphicsAction> undoBoxSave = new ArrayList<GraphicsAction>();
	private int drawCount;// count the draw time, will be reduce when it more than 50.
	private CallBack callback = null;//Callback to set show detail in main window
	private Tools tools = Tools.Picker;

	public MainPaintPanel() {
		setLayout(null);
		setBackground(Color.WHITE);
		initZoom(widthShow, heightShow);
		addMouseMotionListener(new PaintMouseMotionHandler());
		addMouseListener(new PaintMouseHandler());
	}

	// Initialize panel with blank
	public void initPanel() {
		initPanel(null);
		this.repaint();
	}

	/*
	 * Initialize panel with image
	 * */
	public void initPanel(BufferedImage img) {
		originImage = img;
		imgBuffer = null;
		dragCache = null;
		redoBoxSave.clear();
		undoBoxSave.clear();
	}

	// Initialize zoom to specified size
	public void initZoom(int width, int height) {
		widthShow = width;
		heightShow = height;
		setPreferredSize(new Dimension(widthShow, heightShow));
		if (callback != null)
			callback.setViewPaneSize(widthShow, heightShow);
		if (isDashMode)
			paintGBuffer();
		this.repaint();
	}

	// set pen tools, who will draw series dot except for text
	public void setPenTools(String penType) {
		if (this.isDashMode)
			this.paintGBuffer();
		doPaint.setTools(penType);
		if (penType == "Text") {
			seriesDotMode = false;
		} else {
			seriesDotMode = true;
		}
		dotMode = false;
	}

	// Set stroke size
	public void setStrokeSizes(int size) {
		doPaint.setStroke(new BasicStroke((float) (size / zoomSize)));
		setFont(textFont.getFontName());
		if (isDashMode)
			dragUpdate();
	}

	// Set font of text
	public void setFont(String fontName) {
		if (textPane != null) {
			textFont = new Font(fontName, Font.PLAIN, (int) (16 * doPaint.getStroke().getLineWidth()));
			textPane.setFont(textFont);
		}
	}

	// Set type of polygon
	public void setPolygon(String type) {
		seriesDotMode = false;
		dotMode = false;
		if (type == "Line" || type == "Rect" || type == "Image") {
			doPaint.setNormalType(type);
		} else {
			doPaint.setPolygon(type);
		}
		if (isDashMode)
			dragUpdate();
	}

	// Set if is draw fill
	public void setDrawFill(boolean fill) {
		doPaint.setPolygonFill(fill);
		if (isDashMode)
			dragUpdate();
	}

	// Set draw color
	public void setColor(Color color) {
		if (textPane != null) {
			textPane.setForeground(color);
		}
		doPaint.setColor(color);
		if (isDashMode)
			dragUpdate();
	}

	// Set paint on or off
	public void setPaintUse(boolean paintUse) {
		this.dotMode = paintUse;
		if (paintUse)
			tools = Tools.Paint;
		else
			tools = Tools.Pen;
	}

	//Get if is dash mode
	public boolean isDashMode() {
		return this.isDashMode;
	}

	//Get current zoom size with percent
	public double getZoomSizePercent() {
		return zoomSize * 100;
	}

	//Turn on color picker mode
	public void onColorPicker() {
		this.tools = Tools.Picker;
		this.dotMode = true;
	}

	//Add a callback handler to show something
	public void addCallBackHandler(CallBack callback) {
		this.callback = callback;
		this.initZoom(widthShow, heightShow);
	}

	// #REGION COLOR PICKER FUNCTION
	/*
	 * Get the pixel color in (x, y) of the screen
	 * */
	public Color getPixelColorIn(int x, int y) {
		Robot r;
		Color pixelColor;
		try {
			r = new Robot();
			pixelColor = r.getPixelColor(x, y);
		} catch (AWTException e) {
			pixelColor = null;
			System.out.println(e.getMessage());
		}
		return pixelColor;
	}

	/*
	 * Fill color use image editor
	 * */
	public void paintColor(int x, int y, Point p) {
		Color pixelColor = this.getPixelColorIn(p.x, p.y);
		if (pixelColor == null)
			return;
		ImageEditor.fillAreaWithScanLine(imgBuffer, x, y, doPaint.getColor().getRGB(), pixelColor.getRGB());
		this.zoomPanel(zoomSize);
	}
	// #ENDREGION COLOR PICKER FUNCTION

	/*
	 * Single point function, will effect when click once
	 * */
	private void singlePointFunction(Point p, Point screen) {
		switch (tools) {
		case Picker: {
			Color c = this.getPixelColorIn(screen.x, screen.y);
			doPaint.setColor(c);
			callback.setColorHeader(doPaint.getColor());
			this.dotMode = false;
			this.setPenTools("Pen");
		}
			break;
		case Paint: {
			paintColor((int) (p.x / zoomSize), (int) (p.y / zoomSize), screen);
		}
			break;
		default:
			break;
		}
	}

	// #REGION PAINT TEXT
	/*
	 * Get the image of get textArea widget
	 * */
	private BufferedImage textAreaPaint() {
		textPane.setForeground(doPaint.getColor());
		if (textPane != null) {
			textPane.setCaretColor(textPane.getBackground());
			textPane.select(0, 0);
			BufferedImage textImg = ImageEditor.widgetToImage(textPane);
			remove(textPane);
			textPane = null;
			return textImg;
		}
		return null;
	}

	/*
	 * Add one textArea widget on panel
	 * */
	private void PaintText() {
		if (textPane == null) {
			textPane = new JTextArea();
			textPane.setEditable(true);
			textPane.setOpaque(false);
			textPane.setWrapStyleWord(true);
			textPane.setLineWrap(true);
			textPane.setFont(textFont);
			textPane.setForeground(doPaint.getColor());
			add(textPane);
			System.out.println("TextPane add");
		}
		Point begin = doPaint.getPFrom();
		Point end = doPaint.getPTo();
		textPane.setBounds(begin.x + 1, begin.y + 1, end.x - begin.x - 2, end.y - begin.y - 2);
	}
	// #ENDREGION PAINT TEXT

	// #REGION IMAGE
	/*
	 * Start crop
	 * */
	public void cropPanel() {
		isDashMode = false;
		Rectangle rect = doPaint.getRect();
		int x = (int) (rect.x / zoomSize);
		int y = (int) (rect.y / zoomSize);
		int w = (int) (rect.width / zoomSize);
		int h = (int) (rect.height / zoomSize);
		if (rect.width == 0 || rect.height == 0)
			return;
		Rectangle rect2 = new Rectangle(x, y, w-1, h-1);
		initPanel(ImageEditor.cutImage(imgBuffer, rect2));
		initZoom(originImage.getWidth(), originImage.getHeight());
		setPenTools("Pen");
	}

	/*
	 * Zoom panel with a double value
	 * */
	public void zoomPanel(double zs) {
		zoomSize = (zs < 10 ? zs : zs / 100);
		doPaint.setZoomSize(zoomSize);
		int width = (int) (originImage.getWidth() * zoomSize);
		int height = (int) (originImage.getHeight() * zoomSize);
		initZoom(width, height);
	}

	/*
	 * Resize the image with new width and height
	 * */
	public void resizeImage(int width, int height) {
		loadImage(ImageEditor.resizeImage(imgBuffer, width, height));
	}

	/*
	 * Rotate image with ninety degrees
	 * */
	public void rotateImage() {
		loadImage(ImageEditor.rotateImageNinetyDegrees(imgBuffer), zoomSize);
	}

	/*
	 * Load a image to this panel
	 * */
	public boolean loadImage(BufferedImage img) {
		return loadImage(img, 1);
	}

	public boolean loadImage(BufferedImage img, double zoom) {
		originImage = img;
		this.widthShow = originImage.getWidth();
		this.heightShow = originImage.getHeight();
		imgBuffer = null;
		dragCache = null;
		zoomPanel(zoom);
		return true;
	}

	/*
	 * Get current image (to print)
	 * */
	public BufferedImage getCurrentImage() {
		return this.imgBuffer;
	}
	// #ENDREGION IMAGE

	// #REGION PAINT INVIOKE
	/*
	 * Update panel when drag but won't effect to main image unless use paintGBuffer()
	 * */
	private void dragUpdate() {
		Graphics2D g;
		if (dragCache == null) {
			dragCache = new BufferedImage(imgBuffer.getWidth(), imgBuffer.getHeight(), BufferedImage.SCALE_FAST);
		}
		g = dragCache.createGraphics();
		if (doPaint.getNormalType() != null && doPaint.getDrawType().toString() == "RectArea") {
			if (doPaint.getNormalType().toString() == "Text")
				PaintText();
		}
		g.drawImage(imgBuffer, 0, 0, null);
		doPaint.drawExecute(g, true);
		g.dispose();
		this.repaint();
	}

	/*
	 * Paint effect to gBuffer
	 * */
	public void paintGBuffer() {
		if (doPaint.getPFrom().x < 0)
			return;
		isDashMode = false;
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		gBuffer = imgBuffer.createGraphics();
		if (doPaint.getNormalType() != null && doPaint.getDrawType().toString() == "RectArea") {
			if (doPaint.getNormalType().toString() == "Text")
				doPaint.setOperateIMG(textAreaPaint());
		}
		doPaint.drawExecute(gBuffer);
		gBuffer.dispose();
		this.repaint();

		// undoBox and redoBox operation
		Object obj = doPaint.clone();
		if (obj == null)
			return;
		undoBoxSave.add((GraphicsAction) obj);
		callback.enabledUndo(true);
		if (!redoBoxSave.isEmpty()) {
			callback.enabledRedo(false);
			redoBoxSave.clear();
		}
		drawCount++;
		if (drawCount > 50) {
			Graphics2D g1 = originImage.createGraphics();
			undoBoxSave.get(0).drawExecute(g1);
			g1.dispose();
			undoBoxSave.remove(0);
			drawCount--;
		}
	}
	// #ENDREGION PAINT INVIOKE

	// #REGION UNDO AND REDO
	public void UndoCurrent() {
		if (this.isDashMode) {
			this.paintGBuffer();
		}
		if (undoBoxSave.isEmpty())
			return;
		gBuffer = imgBuffer.createGraphics();
		gBuffer.drawImage(originImage, 0, 0, null);
		for (int i = 0; i < undoBoxSave.size() - 1; i++) {
			if (undoBoxSave.get(i) == null) {
				System.out.println("Null : " + i);
				continue;
			}
			undoBoxSave.get(i).drawExecute(gBuffer);
		}
		gBuffer.dispose();
		this.repaint();

		redoBoxSave.add(undoBoxSave.get(undoBoxSave.size() - 1));
		undoBoxSave.remove(undoBoxSave.size() - 1);
		if (undoBoxSave.isEmpty())
			callback.enabledUndo(false);
		callback.enabledRedo(true);
	}

	public void RedoBefore() {
		if (redoBoxSave.isEmpty())
			return;
		gBuffer = imgBuffer.createGraphics();
		GraphicsAction next = redoBoxSave.get(redoBoxSave.size() - 1);
		next.drawExecute(gBuffer);
		gBuffer.dispose();
		this.repaint();

		undoBoxSave.add(next);
		redoBoxSave.remove(redoBoxSave.size() - 1);
		if (redoBoxSave.isEmpty())
			callback.enabledRedo(false);
		callback.enabledUndo(true);
	}
	// #ENDREGION UNDO AND REDO

	// #REGION PAINTCOMPONENT
	private Graphics2D gBuffer;//the graphcis2d  instance of imgBuffer

	/*
	 * Override the paintComponent method
	 * Will draw dragCache when drag mode
	 * Will draw gBuffer when not in drag mode 
	 * */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (imgBuffer == null) {
			if (originImage == null) {
				originImage = new BufferedImage(widthShow, heightShow, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = originImage.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, widthShow, heightShow);
				g2d.dispose();
			}
			imgBuffer = new BufferedImage(originImage.getWidth(), originImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			gBuffer = imgBuffer.createGraphics();
			gBuffer.drawImage(originImage, 0, 0, null);
			if (callback != null)
				callback.setResolution(originImage.getWidth(), originImage.getHeight());
		}
		if ((isDashMode || isDraged) && (!seriesDotMode)) {
			g2.drawImage(dragCache, 0, 0, widthShow, heightShow, this);
			System.out.print("update ");
		} else {
			g2.drawImage(imgBuffer, 0, 0, widthShow, heightShow, this);
			System.out.println("Draw imgBuffer");
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		super.paintChildren(g);
		g2.dispose();
	}
	// #ENDREGION PAINTCOMPONENT

	// #REGION MOUSELISTENER
	
	/*
	 * Get the edit point area and hand
	 * */
	private class PaintMouseMotionHandler extends DashMouseMotionHanlder {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (dotMode)
				return;
			Point current = e.getPoint();
			super.mouseDragged(e);
			if (!isDashMode) {
				doPaint.setPTo(current);
				isDraged = true;
			}
			if (seriesDotMode) {
				paintGBuffer();
			} else {
				Point pFrom = doPaint.getPFrom();
				Point pTo = doPaint.getPTo();
				if (!isDashMode) {
					if (pOrigin.x >= pTo.x) {
						pFrom.x = pTo.x;
						pTo.x = pOrigin.x;
					}
					if (pOrigin.y >= pTo.y) {
						pFrom.y = pTo.y;
						pTo.y = pOrigin.y;
					}
				}
				int x = (int) (doPaint.getRect().width / zoomSize);
				int y = (int) (doPaint.getRect().height / zoomSize);
				callback.setDashRect(x, y);
				dragUpdate();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			Point p = e.getPoint();
			callback.setPosition((int) (p.x / zoomSize), (int) (p.y / zoomSize));
			if (dotMode || (!isDashMode)) {
				callback.setDashRect(0, 0);
				return;
			}
			super.mouseMoved(e);
		}

	}

	private class PaintMouseHandler extends DashMouseHanlder {

		@Override
		public void mousePressed(MouseEvent e) {
			doPaint.setRele(false);
			Point current = e.getPoint();
			if (dotMode)
				return;
			super.mousePressed(e);
			if (!isDashMode) {
				pOrigin = current;
				doPaint.setPFrom(new Point(current));
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			doPaint.setRele(true);
			if (dotMode) {
				return;
			}
			super.mouseReleased(e);
			if (isDashMode) {
			} else {
				doPaint.setPTo(e.getPoint());
			}
			if (seriesDotMode) {
				paintGBuffer();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (dotMode) {
				singlePointFunction(e.getPoint(), e.getLocationOnScreen());
			}
			super.mouseClicked(e);
			// if (!isDashMode)
			// return;
			// if (drag.isInside(e.getPoint()) == false) {
			// isDashMode = false;
			// System.out.println("Outside isDashMode click");
			// }
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	/*
	 * Hand the action when in dash editor mode
	 * */
	private class DashMouseMotionHanlder implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
			if (isDashMode) {
				drag.dragExecute(e.getPoint(), doPaint.getPFrom(), doPaint.getPTo());
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (!drag.isInside(e.getPoint())) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			} else {
				setCursor(new Cursor(drag.getCursorMode(e.getPoint())));
			}
		}

	}

	private class DashMouseHanlder implements MouseListener {

		@Override
		public void mousePressed(MouseEvent e) {
			if (isDashMode) {
				if (drag.isInside(e.getPoint())) {
					drag.setDragPoint(e.getPoint());
				} else {
					paintGBuffer();
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			if (isDraged) {
				if (!seriesDotMode) {
					isDashMode = true;
					drag.setRect(doPaint.getPFrom(), doPaint.getPTo());
				}
				isDraged = false;
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
		}

	}
	// #ENDRGION MOUSELISTENER

}

// StackTraceElement[] s = new Exception().getStackTrace();
// System.out.println(s[1].getClassName() + "\n" + s[1].getMethodName());