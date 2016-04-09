import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents a population of candidate solutions to a problem.
 */

object GenePool {
  def generatePopulation(popSize: Int): HashSet[Chromosome] = {
    var pop = new HashSet[Chromosome]
    var x = 0
    for (x <- 0 until popSize) {
      pop.add(new Chromosome(null));
    }
    return pop
  }
}
class GenePool {
  var sDevTrend = new ArrayBuffer[Double] //Trend of standardDevation over time.
  var averageFitnessTrend = new ArrayBuffer[Double] //Trend of averageFitness.
  var changeTrend = new ArrayBuffer[Double] //Trend of how the average fitness is changing over tie.
  var fitnessRates = new ArrayBuffer[Double] //Trend of fitness over time.

  var population: HashSet[Chromosome] = _
  var POPULATION_SIZE: Int = _
  var generations = 0
  val SAVE_NUM = 100
  var MAX_MUTATION_RATE = 0.1
  var best:Chromosome = _
  
  //Returns information about optimal target for gene pool in string form.
  def getTargetData():String = {
    return ""+Chromosome.TARGET_NUM  
  }
  
  //Returns information about the best chromosome.
  def getBest():String = {
    best.print()
    val bestStr = best.toStringRounded()
    return "Optimal formula:"+bestStr
  }
  def this(popSize: Int) {
    this()
    var i = 0
    for (i <- 0 until SAVE_NUM) {
      fitnessRates += 0.0
    }
    population = GenePool.generatePopulation(popSize)
    POPULATION_SIZE = popSize
  }

  //Starts the genetic algorithm.
  def start() {
    while (true) {
      update()
    }
  }
  
  private def update() {
    var fitness = removeFailures()

    crossover()
    mutations(generations)
    generations += 1
    findBest()
  }

  private def removeFailures() = {
    var totalFitness = 0.0

    for (c: Chromosome <- population) {
      totalFitness = totalFitness + c.getFitness()
    }

    var averageFitness = totalFitness / population.size
    var tolerance = 0.1 //Account for rounding issues that could make each chromosome "below average".
    population = population.filter((c: Chromosome) => c.getFitness > averageFitness - tolerance)
    averageFitnessTrend += averageFitness
  }

  /*
   * Generates new candidate solutions by sharing data between existing solutions.
   */
  private def crossover() {
    var totalFitness = 0
    var newPopulation = new HashSet[Chromosome]()
    while (newPopulation.size < POPULATION_SIZE) {

      var mom = pickRandomSolution(population)
      var dad = pickRandomSolution(population)
      newPopulation += Chromosome.swap(mom,dad)
    }
    population = newPopulation;
  }

  private def pickRandomSolution(s: HashSet[Chromosome]): Chromosome = {
    var index = (Math.random() * s.size).toInt
    var t = 0;
    for (c: Chromosome <- s) {
      if (t == index) {
        return c;
      }
      t += 1
    }
    System.err.println("Population has died off")
    return null
  }

  /**
   * Causes random changes to the solutions.
   * @param generations How long the population has existed. As this
   * value increses, the likelihood of mutations will increase.
   */
  def mutations(generations: Int) {

    var totalFitness = 0.0
    var x: Int = 0

    var difference = 0.001
    if (generations > 1) {
      difference = Math.abs(averageFitnessTrend(generations - 1) - averageFitnessTrend(generations - 2))
      changeTrend += difference
    }
    changeTrend +=difference
    
    //Dynamically change the mutation rate based on the standard deviation to prevent stagnation.
    var sDev = standardDev(population)
    sDevTrend += sDev
    var mutationRate = Math.min(MAX_MUTATION_RATE,generations.toDouble/1000.0 + 0.1 / (difference * sDev + Math.pow(10, -9)))

    for (c: Chromosome <- population) {
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
 		best = max
 	  fitnessRates+=maxVal;   
  }

  def getFitnessRates: ArrayBuffer[Double] = fitnessRates
  def getSDevTrend: ArrayBuffer[Double] = sDevTrend
  def getChangeTrend: ArrayBuffer[Double] = changeTrend
  def getAverageFitnessTrend: ArrayBuffer[Double] = averageFitnessTrend

  def standardDev(population: HashSet[Chromosome]): Double = {

    var data = new HashMap[Chromosome, ArrayBuffer[Double]]
    var average = new ArrayBuffer[Double]
    var i = 0
    for (i <- 0 until population.size) {
      average += 0.0
    }

    var totalFitness = 0.0
    for (c: Chromosome <- population) {
      var expr = c.getExpression();
      var d = new ArrayBuffer[Double]();
      var x = 0
      for (x <- 0 until expr.size) {

        if (x % 2 == 0) {
          var y = expr(x).toDouble;
          d += y;
          average.insert(x, average(x) + y);
        } else {
          var q = expr(x);
          if (q == "+") {
            d += 7.0;
            average(x) = average(x) + 7.0;
          } else if (q == "-") {
            d += 3.0;
            average(x) = average(x) + 3.0;
          } else if (q == "*") {
            d += 9.0;
            average(x) = average(x) + 9.0;
          } else if (q == "/") {
            d += 1.0;
            average(x) = average(x) + 1.0;
          }
        }
      }
      data.put(c, d);
    }

    var totalDistance = 0.0;
    for ((c, location: ArrayBuffer[Double]) <- data) {
      var myDist = 0.0
      var x = 0
      for (x <- 0 until location.size) {
        myDist = myDist + Math.abs(location(x) - average(x));
      }
      totalDistance = totalDistance + myDist;
    }
    var sDev = Math.log(totalDistance);
    return sDev;
  }

}