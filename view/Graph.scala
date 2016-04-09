package view

import java.awt._
import java.text._
import scala.collection.mutable.ArrayBuffer

class Graph {
  
  private var xPos:Int = _
  private var yPos:Int = _
  private var size:Int = _
  private var title:String = _

  //Margins for drawing graph.
  private val topMargin = 30
  private val bottomMargin = 30
  private val leftMargin = 50
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
    //Don't display outliers.

		var index=0.0
		var count=0	
		var saveSize = 10
		var height = size-topMargin-bottomMargin	
	
		var x:Int = 0
		for(x<-leftMargin until size-1-rightMargin){
		  val drawIdx = data.size*(x-leftMargin)/(size-leftMargin-rightMargin)
		  
		  var value = data(drawIdx)
		  
		  //Do a moving average if possible to smooth the graph.
		  if(drawIdx>0&&drawIdx<data.size-1){
		    value = (value+data(drawIdx-1)+data(drawIdx+1))/3
		  }
		  
		  var y = (height-height*data(drawIdx)/maxVal).toInt + topMargin
		  var sz = 4
			fillRectangle(x-sz/2,y-sz/2,sz,sz,dbg)
		}
		
		drawYAxis(xPos+leftMargin,yPos+topMargin,yPos+size-bottomMargin,maxVal,dbg)
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
  
  def drawYAxis(drawX:Int,minY:Int, maxY:Int, maxVal:Double,dbg:Graphics){
    dbg.setFont(new Font("TimesRoman",Font.PLAIN,12))
    var formatter = new DecimalFormat("0.##")
    var medFormat = new DecimalFormat("0.#")
    var largeFormat = new DecimalFormat("0")
    var drawY:Int = 0
    for(drawY<-maxY until minY by -30){
      var value = maxVal - maxVal*(drawY-minY).toDouble/(maxY-minY).toDouble
      dbg.setColor(Color.BLACK)
      if(value>1000){
        dbg.drawString(""+largeFormat.format(value),drawX-40,drawY+6)
      }
      else if(value>100){
        dbg.drawString(""+medFormat.format(value),drawX-40,drawY+6)
      }else{
        dbg.drawString(""+formatter.format(value),drawX-40,drawY+6)
      }
      dbg.setColor(Color.GRAY)
      dbg.drawLine(xPos+leftMargin,drawY,xPos+size-rightMargin,drawY)
      
    }
    dbg.drawLine(xPos+leftMargin,yPos+topMargin,xPos+leftMargin,yPos+size-bottomMargin)
  }
  
  def fillRectangle(x:Int,y:Int,w:Int,h:Int,dbg:Graphics){
    dbg.fillRect(x+xPos,y+yPos,w,h)
  }
}