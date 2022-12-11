package controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;


import exceptions.CannotTreatException;
import exceptions.IncompatibleTargetException;
import model.disasters.Collapse;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Infection;
import model.disasters.Injury;
import model.events.SOSListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Evacuator;
import model.units.FireTruck;
import model.units.GasControlUnit;
import model.units.Unit;
import model.units.UnitState;
import simulation.Rescuable;
import simulation.Simulator;
import view.View;
import view.ViewEnd;
import view.ViewStart;

public class CommandCenter implements SOSListener, ActionListener, MouseListener {

	private Simulator engine;
	private ArrayList<ResidentialBuilding> visibleBuildings;
	private ArrayList<Citizen> visibleCitizens;
	private View view;
	private ViewStart start;
	private ViewEnd end;
	private JButton startbtn;
	private JButton nextCycle;
	private JLabel[][] grid;
	private int cc;
	private ArrayList<JButton> btnsunits;
	private ArrayList<JButton> btnsunitsr;
	private JButton yes;
	private JButton no;
	private int x;
	private int y;
	private Unit unit = null;
	private JPanel nextCyc;
	@SuppressWarnings("unused")
	private ArrayList<Unit> emergencyUnits;
	private JButton recommend;
	private JButton skipcycles;
	
	public int countScore(Unit u, Rescuable r) {
		if (u instanceof Ambulance) {
			Citizen c = (Citizen) r;
			int deltax = Math.abs(r.getLocation().getX() - u.getLocation().getX());
			int deltay = Math.abs(r.getLocation().getY() - u.getLocation().getY());
			int cycles = (int) Math.ceil((deltax + deltay) / u.getStepsPerCycle());
			int blood = c.getBloodLoss();
			int hh = c.getHp();

			for (int l = 0; l < cycles; l++) {
				blood += 10;
				if (blood > 0 && blood < 30)
					hh -= 5;
				else if (blood >= 30 && blood < 70)
					hh -= 10;
				else if (blood >= 70)
					hh -= 15;
				else if (blood >= 100) {
					blood=100;
					hh = 0;}
			}
			while(blood>0) {
				blood-=10;
				if (blood > 0 && blood < 30)
					hh -= 5;
				else if (blood >= 30 && blood < 70)
					hh -= 10;
				else if (blood >= 70)
					hh -= 15;
				else if (blood >= 100)
					hh = 0;
			}
			if (hh <= 0)
				return -1;
			else
				return 100 - hh;

		} else if (u instanceof DiseaseControlUnit) {
			Citizen c = (Citizen) r;
			int deltax = Math.abs(r.getLocation().getX() - u.getLocation().getX());
			int deltay = Math.abs(r.getLocation().getY() - u.getLocation().getY());
			int cycles = (int) Math.ceil((deltax + deltay) / u.getStepsPerCycle());
			int toxic = c.getToxicity();
			int hh = c.getHp();
			for (int l = 0; l < cycles; l++) {
				toxic += 15;
				if (toxic > 0 && toxic < 30)
					hh -= 5;
				else if (toxic >= 30 && toxic < 70)
					hh -= 10;
				else if (toxic >= 70)
					hh -= 15;
				else if (toxic >= 100) {
					toxic =100;
					hh = 0;}
				
			}while(toxic>0) {
				toxic-=10;
				if (toxic > 0 && toxic < 30)
					hh -= 5;
				else if (toxic >= 30 && toxic < 70)
					hh -= 10;
				else if (toxic >= 70)
					hh -= 15;
				else if (toxic >= 100)
					hh = 0;
			}
			if (hh <= 0)
				return -1;
			else
				return 100 - hh;
		} else if (u instanceof FireTruck) {
			ResidentialBuilding c = (ResidentialBuilding) r;
			int deltax = Math.abs(r.getLocation().getX() - u.getLocation().getX());
			int deltay = Math.abs(r.getLocation().getY() - u.getLocation().getY());
			int cycles = (int) Math.ceil((deltax + deltay) / u.getStepsPerCycle());
			int firescore = c.getFireDamage();
			int score = c.getStructuralIntegrity();
			for (int l = 0; l < cycles; l++) {
				firescore += 10;
				if (firescore > 0 && firescore < 30)
					score -= 3;

				else if (firescore >= 30 && firescore < 70)
					score -= 5;

				else if (firescore >= 70)
					score -= 7;
				else if (firescore >= 100) {
					firescore=100;
					score=0;
				}
			}
			while(firescore>0) {
				firescore-=10;
				if (firescore > 0 && firescore < 30)
					score -= 3;

				else if (firescore >= 30 && firescore < 70)
					score -= 5;

				else if (firescore >= 70)
					score -= 7;
				else if (firescore >= 100) {
					firescore=100;
					score=0;
				}
			}
			if (score > 0) {
				score = 100 - score;
				return score + (20 * c.getOccupants().size());
			}

		} else if (u instanceof GasControlUnit) {
			ResidentialBuilding c = (ResidentialBuilding) r;
			int deltax = Math.abs(r.getLocation().getX() - u.getLocation().getX());
			int deltay = Math.abs(r.getLocation().getY() - u.getLocation().getY());
			int cycles = (int) Math.ceil((deltax + deltay) / u.getStepsPerCycle());
			int score = c.getGasLevel() + (15 * cycles);
			if (score > 100) {
				score = 100;
			}
			return score + (10 * c.getOccupants().size());

		} else if (u instanceof Evacuator) {
			ResidentialBuilding c = (ResidentialBuilding) r;
			int deltax = Math.abs(r.getLocation().getX() - u.getLocation().getX());
			int deltay = Math.abs(r.getLocation().getY() - u.getLocation().getY());
			int cycles = (int) Math.ceil((deltax + deltay) / u.getStepsPerCycle());
			int score = c.getStructuralIntegrity() - (5 * cycles);
			if (score > 0) {
				score = 100 - score;
				return score * c.getOccupants().size();
			} else
				return -1;
		}
		
			return -1;
	}

