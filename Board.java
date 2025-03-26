
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.RenderingHints.Key;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import java.awt.Point;

// import Hardpoint.HardpointType;

//TODO: Animation?

public class Board extends JPanel implements ActionListener {

	private Timer timer;
	private Image img;
	protected Rectangle img_bound;
	private String filename = "shuttle.png";
	protected Ship curr_ship = null;
	protected double draw_x = 0, draw_y = 0;
	public boolean update_info = false;
	protected Stack<Hardpoint> undo_hp_history = new Stack<Hardpoint>();
	protected InfoDisplay info_panel;
	protected ControlPanel control_panel;
	protected JFrame this_frame;
	protected boolean altPressed = false; //For angle finding tool
	protected double draw_angle;
	protected boolean should_redraw;

	protected final int DELAY = 10;
	protected final double CIRCLE_DIA = 5.;
	protected final double LOCK_LINE_SCALE = 1.5;
	protected final double ANGLE_LINE_SCALE = 3;
	protected final int SHIP_BOUND_PAD = 10;

	public Board(String file_to_open, JFrame new_frame) {
		this_frame = new_frame;
		filename = file_to_open;
		initBoard();
	}

	private void initBoard() {
		loadShip(filename);
		setBackground(Color.GRAY);
		setFocusable(true);
		requestFocusInWindow();
		// this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		// this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
		// this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
		// this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
		// this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
		// KeyAction ka = new KeyAction();
		// this.getActionMap().put("up", ka);
		// this.getActionMap().put("down", ka);
		// this.getActionMap().put("left", ka);
		// this.getActionMap().put("right", ka);
		this.addKeyListener(new KeyAction());
		this.addMouseListener(new MouseButtonListener());
		this.addMouseMotionListener(new MouseButtonListener());
		this.addMouseWheelListener(new MouseButtonListener());
		timer = new Timer(DELAY, this);
		timer.start();
	}

	public void setInfoPanel(InfoDisplay newInfoPanel) {
		info_panel = newInfoPanel;
	}

	public void setControlPanel(ControlPanel newcontrol_panel) {
		control_panel = newcontrol_panel;
	}

	public void setThis_frame(JFrame new_frame) {
		this_frame = new_frame;
	}
	public JFrame getThis_frame() {
		return this_frame;
	}

	public Ship getShip() {
		return curr_ship;
	}

	public Point2D toShipCoord() {
		// double x = draw_x - getSize().getWidth() * .5 - img.getWidth(null);
		// double y = draw_y - getSize().getHeight() * .5 - img.getHeight(null);
		double x = Math.round((draw_x - getSize().getWidth() * .5 - curr_ship.draw_off_x) / (curr_ship.getImgScale()));
		double y = Math.round((draw_y - getSize().getHeight() * .5 - curr_ship.draw_off_y) / (curr_ship.getImgScale()));
		// double x = Math.round((draw_x - curr_ship.getX()) / (curr_ship.getImgScale()));
		// double y = Math.round((draw_y - curr_ship.getY()) / (curr_ship.getImgScale()));
		return new Point2D.Double(x * .5, y * .5);
	}

	public void undo() {
		int ind = curr_ship.getHardpoints().size() - 1;
		System.out.println("Board Undo." + ind);
		if (ind >= 0) {
			undo_hp_history.add(curr_ship.getHardpoints().get(ind));
			curr_ship.getHardpoints().remove(ind);
			update_info = true;
		}
	}

	public void redo() {
		System.out.println("Board Redo.");
		if (!undo_hp_history.isEmpty()) {
			curr_ship.getHardpoints().add(undo_hp_history.pop());
			update_info = true;
		}
	}

