package pers.px2.paint.paint;

import java.awt.Cursor;
import java.awt.Point;

public class DragVaries {
	private PlacesMode places;
	private int CursorMode;//Save current cursor mode of the mouse
	private int x_ToDrag;//Save the distance to x of drag press point
	private int y_ToDrag;//Save the distance to y of drag press point
	private int width;//Save the width of drag area
	private int height;//Save the height of drag area
	private Point leftTop = new Point();
	private Point rightBottom = new Point();

	//enum, Night  position 
	private enum PlacesMode {
		horiLeft, horiRight, veriTop, veriBottom, leftTop, leftBottom, rightTop, rightBottom, drag
	}

	//enum, the way mouse point close to one of edit rectangle's line
	private enum NearSideMode {
		xBeside, yBeside, xyBeside, xyFar
	}

	//If the mouse is in the inner of edit rectangle
	public boolean isInside(Point p) {
		if (p.x < (leftTop.x - 10) || p.y < (leftTop.y - 10) || p.x > (rightBottom.x + 10) || p.y > (rightBottom.y + 10)) {
			return false;
		}
		return true;
	}

	//Set current mouse drag point
	public void setDragPoint(Point x) {
		this.x_ToDrag = x.x - leftTop.x;
		this.y_ToDrag = x.y - leftTop.y;
	}

	//Set current edit rectangle area
	public void setRect(Point start, Point end) {
		this.leftTop = start;
		this.rightBottom = end;
		this.width = end.x - start.x;
		this.height = end.y - start.y;
	}

	//Get the cursor mode
	public int getCursorMode(Point p) {
		return confirmPlacesMode(p);
	}

	//Get the NearSideMode of one point
	private NearSideMode getNearMode(Point p1, Point p2) {
		NearSideMode pn = NearSideMode.xyFar;
		if (Math.abs(p1.x - p2.x) <= 10) {
			pn = NearSideMode.xBeside;
		}
		if (Math.abs(p1.y - p2.y) <= 10) {
			if (pn == NearSideMode.xBeside)
				return NearSideMode.xyBeside;
			return NearSideMode.yBeside;
		}
		return pn;
	}

	//Confirm the places mode by point
	private int confirmPlacesMode(Point p) {
		places = null;
		NearSideMode pNear = getNearMode(leftTop, p);
		if (pNear != NearSideMode.xyFar) {
			if (pNear == NearSideMode.xBeside) {
				if (Math.abs(p.y - rightBottom.y) <= 10) {
					places = PlacesMode.leftBottom;
					CursorMode = Cursor.SW_RESIZE_CURSOR;
				} else {
					places = PlacesMode.horiLeft;
					CursorMode = Cursor.W_RESIZE_CURSOR;
				}
			} else if (pNear == NearSideMode.yBeside) {
				if (Math.abs(p.x - rightBottom.x) <= 10) {
					places = PlacesMode.rightTop;
					CursorMode = Cursor.NE_RESIZE_CURSOR;
				} else {
					places = PlacesMode.veriTop;
					CursorMode = Cursor.N_RESIZE_CURSOR;
				}
			} else {
				places = PlacesMode.leftTop;
				CursorMode = Cursor.NW_RESIZE_CURSOR;
			}
			if (places != null)
				return CursorMode;
		}

		pNear = getNearMode(rightBottom, p);
		if (pNear != NearSideMode.xyFar) {
			if (pNear == NearSideMode.xBeside) {
				places = PlacesMode.horiRight;
				CursorMode = Cursor.E_RESIZE_CURSOR;
			} else if (pNear == NearSideMode.yBeside) {
				places = PlacesMode.veriBottom;
				CursorMode = Cursor.S_RESIZE_CURSOR;
			} else {
				places = PlacesMode.rightBottom;
				CursorMode = Cursor.SE_RESIZE_CURSOR;
			}
			if (places != null)
				return CursorMode;
		}
		if (places == null) {
			places = PlacesMode.drag;
			CursorMode = Cursor.MOVE_CURSOR;
		}
		return CursorMode;
	}

	//When drag executed, Use this method to change rectangle area
	public void dragExecute(Point mousePoint, Point _beginPoint, Point _endPoint) {
		if(places == null )return;
		switch (places) {// dragMode
		case horiLeft: {
			if (mousePoint.x < _endPoint.x) {
				_beginPoint.x = mousePoint.x;
			}
		}
			break;
		case veriTop: {
			if (mousePoint.y < _endPoint.y) {
				_beginPoint.y = mousePoint.y;
			}
		}
			break;
		case horiRight: {
			if (mousePoint.x > _beginPoint.x) {
				_endPoint.x = mousePoint.x;
			}
		}
			break;
		case veriBottom: {
			if (mousePoint.y > _beginPoint.y) {
				_endPoint.y = mousePoint.y;
			}
		}
			break;
		case leftBottom: {
			if (mousePoint.x < _endPoint.x) {
				_beginPoint.x = mousePoint.x;
			}
			if (mousePoint.y > _beginPoint.y) {
				_endPoint.y = mousePoint.y;
			}
		}
			break;
		case leftTop: {
			if (mousePoint.x < _endPoint.x) {
				_beginPoint.x = mousePoint.x;
			}
			if (mousePoint.y < _endPoint.y) {
				_beginPoint.y = mousePoint.y;
			}
		}
			break;
		case rightBottom: {
			if (mousePoint.x > _beginPoint.x) {
				_endPoint.x = mousePoint.x;
			}
			if (mousePoint.y > _beginPoint.y) {
				_endPoint.y = mousePoint.y;
			}
		}
			break;
		case rightTop: {
			if (mousePoint.x > _beginPoint.x) {
				_endPoint.x = mousePoint.x;
			}
			if (mousePoint.y < _endPoint.y) {
				_beginPoint.y = mousePoint.y;
			}
		}
			break;
		case drag: {
			_beginPoint.x = mousePoint.x - x_ToDrag;
			_beginPoint.y = mousePoint.y - y_ToDrag;
			_endPoint.x = _beginPoint.x + width;
			_endPoint.y = _beginPoint.y + height;
		}
			break;
		}
		this.setRect(_beginPoint, _endPoint);
	}
}
