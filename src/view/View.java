package view;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import controller.CommandCenter;
//import javafx.scene.layout.Border;
import model.disasters.Collapse;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Infection;
import model.disasters.Injury;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Evacuator;
import model.units.FireTruck;
import model.units.PoliceUnit;
import model.units.Unit;
import simulation.Rescuable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class View extends JFrame {
	private JPanel l1;
	private JPanel l2;
	private JPanel l3;
	private JTextArea currentCycle;
	private JTextArea casualities;
	private JTextArea txtinfo;
	private JTextArea txtlog;
	private JTextArea unitinfo;
	private JPanel availableunits;
	private JPanel Respondingandtreatingunits;
	private JPanel l4;
	private JLabel a;
	private JScrollPane s;
	private JScrollPane s1;
	private JScrollPane s2;
	private JScrollPane s5;
	private JLabel back;
	private ArrayList<String> l;
	private JTextArea recommendd;

	public void updateCurrentCycle(int x) {
		currentCycle.setText(x + " ");
	}

	public void updatecasualities(int x) {
		casualities.setText(x + " ");
	}

	public void updateinfo(String r) {
		txtinfo.setText(r);
	}

	public String updateinfo(ArrayList<Unit> t, int x, int y) {
		String r = "<" + x + "," + y + ">" + "\n";
		for (int i = 0; i < t.size(); i++) {
			if (t.get(i).getLocation().getX() == x && t.get(i).getLocation().getY() == y) {
				r += "Unit" + (i + 1) + ": " + "\n";
				Unit unit = t.get(i);
				String type = "";
				if (unit instanceof Ambulance) {
					type = "Ambulance";
				} else if (unit instanceof FireTruck) {
					type = "FireTruck";
				} else if (unit instanceof DiseaseControlUnit) {
					type = "DiseaseControlUnit";
				} else if (unit instanceof Evacuator) {
					type = "Evacuator";
				} else {
					type = "GasControlUnit";
				}
				r += "ID:" + unit.getUnitID() + "\n" + "Type:" + type + "\n" + "Location: " + unit.getLocation() + "\n"
						+ "Steps per Cycle: " + unit.getStepsPerCycle() + "\n";
				if (unit.getTarget() != null) {
					if (unit.getTarget() instanceof ResidentialBuilding)
						r += "Target: Building at " + unit.getTarget().getLocation() + "\n";
					else {
						r += "Target: Citizen at " + unit.getTarget().getLocation() + "\n";
					}
				}
				r += "Unit's State: " + unit.getState() + "\n";
				if (unit instanceof Evacuator) {
					r += "Number of Passengers: " + ((Evacuator) unit).getPassengers().size() + "\n";
					if (((Evacuator) unit).getPassengers().size() != 0) {
						r += "Passengers: " + "\n";
						for (int j = 0; j < ((Evacuator) unit).getPassengers().size(); j++) {
							Citizen c = ((Evacuator) unit).getPassengers().get(j);
							r += (i + 1) + ") Name: " + c.getName() + ", ID: " + c.getNationalID() + ", Age: "
									+ c.getAge() + "\n";
						}
					}
				}
			}
		}
		return r;
	}

	public String updateinfo(Rescuable t) {
		String r = "";
		if (t instanceof ResidentialBuilding) {
			ResidentialBuilding b = (ResidentialBuilding) t;
			r += "Building: " + "\n" + "Location: " + b.getLocation() + "\n" + "Structural Integrity: "
					+ b.getStructuralIntegrity() + "\n" + "Fire Damage: " + b.getFireDamage() + "\n" + "Gas Level: "
					+ b.getGasLevel() + "\n" + "Foundation Damage: " + b.getFoundationDamage() + "\n"
					+ "Number of occupants: " + b.getOccupants().size() + "\n";
			if (b.getOccupants().size() != 0) {
				r += "Occupants: " + "\n";
				for (int i = 0; i < b.getOccupants().size(); i++) {
					Citizen c = b.getOccupants().get(i);
					r += (i + 1) + ") " + updateinfo(c);
				}
			}
			if (b.getDisaster() != null) {
				if (b.getDisaster() instanceof Collapse)
					r += "Disaster: Collapse" + "\n" + "isActive: " + b.getDisaster().isActive() + "\n"
							+ "Disaster StartCycle: " + b.getDisaster().getStartCycle() + "\n";
				else if (b.getDisaster() instanceof Fire)
					r += "Disaster: Fire" + "\n" + "isActive: " + b.getDisaster().isActive() + "\n"
							+ "Disaster StartCycle: " + b.getDisaster().getStartCycle() + "\n";
				else if (b.getDisaster() instanceof GasLeak)
					r += "Disaster: GasLeak" + "\n" + "isActive: " + b.getDisaster().isActive() + "\n"
							+ "Disaster StartCycle: " + b.getDisaster().getStartCycle() + "\n";
			}
			txtinfo.setText(r);
		} else if (t instanceof Citizen) {
			Citizen c = (Citizen) t;
			r += "Citizen: " + "\n" + "Name: " + c.getName() + "\n" + "ID: " + c.getNationalID() + "\n" + "Age: "
					+ c.getAge() + "\n" + "Location: " + c.getLocation() + "\n" + "Hp: " + c.getHp() + "\n"
					+ "BloodLoss: " + c.getBloodLoss() + "\n" + "Toxicity: " + c.getToxicity() + "\n" + "State: "
					+ c.getState() + "\n";
			if (c.getDisaster() != null) {
				if (c.getDisaster() instanceof Infection)
					r += "Disaster: Infection" + "\n" + "isActive: " + c.getDisaster().isActive() + "\n"
							+ "Disaster StartCycle: " + c.getDisaster().getStartCycle() + "\n";
				else if (c.getDisaster() instanceof Injury)
					r += "Disaster: Injury" + "\n" + "isActive: " + c.getDisaster().isActive() + "\n"
							+ "Disaster StartCycle: " + c.getDisaster().getStartCycle() + "\n";
			}
		}
		return r;
	}

	public void updatelog(ArrayList<Citizen> c, ArrayList<ResidentialBuilding> b, int cc) {
		String d = "";
		String a = "Active Disasters: " + "\n";
		// String d = "Disasters Struck in Current Cycle: " + "\n";
		String dead = "Citizens Dead: " + "\n";
		for (int i = 0; i < c.size(); i++) {
			if (c.get(i).getDisaster().getStartCycle() == cc) {
				if (c.get(i).getDisaster() instanceof Injury) {
					l.add("Citizen: Name: " + c.get(i).getName() + " Disaster: Injury in Cycle " + cc + "\n");
				} else if (c.get(i).getDisaster() instanceof Infection) {
					l.add("Citizen: Name: " + c.get(i).getName() + " Disaster: Infection in Cycle " + cc + "\n");
				}
			}
			if (c.get(i).getDisaster().isActive()) {
				if (c.get(i).getDisaster() instanceof Injury) {
					a += "Citizen: Name: " + c.get(i).getName() + " Disaster: Injury" + "\n";
				} else if (c.get(i).getDisaster() instanceof Infection) {
					a += "Citizen: Name: " + c.get(i).getName() + " Disaster: Infection" + "\n";
				}
			}
			if (c.get(i).getHp() == 0) {
				dead += "Citizen: Name: " + c.get(i).getName() + " Location: " + c.get(i).getLocation() + "\n";
			}
		}
		for (int i = 0; i < b.size(); i++) {
			if (b.get(i).getDisaster().getStartCycle() == cc) {
				if (b.get(i).getDisaster() instanceof Fire) {
					l.add("Building: Location: " + b.get(i).getLocation() + " Disaster: Fire in Cycle " + cc + "\n");
				} else if (b.get(i).getDisaster() instanceof Collapse) {
					l.add("Building: Location: " + b.get(i).getLocation() + " Disaster: Collapse in Cycle " + cc
							+ "\n");
				} else if (b.get(i).getDisaster() instanceof GasLeak) {
					l.add("Building: Location: " + b.get(i).getLocation() + " Disaster: GasLeak in Cycle " + cc + "\n");
				}
			}
			if (b.get(i).getDisaster().isActive()) {
				if (b.get(i).getDisaster() instanceof Fire) {
					a += "Building: Location: " + b.get(i).getLocation() + " Disaster: Fire" + "\n";
				} else if (b.get(i).getDisaster() instanceof Collapse) {
					a += "Building: Location: " + b.get(i).getLocation() + " Disaster: Collapse" + "\n";
				} else if (b.get(i).getDisaster() instanceof GasLeak) {
					a += "Building: Location: " + b.get(i).getLocation() + " Disaster: GasLeak" + "\n";
				}
			}
			if (b.get(i).getStructuralIntegrity() == 0) {
				for (int j = 0; j < b.get(i).getOccupants().size(); j++) {
					dead += "Citizen: Name: " + b.get(i).getOccupants().get(j).getName() + " Location: "
							+ b.get(i).getLocation() + "\n";
				}
			}

		}
		for (int i = 0; i < l.size(); i++)
			d += l.get(i);
		txtlog.setText(d + a + dead);

	}

	public void updateunitinfo(Unit unit) {
		String type = "";
		String r = "";
		if (unit instanceof Ambulance) {
			type = "Ambulance";
		} else if (unit instanceof FireTruck) {
			type = "FireTruck";
		} else if (unit instanceof DiseaseControlUnit) {
			type = "DiseaseControlUnit";
		} else if (unit instanceof Evacuator) {
			type = "Evacuator";
		} else {
			type = "GasControlUnit";
		}
		r = "ID:" + unit.getUnitID() + "\n" + "Type:" + type + "\n" + "Location: " + unit.getLocation() + "\n"
				+ "Steps per Cycle: " + unit.getStepsPerCycle() + "\n";
		if (unit.getTarget() != null) {
			if (unit.getTarget() instanceof ResidentialBuilding)
				r += "Target: Building at " + unit.getTarget().getLocation() + "\n";
			else {
				r += "Target: Citizen at " + unit.getTarget().getLocation() + "\n";
			}
		}
		r += "Unit's State: " + unit.getState() + "\n";
		if (unit instanceof Evacuator) {
			r += "Number of Passengers: " + ((Evacuator) unit).getPassengers().size() + "\n";
			if (((Evacuator) unit).getPassengers().size() != 0) {
				r += "Passengers: " + "\n";
				for (int i = 0; i < ((Evacuator) unit).getPassengers().size(); i++) {
					Citizen c = ((Evacuator) unit).getPassengers().get(i);
					r += (i + 1) + ") " + updateinfo(c);
				}
			}
		}
		unitinfo.setText(r);
	}

	public void updateunits() {

	}

	public View() {

		setTitle("RescueSimulation");
		l = new ArrayList<String>();
		l.add("Disasters Struck: " + "\n");
		setBounds(0, 0, 1200, 750);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		back = new JLabel();
		back.setBounds(310, 10, 600, 600);
		back.setIcon(new ImageIcon(
				new ImageIcon("gridback.jpg").getImage().getScaledInstance(600, 600, Image.SCALE_SMOOTH)));
		getContentPane().add(back);
		// getContentPane().setBackground(Color.lightGray);
		l1 = new JPanel();
		l1.setLayout(null);
		l1.setBounds(0, 0, 280, 750);
		javax.swing.border.Border ss = BorderFactory.createLineBorder(Color.BLACK, 3);
		// ss.setOpaque(false);
		txtinfo = new JTextArea();
		txtinfo.setBounds(0, 0, 280, 300);
		TitledBorder titleinfo = BorderFactory.createTitledBorder(ss, "Location's info:");
		txtinfo.setBorder(titleinfo);
		txtinfo.setEditable(false);
		txtinfo.setLineWrap(true);
		txtinfo.setWrapStyleWord(true);
		s1 = new JScrollPane(txtinfo);
		s1.setBounds(0, 0, 280, 300);
		s1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		l1.add(s1);
		txtlog = new JTextArea();
		txtlog.setBounds(0, 300, 280, 250);
		TitledBorder titlelog = BorderFactory.createTitledBorder(ss, "Log:");
		txtlog.setBorder(titlelog);
		txtlog.setEditable(false);
		txtlog.setLineWrap(true);
		txtlog.setWrapStyleWord(true);
		s = new JScrollPane(txtlog);
		s.setBounds(0, 300, 280, 250);
		s.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		l1.add(s);
		l1.setVisible(true);
		getContentPane().add(l1);
		// back.add(l1);
		l2 = new JPanel();
		l2.setLayout(null);
		l2.setBounds(930, 0, 270, 750);
		unitinfo = new JTextArea();
		unitinfo.setLineWrap(true);
		TitledBorder titleunit = BorderFactory.createTitledBorder(ss, "Unit's info:");
		unitinfo.setBorder(titleunit);
		unitinfo.setWrapStyleWord(true);
		unitinfo.setEditable(false);
		s2 = new JScrollPane(unitinfo);
		s2.setBounds(0, 0, 270, 150);
		s2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		availableunits = new JPanel(new GridLayout(3, 2));
		availableunits.setBounds(0, 150, 270, 255);
		availableunits.setBackground(ViewStart.getPurple2());
		// JTextArea b = new JTextArea("Available Units:");
		TitledBorder titleav = BorderFactory.createTitledBorder(ss, "Available Units:");
		availableunits.setBorder(titleav);
		// b.setEditable(false);
		// availableunits.add(b);
		Respondingandtreatingunits = new JPanel(new GridLayout(3, 2));
		Respondingandtreatingunits.setBounds(0, 405, 270, 255);
		Respondingandtreatingunits.setBackground(ViewStart.getPurple2());
		// JTextArea c = new JTextArea("Responding and Treating Units:");
		TitledBorder titlear = BorderFactory.createTitledBorder(ss, "Responding and Treating Units:");
		Respondingandtreatingunits.setBorder(titlear);
		// c.setEditable(false);
		// c.setLineWrap(true);
		// c.setWrapStyleWord(true);
		// Respondingandtreatingunits.add(c);
		l2.add(s2);
		l2.add(availableunits);
		l2.add(Respondingandtreatingunits);
		getContentPane().add(l2);
		// back.add(l2);
		l3 = new JPanel(new GridLayout(10, 10));
		// l3= new JLabel();
		l3.setBorder(ss);
		l3.setBounds(0, 0, 600, 600);
		// l3.setBackground(Color.RED);
		// l3.setIcon(new ImageIcon(new ImageIcon(
		// "gridvack.jpg")
		// .getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH)));

		// getContentPane().add(l3);
		back.add(l3);
		currentCycle = new JTextArea("0");
		TitledBorder titlecycle = BorderFactory.createTitledBorder(ss, "Current Cycle:");
		currentCycle.setBorder(titlecycle);
		currentCycle.setEditable(false);
		casualities = new JTextArea("0");
		// javax.swing.border.Border s1 = BorderFactory.createLineBorder(Color.BLACK,
		// 3);
		TitledBorder titlecas = BorderFactory.createTitledBorder(ss, "Casualities:");
		casualities.setBorder(titlecas);
		currentCycle.setBounds(620, 630, 95, 60);
		casualities.setBounds(720, 630, 95, 60);
		getContentPane().add(currentCycle);
		getContentPane().add(casualities);
//		back.add(currentCycle);
//		back.add(casualities);
		l4 = new JPanel();
		l4.setLayout(null);
		l4.setBounds(310, 630, 305, 60);

		getContentPane().add(l4);
		// back.add(l4);
		a = new JLabel("Do you want to assign the unit to the location?");
		a.setBounds(4, 0, 350, 40);
		a.setForeground(Color.black);
		// l4.setBounds(0,0,350,40);
		// a.setBorder(ss);
		l4.setBorder(ss);
		l4.add(a);
		currentCycle.setOpaque(false);
		casualities.setOpaque(false);
		// txtlog.setOpaque(false);
		// txtinfo.setOpaque(false);
		// unitinfo.setOpaque(false);
		// s.setOpaque(false);
		// s1.setOpaque(false);
		// s2.setOpaque(false);
		txtlog.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		txtinfo.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		unitinfo.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		casualities.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		currentCycle.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		// Image background =
		// Toolkit.getDefaultToolkit().createImage("backgroundsimple.jpg");
		back.setVisible(true);
		l1.setOpaque(false);
		l2.setOpaque(false);
		l3.setOpaque(false);
		// l4.setVisible(false);
		setBackground(ViewStart.getPurple());
		getContentPane().setBackground(ViewStart.getPurple2());
		txtinfo.setForeground(ViewStart.getPurple());
		txtlog.setForeground(ViewStart.getPurple());
		unitinfo.setForeground(ViewStart.getPurple());
		txtlog.setBackground(ViewStart.getPurple2());
		txtinfo.setBackground(ViewStart.getPurple2());
		unitinfo.setBackground(ViewStart.getPurple2());
		l4.setBackground(ViewStart.getPurple2());
		// s.setBackground(ViewStart.getPurple2());
		// s1.setBackground(ViewStart.getPurple2());
		// s2.setBackground(ViewStart.getPurple2());

		Image cursor = Toolkit.getDefaultToolkit().getImage("source.gif");
		Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(cursor.getScaledInstance(45, 45, Image.SCALE_SMOOTH),
				new Point(this.getX(), this.getY()), "cursor");
		this.setCursor(c);
		recommendd = new JTextArea();
		recommendd.setBounds(0, 0, 280, 300);
		TitledBorder recommenddinfo = BorderFactory.createTitledBorder(ss, "Recommended:");
		recommendd.setBorder(recommenddinfo);
		recommendd.setEditable(false);
		recommendd.setLineWrap(true);
		recommendd.setWrapStyleWord(true);
		s5 = new JScrollPane(recommendd);
		s5.setBounds(0, 550, 280, 150);
		s5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		l1.add(s5);
		recommendd.setBackground(ViewStart.getPurple2());
		recommendd.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		recommendd.setForeground(ViewStart.getPurple());

	}

	public JTextArea getRecommendd() {
		return recommendd;
	}

	public void setRecommendd(JTextArea recommendd) {
		this.recommendd = recommendd;
	}

	public JPanel getL4() {
		return l4;
	}

	public JPanel getL3() {
		return l3;
	}

	public JPanel getAvailableunits() {
		return availableunits;
	}

	public JPanel getRespondingandtreatingunits() {
		return Respondingandtreatingunits;
	}

	public static void main(String[] args) {
		View myGUI = new View();
		myGUI.setVisible(true);
//		try {
//			myGUI.PlaySound("audio.wav");
//		} catch (UnsupportedAudioFileException e1) {
//			System.out.println("h");
//		} catch (IOException e1) {
//			System.out.println("hh");
//		} catch (LineUnavailableException e1) {
//			System.out.println("hhh");
//		}
//		

	}

	public Clip PlaySound(String dir) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(dir).getAbsoluteFile());
		Clip clip = AudioSystem.getClip();
		clip.open(audioInputStream);
		return clip;
	}
}
