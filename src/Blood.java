// 105403506資管3A何宜親

import javax.swing.*;
import java.awt.*;

public class Blood extends JPanel {

	int blood = 100;

	public Blood() {}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (blood < 25)
			g.setColor(Color.red);
		else
			g.setColor(Color.blue);
		g.fillRect(0, 4, blood * 7, 3);
	}

	// 碰到牆
	public void hitWall() {
		blood -= 20;
		repaint();
		if (blood == 0 || blood < 0)
			noBlood();
	}

	// 碰到路
	public void hitRoad() {
		blood -= 2;
		repaint();
		if (blood == 0 || blood < 0)
			noBlood();
	}

	// 碰到愛心
	public void getHeart() {
		if (blood > 0) {
			blood += 10;
			if (blood > 100)
				blood = 100;
		}
		repaint();
	}

	// 血量歸零
	public void noBlood() {
		int opt = JOptionPane.showConfirmDialog(null, "Game Over. Try again?", "Game Over", JOptionPane.YES_NO_CANCEL_OPTION);
		if(opt == JOptionPane.YES_OPTION) {
			blood = 100;
			repaint();
			ReadMap.timer.stop();
			try {
				new Thread(()->{Main.readMap.HeartChange();}).start();
				Thread.sleep(1000);
				new Thread(()->{Main.readMap.WallChange();}).start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else
			System.exit(0);
	}

}