	public String recommendhere() {
		String r = "We recommend you assign the following units: " + "\n";
		int maxScore = 0;
		Citizen res = null;
		ResidentialBuilding ress = null;
		ArrayList done = new ArrayList<Rescuable>();

		///////////////////
		for (int i = 0; i < emergencyUnits.size(); i++) {
			if (emergencyUnits.get(i).getState() == UnitState.IDLE) {
				if (emergencyUnits.get(i) instanceof Ambulance) {
					for (int j = 0; j < visibleCitizens.size(); j++) {
						Citizen c = visibleCitizens.get(j);
						if (c.getDisaster() instanceof Injury && c.getState() == CitizenState.IN_TROUBLE
								&& c.getDisaster().isActive()) {
							int x = countScore(emergencyUnits.get(i), c);
							if (x > maxScore) {
								boolean flag = false;
								for (int k = 0; k < done.size(); k++) {
									if (done.get(k).equals(c)) {
										flag = true;

									}
								}
								if (flag == false) {
									for (int h = 0; h < emergencyUnits.size(); h++) {
										if (emergencyUnits.get(h).getTarget() != null
												&& emergencyUnits.get(h).getTarget().equals(c)) {
											flag = true;

										}
									}
									if (flag == false) {
										maxScore = x;
										res = c;
									}
								}
							}
						}
					}
					if (res != null && (emergencyUnits.get(i).getTarget() == null
							|| !emergencyUnits.get(i).getTarget().equals(res))) {
						done.add(res);
						r += "Ambulance at Location <" + emergencyUnits.get(i).getLocation().getX() + ","
								+ emergencyUnits.get(i).getLocation().getY() + ">" + " to  Citizen at Location <"
								+ res.getLocation().getX() + "," + res.getLocation().getY() + ">" + "\n";
					}
					res = null;
					maxScore = 0;
				}

				if (emergencyUnits.get(i) instanceof DiseaseControlUnit) {
					for (int j = 0; j < visibleCitizens.size(); j++) {
						Citizen c = visibleCitizens.get(j);
						if (c.getDisaster() instanceof Infection && c.getState() == CitizenState.IN_TROUBLE
								&& c.getDisaster().isActive()) {
							int x = countScore(emergencyUnits.get(i), c);
							if (x > maxScore) {
								boolean flag = false;
								for (int k = 0; k < done.size(); k++) {
									if (done.get(k).equals(c)) {
										flag = true;

									}
								}

								if (flag == false) {
									for (int h = 0; h < emergencyUnits.size(); h++) {
										if (emergencyUnits.get(h).getTarget() != null
												&& emergencyUnits.get(h).getTarget().equals(c)) {
											flag = true;
										}
									}
									if (flag == false) {
										maxScore = x;
										res = c;
									}
								}

							}
						}
					}
					if (res != null && (emergencyUnits.get(i).getTarget() == null
							|| !emergencyUnits.get(i).getTarget().equals(res))) {
						done.add(res);
						r += "Disease Conrol Unit at Location <" + emergencyUnits.get(i).getLocation().getX() + ","
								+ emergencyUnits.get(i).getLocation().getY() + ">" + " to  Citizen at Location <"
								+ res.getLocation().getX() + "," + res.getLocation().getY() + ">" + "\n";
					}
					res = null;
					maxScore = 0;
				}
				if (emergencyUnits.get(i) instanceof FireTruck) {
					for (int j = 0; j < visibleBuildings.size(); j++) {
						ResidentialBuilding c = visibleBuildings.get(j);
						if (c.getDisaster() instanceof Fire && c.getStructuralIntegrity() != 0
								&& c.getDisaster().isActive()) {
							int x = countScore(emergencyUnits.get(i), c);
							if (x > maxScore) {
								boolean flag = false;
								for (int k = 0; k < done.size(); k++) {
									if (done.get(k).equals(c)) {
										flag = true;
									}
								}
								if (flag == false) {
									for (int h = 0; h < emergencyUnits.size(); h++) {
										if (emergencyUnits.get(h).getTarget() != null
												&& emergencyUnits.get(h).getTarget().equals(c)) {
											flag = true;
										}
									}
									if (flag == false) {
										maxScore = x;
										ress = c;
									}
								}
							}
						}
					}
					if (ress != null && (emergencyUnits.get(i).getTarget() == null
							|| !emergencyUnits.get(i).getTarget().equals(ress))) {
						done.add(ress);
						r += "Fire Truck at Location <" + emergencyUnits.get(i).getLocation().getX() + ","
								+ emergencyUnits.get(i).getLocation().getY() + ">" + " to  Building at Location <"
								+ ress.getLocation().getX() + "," + ress.getLocation().getY() + ">" + "\n";
					}
					ress = null;
					maxScore = 0;
				}
				if (emergencyUnits.get(i) instanceof GasControlUnit) {
					for (int j = 0; j < visibleBuildings.size(); j++) {
						ResidentialBuilding c = visibleBuildings.get(j);
						if (c.getDisaster() instanceof GasLeak && c.getStructuralIntegrity() != 0
								&& c.getDisaster().isActive()) {
							int x = countScore(emergencyUnits.get(i), c);
							if (x > maxScore) {
								boolean flag = false;
								for (int k = 0; k < done.size(); k++) {
									if (done.get(k).equals(c)) {
										flag = true;
									}
								}
								if (flag == false) {
									for (int h = 0; h < emergencyUnits.size(); h++) {
										if (emergencyUnits.get(h).getTarget() != null
												&& emergencyUnits.get(h).getTarget().equals(c)) {
											flag = true;
										}
									}
									if (flag == false) {
										maxScore = x;
										ress = c;
									}
								}
							}
						}
					}
					if (ress != null && (emergencyUnits.get(i).getTarget() == null
							|| !emergencyUnits.get(i).getTarget().equals(ress))) {
						done.add(ress);
						r += "Gas Control Unit at Location <" + emergencyUnits.get(i).getLocation().getX() + ","
								+ emergencyUnits.get(i).getLocation().getY() + ">" + " to  Building at Location <"
								+ ress.getLocation().getX() + "," + ress.getLocation().getY() + ">" + "\n";
					}
					ress = null;
					maxScore = 0;
				}
				if (emergencyUnits.get(i) instanceof Evacuator) {
					for (int j = 0; j < visibleBuildings.size(); j++) {
						ResidentialBuilding c = visibleBuildings.get(j);
						if (c.getDisaster() instanceof Collapse && c.getOccupants().size() > 0
								&& c.getStructuralIntegrity() != 0 && c.getDisaster().isActive()) {
							int x = countScore(emergencyUnits.get(i), c);
							if (x > maxScore) {
								boolean flag = false;
								for (int k = 0; k < done.size(); k++) {
									if (done.get(k).equals(c)) {
										flag = true;
									}
								}
								if (flag == false) {
									for (int h = 0; h < emergencyUnits.size(); h++) {
										if (emergencyUnits.get(h).getTarget() != null
												&& emergencyUnits.get(h).getTarget().equals(c)) {
											flag = true;
										}
									}
									if (flag == false) {
										maxScore = x;
										ress = c;
									}
								}
							}
						}
					}
					if (ress != null && (emergencyUnits.get(i).getTarget() == null
							|| !emergencyUnits.get(i).getTarget().equals(ress))) {
						done.add(ress);
						r += "Evacuator at Location <" + emergencyUnits.get(i).getLocation().getX() + ","
								+ emergencyUnits.get(i).getLocation().getY() + ">" + " to  Building at Location <"
								+ ress.getLocation().getX() + "," + ress.getLocation().getY() + ">" + "\n";
					}
					ress = null;
					maxScore = 0;
				}
			}
		}
		return r;
	}

