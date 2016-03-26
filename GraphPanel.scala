
import java.text.DecimalFormat
import javax.swing._
import java.awt._
import java.awt.event._
import java.awt.image._
import java.awt.image.BufferStrategy._
import java.io._
import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

/**
 * This class contains code for generating visualizations of the genetic algorithm. 
 * The graphics rendering code is a modified version of code taken from Killer Game Programming in Java
 */
class GraphPanel(pd:Long, w:Int, h:Int) extends JPanel with Runnable{

	val POPULATION_SIZE = 20
  var genePool = new GenePool(POPULATION_SIZE)
	
	var PWIDTH=w
	var PHEIGHT=h
	
	var period = pd

	setBackground(Color.white)
	setPreferredSize( new Dimension(PWIDTH,PHEIGHT))

	setLayout(new BorderLayout())

	// create game components
	var top=new JPanel()
	top.setLayout(new GridLayout(1,1))
	add(top, BorderLayout.NORTH)

	var title=new JLabel("Genetic algorithm visualization")//Game Title
	top.add(title)

	var scorePanel=new JPanel()
	scorePanel.setLayout(new GridLayout(1,1))
	var PlayerOneScore=new JLabel("")//Score for playerOne
	scorePanel.add(PlayerOneScore)


	add(scorePanel, BorderLayout.SOUTH)
	setFocusable(true)

	//Menu and title objects

	var playerOneScore:JLabel=_

	//public GameMenu myMenu;    //Buttons for menu
	var menuPanel:JLabel=_

	var myImage:BufferedImage=_
	var myBuffer1:Graphics=_
	var myBuffer2:Graphics=_

	val N = 400
	var BACKGROUND = new Color(204,204,204)

	var g:Graphics=_

	val NO_DELAYS_PER_YIELD = 16
	/* Number of frames with a delay of 0 ms before the animation thread yields
   to other running threads. */

	val MAX_FRAME_SKIPS = 5  // was 2;
	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered

	val NUM_FPS = 10
	// number of FPS values stored to get an average


	var animator:Thread = _
	@volatile var running:Boolean = false
	@volatile var isPaused:Boolean = false

	// used at game termination
	@volatile var gameOver:Boolean = false
	var metrics:FontMetrics=_
	var finishedOff:Boolean = false

	var dbg:Graphics = _
	var dbImage:Image = _

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
	}

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

		var beforeTime = 0L
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
				populationUpdate() 
				dataRender()  // render the game to a buffer
				paintScreen()  // draw the buffer on-screen
			}
			afterTime =System.nanoTime()
			timeDiff = afterTime - beforeTime
			sleepTime = (period - timeDiff) - overSleepTime

			if (sleepTime > 0) {   // some time left in this cycle
				try {
					Thread.sleep(sleepTime/1000000L);  // nano -> ms
				}
				catch{
				    case ex:InterruptedException => ex.printStackTrace()
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			}
			else {    // sleepTime <= 0; the frame took longer than the period
				excess -= sleepTime  // store excess time value
				overSleepTime = 0L

				noDelays+=1
				if (noDelays >= NO_DELAYS_PER_YIELD) {
					//Thread.yield  // give another thread a chance to run
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
				populationUpdate()    // update state but don't render
				skips+=1
			} 
		}
		finishOff()
	} // end of run()

	def populationUpdate() { 
		if (!isPaused && !gameOver){
	    genePool.update()
	  
		}
	}

	def dataRender(){
		if (dbImage == null){
			dbImage = createImage(PWIDTH, PHEIGHT);
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			}
			else{
				dbg = dbImage.getGraphics();		    
			}
		}

		// clear the background
		dbg.setColor(Color.white);
		dbg.fillRect (0, 0, PWIDTH, PHEIGHT);
		dbg.setColor(Color.RED);

		//dbg.fillRect(0,0,800,800);
		drawGraph(genePool.getFitnessRates,1,dbg);

		dbg.setColor(Color.BLUE);
		drawGraph(genePool.getSDevTrend,2,dbg);
		dbg.setColor(Color.GREEN);
		drawGraph(genePool.getChangeTrend,3,dbg);
		dbg.setColor(Color.GRAY);
		drawGraph(genePool.getAverageFitnessTrend,4,dbg);

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

		var x:Int = 0
		for(x<-0 until 500){
			var drawIndex=x*c.size/500
			var total=0.0
			var i:Int = 0
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
	  catch{
	    case ex: Exception =>System.out.println("Graphics Context error: " + ex)
	  }
	}

	def  finishOff(){ 
		if (!finishedOff) {
			finishedOff = true;
		}
	}

	def getLeftBorder:Int = MIN_X_POS
	def getRigtBorder:Int = MAX_X_POS
	def getBottomBorder:Int = MIN_Y_POS
	def getTopBorder:Int = MAX_X_POS

	def  restartGame(){
		println("Not implemented")   
	}

}  