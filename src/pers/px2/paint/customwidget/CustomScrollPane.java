package pers.px2.paint.customwidget;

import javax.swing.JScrollPane;

import pers.px2.paint.uiresource.CSScrollBarUI;

public class CustomScrollPane extends JScrollPane{
	private static final long serialVersionUID = 1L;

	public CustomScrollPane(){
		super();
		this.getVerticalScrollBar().setUI(new CSScrollBarUI());
		this.getHorizontalScrollBar().setUI(new CSScrollBarUI());
	}
}
