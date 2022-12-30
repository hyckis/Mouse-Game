// Author: YICHIN HO
// 加分題: 遊戲說明、血條在30以下時變成紅色

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
		frame.setResizable(false);	// 固定視窗大小
		frame.setVisible(true);	
		
		try {
			frame.add(ReadMap.blood, BorderLayout.NORTH);	// 加血條
			frame.add(readMap, BorderLayout.CENTER);	// 加迷宮
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
