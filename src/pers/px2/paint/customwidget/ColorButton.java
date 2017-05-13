package pers.px2.paint.customwidget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

enum MyColor {
	BLACK("000000"), RED("FF0000"), ORANGE("FFA500"), GREEN("00FF00"), PURPLE("A020F0"), BLUE("0000FF"), DARK_GRAY(
			"696969"), BROWN("A52A2A"), GOLD("FFD700"), LIME_GREEN("32CD32"), MAGENTA("FF00FF"), TURQUOISE(
					"40E0D0"), GRAY("BEBEBE"), DARK_RED("8B0000"), YELLOW("FFFF00"), CYAN("00FFFF"), PINK(
							"FFC0CB"), SKYBLUE("87CEEB"), WHITE("FFFFFF"), INDIGO("4B0082"), LIGHT_YELLOW(
									"FFFFE0"), LAVENDER("E6E6FA"), MISTYROSE("FFE4E1");

	public String value;

	private MyColor(String value) {
		this.value = value;
	}

}
/*
 * Custom color button for create button fill with different color.
 * */
public class ColorButton extends CustomButton {
	private static final long serialVersionUID = 1L;

	public ColorButton(String text) {
		super();
		setContentAreaFilled(false);
		Color color = null;
		if (text != "") {
			color = new Color(Integer.parseInt(MyColor.valueOf(text).value, 16));
		}
		setBackground(color);
		setFocusPainted(false);
		setMargin(new Insets(0, 0, 0, 0));
	}

	public ColorButton() {
		this("");
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if (this.isSelected()) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.BLACK);
			g2.drawRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.dispose();
	}
}