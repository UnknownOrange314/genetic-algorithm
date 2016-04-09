import scala.collection.mutable.ArrayBuffer
import java.text._

object Chromosome{
	val TARGET_NUM=1000.0 //The number we want the chromosome's expression to add up to.
  def swap(mom:Chromosome,dad:Chromosome):Chromosome={
    
    var momExpr = mom.getExpression()
    var dadExpr = dad.getExpression()
    var crossPoint = (Math.random() * momExpr.size - 1).toInt
    var childExpr = new ArrayBuffer[String]()
    
    var y = 0
    for (y <- 0 until momExpr.size) {
      if (y < crossPoint) {
        childExpr += momExpr(y)
      } else {
        childExpr += dadExpr(y)
      }
    }
    return new Chromosome(childExpr)
  }
}

/**
 * A object representing a mathematical expression.
 */
class Chromosome() {
 
	val EXPRESSION_SIZE=19 //Size of data.
	val MUTATION_RATE=0.01 //Chance of a random mutation.

	var myFitness=0.0
	val operators = Array("^","*","d","+","-")
	val functions = Array( 
			(n:Double) => Math.sin(n),
			(n:Double) => Math.cos(n),
			(n:Double) => Math.tan(n),
			(n:Double) => Math.log(n),
			(n:Double) => Math.log(n)/Math.log(10)
			)
	
	var expression:ArrayBuffer[String] =_
	
  def this(expr:ArrayBuffer[String]){
    
    this()
  	if(expr==null){
  		expression = createRandomExpression()
  	}
  	else{
  		expression=expr
  	}
  	myFitness = calculateFitness()
  }


	def createRandomExpression():ArrayBuffer[String]={
		var newExpression=new ArrayBuffer[String](EXPRESSION_SIZE)
			var i = 0;

		for(i <- 0 until EXPRESSION_SIZE){
  		if(i%2==0){
  
  			var number=(Math.random()*10+1)
				var expressionNumber=(Math.random()*3).toInt
				if(expressionNumber==1){
					val idx = (Math.random()*(functions.length-1)).toInt
					number = functions(idx)(number)
				}
				newExpression+=(number.toString())
			}
  		
			else{
				var operator=operators((Math.random()*operators.length).toInt)
				newExpression+=(operator)
			}
		}
		return newExpression

	}

	def mutate(mutationRate:Double){
		var x = 0;
		
		for(x<-0 until expression.length){
			if( Math.random()<mutationRate){
			  
			  var newStr:String=null;
  			if( x%2==0){
  
  				var newVal= Math.random()*9;
  				var expressionNumber=(Math.random()*3).toInt;
  				if(expressionNumber==1){
  
  					val idx = (Math.random()*(functions.length-1)).toInt
						newVal = functions(idx)(newVal)
  
  				}
  				newStr=newVal.toString()
  
  			}
  			else{
  				val selectionIndex=(Math.random()*4).toInt;
  				newStr=operators(selectionIndex)
  			}
  			expression.remove(x)
  			expression.insert(x,newStr)
			}
		}
		myFitness = calculateFitness();
	}

	def replaceExpression(newExpression:ArrayBuffer[String]){
		expression=newExpression
		myFitness=calculateFitness()
	}

	/**
	 * Calculate how close the expression is to a target value. The expression is stored and evaulated in 
	 * ArrayList form. There are no parethesis, so an expression tree is not necessary.
	 */
	private def calculateFitness():Double = {

		var i = 0
		var expressionCopy=new ArrayBuffer[String]()
		expressionCopy.appendAll(expression)
		
		/*
		 * The loop runs 5 times for the sake of simplifying the code. If is possible to make changes that 
		 * require the loop to only run 3 times, which would mean a significant performance improvement.
		 */
		for(i<-0 until operators.length){
			for(j<-2 until expressionCopy.length){
			  
				while(j<expressionCopy.length && expressionCopy(j-1) == operators(i)){
					val x = expressionCopy(j-2).toDouble
					val y = expressionCopy(j).toDouble
		      
					var total = 0.0
		      
					if(operators(i)=="^"){
						total = Math.pow(Math.abs(x),Math.abs(y))
					}
					if(operators(i)=="*"){
						total = x*y;
					}
					if(operators(i)=="d"){
						total = x/y;
					}
					if(operators(i)=="+"){
						total = x+y;
					}
					if(operators(i)=="-"){
						total = x-y;
					}
					
					if(total.isNaN){
					  println(x+operators(i)+y+"="+total)
					}
					expressionCopy(j-2)=""+total
					expressionCopy -=(expressionCopy(j-1),expressionCopy(j))
				}
			}
		}
		return Math.sqrt(Math.abs(Chromosome.TARGET_NUM/(Chromosome.TARGET_NUM-expressionCopy(0).toDouble)));				
	}

	def getExpression():ArrayBuffer[String]={
			return expression;
	}

	def getFitness():Double={
			return myFitness;
	}

	def getExpressionSize():Int={
			return expression.length;
	}

	def toStringRounded():String={
	  
	  var s = ""
	  for(str <- expression){
	    if(str.equals("d")){
	      s+="/"
	    }
	    else if(str.size==1){
	      s+=str
	    }
	    //Only numeric values will have a string length greater than 1.
	    else{ 
        var formatter = new DecimalFormat("0.###########")
        s+=formatter.format(str.toDouble)
	    }
	    
	  }
	  return s
	}
	
	def print(){
		for(s <- expression){
			if(s.equals("d"))
				System.out.print("/");
			else
				System.out.print(s);
		}
		System.out.println();
	}

}