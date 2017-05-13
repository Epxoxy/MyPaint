package pers.px2.paint.customwidget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import pers.px2.paint.uiresource.CSScrollBarUI;


/*
 * Custom tools grid, it is a area contain header button and bottom title and button group
 * */
public class ToolsGridPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private int row = 0;
	private JComponent itemsPanel;
	private AbstractButton headerBtn;
	private AbstractButton header2Btn;
	private JPopupMenu popupItem;
	private CustomButton popupHeader;
	private String labelText;

	public ToolsGridPanel(AbstractButton header, AbstractButton header2, String label) {
		setPreferredSize(new Dimension(140, 94));
		setMaximumSize(new Dimension(140, 94));
		setLayout(new BorderLayout(0, 0));
		this.headerBtn = header;
		this.header2Btn = header2;
		this.labelText = label;
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
		initItem();
	}

	public ToolsGridPanel(AbstractButton header, String label) {
		this(header, null, label);
	}

	public ToolsGridPanel(String labelText) {
		this(null, null, labelText);
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setPopup(boolean isPopup) {
		if (isPopup) {
			if (itemsPanel != null) {
				popupItem = new JPopupMenu();
				popupItem.add(itemsPanel);
				popupHeader = new CustomButton(labelText);
				popupHeader.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						showPopup(80);
					}

				});
				this.add(popupHeader);
				this.remove(itemsPanel);
			}
			this.setPreferredSize(new Dimension(60, 60));
		}
		this.updateUI();
		System.gc();
	}

	public void showPopup(int height) {
		if (popupItem != null)
			popupItem.show(this, 0, height);
	}

	public void addItem(AbstractButton[] btn) {
		if (row == 0)
			this.row = (int) Math.sqrt(btn.length);
		if (itemsPanel == null) {
			itemsPanel = new JPanel();
			itemsPanel.setLayout(new GridLayout(0, row, 0, 0));
//			itemsPanel.setBorder(new LineBorder(Color.WHITE));
			itemsPanel.setPreferredSize(new Dimension(80, 140));
			this.add(itemsPanel);
		}
		for (int i = 0; i < btn.length; i++) {
			btn[i].addActionListener(new popupMenuHandler());
			this.itemsPanel.add(btn[i]);
		}
	}

	private class popupMenuHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (popupItem == null)
				return;
			popupItem.setVisible(false);
			popupHeader.setText(((AbstractButton) e.getSource()).getText());
		}
	}

	public void setItemsScroll(JScrollPane scrollPane) {
		scrollPane.getHorizontalScrollBar().setUI(new CSScrollBarUI());
		scrollPane.getVerticalScrollBar().setUI(new CSScrollBarUI());
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setPreferredSize(new Dimension(80, 50));
		scrollPane.setViewportView(itemsPanel);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		this.remove(itemsPanel);
		this.add(scrollPane);
	}

	private void initItem() {
		if (headerBtn != null) {
			MetalToggleButtonUI noSelectColorUI = new MetalToggleButtonUI() {
				@Override
				protected Color getSelectColor() {
					return null;
				}
			};
			headerBtn.setUI(noSelectColorUI);
			if (header2Btn == null) {
				headerBtn.setPreferredSize(new Dimension(50, 80));
				this.add(headerBtn, BorderLayout.WEST);
			} else {
				JPanel headerPanel = new JPanel();
				ButtonGroup headerGroup = new ButtonGroup();
				header2Btn.setUI(noSelectColorUI);
				headerPanel.setLayout(new GridLayout(0, 1, 0, 0));
				headerPanel.setPreferredSize(new Dimension(50, 80));
				headerGroup.add(headerBtn);
				headerGroup.add(header2Btn);
				headerPanel.add(headerBtn);
				headerPanel.add(header2Btn);
				this.add(headerPanel, BorderLayout.WEST);
			}
		}
		if (labelText != null) {
			JLabel label = new JLabel(labelText, JLabel.CENTER);
			label.setForeground(Color.GRAY);
			this.add(label, BorderLayout.SOUTH);
		}

	}
}