//Bullets only move once
//HAS SOMETHING TO DO WITH RECENT CHANGES
   import javax.swing.*;
   import java.awt.*;
   import java.awt.event.*;
   import java.awt.image.*;
   import java.util.*;
   import java.awt.image.BufferStrategy.*;
   import java.applet.*;
   import java.io.*; 
   import java.util.*;
   import  sun.audio.*;    //import the sun.audio package
   import  java.io.*;
   //import javax.media.j3d.*; 
   //import com.sun.j3d.utils.timer.J3DTimer.*;
	
	
    public class FormulaGenerator extends JFrame
   {
      private static final int DEFAULT_FPS=30;
      private static final int WIDTH=1000;
      private static final int HEIGHT=1000;
   
      private  GraphPanel myGraphPanel;//Game Windows

   	//buttons for gameMenu
      private JPanel menu; //The menu panel
      private JButton quit; //Tells Game to quit
      private JButton restart; //Tells Game to restart
      private JButton pause;//Pauses game
      private JButton resume;//Resumes game
      private JButton help; //Gives Game Instructions
   	
      private JPanel scorePanel;//gives game score
      private JLabel PlayerOneScore;
   	
   
      private javax.swing.Timer ScoringTimer; //Gets Player One's score
      
   
   
       public static void main(String[] args)
      {
      
         long period=(long)1000/DEFAULT_FPS;
         new FormulaGenerator(period*1000000L);
      }
       public FormulaGenerator(long period)
      {
         
      
         super("The Great Space War");
      	
         makeGUI();
         pack();
         setResizable(true);
      	
         Container c=getContentPane();
      	
      	
      	
         myGraphPanel=new GraphPanel(this,period,WIDTH,HEIGHT);
         c.add(myGraphPanel,"Center");
         pack();
      	
         myGraphPanel.startGame();
      	
         setVisible(true);
      
      
      }//end of init()
   
       private  void makeGUI()
      {	
         Container c= getContentPane();
         c.setLayout(new BorderLayout());
      
         
      
      
         menu=new JPanel();
         menu.setLayout(new GridLayout(5,1));
        
      
         quit=new JButton("quit");
         menu.add(quit);
         quit.addActionListener(new QuitListener());
      
         pause=new JButton("pause");
         menu.add(pause);
         pause.addActionListener(new PauseListener());
      
         restart=new JButton("reset");
         menu.add(restart);
         restart.addActionListener(new RestartListener());
      
         resume=new JButton("resume");
         menu.add(resume);
         resume.addActionListener(new ResumeListener());
      
      
      
         c.add(menu,"East");
      	
      	
         scorePanel=new JPanel();
         scorePanel.setLayout(new GridLayout(1,1));
      	
         PlayerOneScore=new JLabel("1234123");
         scorePanel.add(PlayerOneScore);
      	

         c.add(scorePanel, "South");
      	
       
      
      }  // end of makeGUI()
   	
      
       private class QuitListener implements ActionListener//Listener for quit button
      {
          public void actionPerformed(ActionEvent e)
         {
            // myGameMenu=new GameMenu();
         	System.exit(1);
         	
         }
      }
   
       private class RestartListener implements ActionListener
      {
          public void actionPerformed(ActionEvent e)
         {
        	  myGraphPanel.restartGame();
           
         }
      }
   
       private class PauseListener implements ActionListener
      {
          public void actionPerformed(ActionEvent e)
         {
        	  myGraphPanel.pauseGame();
         }
      }
   
       private class ResumeListener implements ActionListener
      {
          public void actionPerformed(ActionEvent e)
         {
        	  myGraphPanel.resumeGame();
         }
      
      }
   
   
   
   
   // -------------------- applet life cycle methods --------------
   
       public void start()
      {  myGraphPanel.resumeGame();  }
   
       public void stop()
      {  myGraphPanel.pauseGame();  }
   
       public void destroy()
      {  myGraphPanel.stopGame();  }
   
   
   } // end of WormChaseApplet class
