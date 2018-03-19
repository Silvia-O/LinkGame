package link.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import link.dao.HandleDB;
import link.entity.TimeOrder;

public class TimeOrderDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	
	
	public TimeOrderDialog(MainFrame parent,boolean isShow) {
		super(parent,isShow);    //主窗体是否可操作
		
		this.setTitle("显示排行榜");
		setBounds(100, 100, 450, 300);
		this.setResizable(false);
		this.setLocationRelativeTo(this);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel label = new JLabel("\u6392\u884C\u699C");
		label.setFont(new Font("宋体", Font.PLAIN, 18));
		label.setBounds(178, 13, 82, 30);
		contentPanel.add(label);
		
		JTextArea taResult = new JTextArea();
		taResult.setBackground(Color.LIGHT_GRAY);
		taResult.setEditable(false);
		taResult.setBounds(40, 40, 352, 163);
		contentPanel.add(taResult);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		HandleDB hdb = new HandleDB();
		ArrayList<TimeOrder> al = hdb.selectInfo();
		if(al.size()==0) {
			taResult.setText("暂时没有游戏记录");
		}else {
			taResult.append("名次\t姓名\t所用时间\t最终得分\r\n\r\n");
			for(int i=0; i<al.size(); i++) {
				TimeOrder to = al.get(i);
				taResult.append("第"+(i+1)+"名\t" + to.getName() + "\t" + to.getTime() + "\t" + to.getScore() +  "\r\n");
			}
		}
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
}
