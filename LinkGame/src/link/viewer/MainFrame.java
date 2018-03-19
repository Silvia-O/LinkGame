package link.viewer;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import link.dao.HandleDB;

public class MainFrame extends JFrame implements ActionListener {

	// ������Ϣ����
	private JPanel contentPane;
	JPanel imagePanel = new JPanel();
	JButton btnStart = new JButton("��ʼ��Ϸ");
	JButton btnRefresh = new JButton("ˢ�²���");
	JButton btnHint = new JButton("��ʾ ��3");
	JButton btnOrder = new JButton("��ʾ���а�");
	JButton btnRule = new JButton("�÷ֹ���");
	JLabel lblLeftTime = new JLabel("60");
	JLabel lblScore = new JLabel("0");
	private int leftTime;
	private int refreshCount;
	private int score;
	private int hintCount;
	private boolean isRun = false; // ��Ϸ�Ƿ�ʼ��

	// ͼ����Ϣ����
	private int rows = 10;
	private int cols = 12;
	private int iconNum = 20;
	ImageButton[][] btnArr = null;
	private int[] numArr = null;

	// �洢ѡ�е�һ��ͼ�����Ϣ����
	int[] row = new int[2];
	int[] col = new int[2];
	private int flag = 0;
	private int rowa;
	private int cola;
	private int rowt;
	private int colt;

	// ��������
	private Timer timer;
	private AudioClip sound = null;
	HandleDB hdb = new HandleDB();
	private Random rand = new Random();
	// ����ͼƬ
	String filename = this.getClass().getResource("/images/0.jpg").getFile();
	ImageIcon draw = new ImageIcon(filename);
	


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new MainFrame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/* ����ʱ */
	public class MyTask extends TimerTask {

		@Override
		public void run() {
			clear(); // ������ߺ�������ͼ��
			leftTime--;
			lblLeftTime.setText(leftTime + "");
			if (leftTime == 0) {
				lblLeftTime.setText(leftTime + "");
				JOptionPane.showMessageDialog(null, "��Ϸʱ�䵽����");
				this.cancel();
				imagePanel.setVisible(false);
			}
		}

	}

	public void startThread() {
		if (timer != null) // ����������Ϸ��������ͬʱ����
			timer.cancel();

		timer = new Timer();
		timer.schedule(new MyTask(), 1000, 1000); // һ��������̣߳�ÿ��һ����Ӧ�߳�
	}

