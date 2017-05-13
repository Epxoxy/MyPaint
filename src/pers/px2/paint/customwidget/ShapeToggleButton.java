package pers.px2.paint.customwidget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JToggleButton;
import pers.px2.paint.paint.MostPolygon;

/*
 * Custom shape button for create button contain different shape
 * */
public class ShapeToggleButton extends JToggleButton {
	private static final long serialVersionUID = 1L;

	public ShapeToggleButton(String type) {
		super(type);
	}

	private void paintView(Graphics2D g) {
		int width = Math.min(this.getHeight() - 10, this.getWidth() - 10);
		Point p = new Point((this.getWidth() - width) / 2, (this.getHeight() - width) / 2);
		Point p2 = new Point(this.getWidth() - p.x, width + p.y);
		switch (this.getText()) {
		case "Line": {
			g.drawLine(p.x, (p.y + p2.y) / 2, p2.x, (p.y + p2.y) / 2);
		}break;
		case "Rect": {
			g.drawRect(p.x, p.y, p2.x - p.x, p2.y - p.y); 
		}break;
		default:{
			Polygon polygon = MostPolygon.getValue(getText(), p, p2);
			if (polygon != null)
				g.drawPolygon(polygon);
		}break;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
//		 super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(getBackground());
		if (this.isSelected()) {
			g2.setColor(Color.LIGHT_GRAY);
		}
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setColor(Color.BLACK);
		this.paintView(g2);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.dispose();
	}
}