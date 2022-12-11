package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class ViewEnd extends JFrame {
	private JLabel m;
	private JTextArea a;
	private JTextArea b;

	public void updatecasualities(int x) {
		b.setText("Causalities: " + x);
	}

	public ViewEnd() {
		setTitle("RescueSimulation");
		setBounds(0, 0, 1200, 750);
		m = new JLabel();
		m.setBounds(0, 0, 1400, 800);
		m.setIcon(new ImageIcon(new ImageIcon("backgroundsimple.jpg")
				.getImage().getScaledInstance(1400, 800, Image.SCALE_SMOOTH)));
	//	setBounds(200, 200, 500, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setTitle("GameOver");
		a = new JTextArea("Game Over!");
		a.setOpaque(false);
		a.setFont(new Font("Comic Sans MS", Font.BOLD,50));;
		a.setBounds(470,150,550,100);
		a.setForeground(Color.WHITE);
		a.setEditable(false);
		m.add(a);
		b = new JTextArea("Casualities: 0");
		b.setFont(new Font("Comic Sans MS", Font.BOLD,30));
		b.setBounds(500,250,550,100);
		b.setForeground(Color.WHITE);
		b.setEditable(false);
		b.setOpaque(false);
		m.add(b);
		getContentPane().add(m);
		setBackground(ViewStart.getPurple());
		Image cursor = Toolkit.getDefaultToolkit().getImage("source.gif");
		Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(cursor.getScaledInstance(45, 45, Image.SCALE_SMOOTH),new Point(this.getX(),this.getY()), "cursor");
		this.setCursor(c);
	}

	public static void main(String[] args) {
		ViewEnd c = new ViewEnd();
		c.setVisible(true);
	}

}
