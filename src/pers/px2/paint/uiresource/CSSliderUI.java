package pers.px2.paint.uiresource;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JSlider;
import javax.swing.plaf.metal.MetalSliderUI;

public class CSSliderUI extends MetalSliderUI {
	public CSSliderUI() {
		super();
	}

	public void paintThumb(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setPaint(Color.LIGHT_GRAY);
		g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
		g2d.setPaint(Color.WHITE);
		g2d.fillOval(thumbRect.x + 1, thumbRect.y + 1, thumbRect.width - 2, thumbRect.height - 2);
		g2d.setPaint(Color.GRAY);
		g2d.fillOval(thumbRect.x + thumbRect.width / 2 - 2, thumbRect.y + thumbRect.height / 2 - 2, 4, 4);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	public void paintTrack(Graphics g) {
		if (slider.getOrientation() == JSlider.HORIZONTAL) {
			int cy = (trackRect.height / 2) - 2;
			int cw = trackRect.width;
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// Move start point of paint, I think it may in the point of (0,0) before
			g2.translate(trackRect.x, trackRect.y + cy);

			g2.setPaint(Color.LIGHT_GRAY);
			g2.fillRoundRect(0, 0, cw, cy, cy, cy);

			int width = thumbRect.x + (thumbRect.width / 2) - trackRect.x;

			g2.setPaint(Color.GREEN);
			g2.fillRoundRect(0, 0, width, cy, cy, cy);

			g2.setPaint(slider.getBackground());
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

			// Move paint point back;
			g2.translate(-trackRect.x, -(trackRect.y + cy));
		} else {
			super.paintTrack(g);
		}
	}
}