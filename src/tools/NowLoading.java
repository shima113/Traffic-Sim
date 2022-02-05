package tools;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class NowLoading extends JFrame {
	
	public NowLoading() {
		new JFrame();
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setUndecorated(true);
		this.setSize(300, 100);
		this.setLocationRelativeTo(null);
		JPanel load = new JPanel();
		load.setLayout(null);
		JLabel label = new JLabel("Now Loading...");
		label.setBounds(0, 0, 300, 100);
		label.setFont(new Font("Consolas", Font.BOLD, 30));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);
		load.add(label);
		load.setBorder(new LineBorder(Color.DARK_GRAY, 2, false));
		this.add(load);
	}
}
