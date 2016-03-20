
import javax.swing._
import java.awt.event._
import java.awt._
import java.text.DecimalFormat
  
import javax.swing._
import java.awt._
import java.awt.event._
import java.awt.image._
import java.awt.image.BufferStrategy._
import java.applet._
import java.io._
import java.util._
import  sun.audio._   
import  java.io._
import scala.collection.mutable.HashSet

import scala.collection.mutable.ArrayBuffer


/**
 * This class contains code for generating visualizations of the genetic algorithm. 
 * The graphics rendering code is a modified version of code taken from Killer Game Programming in Java
 */
class GraphPanel(mySpaceshipPanel:FormulaGenerator, pd:Long, w:Int, h:Int) extends JPanel with Runnable{
  
    var fitnessRates = new ArrayBuffer[Double] //Trend of fitness over time.
    var sDevTrend= new ArrayBuffer[Double]  //Trend of standardDevation over time.
    var changeTrend= new ArrayBuffer[Double]; //Trend of averageChange over time.
    var averageFitnessTrend= new ArrayBuffer[Double];//Trend of averageFitness.
    
    val SAVE_NUM = 100
    var i = 0 
    for(int <-0 until SAVE_NUM){
        fitnessRates(i)=0.0;
    }
    
    var population = new HashSet[Chromosome]

    generatePopulation(POPULATION_SIZE);
            
            
    PWIDTH=w;
    PHEIGHT=h;
    spTop = mySpaceshipPanel;
    period = pd;
          
    setBackground(Color.white);
    setPreferredSize( new Dimension(PWIDTH,PHEIGHT));
            
    setLayout(new BorderLayout());
      
      // create game components
    var top=new JPanel();
    top.setLayout(new GridLayout(1,1));
    add(top, BorderLayout.NORTH);
      
    title=new JLabel("The ultimate battle");//Game Title
    top.add(title);
      
    var scorePanel=new JPanel();
    scorePanel.setLayout(new GridLayout(1,1));
    var PlayerOneScore=new JLabel("");//Score for playerOne
    scorePanel.add(PlayerOneScore);
        
        
    add(scorePanel, BorderLayout.SOUTH);
    setFocusable(true);
         
    //Menu and title objects
   
    var title:JLabel //Shows title of game.
    var playerOneScore:JLabel
             
    //public GameMenu myMenu;    //Buttons for menu
    var menuPanel:JLabel
      
     //game variables    
    var BulletNumber =0
   
  
    var myImage:BufferedImage
    var myBuffer1:Graphics
    var myBuffer2:Graphics
    
    val N = 400
    var BACKGROUND = new Color(204,204,204)
    var RADIUS = 25
    
    var g:Graphics
    
    var gameScores = 0
 

    val NO_DELAYS_PER_YIELD = 16
   /* Number of frames with a delay of 0 ms before the animation thread yields
   to other running threads. */
 
    val MAX_FRAME_SKIPS = 5  // was 2;
    // no. of frames that can be skipped in any one animation loop
    // i.e the games state is updated but not rendered
 
    val NUM_FPS = 10
   // number of FPS values stored to get an average
 
 
    var animator:Thread
    @volatile var running:Boolean = false
    @volatile var isPaused:Boolean = false
   
    var period:Long
    var spTop:FormulaGenerator
      
   // used at game termination
    @volatile var gameOver:Boolean = false
    var score:Int = 0
    var font:Font
    var metrics:FontMetrics
    var finishedOff:Boolean = false
    
    var dbg:Graphics
    var dbImage:Image
    
    var PWIDTH:Int
    var PHEIGHT:Int

  
    //Genetic Algorithm Variables
    var targetNumber = 5
    
    val POPULATION_SIZE = 20
    val MUTATION_RATE=0.01

    //these variables represent the bounds of the playing area
    var MIN_X_POS=50
    var MAX_X_POS=450
    var MIN_Y_POS=50
    var MAX_Y_POS=450
    
    
    def startGame(){ 
      if (animator == null || !running) {
            animator = new Thread(this)
            animator.start()
         }
      } // end of init()
    
   
    def  resumeGame(){  
        isPaused = false          
    } 
    
    def pauseGame(){
        isPaused = true
    }

    def stopGame(){
        running = false
        finishOff()
    }

