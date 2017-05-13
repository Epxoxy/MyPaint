package pers.px2.paint.customwidget;

import java.awt.Insets;

import javax.swing.JButton;

import pers.px2.paint.uiresource.CSButtonUI;

/*
 * Custom button for which button like to used some wanted UI.
 * */
public class CustomButton extends JButton {
	private static final long serialVersionUID = 1L;

	public CustomButton(String text) {
		super(text);
		this.setUI(CSButtonUI.createUI(this));
		this.setMargin(new Insets(0, 0, 0, 0));
		this.setContentAreaFilled(true);
		this.setBorder(null);
	}

	public CustomButton() {
		this("");
	}
}