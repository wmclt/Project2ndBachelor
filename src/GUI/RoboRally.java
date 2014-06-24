package GUI;

import static java.lang.System.out;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class RoboRally<Board, Robot, Wall, Battery, RepairKit, SurpriseBox> extends JFrame {
	
	private static final long serialVersionUID = 1949718792817670580L;
	private static final long BOARD_WIDTH = 2000;
	private static final long BOARD_HEIGHT = 1000;
	
	private Map<String, Robot> robots = new HashMap<String, Robot>();
	private Map<String, Battery> batteries = new HashMap<String, Battery>();
	private Map<String, RepairKit> repairKits = new HashMap<String, RepairKit>();
	private Map<String, SurpriseBox> surpriseBoxes = new HashMap<String, SurpriseBox>();
	private Board board;
	private final RoboRallyView<Board, Robot, Wall, Battery, RepairKit, SurpriseBox> view;
	private final IFacade<Board, Robot, Wall, Battery, RepairKit, SurpriseBox> facade;
	private final JLabel statusBar;
	
	public RoboRally(IFacade<Board, Robot, Wall, Battery, RepairKit, SurpriseBox> facade) {
		super("RoboRally");
		this.facade = facade;
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
		board = facade.createBoard(BOARD_WIDTH, BOARD_HEIGHT);
		this.setAlwaysOnTop(true);
		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		statusBar = new JLabel();
		statusBar.setAlignmentX(LEFT_ALIGNMENT);
		statusBar.setHorizontalTextPosition(SwingConstants.LEFT);
		view = new RoboRallyView<Board, Robot, Wall, Battery, RepairKit, SurpriseBox>(this);
		root.add(view);
		root.add(statusBar);
		this.add(root);
		this.setPreferredSize(new Dimension(400, 400));
		this.pack();
	}
	
	void setStatus(String msg) {
		statusBar.setText(msg);	
	}
	
	Board getBoard() {
		return board;
	}
	
	IFacade<Board, Robot, Wall, Battery, RepairKit, SurpriseBox> getFacade() {
		return facade;
	}
	
	String getRobotName(Robot robot) {
		for(Entry<String, Robot> entry : robots.entrySet()) {
			if(entry.getValue() == robot) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	String getBatteryName(Battery battery) {
		for(Entry<String, Battery> entry : batteries.entrySet()) {
			if(entry.getValue() == battery) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	String getRepairKitName(RepairKit repairKit) {
		for(Entry<String, RepairKit> entry : repairKits.entrySet()) {
			if(entry.getValue() == repairKit) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	String getSurpriseBoxName(SurpriseBox surpriseBox) {
		for(Entry<String, SurpriseBox> entry : surpriseBoxes.entrySet()) {
			if(entry.getValue() == surpriseBox) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	private boolean existsItemNamed(String name) {
		return batteries.containsKey(name) || repairKits.containsKey(name) || surpriseBoxes.containsKey(name);
	}
	
	private void processCommand(String command) {
		String[] words = command.split(" ");
		if(words[0].equals("addrobot") && 4 <= words.length && words.length <= 5) {
			String name = words[1];
			if(robots.containsKey(name)) {
				out.println("robot named " + name + " already exists");
				return;
			}
			long x, y;
			try {
				x = Long.parseLong(words[2]);
				y = Long.parseLong(words[3]);
			} catch(NumberFormatException ex) {
				out.println("position expected but found " + words[2] + " " + words[3]);
				return;
			}
			double initialEnergy = 10000;
			if(5 <= words.length ) {
				try {
					initialEnergy = Double.parseDouble(words[4]);
				} catch(NumberFormatException ex) {
					out.println("double expected but found " + words[4]);
					return;
				}
			}
			Robot newRobot = facade.createRobot(1, initialEnergy);
			if(newRobot != null) {
				robots.put(words[1], newRobot);
				facade.putRobot(board, x, y, newRobot);
			}
		} else if(words[0].equals("addbattery") && 4 <= words.length && words.length <= 6) {
			String name = words[1];
			if(existsItemNamed(name)) {
				out.println("item named " + name + " already exists");
				return;
			}
			long x, y;
			try {
				x = Long.parseLong(words[2]);
				y = Long.parseLong(words[3]);
			} catch(NumberFormatException ex) {
				out.println("position expected but found " + words[2] + " " + words[3]);
				return;
			}
			double initialEnergy = 1000;
			if(5 <= words.length ) {
				try {
					initialEnergy = Double.parseDouble(words[4]);
				} catch(NumberFormatException ex) {
					out.println("double expected but found " + words[4]);
					return;
				}
			}
			int weight = 1500;
			if(6 <= words.length) {
				try {
					weight = Integer.parseInt(words[5]);
				} catch(NumberFormatException ex) {
					out.println("integer expected but found " + words[5]);
					return;
				}
			}
			Battery newBattery = facade.createBattery(initialEnergy, weight);
			if(newBattery != null) {
				batteries.put(words[1], newBattery);
				facade.putBattery(board, x, y, newBattery);
			}
		} else if(words[0].equals("addwall") && words.length == 3) {
			int x, y;
			try {
				x = Integer.parseInt(words[1]);
				y = Integer.parseInt(words[2]);
			} catch(NumberFormatException ex) {
				out.println("position expected but found " + words[1] + " " + words[2]);
				return;
			}
			Wall wall = facade.createWall();
			if(wall != null) {
				facade.putWall(board, x, y, wall);
			}
		} else if(words[0].equals("addrepair") && 5 == words.length) {
			String name = words[1];
			if(existsItemNamed(name)) {
				out.println("item named " + name + " already exists");
				return;
			}
			long x, y;
			try {
				x = Long.parseLong(words[2]);
				y = Long.parseLong(words[3]);
			} catch(NumberFormatException ex) {
				out.println("position expected but found " + words[2] + " " + words[3]);
				return;
			}
			double repairAmount;
			try {
				repairAmount = Double.parseDouble(words[4]);
			} catch(NumberFormatException ex) {
				out.println("double expected but found " + words[4]);
				return;
			}
			RepairKit newRepairKit = facade.createRepairKit(repairAmount, 1000);
			if(newRepairKit != null) {
				repairKits.put(words[1], newRepairKit);
				facade.putRepairKit(board, x, y, newRepairKit);
			}
		}  else if(words[0].equals("addsurprise") && 5 == words.length) {
			String name = words[1];
			if(existsItemNamed(name)) {
				out.println("item named " + name + " already exists");
				return;
			}
			long x, y;
			try {
				x = Long.parseLong(words[2]);
				y = Long.parseLong(words[3]);
			} catch(NumberFormatException ex) {
				out.println("position expected but found " + words[2] + " " + words[3]);
				return;
			}
			int weight;
			try {
				weight = Integer.parseInt(words[4]);
			} catch(NumberFormatException ex) {
				out.println("double expected but found " + words[4]);
				return;
			}
			SurpriseBox newSurpriseBox = facade.createSurpriseBox(weight);
			if(newSurpriseBox != null) {
				surpriseBoxes.put(words[1], newSurpriseBox);
				facade.putSurpriseBox(board, x, y, newSurpriseBox);
			}
		} else if(words[0].equals("move") && words.length == 2) {
			String name = words[1];
			if(! robots.containsKey(name)) {
				out.println("robot named " + name + " does not exist");
				return;
			}
			facade.move(robots.get(name));
		} else if(words[0].equals("turn") && words.length == 2) {
			String name = words[1];
			if(! robots.containsKey(name)) {
				out.println("robot named " + name + " does not exist");
				return;
			}
			facade.turn(robots.get(name));
		} else if(words[0].equals("pickup") && words.length == 3) {
			String rname = words[1];
			if(! robots.containsKey(rname)) {
				out.println("robot named " + rname + " does not exist");
				return;
			}
			String iname = words[2];
			if(! existsItemNamed(iname)) {
				out.println("item named " + iname + " does not exist");
				return;
			}
			if(batteries.containsKey(iname)) {
				facade.pickUpBattery(robots.get(rname), batteries.get(iname));
			} else if(repairKits.containsKey(iname)) {
				facade.pickUpRepairKit(robots.get(rname), repairKits.get(iname));
			} else {
				facade.pickUpSurpriseBox(robots.get(rname), surpriseBoxes.get(iname));
			} 
		} else if(words[0].equals("use") && words.length == 3) {
			String rname = words[1];
			if(! robots.containsKey(rname)) {
				out.println("robot named " + rname + " does not exist");
				return;
			}
			String iname = words[2];
			if(! existsItemNamed(iname)) {
				out.println("item named " + iname + " does not exist");
				return;
			}
			if(batteries.containsKey(iname)) {
				facade.useBattery(robots.get(rname), batteries.get(iname));
			} else if(repairKits.containsKey(iname)) {
				facade.useRepairKit(robots.get(rname), repairKits.get(iname));
			} else {
				facade.useSurpriseBox(robots.get(rname), surpriseBoxes.get(iname));
			}
		} else if(words[0].equals("transfer") && words.length == 3) {
			String rname = words[1];
			if(! robots.containsKey(rname)) {
				out.println("robot named " + rname + " does not exist");
				return;
			}
			String rname2 = words[2];
			if(! robots.containsKey(rname2)) {
				out.println("robot named " + rname2 + " does not exist");
				return;
			}
			facade.transferItems(robots.get(rname), robots.get(rname2));
		} else if(words[0].equals("drop") && words.length == 3) {
			String rname = words[1];
			if(! robots.containsKey(rname)) {
				out.println("robot named " + rname + " does not exist");
				return;
			}
			String iname = words[2];
			if(! existsItemNamed(iname)) {
				out.println("item named " + iname + " does not exist");
				return;
			}
			if(batteries.containsKey(iname)) {
				facade.dropBattery(robots.get(rname), batteries.get(iname));
			} else if(repairKits.containsKey(iname)) {
				facade.dropRepairKit(robots.get(rname), repairKits.get(iname));
			} else {
				facade.dropSurpriseBox(robots.get(rname), surpriseBoxes.get(iname));
			}
		} else if(words[0].equals("moveto") && words.length == 3) {
			String rname = words[1];
			if(! robots.containsKey(rname)) {
				out.println("robot named " + rname + " does not exist");
				return;
			}
			String rname2 = words[2];
			if(! robots.containsKey(rname2)) {
				out.println("robot named " + rname2 + " does not exist");
				return;
			}
			facade.moveNextTo(robots.get(rname), robots.get(rname2));
		} else if(words[0].equals("shoot") && words.length == 2) {
			String rname = words[1];
			if(! robots.containsKey(rname)) {
				out.println("robot named " + rname + " does not exist");
				return;
			}
			facade.shoot(robots.get(rname));
		} else if(words[0].equals("canreach") && words.length == 4) {
			String name = words[1];
			if(! robots.containsKey(name)) {
				out.println("robot named " + name + " does not exist");
				return;
			}
			int x, y;
			try {
				x = Integer.parseInt(words[2]);
				y = Integer.parseInt(words[3]);
			} catch(NumberFormatException ex) {
				out.println("position expected but found " + words[2] + " " + words[3]);
				return;
			}
			
			double required = facade.getMinimalCostToReach(robots.get(name), x, y);
			if(required == -1) {
				out.println("no (blocked by obstacles)");
			} else if(required == -2) {
				out.println("no (insufficient energy)");
			} else {
				out.println("yes (consuming " + required + " ws)");
			}
		} else if(words[0].equals("loadprogram") && words.length == 3) {
			String name = words[1];
			if(! robots.containsKey(name)) {
				out.println("robot named " + name + " does not exist");
				return;
			}
			facade.loadProgramFromFile(robots.get(name), words[2]);
		} else if(words[0].equals("saveprogram") && words.length == 3) {
			String name = words[1];
			if(! robots.containsKey(name)) {
				out.println("robot named " + name + " does not exist");
				return;
			}
			facade.saveProgramToFile(robots.get(name), words[2]);
		} else if(words[0].equals("showprogram") && words.length == 2) {
			String name = words[1];
			if(! robots.containsKey(name)) {
				out.println("robot named " + name + " does not exist");
				return;
			}
			StringWriter writer = new StringWriter();
			facade.prettyPrintProgram(robots.get(name), writer);
			out.println(writer.toString());
		} else if(words[0].equals("execute") && words.length == 3) {
			String name = words[1];
			if(! robots.containsKey(name)) {
				out.println("robot named " + name + " does not exist");
				return;
			}
			int nbSteps;
			try {
				nbSteps = Integer.parseInt(words[2]);
			} catch(NumberFormatException ex) {
				out.println("integer expected but found " + words[2]);
				return;
			}
			while(0 < nbSteps) {
				facade.stepn(robots.get(name), 1);
				this.repaint();
				if(nbSteps != 0) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				nbSteps--;
			}
		} else if(words[0].equals("executeall") && words.length == 2) {
			int nbSteps;
			try {
				nbSteps = Integer.parseInt(words[1]);
			} catch(NumberFormatException ex) {
				out.println("integer expected but found " + words[1]);
				return;
			}
			while(0 < nbSteps) {
				for(Robot robot : robots.values()) {
					facade.stepn(robot, 1);
				}
				this.repaint();
				if(nbSteps != 0) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				nbSteps--;
			}
		} else if(words[0].equals("help") && words.length == 1)  {
			out.println("commands:");
			out.println("\taddbattery <iname> <long> <long> [<double>] [<int>]");
			out.println("\taddrepair <iname> <long> <long> <double>");
			out.println("\taddsurprise <iname> <long> <long> <double>");
			out.println("\taddwall <long> <long>");
			out.println("\taddrobot <rname> <long> <long> [<double>]");
			out.println("\tmove <rname>");
			out.println("\tturn <rname>");
			out.println("\tshoot <rname>");
			out.println("\tpickup <rname> <iname>");
			out.println("\tuse <rname> <iname>");
			out.println("\ttransfer <rname> <rname>");
			out.println("\tdrop <rname> <iname>");
			out.println("\tcanreach <rname> <long> <long>");
			out.println("\tmoveto <rname> <rname>");
			out.println("\tloadprogram <rname> <path>");
			out.println("\tsaveprogram <rname> <path>");
			out.println("\tshowprogram <rname>");
			out.println("\texecute <rname> <int>");
			out.println("\texecuteall <int>");
			out.println("\texit");
		} else {
			out.println("unknown command");
		}
	}
	
	private String readCommand(BufferedReader reader) {
		try { 
			out.print(">");
			out.flush();
			return reader.readLine();
		} catch(IOException e) {
			out.println("error reading from standard in");
			System.exit(ERROR);
			return null;
		}
	}
	
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String command = readCommand(reader);
		while(command != null) {
			if(command.equals("exit")) {
				break;
			} else {
				processCommand(command);
				view.repaint();
			}
			command = readCommand(reader);
		}
		setVisible(false);
		dispose();
		out.println("bye");
	}
	
	public static void main(String[] args) {
		// modify the code between <begin> and <end> (substitute the generic arguments with your classes and replace
		// roborally.model.Facade with your facade implementation)
		/* <begin> */
		RoboRally<core.Board, core.Robot, core.Wall, Items.Battery, Items.RepairKit, Items.SurpriseBox> roboRally 
			= new RoboRally<core.Board, core.Robot,core.Wall, Items.Battery, Items.RepairKit, Items.SurpriseBox>(new GUI.Facade());
		/* <end> */
		roboRally.setVisible(true);
		roboRally.run();
	}
}
