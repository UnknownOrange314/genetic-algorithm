
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
import view.Graph

/**
 * This class contains code for generating visualizations of the genetic algorithm. 
 * The graphics rendering code is a modified version of code taken from Killer Game Programming in Java
 */
class GraphPanel(pd:Long, w:Int, h:Int,gPool:GenePool) extends JPanel with Runnable{


	val genePool = gPool
	
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

	var fitnessGraph = new Graph(0,0,500,"Fitness")
	var sDevGraph = new Graph(0,500,500,"Standard deviation")
	var changeGraph = new Graph(500,0,500,"Change")
	var avgFitnessGraph = new Graph(500,500,500,"Average fitness");

	def startRendering(){ 
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
				dataRender()  // render the game to a buffer
				paintScreen()  // draw the buffer on-screen
			}
			afterTime = System.nanoTime()
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
			  println("Too long")
				skips+=1
			} 
		}
		finishOff()
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
		
		fitnessGraph.draw(Color.RED,genePool.getFitnessRates,dbg)
		sDevGraph.draw(Color.BLUE,genePool.getSDevTrend,dbg)
		changeGraph.draw(Color.GREEN,genePool.getChangeTrend,dbg)
		avgFitnessGraph.draw(Color.GRAY,genePool.getAverageFitnessTrend,dbg)
		drawStats(dbg)
	}
	
	private def drawStats(dbg:Graphics){

	  dbg.setColor(Color.BLACK)
	  dbg.drawString("Target number:"+genePool.getTargetData(),1050,200)
	  
	  dbg.drawString(genePool.getBest,50,1020)
	}

	def paintScreen(){ 
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

}  