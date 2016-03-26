
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
	private var myGraphPanel=new GraphPanel(period,WIDTH,HEIGHT)
	c.add(myGraphPanel,"Center")
	pack()

	myGraphPanel.startGame()

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

		pause=new JButton("pause")
		menu.add(pause)
		pause.addActionListener(new PauseListener())

		restart=new JButton("reset")
		menu.add(restart)
		restart.addActionListener(new RestartListener())

		resume=new JButton("resume")
		menu.add(resume);
		resume.addActionListener(new ResumeListener())

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

	private class RestartListener extends ActionListener{
		def actionPerformed(e:ActionEvent){
			myGraphPanel.restartGame()
		}
	}

	private class PauseListener extends ActionListener{
		def actionPerformed(e:ActionEvent){
			myGraphPanel.pauseGame()
		}
	}

	private class ResumeListener extends ActionListener{
		def actionPerformed(e:ActionEvent){
			myGraphPanel.resumeGame()
		}
	}

	def start(){  
    myGraphPanel.resumeGame()
	}

	def stop(){
	  myGraphPanel.pauseGame()
  }

	def destroy(){  
    myGraphPanel.stopGame()
  }


} // end of WormChaseApplet class
