package pers.px2.paint.uiresource;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CSScrollBarUI extends BasicScrollBarUI {
	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds){
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(c.getBackground());
		g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
	}
	@Override
	public Dimension getPreferredSize(JComponent c){
		return new Dimension(6,6);
	}
	
	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds){
		Graphics2D g2 = (Graphics2D)g;
		g2.translate(thumbBounds.x, thumbBounds.y);
		g2.setColor(Color.GRAY);
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.addRenderingHints(rh);
		g2.fillRoundRect(0, 0, thumbBounds.width, thumbBounds.height - 1, 5, 5);
		g2.translate(-thumbBounds.x, -thumbBounds.y);
	}
	@Override
	protected JButton createIncreaseButton(int orientation){
		JButton button = new JButton();
		button.setBorder(null);
		return button;
	}
	
	@Override
	protected JButton createDecreaseButton(int orientation){
		JButton button = new JButton();
		button.setBorder(null);
		return button;
	}

}
