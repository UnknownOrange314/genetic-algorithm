import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import java.util.Timer;
import java.util.TimerTask;

class GenePool {
	var sDevTrend= new ArrayBuffer[Double]  //Trend of standardDevation over time.
	var averageFitnessTrend= new ArrayBuffer[Double]//Trend of averageFitness.
	var changeTrend = new ArrayBuffer[Double]
	var fitnessRates = new ArrayBuffer[Double] //Trend of fitness over time.
	
	var population: HashSet[Chromosome] = _
	var POPULATION_SIZE:Int =_
	val SAVE_NUM = 100

	//Genetic Algorithm Variables
	var targetNumber = 5
	val MUTATION_RATE=0.01
	
	def this(popSize:Int){
    this()
  	var i = 0 
  	for(i <-0 until SAVE_NUM){
  		fitnessRates+=0.0 
  	}
    population = generatePopulation(popSize)
    POPULATION_SIZE = popSize
	}
	
	def start(){
	  while(true){
	    update()
	  }
	}
	
	def generatePopulation(popSize:Int):HashSet[Chromosome]={
		var pop = new HashSet[Chromosome]
    var x = 0
		for(x<-0 until popSize){
			pop.add(new Chromosome(null));
		}
		return pop
	}
	
	def update(){
		var fitness=removeFailures()
		var generations=changeTrend.size
		fitnessRates(generations%100)=fitness

		crossover()
		mutations(generations)
		generations+=1

		findBest()	
  }
	
	def removeFailures():Double = {
		var totalFitness = 0.0
		var totalSize=0

		for(c:Chromosome <- population){
			totalSize=totalSize+c.getExpressionSize()
			totalFitness=totalFitness+c.getFitness()
		}


		var  averageFitness = totalFitness/population.size

    var tolerance = 0.1 //Account for rounding issues that could make each chromosome "below average"
		population = population.filter((c:Chromosome) =>c.getFitness>averageFitness-tolerance)
		averageFitnessTrend+=averageFitness
		return averageFitness

	}
	
	def crossover(){
		var totalFitness=0
		var newPopulation = new HashSet[Chromosome]()
		while(newPopulation.size<POPULATION_SIZE){

			var A=pickRandomExpression(population)
  		var expressionA=A.getExpression()
  
  		var B=pickRandomExpression(population)
  		var expressionB=B.getExpression()
  
  		var crossPoint=(Math.random()*expressionA.size-1).toInt
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
		var x:Int = 0
		for(x <-0 until fitnessRates.length){
			totalFitness=totalFitness+fitnessRates(x)
		}

		var avgFitness=totalFitness/SAVE_NUM;
		var difference=Math.abs(totalFitness/(avgFitness+0.00000001))

		changeTrend+=difference
		sDevTrend+=sDev

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
			}
		}
	  fitnessRates+=maxVal;    
	}
	
	def getFitnessRates:ArrayBuffer[Double] = fitnessRates
	def getSDevTrend:ArrayBuffer[Double] = sDevTrend
	def getChangeTrend:ArrayBuffer[Double] = changeTrend
	def getAverageFitnessTrend:ArrayBuffer[Double] = averageFitnessTrend
	
	def standardDev(population:HashSet[Chromosome]):Double={ 

		var data = new HashMap[Chromosome,ArrayBuffer[Double]]
		var average = new ArrayBuffer[Double]
		var i = 0
		for(i<-0 until population.size){
			average+=0.0
		}

		var totalFitness = 0.0
		for(c:Chromosome <- population){
			var expr=c.getExpression();
		  var d=new ArrayBuffer[Double]();
			var x =0
			for(x<-0 until expr.size){

				if(x%2==0){
					var y=expr(x).toDouble;
					d+=y;
					average.insert(x,average(x)+y);
				}

				else{
					var q =expr(x);
					if(q=="+"){
						d+=7.0;
						average(x)=average(x)+7.0;
					}
					else if(q=="-"){
						d+=3.0;
						average(x)=average(x)+3.0; 
					}
					else if(q=="*"){
						d+=9.0;
						average(x)=average(x)+9.0;
					}
					else if(q=="/"){
						d+=1.0;
						average(x)=average(x)+1.0;
					}
				}
			}
			data.put(c,d);
		}

		var totalDistance=0.0;
		for((c,location:ArrayBuffer[Double])<-data){
  		var myDist=0.0
  		var x = 0
  		for(x<-0 until location.size){
  			myDist=myDist+Math.abs(location(x)-average(x));
  		}
  		totalDistance=totalDistance+myDist;
  	}
		var sDev=Math.log(totalDistance);
		return sDev;
	}

}