	public CommandCenter() throws Exception {
		skipcycles = new JButton ("Skip 5 Cycles");
		engine = new Simulator(this);
		visibleBuildings = new ArrayList<ResidentialBuilding>();
		visibleCitizens = new ArrayList<Citizen>();
		emergencyUnits = engine.getEmergencyUnits();
		view = new View();
		start = new ViewStart();
		end = new ViewEnd();
		btnsunits = new ArrayList<JButton>();
		btnsunitsr = new ArrayList<JButton>();
		grid = new JLabel[10][10];
		yes = new JButton("YES");
		no = new JButton("NO");
		yes.setBounds(185, 28, 60, 30);
		no.setBounds(240, 28, 60, 30);
		yes.addActionListener(this);
		no.addActionListener(this);
		recommend = new JButton("Help Me");
	//	recommend.setBounds(930, 665, 100, 40);
		recommend.setBounds(820, 630, 90, 60);
		recommend.addActionListener(this);
		skipcycles.setBounds(930, 665, 120, 40);
		view.getL4().add(yes);
		view.getL4().add(no);
		view.getContentPane().add(recommend);
		view.getL4().setVisible(false);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (i == 0 && j == 0) {
					grid[i][j] = new JLabel("  Base");
					grid[i][j].setFont(new Font("Comic Sans MS", Font.BOLD, 15));
					grid[i][j].setForeground(Color.white);
				} else {
					grid[i][j] = new JLabel();
				}
				Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
				grid[i][j].setBorder(border);
				grid[i][j].setVisible(true);
				grid[i][j].addMouseListener((MouseListener) this);
				view.getL3().add(grid[i][j]);
			}
		}
		for (final Unit unit : emergencyUnits) {
			JButton btnunit = new JButton();
			if (unit instanceof FireTruck)
				btnunit.setIcon(new ImageIcon(new ImageIcon("fire-truck-vector-618261.jpg").getImage()
						.getScaledInstance(140, 70, Image.SCALE_SMOOTH)));
			else if (unit instanceof Ambulance)
				btnunit.setIcon(new ImageIcon(new ImageIcon("ambulance-cartoon-vector-407482.jpg").getImage()
						.getScaledInstance(140, 70, Image.SCALE_SMOOTH)));
			else if (unit instanceof Evacuator)
				btnunit.setIcon(new ImageIcon(new ImageIcon("wall-murals-cartoon-police-van.jpg").getImage()
						.getScaledInstance(140, 70, Image.SCALE_SMOOTH)));
			else if (unit instanceof DiseaseControlUnit)
				btnunit.setIcon(new ImageIcon(new ImageIcon("car-pollution-DMT4XN.jpg").getImage()
						.getScaledInstance(140, 70, Image.SCALE_SMOOTH)));
			else
				btnunit.setIcon(new ImageIcon(
						new ImageIcon("untitled.png").getImage().getScaledInstance(140, 70, Image.SCALE_SMOOTH)));
			btnunit.validate();
			btnunit.addActionListener(this);
			btnsunits.add(btnunit);
			view.getAvailableunits().add(btnunit);
		}
		for (final Unit unit : emergencyUnits) {
			JButton btnunit = new JButton();
			if (unit instanceof FireTruck)
				btnunit.setIcon(new ImageIcon(new ImageIcon("fire-truck-vector-618261.jpg").getImage()
						.getScaledInstance(140, 70, Image.SCALE_SMOOTH)));
			else if (unit instanceof Ambulance)
				btnunit.setIcon(new ImageIcon(new ImageIcon("ambulance-cartoon-vector-407482.jpg").getImage()
						.getScaledInstance(140, 70, Image.SCALE_SMOOTH)));
			else if (unit instanceof Evacuator)
				btnunit.setIcon(new ImageIcon(new ImageIcon("wall-murals-cartoon-police-van.jpg").getImage()
						.getScaledInstance(140, 70, Image.SCALE_SMOOTH)));
			else if (unit instanceof DiseaseControlUnit)
				btnunit.setIcon(new ImageIcon(new ImageIcon("car-pollution-DMT4XN.jpg").getImage()
						.getScaledInstance(140, 70, Image.SCALE_SMOOTH)));
			else
				btnunit.setIcon(new ImageIcon(
						new ImageIcon("untitled.png").getImage().getScaledInstance(140, 70, Image.SCALE_SMOOTH)));
			btnunit.validate();
			btnunit.addActionListener(this);
			btnsunitsr.add(btnunit);
			btnunit.setVisible(false);
			view.getRespondingandtreatingunits().add(btnunit);
		}
		// nextCyc = new JPanel();
		// nextCyc.setBounds(1030, 665, 100, 40);
		// nextCyc.setLayout(null);
		nextCycle = new JButton("Next Cycle");
		nextCycle.setBounds(1055, 665, 110, 40);
		skipcycles.addActionListener(this);
		nextCycle.addActionListener(this);
		startbtn = new JButton("START GAME");
		startbtn.addActionListener(this);
		startbtn.setBounds(400, 360, 360, 70);
		// b1.addActionListener(this);
		startbtn.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
		startbtn.setForeground(ViewStart.getPurple());
		// startbtn.setBounds(190, 160, 100, 60);
		start.getM().add(startbtn);
		start.setVisible(true);
		start.repaint();
		start.setVisible(true);
		nextCycle.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		skipcycles.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		javax.swing.border.Border ss = BorderFactory.createLineBorder(Color.BLACK, 3);
		// nextCyc.setBorder(ss);
		// nextCyc.add(nextCycle);
		skipcycles.setForeground(ViewStart.getPurple());
		nextCycle.setForeground(ViewStart.getPurple());
		nextCycle.setForeground(ViewStart.getPurple());
		view.getContentPane().add(skipcycles);
		view.getContentPane().add(nextCycle);
		yes.setForeground(ViewStart.getPurple());
		no.setForeground(ViewStart.getPurple());
		recommend.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		recommend.setForeground(ViewStart.getPurple());

	}

	@Override
	public void receiveSOSCall(Rescuable r) {

		if (r instanceof ResidentialBuilding) {

			if (!visibleBuildings.contains(r))
				visibleBuildings.add((ResidentialBuilding) r);

		} else {

			if (!visibleCitizens.contains(r))
				visibleCitizens.add((Citizen) r);
		}

	}

	public void updategrid() {
		boolean[][] a = new boolean[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				a[i][j] = false;
			}
		}
		boolean[][] b = new boolean[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				b[i][j] = false;
			}
		}
		boolean[][] s = new boolean[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				s[i][j] = true;
			}
		}
		boolean[][] d = new boolean[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				d[i][j] = false;
			}
		}
		for (int i = 0; i < visibleBuildings.size(); i++) {
			int x = visibleBuildings.get(i).getLocation().getX();
			int y = visibleBuildings.get(i).getLocation().getY();
			if (!visibleBuildings.get(i).getDisaster().isActive() && visibleBuildings.get(i).getGasLevel() == 0
					&& visibleBuildings.get(i).getFireDamage() == 0
					&& visibleBuildings.get(i).getStructuralIntegrity() > 0
					&& visibleBuildings.get(i).getFoundationDamage() < 100
					&& visibleBuildings.get(i).getOccupants().size() == 0) {
				grid[x][y].setIcon(new ImageIcon(
						new ImageIcon("normalnocitizen.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			} else if (!visibleBuildings.get(i).getDisaster().isActive() && visibleBuildings.get(i).getGasLevel() == 0
					&& visibleBuildings.get(i).getFireDamage() == 0
					&& visibleBuildings.get(i).getStructuralIntegrity() > 0
					&& visibleBuildings.get(i).getFoundationDamage() < 100
					&& visibleBuildings.get(i).getOccupants().size() > 0) {
				grid[x][y].setIcon(new ImageIcon(
						new ImageIcon("normalcitizen.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			}

			else if (visibleBuildings.get(i).getStructuralIntegrity() <= 0
					&& visibleBuildings.get(i).getOccupants().size() == 0) {
				grid[x][y].setIcon(new ImageIcon(new ImageIcon("demolishednocitizen.png").getImage()
						.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			} else if (visibleBuildings.get(i).getStructuralIntegrity() <= 0
					&& visibleBuildings.get(i).getOccupants().size() != 0) {
				grid[x][y].setIcon(new ImageIcon(new ImageIcon("demolishedcitizen.png").getImage().getScaledInstance(60,
						60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			}

			else if (visibleBuildings.get(i).getDisaster() instanceof Fire
					&& visibleBuildings.get(i).getOccupants().size() == 0) {
				grid[x][y].setIcon(new ImageIcon(
						new ImageIcon("firenocitizen.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			} else if (visibleBuildings.get(i).getDisaster() instanceof Fire
					&& visibleBuildings.get(i).getOccupants().size() != 0) {
				grid[x][y].setIcon(new ImageIcon(
						new ImageIcon("firecitizen.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			} else if (visibleBuildings.get(i).getDisaster() instanceof GasLeak
					&& visibleBuildings.get(i).getOccupants().size() == 0) {
				grid[x][y].setIcon(new ImageIcon(
						new ImageIcon("gasnocitizens.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			} else if (visibleBuildings.get(i).getDisaster() instanceof GasLeak
					&& visibleBuildings.get(i).getOccupants().size() != 0) {
				grid[x][y].setIcon(new ImageIcon(
						new ImageIcon("gascitizens.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			} else if (visibleBuildings.get(i).getDisaster() instanceof Collapse
					&& visibleBuildings.get(i).getOccupants().size() == 0) {
				grid[x][y].setIcon(new ImageIcon(new ImageIcon("collapsenocitizen.png").getImage().getScaledInstance(60,
						60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			} else if (visibleBuildings.get(i).getDisaster() instanceof Collapse
					&& visibleBuildings.get(i).getOccupants().size() != 0) {
				grid[x][y].setIcon(new ImageIcon(
						new ImageIcon("collapsecitizen.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			} else if (visibleBuildings.get(i).getDisaster() == null
					&& visibleBuildings.get(i).getOccupants().size() == 0) {
				grid[x][y].setIcon(new ImageIcon(new ImageIcon("normalnoocitizen.png").getImage().getScaledInstance(60,
						60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			} else if (visibleBuildings.get(i).getDisaster() == null
					&& visibleBuildings.get(i).getOccupants().size() != 0) {
				grid[x][y].setIcon(new ImageIcon(
						new ImageIcon("normalcitizen.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				a[x][y] = true;
			}
		}
		for (int i = 0; i < visibleCitizens.size(); i++) {
			int x = visibleCitizens.get(i).getLocation().getX();
			int y = visibleCitizens.get(i).getLocation().getY();

			if (!a[x][y] && !b[x][y]) {
				b[x][y] = true;
				if (!visibleCitizens.get(i).getDisaster().isActive() && visibleCitizens.get(i).getBloodLoss() == 0
						&& visibleCitizens.get(i).getToxicity() == 0) {
					grid[x][y].setIcon(new ImageIcon(
							new ImageIcon("normal.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
					s[x][y] = false;
				}

				else if (visibleCitizens.get(i).getState().equals(CitizenState.DECEASED)) {
					grid[x][y].setIcon(new ImageIcon(new ImageIcon("tombstone-159792_960_720.png").getImage()
							.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
					d[x][y] = true;

				} else if (visibleCitizens.get(i).getDisaster() instanceof Injury) {
					grid[x][y].setIcon(new ImageIcon(
							new ImageIcon("wawa.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				} else if (visibleCitizens.get(i).getDisaster() instanceof Infection) {
					grid[x][y].setIcon(new ImageIcon(
							new ImageIcon("sick.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				} else if (visibleCitizens.get(i).getDisaster() == null) {
					grid[x][y].setIcon(new ImageIcon(
							new ImageIcon("normal.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
				}

			} else if (!a[x][y] && b[x][y]) {
				if (!s[x][y]) {
					if (!visibleCitizens.get(i).getDisaster().isActive() && visibleCitizens.get(i).getBloodLoss() == 0
							&& visibleCitizens.get(i).getToxicity() == 0) {
						grid[x][y].setIcon(new ImageIcon(
								new ImageIcon("keteer.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
					} else {
						grid[x][y].setIcon(new ImageIcon(new ImageIcon("keteerRedpng.png").getImage()
								.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
					}
				} else if (d[x][y]) {
					if (visibleCitizens.get(i).getState().equals(CitizenState.DECEASED)) {
						grid[x][y].setIcon(new ImageIcon(new ImageIcon("tombstone-159792_960_720.png").getImage()
								.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
						d[x][y] = true;
					} else {
						grid[x][y].setIcon(new ImageIcon(new ImageIcon("keteerRedpng.png").getImage()
								.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
						d[x][y] = false;
					}

				} else {
					grid[x][y].setIcon(new ImageIcon(new ImageIcon("keteerRedpng.png").getImage().getScaledInstance(60,
							60, Image.SCALE_SMOOTH)));

				}

			}

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		if (btn == recommend) {
			view.getRecommendd().setText(recommendhere());
		}

		else if (btn == startbtn) {
			if (start.getF1().getText().equals("")) {
				JOptionPane.showMessageDialog(view, "Please Enter Your Name", "No Name", JOptionPane.ERROR_MESSAGE);
			} else {
				start.setVisible(false);
				view.setVisible(true);
				cc = 0;
				// music();
//				try {
//					PlaySound("audio.wav");
//				} catch (UnsupportedAudioFileException e2) {
//					System.out.println("h");
//				} catch (IOException e1) {
//					System.out.println("hh");
//				} catch (LineUnavailableException e4) {
//					System.out.println("hhh");
//				}
			}
		} else if (btn == nextCycle) {

			if (engine.checkGameOver()) {
				view.setVisible(false);
				end.updatecasualities(engine.calculateCasualties());
				end.setVisible(true);
			} else {
				engine.nextCycle();
				for (int i = 0; i < emergencyUnits.size(); i++) {
					if (emergencyUnits.get(i).getState().equals(UnitState.IDLE)) {
						btnsunits.get(i).setVisible(true);
						btnsunitsr.get(i).setVisible(false);
					}
				}
				updategrid();
				view.updateCurrentCycle(++cc);
				view.updatecasualities(engine.calculateCasualties());
				view.updatelog(visibleCitizens, visibleBuildings, cc);
			}
			
		} else if (btn == skipcycles) {
			for (int o=0; o<5; o++) {
			if (engine.checkGameOver()) {
				view.setVisible(false);
				end.updatecasualities(engine.calculateCasualties());
				end.setVisible(true);
			} else {
				engine.nextCycle();
				for (int i = 0; i < emergencyUnits.size(); i++) {
					if (emergencyUnits.get(i).getState().equals(UnitState.IDLE)) {
						btnsunits.get(i).setVisible(true);
						btnsunitsr.get(i).setVisible(false);
					}
				}
				updategrid();
				view.updateCurrentCycle(++cc);
				view.updatecasualities(engine.calculateCasualties());
				view.updatelog(visibleCitizens, visibleBuildings, cc);
			}
			
		}} else if (btnsunits.contains(btn) || btnsunitsr.contains(btn)) {
			if (btnsunits.contains(btn)) {
				int Index = btnsunits.indexOf(btn);
				unit = emergencyUnits.get(Index);
				view.updateunitinfo(unit);
			} else {
				int Index = btnsunitsr.indexOf(btn);
				unit = emergencyUnits.get(Index);
				view.updateunitinfo(unit);
			}
			if (x != 0 && y != 0) {
				for (int i = 0; i < visibleBuildings.size(); i++) {
					if (x == visibleBuildings.get(i).getLocation().getX()
							&& y == visibleBuildings.get(i).getLocation().getY()) {
						view.getL4().setVisible(true);
						return;
					}
				}
				for (int i = 0; i < visibleCitizens.size(); i++) {
					if (x == visibleCitizens.get(i).getLocation().getX()
							&& y == visibleCitizens.get(i).getLocation().getY()) {
						view.getL4().setVisible(true);
						return;
					}
				}

			}

		} else if (btn == yes) {
			for (int i = 0; i < visibleBuildings.size(); i++) {
				if (x == visibleBuildings.get(i).getLocation().getX()
						&& y == visibleBuildings.get(i).getLocation().getY()) {
					try {
						unit.respond(visibleBuildings.get(i));
						int index = emergencyUnits.indexOf(unit);
						btnsunits.get(index).setVisible(false);
						btnsunitsr.get(index).setVisible(true);

					} catch (IncompatibleTargetException e1) {
						JOptionPane.showMessageDialog(view, "Incompatible Target", "Can't Respond",
								JOptionPane.ERROR_MESSAGE);
					} catch (CannotTreatException e1) {
						JOptionPane.showMessageDialog(view, "Cannot Treat Target", "Can't Respond",
								JOptionPane.ERROR_MESSAGE);
					}
					view.getL4().setVisible(false);
					return;
				}
			}
			ArrayList<Citizen> c = new ArrayList<Citizen>();
			for (int i = 0; i < visibleCitizens.size(); i++) {
				if (x == visibleCitizens.get(i).getLocation().getX()
						&& y == visibleCitizens.get(i).getLocation().getY()) {
					c.add(visibleCitizens.get(i));
				}

			}
			if (c.size() != 0) {
				if (c.size() == 1) {
					try {
						unit.respond(c.get(0));
						int index = emergencyUnits.indexOf(unit);
						btnsunits.get(index).setVisible(false);
						btnsunitsr.get(index).setVisible(true);
					} catch (IncompatibleTargetException e1) {
						JOptionPane.showMessageDialog(view, "Incompatible Target", "Can't Respond",
								JOptionPane.ERROR_MESSAGE);
					} catch (CannotTreatException e1) {
						JOptionPane.showMessageDialog(view, "Cannot Treat Target", "Can't Respond",
								JOptionPane.ERROR_MESSAGE);
					}
					view.getL4().setVisible(false);
					return;
				} else {
					Object iconArray[] = new Object[c.size()];
					for (int i = 0; i < c.size(); i++) {
						if (c.get(i).getDisaster() instanceof Injury)
							iconArray[i] = new ImageIcon("wawa.png");
						if (c.get(i).getDisaster() instanceof Infection)
							iconArray[i] = new ImageIcon("sick.png");

					}
					int d = JOptionPane.showOptionDialog(view, "Choose a Citizen", "Select an Option",
							JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, iconArray, iconArray[0]);
					try {
						unit.respond(c.get(d));
						int index = emergencyUnits.indexOf(unit);
						btnsunits.get(index).setVisible(false);
						btnsunitsr.get(index).setVisible(true);
					} catch (IncompatibleTargetException e1) {
						JOptionPane.showMessageDialog(view, "Incompatible Target", "Can't Respond",
								JOptionPane.ERROR_MESSAGE);
					} catch (CannotTreatException e1) {
						JOptionPane.showMessageDialog(view, "Cannot Treat Target", "Can't Respond",
								JOptionPane.ERROR_MESSAGE);
					}
					view.getL4().setVisible(false);
					return;
				}
			}

		} else if (btn == no) {
			view.getL4().setVisible(false);
		}

	}

	public static boolean contains(JLabel[][] grid2, JLabel e) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (grid2[i][j] == e)
					return true;
			}
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		CommandCenter c = new CommandCenter();
//		try {
//			PlaySound("audio.wav");
//		} catch (UnsupportedAudioFileException e1) {
//			System.out.println("h");
//		} catch (IOException e1) {
//			System.out.println("hh");
//		} catch (LineUnavailableException e1) {
//			System.out.println("hhh");
//		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		JLabel btn = (JLabel) e.getSource();
		if (contains(grid, btn)) {
			String r = "";
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					if (grid[i][j] == btn) {
						x = i;
						y = j;
					}
				}
			}
			boolean flag = false;
			for (int i = 0; i < visibleBuildings.size(); i++) {
				if (x == visibleBuildings.get(i).getLocation().getX()
						&& y == visibleBuildings.get(i).getLocation().getY()) {
					flag = true;
				}
			}
			for (int i = 0; i < visibleCitizens.size(); i++) {
				if (x == visibleCitizens.get(i).getLocation().getX()
						&& y == visibleCitizens.get(i).getLocation().getY()) {
					flag = true;
				}
			}
			if (!flag)
				view.getL4().setVisible(false);

			r += view.updateinfo(emergencyUnits, x, y);
			view.updateinfo(r);

			for (int i = 0; i < visibleBuildings.size(); i++) {
				if (x == visibleBuildings.get(i).getLocation().getX()
						&& y == visibleBuildings.get(i).getLocation().getY()) {
					r += view.updateinfo(visibleBuildings.get(i));
					view.updateinfo(r);
					return;
				}
			}
			for (int i = 0; i < visibleCitizens.size(); i++) {
				if (x == visibleCitizens.get(i).getLocation().getX()
						&& y == visibleCitizens.get(i).getLocation().getY()) {
					r += view.updateinfo(visibleCitizens.get(i));
				}
			}
			view.updateinfo(r);
		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

//	public static void music() {
//		AudioPlayer MGP = AudioPlayer.player;
//		AudioStream BGM;
//		AudioData MD;
//		ContinuousAudioDataStream loop = null;
//		
//		try {
//			BGM = new AudioStream(new FileInputStream("audio.mp3"));
//			MD = BGM.getData();
//			loop = new ContinuousAudioDataStream(MD);
//			
//		}
//		catch (IOException error) {
//			System.out.println("hi");
//		}
//		MGP.start(loop);
//	}
//	
	public static Clip PlaySound(String dir)
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(dir).getAbsoluteFile());
		Clip clip = AudioSystem.getClip();
		clip.open(audioInputStream);
		return clip;
	}

}
