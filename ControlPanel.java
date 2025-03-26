
import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.sound.sampled.Line;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.LabelUI;

public class ControlPanel extends JPanel implements ItemListener{

	protected JCheckBox mirrorToggle;
	protected JCheckBox snapCenter;
	protected JCheckBox lockX;
	protected JCheckBox lockY;
	// protected Vector<JRadioButton> hp_btns = new Vector<JRadioButton>();
	private JTabbedPane hp_Plane;
	// protected JRadioButton engineButton;
	// protected JRadioButton revengineButton;
	// protected JRadioButton steerengineButton;
	// protected JRadioButton gunButton;
	// protected JRadioButton turretButton;
	// protected JRadioButton bayButton;
	protected boolean mirror_mode = false;
	protected boolean center_mode = false;
	protected boolean lock_x = false;
	protected boolean lock_y = false;
	protected Hardpoint gun_data = new Hardpoint();
	protected Hardpoint turret_data = new Hardpoint();
	protected Hardpoint engine_data = new Hardpoint();
	protected Hardpoint rev_engine_data = new Hardpoint();
	protected Hardpoint ste_engine_data = new Hardpoint();
	protected Hardpoint bay_data = new Hardpoint();

	public ControlPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setAlignmentX(-1);
		//TODO:  Collision mask toggle, Swizzle drop down (+ swizzle mask?)
		JButton loadNewImage = new JButton("load png");
		loadNewImage.setBackground(Shipyard.backgroundColor);
		loadNewImage.setForeground(Shipyard.textColor);
		loadNewImage.setHorizontalAlignment(SwingConstants.LEFT);
		// selectImage()

		mirrorToggle = new JCheckBox("Mirror");
		mirrorToggle.setBackground(Shipyard.backgroundColor);
		mirrorToggle.setForeground(Shipyard.textColor);
		mirrorToggle.setOpaque(false);
		mirrorToggle.setMnemonic(KeyEvent.VK_M);
		mirrorToggle.setDisplayedMnemonicIndex(0);
		mirrorToggle.setHorizontalAlignment(SwingConstants.LEFT);
		// mirrorToggle.setMargin(new Insets(5, 5, 5, 5));
		// mirrorToggle.setMinimumSize(new Dimension(120, 50));
		// mirrorToggle.setMaximumSize(new Dimension(200, 50));
		// mirrorToggle.setPreferredSize(new Dimension(200, 50));
		// mirrorToggle.setBorder(new EmptyBorder(10, 20, 10, 10));;
		snapCenter = new JCheckBox("Snap to Center");
		snapCenter.setBackground(Shipyard.backgroundColor);
		snapCenter.setForeground(Shipyard.textColor);
		snapCenter.setOpaque(false);
		snapCenter.setMnemonic(KeyEvent.VK_C);
		snapCenter.setDisplayedMnemonicIndex(8);
		// snapCenter.setMargin(new Insets(5, 5, 5, 5));
		// snapCenter.setBorder(new EmptyBorder(10, 20, 10, 10));;
		lockX = new JCheckBox("Lock X");
		lockX.setBackground(Shipyard.backgroundColor);
		lockX.setForeground(Shipyard.textColor);
		lockX.setMnemonic(KeyEvent.VK_X);
		lockX.setDisplayedMnemonicIndex(5);
		lockY = new JCheckBox("Lock Y");
		lockY.setBackground(Shipyard.backgroundColor);
		lockY.setForeground(Shipyard.textColor);
		lockY.setMnemonic(KeyEvent.VK_Y);
		lockY.setDisplayedMnemonicIndex(5);

		// ButtonGroup hp_Group = new ButtonGroup();
		// HardpointBtnListener hp_Listener = new HardpointBtnListener();
		// for (String hp : Hardpoint.hp_types) {
		// 	JRadioButton tmp = new JRadioButton(hp);
		// 	// tmp.setOpaque(true);
		// 	tmp.setBackground(Shipyard.backgroundColor);
		// 	tmp.setForeground(Shipyard.textColor);
		// 	tmp.addItemListener(hp_Listener);
		// 	hp_btns.add(tmp);
		// 	hp_Group.add(tmp);
		// }
		
		int i = 0;
		hp_Plane = new JTabbedPane(JTabbedPane.TOP);
		hp_Plane.setBackground(Shipyard.backgroundColor);
		hp_Plane.setForeground(Shipyard.textColor);
		for (String hp : Hardpoint.hp_types) {
			JPanel panel = makeHardpointPanel(hp);
			JScrollPane scrollPane = new JScrollPane(panel);
			// tmp.setOpaque(true);
			
			// panel.addItemListener(hp_Listener);
			hp_Plane.addTab(hp, scrollPane);
			// JLabel tabLabel = new JLabel(hp);
			// tabLabel.setUI(new VerticalLabelUI(false));
			// hp_Plane.setTabComponentAt(i, panel);
			i++;
		}

