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
		bloodLabel.setText("�ثe��q: " + blood);
		add(bloodLabel);
		controllWallWithBlood();
	}

	// �I����
	public void hitWall() {
		blood -= 20;
		repaint();
		if (blood == 0 || blood < 0)
			noBlood();
	}

	// �I���
	public void hitRoad() {
		blood -= 2;
		repaint();
		if (blood == 0 || blood < 0)
			noBlood();
	}

	// �I��R��
	public void getHeart(int heartType) {
		if (blood > 0) {
			blood += heartType;
			if (blood > 100)
				blood = 100;
		}
		repaint();
	}

	// ��q�k�s
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
	
	// ��q�C��30
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
