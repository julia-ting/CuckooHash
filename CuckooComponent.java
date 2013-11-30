import javax.imageio.ImageIO;
import javax.swing.JComponent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;

public class CuckooComponent extends JComponent{
	
	BufferedImage cucco;
	BufferedImage cucco1;
	BufferedImage cucco2;
	float cuccoX;
	float cuccoY;
	
	Rectangle[] table;
	Rectangle[] table2;
	Rectangle entry;
	int size;
	
	String entryString="";
	float entryX;
	float entryY;
	
	static String messageString="";
	
	String[] tableStrings;
	String[] table2Strings;
	String[] rehashStrings;
	String[] rehashLocationStrings;
	
	Line2D.Double tableArrow;
	Line2D.Double table2Arrow;
	
	int ENTRY_BOX_X=50;
	int ENTRY_BOX_Y=175;
	int TABLE_X=200;
	int TABLE_Y=75;
	int TABLE2_Y=275;
	int BOX_SIZE=50;
	int TABLE_SIZE=10;
	
	int SPEED=120;

	int[] deleteRect;
	public CuckooComponent(){
		table=new Rectangle[TABLE_SIZE];
		table2=new Rectangle[TABLE_SIZE];
		tableStrings = new String[TABLE_SIZE];
		table2Strings = new String[TABLE_SIZE];
		rehashStrings = new String[TABLE_SIZE];
		rehashLocationStrings = new String[TABLE_SIZE];
		for(int ii=0;ii<TABLE_SIZE;ii++){
			table[ii]=new Rectangle(ii*BOX_SIZE+TABLE_X,TABLE_Y,BOX_SIZE,BOX_SIZE);
			table2[ii]=new Rectangle(ii*BOX_SIZE+TABLE_X,TABLE2_Y,BOX_SIZE,BOX_SIZE);
			tableStrings[ii]="";
			table2Strings[ii]="";
			rehashStrings[ii]="";
			rehashLocationStrings[ii]="";
		}
		entry = new Rectangle(ENTRY_BOX_X,ENTRY_BOX_Y,BOX_SIZE,BOX_SIZE);
		tableArrow=new Line2D.Double(100,175,200,99);
		table2Arrow=new Line2D.Double(100,215,200,255);
		size=0;
		entryX=ENTRY_BOX_X+5;
		entryY=ENTRY_BOX_Y+30;
		deleteRect=new int[2];
		deleteRect[0]=-1;
		deleteRect[1]=1;
		try{
		cucco1=ImageIO.read(new File("cucco1.jpg"));
		cucco2=ImageIO.read(new File("cucco2.jpg"));
		cucco=cucco1;
		cuccoX=50;
		cuccoY=175;
		}catch(IOException e){System.out.print("FILE DOESN'T EXIST YA DUMB");}
	}
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.draw(entry);
		for(int ii=0;ii<TABLE_SIZE;ii++){
			g2.draw(table[ii]);
			g2.draw(table2[ii]);
			g2.drawString(tableStrings[ii],ii*BOX_SIZE+TABLE_X+5,TABLE_Y+30);
			g2.drawString(table2Strings[ii],ii*BOX_SIZE+TABLE_X+5,TABLE2_Y+30);
			g2.drawString(rehashStrings[ii], ii*BOX_SIZE+TABLE_X+5, 175);
			g2.drawString(rehashLocationStrings[ii],ii*BOX_SIZE+TABLE_X+5,200);
		}
		g2.drawString(entryString,entryX,entryY);
		g2.drawString(messageString,100,35);
		g2.drawImage(cucco,(int)cuccoX,(int)cuccoY,20,20,null);
		if(deleteRect[0]>-1){
			g2.setColor(Color.RED);
			g2.drawRect(deleteRect[0]*BOX_SIZE+TABLE_X, (deleteRect[1]==1)?75:275, BOX_SIZE, BOX_SIZE);
		}
	}
	
	private class Arrow{
		Graphics2D g;
		public Arrow(Graphics2D g){
			this.g=g;
		}
		public void draw(int xi, int yi, int xf, int yf){
			
		}
	}
}