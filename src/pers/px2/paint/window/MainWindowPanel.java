package pers.px2.paint.window;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import pers.px2.paint.paint.MainPaintPanel;
import pers.px2.paint.customwidget.*;

public class MainWindowPanel {

	//Main command
	private enum CommandSet {
		COLOR('0'), SHAPES('1'), IMAGE('2'), BRUSHES('3'), PEN_TOOLS('4'), SIZES('5');
		private CommandSet(char cmd) {
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindowPanel window = new MainWindowPanel();
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainWindowPanel() {
		initialize();
	}

	private JFrame frame;
	private JPanel toolsCollectPane;
	private MainPaintPanel panel;
	private AbstractButton[] shapesTools;
	private AbstractButton[] colorTools;
	private AbstractButton[] imgTools;
	private AbstractButton[] penTools;
	private AbstractButton[] sizeTools;
	private AbstractButton[] brushesTools;
	private AbstractButton selectedColorHeader;
	private CustomSlider slider;
	private JLabel zoomLabel;
	private JLabel position;
	private JLabel editArea;
	private JPanel controlPaint;
	private CustomScrollPane paintView;
	private JLabel resoluTips;
	private CustomButton undoBtn;
	private CustomButton redoBtn;
	private AbstractButton imgBtn;

	/*
	 * initialize main frame layout
	 */
	private void initialize() {
		frame = new JFrame("Paint");
		frame.setBounds(100, 100, 720, 480);
		frame.setMinimumSize(new Dimension(600, 480));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addComponentListener(new WindowStateHandler());
		frame.setIconImage(frame.getToolkit().getImage(getClass().getResource("icon/icon.PNG"))); 

		/*
		 * The Top part of the main frame : Top tools bar contain the menu bar
		 * and main function area, Those two part are using most same handler to
		 * reach same function
		 */
		JPanel toolMainBar = new JPanel();
		toolMainBar.setLayout(new BorderLayout());
		frame.getContentPane().add(toolMainBar, BorderLayout.NORTH);

		/*
		 * Add MenuBar in the top
		 * MenuBar is a class extends JMenuBar and use custom style
		 */
		MenuBar menuBar = new MenuBar();// Add fontMenu
		toolMainBar.add(menuBar, BorderLayout.NORTH);

		/*
		 * Main function area of shortcut of menu.
		 * toolsCollectPane is the panel to contain those function area.
		 */
		toolsCollectPane = new JPanel();
		toolsCollectPane.setLayout(new BoxLayout(toolsCollectPane, BoxLayout.X_AXIS));
		toolMainBar.add(toolsCollectPane, BorderLayout.CENTER);

		// Brushes function button area
		ToolsGridPanel brushes = new ToolsGridPanel("Brushes");
		brushes.addItem(brushesTools);
		brushes.setPopup(true);
		toolsCollectPane.add(brushes);

		// Pen Sizes function button area
		ToolsGridPanel sizeGrid = new ToolsGridPanel("Size");
		sizeGrid.addItem(sizeTools);
		sizeGrid.setPopup(true);
		toolsCollectPane.add(sizeGrid);

		// Tools function button area
		ToolsGridPanel penGrid = new ToolsGridPanel("Tools");
		penGrid.setPreferredSize(new Dimension(120, 94));
		penGrid.addItem(penTools);
		toolsCollectPane.add(penGrid);

		// Image function button area
		imgBtn = new CustomToggleButton("Image");
		imgBtn.addActionListener(new SetImageHandler());
		ToolsGridPanel image = new ToolsGridPanel(imgBtn, "Image");
		image.setPreferredSize(new Dimension(120, 94));
		image.addItem(imgTools);
		toolsCollectPane.add(image);

		// Color function button area
		// Color1 and color2 button is the header of all color button.Its show
		// the selected color who be selected.
		AbstractButton colorBtn1 = new CustomToggleButton("Color1");
		colorBtn1.setSelected(true);
		colorBtn1.setBackground(Color.BLACK);
		colorBtn1.addActionListener(new SetColorHandler());

		AbstractButton colorBtn2 = new CustomToggleButton("Color2");
		colorBtn2.addActionListener(new SetColorHandler());

		AbstractButton colorSet = new CustomButton("Custom Color");// Custom
																	// color by
																	// JColorChooser.
		colorSet.addActionListener(new CustomColorHandler());
		selectedColorHeader = colorBtn1;
		ToolsGridPanel color = new ToolsGridPanel(colorBtn1, colorBtn2, "Color");
		color.setRow(6);
		color.addItem(colorTools);
		color.add(colorSet, BorderLayout.NORTH);
		toolsCollectPane.add(color);

		// Shapes function button area
		CustomScrollPane scrollPane = new CustomScrollPane();
		ToolsGridPanel shapes = new ToolsGridPanel("Shapes");
		shapes.addItem(shapesTools);
		shapes.setItemsScroll(scrollPane);
		CustomToggleButton fillBtn = new CustomToggleButton("InFill");// fillBtn turn the
															// fill when paint
															// most polygon.
		fillBtn.setActionCommand("Fill");
		fillBtn.addActionListener(new SetShapesHandler());
		fillBtn.setSelected(false);
		shapes.add(fillBtn, BorderLayout.WEST);
		toolsCollectPane.add(shapes);

		/*
		 * The center part of the main frame : Main paint panel JScrollPane
		 * contain "controlPaint" who is contain "PaintPanel"
		 */
		// controlPaint to contain the PaintPanel
		controlPaint = new JPanel();
		controlPaint.setBackground(new Color(Integer.parseInt("CCFFCC", 16)));
		controlPaint.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		// Main paint panel : PaintPanel
		panel = new MainPaintPanel();
		panel.addCallBackHandler(new CallBackHandlerOfState());
		panel.setBorder(new LineBorder(Color.BLUE));
		controlPaint.add(panel);

		// JScrollPane contain controlPaint.So that to view big zoom.
		paintView = new CustomScrollPane();
		paintView.setBorder(null);
		paintView.getVerticalScrollBar().setUnitIncrement(10);
		paintView.setViewportView(controlPaint);
		frame.getContentPane().add(paintView, BorderLayout.CENTER);

		/*
		 * The bottom part of the main frame
		 * This part add some control to show draw detail and control zoom
		 */
		// bottom area panel
		JPanel panelBottom = new JPanel();
		panelBottom.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
		frame.getContentPane().add(panelBottom, BorderLayout.SOUTH);

		// detailPanel show the message of position ,edit area and resolution.
		JPanel detailPanel = new JPanel();
		panelBottom.add(detailPanel);

		// Show the mouse position of the PaintPanel
		position = new JLabel("px", JLabel.LEFT);
		position.setPreferredSize(new Dimension(100, 20));
		detailPanel.add(position);

		editArea = new JLabel("px", JLabel.LEFT);
		editArea.setPreferredSize(new Dimension(110, 20));
		detailPanel.add(editArea);

		// Show current editing image resolution
		resoluTips = new JLabel("DPI : " + 800 + "x" + 480 + "px", JLabel.LEFT);
		resoluTips.setPreferredSize(new Dimension(110, 20));
		detailPanel.add(resoluTips);
		 //zoomLabel is using to  show current zoom.
		zoomLabel = new JLabel("100%", JLabel.CENTER);
		zoomLabel.setPreferredSize(new Dimension(30, 30));
		panelBottom.add(zoomLabel);

		// ZoomOut button, function : zoom out draw area;
		CustomButton zoomOut = new CustomButton("Out");
		zoomOut.setPreferredSize(new Dimension(30, 30));
		zoomOut.setActionCommand("ZOOM_OUT");
		zoomOut.addActionListener(new ZoomHandler());
		panelBottom.add(zoomOut);

		// Slider of control zoom, 
		//ZoomIn and ZoomOut button is using this to make effect
		//Change the slider's value can effect by slider state handler
		slider = new CustomSlider(0, 10, 3);
		slider.setMajorTickSpacing(1);
		slider.setSnapToTicks(true);
		slider.addChangeListener(new SliderStateHandler());
		panelBottom.add(slider);

		// ZoomIn button, zoom in paint area
		CustomButton zoomIn = new CustomButton("In");
		zoomIn.setPreferredSize(new Dimension(30, 30));
		zoomIn.setActionCommand("ZOOM_IN");
		zoomIn.addActionListener(new ZoomHandler());
		panelBottom.add(zoomIn);

		// Undo button, undo current draw.
		undoBtn = new CustomButton("undo");
		undoBtn.setEnabled(false);
		undoBtn.addActionListener(new RedoOrUndoHandler());
		panelBottom.add(undoBtn);

		// Redo button, redo undo draw.
		redoBtn = new CustomButton("redo");
		redoBtn.setEnabled(false);
		redoBtn.addActionListener(new RedoOrUndoHandler());
		panelBottom.add(redoBtn);
	}

	/*
	 * Custom MenuBar on the top
	 * Add and initialize menu on MenuBar
	 * Add handler to MenuItem 
	 */
	private class MenuBar extends JMenuBar {
		private static final long serialVersionUID = 1L;
		private String[] menuItemName;

		public MenuBar() {
			this.setBackground(Color.WHITE);
			initMenuItem();
		}

		/*
		 * Initialize the menuItem in menuBar
		 */
		private void initMenuItem() {
			JMenu mnFile = new JMenu("File");
			JMenu mnEdit = new JMenu("Edit");
			JMenu mnView = new JMenu("View");
			FontMenu fmFont = new FontMenu("Font");
			initFuncItem(mnEdit);
			this.add(mnFile);
			this.add(mnEdit);
			this.add(mnView);
			this.add(fmFont);

			menuItemName = new String[] { "New", "Open", "Save", "Save As", "Properties", "About", "Exit" };
			JMenuItem[] menuItemCollect = new JMenuItem[menuItemName.length];
			ActionListener handler = new FileMenuHandler();
			for (int i = 0; i < menuItemName.length; i++) {
				menuItemCollect[i] = new JMenuItem(menuItemName[i]);
				menuItemCollect[i].addActionListener(handler);
				mnFile.add(menuItemCollect[i]);
			}

			JMenuItem mnItemShow = new JMenuItem("Show/Hide");
			mnItemShow.addActionListener(handler);
			mnView.add(mnItemShow);
		}

		/*
		 * File handler of file menu 
		 * Include : new a file, open a file, save a file, save a file as, show properties, about, exit. 
		 * This class include a inner private
		 * class of image filter to filter file when open or save.
		 */
		private class FileMenuHandler implements ActionListener {
			// Normal extension of image file
			private String[][] ext2 = { { ".jpg", ".jpeg", ".jpe", ".jfif" }, { ".bmp", ".dib" }, { ".bmp", ".dib" },
					{ ".bmp", ".dib" }, { ".bmp", ".dib" }, { ".gif" }, { ".tif", ".tiff" }, { ".png" } };
			private JFileChooser chooser;
			private String openFilePath = null;

			public void initChooser() {
				if (chooser != null)
					return;
				chooser = new JFileChooser();
				new ImageFilter().setChooser(chooser);
			}

			// Get if a file name contain right extension
			private boolean isRightExt(String ext) {
				String ext1 = ext.toLowerCase();
				for (int i = 0; i < ext2.length; i++) {
					for (int j = 0; j < ext2[i].length; j++) {
						if (ext1.equals(ext2[i][j])) {
							return true;
						}
					}
				}
				return false;
			}

			// This class include a inner private class of image filter to
			// filter file when open or save.
			private class ImageFilter extends FileFilter {
				private String[] ext;
				private String description;
				private String[] descrip = { "JPEG", "Monochrome Bitmap", "16 Color Bitmap", "256 Color Bitmap",
						"24-bit Bitmap", "GIF", "TIFF", "PNG" };

				public ImageFilter(String description, String... name) {
					this.description = description + "(";
					ext = new String[name.length];
					for (int i = 0; i < name.length; i++) {
						ext[i] = name[i];
						this.description += ("*" + ext[i] + (i == name.length - 1 ? ")" : ";"));
					}
				}

				public ImageFilter() {
				}

				public void setChooser(JFileChooser chooser) {
					for (int i = 0; i < descrip.length; i++) {
						ImageFilter filter = new ImageFilter(descrip[i], ext2[i]);
						chooser.setFileFilter(filter);
					}
				}

				//Override the accept(File) and getDescription() method
				@Override
				public boolean accept(File f) {
					String name = f.getName();
					if (f.isDirectory())
						return true;
					for (String s : ext) {
						if (name.endsWith(s))
							return true;
					}
					return false;
				}

				@Override
				public String getDescription() {
					return this.description;
				}

				public String getEnds() {
					return this.ext[0];
				}

			}

			// Save buffered image to file of path from paint panel
			private void saveImage(String path, String ext) {
				BufferedImage operateImg = panel.getCurrentImage();
				if (operateImg == null) {
					System.out.println("OperateImg null");
					return;
				}
				BufferedImage imgSave = new BufferedImage(operateImg.getWidth(), operateImg.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics g = imgSave.getGraphics();
				try {
					g.drawImage(operateImg, 0, 0, operateImg.getWidth(), operateImg.getHeight(), null);
					ImageIO.write(imgSave, ext, new File(path));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}
				g.dispose();
				System.out.println("Save Done");
			}

			// Integrated function about file dialog of save to choose path and
			// save
			private void fileDialogOfSave() {
				initChooser();
				chooser.showSaveDialog(frame);
				File f = chooser.getSelectedFile();
				if (f == null)
					return;
				String fileName = f.getName();
				String imgPath = f.getPath();
				if (fileName == null)
					return;
				int index = fileName.lastIndexOf('.');
				String extName = ((ImageFilter) chooser.getFileFilter()).getEnds();
				if (index < 0) {
					imgPath += extName;
					saveImage(imgPath, extName.substring(1, extName.length()));
				} else {
					String extBeGive = fileName.substring(index + 1, fileName.length());
					if (!isRightExt("." + extBeGive)) {
						imgPath += extName;
						saveImage(imgPath, extName.substring(1, extName.length()));
					} else {
						saveImage(imgPath, extBeGive);
					}
				}
				System.out.println("Save : " + imgPath);
			}

			// Integrated function to open file with file dialog
			private void fileDialogOfOpen() {
				initChooser();
				chooser.showOpenDialog(frame);
				File f = chooser.getSelectedFile();
				if (f == null)
					return;
				String imgPath = f.getPath();
				if (imgPath == null)
					return;
				BufferedImage img = null;
				try {
					img = ImageIO.read(new FileInputStream(imgPath));
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				} catch (IOException e2) {
					JOptionPane.showMessageDialog(null, e2.getMessage());
				}
				if (img == null) {
					JOptionPane.showMessageDialog(null, imgPath, "Open a error file!", JOptionPane.ERROR_MESSAGE);
				} else {
					panel.loadImage(img);
					openFilePath = imgPath;
					controlPaint.setSize(panel.getWidth(), panel.getHeight());
					slider.setValue(3);
					System.out.println("Open : " + openFilePath);
				}
			}

			// Action handler of file menu
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "New": {
					panel.initPanel();
					openFilePath = null;
				}
					break;
				case "Open": {
					fileDialogOfOpen();
				}
					break;
				case "Save": {
					if (openFilePath == null) {
						fileDialogOfSave();
					} else {
						int index = openFilePath.lastIndexOf('.');
						String extName = openFilePath.substring(index + 1, openFilePath.length());
						saveImage(openFilePath, extName);
					}
				}
					break;
				case "Save As": {
					fileDialogOfSave();
				}
					break;
				case "Properties": {
					String detail = "Current image\nWidth:" + String.valueOf(panel.getWidth()) + " Height:"
							+ String.valueOf(panel.getHeight());
					JOptionPane.showMessageDialog(frame, detail, "Properity", JOptionPane.PLAIN_MESSAGE);
				}
					break;
				case "About": {
					JOptionPane.showMessageDialog(frame, "px2 design\nVerision 7.0\nxiaoxiaodx@outlook.com",
							"About Paint", JOptionPane.PLAIN_MESSAGE);
				}
					break;
				case "Exit": {
					System.exit(1);
				}
					break;
				case "Show/Hide": {
					if (toolsCollectPane.getHeight() == 0) {
						toolsCollectPane.setPreferredSize(new Dimension(160, 80));
					} else {
						toolsCollectPane.setPreferredSize(new Dimension(0, 0));
					}
					toolsCollectPane.updateUI();
				}
					break;
				}
			}
		}
	}

