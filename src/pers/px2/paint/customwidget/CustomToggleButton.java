package pers.px2.paint.customwidget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JToggleButton;
import pers.px2.paint.uiresource.CSButtonUI;

/*
 * Custom toggle button
 * */
public class CustomToggleButton extends JToggleButton {
	private static final long serialVersionUID = 1L;

	public CustomToggleButton(String text) {
		super(text);
		this.setUI(CSButtonUI.createUI(this));
		this.setMargin(new Insets(0, 0, 0, 0));
		this.setFocusable(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if (this.isSelected()) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.LIGHT_GRAY);
			g2.setStroke(new BasicStroke(2f));
			g2.drawRect(0, 0, this.getWidth() - 2, this.getHeight() - 2);
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.dispose();
	}
}
