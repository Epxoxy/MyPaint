package pers.px2.paint.paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

enum MainType {
	Tools, RectArea, MostPolygon, Image, Other;
}

enum Tools {
	Spray, Eraser, Pen, Paint, Picker;
}

enum NormalRectArea {
	Line, Rect, Curve, Oval, RoundRect, Arc, Text;
}

//Polygon type, but not all function is completed up to now.
enum PolygonType {
	FourStar, FiveStar, SixSlide, SixStar, Pentagon, Arrow, Diamond, PengQiang, Triangle, RightTriangle, DialogBox
}

public class GraphicsAction implements Cloneable {
	private ArrayList<Point> pCollect = new ArrayList<Point>();//cache collect point in series will clear in undo save completed
	private Point pFrom = new Point(0, 0);//save the point draw action start
	private Point pTo;//save the point draw action end.
	private Point pPre;//save the point last time, only use in main type of tools, only because if draw with series point
	private Point[] allPoint;//save all point draw in series point mode, this only use in redo and undo action
	
	/*
	 * Those three varies only one will effect in the same time
	 * Control in set value action 
	 * */
	private Tools tools = Tools.Pen;//depend the tools type
	private NormalRectArea normalType;//depend the normal type
	private PolygonType polygon;//depend the polygon type
	private MainType drawType = MainType.Tools;  //depend the main draw of type.
	
	/*
	 * Those change the Graphics2D detail
	 * */
	private boolean polygonFill; //save if the polygon will be fill with color
	private BasicStroke stroke = new BasicStroke((float) (1f));
	private Color color = Color.BLACK; //save current draw color
	private Rectangle rectangle = new Rectangle(0, 0, 0, 0);//current edit rectangle
	private double zoomSize = 1;//save the zoom size from paint panel
	private boolean released = true;//save if the mouse is released of mouse event on paint panel
	private boolean isRedo = false; //depend if redo are done
	
	//save the operate image only if it need in image draw mode
	private BufferedImage operateImg;

	public void setZoomSize(double x) {
		this.zoomSize = x;
	}

	public void setColor(Color c) {
		this.color = c;
	}

	public Color getColor() {
		return this.color;
	}

	public void setStroke(BasicStroke stroke) {
		this.stroke = stroke;
	}

	public BasicStroke getStroke() {
		return this.stroke;
	}

	public void setNormalType(String figure) {
		if (figure == "Image") {
			this.drawType = MainType.Image;
			return;
		} else if (figure == "Text") {
			System.out.println(figure);
		}
		this.normalType = NormalRectArea.valueOf(figure);
		this.drawType = MainType.RectArea;
	}

	public NormalRectArea getNormalType() {
		return this.normalType;
	}

	public void setPolygonFill(boolean fill) {
		this.polygonFill = fill;
	}

	public void setPoint(Point begin, Point end) {
		this.setPFrom(begin);
		this.setPTo(end);
	}

	public void setRele(boolean rele) {
		if (released == rele) {
			return;
		}
		this.released = rele;
	}

	public void setPFrom(Point p) {
		if (drawType == MainType.Tools) {
			pPre = new Point(p);
		}
		if (!pCollect.isEmpty()) {
			pCollect.clear();
		}
		if (isRedo)
			isRedo = false;
		pFrom = p;
		pTo = null;
	}

	public Point getPFrom() {
		if (pFrom == null)
			return null;
		return pFrom;
	}

	public void setPTo(Point p) {
		rectangle = new Rectangle(pFrom.x, pFrom.y, p.x - pFrom.x, p.y - pFrom.y);
		if (drawType == MainType.Tools) {
			if (pTo != null)
				pPre = new Point(pTo);
			rectangle = new Rectangle(0,0,0,0);
		}
		pTo = p;
	}

	public Point getPTo() {
		return pTo;
	}

	public void setPolygon(String polygon) {
		this.polygon = PolygonType.valueOf(polygon);
		drawType = MainType.MostPolygon;
	}

	public PolygonType getPolygonType() {
		return this.polygon;
	}

	public void setTools(String tools) {
		if (tools == "Text") {
			this.setNormalType(tools);
		} else {
			this.tools = Tools.valueOf(tools);
			drawType = MainType.Tools;
		}
	}

	public Tools getSelectedTools() {
		return this.tools;
	}

	public void setOperateIMG(BufferedImage img) {
		this.operateImg = img;
		this.drawType = MainType.Image;
	}

	public MainType getDrawType() {
		return this.drawType;
	}
	
	public Rectangle getRect() {
		return this.rectangle;
	}

	// #REGION SWITCH DRAW FUNCTION
	public void drawExecute(Graphics g) {
		drawExecute(g, false);
	}