    def run(){
      
       var  beforeTime = 0L
       var afterTime = 0L
       var timeDiff = 0L
       var sleepTime = 0L
       var overSleepTime = 0L
       var noDelays = 0
       var excess = 0L
      
       var gameStartTime =System.nanoTime()
       var prevStatsTime = gameStartTime
       beforeTime = gameStartTime
      
       running = true;
       while(running) {
          if(!isPaused){
            gameUpdate() 
            gameRender()  // render the game to a buffer
            paintScreen()  // draw the buffer on-screen
          }
          afterTime =System.nanoTime()
          timeDiff = afterTime - beforeTime
          sleepTime = (period - timeDiff) - overSleepTime
         
          if (sleepTime > 0) {   // some time left in this cycle
             try {
                Thread.sleep(sleepTime/1000000L);  // nano -> ms
             }
             catch(ex:InterruptedException){}
               overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
             }
             else {    // sleepTime <= 0; the frame took longer than the period
               excess -= sleepTime  // store excess time value
               overSleepTime = 0L
            
               if (++noDelays >= NO_DELAYS_PER_YIELD) {
                  Thread.yield()   // give another thread a chance to run
                  noDelays = 0
               }
            }
         
            beforeTime =System.nanoTime()
         
           /* If frame animation is taking too long, update the game state
           without rendering it, to get the updates/sec nearer to
           the required FPS. */
            var skips = 0
            while((excess > period) && (skips < MAX_FRAME_SKIPS)) {
               excess -= period
               gameUpdate()    // update state but don't render
               skips+=1
            } 
         }
         finishOff()
      } // end of run()

    def gameUpdate() { 
        if (!isPaused && !gameOver){
            var fitness=removeFailures()
            var generations=changeTrend.size
            fitnessRates(generations%100)=fitness

            crossover()
            mutations(generations)
            generations+=1
      
            findBest()
          
            System.out.println(fitness)
        }
    }
 
    def removeFailures():Double = {
        var totalFitness = 0.0
        var totalSize=0
        
        for(c:Chromosome <- population){
            totalSize=totalSize+c.getExpressionSize();
            totalFitness=totalFitness+c.getFitness();
        }
        
        var  averageFitness = totalFitness/population.size;
        
        population = population.filter((c:Chromosome) =>c.getFitness<averageFitness)
        averageFitnessTrend+=averageFitness;
        return averageFitness;
    
    }
    
    def crossover(){
        var totalFitness=0
        var newPopulation =new HashSet[Chromosome]()
        while(newPopulation.size<POPULATION_SIZE){
          
            var  A=pickRandomExpression(population)
            var expressionA=A.getExpression()
            
            var B=pickRandomExpression(population)
            var expressionB=B.getExpression()
            
            
            var  crossPoint=(Math.random()*expressionA.size-1).toInt
            var newA=new ArrayBuffer[String]()
            var newB=new ArrayBuffer[String]()

            var y =0
            for(y<-0 until expressionA.size){
                if(y<crossPoint){
                    newA+=expressionB(y)
                    newB+=expressionA(y)
                }
                else{
                    newB+=expressionB(y)
                    newA+=expressionA(y)  
                }
            }
             newPopulation+=(new Chromosome(newA))  
        }
        population=newPopulation;    
    }
    
    def  pickRandomExpression(s:HashSet[Chromosome] ):Chromosome={
        
        var index = (Math.random()*s.size).toInt
        var t = 0;
        for(c:Chromosome <- s){
            if(t==index){
                return c;
            }
            t+=1
        }
        System.out.println("there is a problem")
        System.out.println(index)
        
        return null
    }
       
    def mutations(generations:Int){
  
        
        var sDev=standardDev(population)
        var currentFitness=fitnessRates(generations%SAVE_NUM)
        var totalFitness=0.0
        var x
        for(x <-0 until fitnessRates.length){
            totalFitness=totalFitness+fitnessRates(x)
        }
        
        var avgFitness=totalFitness/SAVE_NUM;
        var difference=Math.abs(totalFitness/(avgFitness+0.00000001))
        
        changeTrend+=difference
        
        //The mutation rate should be higher if the population is starting to stagnate
        var mutationRate=Math.min(0.1,0.1/(difference*sDev+0.00000001))
        
        
        for(c:Chromosome <- population){
            c.mutate(mutationRate)    
        }
    }
    
