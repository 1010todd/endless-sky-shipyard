

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultEditorKit.CopyAction;

// import Hardpoint.HardpointType;

public class InfoDisplay extends JPanel{

	private JTextField coordinateField = new JTextField("X: 000.0 Y: 000.0 A: 0.0");
	private JTextArea hardpointArea = new JTextArea("");

	protected InfoDisplay info_panel;
	protected boolean set_clear_hardpoints = false;

	public InfoDisplay() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		info_panel = this;
		// this.setLayout(new GridLayout(5, 1));

		JButton clearBtn = new JButton("Clear");
		clearBtn.setBackground(Shipyard.backgroundColor);
		clearBtn.setForeground(Shipyard.textColor);
		clearBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		clearBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(clearBtn, 
											"You're about to delete all created hardpoint coordinate. Confirm?", 
											"Clear all",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.WARNING_MESSAGE);
				// System.out.println("res:" + res);
				if (res == 0)
				{
					info_panel.hardpointArea.setText("");
					set_clear_hardpoints = true;
				}
			}
		});

		JPanel top_subframe = new JPanel();
		top_subframe.setLayout(new GridLayout(1, 2));
		top_subframe.setMaximumSize(new Dimension(300, 30));

		coordinateField.setEditable(false);
		coordinateField.setVisible(true);
		coordinateField.setBackground(Shipyard.backgroundColor);
		coordinateField.setForeground(Shipyard.textColor);
		coordinateField.setAlignmentX(Component.LEFT_ALIGNMENT);
		coordinateField.setAlignmentY(Component.TOP_ALIGNMENT);
		// coordinateField.setMargin(new Insets(5, 5, 5, 5));
		coordinateField.setMinimumSize(new Dimension(120, 15));
		coordinateField.setMaximumSize(new Dimension(200, 15));
		coordinateField.setPreferredSize(new Dimension(150, 15));
		top_subframe.add(coordinateField);
		top_subframe.add(clearBtn);
		this.add(top_subframe);
		// this.add(coordinateField);

		JScrollPane scrollPane = new JScrollPane(hardpointArea);
		hardpointArea.setEditable(false);
		hardpointArea.setBackground(Shipyard.backgroundColor);
		hardpointArea.setForeground(Shipyard.textColor);
		hardpointArea.setVisible(true);
		hardpointArea.setTabSize(2);
		hardpointArea.setAlignmentX(Component.LEFT_ALIGNMENT);
		hardpointArea.setAlignmentY(Component.TOP_ALIGNMENT);
		hardpointArea.setPreferredSize(new Dimension(150, 500));
		this.add(scrollPane);
		// this.add(hardpointArea);

		InfoDisMouseListener mouseListener = new InfoDisMouseListener();
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
		this.addMouseWheelListener(mouseListener);
		hardpointArea.addMouseListener(mouseListener);
		hardpointArea.addMouseMotionListener(mouseListener);
		hardpointArea.addMouseWheelListener(mouseListener);
	}

	public void updateCoordinateField(double x, double y, double angle) {
		coordinateField.setText(String.format("X: %2.1f Y: %2.1f A: %2.1f", x, y, angle));
	}

	public void displayHardpoints(Ship ship) {
		if (set_clear_hardpoints) {
			ship.getHardpoints().clear();
			set_clear_hardpoints = false;
			//TODO:clear undo history?
		}
		// int x_off = 0;
		// int y_off = 12;
		hardpointArea.setText("");
		// x_off = 0;
		for (Hardpoint hp : ship.getHardpoints()) {
			hardpointArea.append("\t" + hp.data_string + " " + hp.x + " " + hp.y + "\n");
			if (hp.zoom != 1 && hp.zoom != 0) {
				hardpointArea.append("\t\tzoom " + hp.zoom + "\n");
			}
			if (hp.angle != 0) {
				hardpointArea.append("\t\tangle " + hp.angle + "\n");
			}
			if (hp.parallel) {
				hardpointArea.append("\t\tparallel" + "\n");
			}
			if (hp.gimble != 0) {
				hardpointArea.append("\t\tgimble " + hp.gimble + "\n");
			}
			if (hp.have_arc) {
				hardpointArea.append("\t\tarc " + hp.arc_min + " " + hp.arc_max + "\n");
			}
			if (hp.have_turn_mult) {
				hardpointArea.append("\t\t\"turret turn multiplier\" " + hp.turn_mult + "\n");
			}
			if (hp.over) {
				hardpointArea.append("\t\tover" + "\n");
			}
			if (hp.under) {
				hardpointArea.append("\t\tunder" + "\n");
			}
			if (hp.facing != null){
				switch (hp.facing){
					case LEFT:
						hardpointArea.append("\t\tleft" + "\n");
						break;
					case RIGHT:
						hardpointArea.append("\t\tright" + "\n");
						break;
					case BACK:
						hardpointArea.append("\t\tback" + "\n");
						break;
					default:
				}
			}
			if (hp.launch_effect != null && !hp.launch_effect.equals("") && hp.launch_effect_count > 0) {
				hardpointArea.append("\t\t\"launch effect\" " + hp.launch_effect + " " + hp.launch_effect_count + "\n");
			}
			// tmp.setText(hp.data_string + " " + hp.x + " " + hp.y);
			// System.out.println("Label: " + tmp.getText() + " at [" + x_off + ", " + y_off + "]");
			// x_off += tmp.getFont().getSize() + 5;
			// System.out.println("Check: " + tmp.getText() + " at [" + tmp.getLocation().x + ", " + tmp.getLocation().y + "]");
			// this.add(tmp);
		}
	}
	class InfoDisMouseListener extends MouseAdapter {
		public static final double max_dist_menu = 30.;
		private int recent_button;
		private Point menu_beginclick;
		protected DataMenu ifdmenu = new DataMenu();

		@Override
		public void mouseClicked(MouseEvent event) {
			System.out.println("IFD_Click:" + event.getButton() + " : " + event.getPoint());
		}

		@Override
		public void mousePressed(MouseEvent event) {
			System.out.println("IFD_Press:" + event.getButton() + " : " + event.getPoint());
			requestFocusInWindow();
			recent_button = event.getButton();
			if (event.getButton() == MouseEvent.BUTTON3) {
				menu_beginclick = event.getPoint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			System.out.println("IFD_Released:" + event.getButton() + ":" + event.getPoint());
			if (event.getButton() == MouseEvent.BUTTON3 && MathUtils.lengthPoints(menu_beginclick, event.getPoint()) < max_dist_menu) {
				ifdmenu.show(event.getComponent(), event.getX(), event.getY());
			}
			recent_button = 0;
		}
	}

	class DataMenu extends JPopupMenu implements ActionListener {
		private Vector<JMenuItem> items = new Vector<JMenuItem>();
		private Clipboard clipboard;

		public final List<String> datamenu_items = Arrays.asList(
			"copy"
		);
		public DataMenu() {
			for (String hp : datamenu_items) {
				JMenuItem tmp = new JMenuItem(hp);
				tmp.addActionListener(this);
				items.add(tmp);
				add(tmp);
			}
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(this.getClass().getName() + ".actionPerformed: Source=" + e.getSource());
			requestFocusInWindow();
			JMenuItem selected_menu = (JMenuItem) e.getSource();
			switch (selected_menu.getText()) {
				case "copy":
					StringSelection select = new StringSelection(hardpointArea.getText());
					clipboard.setContents(select, null);
					break ;
				default:
			}
		}
	}
}
