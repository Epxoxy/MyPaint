package pers.px2.paint.paint;
import java.awt.Point;
import java.awt.Polygon;

public class MostPolygon {
	
	public static Polygon getValue(String str,Point X1, Point X2){
		for(PolygonType s:PolygonType.values()){
			if(str == s.toString()){
				return getValue(s,X1,X2);
			}
		}
		return null;
	}

	//All the method is use mathematical formula to get polygon object 
	public static Polygon getValue(PolygonType type, Point X1, Point X2) {
		Point[] p = null;
		Polygon polygon = new Polygon();
		switch (type) {
		case FourStar: {
			p = getFourStar(X1, X2);
		}
			break;
		case FiveStar: {
			p = getFiveStar(X1, X2);
		}
			break;
		case SixSlide: {
			p = getHexagonal(X1, X2);
		}
			break;
		case SixStar: {
			p = getSixStar(X1, X2);
		}
			break;
		case Pentagon: {
			p = getPentagonPoints(X1, X2);
		}
			break;
		case Arrow: {
			p = getArrow(X1, X2);
		}
			break;
		case Diamond: {
			p = getDiamond(X1, X2);
		}
			break;
		case PengQiang: {
		}
			break;
		case Triangle: {
			p = getTriangle(X1, X2);
		}
			break;
		case RightTriangle: {
			p = getRightTriangle(X1, X2);
		}
			break;
		case DialogBox: {
		}
			break;
		default:
			break;
		}
		for(int i = 0; i < p.length; i++){
			polygon.addPoint(p[i].x, p[i].y);
		}
		return polygon;
	}


	// This ratio is base on a rectangle with width and height both equal to 1
	static double heightRatio = 1d / 2 * (Math.tan(Math.toRadians(36)));
	static double widthRatio = (1d - heightRatio) / Math.tan(Math.toRadians(72));

	//Get pentagon
	public static Point[] getPentagonPoints(Point beginPoint, Point endPoint) {
		int height = endPoint.y - beginPoint.y;
		int width = endPoint.x - beginPoint.x;
		int heightNearY1 = (int) (height * heightRatio);
		int widthNearX1 = (int) (width * widthRatio);
		Point[] p = new Point[5];
		p[0] = new Point(beginPoint.x + width / 2, beginPoint.y);
		p[1] = new Point(beginPoint.x, beginPoint.y + heightNearY1);
		p[2] = new Point(beginPoint.x + widthNearX1, endPoint.y);
		p[3] = new Point(endPoint.x - widthNearX1, endPoint.y);
		p[4] = new Point(endPoint.x, beginPoint.y + heightNearY1);
		return p;
	}

	//Get five star, all five star point is consist of two pentagon
	public static Point[] getFiveStar(Point beginPoint, Point endPoint) {
		Point[] pOut;
		Point[] p2In;
		Point[] p = new Point[10];
		pOut = getPentagonPoints(beginPoint, endPoint);

		int height = endPoint.y - beginPoint.y;
		int width = endPoint.x - beginPoint.x;
		double bottomAccupyHeiPer = ((1d / 2 - widthRatio) / Math.tan(Math.toRadians(54)));
		double innerPentagonHeiPer = 1d - heightRatio - bottomAccupyHeiPer;
		double innerPentagonWidPer = innerPentagonHeiPer * heightRatio * 2 / Math.tan(Math.toRadians(36));
		double leftPartPer = (1d - innerPentagonWidPer) / 2;
		Point p1 = new Point(beginPoint.x + (int) (width * leftPartPer), beginPoint.y + (int) (height * heightRatio));
		Point p2 = new Point(endPoint.x - (int) (width * leftPartPer),
				endPoint.y - (int) (height * bottomAccupyHeiPer));
		p2In = getPentagonPoints(p2, p1);

		for (int i = 0, j = 0, k = 3; i < p.length; i++) {
			if (i % 2 == 0) {
				p[i] = new Point(pOut[j].x, pOut[j].y);
				j++;
			} else {
				p[i] = new Point(p2In[k].x, p2In[k].y);
				if (k == 4)
					k = -1;
				k++;
			}
		}
		return p;
	}

	//Get Arrow
	public static Point[] getArrow(Point beginPoint, Point endPoint) {
		int height = endPoint.y - beginPoint.y;
		int width = endPoint.x - beginPoint.x;
		Point[] p = new Point[7];
		p[0] = new Point(beginPoint.x + width / 2, beginPoint.y);
		p[1] = new Point(beginPoint.x + width / 2, beginPoint.y + height / 4);
		p[2] = new Point(beginPoint.x, beginPoint.y + height / 4);
		p[3] = new Point(beginPoint.x, endPoint.y - height / 4);
		p[4] = new Point(beginPoint.x + width / 2, endPoint.y - height / 4);
		p[5] = new Point(beginPoint.x + width / 2, endPoint.y);
		p[6] = new Point(endPoint.x, endPoint.y - height / 2);
		return p;
	}

