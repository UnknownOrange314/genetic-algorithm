import scala.collection.mutable.ArrayBuffer

/**
 * A object representing a mathematical expression.
 */
class Chromosome(expr:ArrayBuffer[String]) {
  
	if(expr==null){
			expression = createRandomExpression()
		}
		else{
			expression=expr
		}
		calculateFitness()
	

  val EXPRESSION_SIZE=19 //Size of data.
  val MUTATION_RATE=0.01 //Chance of a random mutation.
  val TARGET_NUM=100.0 //The number we want the chromosome's expression to add up to.
	
  var myFitness=0.0
	var expression:ArrayBuffer[String]=null
	val operators = Array("+","-","*","d","^");
	val functions = Array( 
	    (n:Double) => Math.sin(n),
	    (n:Double) => Math.cos(n),
	    (n:Double) => Math.tan(n),
	    (n:Double) => Math.log(n),
	    (n:Double) => Math.log(n)/Math.log(10)
	    )
	
	
	
	def createRandomExpression():ArrayBuffer[String]={
	  var newExpression=new ArrayBuffer[String](EXPRESSION_SIZE)
	  var i = 0;
	  
		for(i <- 0 until EXPRESSION_SIZE){
			if(i%2==0){
				
			  var number=(Math.random()*10+1)
				var  expressionNumber=(Math.random()*3).toInt
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
	   * require the loop to only run 3 times, which would mean a signficant performance improvemment.
	   */
	  for(i<-0 until operators.length){
	    for(j<-2 until expressionCopy.length){
	      if(expressionCopy(j-1) == operators(i)){
	        val x = expressionCopy(j-2).toDouble
	        val y = expressionCopy(j).toDouble
	        var total = 0.0
	        
	        if(operators(i)=="^"){
	          total = Math.pow(x,y)
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
	        
	      }
	    }
	  }
		return Math.abs(TARGET_NUM/(TARGET_NUM-expressionCopy(0).toDouble));				
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
	
	def print(){
	  		
		for(s <- expression){
			if(s.equals("d"))
				System.out.print("/");
			else
				System.out.print(s);
			
			
		}
		System.out.println();
		// TODO Auto-generated method stub
		
	}

	
}