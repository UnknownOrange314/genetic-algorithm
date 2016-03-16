// WormPanel.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* The game's drawing surface. It shows:
     - the moving worm
     - the obstacles (blue boxes)
     - the current average FPS and UPS
*/

   import javax.swing.*;
   import java.awt.event.*;
   import java.awt.*;
   import java.text.DecimalFormat;
    
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

   //import com.sun.j3d.utils.timer.J3DTimer;



    public class    GraphPanel extends JPanel implements Runnable
   {
       //Menu and title objects
       
      JLabel title; //Shows title of game
       
       
      JLabel PlayerOneScore;
               
      //public GameMenu myMenu;    //Buttons for menu
      JPanel top,menuPanel, scorePanel; //Top is for title of game
                                                   //menuPanel is for the menu
                                                   //ScorePanel is for the score
       
       //game variables    
      private int BulletNumber=0;
       

       
       //Graphics variables
      public BufferedImage myImage;
      public Graphics myBuffer1,myBuffer2;
      private static final int N=400;//size of panel
      private static final Color BACKGROUND = new Color(204, 204, 204);
      private static final int RADIUS=25;//Radius of ship
      public Graphics g;
     
      public int GameScores=0;//Total score of player one and two in game
      
  

        
       //Player movement threads
       
       //Two Players


       

       
     
       

       
       //Array for players bullets
      int BulletNum=1;//Number of bullets
      int BulletAddNum=0;
      int XPos=1;
      int YPos=2;
      int XSpeed=3;
       
      int YSpeed=4;
       //int Direction=5;
      int Size=5;
      int delete=6;
      int PlayerShot=7;
      private double[][]PlayerBullets=new double[100][7];
      int EnemyShot=7;
       
       //Size of bullets
      final int BULLET_SIZE=10;
       
       //Speed of bullets
      final int    BULLET_X_SPEED=10;
      final int BULLET_Y_SPEED=10;
       
       
   
       
   
      private static long MAX_STATS_INTERVAL = 1000000000L;
   // private static long MAX_STATS_INTERVAL = 1000L;
    // record stats every 1 second (roughly)
   
      private static final int NO_DELAYS_PER_YIELD = 16;
   /* Number of frames with a delay of 0 ms before the animation thread yields
     to other running threads. */
   
      private static int MAX_FRAME_SKIPS = 5;   // was 2;
    // no. of frames that can be skipped in any one animation loop
    // i.e the games state is updated but not rendered
   
      private static int NUM_FPS = 10;
     // number of FPS values stored to get an average
   
   
   // used for gathering statistics
      private long statsInterval = 0L;    // in ns
      private long prevStatsTime;   
      private long totalElapsedTime = 0L;
      private long gameStartTime;
      private int timeSpentInGame = 0;    // in seconds
   
      private long frameCount = 0;
      private double fpsStore[];
      private long statsCount = 0;
      private double averageFPS = 0.0;
   
      private long framesSkipped = 0L;
      private long totalFramesSkipped = 0L;
      private double upsStore[];
      private double averageUPS = 0.0;
   
   
      private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
      private DecimalFormat timedf = new DecimalFormat("0.####");  // 4 dp
   
   
      private Thread animator;           // the thread that performs the animation
      private volatile boolean running = false;   // used to stop the animation thread
      private volatile boolean isPaused = false;
   
      private long period;                // period between drawing in _nanosecs_
   
      private FormulaGenerator spTop;
   
   
   
   // used at game termination
      private volatile boolean gameOver = false;
      private int score = 0;
      private Font font;
      private FontMetrics metrics;
      private boolean finishedOff = false;
   
   // off screen rendering
      private Graphics dbg; 
      private Image dbImage = null;
        
    //size of panel
        private int PWIDTH;
        private int PHEIGHT;
        
    //these variables represent the bounds of the playing area
        private static final int MIN_X_POS=50;
        private static final int MAX_X_POS=450;
        private static final int MIN_Y_POS=50;
        private static final int MAX_Y_POS=450;
   
   
        //Genetic Algorithm Variables
        public static HashSet<Chromosome> population;
        public static double targetNumber=5;

        
        public static int POPULATION_SIZE=20;
        public static double MUTATION_RATE=0.01;
         
        public static Double[] fitnessRates;//stores the average fitness rate to see if it is improving
        public static final int SAVE_NUM=100;//number of generations that will be stored
        public static ArrayList<Double> fitnessTrend; //Trend of fitness over time
        public static ArrayList<Double> sDevTrend; //Trend of standardDevation over time
        public static ArrayList<Double> changeTrend; //Trend of averageChange over time
        public static ArrayList<Double> averageFitnessTrend;//Trend of averageFitness
        
       public    GraphPanel(FormulaGenerator mySpaceshipPanel, long period,int w,int h)
      {
        
           //Initialize GA variables
            fitnessTrend=new ArrayList<Double>();
            averageFitnessTrend=new ArrayList<Double>();
            sDevTrend=new ArrayList<Double>();
            changeTrend=new ArrayList<Double>();
            fitnessRates=new Double[SAVE_NUM];
            for(int i=0;i<SAVE_NUM;i++)
            {
                fitnessRates[i]=0.0;
            }
            population=new HashSet<Chromosome>();
            generatePopulation(POPULATION_SIZE);
            
            
            PWIDTH=w;
            PHEIGHT=h;
         spTop = mySpaceshipPanel;
         this.period = period;
          
         setBackground(Color.white);
         setPreferredSize( new Dimension(PWIDTH,PHEIGHT));
            
            setLayout(new BorderLayout());
      
      // create game components
         top=new JPanel();
         top.setLayout(new GridLayout(1,1));
         add(top, BorderLayout.NORTH);
          
         title=new JLabel("The ultimate battle");//Game Title
         top.add(title);
          
         scorePanel=new JPanel();
         scorePanel.setLayout(new GridLayout(1,1));
            PlayerOneScore=new JLabel("");//Score for playerOne
         scorePanel.add(PlayerOneScore);
            
            
         add(scorePanel, BorderLayout.SOUTH);
          
      
         //ImageIcon Instructions=new ImageIcon("Instructions.JPG");
            //g.drawImage(Instructions.getImage(),0,0,1000,300,null);
        
      
          
          
      

         

      
        // addKeyListener(new Player2Key());
         setFocusable(true);
          
      }  // end of WormPanel()
   
   
   // ------------- game life cycle methods ------------
   // called from the applet's life cycle methods
   
       public void startGame()
      // initialise and start the thread 
      { 
         if (animator == null || !running) {
            animator = new Thread(this);
            animator.start();
         }
      } // end of init()
    
   
       public void resumeGame()
      // start game /resume a paused game
      {  isPaused = false;  
                
        } 
   
   
       public  void pauseGame()
      {
          isPaused = true;    
         
             
         }
   
   
       public void stopGame() 
      // stop the thread by flag setting
      { running = false;  
         finishOff();
      }
   
   // ----------------------------------------------
   
   
   
   
       public void run()
      /* The frames of the animation are drawn inside the while loop. */
      {
         long beforeTime, afterTime, timeDiff, sleepTime;
         long overSleepTime = 0L;
         int noDelays = 0;
         long excess = 0L;
      
         gameStartTime =System.nanoTime();
         prevStatsTime = gameStartTime;
         beforeTime = gameStartTime;
      
         running = true;
      
         while(running) {
            
                
                
                if(!isPaused)
                {
            gameUpdate(); 
            gameRender();   // render the game to a buffer
            paintScreen();  // draw the buffer on-screen
         }
            afterTime =System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;  
         
            if (sleepTime > 0) {   // some time left in this cycle
               try {
                  Thread.sleep(sleepTime/1000000L);  // nano -> ms
               }
                   catch(InterruptedException ex){}
               overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            }
            else {    // sleepTime <= 0; the frame took longer than the period
               excess -= sleepTime;  // store excess time value
               overSleepTime = 0L;
            
               if (++noDelays >= NO_DELAYS_PER_YIELD) {
                  Thread.yield();   // give another thread a chance to run
                  noDelays = 0;
               }
            }
         
            beforeTime =System.nanoTime();
         
         /* If frame animation is taking too long, update the game state
         without rendering it, to get the updates/sec nearer to
         the required FPS. */
            int skips = 0;
            while((excess > period) && (skips < MAX_FRAME_SKIPS)) {
               excess -= period;
               gameUpdate();    // update state but don't render
               skips++;
            }
            framesSkipped += skips;
         
         }
         finishOff();
      } // end of run()
   
   
   
       private void gameUpdate() 
      { 
         if (!isPaused && !gameOver)
            {
                double fitness=removeFailures();
                int generations=changeTrend.size();
                fitnessRates[generations%100]=fitness;

                crossover();
                mutations(generations);
                generations++;
            
                findBest();
                
                System.out.println(fitness);
            }
          //fred.move()
      }  // end of gameUpdate()
       
       public static double removeFailures()//make sure it removed the bad ones
    {
        double totalFitness=0;
        int totalSize=0;
        for(Chromosome c: population)
        {
            totalSize=totalSize+c.getExpressionSize();
            totalFitness=totalFitness+c.getFitness();
        }
        double averageFitness=totalFitness/population.size();
        Iterator removeIterator=population.iterator(); //something is wrong here because sometimes all the chromosomes are removed
        while(removeIterator.hasNext()&population.size()>5)
        {
            Chromosome c=(Chromosome)(removeIterator.next());
            if(c.getFitness()<averageFitness)
            {
                removeIterator.remove();
            }
        
        }
        averageFitnessTrend.add(averageFitness);
        return averageFitness;
        
        
    }
       public static void  crossover()
    {
        double totalFitness=0;
        HashSet<Chromosome> newPopulation=new HashSet<Chromosome>();
        while(newPopulation.size()<POPULATION_SIZE)
        {
            Chromosome A=pickRandomExpression(population);
            ArrayList<String> expressionA=A.getExpression();
            
            Chromosome B=pickRandomExpression(population);
            ArrayList<String> expressionB=B.getExpression();
            
            
            int crossPoint=(int)(Math.random()*expressionA.size()-1);
            ArrayList<String> newA=new ArrayList<String>();
            ArrayList<String> newB=new ArrayList<String>();
            
            for(int y=0;y<expressionA.size();y++)
            {
                
                if(y<crossPoint)
                {
                    newA.add(expressionB.get(y));
                    newB.add(expressionA.get(y));
                }
                else
                {
                    newB.add(expressionB.get(y));
                    newA.add(expressionA.get(y));
                    
                }
    

            }
            
            
            
         
             newPopulation.add(new Chromosome(newA));
             
             
            
        }
        population=newPopulation;
    
    
    
        
    }
    public static Chromosome pickRandomExpression(HashSet<Chromosome> s)
    {
        int index=(int)(Math.random()*s.size());
        int t=0;
    
        for(Chromosome c: s)
        {
            if(t==index)
            {
                
                return c;
            }
            t++;
        }
        System.out.println("there is a problem");
        System.out.println(index);
        
        return null;
    }
       public static void mutations(int generations)//mutation rate will be low in the beginning
    { 
        //see if the population is stagnant
        
        
        double sDev=standardDev(population);
        fitnessTrend.add(sDev);
        double currentFitness=fitnessRates[generations%SAVE_NUM];
        double totalFitness=0;
        for(int x=0;x<fitnessRates.length;x++)
        {
            totalFitness=totalFitness+fitnessRates[x];
        }
        double avgFitness=totalFitness/SAVE_NUM;
        double difference=Math.abs(totalFitness/(avgFitness+0.00000001));
        changeTrend.add(difference);
        double mutationRate=0.1/(difference*sDev+0.00000001);
        if (mutationRate>0.1)
        {
            mutationRate=0.1;
        }
        
        for(Chromosome c: population)
        {
            c.mutate(mutationRate);
            
            
            
        }
    }
       public static void findBest()
    {
        Chromosome max=null;
        double maxVal=-1.0;
        for(Chromosome c: population)
        {
            if(c.getFitness()>maxVal)
            {
                max=c;
                maxVal=c.getFitness();
                System.out.println(c.getFitness());
            }
        }
        fitnessTrend.add(maxVal);
        //System.out.println(maxVal);
    
    }
       private void gameRender()
      {
         if (dbImage == null){
            dbImage = createImage(PWIDTH, PHEIGHT);
            if (dbImage == null) {
               System.out.println("dbImage is null");
               return;
            }
            else
               dbg = dbImage.getGraphics();
         }
      
      // clear the background
         dbg.setColor(Color.white);
         dbg.fillRect (0, 0, PWIDTH, PHEIGHT);
         dbg.setColor(Color.RED);
        
         //dbg.fillRect(0,0,800,800);
         drawGraph(fitnessTrend,1,dbg);
         
         dbg.setColor(Color.BLUE);
         drawGraph(sDevTrend,2,dbg);
         dbg.setColor(Color.GREEN);
         drawGraph(changeTrend,3,dbg);
         dbg.setColor(Color.GRAY);
         drawGraph(averageFitnessTrend,4,dbg);
         
         dbg.setFont(font);
      
      // report frame count & average FPS and UPS at top left
      // dbg.drawString("Frame Count " + frameCount, 10, 25);
         dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", " +
                                df.format(averageUPS), 20, 25);  // was (10,55)
      
         dbg.setColor(Color.black);
      
      // draw game elements: the obstacles and the worm

          

 

   
         //Bullets.setText("Bullets"+myBullets.size());
          


          
      
      
         if (gameOver)
            gameOverMessage(dbg);
      }  // end of gameRender()
      private void drawGraph(ArrayList<Double> c,int quadrant,Graphics dbg)
      {
          if(c.size()==0)
          {
              return;
          }
          int saveSize=10;
          int addX=0;
          int addY=0;
          if(quadrant==1)
          {
              addX=0;
              addY=0;
          }
          if(quadrant==2)
          {
              addX=500;
              addY=0;
          }
          if(quadrant==3)
          {
              addX=0;
              addY=500;
          }
          if(quadrant==4)
          {
              addX=500;
              addY=500;
          }
          double interval=500.0/((double)c.size());
          double maxVal=-1.0;
          for(double data: c)
          {
            if(data>maxVal)
            {
                maxVal=data;
            }
          }
          
          double index=0.0;
          int count=0;
          
          for(int x=0;x<500;x++)
          {
              int drawIndex=x*c.size()/500;
              double total=0.0;
              for(int i=drawIndex;i>drawIndex-saveSize;i--)
              {
                 if(i<0)
                 {
                     break;
                 }
                 total=total+c.get(i); 
              }
              total=total/saveSize;
              
              
              int xPos=x;
              int yPos=(int)(500-500*total/maxVal);
              dbg.drawRect(xPos+addX,yPos+addY, 1, 1);
              
            
              
          }
          
         
          
      }
   
   
       private void gameOverMessage(Graphics g)
      // center the game-over message in the panel
      {
         String msg = "Game Over. Your Score: " + score;
         int x = (PWIDTH - metrics.stringWidth(msg))/2; 
         int y = (PHEIGHT - metrics.getHeight())/2;
         g.setColor(Color.red);
         g.setFont(font);
         g.drawString(msg, x, y);
      }  // end of gameOverMessage()
   
   
       private void paintScreen()
      // use active rendering to put the buffered image on-screen
      { 
         Graphics g;
         try {
            g = this.getGraphics();
            if ((g != null) && (dbImage != null))
               g.drawImage(dbImage, 0, 0, null);
            Toolkit.getDefaultToolkit().sync();  // sync the display on some systems
            g.dispose();
         }
             catch (Exception e)   // quite commonly seen at applet destruction
            { System.out.println("Graphics Context error: " + e);  }
      } // end of paintScreen()
   
       
   
   
       private void finishOff()
      /* Tasks to do before terminating. Called at end of run()
      and via applet's destroy() calling stopGame().
      
      The call at the end of run() is not really necessary, but
      included for safety. The flag stops the code being called
      twice.
      */
      { 
         if (!finishedOff) {
            finishedOff = true;
         
         }
      } // end of finishedOff()
   
   
   
       
            
                  
       
       
       
      
   

  
       
        
       public static void HighScore(int score)
      {
          
         Scanner infile;
          
          
         String[] HighScoreData=new String[10];
          
         try
         {
            infile=new Scanner(new File("HighScores.txt"));
             
             
             
            /*for(int x=0;x<10;x++)
            {
               System.out.println(""+x);
               HighScoreData[x]=infile.nextLine();
            }
             */
            for(int x=0;x<10;x++)
            {
                    HighScoreData[x]=infile.nextLine();
                    
               int pos=HighScoreData[x].indexOf(":");
               int temp=Integer.parseInt(HighScoreData[x].substring(pos+1));
                
               if(score>temp)
               {
                  String name =JOptionPane.showInputDialog("What is your name");
                   
                  
                   
                  System.out.println(HighScoreData[x]);
                   
                  
                  for(int y=x+1;y<10;y++)
                  {
                     HighScoreData[y]=infile.nextLine();
                            System.out.println(y);
                  }
                        HighScoreData[x]="#"+(x)+" "+name+" :"+score;
                        break;
               }
            }
             
            System.setOut(new PrintStream(new FileOutputStream("HighScores.txt")));
             
            for(int x=0;x<10;x++)
            {
               System.out.println(HighScoreData[x]);
            }
            
         }
         
         
             catch(Exception e)
            {
               e.printStackTrace();
               System.out.println("Error");
               System.exit(1);
            }
              
              
         
         System.exit(1);
      
      }
       
        public static int getLeftBorder()
        {
            return MIN_X_POS;
        }
        
        public static int getRightBorder()
        {
            return MAX_X_POS;
        }
        
        public static int getTopBorder()
        {
            return MIN_Y_POS;
        }
        
        public static int getBottomBorder()    
        {
            return MAX_Y_POS;
        }
        
        public static void generatePopulation(int popSize)
        {
            
            for(int x=0;x<popSize;x++)
            {
                
                
                
            
                Chromosome bob=new Chromosome(null);
                bob.getFitness();
                population.add(bob);
            
                
            
                
                
        
                //make sure that expressions are not being deleted
            }
        
            
        }
        
        
        public void restartGame()
        {


       
  
        
        }
    
        public static double standardDev(HashSet<Chromosome> population)
        {
        
            
            HashMap<Chromosome,ArrayList<Double>> data=new HashMap<Chromosome,ArrayList<Double>>();
            ArrayList<Double> average=new ArrayList<Double>();
            for(int i=0;i<population.size();i++)
            {
                average.add(0.0);
            }
            double totalFitness=0.0;
            for(Chromosome c: population)
            {
                ArrayList<String> str=c.getExpression();
                ArrayList<Double> d=new ArrayList<Double>();
                for(int x=0;x<str.size();x++)
                {
                    
                    if(x%2==0)
                    {
                        double y=Double.parseDouble(str.get(x));
                        d.add(y);
                        average.set(x,average.get(x)+y);
                    }
                    else 
                    {
                        String q=str.get(x);
                        if(q=="+")
                        {
                            d.add(7.0);
                            average.set(x,average.get(x)+7.0);
                        }
                        else if(q=="-")
                        {
                            d.add(3.0);
                            average.set(x,average.get(x)+3.0);
                            
                        }
                        else if(q=="*")
                        {
                            d.add(9.0);
                            average.set(x,average.get(x)+9.0);
                        }
                        else if(q=="/")
                        {
                            d.add(1.0);
                            average.set(x,average.get(x)+1.0);
                        }
                            
                    }
                }
                data.put(c,d);
                
            }
        
            double totalDistance=0;
            for(Chromosome c: data.keySet())
            {
                ArrayList<Double> location=data.get(c);
                
                double myDist=0;
                for(int x=0;x<location.size();x++)
                {
                    myDist=myDist+Math.abs(location.get(x)-average.get(x));
                }
					 totalDistance=totalDistance+myDist;
            }
                
        
            double sDev=Math.log(totalDistance);
            
            return sDev;
        }
   
   }  