
import javax.swing._;
import java.awt._;
import java.awt.event._;
import java.awt.image._;
import java.util._;
import java.awt.image.BufferStrategy._;
import java.applet._;
import java.io._; 
import java.util._;
import  sun.audio._;    
import  java.io._;

object FormulaGenerator{
  	
  private val DEFAULT_FPS=30

	def main(args: Array[String]){
	  var period=(1000/DEFAULT_FPS).toLong
	  new FormulaGenerator(period*1000000L)
	}
}
class FormulaGenerator(period:Long) extends JFrame{


	makeGUI()
	pack()
	setResizable(true)

	var c=getContentPane()


	private val WIDTH=1000
	private val HEIGHT=1000
	
	val POPULATION_SIZE = 20
  var genePool = new GenePool(POPULATION_SIZE)
	private var myGraphPanel=new GraphPanel(period,WIDTH,HEIGHT,genePool)
	
	c.add(myGraphPanel,"Center")
	pack()

	println("Start population")
	
	val population = new Thread(){
	  override def run(){
	    genePool.start()
	  }
	}
	population.start()
	
	println("Start rendering")
	myGraphPanel.startRendering()

	setVisible(true)

	//buttons for gameMenu
	var menu:JPanel=_ //The menu panel
	var quit:JButton=_ //Tells Game to quit
	var restart:JButton=_ //Tells Game to restart
	var pause:JButton=_//Pauses game
	var resume:JButton=_//Resumes game
	var help:JButton=_//Gives Game Instructions

	var scorePanel:JPanel=_//gives game score
	var playerOneScore:JLabel=_
	var scoringTimer:javax.swing.Timer=_//Gets Player One's score

	def makeGUI(){	
		var c = getContentPane()
		c.setLayout(new BorderLayout())

		menu=new JPanel()
		menu.setLayout(new GridLayout(5,1))

		quit=new JButton("quit")
		menu.add(quit)
		quit.addActionListener(new QuitListener())

		c.add(menu,"East")

		scorePanel=new JPanel()
		scorePanel.setLayout(new GridLayout(1,1))

		c.add(scorePanel, "South")
	} 


	private class QuitListener extends ActionListener{
		def actionPerformed(e:ActionEvent){
			System.exit(1)
		}
	}

}