	public void loadShip(String name) {
		System.out.println("Loading ship: " + name);
		try {
			curr_ship = new Ship(name, this_frame);
		} catch (IOException e) {
			// System.out.println("Failed to load ship: " + curr_ship.getFileName());
			// System.out.println("Message: " + e.getMessage());
			JOptionPane.showMessageDialog(this,
											"Failed to load image: " + e.getMessage() + "\nIf this happened on start-up the program will exit.",
											"Failed to load image",
											JOptionPane.ERROR_MESSAGE);
			if (curr_ship == null) {
				System.exit(1);
			}
			return ;
		}
		System.out.println("Loaded ship: " + curr_ship.getFileName());
		img = curr_ship.getImage();
		if (img == null) {
			System.out.println("Failed to load ship image");
			JOptionPane.showMessageDialog(this,
											"Failed to get image",
											"Failed to get image",
											JOptionPane.ERROR_MESSAGE);

			System.exit(2);
		}
		else {
			System.out.println("Ship image loaded");
		}
		should_redraw = true;
	}

	// 	@Override
	// 	public void paintComponent(Graphics g) {
	// 		super.paintComponent(g);
	// 		Point center = new Point((int)(getWidth() * .5), (int)(getHeight() * .5));
	// 		Point idle_at = MathUtils.polarToCartesian(IDL_LEN, turret_data.angle - 90);
	// 		Point min = MathUtils.polarToCartesian(LIM_LEN, turret_data.angle + turret_data.arc_min - 90);
	// 		Point max = MathUtils.polarToCartesian(LIM_LEN, turret_data.angle + turret_data.arc_max - 90);

	// 		// System.out.println("len:"+IDL_LEN+" angle:"+turret_data.angle+"P="+idle_at+"Orign:"+center);
	// 		idle_at.x += center.x;
	// 		idle_at.y += center.y;
	// 		min.x += center.x;
	// 		min.y += center.y;
	// 		max.x += center.x;
	// 		max.y += center.y;
	// 		// System.out.println("len:"+IDL_LEN+" angle:"+turret_data.angle+"P="+idle_at+"Orign:"+center);
	// 		drawLine(g, center, min, Color.GREEN, 1);
	// 		drawLine(g, center, max, Color.RED, 1);
	// 		drawLine(g, center, idle_at, Color.CYAN, 2);
	// 		drawCircle(g, getWidth() * .5, getHeight() * .5, Color.CYAN);
	// 		Toolkit.getDefaultToolkit().sync();
	// 	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// System.out.println("paintComponent" );
		// Dimension size = getSize();

		double w = getSize().getWidth();
		double h = getSize().getHeight();
		int img_x = (int)w/2 - img.getWidth(null)/2;
		int img_y = (int)h/2 - img.getHeight(null)/2;
		curr_ship.setImgX(img_x);
		curr_ship.setImgY(img_y);
		// System.out.println("Ship paint image X: " + img_x);
		// System.out.println("Ship paint image Y: " + img_y);

