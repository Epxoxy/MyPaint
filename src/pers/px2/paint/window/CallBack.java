package pers.px2.paint.window;

import java.awt.Color;

//interface  : use to callback and set something on main frame
public interface CallBack {
	void setColorHeader(Color color);
	void setPosition(int x, int y);
	void setDashRect(int x, int y);
	void setResolution(int width, int height);
	void setViewPaneSize(int width, int height);
	void enabledUndo(boolean enable);
	void enabledRedo(boolean enable);
}
