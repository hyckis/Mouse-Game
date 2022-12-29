// 105403506資管3A何宜親

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.regex.*;
import java.util.stream.*;
import java.security.*;
import javax.swing.*;

public class ReadMap extends JPanel {

	// 圖片
	ImageIcon wall = new ImageIcon("brickwall.png");
	ImageIcon diamond = new ImageIcon("diamond.png");
	ImageIcon heart = new ImageIcon("heart.png");
	// 重設圖片大小
	Image wallImg = wall.getImage();
	Image wallImage = wallImg.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon wallIcon = new ImageIcon(wallImage);	// 牆
	Image diamondImg = diamond.getImage();
	Image diamondImage = diamondImg.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon diamondIcon = new ImageIcon(diamondImage);	// 鑽石
	Image heartImg = heart.getImage();
	Image heartImage = heartImg.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon heartIcon = new ImageIcon(heartImage);	// 愛心
	// 放圖片
	GridLayout mazeBlocks = new GridLayout(10, 10);	// 10 * 10的gridlayout
	JLabel[] blocks = new JLabel[100];	// 100個label
	int index = -1;	// 取索引值

	// 血條
	static Blood blood = new Blood();
	// 定時扣血量
	static int pointer;	// 扣血量模式, 1為停在路上; 2為停在牆上
	static TimerHandler handler = new TimerHandler();	// timer的actionlistener
	static Timer timer = new Timer(1000, handler);	// timer

	// 牆/ 愛心轉換
	ArrayList<Integer> wallCanChange = new ArrayList<>();	// 儲存index of wall that can change
	SecureRandom randSec = new SecureRandom();	// 產生隨機秒數
	Object lock = new Object();	// 呼叫此物件 => wait/ notify
	
	// 滑鼠事件
	MouseListener mouseOnWall = new MouseAdapter() {	// 牆壁
		@Override
		public void mouseEntered(MouseEvent e) {
			blood.hitWall();
			pointer = 2;
			timer.start();
		}
		@Override
		public void mouseExited(MouseEvent e) {
			timer.stop();
		}
	};
	MouseListener mouseOnHeart = new MouseAdapter() {	// 愛心
		@Override
		public void mouseEntered(MouseEvent e) {
			blood.getHeart();
		}
	};

	public ReadMap() {

		setLayout(mazeBlocks);	// 放gridlayout
		
		try {
			Pattern pattern = Pattern.compile("\t");	// to split with tab
			// 把map.txt的內容裝到stream裡
			Stream<Object> map =
					Files.lines(Paths.get("map.txt"))	// 得到map.txt的路徑
						 .flatMap(line -> pattern.splitAsStream(line));	// 用tab分開
			// 一個一個跑
			map.forEach(m -> {

				index++;	// 得到當前索引值
				blocks[index] = new JLabel();	// 初始化第index個JLabel

				switch(m.toString()) {

						case "0":	// 路
							blocks[index].addMouseListener(
									new MouseAdapter() {
										@Override
										public void mouseEntered(MouseEvent e) {
											blood.hitRoad();
											pointer = 1;
											timer.start();
										}
										@Override
										public void mouseExited(MouseEvent e) {
											timer.stop();
										}
									});
							break;

						case "1":	// 牆或愛心
							blocks[index].setIcon(wallIcon);
					  		blocks[index].addMouseListener(mouseOnWall);
							SecureRandom random = new SecureRandom();
							random.ints(1, 1, 3)	// 隨機生成1個為1或2的數
								  .boxed()
								  .forEach(r -> {
									  switch(r) {
									  	case 1:	// 牆
									  		break;
									  	case 2:	// 可以轉成愛心的牆
									  		wallCanChange.add(index);	// 把該label的index加入list
									  		break;
									  }
									});
							break;

						case "2":	// 出口
							blocks[index].setIcon(diamondIcon);
							blocks[index].addMouseListener(
									new MouseAdapter() {
										@Override
										public void mouseEntered(MouseEvent e) {
											JOptionPane.showMessageDialog(null, "Reached the End!!", "Congradulations", JOptionPane.INFORMATION_MESSAGE);
											System.exit(0);
										}
									});
							break;

					}
				
				add(blocks[index]);	// 加到gridlayout
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	// 處理timer
	private static class TimerHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (pointer == 1)	// 路
				blood.hitRoad();
			else if (pointer == 2)	// 牆
				blood.hitWall();
		}
	}

	// 牆壁/ 愛心轉換
	// 牆壁變愛心
	public void WallChange() {
		try {
			synchronized(lock) {
				Thread.sleep(randSec.nextInt(10)*1000);	// 0~10 sec
				for (int i = 0; i < 100; i++) {
					if (wallCanChange.contains(i)) {
						blocks[i].removeAll();
						blocks[i].removeMouseListener(mouseOnWall);
						blocks[i].setIcon(heartIcon);
						blocks[i].addMouseListener(mouseOnHeart);
					}
				}
				lock.notify();	// 叫醒heartchange, 讓他執行wait()以後的程式碼
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// 愛心變牆壁
	public void HeartChange() {
		try {
			synchronized(lock) {
				lock.wait();	// 等wallchange結束才執行下面的東西
				Thread.sleep(randSec.nextInt(7)*1000+3000);	// 3~10 sec
				for (int i = 0; i < 100; i++) {
					if (wallCanChange.contains(i)) {
						blocks[i].removeAll();
						blocks[i].removeMouseListener(mouseOnHeart);
						blocks[i].setIcon(wallIcon);
						blocks[i].addMouseListener(mouseOnWall);
						if (!timer.isRunning()) {	// 處理愛心變回牆壁後滑鼠還停在上面的情況
							pointer = 2;
							timer.start();
						}
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