	/* ����Ƿ�ȫ������ */
	public boolean checkEmpty() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (btnArr[i][j].getNum() != 0) {
					return false;
				}
			}
		}
		return true;
	}

	/* ������ߺ�������ͼ�꣬ˢ������ͼ���� */
	public void clear() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				btnArr[i][j].updateIcon();
			}
		}
	}

	/* ������ʾ */
	public boolean hint() {
		for (int i = 0; i < rows * cols; i++) {
			if (numArr[i] != 0) {
				for (int j = i + 1; j < rows * cols; j++) {
					if (numArr[j] == numArr[i]) {
						if (match0(i / cols, i % cols, j / cols, j % cols)
								|| match1(i / cols, i % cols, j / cols, j % cols)
								|| match2(i / cols, i % cols, j / cols, j % cols)) {
							btnArr[i / cols][i % cols].setBorderPainted(true);
							btnArr[i / cols][i % cols].setBorder(new LineBorder(Color.YELLOW, 7));
							btnArr[j / cols][j % cols].setBorderPainted(true);
							btnArr[j / cols][j % cols].setBorder(new LineBorder(Color.YELLOW, 7));
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/* ��ʼ��ͼ��λ�� */
	public void initNum() {

		leftTime = 60;  //��ʼ��ʣ��ʱ��
		hintCount = 3;  //��ʼ��ʣ����ʾ����
		score = 0;      //��ʼ������
		refreshCount = 0;
		
		numArr = new int[rows * cols];
		for (int i = 0; i < rows * cols; i++) {
			numArr[i] = 0;
		}

		for (int i = 0; i < iconNum; i++) {
			int count = 0;
			while (count < 4) { // ÿ��ͼ�����4��
				int n = rand.nextInt(rows * cols); // ����[0,rows*cols-1]����
				if (n < 13 || n == 23 || n == 24 || n == 35 || n == 36 || n == 47 || n == 48 || n == 59 || n == 60 // ȥ���߿�:
																													// �����������ͼ��
																													// numArr[13-106]
						|| n == 71 || n == 72 || n == 83 || n == 84 || n == 95 || n == 96 || n > 106) {
					continue;
				}
				if (numArr[n] == 0) { // ����λ����δͼ�꣬�򽫸�ͼ����ڴ˴�
					numArr[n] = i + 1;
					count++;
				}
			}

		}
	}

	/* ͼ�������²��� */
	public void refreshNum() {
		int temp;
		boolean exchanged;
		for (int i = 0; i < rows * cols; i++) {
			if (numArr[i] != 0) {
				exchanged = false;
				while (!exchanged) {
					int n = rand.nextInt(rows * cols);
					if (numArr[n] != 0) {
						temp = numArr[i];
						numArr[i] = numArr[n];
						numArr[n] = temp;
						exchanged = true;
					}

				}

			}
		}
	}

	/* ��ʾͼ���� */
	public void init() {
		imagePanel.setVisible(true);

		btnArr = new ImageButton[rows][cols];
		imagePanel.removeAll();
		imagePanel.setLayout(new GridLayout(rows, cols));

		int k = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				ImageButton btn = new ImageButton(i, j, numArr[k]);
				k++;
				imagePanel.add(btn);
				btnArr[i][j] = btn; // �߼���Ӧ
				btnArr[i][j].addActionListener(this);
				btn.updateIcon();
			}
		}
	}

	/* �ж�����ͼ���Ƿ���԰��������� */
	// 0.�ж�����ͼ���Ƿ����ֱ��
	public boolean match0(int row1, int col1, int row2, int col2) {
		int min = 0;
		int max = 0;

		if (!(col1 == col2 || row1 == row2)) {
			return false;
		}
		if (col1 == col2) { // ����ֱ��
			min = row1 < row2 ? row1 : row2;
			max = row1 < row2 ? row2 : row1;
			for (min++; min < max; min++) {
				if (numArr[min * cols + col1] != 0) {
					return false;
				}
			}
		} else { // ����ֱ��
			min = col1 < col2 ? col1 : col2;
			max = col1 < col2 ? col2 : col1;
			for (min++; min < max; min++) {
				if (numArr[row1 * cols + min] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	// 1.�ж�����ͼ���Ƿ����һ����
	public boolean match1(int row1, int col1, int row2, int col2) {

		// ��(x1,y1)��(x2,y2)λ���ζԽǣ�Ѱ�Ҿ����������Խ��߶���
		int cola1 = col1;
		int rowa1 = row2;
		int cola2 = col2;
		int rowa2 = row1;

		// a1��a2������Ϊ�գ���ֱ������
		if (numArr[rowa1 * cols + cola1] != 0 && numArr[rowa2 * cols + cola2] != 0) {
			return false;
		}
		if (numArr[rowa1 * cols + cola1] == 0) { // �ж�a1-s1ֱ�� a1-s2ֱ��
			if (!(match0(rowa1, cola1, row1, col1) && match0(rowa1, cola1, row2, col2))) {
				return false;
			} else {
				rowa = rowa1;
				cola = cola1;

			}
		} else { // �ж�a2-s1ֱ�� a2-s2ֱ��
			if (!(match0(rowa2, cola2, row1, col1) && match0(rowa2, cola2, row2, col2))) {
				return false;
			} else {
				rowa = rowa2;
				cola = cola2;
			}
		}
		return true;
	}

	// 2.�ж�����ͼ���Ƿ���Զ�����
	public boolean match2(int row1, int col1, int row2, int col2) {

		int rowtemp;
		int coltemp;

		// �ֱ����ĸ�����Ѱ�ҹյ�

		// s1��col+1
		if (col1 != cols - 1) {
			rowtemp = row1;
			coltemp = col1;
			for (coltemp++; coltemp < cols; coltemp++) {
				if (numArr[rowtemp * cols + coltemp] == 0) {
					if (match1(rowtemp, coltemp, row2, col2)) {
						rowt = rowtemp;
						colt = coltemp;
						return true;
					}
				} else {
					break;
				}
			}
		}

		// s1��col-1
		if (col1 != 0) {
			rowtemp = row1;
			coltemp = col1;
			for (coltemp--; coltemp >= 0; coltemp--) {
				if (numArr[rowtemp * cols + coltemp] == 0) {
					if (match1(rowtemp, coltemp, row2, col2)) {
						rowt = rowtemp;
						colt = coltemp;
						return true;
					}
				} else {
					break;
				}
			}
		}

		// s1��row+1
		if (row1 != rows - 1) {
			rowtemp = row1;
			coltemp = col1;
			for (rowtemp++; rowtemp < rows; rowtemp++) {
				if (numArr[rowtemp * cols + coltemp] == 0) {
					if (match1(rowtemp, coltemp, row2, col2)) {
						rowt = rowtemp;
						colt = coltemp;
						return true;
					}
				} else {
					break;
				}
			}
		}

		// s1��row-1
		if (row1 != 0) {
			rowtemp = row1;
			coltemp = col1;
			for (rowtemp--; rowtemp >= 0; rowtemp--) {
				if (numArr[rowtemp * cols + coltemp] == 0) {
					if (match1(rowtemp, coltemp, row2, col2)) {
						rowt = rowtemp;
						colt = coltemp;
						return true;
					}
				} else {
					break;
				}
			}
		}
		return false;
	}

	/* ����ͼ������� */
	public void drawLine(int row1, int col1, int row2, int col2) {
		int min;
		int max;

		if (col1 == col2) {
			min = row1 < row2 ? row1 : row2;
			max = row1 < row2 ? row2 : row1;
			for (; min <= max; min++) {
				btnArr[min][col1].setContentAreaFilled(false);
				btnArr[min][col1].setIcon(draw);

			}
		} else {
			min = col1 < col2 ? col1 : col2;
			max = col1 < col2 ? col2 : col1;
			for (; min <= max; min++) {
				btnArr[row1][min].setContentAreaFilled(false);
				btnArr[row1][min].setIcon(draw);
			}
		}
	}

	/* ��Ӧ����ѡ�е�һ��ͼ�� */
	public void linkIcon(ActionEvent e) {
		ImageButton btn = (ImageButton) e.getSource();
		row[flag] = btn.getRow();
		col[flag] = btn.getCol();

		btn.setBorderPainted(true);
		btn.setBorder(new LineBorder(Color.BLACK, 3)); // ѡ�а�ť�����߱߿�
		int n0 = row[0] * cols + col[0];
		int n1 = row[1] * cols + col[1];

		if (numArr[n0] == numArr[n1] && numArr[n0] != 0 && n0 != n1) { // ������ͬ��ť�ϵ�ͼ����ͬ
			if (match0(row[0], col[0], row[1], col[1]) || match1(row[0], col[0], row[1], col[1])
					|| match2(row[0], col[0], row[1], col[1])) {
				// ˢ��ʱ��
				if (leftTime > 58)
					leftTime = 60;
				else
					leftTime += 2;
				startThread();
				// ˢ�°�ť��Ϣ
				btnArr[row[0]][col[0]].setNum(0);
				btnArr[row[1]][col[1]].setNum(0);
				numArr[n0] = 0;
				numArr[n1] = 0; // ������ͼ����Ϊ0
				btnArr[row[0]][col[0]].updateIcon();
				btnArr[row[1]][col[1]].updateIcon();
				sound.play(); // ������Ч
				score += 5;
				lblScore.setText(score + "");
				// ʵ������
				// ��Ϊ���߳�run()��clear()��ÿ��һ����Ӧ�߳�,�˿鲻���������ǿ����˳��,����������ͬһ��Ӧ�����̱�����!!!!!
				if (match0(row[0], col[0], row[1], col[1])) {
					drawLine(row[0], col[0], row[1], col[1]);
				} else if (match1(row[0], col[0], row[1], col[1])) {
					drawLine(row[0], col[0], rowa, cola);
					drawLine(rowa, cola, row[1], col[1]);
				} else {
					drawLine(row[0], col[0], rowt, colt);
					drawLine(rowt, colt, rowa, cola);
					drawLine(rowa, cola, row[1], col[1]);
				}
			} else {
				btnArr[row[(flag + 1) % 2]][col[(flag + 1) % 2]].setBorderPainted(false); // ȡ��֮ǰ��ѡ�У�����ѡ�иոյİ�ť
			}
		} else {
			btnArr[row[(flag + 1) % 2]][col[(flag + 1) % 2]].setBorderPainted(false); // ȡ��֮ǰ��ѡ�У�����ѡ�иոյİ�ť
		}

		flag = (flag + 1) % 2; // 0/1�滻

		// �ж��Ƿ�ȫ��������
		if (checkEmpty()) {
			int leftTime = Integer.parseInt(lblLeftTime.getText());
			score = score + 20 * hintCount - 10 * refreshCount;
			lblScore.setText(score + "");
			int time = 60 - leftTime; // ������Ϸ����ʱ��
			JOptionPane.showMessageDialog(this, "��Ӯ��!");// ��һ������ָ��Ϣ�������ĸ����齨���ڶ�������ָ��Ϣ��������
			String name = JOptionPane.showInputDialog(this, "�������ǳ�");
			if (name == null || "".equals(name.trim())) { // �������������ַ���
				name = "����";
			}
			hdb.insertInfo(name, time, score);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStart) {
			if (isRun) {
				int n = JOptionPane.showConfirmDialog(this, "�����Ҫ���¿�ʼ��Ϸ��");
				if (n == 0) {
					initNum();
					init();
					startThread();
				}
			} else {
				initNum();
				init();
				startThread();
				isRun = true;
				btnStart.setText("���¿�ʼ");
			}
		} else if (e.getSource() == btnRefresh) {
			if (isRun) {
				refreshCount++;
				refreshNum();
				init();
			}
		} else if (e.getSource() == btnHint) {
			if (isRun) {
				JButton btn = (JButton)e.getSource(); 
				if("��ʾ ��3".equals(btn.getText().trim()) || "��ʾ ��2".equals(btn.getText().trim()) || "��ʾ ��1".equals(btn.getText().trim())) {
					if(!hint()) {     //û�п������ľ�ˢ�²���
						refreshNum();
						init();
					}
					hintCount--;
					btnHint.setText("��ʾ ��" + hintCount);
				}	
			}
		} else if (e.getSource() == btnOrder) {
			new TimeOrderDialog(this, true); // ����dialogʱ�����岻�ɶ�
		} else if(e.getSource() == btnRule){
			JOptionPane.showMessageDialog(this,"���յ÷� = ��������*5 + ʣ����ʾ��*20 - ˢ�¾������*10");		
		}else {
			linkIcon(e);
		}
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 957, 700);
		setLocationRelativeTo(this);
		setTitle("-��ըС���ʵ�������-");

		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 250, 240));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		imagePanel.setBackground(new Color(255, 250, 240));
		imagePanel.setBounds(20, 25, 720, 600);
		contentPane.add(imagePanel);

		btnStart.setBackground(new Color(255, 218, 185));
		btnStart.setBounds(772, 326, 113, 27);
		contentPane.add(btnStart);
		btnStart.addActionListener(this);

		JLabel lblLeftTimeN = new JLabel("ʣ��ʱ��:");
		lblLeftTimeN.setFont(new Font("����", Font.BOLD, 18));
		lblLeftTimeN.setBounds(772, 193, 113, 27);
		contentPane.add(lblLeftTimeN);
		lblLeftTime.setFont(new Font("����", Font.PLAIN, 18));

		JLabel lblS = new JLabel("s");
		lblS.setFont(new Font("����", Font.PLAIN, 18));
		lblS.setBounds(902, 194, 23, 24);
		contentPane.add(lblS);
		btnRefresh.addActionListener(this);

		lblLeftTime.setForeground(new Color(95, 158, 160));
		lblLeftTime.setBounds(877, 197, 72, 18);
		contentPane.add(lblLeftTime);

		btnOrder.setBackground(new Color(255, 218, 185));
		btnOrder.setBounds(772, 500, 113, 27);
		contentPane.add(btnOrder);
		btnOrder.addActionListener(this);

		btnRefresh.setBackground(new Color(255, 218, 185));
		btnRefresh.setBounds(772, 385, 113, 27);
		contentPane.add(btnRefresh);

		btnHint.setBackground(new Color(255, 218, 185));
		btnHint.setBounds(772, 443, 113, 27);
		contentPane.add(btnHint);
		btnHint.addActionListener(this);
		
		JLabel lblScoreN = new JLabel("��Ϸ�÷�:");
		lblScoreN.setFont(new Font("����", Font.BOLD, 18));
		lblScoreN.setBounds(772, 252, 91, 18);
		contentPane.add(lblScoreN);
		
		lblScore.setFont(new Font("����", Font.PLAIN, 18));
		lblScore.setForeground(new Color(95, 158, 160));	
		lblScore.setBounds(887, 252, 72, 18);
		contentPane.add(lblScore);
		
		btnRule.setBackground(new Color(255, 218, 185));
		btnRule.setBounds(772, 561, 113, 27);
		contentPane.add(btnRule);
		btnRule.addActionListener(this);
		

		// ������Ч
		URL urlSound = this.getClass().getResource("/music/biu.wav"); // javaһ�㲻֧��MP3�ļ�
		sound = Applet.newAudioClip(urlSound); // ͳһ��Դ��λ��

		setVisible(true);
	}
}
