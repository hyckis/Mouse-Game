// 105403506���3A��y��

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

	// �Ϥ�
	ImageIcon wall = new ImageIcon("brickwall.png");
	ImageIcon diamond = new ImageIcon("diamond.png");
	ImageIcon heart = new ImageIcon("heart.png");
	// ���]�Ϥ��j�p
	Image wallImg = wall.getImage();
	Image wallImage = wallImg.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon wallIcon = new ImageIcon(wallImage);	// ��
	Image diamondImg = diamond.getImage();
	Image diamondImage = diamondImg.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon diamondIcon = new ImageIcon(diamondImage);	// �p��
	Image heartImg = heart.getImage();
	Image heartImage = heartImg.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon heartIcon = new ImageIcon(heartImage);	// �R��
	// ��Ϥ�
	GridLayout mazeBlocks = new GridLayout(10, 10);	// 10 * 10��gridlayout
	JLabel[] blocks = new JLabel[100];	// 100��label
	int index = -1;	// �����ޭ�

	// ���
	static Blood blood = new Blood();
	// �w�ɦ���q
	static int pointer;	// ����q�Ҧ�, 1�����b���W; 2�����b��W
	static TimerHandler handler = new TimerHandler();	// timer��actionlistener
	static Timer timer = new Timer(1000, handler);	// timer

	// ��/ �R���ഫ
	ArrayList<Integer> wallCanChange = new ArrayList<>();	// �x�sindex of wall that can change
	SecureRandom randSec = new SecureRandom();	// �����H�����
	Object lock = new Object();	// �I�s������ => wait/ notify
	
	// �ƹ��ƥ�
	MouseListener mouseOnWall = new MouseAdapter() {	// ���
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
	MouseListener mouseOnHeart = new MouseAdapter() {	// �R��
		@Override
		public void mouseEntered(MouseEvent e) {
			blood.getHeart();
		}
	};

	public ReadMap() {

		setLayout(mazeBlocks);	// ��gridlayout
		
		try {
			Pattern pattern = Pattern.compile("\t");	// to split with tab
			// ��map.txt�����e�˨�stream��
			Stream<Object> map =
					Files.lines(Paths.get("map.txt"))	// �o��map.txt�����|
						 .flatMap(line -> pattern.splitAsStream(line));	// ��tab���}
			// �@�Ӥ@�Ӷ]
			map.forEach(m -> {

				index++;	// �o���e���ޭ�
				blocks[index] = new JLabel();	// ��l�Ʋ�index��JLabel

				switch(m.toString()) {

						case "0":	// ��
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

						case "1":	// ��ηR��
							blocks[index].setIcon(wallIcon);
					  		blocks[index].addMouseListener(mouseOnWall);
							SecureRandom random = new SecureRandom();
							random.ints(1, 1, 3)	// �H���ͦ�1�Ӭ�1��2����
								  .boxed()
								  .forEach(r -> {
									  switch(r) {
									  	case 1:	// ��
									  		break;
									  	case 2:	// �i�H�ন�R�ߪ���
									  		wallCanChange.add(index);	// ���label��index�[�Jlist
									  		break;
									  }
									});
							break;

						case "2":	// �X�f
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
				
				add(blocks[index]);	// �[��gridlayout
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	// �B�ztimer
	private static class TimerHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (pointer == 1)	// ��
				blood.hitRoad();
			else if (pointer == 2)	// ��
				blood.hitWall();
		}
	}

	// ���/ �R���ഫ
	// ����ܷR��
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
				lock.notify();	// �s��heartchange, ���L����wait()�H�᪺�{���X
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// �R�������
	public void HeartChange() {
		try {
			synchronized(lock) {
				lock.wait();	// ��wallchange�����~����U�����F��
				Thread.sleep(randSec.nextInt(7)*1000+3000);	// 3~10 sec
				for (int i = 0; i < 100; i++) {
					if (wallCanChange.contains(i)) {
						blocks[i].removeAll();
						blocks[i].removeMouseListener(mouseOnHeart);
						blocks[i].setIcon(wallIcon);
						blocks[i].addMouseListener(mouseOnWall);
						if (!timer.isRunning()) {	// �B�z�R���ܦ^�����ƹ��ٰ��b�W�������p
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
