package test;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class KeyTest extends JFrame {
	
	JLabel label;

	public static void main(String[] args) {
		new KeyTest();
	}
	
	public KeyTest() {
		setBounds(500, 500, 600, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		addKeyListener(new KeyInput());
		Timer timer = new Timer();
		timer.schedule(new TimerInput(), 4000, 30);
		
		label = new JLabel("yeaaaaaaaa");
		label.setBounds(10, 10, 100, 100);
		label.setFont(new Font("メイリオ", Font.BOLD, 15));
		JPanel panel = new JPanel();
		panel.add(label);
		panel.setLayout(null);
		getContentPane().add(panel);
	}
	
	class KeyInput implements KeyListener{

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO 自動生成されたメソッド・スタブ
			System.out.println("types");
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO 自動生成されたメソッド・スタブ
			System.out.println("pes");
			int i = e.getKeyCode();
			if(i == KeyEvent.VK_SLASH) {
				System.out.println("down");
			}
			if(i == KeyEvent.VK_BACK_SLASH) {
				System.out.println("baxk");
			}
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO 自動生成されたメソッド・スタブ
			
		}
		
	}
	
	class TimerInput extends TimerTask{

		int i = 0;
		@Override
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			i++;
			label.setText(String.valueOf(i));
		}
		
	}

}