    def findBest(){
        
        var max:Chromosome = null
        var maxVal=(-1.0)
        for(c:Chromosome<-population){
            if(c.getFitness()>maxVal){
                max=c;
                maxVal=c.getFitness();
                System.out.println(c.getFitness());
            }
        }
        fitnessRates+=maxVal;    
    }
    
    def gameRender(){
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
         drawGraph(fitnessRates,1,dbg);
         
         dbg.setColor(Color.BLUE);
         drawGraph(sDevTrend,2,dbg);
         dbg.setColor(Color.GREEN);
         drawGraph(changeTrend,3,dbg);
         dbg.setColor(Color.GRAY);
         drawGraph(averageFitnessTrend,4,dbg);
         

      }
    
      def drawGraph(c:ArrayBuffer[Double] ,quadrant:Int,dbg:Graphics){
          if(c.size==0){
              return
          }
          
          var saveSize=10
          var addX=0
          var addY=0
          if(quadrant==1){
              addX=0
              addY=0
          }
          if(quadrant==2) {
              addX=500
              addY=0
          }
          if(quadrant==3){
              addX=0
              addY=500
          }
          if(quadrant==4){
              addX=500
              addY=500
          }
          
          var interval=500.0/(c.size.toDouble)
          var maxVal=(-1.0)
          for(data:Double <- c){
              if(data>maxVal){
                  maxVal=data
              }
          }
          
          var index=0.0
          var count=0
         
          var x
          for(x<-0 until 500){
              var drawIndex=x*c.size/500
              var total=0.0
              var i
              
              for(i<-drawIndex to drawIndex-saveSize by -1){
                 if(i>=0){
                     total=total+c(i)
                 }
              }
              total=total/saveSize
              
              
              var xPos=x
              var yPos=(500-500*total/maxVal).toInt
              dbg.drawRect(xPos+addX,yPos+addY, 1, 1)
     
          
          }     
      }

     def  paintScreen(){ 
         var g:Graphics = null
         try {
            g = this.getGraphics();
            if ((g != null) && (dbImage != null)){
               g.drawImage(dbImage, 0, 0, null);
            }
            Toolkit.getDefaultToolkit().sync()  // sync the display on some systems
            g.dispose();
         }
         catch (ex:Exception){ 
           System.out.println("Graphics Context error: " + ex)
         }
      } // end of paintScreen()
   

    def  finishOff(){ 
         if (!finishedOff) {
            finishedOff = true;
        }
    }
  
 
       
    def getLeftBorder:Int = MIN_X_POS
    def getRigtBorder:Int = MAX_X_POS
    def getBottomBorder:Int = MIN_Y_POS
    def getTopBorder:Int = MAX_X_POS
    

    def generatePopulation(popSize:Int){
        var x
        for(x<-0 until popSize){
              population.add(new Chromosome(null));
            }      
        }
        
        
    def  restartGame(){
      println("Not implemented")   
    }
    
    def standardDev(population:HashSet[Chromosome]):Double={ 
          
        var data = new HashMap[Chromosome,ArrayBuffer[Double]]
        var average = new ArrayList[Double]
        var i = 0
        for(i<-0 until population.size){
          average+=0.0
        }
        
        var totalFitness = 0.0
        for(c:Chromosome <- population){
          
            var str=c.getExpression();
            var  d=new ArrayBuffer[Double]();
            var x =0
            for(x<-0 until str.size){
                
                if(x%2==0){
                    var y=str.get(x).toDouble;
                    d+=y;
                    average.set(x,average.get(x)+y);
                }
                
                else{
                    var q =str(x);
                    if(q=="+"){
                        d+=7.0;
                        average.set(x,average.get(x)+7.0);
                    }
                    else if(q=="-"){
                        d+=3.0;
                        average.set(x,average.get(x)+3.0); 
                    }
                    else if(q=="*"){
                        d+=9.0;
                        average.set(x,average.get(x)+9.0);
                    }
                    else if(q=="/"){
                        d+=1.0;
                        average.set(x,average.get(x)+1.0);
                    }
                        
                }
            }
            data.put(c,d);
            
        }
    
        var totalDistance=0.0;
        for(c:Chromosome<-data.keySet()){
            var location=data.get(c);
            
            var myDist=0
            var x = 0
            for(x<-0 until location.size){
                myDist=myDist+Math.abs(location(x)-average.get(x));
            }
              
				    totalDistance=totalDistance+myDist;
       }
              
      
        var sDev=Math.log(totalDistance);
        
        return sDev;
    }
 
 }  