import javax.swing.*;
import java.awt.*;

public class Blood extends JPanel {

	int blood = 100;
	JLabel bloodLabel = new JLabel();

	public Blood() {}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (blood < 30)
			g.setColor(Color.red);
		else
			g.setColor(Color.blue);
		g.fillRect(0, 4, blood * 7, 3);
		bloodLabel.setText("目前血量: " + blood);
		add(bloodLabel);
		controllWallWithBlood();
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
	public void getHeart(int heartType) {
		if (blood > 0) {
			blood += heartType;
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
			Main.changeToHeartThread.interrupt();
			Main.changeToWallThread.interrupt();
			try {
				new Thread(()->{Main.readMap.heartChange();}).start();
				Thread.sleep(1000);
				new Thread(()->{Main.readMap.wallChange();}).start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else
			System.exit(0);
	}
	
	// 血量低於30
	public void controllWallWithBlood() {
		if (blood < 30)
			new Thread(()->{Main.readMap.grayWall();}).start();		
		if (blood > 30)
			new Thread(()->{Main.readMap.generalWall();}).start();			
	}
	
	public int getBlood() {
		return blood;
	}

}