		img_x += curr_ship.draw_off_x;
		img_y += curr_ship.draw_off_y;
		g.drawImage(img, img_x, img_y, null);
		img_bound = new Rectangle(img_x - SHIP_BOUND_PAD, img_y - SHIP_BOUND_PAD,
								img.getWidth(null) + SHIP_BOUND_PAD,
								img.getHeight(null) + SHIP_BOUND_PAD);
		if (control_panel.isMirrorToggle()) {
			double img_center_x = (img_x + img.getWidth(null) * .5);
			double img_center_y = (img_y + img.getHeight(null) * .5);
			double mirr_cir_x = (img_center_x - (draw_x - img_center_x));
			drawDashLine(g, new Point((int)img_center_x, 0), new Point((int)img_center_x, (int)h), Color.RED, 1f);
			drawIndicatorCircle(g, mirr_cir_x, draw_y, Color.GREEN, true);
		}
		drawIndicatorCircle(g, draw_x, draw_y, Color.RED, false);
		if (altPressed) {
			drawLine(g, new Point((int)draw_x, (int)draw_y), getMousePosition(), Color.RED, 1);
			if (getMousePosition() != null) {
				double tmp_x = getMousePosition().x - draw_x;
				double tmp_y = getMousePosition().y - draw_y;
				draw_angle = (180 - Math.toDegrees(MathUtils.cartesianToPolarAngle(tmp_x, tmp_y)));
			}
		}
		Toolkit.getDefaultToolkit().sync();
	}

	private double toGameAngle(double angle, boolean mirror) {
		double inv_a = 1;
		if (mirror) {
			inv_a = -1;
		}
		return (angle * inv_a - 90);
	}

	protected void drawIndicatorCircle(Graphics g, double x, double y, Color col, boolean mirror) {
		drawCircle(g, x, y, col);
		if (control_panel.isLockX()) {
			Point lockX_l_src = new Point((int)x, (int)(y + CIRCLE_DIA * .5));
			Point lockX_l_dst = new Point((int)x, (int)(y + CIRCLE_DIA * LOCK_LINE_SCALE));
			Point lockX_l_src_n = new Point((int)x, (int)(y - CIRCLE_DIA * .5));
			Point lockX_l_dst_n = new Point((int)x, (int)(y - CIRCLE_DIA * LOCK_LINE_SCALE));
			drawLine(g, lockX_l_src, lockX_l_dst, col,1f );
			drawLine(g, lockX_l_src_n, lockX_l_dst_n, col,1f );
		}
		else if (control_panel.isLockY()) {
			Point lock_l_src = new Point((int)(x + CIRCLE_DIA * .5), (int)y);
			Point lock_l_dst = new Point((int)(x + CIRCLE_DIA * LOCK_LINE_SCALE), (int)y);
			Point lock_l_src_a = new Point((int)(x - CIRCLE_DIA * .5), (int)y);
			Point lock_l_dst_a = new Point((int)(x - CIRCLE_DIA * LOCK_LINE_SCALE), (int)y);
			drawLine(g, lock_l_src, lock_l_dst, col,1f );
			drawLine(g, lock_l_src_a, lock_l_dst_a, col,1f );
		}
		// System.out.println("selected hp" + control_panel.getHardpointPanel().getSelectedIndex());
		int selectedHardpoint = control_panel.getHardpointPanel().getSelectedIndex();
		Point angle_targ = null;
		
		switch(Hardpoint.hp_types.get(selectedHardpoint)) {
			case "gun":
				angle_targ = MathUtils.polarToCartesian(CIRCLE_DIA * ANGLE_LINE_SCALE, toGameAngle(control_panel.getGunData().angle, mirror));
				break;
			case "turret":
				angle_targ = MathUtils.polarToCartesian(CIRCLE_DIA * ANGLE_LINE_SCALE, toGameAngle(control_panel.getTurretData().angle, mirror));
				double t_a = control_panel.getTurretData().angle;
				Point src = new Point((int)x, (int)y);
				Point arcmin = MathUtils.polarToCartesian(CIRCLE_DIA * ANGLE_LINE_SCALE * .75, toGameAngle(t_a + control_panel.getTurretData().arc_min, mirror));
				Point arcmax = MathUtils.polarToCartesian(CIRCLE_DIA * ANGLE_LINE_SCALE * .75, toGameAngle(t_a + control_panel.getTurretData().arc_max, mirror));
				arcmin.x += src.x;
				arcmin.y += src.y;
				arcmax.x += src.x;
				arcmax.y += src.y;
				drawLine(g, src, arcmin, Color.GREEN,1f );
				drawLine(g, src, arcmax, Color.RED,1f );
				break;
			case "engine":
				angle_targ = MathUtils.polarToCartesian(CIRCLE_DIA * ANGLE_LINE_SCALE, toGameAngle(control_panel.engine_data.angle - 180, mirror));
				break;
			case "reverse engine":
				angle_targ = MathUtils.polarToCartesian(CIRCLE_DIA * ANGLE_LINE_SCALE, toGameAngle(control_panel.rev_engine_data.angle, mirror));
				break;
			case "steering engine":
				angle_targ = MathUtils.polarToCartesian(CIRCLE_DIA * ANGLE_LINE_SCALE, toGameAngle(control_panel.ste_engine_data.angle - 180, mirror));
				break;
			case "bay":
				angle_targ = MathUtils.polarToCartesian(CIRCLE_DIA * ANGLE_LINE_SCALE, toGameAngle(control_panel.bay_data.angle, mirror));
				break;
		}
		if (angle_targ != null) {
			Point src = new Point((int)x, (int)y);
			angle_targ.x += src.x;
			angle_targ.y += src.y;
			drawLine(g, src, angle_targ, Color.MAGENTA,1f );
		}
	}

	protected void drawDashLine(Graphics g, Point origin, Point dst, Color col, float line_width) {
		float dash_center_line[] = {10.0f, 5f, 2f, 5f};
		BasicStroke dashedStroke = new BasicStroke(line_width,	BasicStroke.CAP_ROUND,
																BasicStroke.JOIN_ROUND,
																10f, dash_center_line, 0.0f);
		drawLine(g, origin, dst, col, dashedStroke);
	}

	protected void drawLine(Graphics g, Point origin, Point dst, Color col, float line_width) {
		drawLine(g, origin, dst, col, new BasicStroke(line_width));
	}

	protected void drawLine(Graphics g, Point origin, Point dst, Color col, BasicStroke stoke) {
		Graphics2D g2d = (Graphics2D) g;
		if (dst == null)
			return ;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHints(rh);

		Line2D line = new Line2D.Double(origin.x, origin.y, dst.x, dst.y);
		g2d.setStroke(stoke);
		g2d.setColor(col);
		g2d.draw(line);
		should_redraw = true;
	}

	//Draw circle with center on x, y
	protected void drawCircle(Graphics g, double x, double y, Color col) {
		Graphics2D g2d = (Graphics2D) g;

		// System.out.println("Drawing Circle with col:" + col);
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
		should_redraw = true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println("Action Performed");
		step();
	}

	private void step() {
		if (curr_ship.scaleImg()) {
			img = curr_ship.getImage();
		}
		if (should_redraw){
			repaint();
			should_redraw = false;
		}
		if (update_info) {
			if (info_panel != null) {
				info_panel.updateCoordinateField(toShipCoord().getX(), toShipCoord().getY(), draw_angle);
				info_panel.displayHardpoints(curr_ship);
			}
			update_info = false;
		}
		String new_img_path = control_panel.consumeNewImgPath();
		if (new_img_path != null) {
			loadShip(new_img_path);
		}
	}

	public void setDrawX(double x) {
		if (!control_panel.isLockX()) {
			draw_x = x;
			should_redraw = true;
		}
	}
	public void setDrawY(double y) {
		if (!control_panel.isLockY()) {
			draw_y = y;
			should_redraw = true;
		}
	}

	protected int recent_button;
	protected double prev_x, prev_y;
	protected Point menu_beginclick;

	class MouseButtonListener extends MouseAdapter {
		public static final double max_dist_menu = 30.;
		/*
		 * 1 = left
		 * 2 = middle
		 * 3 = right
		 * 5 = forward
		 * 4 = backward
		 */
		@Override
		public void mouseClicked(MouseEvent event) {
			// System.out.println("Click:" + event.getButton() + " : " + event.getPoint());
		}

		@Override
		public void mousePressed(MouseEvent event) {
			// System.out.println("Press:" + event.getButton() + " : " + event.getPoint());
			requestFocusInWindow();
			recent_button = event.getButton();
			if (event.getButton() == MouseEvent.BUTTON1 && altPressed) {
				int selectedHardpoint = control_panel.getHardpointPanel().getSelectedIndex();
				// Point angle_targ = null;
				
				switch(Hardpoint.hp_types.get(selectedHardpoint)) {
					case "gun":
						control_panel.setHardpointAngle("gun", draw_angle);
						break;
					case "turret":
						control_panel.setHardpointAngle("turret", draw_angle);
						break;
					case "engine":
						control_panel.setHardpointAngle("engine", draw_angle);
						break;
					case "reverse engine":
						control_panel.setHardpointAngle("reverse engine", draw_angle);
						break;
					case "steering engine":
						control_panel.setHardpointAngle("steering engine", draw_angle);
						break;
					case "bay":
						control_panel.setHardpointAngle("bay", draw_angle);
						break;
				}
			}
			else if (event.getButton() == MouseEvent.BUTTON1 && img_bound.contains(event.getPoint())) {
				if (control_panel.isSnapCenter() && shouldSnapCenter(event.getPoint().getX())) {
					setDrawX(0);
					setDrawY(event.getPoint().getY());
				}
				else {
					setDrawX(event.getPoint().getX());
					setDrawY(event.getPoint().getY());
				}
				update_info = true;
			}
			else if (event.getButton() == MouseEvent.BUTTON2) {
				// curr_ship.setImgX();
				prev_x = event.getX();
				prev_y = event.getY();
			}
			else if (event.getButton() == MouseEvent.BUTTON3) {
				menu_beginclick = event.getPoint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			// System.out.println("Released:" + event.getButton() + ":" + event.getPoint());
			if (event.getButton() == MouseEvent.BUTTON3 && MathUtils.lengthPoints(menu_beginclick, event.getPoint()) < max_dist_menu) {
				HardpointMenu menu = new HardpointMenu();
				menu.show(event.getComponent(), event.getX(), event.getY());
			}
			recent_button = 0;
			prev_x = 0;
			prev_y = 0;
		}


		@Override
		public void mouseDragged(MouseEvent event) {
			// System.out.println("Drag:" + event.getButton() + " : " + event.getPoint());
			if (recent_button == MouseEvent.BUTTON1 && img_bound.contains(event.getPoint()) && !altPressed) {
				setDrawX(event.getPoint().getX());
				setDrawY(event.getPoint().getY());
				update_info = true;
			}
			if (recent_button == MouseEvent.BUTTON2) {
				double d_x = prev_x - event.getX();
				double d_y = prev_y - event.getY();
				// System.out.println("Drag Btn2 dx dy:" + d_x + ", " + d_y);
				curr_ship.draw_off_x -= d_x;
				curr_ship.draw_off_y -= d_y;
				// System.out.println("Drag Btn2 drx dry:" + curr_ship.draw_off_x + ", " + curr_ship.draw_off_y);
				prev_x = event.getX();
				prev_y = event.getY();
				should_redraw = true;
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e){
			// System.out.println("Scroll: Unit2Scroll :" + e.getUnitsToScroll() + " WheelRot: " + e.getWheelRotation());
			// System.out.println("Prev img_scale: " + curr_ship.img_scale);
			if ((curr_ship.img_scale > 0.05 || e.getWheelRotation() > 0) &&
				(curr_ship.img_scale <= 10 || e.getWheelRotation() < 0)) {
				curr_ship.img_scale += e.getUnitsToScroll() * 0.01;
				should_redraw = true;
			}
			// System.out.println("Curr img_scale: " + curr_ship.img_scale);
		}
	}

	class KeyAction extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_A) {
				altPressed = false;
				should_redraw = true;
				update_info = true;
			}
		}
		@Override
		public void keyPressed(KeyEvent e) {
			double mod = 1.;
			if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
				mod *= 10.;
			}
			if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK) {
				mod *= 5.;
			}
			// System.out.println("KeyPressed:" + e.getKeyCode() + " : " + e.getKeyLocation());
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				setDrawY(draw_y - .5 * mod);
				should_redraw = true;
				update_info = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				setDrawY(draw_y + .5 * mod);
				should_redraw = true;
				update_info = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				setDrawX(draw_x - .5 * mod);
				should_redraw = true;
				update_info = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				setDrawX(draw_x + .5 * mod);
				should_redraw = true;
				update_info = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_A) {
				altPressed = true;
				should_redraw = true;
				update_info = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_HOME) {
				//reset
				curr_ship.draw_off_x = 0;
				curr_ship.draw_off_y = 0;
				curr_ship.img_scale = curr_ship.getStartingImgScale();
				should_redraw = true;
				update_info = true;
			}
		}

	}

	public boolean shouldSnapCenter(double x) {
		double tolerance = curr_ship.getWidth() * 0.005;
		return ((x <= 0.51 && x >= -0.51) || (x <= tolerance && x >= -tolerance));
	}

	public void addHardpoint(Hardpoint.HardpointType type) {
		double x_fin = toShipCoord().getX();
		double y_fin = toShipCoord().getY();
		// double tolerance = curr_ship.getWidth() * 0.005;
		if (control_panel.isSnapCenter() && shouldSnapCenter(x_fin)) {
			x_fin = 0.0;
		}
		addHardpoint(type, x_fin, y_fin, false);
		if (control_panel.isMirrorToggle() && (x_fin >= 0.5 || x_fin <= -0.5)) {
			addHardpoint(type, -x_fin, y_fin, true);
		}
	}

	public void addHardpoint(Hardpoint.HardpointType type, double x, double y, boolean mirrored) {
		curr_ship.createHardpoint(type, x, y, mirrored);
		should_redraw = true;
		update_info = true;
	}

	class HardpointMenu extends JPopupMenu implements ActionListener {
		private Vector<JMenuItem> items = new Vector<JMenuItem>();
		// public double pos_x, pos_y;

		public HardpointMenu() {
			for (String hp : Hardpoint.hp_types) {
				JMenuItem tmp = new JMenuItem(hp);
				tmp.addActionListener(this);
				items.add(tmp);
				add(tmp);
			}
		}

		// protected void addHardpoint(Hardpoint.HardpointType type) {
		// 	double x_fin = toShipCoord().getX();
		// 	double y_fin = toShipCoord().getY();
		// 	if (control_panel.isSnapCenter() && (x_fin <= 0.51 && x_fin >= -0.51)) {
		// 		x_fin = 0.0;
		// 	}
		// 	addHardpoint(type, x_fin, y_fin, false);
		// 	if (control_panel.isMirrorToggle() && (x_fin >= 0.5 || x_fin <= -0.5)) {
		// 		addHardpoint(type, -x_fin, y_fin, true);
		// 	}
		// }

		// protected void addHardpoint(Hardpoint.HardpointType type, double x, double y, boolean mirrored) {
		// 	curr_ship.createHardpoint(type, x, y, mirrored);
		// 	update_info = true;
		// }

		@Override
		public void actionPerformed(ActionEvent e) {
			// if (!(e.getSource() instanceof JMenuItem))
			// 	return ;
			// System.out.println(this.getClass().getName() + ".actionPerformed: Source=" + e.getSource());
			JMenuItem selected_menu = (JMenuItem) e.getSource();
			if (selected_menu.getText().equals("gun")) {
				addHardpoint(Hardpoint.HardpointType.GUN);
			}
			else if (selected_menu.getText().equals("turret")) {
				addHardpoint(Hardpoint.HardpointType.TURRET);
			}
			else if (selected_menu.getText().equals("engine")) {
				addHardpoint(Hardpoint.HardpointType.ENGINE);
			}
			else if (selected_menu.getText().equals("reverse engine")) {
				addHardpoint(Hardpoint.HardpointType.REVERSE_ENGINE);
			}
			else if (selected_menu.getText().equals("steering engine")) {
				addHardpoint(Hardpoint.HardpointType.STEERING_ENGINE);
			}
			else if (selected_menu.getText().equals("bay")) {
				addHardpoint(Hardpoint.HardpointType.BAY);
			}
		}
	}

}