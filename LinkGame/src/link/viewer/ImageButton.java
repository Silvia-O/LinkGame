package link.viewer;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton {
	
    private int row;          //������
    private int col;          //������
    private int num;          //ͼƬ���
    
	public ImageButton(int row, int col, int num) {
		
		super();
		this.row = row;    
		this.col = col;    
		this.num = num;     
		updateIcon();
	}
	
	
	
	/*���ͼ��*/
	public void updateIcon() {
		this.setBorderPainted(false);   //��ť�ޱ߿�

		String fileName = this.getClass().getResource("/images/" + num + ".jpg").getFile(); // �õ�ͼ����ַ·��
		ImageIcon icon = new ImageIcon(fileName);
		this.setIcon(icon);
		if(num==0) {
			
			this.setBorder(BorderFactory.createLoweredBevelBorder());  //��ť����
			this.setContentAreaFilled(false);  //��ť͸��
			this.setIcon(null);   //��ť��ͼ��
			
		   //this.setBorder(BorderFactory.createRaisedBevelBorder());   //��ť͹��
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
