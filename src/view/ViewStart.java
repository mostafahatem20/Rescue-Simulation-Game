package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//public class ViewStart extends JFrame {
//  JTextArea a;
//	public ViewStart() {
//		setTitle("Rescue");
//		setBounds(0, 0, 1200, 750);
//	//	setBounds(200,200, 500, 300);
//		getContentPane().setBackground(Color.yellow);
//		setBackground(Color.YELLOW);
//		setDefaultCloseOperation(EXIT_ON_CLOSE);
//		setLayout(null);
//		a= new JTextArea("RESCUE US!!");
//		a.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
//		a.setBounds(175,100,150,50);
//		a.setEditable(false);
//		getContentPane().add(a);
//		
//	}
//}
public class ViewStart extends JFrame 
//implements ActionListener 
{
//
	private JLabel m;
	private JLabel welcome;
//	private JButton b1;
//	private JButton b2;
	private JLabel l1;
	private JTextField f1;
	private JLabel a;
	public static final Color Purple = new Color(75,0,130);
	public static final Color Purple2 = new Color(147,112,219);

	public static Color getPurple2() {
		return Purple2;
	}

	public JLabel getM() {
		return m;
	}

	public JTextField getF1() {
		return f1;
	}

	public void setF1(JTextField f1) {
		this.f1 = f1;
	}

	public ViewStart() {
		setTitle("RescueSimulation");
		m = new JLabel();
		m.setBounds(0, 0, 1400, 800);
		m.setIcon(new ImageIcon(new ImageIcon("backgroundnotsimple.jpg")
				.getImage().getScaledInstance(1400, 800, Image.SCALE_SMOOTH)));
		getContentPane().add(m);
		a= new JLabel("RESCUE US!!");
		a.setFont(new Font("Comic Sans MS", Font.BOLD,45));
		a.setForeground(Color.WHITE);
		a.setBounds(430,100,550,100);
//		a.setEditable(false);
		m.add(a);
		
		this.getContentPane().setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(0, 0, 1200, 850);
		getContentPane().setBackground(Color.yellow);
		setBackground(getPurple());
		/////////////

//		/////////////
		welcome = new JLabel();
		welcome.setBounds(0, 0, 1200, 850);
		welcome.setOpaque(false);
		m.add(welcome);
		////////////
		l1 = new JLabel("Player Name:");
		l1.setFont(new Font("Comic Sans MS", Font.BOLD,25));
		l1.setForeground(Color.WHITE);
		l1.setBounds(400, 240, 200, 20);
		welcome.add(l1);
		l1.setFont(new Font("Comic Sans MS", Font.PLAIN,20));
		////////////
		f1 = new JTextField();
		f1.setBounds(550, 230, 200, 40);
		welcome.add(f1);
		////////////
		Image cursor = Toolkit.getDefaultToolkit().getImage("source.gif");
		Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(cursor.getScaledInstance(45, 45, Image.SCALE_SMOOTH),new Point(this.getX(),this.getY()), "cursor");
		this.setCursor(c);
		repaint();
	}
	
	public static Color getPurple() {
		return Purple;
	}

	public static void main(String[] args) {
				ViewStart s = new ViewStart();
				s.setVisible(true);
			}
}