	//Execute draw action to graphics
	public void drawExecute(Graphics g, boolean dashAround) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Point begin = new Point((int) (pFrom.x / zoomSize), (int) (pFrom.y / zoomSize));
		Point end = new Point((int) (pTo.x / zoomSize), (int) (pTo.y / zoomSize));
		if (dashAround) {
			Stroke dash = new BasicStroke((float) (1 /zoomSize), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0.5f,
					new float[] { 5, 5 }, 0f);
			g2d.setStroke(dash);
			g2d.setColor(Color.BLUE);
			g2d.drawRect(begin.x - 1, begin.y - 1, end.x - begin.x + 2, end.y - begin.y + 2);
		}
		g2d.setStroke(stroke);
		g2d.setColor(color);
		switch (drawType) {
		case Tools: {
			penDrawExecute(g2d, begin, end, rectangle.width, rectangle.height);
		}
			break;
		case Image: {
			if (operateImg == null)
				return;
			this.drawImage(g2d, this.operateImg, begin, end);
		}
			break;
		case RectArea: {
			drawRectArea(g2d, begin, end, end.x - begin.x, end.y - begin.y);
		}
			break;
		case MostPolygon: {
			if (this.polygon == null)
				return;
			Polygon p = MostPolygon.getValue(polygon, begin, end);
			if (polygonFill) {
				g2d.fillPolygon(p);
			} else {
				g2d.drawPolygon(p);
			}
		}
			break;
		case Other: {
			// otherExecute(operateImg);
		}
		default:
			break;
		}
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	/*
	 * Draw image
	 * */
	public void drawImage(Graphics2D g2d, BufferedImage img, Point begin, Point end) {
		g2d.drawImage(img, begin.x, begin.y, end.x - begin.x ,end.y - begin.y, null);
	}

	/*
	 * Draw with pen tools
	 * */
	private void penDrawExecute(Graphics2D g2d, Point begin, Point end, int width, int height) {

		switch (tools) {
		case Spray: {
			// paintSpray(end, g2d);
			int x1, x2, y1, y2;
			float x = stroke.getLineWidth();
			g2d.setStroke(new BasicStroke(1f));
			Random random = new Random();
			if (isRedo) {
				for (Point p : allPoint) {
					g2d.drawLine(p.x, p.y, p.x, p.y);
				}
			} else {
				x1 = end.x;
				y1 = end.y;
				for (int i = 0, j = 100; i < j; i++) {
					x2 = random.nextInt(25) - 12;
					y2 = random.nextInt(25) - 12;
					if (x2 * x2 + y2 * y2 > (j / 24 * x))
						continue;
					pCollect.add(new Point(x1 + x2, y1 + y2));
					g2d.drawLine(x1 + x2, y1 + y2, x1 + x2, y1 + y2);
				}
			}
		}
			break;
		case Eraser: {
			double radius = stroke.getLineWidth();
			Color c = g2d.getColor();
			g2d.setColor(Color.WHITE);
			if (isRedo) {
				for (Point p : allPoint) {
					g2d.fillOval((int) (p.x - radius), (int) (p.y - radius), (int) (radius * 2), (int) (radius * 2));
				}
			} else {
				g2d.fillOval((int) (end.x - radius), (int) (end.y - radius), (int) (radius * 2), (int) (radius * 2));
				pCollect.add(new Point(end));
			}
			g2d.setColor(c);
		}
			break;
		case Pen: {
			if (isRedo) {
				pPre = begin;
				for (Point p : allPoint) {
					g2d.drawLine(pPre.x, pPre.y, p.x, p.y);
					pPre = p;
				}
			} else {
				g2d.drawLine((int) (pPre.x / zoomSize), (int) (pPre.y / zoomSize), end.x, end.y);
				pCollect.add(new Point(end));
			}
		}
			break;
		case Paint:{
		}break;
		default:
			break;

		}
	}

	/*
	 * Draw shapes which is draw in a rectangle area
	 * */
	public void drawRectArea(Graphics2D g2d, Point begin, Point end, int width, int height) {
		switch (normalType) {
		case Line: {
			g2d.drawLine(begin.x, begin.y, end.x, end.y);
		}
			break;
		case Rect: {
			if (polygonFill) {
				g2d.fillRect(begin.x + 1, begin.y + 1, width - 2, height - 2);
			} else {
				g2d.drawRect(begin.x + 1, begin.y + 1, width - 2, height - 2);
			}
		}
			break;
		case Curve: {
			Point control = new Point();// Set control. Current state: no been
										// set
			((Graphics2D) (g2d)).draw(new QuadCurve2D.Double(begin.x, begin.y, control.x, control.y, end.x, end.y));
		}
			break;
		case Oval: {
			if (polygonFill) {
				g2d.fillOval(begin.x, begin.y, width, height);
			} else {
				g2d.drawOval(begin.x, begin.y, width, height);
			}
		}
			break;
		case RoundRect: {
			if (polygonFill) {
				g2d.fillRoundRect(begin.x, begin.y, width, height, 5, 5);
			} else {
				g2d.drawRoundRect(begin.x, begin.y, width, height, 5, 5);
			}
		}
			break;
		case Arc: {
			// g2d.drawArc(begin.x, begin.y, width, height, startAngle,
			// arcAngle);
			// g2d.fillArc(begin.x, begin.y, width, height, startAngle,
			// arcAngle);
		}
			break;
		default:
			break;
		}
	}
	// #ENDGION SWITCH DRAW FUNCTION

	/*
	 * Implements to clone interface
	 * */
	public Object clone() {
		Object o = null;
		if (drawType == MainType.Tools) {
			if (!released)
				return null;
			isRedo = true;
			allPoint = new Point[pCollect.size()];
			for (int i = 0; i < pCollect.size(); i++) {
				allPoint[i] = pCollect.get(i);
			}
			pCollect.clear();
		}
		try {
			o = (GraphicsAction) super.clone();

		} catch (CloneNotSupportedException e) {
			System.out.println(e.getMessage());
		}
		if (operateImg != null)
			operateImg = null;
		System.out.println("Clone : "+drawType);
		return o;
	}
}