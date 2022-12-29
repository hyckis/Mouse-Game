// 105403506���3A��y��

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import java.util.stream.*;
import java.security.*;
import java.sql.*;
import javax.swing.*;

public class ReadMap extends JPanel implements Runnable {
	
	// ��Ʈw
	private static final String URL = "jdbc:mysql://localhost:3306/member?autoReconnect=true&useSSL=false";
	private static final String USERNAME = "java";
	private static final String PASSWORD = "java";
	private static final String DEFAULT_QUERY = "SELECT * FROM user";
	private Connection connection;
	private PreparedStatement selectAllUser;
	private PreparedStatement updateUser;
	
	// �ϥΪ̸��
	String userID;
	int level;

	// �Ϥ�
	ImageIcon wall = new ImageIcon("brickwall.png");
	ImageIcon grayWall = new ImageIcon("grayBrickwall.png");
	ImageIcon diamond = new ImageIcon("diamond.png");
	ImageIcon heart2 = new ImageIcon("heart2.png");
	ImageIcon heart4 = new ImageIcon("heart4.png");
	ImageIcon heart6 = new ImageIcon("heart6.png");
	ImageIcon heart8 = new ImageIcon("heart8.png");
	ImageIcon heart10 = new ImageIcon("heart10.png");
	// ���]�Ϥ��j�p
	Image wallImg = wall.getImage();
	Image wallImage = wallImg.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon wallIcon = new ImageIcon(wallImage);	// ��
	Image graywallImg = grayWall.getImage();
	Image graywallImage = graywallImg.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon grayWallIcon = new ImageIcon(graywallImage);	// �Ǧ���
	Image diamondImg = diamond.getImage();
	Image diamondImage = diamondImg.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon diamondIcon = new ImageIcon(diamondImage);	// �p��
	Image heart2Img = heart2.getImage();
	Image heart2Image = heart2Img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon heart2Icon = new ImageIcon(heart2Image);	// 2���R��
	Image heart4Img = heart4.getImage();
	Image heart4Image = heart4Img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon heart4Icon = new ImageIcon(heart4Image);	// 4���R��
	Image heart6Img = heart6.getImage();
	Image heart6Image = heart6Img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon heart6Icon = new ImageIcon(heart6Image);	// 6���R��
	Image heart8Img = heart8.getImage();
	Image heart8Image = heart8Img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon heart8Icon = new ImageIcon(heart8Image); 	// 8���R��
	Image heart10Img = heart10.getImage();
	Image heart10Image = heart10Img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	Icon heart10Icon = new ImageIcon(heart10Image);	// 10���R��
	
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
	int heartType;	// ���h�֦�
	
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
			blood.getHeart(heartType);
		}
	};

	public ReadMap() {
		
		userID = JOptionPane.showInputDialog("��J�N��:  ");
		
		try {
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			selectAllUser = connection.prepareStatement(DEFAULT_QUERY);
			updateUser = connection.prepareStatement("UPDATE user SET level = ? WHERE "+"(userID = ?)");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		setLayout(mazeBlocks);	// ��gridlayout
		
		// ��user level
		getAllUser().stream().forEach(u->{
			if (u.getUserID().equals(userID))
				level = u.getLevel();
		});
		if (level == 0) {
			JOptionPane.showMessageDialog(null, "invalid userid", "error", 0);
			System.exit(0);
		}
		
		try {
			Pattern pattern = Pattern.compile("\t");	// to split with tab
			
			// ��txt�����e�˨�stream��
			Stream<Object> map;
			if (level == 2) {
				map = Files	.lines(Paths.get("2.txt"))
							.flatMap(line -> pattern.splitAsStream(line));
			} else if (level == 3) {
				map = Files .lines(Paths.get("3.txt"))
							.flatMap(line -> pattern.splitAsStream(line));
			} else {
				map = Files .lines(Paths.get("1.txt"))
						.flatMap(line -> pattern.splitAsStream(line));
			}
			
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
											if (level < 3)
												updateUser(userID, level+1);
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
	public void wallChange() {
		try {
			synchronized(lock) {
				Thread.sleep(randSec.nextInt(10)*1000);	// 0~10 sec
				heartType = 10;
				for (int i = 0; i < 100; i++) {
					if (wallCanChange.contains(i)) {
						blocks[i].removeAll();
						blocks[i].removeMouseListener(mouseOnWall);
						blocks[i].setIcon(heart10Icon);
						blocks[i].addMouseListener(mouseOnHeart);
					}
				}
				lock.notifyAll();	// �s��heartchange, ���L����wait()�H�᪺�{���X
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void grayWall() {
		try {
			Thread.sleep(1);
			for (int i = 0; i < 100; i++) {
				if (blocks[i].getIcon() == wallIcon) {
					blocks[i].removeAll();
					blocks[i].setIcon(grayWallIcon);
				}
			}		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void generalWall() {
		try {
			Thread.sleep(1);
			for (int i = 0; i < 100; i++) {
				if (blocks[i].getIcon() == grayWallIcon) {
					blocks[i].removeAll();
					blocks[i].setIcon(wallIcon);
				}
			}	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// �R���ഫ
	public void heartChange() {
		try {
			synchronized(lock) {
				lock.wait();	
				Thread.sleep(1000);
				heartType = 8;
				for (int i = 0; i < 100; i++) {
					if (wallCanChange.contains(i)) {
						blocks[i].removeAll();
						blocks[i].setIcon(heart8Icon);
					}
				}
				Thread.sleep(1000);
				heartType = 6;
				for (int i = 0; i < 100; i++) {
					if (wallCanChange.contains(i)) {
						blocks[i].removeAll();
						blocks[i].setIcon(heart6Icon);
					}
				}
				Thread.sleep(1000);
				heartType = 4;
				for (int i = 0; i < 100; i++) {
					if (wallCanChange.contains(i)) {
						blocks[i].removeAll();
						blocks[i].setIcon(heart4Icon);
					}
				}
				Thread.sleep(1000);
				heartType = 2;
				for (int i = 0; i < 100; i++) {
					if (wallCanChange.contains(i)) {
						blocks[i].removeAll();
						blocks[i].setIcon(heart2Icon);
					}
				}
				Thread.sleep(1000);
				for (int i = 0; i < 100; i++) {
					if (wallCanChange.contains(i)) {
						blocks[i].removeAll();
						blocks[i].removeMouseListener(mouseOnHeart);
						if (blood.getBlood() > 30)
							blocks[i].setIcon(wallIcon);
						else 
							blocks[i].setIcon(grayWallIcon);
						blocks[i].addMouseListener(mouseOnWall);
						if (!timer.isRunning() && pointer != 1) {	// �B�z�R���ܦ^�����ƹ��ٰ��b�W�������p
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
	
	
	// �ϥΪ̸��
	public List<User> getAllUser() {
		ResultSet resultSet = null;
		List<User> results = new ArrayList<User>();
		try {
			resultSet = selectAllUser.executeQuery();
			while (resultSet.next()) {
				results.add(new User(
					resultSet.getString("UserID"),
					resultSet.getInt("level")
				));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
				close();
			}
		}
		return results;
	}
	
	//
	public void updateUser(String userID, int level) {
		try {
			updateUser.setInt(1, level);
			updateUser.setString(2, userID);		
			int result = updateUser.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			close();
		}
	}
	
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
