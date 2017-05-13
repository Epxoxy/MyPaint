package pers.px2.paint.customwidget;

import javax.swing.JSlider;
import pers.px2.paint.uiresource.CSSliderUI;

public class CustomSlider extends JSlider{
	private static final long serialVersionUID = 1L;

	public CustomSlider(){
		this.setUI(new CSSliderUI());
	}

	public CustomSlider(int i, int j, int k) {
		super(i,j,k);
		this.setUI(new CSSliderUI());
	}
}