		this.add(loadNewImage);
		loadNewImage.addActionListener(new loadNewImageListener());
		this.add(mirrorToggle);
		this.add(snapCenter);
		this.add(lockX);
		this.add(lockY);
		this.add(hp_Plane);
		// for (JRadioButton btn : hp_btns) {
		// 	this.add(btn);
		// }
		mirrorToggle.addItemListener(this);
		snapCenter.addItemListener(this);
		lockX.addItemListener(this);
		lockY.addItemListener(this);
	}

	private String new_img_path = null;
	public String consumeNewImgPath() {
		String path = new_img_path;
		new_img_path = null;
		return path;
	}
	class loadNewImageListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// System.out.println("LoadNewImg ActionCmd:" + e.getActionCommand());
			new_img_path = Shipyard.selectImage();
		}
		
	}

	public JTabbedPane getHardpointPanel() {
		return (hp_Plane);
	}

	public boolean isMirrorToggle() {
		return mirror_mode;
	}

	public boolean isSnapCenter() {
		return center_mode;
	}
	public boolean isLockX() {
		return lock_x;
	}
	public boolean isLockY() {
		return lock_y;
	}
	public Hardpoint getGunData() {
		return gun_data;
	}
	public Hardpoint getTurretData() {
		return turret_data;
	}
	public Hardpoint getEngineData() {
		return engine_data;
	}
	public Hardpoint getRevEngineData() {
		return rev_engine_data;
	}
	public Hardpoint getSteerEngineData() {
		return ste_engine_data;
	}
	public Hardpoint getBayData() {
		return bay_data;
	}

	protected void setGunAngle(int angle) {
		gun_data.angle = (double)angle;
	}
	protected void setGunAngle(double angle) {
		gun_data.angle = angle;
	}
	protected void setGunParallel(boolean para) {
		gun_data.parallel = para;
	}
	protected void setGunOver(boolean over) {
		gun_data.over = over;
	}

	protected void setTurretAngle(int angle) {
		turret_data.angle = (double)angle;
	}
	protected void setTurretAngle(double angle) {
		turret_data.angle = angle;
	}
	protected void setTurretUnder(boolean under) {
		turret_data.under = under;
	}
	protected void setTurretHaveArc(boolean arc) {
		turret_data.have_arc = arc;
	}
	protected void setTurretArc(int min, int max) {
		turret_data.arc_min = (double)min;
		turret_data.arc_max = (double)max;
	}
	protected void setTurretArc(double min, double max) {
		turret_data.arc_min = min;
		turret_data.arc_max = max;
	}
	protected void setTurretTurnMult(double mult) {
		if (mult != 1)
			turret_data.have_turn_mult = true;
		else if (mult == 1)
			turret_data.have_turn_mult = false;
		turret_data.turn_mult = mult;
	}

	protected void setBayOver(boolean over) {
		bay_data.over = over;
		if (over) {
			bay_data.under = false;
		}
	}
	protected void setBayUnder(boolean under) {
		if (under){
			bay_data.over = false;
		}
		bay_data.under = under;
	}

	protected ArcVisualizationCanvas arcVisCanvas = new ArcVisualizationCanvas();

	protected class ArcVisualizationCanvas extends JPanel {
		public static final double CIRCLE_DIA = 50.;
		public static final double IDL_LEN = 50.;
		public static final double LIM_LEN = 42.;

		public ArcVisualizationCanvas() {
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Point center = new Point((int)(getWidth() * .5), (int)(getHeight() * .5));
			Point idle_at = MathUtils.polarToCartesian(IDL_LEN, turret_data.angle - 90);
			Point min = MathUtils.polarToCartesian(LIM_LEN, turret_data.angle + turret_data.arc_min - 90);
			Point max = MathUtils.polarToCartesian(LIM_LEN, turret_data.angle + turret_data.arc_max - 90);

			// System.out.println("len:"+IDL_LEN+" angle:"+turret_data.angle+"P="+idle_at+"Orign:"+center);
			idle_at.x += center.x;
			idle_at.y += center.y;
			min.x += center.x;
			min.y += center.y;
			max.x += center.x;
			max.y += center.y;
			// System.out.println("len:"+IDL_LEN+" angle:"+turret_data.angle+"P="+idle_at+"Orign:"+center);
			drawLine(g, center, min, Color.GREEN, 1);
			drawLine(g, center, max, Color.RED, 1);
			drawLine(g, center, idle_at, Color.CYAN, 2);
			drawCircle(g, getWidth() * .5, getHeight() * .5, Color.CYAN);
			Toolkit.getDefaultToolkit().sync();
		}

		protected void drawLine(Graphics g, Point origin, Point dst, Color col, float line_width) {
			Graphics2D g2d = (Graphics2D) g;
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHints(rh);

			Line2D line = new Line2D.Double(origin.x, origin.y, dst.x, dst.y);
			g2d.setStroke(new BasicStroke(line_width));
			g2d.setColor(col);
			g2d.draw(line);
		}

		//Draw circle with center on x, y
		protected void drawCircle(Graphics g, double x, double y, Color col) {
			
			Graphics2D g2d = (Graphics2D) g;
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHints(rh);

			Ellipse2D e = new Ellipse2D.Double(x - (CIRCLE_DIA/2), y - (CIRCLE_DIA/2), CIRCLE_DIA, CIRCLE_DIA);
			g2d.setStroke(new BasicStroke(1));
			g2d.setColor(col);

			// for (double deg = 0; deg < 360; deg += 5) {
			// 	AffineTransform at = AffineTransform.getTranslateInstance(w/2, h/2);
			// 	at.rotate(Math.toRadians(deg));
			// 	g2d.draw(at.createTransformedShape(e));
			// }
			g2d.draw(e);
		}

		public void doRedraw() {
			repaint();
		}
	}
	
	

	static public Dimension smallTextField = new Dimension(100, 18);
	static public Dimension labelAndFieldPanel = new Dimension(150, 18);
	static public Dimension toggle = new Dimension(150, 18);
	static public Dimension slider = new Dimension(200, 45);
	static public Dimension comboBoxDim = new Dimension(200, 20);

	static public int input_field_index;

	protected JPanel makeLabeledField(String label, String tooltip) {
		return makeLabeledField(label, tooltip, 0);
	}
	protected JPanel makeLabeledField(String label, String tooltip, double def_num) {
		JPanel panel = new JPanel();
		panel.setMaximumSize(labelAndFieldPanel);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setLayout(new GridLayout(1, 2));
		panel.setBackground(Shipyard.backgroundColor);
		panel.setForeground(Shipyard.textColor);

		JLabel angleLabel = new JLabel(label);
		angleLabel.setMaximumSize(smallTextField);
		angleLabel.setBackground(Shipyard.backgroundColor);
		angleLabel.setForeground(Shipyard.textColor);
		if (tooltip != null) {
			angleLabel.setToolTipText(tooltip);
		}
		
		JFormattedTextField angleField = new JFormattedTextField(NumberFormat.getNumberInstance());
		angleField.setValue(def_num);
		angleField.setMaximumSize(smallTextField);
		angleField.setAlignmentX(Component.LEFT_ALIGNMENT);
		angleField.setBackground(Shipyard.backgroundColor);
		angleField.setForeground(Shipyard.textColor);
		if (tooltip != null) {
			angleField.setToolTipText(tooltip);
		}

		panel.add(angleLabel);
		panel.add(angleField);
		input_field_index = 1;
		return panel;
	}

	JFormattedTextField gunAngleField;
	JFormattedTextField turretAngleField;
	JFormattedTextField engineAngleField;
	JFormattedTextField revengineAngleField;
	JFormattedTextField steeringengineAngleField;
	JFormattedTextField bayAngleField;

	public void setHardpointAngle(String hp_str, double angle) {
		switch (hp_str) {
			case "gun":
				gunAngleField.setValue(angle);
				break ;
			case "turret":
				turretAngleField.setValue(angle);
				break ;
			case "engine":
				engineAngleField.setValue(angle);
				break ;
			case "reverse engine":
				revengineAngleField.setValue(angle);
				break ;
			case "steering engine":
				steeringengineAngleField.setValue(angle);
				break ;
			case "bay":
				bayAngleField.setValue(angle);
				break ;
		}
	}

	protected JPanel makeHardpointPanel(String type) {
		JPanel panel = new JPanel(false);
		Hardpoint.HardpointType hp_type;
		panel.setBackground(Shipyard.backgroundColor);
		panel.setForeground(Shipyard.textColor);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		if (type.equals("gun")) {
			hp_type = Hardpoint.HardpointType.GUN;
			//Angle slider/spinner
			//parallel toggle
			//over toggle
			JSlider angleSlider = new JSlider(JSlider.HORIZONTAL, -360, 360, 0);
			angleSlider.setMaximumSize(slider);
			angleSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			angleSlider.setMajorTickSpacing(90);
			angleSlider.setMinorTickSpacing(45);
			angleSlider.setSnapToTicks(false);
			angleSlider.setPaintTicks(true);
			angleSlider.setPaintLabels(true);
			angleSlider.setBackground(Shipyard.backgroundColor);
			angleSlider.setForeground(Shipyard.textColor);
			
			JPanel anglePanel = makeLabeledField("angle", "Angle which the gun fires. Default is 0 (forward).");

			//NOTE: These lable text must be exactly this as the single listener class use it to figure out the source.
			JCheckBox parallelCheck = new JCheckBox("parallel");
			parallelCheck.setMaximumSize(toggle);
			parallelCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
			parallelCheck.setAlignmentX(-1);
			parallelCheck.setBackground(Shipyard.backgroundColor);
			parallelCheck.setForeground(Shipyard.textColor);
			parallelCheck.setToolTipText("Make the gun fire parallel to other guns of the same angle instead of converging");
			
			JCheckBox overCheck = new JCheckBox("over");
			overCheck.setMaximumSize(toggle);
			overCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
			overCheck.setBackground(Shipyard.backgroundColor);
			overCheck.setForeground(Shipyard.textColor);
			overCheck.setToolTipText("Draw the hardpoint sprite over the ship instead of under.");

			JTextField angleField = (JTextField)anglePanel.getComponent(input_field_index);
			gunAngleField = (JFormattedTextField)angleField;
			angleSlider.addChangeListener(new AngleSliderListener(angleField, hp_type));
			angleField.addPropertyChangeListener(new NumFieldListener(angleSlider, hp_type));
			parallelCheck.addItemListener(new HardpointCheckBoxListener(hp_type));
			overCheck.addItemListener(new HardpointCheckBoxListener(hp_type));
			panel.add(angleSlider);
			// panel.add(angleField);
			panel.add(anglePanel);
			panel.add(parallelCheck);
			panel.add(overCheck);
		}
		else if (type.equals("turret")) {
			hp_type = Hardpoint.HardpointType.TURRET;
			//angle slider/spinner
			//min arc/max arc
			//under toggle
			//turret turn mult num-entry
			arcVisCanvas.setMaximumSize(new Dimension(200, 200));
			arcVisCanvas.setAlignmentX(Component.LEFT_ALIGNMENT);
			arcVisCanvas.setBackground(Shipyard.backgroundColor);
			arcVisCanvas.setForeground(Shipyard.textColor);
			// arcVisCanvas.getLocationOnScreen();

			JSlider angleSlider = new JSlider(JSlider.HORIZONTAL, -360, 360, 0);
			angleSlider.setMaximumSize(slider);
			angleSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			angleSlider.setMajorTickSpacing(90);
			angleSlider.setMinorTickSpacing(45);
			angleSlider.setSnapToTicks(false);
			angleSlider.setPaintTicks(true);
			angleSlider.setPaintLabels(true);
			angleSlider.setBackground(Shipyard.backgroundColor);
			angleSlider.setForeground(Shipyard.textColor);
			
			JPanel anglePanel = makeLabeledField("angle", "Angle which the turret points at when idle.");

			JSlider minArcSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, -180);
			minArcSlider.setMaximumSize(slider);
			minArcSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			minArcSlider.setMajorTickSpacing(90);
			minArcSlider.setMinorTickSpacing(45);
			minArcSlider.setSnapToTicks(false);
			minArcSlider.setPaintTicks(true);
			minArcSlider.setPaintLabels(true);
			minArcSlider.setBackground(Shipyard.backgroundColor);
			minArcSlider.setForeground(Shipyard.textColor);
			
			JPanel minArcPanel = makeLabeledField("min arc", "Min/Left-hand/Counter-Clockwise turret aiming angle limit relative to base angle.", -180);

			JSlider maxArcSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 180);
			maxArcSlider.setMaximumSize(slider);
			maxArcSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			maxArcSlider.setMajorTickSpacing(90);
			maxArcSlider.setMinorTickSpacing(45);
			maxArcSlider.setSnapToTicks(false);
			maxArcSlider.setPaintTicks(true);
			maxArcSlider.setPaintLabels(true);
			maxArcSlider.setBackground(Shipyard.backgroundColor);
			maxArcSlider.setForeground(Shipyard.textColor);
			
			JPanel maxArcPanel = makeLabeledField("max arc", "Max/Right-hand/Clockwise turret aiming angle limit relative to base angle.", 180);
			
			JPanel turnmultPanel = makeLabeledField("turn mult", "Turn multiplier for the turret.", 1);

			JCheckBox underCheck = new JCheckBox("under");
			underCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
			underCheck.setBackground(Shipyard.backgroundColor);
			underCheck.setForeground(Shipyard.textColor);
			underCheck.setToolTipText("Draw the hardpoint sprite under the ship instead of over.");

			JTextField angleField = (JTextField)anglePanel.getComponent(input_field_index);
			turretAngleField = (JFormattedTextField)angleField;
			angleSlider.addChangeListener(new AngleSliderListener(angleField, hp_type));
			angleField.addPropertyChangeListener(new NumFieldListener(angleSlider, hp_type));
			
			JTextField minArcField = (JTextField)minArcPanel.getComponent(input_field_index);
			JTextField maxArcField = (JTextField)maxArcPanel.getComponent(input_field_index);
			minArcSlider.addChangeListener(new AngleSliderListener(minArcField, hp_type, "min arc", maxArcField, maxArcSlider));
			minArcField.addPropertyChangeListener(new NumFieldListener(minArcSlider, hp_type, "min arc", maxArcSlider));

			maxArcSlider.addChangeListener(new AngleSliderListener(maxArcField, hp_type, "max arc", minArcField, minArcSlider));
			maxArcField.addPropertyChangeListener(new NumFieldListener(maxArcSlider, hp_type, "max arc", minArcSlider));

			JTextField turnmultField = (JTextField)turnmultPanel.getComponent(input_field_index);
			turnmultField.addPropertyChangeListener(new NumFieldListener(null, hp_type, "turn mult"));
			underCheck.addItemListener(new HardpointCheckBoxListener(hp_type));

			panel.add(arcVisCanvas);
			arcVisCanvas.doRedraw();
			panel.add(angleSlider);
			panel.add(anglePanel);
			panel.add(minArcSlider);
			panel.add(minArcPanel);
			panel.add(maxArcSlider);
			panel.add(maxArcPanel);
			panel.add(turnmultPanel);
			panel.add(underCheck);
		}
		else if (type.equals("engine") || type.equals("reverse engine") || type.equals("steering engine")) {
			hp_type = Hardpoint.HardpointType.ENGINE;
			if (type.equals("reverse engine")) {
				hp_type = Hardpoint.HardpointType.REVERSE_ENGINE;
			}
			else if (type.equals("steering engine")) {
				hp_type = Hardpoint.HardpointType.STEERING_ENGINE;
			}
			ButtonGroup heightGroup = null;
			JRadioButton leftBtn = null;
			JRadioButton rightBtn = null;
			JRadioButton noneBtn = null;
			JRadioButton autoBtn = null;

			JCheckBox autoAngleBtn = null;
			//angle slider/spinner
			//zoom entry
			//gimble entry
			//over toggle
			//left/right toggle (steering only.)

			JSlider angleSlider = new JSlider(JSlider.HORIZONTAL, -360, 360, 0);
			angleSlider.setMaximumSize(slider);
			angleSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			angleSlider.setMajorTickSpacing(90);
			angleSlider.setMinorTickSpacing(45);
			angleSlider.setSnapToTicks(false);
			angleSlider.setPaintTicks(true);
			angleSlider.setPaintLabels(true);
			angleSlider.setBackground(Shipyard.backgroundColor);
			angleSlider.setForeground(Shipyard.textColor);
			
			String tooltip = "Direction for thruster flare. ";
			if (hp_type == Hardpoint.HardpointType.REVERSE_ENGINE) {
				tooltip += "Reverse engine default (0) is pointing forward.";
			}
			else {
				tooltip += "Default (0) is pointing backward.";
			}
			JPanel anglePanel = makeLabeledField("angle", tooltip);

			if (hp_type == Hardpoint.HardpointType.STEERING_ENGINE) {
				autoAngleBtn = new JCheckBox("auto angle");
				autoAngleBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
				autoAngleBtn.setBackground(Shipyard.backgroundColor);
				autoAngleBtn.setForeground(Shipyard.textColor);
				autoAngleBtn.setToolTipText("Automatically assign angle based on quadrant of the coordinate.");
				autoAngleBtn.addItemListener(new HardpointCheckBoxListener(hp_type));
			}

			JPanel zoomPanel = makeLabeledField("zoom", "Multiplier for scale of thruster flares. > 1 for bigger, < 1 (decimals) for smaller.", 1);

			JPanel gimblePanel = makeLabeledField("gimble", "Additional angle added when both thrusting (fwd or back) and turning at the same time.");

			JCheckBox overBtn = new JCheckBox("over");
			overBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
			overBtn.setBackground(Shipyard.backgroundColor);
			overBtn.setForeground(Shipyard.textColor);
			overBtn.setToolTipText("Draw the thruster flare sprite over the ship instead of under.");
			overBtn.addItemListener(new HardpointCheckBoxListener(hp_type));

			if (hp_type == Hardpoint.HardpointType.STEERING_ENGINE) {
				heightGroup = new ButtonGroup();
				leftBtn = new JRadioButton("left");
				leftBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
				leftBtn.setBackground(Shipyard.backgroundColor);
				leftBtn.setForeground(Shipyard.textColor);
				leftBtn.setToolTipText("Specify which direction the ship must be turning for the flare to show.");
				rightBtn = new JRadioButton("right");
				rightBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
				rightBtn.setBackground(Shipyard.backgroundColor);
				rightBtn.setForeground(Shipyard.textColor);
				rightBtn.setToolTipText("Specify which direction the ship must be turning for the flare to show.");
				noneBtn = new JRadioButton("none");
				noneBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
				noneBtn.setBackground(Shipyard.backgroundColor);
				noneBtn.setForeground(Shipyard.textColor);
				noneBtn.setToolTipText("Always show when the ship is steering");
				noneBtn.setSelected(true);
				autoBtn = new JRadioButton("auto");
				autoBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
				autoBtn.setBackground(Shipyard.backgroundColor);
				autoBtn.setForeground(Shipyard.textColor);
				autoBtn.setToolTipText("Automatically assign direction based on quadrant of the coordinate.");
				heightGroup.add(leftBtn);
				heightGroup.add(rightBtn);
				heightGroup.add(noneBtn);
				heightGroup.add(autoBtn);
				leftBtn.addItemListener(new HardpointCheckBoxListener(hp_type));
				rightBtn.addItemListener(new HardpointCheckBoxListener(hp_type));
				noneBtn.addItemListener(new HardpointCheckBoxListener(hp_type));
				autoBtn.addItemListener(new HardpointCheckBoxListener(hp_type));
			}

			JTextField angleField = (JTextField)anglePanel.getComponent(input_field_index);
			if (type.equals("engine")) {
				engineAngleField = (JFormattedTextField)angleField;
			}
			else if (type.equals("reverse engine")) {
				revengineAngleField = (JFormattedTextField)angleField;
			}
			else if (type.equals("steering engine")) {
				steeringengineAngleField = (JFormattedTextField)angleField;
			}
			angleSlider.addChangeListener(new AngleSliderListener(angleField, hp_type));
			angleField.addPropertyChangeListener(new NumFieldListener(angleSlider, hp_type));
			
			JTextField zoomField = (JTextField)zoomPanel.getComponent(input_field_index);
			zoomField.addPropertyChangeListener(new NumFieldListener(null, hp_type, "zoom"));

			JTextField gimbleField = (JTextField)gimblePanel.getComponent(input_field_index);
			gimbleField.addPropertyChangeListener(new NumFieldListener(null, hp_type, "gimble"));
			
			panel.add(angleSlider);
			panel.add(anglePanel);
			if (hp_type == Hardpoint.HardpointType.STEERING_ENGINE) {
				panel.add(autoAngleBtn);
			}
			panel.add(zoomPanel);
			panel.add(gimblePanel);
			if (hp_type == Hardpoint.HardpointType.STEERING_ENGINE) {
				panel.add(autoAngleBtn);
				if (heightGroup != null) {
					panel.add(leftBtn);
					panel.add(rightBtn);
					panel.add(noneBtn);
					panel.add(autoBtn);
				}
			}
		}
		else if (type.equals("bay")) {
			hp_type = Hardpoint.HardpointType.BAY;
			//over/under toggle
			//angle slider/spinner
			//left/right/back toggle (Or not and just use angle)
			//launch effect text entry/dropdown
			//baytype entry/dropdown
			String[] bay_items = Arrays.copyOf(Hardpoint.getBayTypes().toArray(), Hardpoint.getBayTypes().toArray().length, String[].class);
			JComboBox<String> bayTypeSelect = new JComboBox<String>(bay_items);
			bayTypeSelect.setMaximumSize(comboBoxDim);
			bayTypeSelect.setEditable(true);
			bayTypeSelect.setAlignmentX(Component.LEFT_ALIGNMENT);
			bayTypeSelect.setBackground(Shipyard.backgroundColor);
			bayTypeSelect.setForeground(Shipyard.textColor);
			bayTypeSelect.addActionListener(new ComboBoxListener(hp_type, "bay type"));

			ButtonGroup heightGroup = new ButtonGroup();
			JRadioButton overBtn = new JRadioButton("over");
			overBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
			overBtn.setBackground(Shipyard.backgroundColor);
			overBtn.setForeground(Shipyard.textColor);
			overBtn.setToolTipText("Display carried ship over the carrier.");
			JRadioButton underBtn = new JRadioButton("under");
			underBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
			underBtn.setBackground(Shipyard.backgroundColor);
			underBtn.setForeground(Shipyard.textColor);
			underBtn.setToolTipText("Display carried ship under the carrier.");
			JRadioButton hideBtn = new JRadioButton("hidden");
			hideBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
			hideBtn.setBackground(Shipyard.backgroundColor);
			hideBtn.setForeground(Shipyard.textColor);
			hideBtn.setSelected(true);
			hideBtn.setToolTipText("Do not show carried ship.");
			heightGroup.add(overBtn);
			heightGroup.add(underBtn);
			heightGroup.add(hideBtn);
			overBtn.addItemListener(new HardpointCheckBoxListener(hp_type));
			underBtn.addItemListener(new HardpointCheckBoxListener(hp_type));
			hideBtn.addItemListener(new HardpointCheckBoxListener(hp_type));

			JSlider angleSlider = new JSlider(JSlider.HORIZONTAL, -360, 360, 0);
			angleSlider.setMaximumSize(slider);
			angleSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			angleSlider.setMajorTickSpacing(90);
			angleSlider.setMinorTickSpacing(45);
			angleSlider.setSnapToTicks(false);
			angleSlider.setPaintTicks(true);
			angleSlider.setPaintLabels(true);
			angleSlider.setBackground(Shipyard.backgroundColor);
			angleSlider.setForeground(Shipyard.textColor);
			
			JPanel anglePanel = makeLabeledField("angle", "Facing for the carried ship.");

			JTextField angleField = (JTextField)anglePanel.getComponent(input_field_index);
			bayAngleField = (JFormattedTextField)angleField;
			angleSlider.addChangeListener(new AngleSliderListener(angleField, hp_type));
			angleField.addPropertyChangeListener(new NumFieldListener(angleSlider, hp_type));

			String[] lauEff_items = Arrays.copyOf(Hardpoint.getLaunchEffects().toArray(), Hardpoint.getLaunchEffects().toArray().length, String[].class);
			JComboBox<String> launchEffSelect = new JComboBox<String>(lauEff_items);
			launchEffSelect.setMaximumSize(comboBoxDim);
			launchEffSelect.setEditable(true);
			launchEffSelect.setEditable(true);
			launchEffSelect.setAlignmentX(Component.LEFT_ALIGNMENT);
			launchEffSelect.setBackground(Shipyard.backgroundColor);
			launchEffSelect.setForeground(Shipyard.textColor);
			launchEffSelect.addActionListener(new ComboBoxListener(hp_type, "launch effect"));

			panel.add(bayTypeSelect);
			panel.add(overBtn);
			panel.add(underBtn);
			panel.add(hideBtn);
			panel.add(angleSlider);
			panel.add(anglePanel);
			panel.add(launchEffSelect);
		}
		return panel;
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		Object source = event.getItemSelectable();

		if (source == mirrorToggle && event.getStateChange() == ItemEvent.SELECTED) {
			mirror_mode = true;
		}
		else if (source == mirrorToggle && event.getStateChange() == ItemEvent.DESELECTED) {
			mirror_mode = false;
		}
		if (source == snapCenter && event.getStateChange() == ItemEvent.SELECTED) {
			center_mode = true;
		}
		else if (source == snapCenter && event.getStateChange() == ItemEvent.DESELECTED) {
			center_mode = false;
		}

		if (source == lockX && event.getStateChange() == ItemEvent.SELECTED) {
			lock_x = true;
			if (lock_y) {
				lock_y = false;
				lockY.setSelected(false);
			}
		}
		else if (source == lockX && event.getStateChange() == ItemEvent.DESELECTED) {
			lock_x = false;
		}
		if (source == lockY && event.getStateChange() == ItemEvent.SELECTED) {
			lock_y = true;
			if (lock_x) {
				lock_x = false;
				lockX.setSelected(false);
			}
		}
		else if (source == lockY && event.getStateChange() == ItemEvent.DESELECTED) {
			lock_y = false;
		}
	}

	class ComboBoxListener implements ActionListener {
		Hardpoint.HardpointType type;
		String property_str;
		public ComboBoxListener(Hardpoint.HardpointType set_type, String prop_str) {
			this.type = set_type;
			this.property_str = prop_str;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox<String> src = (JComboBox<String>)e.getSource();
			String text = (String)src.getSelectedItem();
			System.out.println("ComboListener selected:" + text);
			System.out.println("ComboListener actionCommand:" + e.getActionCommand());
			if (property_str == "bay type") {
				bay_data.bay_type = text;
			}
			else if (property_str == "launch effect") {
				bay_data.launch_effect = text;
			}
		}
		
	}

	class HardpointCheckBoxListener implements ItemListener {
		Hardpoint.HardpointType type;
		public HardpointCheckBoxListener(Hardpoint.HardpointType n_type) {
			type = n_type;
		}
		@Override
		public void itemStateChanged(ItemEvent event) {
			String src_name;
			if (event.getSource() instanceof JCheckBox){
				JCheckBox src = (JCheckBox)event.getSource();
				src_name = src.getText();
			}
			else if (event.getSource() instanceof JRadioButton){
				JRadioButton src = (JRadioButton)event.getSource();
				src_name = src.getText();
			}
			else {
				src_name = "none";
			}

			// System.out.println("srcname="+src_name);
			if (src_name == "parallel" && event.getStateChange() == ItemEvent.SELECTED) {
				if (type == Hardpoint.HardpointType.GUN) {
					setGunParallel(true);
				}
			}
			else if (src_name == "parallel" && event.getStateChange() == ItemEvent.DESELECTED) {
				if (type == Hardpoint.HardpointType.GUN) {
					setGunParallel(false);
				}
			}
			else if (src_name == "over" && event.getStateChange() == ItemEvent.SELECTED) {
				if (type == Hardpoint.HardpointType.GUN) {
					setGunOver(true);
				}
				else if (type == Hardpoint.HardpointType.ENGINE) {
					engine_data.over = true;
				}
				else if (type == Hardpoint.HardpointType.REVERSE_ENGINE) {
					rev_engine_data.over = true;
				}
				else if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.over = true;
				}
				else if (type == Hardpoint.HardpointType.BAY) {
					bay_data.over = true;
				}
			}
			else if (src_name == "over" && event.getStateChange() == ItemEvent.DESELECTED) {
				if (type == Hardpoint.HardpointType.GUN) {
					setGunOver(false);
				}
				else if (type == Hardpoint.HardpointType.ENGINE) {
					engine_data.over = false;
				}
				else if (type == Hardpoint.HardpointType.REVERSE_ENGINE) {
					rev_engine_data.over = false;
				}
				else if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.over = false;
				}
				else if (type == Hardpoint.HardpointType.BAY) {
					bay_data.over = false;
				}
			}
			else if (src_name == "under" && event.getStateChange() == ItemEvent.SELECTED) {
				if (type == Hardpoint.HardpointType.TURRET) {
					setTurretUnder(true);
				}
				else if (type == Hardpoint.HardpointType.BAY) {
					bay_data.under = true;
				}
			}
			else if (src_name == "under" && event.getStateChange() == ItemEvent.DESELECTED) {
				if (type == Hardpoint.HardpointType.TURRET) {
					setTurretUnder(false);
				}
				else if (type == Hardpoint.HardpointType.BAY) {
					bay_data.under = false;
				}
			}
			else if (src_name == "left" && event.getStateChange() == ItemEvent.SELECTED) {
				if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.facing = Hardpoint.Facing.LEFT;
				}
			}
			else if (src_name == "left" && event.getStateChange() == ItemEvent.DESELECTED) {
				if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.facing = Hardpoint.Facing.NONE;
				}
			}
			else if (src_name == "right" && event.getStateChange() == ItemEvent.SELECTED) {
				if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.facing = Hardpoint.Facing.RIGHT;
				}
			}
			else if (src_name == "right" && event.getStateChange() == ItemEvent.DESELECTED) {
				if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.facing = Hardpoint.Facing.NONE;
				}
			}
			else if (src_name == "none" && event.getStateChange() == ItemEvent.SELECTED) {
				if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.facing = Hardpoint.Facing.NONE;
				}
			}
			else if (src_name == "none" && event.getStateChange() == ItemEvent.DESELECTED) {
				if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.facing = Hardpoint.Facing.NONE;
				}
			}
			else if (src_name == "auto" && event.getStateChange() == ItemEvent.SELECTED) {
				if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.facing = Hardpoint.Facing.AUTO;
				}
			}
			else if (src_name == "auto" && event.getStateChange() == ItemEvent.DESELECTED) {
				if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.facing = Hardpoint.Facing.AUTO;
				}
			}
			else if (src_name == "auto angle" && event.getStateChange() == ItemEvent.SELECTED) {
				if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.auto_angle = true;
				}
			}
			else if (src_name == "auto angle" && event.getStateChange() == ItemEvent.DESELECTED) {
				if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.auto_angle = false;
				}
			}
		}
	}

	class AngleSliderListener implements ChangeListener {
		JTextField paired_field;
		JTextField binding_field = null;
		JSlider binding_slider = null;
		Hardpoint.HardpointType type;
		String prop = null;
		public AngleSliderListener(JTextField field, Hardpoint.HardpointType n_type) {
			paired_field = field;
			type = n_type;
			prop = "default";
		}
		public AngleSliderListener(JTextField field, Hardpoint.HardpointType n_type, String prop_string) {
			paired_field = field;
			type = n_type;
			prop = prop_string;
		}
		public AngleSliderListener(JTextField field, Hardpoint.HardpointType n_type, String prop_string, 
									JTextField bind_field, JSlider bind_slider) {
			paired_field = field;
			binding_field = bind_field;
			binding_slider = bind_slider;
			type = n_type;
			prop = prop_string;
		}
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			double dval = ((Integer)source.getValue()).doubleValue();
			paired_field.setText("" + source.getValue());
			if (prop == "min arc") {
				if (dval > turret_data.arc_max) {
					turret_data.arc_max = dval;
					if (binding_slider != null) {
						binding_field.setText("" + source.getValue());
						binding_slider.setValue(source.getValue());
					}
				}
			}
			else if (prop == "max arc") {
				if (dval < turret_data.arc_min) {
					turret_data.arc_min = dval;
					if (binding_slider != null) {
						binding_field.setText("" + source.getValue());
						binding_slider.setValue(source.getValue());
					}
				}
			}
			if (type == Hardpoint.HardpointType.TURRET) {
				arcVisCanvas.doRedraw();
			}
			if (!source.getValueIsAdjusting()) {
				if (type == Hardpoint.HardpointType.GUN) {
					setGunAngle(source.getValue());
				}
				else if (type == Hardpoint.HardpointType.TURRET && prop == "default") {
					setTurretAngle(source.getValue());
					arcVisCanvas.doRedraw();
				}
				else if (type == Hardpoint.HardpointType.TURRET && prop == "min arc") {
					turret_data.arc_min = dval;
					checkSetArc();
					arcVisCanvas.doRedraw();
				}
				else if (type == Hardpoint.HardpointType.TURRET && prop == "max arc") {
					turret_data.arc_max = dval;
					checkSetArc();
					arcVisCanvas.doRedraw();
				}
				else if (type == Hardpoint.HardpointType.ENGINE) {
					engine_data.angle = dval;
				}
				else if (type == Hardpoint.HardpointType.REVERSE_ENGINE) {
					rev_engine_data.angle = dval;
				}
				else if (type == Hardpoint.HardpointType.STEERING_ENGINE) {
					ste_engine_data.angle = dval;
				}
				else if (type == Hardpoint.HardpointType.BAY) {
					bay_data.angle = dval;
				}
			}
		}
	}

	protected void checkSetArc() {
		if (turret_data.arc_min == -180. && turret_data.arc_max == 180.) {
			setTurretHaveArc(false);
		}
		else
			setTurretHaveArc(true);
	}
	class NumFieldListener implements PropertyChangeListener {
		JSlider paired_slider = null;
		JSlider binding_slider = null;
		Hardpoint.HardpointType type;
		String property_str;
		public NumFieldListener(JSlider slider, Hardpoint.HardpointType n_type) {
			paired_slider = slider;
			type = n_type;
			property_str = "angle";
		}
		public NumFieldListener(JSlider slider, Hardpoint.HardpointType n_type, String prop_str) {
			paired_slider = slider;
			type = n_type;
			property_str = prop_str;
		}
		public NumFieldListener(JSlider slider, Hardpoint.HardpointType n_type, String prop_str, JSlider bind_slider) {
			paired_slider = slider;
			binding_slider = bind_slider;
			type = n_type;
			property_str = prop_str;
		}
		@Override
		public void propertyChange(PropertyChangeEvent e) {
			JTextField src = (JTextField)e.getSource();
			// if (paired_slider != null) {
				JFormattedTextField fsrc = (JFormattedTextField) src;
				Object wtfareyou = fsrc.getValue();
				int val;
				double dval; //To allow user to input more precision.
				if (wtfareyou instanceof Double) {
					val = Math.toIntExact(Math.round((Double)wtfareyou));
					dval = (Double)wtfareyou;
					// System.out.println("Get FTextVal=Double");
				}
				else if (wtfareyou instanceof Long) {
					val = Math.toIntExact((Long)wtfareyou);
					dval = ((Long)wtfareyou).doubleValue();
					// System.out.println("Get FTextVal=Long");
				}
				else if (wtfareyou instanceof Integer) {
					val = (int)wtfareyou;
					dval = ((Integer)wtfareyou).doubleValue();
					// System.out.println("Get FTextVal=Int");
				}
				else {
					val = Integer.parseInt(fsrc.getText());
					dval = Double.parseDouble(fsrc.getText());
					// System.out.println("Get FTextVal=idfk");
				}
				// System.out.println("Get FTextVal=" + val);
				// System.out.println("Get FTextDVal=" + dval);
				if (val > 360) {
					val = 360;
					fsrc.setValue(val);
				}
				else if (val < -360) {
					val = -360;
					fsrc.setValue(val);
				}
				if (property_str == "min arc") {
					if (dval > turret_data.arc_max) {
						turret_data.arc_max = dval;
						if (binding_slider != null) {
							binding_slider.setValue(val);
						}
					}
				}
				else if (property_str == "max arc") {
					if (dval < turret_data.arc_min) {
						turret_data.arc_min = dval;
						if (binding_slider != null) {
							binding_slider.setValue(val);
						}
					}
				}
				if (paired_slider != null) {
					paired_slider.setValue(val);
				}
				if (type == Hardpoint.HardpointType.GUN && property_str == "angle") {
					setGunAngle(dval);
				}
				else if (type == Hardpoint.HardpointType.TURRET && property_str == "turn mult") {
					setTurretTurnMult(dval);
				}
				else if (type == Hardpoint.HardpointType.TURRET && property_str == "angle") {
					setTurretAngle(dval);
					arcVisCanvas.doRedraw();
				}
				else if (type == Hardpoint.HardpointType.TURRET && property_str == "min arc") {
					turret_data.arc_min = dval;
					checkSetArc();
					arcVisCanvas.doRedraw();
				}
				else if (type == Hardpoint.HardpointType.TURRET && property_str == "max arc") {
					turret_data.arc_max = dval;
					checkSetArc();
					arcVisCanvas.doRedraw();
				}
				else if (type == Hardpoint.HardpointType.ENGINE && property_str == "angle") {
					engine_data.angle = dval;
				}
				else if (type == Hardpoint.HardpointType.ENGINE && property_str == "zoom") {
					engine_data.zoom = dval;
				}
				else if (type == Hardpoint.HardpointType.ENGINE && property_str == "gimble") {
					engine_data.gimble = dval;
				}
				else if (type == Hardpoint.HardpointType.REVERSE_ENGINE && property_str == "angle") {
					rev_engine_data.angle = dval;
				}
				else if (type == Hardpoint.HardpointType.REVERSE_ENGINE && property_str == "zoom") {
					engine_data.zoom = dval;
				}
				else if (type == Hardpoint.HardpointType.REVERSE_ENGINE && property_str == "gimble") {
					engine_data.gimble = dval;
				}
				else if (type == Hardpoint.HardpointType.STEERING_ENGINE && property_str == "angle") {
					ste_engine_data.angle = dval;
				}
				else if (type == Hardpoint.HardpointType.STEERING_ENGINE && property_str == "zoom") {
					engine_data.zoom = dval;
				}
				else if (type == Hardpoint.HardpointType.STEERING_ENGINE && property_str == "gimble") {
					engine_data.gimble = dval;
				}
				else if (type == Hardpoint.HardpointType.BAY && property_str == "angle") {
					bay_data.angle = dval;
				}
			// }
		}
		
	}
}
