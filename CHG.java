import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class CHG extends JFrame{
	static CuckooComponent cuckoo;
	int position;
	int position2;
	
	public static int hashTableOne=1;
	public static int hashTableTwo=2;
	
	
	public CHG(int w,int h){
		super();
		setSize(w,h);
		setTitle("CuckooHashGUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(letThereBeButtons(),BorderLayout.NORTH);
		add(datCuckooPanel(),BorderLayout.CENTER);
		setVisible(true);
	}
	//rehash
	public void rehash(String function, boolean table1){
		for(int ii=0;ii<cuckoo.rehashStrings.length;ii++){
			cuckoo.rehashStrings[ii]=(table1)?cuckoo.tableStrings[ii]:cuckoo.table2Strings[ii];
		}
		
		cuckoo.repaint();
	}
	public class testListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			cuckoo.deleteRect[0]=4;
			cuckoo.repaint();
		}
	}
	/**
	 * Creates options bar (add/remove, text area)
	 * @return JPanel
	 */
	public JPanel letThereBeButtons(){
		JPanel panel = new JPanel();
		JButton addButton = new JButton("Add");
		JTextArea text = new JTextArea(1,10);
		JButton removeButton = new JButton("Remove");
		addButton.addActionListener(new heyListen(text));
		removeButton.addActionListener(new watchOut(text));
		panel.add(addButton);
		panel.add(text);
		panel.add(removeButton);
		
		JButton testButton = new JButton("TEST");
		testButton.addActionListener(new testListener());
		panel.add(testButton);
		
		return panel;
	}
	/**
	 * creates the bottom panel with tables and stuff
	 * @return JPanel
	 */
	public JPanel datCuckooPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);
		cuckoo=new CuckooComponent();
		panel.add(cuckoo);
		return panel;
	}
	/**
	 * listener for add, uses SongOfTime
	 * @author Justin
	 *
	 */
	private class heyListen implements ActionListener{
		JTextArea text;

		
		private heyListen(JTextArea text){
			this.text=text;
		}
		
		public void actionPerformed(ActionEvent e){
			cuckoo.deleteRect[0]=-1;
			position2=-1;
			position=CuckooHashUtils.hash(hashTableOne,Integer.parseInt(text.getText()));//testing
			System.out.println("PUT TO "+position);
			cuckoo.entryString=text.getText();
			cuckoo.messageString=CuckooHashUtils.showHash(text.getText(), hashTableOne, position);
		//	cuckoo.messageString= "Adding "+ text.getText();
			Timer timerX = new Timer(10,new SongOfTime(position));
			Timer timerY = new Timer(10,new SongOfTime(-1));
			timerX.start();
			timerY.start();
		}
	}
	/**
	 * listener for delete
	 * @author Justin
	 *
	 */
	private class watchOut implements ActionListener{
		JTextArea text;
		boolean removed;
		private watchOut(JTextArea text){
			this.text=text;
			removed = false;
		}
		public void actionPerformed(ActionEvent e){
			int index = CuckooHashUtils.hash(hashTableOne, Integer.parseInt(text.getText()));
			String key = text.getText().trim();
			if(cuckoo.tableStrings[index].equals(key)){
				cuckoo.deleteRect[0]=index;
				cuckoo.deleteRect[1]=1;
				cuckoo.messageString=CuckooHashUtils.showHash(text.getText(), 1, index);
				cuckoo.tableStrings[index]="";
			}
			else {
				index = CuckooHashUtils.hash(hashTableTwo, Integer.parseInt(text.getText()));
				if(cuckoo.table2Strings[index].equals(key)){
					cuckoo.deleteRect[0]=index;
					cuckoo.deleteRect[1]=2;
					cuckoo.messageString=CuckooHashUtils.showHash(text.getText(), 2, index);
					cuckoo.table2Strings[index]="";
				}
				else{
					String[] table=cuckoo.tableStrings;
					boolean table1=true;
					boolean notFound=true;
					int ii=0;
					while(notFound && ii<table.length){
						if(table[ii].equals(key)){
							notFound=false;
							if(table1){
								cuckoo.tableStrings[ii]="";
							}
							else{
								cuckoo.table2Strings[ii]="";
							}
							removed=true;
						}
						else if(ii==table.length-1 && table1){
							table1=false;
							table=cuckoo.table2Strings;
							ii=0;
						}
						else{
							ii++;
						}
					}
					if(!removed){
						cuckoo.messageString="That is not in the hashtable!";
					}
					else{
						cuckoo.messageString+="     Removed "+text.getText();
					}
				}
			}
			cuckoo.repaint();
		}
	}
	
	float yInc;//stop y bug
	int anim=0;//animation
	/**
	 * does initial add
	 * @author Justin
	 *
	 */
	public class SongOfTime implements ActionListener{
		int xf;
		int yf;
		float xInc;
		boolean x;
		
		public SongOfTime(int xf){
			if(xf>=0){
				this.xf=xf;
				xInc = ((float)((this.xf*cuckoo.BOX_SIZE+cuckoo.TABLE_X+5)-cuckoo.ENTRY_BOX_X)/cuckoo.SPEED);
				x=true;
			}
			else{
				yf=cuckoo.TABLE_Y;
				yInc=((float)(yf-cuckoo.ENTRY_BOX_Y)/cuckoo.SPEED);
				x=false;
			}
		}
		public void actionPerformed(ActionEvent e){
			if(x){
				cuckoo.cucco= (anim++ % 2 ==0) ? cuckoo.cucco1 : cuckoo.cucco2;//animation
				if(cuckoo.cuccoX>=(xf*cuckoo.BOX_SIZE+cuckoo.TABLE_X+5)){//if greater than the top table
					((Timer)e.getSource()).stop();
					String temp = cuckoo.entryString;
					if(!(cuckoo.tableStrings[xf].equals(""))){//collision
						cuckoo.messageString="Collision at "+position;
						System.out.println("COLLISION at " + position);
						cuckoo.entryString = cuckoo.tableStrings[xf];
						position2=CuckooHashUtils.hash(hashTableTwo,Integer.parseInt(cuckoo.entryString));
						cuckoo.messageString+="    "+CuckooHashUtils.showHash(cuckoo.entryString, hashTableTwo, position2);
					}
					cuckoo.tableStrings[xf]=temp;
					if(position2>=0){
						faroresWind(true);//calls collision 
					}
					else{//reset
						cuckoo.entryX=cuckoo.ENTRY_BOX_X+5;
						cuckoo.entryY=cuckoo.ENTRY_BOX_Y+30;
						cuckoo.cuccoX=cuckoo.ENTRY_BOX_X;
						cuckoo.cuccoY=cuckoo.ENTRY_BOX_Y;	
						cuckoo.entryString="";
					}
				}
				else{
					cuckoo.cuccoX+=xInc;
					cuckoo.entryX+=xInc;	
				}
			}
			else{//y
				if(cuckoo.cuccoY<=yf){
					((Timer)e.getSource()).stop();
				}
				else{
					if(cuckoo.cuccoX!=cuckoo.ENTRY_BOX_X){
						cuckoo.cuccoY+=yInc;
						cuckoo.entryY+=yInc;
					}
				}
			}
			cuckoo.repaint();
		}
	}
	/**
	 * collision function
	 * @param toTable2 which table to add to 
	 */
	public void faroresWind(boolean toTable2){
		/*
		 * xi = cuckoo.entryX
		 * xf = position2*cucko.BOX_SIZE+cuckoo.table_X+5
		 * yi = 75
		 * yf = 275 
		 */
		int xf=position2*cuckoo.BOX_SIZE+cuckoo.TABLE_X+5;
		float xInc = ((float)((xf)-cuckoo.entryX)/cuckoo.SPEED);
		float yInc = ((float)(cuckoo.TABLE2_Y-cuckoo.TABLE_Y)/(cuckoo.SPEED/2));
		Timer timer = new Timer(10,new FaroresWind(xf,xInc,yInc,true,toTable2));
		Timer timer2 = new Timer(10,new FaroresWind(xf,xInc,yInc,false,toTable2));
		timer.start();
		timer2.start();
		
	}
	/**
	 * listener for collision timer
	 * @author Justin
	 *
	 */
	public class FaroresWind implements ActionListener{
		int xf;
		float xInc;
		float yInc;
		boolean before;
		boolean x;
		boolean toTable2;
		
		public FaroresWind(int xf, float xInc, float yInc, boolean x, boolean toTable2){
			this.xf=xf;
			this.xInc=xInc;
			this.toTable2=toTable2;
			this.yInc=(toTable2)?yInc:-1*yInc;
			this.x=x;
			before = xf<cuckoo.cuccoX;
			if(x){
				System.out.println("POSITION 2: "+position2);
			}
		}
		public void actionPerformed(ActionEvent e){
			if(x && position!=position2){
				cuckoo.cucco= (anim++ % 2 ==0) ? cuckoo.cucco1 : cuckoo.cucco2;//animation
				if((!before && cuckoo.cuccoX>=xf)
						|| (before && cuckoo.cuccoX<=xf)){
					((Timer)e.getSource()).stop();
					String temp = cuckoo.entryString;
					cuckoo.messageString="Collision at "+position;
					if(toTable2){
						if(!(cuckoo.table2Strings[position2].equals(""))){
							cuckoo.entryString = cuckoo.table2Strings[position2];
							position=position2;
							System.out.print("Hello");
							position2=CuckooHashUtils.hash(hashTableTwo, Integer.parseInt(cuckoo.table2Strings[position2]));//position2
							cuckoo.messageString+="   "+CuckooHashUtils.showHash(cuckoo.entryString, hashTableTwo, position2);
						}
						else{
							position=position2;
							position2=-1;
							cuckoo.messageString="Put "+temp+" at " +position+" of table 2.";
						}
						cuckoo.table2Strings[position]=temp;
					}
					else{
						if(!(cuckoo.tableStrings[position2].equals(""))){
							cuckoo.entryString = cuckoo.tableStrings[position2];
							position=position2;
							System.out.print("HELLO");
							position2=CuckooHashUtils.hash(hashTableOne, Integer.parseInt(cuckoo.table2Strings[position2]));//position2
							cuckoo.messageString+="   "+CuckooHashUtils.showHash(cuckoo.entryString, hashTableOne, position2);
						}
						else{
							position=position2;
							position2=-1;
							cuckoo.messageString="Put "+temp+" at " +position+" of table 1.";
						}
						cuckoo.tableStrings[position]=temp;
					}//toTable2
					
					if(position2>=0){//calls collision again
						System.out.println("COLLISION AT "+position);
						faroresWind(cuckoo.cuccoY<175);
					}
					else{
						cuckoo.entryX=cuckoo.ENTRY_BOX_X+5;
						cuckoo.entryY=cuckoo.ENTRY_BOX_Y+30;
						cuckoo.cuccoX=cuckoo.ENTRY_BOX_X;
						cuckoo.cuccoY=cuckoo.ENTRY_BOX_Y;
						cuckoo.entryString="";
						cuckoo.repaint();
					}//position2>=0
						
				}
				else{
					cuckoo.cuccoX+=xInc;
					cuckoo.entryX+=xInc;
					cuckoo.repaint();
				}
			}
			else{
				if((cuckoo.cuccoY>=275 && toTable2) 
						|| (cuckoo.cuccoY<=75 && !toTable2)){
					((Timer)e.getSource()).stop();
					
					if(x && position==position2){//things got funky here so this is to fix bugs
						String temp = cuckoo.entryString;
						if(toTable2){
							if(!(cuckoo.table2Strings[position].equals(""))){
								cuckoo.entryString=cuckoo.table2Strings[position];
								position=position2;
								System.out.print("C");
								position2=CuckooHashUtils.hash(hashTableTwo, Integer.parseInt(cuckoo.entryString));//position2
							}
							else{
								position=position2;
								position2=-1;
								cuckoo.messageString="Put "+temp+" at " +position+" of table 2.";
							}
							cuckoo.table2Strings[position]=temp;
						}
						else{
							if(!(cuckoo.tableStrings[position].equals(""))){
								cuckoo.entryString=cuckoo.tableStrings[position];
								position=position2;
								System.out.print("D");
								position2=CuckooHashUtils.hash(hashTableOne, Integer.parseInt(cuckoo.entryString));//position2
							}
							else{
								position=position2;
								position2=-1;
								cuckoo.messageString="Put "+temp+" at " +position+" of table 1.";
							}
							cuckoo.tableStrings[position]=temp;
						}
						if(position2>=0){
							faroresWind(cuckoo.cuccoY<175);
						}
						else{
							cuckoo.entryX=cuckoo.ENTRY_BOX_X+5;
							cuckoo.entryY=cuckoo.ENTRY_BOX_Y+30;
							cuckoo.cuccoX=cuckoo.ENTRY_BOX_X;
							cuckoo.cuccoY=cuckoo.ENTRY_BOX_Y;
							cuckoo.entryString="";
							cuckoo.repaint();
						}
					}
				}
				else{
					cuckoo.cuccoY+=yInc;
					cuckoo.entryY+=yInc;
					cuckoo.repaint();
				}
			}
		}	
		
	}

}