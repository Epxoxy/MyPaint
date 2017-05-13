package pers.px2.paint.uiresource;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;

public class CSButtonUI extends BasicButtonUI {
	protected static CSButtonUI singleton = new CSButtonUI();
	private Color cHightLight = new Color(Integer.parseInt("238E78", 16));

	public static ComponentUI createUI(JComponent c) {
		return singleton;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();
		boolean pressed = (model.isArmed() && model.isPressed()) || model.isSelected();
		Rectangle viewRect = new Rectangle(0, 0, b.getWidth(), b.getHeight());
		g2.setPaint(pressed ? Color.LIGHT_GRAY:b.getBackground());
		g2.fill(viewRect);
		if(model.isRollover()){
			b.setForeground(cHightLight);
		}
		else{
			b.setForeground(Color.BLACK);
		}
		super.paint(g, c);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		AbstractButton b = (AbstractButton) c;
		Dimension dim = super.getPreferredSize(c);
		dim.height += (b.getMargin().top + b.getMargin().bottom);
		dim.width += (b.getMargin().left + b.getMargin().right);
		return dim;
	}
}
