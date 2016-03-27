package view

import java.awt._
import scala.collection.mutable.ArrayBuffer

class Graph {
  
  private var xPos:Int = _
  private var yPos:Int = _
  private var size:Int = _
  private var title:String = _

  //Margins for drawing graph.
  private val topMargin = 30
  private val bottomMargin = 30
  private val leftMargin = 30
  private val rightMargin = 30
  def this(x:Int,y:Int,s:Int,t:String){
    this()  
    xPos = x
    yPos = y
    size = s 
    title = t
  }

  //TODO: Add axis labels.
  def draw(c:Color,data:ArrayBuffer[Double],dbg:Graphics){
    
    if(data.size == 0){
      println("No data")
      return
    }
    
    dbg.setColor(c)
    
		var interval=size.toDouble/(data.size.toDouble)
		
		var maxVal = 0.0001
		for(dVal:Double <- data){
			if(dVal>maxVal){
				maxVal=dVal
			}
		}

		var index=0.0
		var count=0	
		var saveSize = 10
		var height = size-topMargin-bottomMargin	
	
		var x:Int = 0
		for(x<-leftMargin until size-1){
		  val drawIdx = data.size*(x-leftMargin)/(size-leftMargin)
		  var y = (height-height*data(drawIdx)/maxVal).toInt + topMargin
			fillRectangle(x,y,1,1,dbg)
		}
		
		dbg.setColor(Color.BLACK)
		dbg.setFont(new Font("TimesRoman", Font.PLAIN,20))
		dbg.drawString(title,xPos+20,yPos+20)
		
		var borderSize = 5
		dbg.setColor(Color.GRAY)
		fillRectangle(0,size-borderSize,size,borderSize,dbg)
		fillRectangle(size-borderSize,0,borderSize,size,dbg)
    fillRectangle(0,0,size,borderSize,dbg)
    fillRectangle(0,0,borderSize,size,dbg)
  }
  
  def fillRectangle(x:Int,y:Int,w:Int,h:Int,dbg:Graphics){
    dbg.fillRect(x+xPos,y+yPos,w,h)
  }
}