	/*
	 * Custom class font menu.
	 * Get font from system and initialize the FontMenu
	 */
	class FontMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		private String[] fontName = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		private FontMenuHandler handler = new FontMenuHandler();
		// Get the system font from this function

		public FontMenu(String text) {
			super(text);

			// Add menuItem
			JMenu fontItem = null;
			for (int i = 0, index = 0; i < fontName.length; i++) {
				if (i % 20 == 0) {
					index = i / 20;
					fontItem = new JMenu("Font" + String.valueOf(index));
					this.add(fontItem);
				}
				JMenuItem menuitem = new JMenuItem(fontName[i]);
				menuitem.addActionListener(handler);
				fontItem.add(menuitem);
			}
		}

		/*
		 * Font menu handler to set font
		 */
		private class FontMenuHandler implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand());
				panel.setFont(e.getActionCommand());
			}

		}
	}

	/*
	 * Initialize the functionItem both of the MenuItem and fast short cut of menu
	 */
	private void initFuncItem(JMenu menu) {
		// All function name to be add to menu and function area
		String[] sizeName = new String[] { "1", "2", "5", "10", "15" };
		String[] brushesName = new String[] { "Spray", "Type2", "Type3", "Type4", "Type5" };
		String[] shapesName = new String[] { "Line", "Rect", "Arrow", "FourStar", "Diamond", "FiveStar", "SixStar",
				"Pentagon", "SixSlide", "Triangle" };
		String[] colorName = new String[] { "BLACK", "RED", "ORANGE", "GREEN", "PURPLE", "BLUE", "DARK_GRAY", "BROWN",
				"GOLD", "LIME_GREEN", "MAGENTA", "TURQUOISE", "GRAY", "DARK_RED", "YELLOW", "CYAN", "PINK", "SKYBLUE",
				"WHITE", "INDIGO", "LIGHT_YELLOW", "LAVENDER", "MISTYROSE"};
		String[] imgToolsName = new String[] { "Crop", "Resize", "Rotate" };
		String[] penToolsName = new String[] { "Pen", "Eraser", "Picker", "Paint", "Zoom", "Text" };

		String[][] itemsOf = new String[][] { penToolsName, imgToolsName, shapesName, sizeName, brushesName,
				colorName };
		String[] header = new String[] { "PenTools", "Image", "Shapes", "Size", "Brushes", "Color" };
		CommandSet[] cmd = new CommandSet[] { CommandSet.PEN_TOOLS, CommandSet.IMAGE, CommandSet.SHAPES,
				CommandSet.SIZES, CommandSet.BRUSHES, CommandSet.COLOR };
		AbstractButton[][] allbtn = new AbstractButton[cmd.length][];
		ButtonGroup drawToolsGroup = new ButtonGroup();// tools are in one group
														// so that just one
														// function on in a time
		ButtonGroup valueToolsGroup = new ButtonGroup();
		ButtonGroup btnGroup;
		for (int i = 0; i < itemsOf.length; i++) {
			JMenu menuItem = new JMenu(header[i]);
			menu.add(menuItem);

			if (cmd[i] == CommandSet.SIZES || cmd[i] == CommandSet.COLOR) {
				btnGroup = valueToolsGroup;
			} else {
				btnGroup = drawToolsGroup;
			}
			allbtn[i] = new AbstractButton[itemsOf[i].length];
			ActionListener handler = getHandler(cmd[i]);

			for (int j = 0; j < itemsOf[i].length; j++) {
				JMenuItem itemItem = new JMenuItem(itemsOf[i][j]);
				itemItem.setActionCommand(itemsOf[i][j]);
				itemItem.addActionListener(handler);
				menuItem.add(itemItem);

				allbtn[i][j] = getButtonInstance(cmd[i], itemsOf[i][j]);
				allbtn[i][j].setToolTipText(itemsOf[i][j]);
				allbtn[i][j].setActionCommand(itemsOf[i][j]);
				allbtn[i][j].addActionListener(handler);
				btnGroup.add(allbtn[i][j]);
			}
		}
		// Get the reference when initialize complete.
		this.penTools = allbtn[0];
		this.imgTools = allbtn[1];
		this.shapesTools = allbtn[2];
		this.sizeTools = allbtn[3];
		this.brushesTools = allbtn[4];
		this.colorTools = allbtn[5];

	}

	/*
	 * Return the handler by command, it's used for initFuncItem(..)
	 */
	private ActionListener getHandler(CommandSet cmd) {
		switch (cmd) {
		case COLOR: {
			return new SetColorHandler();
		}
		case SHAPES: {
			return new SetShapesHandler();
		}
		case IMAGE: {
			return new SetImageHandler();
		}
		case BRUSHES: {
			return new SetBrushesHandler();
		}
		case PEN_TOOLS: {
			return new SetToolsHandler();
		}
		case SIZES: {
			return new SetPenSizeHandler();
		}
		default: {
			return null;
		}
		}
	}

	/*
	 * Return the AbstractButton instance by command, it's used for
	 * initFuncItem(..)
	 */
	private AbstractButton getButtonInstance(CommandSet cmd, String text) {
		switch (cmd) {
		case SHAPES: {
			return new ShapeToggleButton(text);
		}
		case COLOR: {
			return new ColorButton(text);
		}
		case IMAGE: {
			return new CustomButton(text);
		}
		default: {
			return new CustomToggleButton(text);
		}
		}
	}

	/*
	 * CallBack manager, implements CallBack for PaintPanel to set this bottom
	 * area detail There those detail will be set: Mouse Position, Edit Dash
	 * Rectangle, Color Header
	 */
	private class CallBackHandlerOfState implements CallBack {

		//Set header color button's color
		@Override
		public void setColorHeader(Color color) {
			selectedColorHeader.setBackground(color);
			penTools[0].setSelected(true);
		}

		//Set the position "position"(JLabel, show mouse position).
		@Override
		public void setPosition(int x, int y) {
			position.setText("P : " + x + ", " + y + "px");
		}

		//Set the edit area data (show)
		@Override
		public void setDashRect(int x, int y) {
			editArea.setText("Edit : " + x + "x" + y + "px");
		}

		//Set the view pane size when open or resize
		@Override
		public void setViewPaneSize(int width, int height) {
			controlPaint.setSize(width, height);
		}

		//Set the DPI value of image that show in JLable control
		@Override
		public void setResolution(int width, int height) {
			resoluTips.setText("DPI : " + width + "x" + height + "px");
		}

		//Let undo button enable
		@Override
		public void enabledUndo(boolean enable) {
			if (undoBtn.isEnabled() == enable)
				return;
			undoBtn.setEnabled(enable);
		}

		//Let redo button enable
		@Override
		public void enabledRedo(boolean enable) {
			if (redoBtn.isEnabled() == enable)
				return;
			redoBtn.setEnabled(enable);
		}
	}

	/*
	 * WindowStateHandler, Paint the dash area when window state change.
	 */
	private class WindowStateHandler implements ComponentListener {

		@Override
		public void componentHidden(ComponentEvent e) {
			if (panel.isDashMode()) {
				panel.paintGBuffer();
			}
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			if (panel.isDashMode()) {
				panel.paintGBuffer();
			}
		}

		@Override
		public void componentResized(ComponentEvent e) {
			if (panel.isDashMode()) {
				panel.paintGBuffer();
			}
		}

		@Override
		public void componentShown(ComponentEvent e) {
			if (panel.isDashMode()) {
				panel.paintGBuffer();
			}
		}

	}

	/*
	 * Slider state handler, change the zoom sizes of paint panel when slider
	 * value change.
	 */
	private class SliderStateHandler implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			double a = slider.getValue();
			a = a > 2 ? (a - 2) * 100 : (a == 0 ? 12.5 : a * 25);
			zoomLabel.setText(String.valueOf((int) a) + "%");
			panel.zoomPanel(a);
		}

	}

	/*
	 * Shapes button handler, change the paint shapes of paint panel
	 */
	private class SetShapesHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			AbstractButton b = (AbstractButton) e.getSource();
			String cmd = e.getActionCommand();
			if (cmd == "Fill") {
				panel.setDrawFill(b.isSelected());
			} else if (b.isSelected()) {
				if (cmd == "" || cmd == null)
					return;
				System.out.println(cmd);
				panel.setPolygon(cmd);
			}
		}
	}

	/*
	 * Tools button handler, set the current tools function of paint panel
	 */
	private class SetToolsHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			switch (e.getActionCommand()) {
			case "Pen": {
				panel.setPenTools("Pen");
			}
				break;
			case "Eraser": {
				panel.setPenTools("Eraser");
			}
				break;
			case "Picker": {
				panel.onColorPicker();
			}
				break;
			case "Zoom": {
				int a = slider.getValue();
				slider.setValue(++a);
			}
				break;
			case "Text": {
				panel.setPenTools("Text");
			}
				break;
			case "Paint": {
				panel.setPaintUse(true);
			}
				break;
			}
		}
	}

	/*
	 * Color button handler, set the color of paint panel
	 */
	private class SetColorHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			AbstractButton b = (AbstractButton) (e.getSource());
			Color c = b.getBackground();
			panel.setColor(c);
			if (selectedColorHeader != null) {
				if (b.isSelected())
					selectedColorHeader = b;
				selectedColorHeader.setBackground(c);
			}
		}
	}

	/*
	 * CustomColor button handler, get and set a custom value color.
	 */
	private class CustomColorHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Color c = JColorChooser.showDialog(null, "Custom Color", selectedColorHeader.getBackground());
			if (c == null)
				return;
			panel.setColor(c);
			selectedColorHeader.setBackground(c);
		}

	}

	/*
	 * Image button handler, turn on image edit function.
	 */
	private class SetImageHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Actioon " + e.getActionCommand());
			switch (e.getActionCommand()) {
			case "Rotate": {
				panel.rotateImage();
				controlPaint.setSize(panel.getWidth(), panel.getHeight());
			}
				break;
			case "Image" : {
				panel.setPolygon("Image");
			}break;
			case "Crop": {
				if(imgBtn.isSelected()){
					panel.cropPanel();
					imgBtn.setSelected(false);
				}
			}
				break;
			case "Resize": {
				String s = JOptionPane.showInputDialog(frame, "Input width", panel.getWidth());
				String s2 = JOptionPane.showInputDialog(frame, "Input height", panel.getHeight());
				if (s == null || s2 == null) {
					System.out.println("Current input is null");
				} else {
					if (s.matches("\\d+") && s2.matches("\\d+")) {
						panel.resizeImage(Integer.parseInt(s), Integer.parseInt(s2));
					} else {
						JOptionPane.showMessageDialog(frame, "Input error");
					}
				}
			}
				break;
			}
		}
	}

	/*
	 * Zoom button handler, use slider to set current zoom.
	 */
	private class ZoomHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			String cmd = e.getActionCommand();
			double a = panel.getZoomSizePercent();
			if (cmd == "ZOOM_OUT") {
				if (a < 25)
					return;
				a = (a < 101 ? a / 2 : a - 100);
			} else if (cmd == "ZOOM_IN") {
				if (a > 799)
					return;
				a = (a < 101 ? a * 2 : a + 100);
			}
			slider.setValue(a > 25 ? (int) (a / 100) + 2 : (int) (a / 25));
		}

	}

	/*
	 * Size button handler, set the current stroke of paint panel.
	 */
	private class SetPenSizeHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(e.getActionCommand());
			int i = Integer.parseInt(e.getActionCommand());
			System.out.println(i);
			panel.setStrokeSizes(i);
		}
	}

	/*
	 * Brushes handler, set current select brushes tools. But now there is just
	 * reach spray function.Other is coming soon
	 */
	private class SetBrushesHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(e.getActionCommand());
			panel.setPenTools("Spray");
		}
	}

	/*
	 * Redo and undo button handler, to redo or undo change by paint panel
	 */
	private class RedoOrUndoHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd == "redo") {
				panel.RedoBefore();
			} else {
				panel.UndoCurrent();
			}

		}
	}
}
/*
 * Mark : some slider setting may be use again
 */
// slider.setPreferredSize(new Dimension(150,25));
// slider.setPaintLabels(true);
// slider.setPaintTrack(false);
// slider.setPaintTicks(true);

/*
 * Mark : use fileDialog. Don't use because fileDialog can't use filter on m-s
 * windows
 */
// java.awt.FileDialog f = new java.awt.FileDialog(frame, "Save Picture",
// java.awt.FileDialog.SAVE);
// f.setDirectory(System.getProperty("user.desktop"));
// ImageFilter filter = new ImageFilter("jpg");
// f.setFilenameFilter(filter);
// f.setVisible(true);

// java.awt.FileDialog f = new java.awt.FileDialog(frame, "Choose Picture",
// java.awt.FileDialog.LOAD);
// f.setVisible(true);