	//Get Triangle
	public static Point[] getTriangle(Point beginPoint, Point endPoint) {
		Point[] p = new Point[3];
		int width = endPoint.x - beginPoint.x;

		p[0] = new Point(beginPoint.x + width / 2, beginPoint.y);
		p[1] = new Point(beginPoint.x, endPoint.y);
		p[2] = new Point(endPoint.x, endPoint.y);
		return p;

	}

	//Get RightTriangle
	public static Point[] getRightTriangle(Point beginPoint, Point endPoint) {
		Point[] p = new Point[3];
		p[0] = new Point(beginPoint.x, beginPoint.y);
		p[1] = new Point(beginPoint.x, endPoint.y);
		p[2] = new Point(endPoint.x, endPoint.y);
		return p;

	}

	//Get FourStar
	public static Point[] getFourStar(Point beginPoint, Point endPoint) {
		Point[] p = new Point[8];
		int height = endPoint.y - beginPoint.y;
		int width = endPoint.x - beginPoint.x;

		double widthToInnerPer = (1d / 2 *(1- (Math.tan(Math.toRadians(15)))));
		int widthToInner = (int) (width * widthToInnerPer);
		int heightToInner = (int)(height *widthToInnerPer);
		
		p[0] = new Point(beginPoint.x + width / 2, beginPoint.y);
		p[1] = new Point(beginPoint.x + widthToInner, beginPoint.y + heightToInner);
		p[2] = new Point(beginPoint.x, beginPoint.y + height / 2);
		p[3] = new Point(beginPoint.x + widthToInner, endPoint.y - heightToInner);
		p[4] = new Point(beginPoint.x + width / 2, endPoint.y);
		p[5] = new Point(endPoint.x - widthToInner, endPoint.y - heightToInner);
		p[6] = new Point(endPoint.x, beginPoint.y + height / 2);
		p[7] = new Point(endPoint.x - widthToInner, beginPoint.y + heightToInner);
		return p;
	}

	//Get Hexagonal
	public static Point[] getHexagonal(Point beginPoint, Point endPoint) {
		int width = endPoint.x - beginPoint.x;
		int height = endPoint.y - beginPoint.y;
		Point[] p = new Point[6];
		p[0] = new Point(beginPoint.x + width / 2, beginPoint.y);
		p[1] = new Point(beginPoint.x, beginPoint.y + height / 4);
		p[2] = new Point(beginPoint.x, beginPoint.y + height * 3 / 4);
		p[3] = new Point(beginPoint.x + width / 2, endPoint.y);
		p[4] = new Point(endPoint.x, beginPoint.y + height * 3 / 4);
		p[5] = new Point(endPoint.x, beginPoint.y + height / 4);
		return p;
	}

	//pointRotate
	public static Point pointRotate(int i, Point x0, Point p) {
		double sinx = Math.sin(Math.toRadians(i));
		double conx = Math.cos(Math.toRadians(i));
		Point x1 = new Point();
		x1.x = (int) ((p.x - x0.x) * conx + (p.y - x0.y) * sinx + x0.x);
		x1.y = (int) (-(p.x - x0.x) * sinx + (p.y - x0.y) * conx + x0.y);
		return x1;
	}

	//Get SixStar, all six star point is consist of two hexagonal 
	public static Point[] getSixStar(Point beginPoint, Point endPoint) {
		Point[] p = new Point[12];
		int width = endPoint.x - beginPoint.x;
		int height = endPoint.y - beginPoint.y;
		int x0 = (endPoint.x + beginPoint.x) / 2;
		int y0 = (endPoint.y + beginPoint.y) / 2;
		Point center = new Point(x0, y0);
		Point[] cache = getHexagonal(beginPoint, endPoint);

		Point x1 = new Point(beginPoint.x + width / 6, endPoint.y - height / 4);
		Point x2 = new Point(endPoint.x - width / 6, beginPoint.y + height / 4);
		Point innerBegin = pointRotate(90, center, x1);
		Point innerEnd = pointRotate(90, center, x2);

		Point[] cache2 = getHexagonal(innerBegin, innerEnd);

		for (int i = 0, j = 0, k = 2; i < p.length; i++) {
			if (i % 2 == 0) {
				p[i] = new Point(cache[j].x, cache[j].y);
				j++;
			} else {
				int temp = k;
				if (k > 5) {
					temp = k - 6;
				}
				Point tp = pointRotate(90, center, cache2[temp]);
				p[i] = new Point(tp.x, tp.y);
				k++;
			}
		}
		return p;
	}

	//Get Diamond
	public static Point[] getDiamond(Point beginPoint, Point endPoint) {
		Point[] p = new Point[4];
		int width = endPoint.x - beginPoint.x;
		int hieght = endPoint.y - beginPoint.y;
		p[0] = new Point(beginPoint.x + width / 2, beginPoint.y);
		p[1] = new Point(beginPoint.x, beginPoint.y + hieght / 2);
		p[2] = new Point(beginPoint.x + width / 2, endPoint.y);
		p[3] = new Point(endPoint.x, beginPoint.y + hieght / 2);
		return p;
	}

}