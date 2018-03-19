package link.viewer;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton {
	
    private int row;          //所在行
    private int col;          //所在列
    private int num;          //图片编号
    
	public ImageButton(int row, int col, int num) {
		
		super();
		this.row = row;    
		this.col = col;    
		this.num = num;     
		updateIcon();
	}
	
	
	
	/*获得图标*/
	public void updateIcon() {
		this.setBorderPainted(false);   //按钮无边框

		String fileName = this.getClass().getResource("/images/" + num + ".jpg").getFile(); // 得到图标网址路径
		ImageIcon icon = new ImageIcon(fileName);
		this.setIcon(icon);
		if(num==0) {
			
			this.setBorder(BorderFactory.createLoweredBevelBorder());  //按钮凹陷
			this.setContentAreaFilled(false);  //按钮透明
			this.setIcon(null);   //按钮无图标
			
		   //this.setBorder(BorderFactory.createRaisedBevelBorder());   //按钮凸起
		}

	}
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}

    
}
