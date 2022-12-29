// 105403506���3A��y�ˡC�[���D: �C�������B����b30�H�U���ܦ�����

import java.awt.*;
import javax.swing.*;

public class Main {
	
	static ReadMap readMap = new ReadMap();
	static Thread changeToWallThread = new Thread(()->{readMap.heartChange();});
	static Thread changeToHeartThread = new Thread(()->{readMap.wallChange();});
	static Object lock = new Object();
	
	public Main() {}

	public static void main(String[] args) {

		String intro = "Welcome!\nYou have 100 LP at first. "
				+ "\nHit the road, LP-2;\nHit the wall, LP-20;\nGet the heart, LP+10;"
				+ "\nNotice: hearts will change into walls in random seconds."
				+ "\nNotice: Don't stay at the same block, or lose LP for every second."
				+ "\nWhen reaching the end or LP is 0, the game is over.\nGood luck!";
		
		
		JOptionPane.showMessageDialog(null, intro, "Welcome", JOptionPane.PLAIN_MESSAGE);
		
		
		JFrame frame = new JFrame();		
		BorderLayout mazeInterface = new BorderLayout();
		frame.setLayout(mazeInterface);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 750);
		frame.setResizable(false);	// �T�w�����j�p
		frame.setVisible(true);	
		
		try {
			frame.add(ReadMap.blood, BorderLayout.NORTH);	// �[���
			frame.add(readMap, BorderLayout.CENTER);	// �[�g�c
			Thread.sleep(10);
			readMap.run();
			changeToWallThread.start();
			Thread.sleep(1000);
			changeToHeartThread.start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
