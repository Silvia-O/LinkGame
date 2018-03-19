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

	// 界面信息变量
	private JPanel contentPane;
	JPanel imagePanel = new JPanel();
	JButton btnStart = new JButton("开始游戏");
	JButton btnRefresh = new JButton("刷新布局");
	JButton btnHint = new JButton("提示 ×3");
	JButton btnOrder = new JButton("显示排行榜");
	JButton btnRule = new JButton("得分规则");
	JLabel lblLeftTime = new JLabel("60");
	JLabel lblScore = new JLabel("0");
	private int leftTime;
	private int refreshCount;
	private int score;
	private int hintCount;
	private boolean isRun = false; // 游戏是否开始了

	// 图标信息变量
	private int rows = 10;
	private int cols = 12;
	private int iconNum = 20;
	ImageButton[][] btnArr = null;
	private int[] numArr = null;

	// 存储选中的一对图标的信息变量
	int[] row = new int[2];
	int[] col = new int[2];
	private int flag = 0;
	private int rowa;
	private int cola;
	private int rowt;
	private int colt;

	// 其他变量
	private Timer timer;
	private AudioClip sound = null;
	HandleDB hdb = new HandleDB();
	private Random rand = new Random();
	// 连线图片
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

	/* 倒计时 */
	public class MyTask extends TimerTask {

		@Override
		public void run() {
			clear(); // 清除连线和消除的图标
			leftTime--;
			lblLeftTime.setText(leftTime + "");
			if (leftTime == 0) {
				lblLeftTime.setText(leftTime + "");
				JOptionPane.showMessageDialog(null, "游戏时间到啦！");
				this.cancel();
				imagePanel.setVisible(false);
			}
		}

	}

	public void startThread() {
		if (timer != null) // 避免重新游戏后多个任务同时进行
			timer.cancel();

		timer = new Timer();
		timer.schedule(new MyTask(), 1000, 1000); // 一秒后启动线程，每隔一秒响应线程
	}

	/* 检查是否全部连完 */
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

	/* 清除连线和消除的图标，刷新整个图标盘 */
	public void clear() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				btnArr[i][j].updateIcon();
			}
		}
	}

	/* 给予提示 */
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

	/* 初始化图标位置 */
	public void initNum() {

		leftTime = 60;  //初始化剩余时间
		hintCount = 3;  //初始化剩余提示次数
		score = 0;      //初始化分数
		refreshCount = 0;
		
		numArr = new int[rows * cols];
		for (int i = 0; i < rows * cols; i++) {
			numArr[i] = 0;
		}

		for (int i = 0; i < iconNum; i++) {
			int count = 0;
			while (count < 4) { // 每个图标出现4次
				int n = rand.nextInt(rows * cols); // 产生[0,rows*cols-1]的数
				if (n < 13 || n == 23 || n == 24 || n == 35 || n == 36 || n == 47 || n == 48 || n == 59 || n == 60 // 去掉边框:
																													// 中央区域放置图标
																													// numArr[13-106]
						|| n == 71 || n == 72 || n == 83 || n == 84 || n == 95 || n == 96 || n > 106) {
					continue;
				}
				if (numArr[n] == 0) { // 若该位置尚未图标，则将该图标放在此处
					numArr[n] = i + 1;
					count++;
				}
			}

		}
	}

	/* 图标盘重新布局 */
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

	/* 显示图标盘 */
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
				btnArr[i][j] = btn; // 逻辑对应
				btnArr[i][j].addActionListener(this);
				btn.updateIcon();
			}
		}
	}

	/* 判断两个图标是否可以按规则相连 */
	// 0.判断两个图标是否可以直连
	public boolean match0(int row1, int col1, int row2, int col2) {
		int min = 0;
		int max = 0;

		if (!(col1 == col2 || row1 == row2)) {
			return false;
		}
		if (col1 == col2) { // 竖线直连
			min = row1 < row2 ? row1 : row2;
			max = row1 < row2 ? row2 : row1;
			for (min++; min < max; min++) {
				if (numArr[min * cols + col1] != 0) {
					return false;
				}
			}
		} else { // 横线直连
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

	// 1.判断两个图标是否可以一折连
	public boolean match1(int row1, int col1, int row2, int col2) {

		// 以(x1,y1)和(x2,y2)位矩形对角，寻找矩形另外两对角线顶点
		int cola1 = col1;
		int rowa1 = row2;
		int cola2 = col2;
		int rowa2 = row1;

		// a1、a2若都不为空，则直接跳过
		if (numArr[rowa1 * cols + cola1] != 0 && numArr[rowa2 * cols + cola2] != 0) {
			return false;
		}
		if (numArr[rowa1 * cols + cola1] == 0) { // 判断a1-s1直连 a1-s2直连
			if (!(match0(rowa1, cola1, row1, col1) && match0(rowa1, cola1, row2, col2))) {
				return false;
			} else {
				rowa = rowa1;
				cola = cola1;

			}
		} else { // 判断a2-s1直连 a2-s2直连
			if (!(match0(rowa2, cola2, row1, col1) && match0(rowa2, cola2, row2, col2))) {
				return false;
			} else {
				rowa = rowa2;
				cola = cola2;
			}
		}
		return true;
	}

	// 2.判断两个图标是否可以二折连
	public boolean match2(int row1, int col1, int row2, int col2) {

		int rowtemp;
		int coltemp;

		// 分别向四个方向寻找拐点

		// s1的col+1
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

		// s1的col-1
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

		// s1的row+1
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

		// s1的row-1
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

	/* 两个图标间连线 */
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

	/* 相应处理选中的一对图标 */
	public void linkIcon(ActionEvent e) {
		ImageButton btn = (ImageButton) e.getSource();
		row[flag] = btn.getRow();
		col[flag] = btn.getCol();

		btn.setBorderPainted(true);
		btn.setBorder(new LineBorder(Color.BLACK, 3)); // 选中按钮设置线边框
		int n0 = row[0] * cols + col[0];
		int n1 = row[1] * cols + col[1];

		if (numArr[n0] == numArr[n1] && numArr[n0] != 0 && n0 != n1) { // 两个不同按钮上的图标相同
			if (match0(row[0], col[0], row[1], col[1]) || match1(row[0], col[0], row[1], col[1])
					|| match2(row[0], col[0], row[1], col[1])) {
				// 刷新时间
				if (leftTime > 58)
					leftTime = 60;
				else
					leftTime += 2;
				startThread();
				// 刷新按钮信息
				btnArr[row[0]][col[0]].setNum(0);
				btnArr[row[1]][col[1]].setNum(0);
				numArr[n0] = 0;
				numArr[n1] = 0; // 消除后图标标记为0
				btnArr[row[0]][col[0]].updateIcon();
				btnArr[row[1]][col[1]].updateIcon();
				sound.play(); // 消除音效
				score += 5;
				lblScore.setText(score + "");
				// 实现连线
				// 因为在线程run()中clear()，每隔一秒响应线程,此块不能与上面那块调换顺序,否则，连线在同一响应中立刻被消除!!!!!
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
				btnArr[row[(flag + 1) % 2]][col[(flag + 1) % 2]].setBorderPainted(false); // 取消之前的选中，总是选中刚刚的按钮
			}
		} else {
			btnArr[row[(flag + 1) % 2]][col[(flag + 1) % 2]].setBorderPainted(false); // 取消之前的选中，总是选中刚刚的按钮
		}

		flag = (flag + 1) % 2; // 0/1替换

		// 判断是否全部消除完
		if (checkEmpty()) {
			int leftTime = Integer.parseInt(lblLeftTime.getText());
			score = score + 20 * hintCount - 10 * refreshCount;
			lblScore.setText(score + "");
			int time = 60 - leftTime; // 本次游戏所用时间
			JOptionPane.showMessageDialog(this, "你赢啦!");// 第一个参数指消息框依赖的父亲组建，第二个参数指消息框中内容
			String name = JOptionPane.showInputDialog(this, "请输入昵称");
			if (name == null || "".equals(name.trim())) { // 不输入或输入空字符串
				name = "匿名";
			}
			hdb.insertInfo(name, time, score);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStart) {
			if (isRun) {
				int n = JOptionPane.showConfirmDialog(this, "您真的要重新开始游戏吗？");
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
				btnStart.setText("重新开始");
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
				if("提示 ×3".equals(btn.getText().trim()) || "提示 ×2".equals(btn.getText().trim()) || "提示 ×1".equals(btn.getText().trim())) {
					if(!hint()) {     //没有可消除的就刷新布局
						refreshNum();
						init();
					}
					hintCount--;
					btnHint.setText("提示 ×" + hintCount);
				}	
			}
		} else if (e.getSource() == btnOrder) {
			new TimeOrderDialog(this, true); // 弹出dialog时主窗体不可动
		} else if(e.getSource() == btnRule){
			JOptionPane.showMessageDialog(this,"最终得分 = 消除对数*5 + 剩余提示数*20 - 刷新局面次数*10");		
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
		setTitle("-油炸小汪肥的连连看-");

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

		JLabel lblLeftTimeN = new JLabel("剩余时间:");
		lblLeftTimeN.setFont(new Font("宋体", Font.BOLD, 18));
		lblLeftTimeN.setBounds(772, 193, 113, 27);
		contentPane.add(lblLeftTimeN);
		lblLeftTime.setFont(new Font("宋体", Font.PLAIN, 18));

		JLabel lblS = new JLabel("s");
		lblS.setFont(new Font("宋体", Font.PLAIN, 18));
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
		
		JLabel lblScoreN = new JLabel("游戏得分:");
		lblScoreN.setFont(new Font("宋体", Font.BOLD, 18));
		lblScoreN.setBounds(772, 252, 91, 18);
		contentPane.add(lblScoreN);
		
		lblScore.setFont(new Font("宋体", Font.PLAIN, 18));
		lblScore.setForeground(new Color(95, 158, 160));	
		lblScore.setBounds(887, 252, 72, 18);
		contentPane.add(lblScore);
		
		btnRule.setBackground(new Color(255, 218, 185));
		btnRule.setBounds(772, 561, 113, 27);
		contentPane.add(btnRule);
		btnRule.addActionListener(this);
		

		// 消除音效
		URL urlSound = this.getClass().getResource("/music/biu.wav"); // java一般不支持MP3文件
		sound = Applet.newAudioClip(urlSound); // 统一资源定位符

		setVisible(true);
	}
}
