import java.util.*;
public class Chromosome{

    public static final int EXPRESSION_SIZE=19;
    public static final double MUTATION_RATE=0.01;
    public static final int TARGET_NUM=100;
	double myFitness=0;
	ArrayList<String> expression=null;
	public static final String[] operators={"+","-","*","d","^"};
	public static final String[] functions={"sin","cos","tan","ln","log"};
	
	public static final Double[]inputs={0.0,1.0,2.0,3.0,4.0};
	public static final Double[]outputs={1.0,6.0,63.0,364.0,1365.0};
	public Chromosome(ArrayList<String> ex)
	{
		if(ex==null)
		{
			createRandomExpression();
		}
		else
		{
			expression=ex;
		}
	
		calculateFitness();
		
	}
	public void createRandomExpression()
	{
		ArrayList<String> newExpression=new ArrayList<String>(EXPRESSION_SIZE);
		for(int index=0;index<EXPRESSION_SIZE;index++)
		{
			if(index%2==0)
			{
				Double number=(Math.random()*10+1);
				
				int expressionNumber=(int)(Math.random()*3);
				if(expressionNumber==1)
				{
					String function=functions[(int)(Math.random()*(functions.length-1))];
					if(function=="sin")
					{
						number=Math.sin(number);
					}
					if(function=="cos")
					{
						number=Math.cos(number);
					}
					if(function=="tan")
					{
						number=Math.tan(number);
					}
					if(function=="ln")
					{
						number=Math.log(number);
					}
					if(function=="log")
					{
						number=Math.log(number)/Math.log(10);
					}
					
				}
				
				newExpression.add(number.toString());
			}
			else
			{
				String operator=operators[(int)(Math.random()*operators.length)];
				newExpression.add(operator);
			}
		}
		
			
		
		expression=newExpression;
		
		
	}
	public void mutate(double mutationRate)
	{
		
		for(int x=0;x<expression.size();x++)
		{
			if( Math.random()<mutationRate)
			{
				
				String newStr=null;
				if( x%2==0)
				{
					Double newVal= new Double(Math.random()*9);
					
					int expressionNumber=(int)(Math.random()*3);
					if(expressionNumber==1)
					{
						String function=functions[(int)(Math.random()*(functions.length-1))];
						if(function=="sin")
						{
							newVal=Math.sin(newVal);
						}
						if(function=="cos")
						{
							newVal=Math.cos(newVal);
						}
						if(function=="tan")
						{
							newVal=Math.tan(newVal);
						}
						if(function=="ln")
						{
							newVal=Math.log(newVal);
						}
						if(function=="log")
						{
							newVal=Math.log(newVal)/Math.log(10);
						}
						
					}
					newStr=newVal.toString();
					
				}
				else
				{
					int selectionIndex=(int)(Math.random()*4);
					newStr=operators[selectionIndex];
				}
				expression.remove(x);
				expression.add(x,newStr);
			}
		}
		
		calculateFitness();
	}
	public void replaceExpression(ArrayList<String> newExpression)
	{
		expression=newExpression;
		calculateFitness();
	}
	public void calculateFitness()
	{
		
		for(int x=0;x<inputs.length;x++)
		{
		ArrayList<String> expressionCopy=new ArrayList<String>();
		expressionCopy.addAll(expression);

		for(int index=2; index<expressionCopy.size();index++)
		{
				if(expressionCopy.get(index-1)=="^")
				{
					Double firstNum=Double.parseDouble(expressionCopy.get(index-2));
					Double secondNum=Double.parseDouble(expressionCopy.get(index));
					Double total=new Double(Math.pow(firstNum,secondNum));
					
					expressionCopy.remove(index-2);
					expressionCopy.remove(index-2);
					expressionCopy.set(index-2,total.toString());
					index=1;
				}
		}
		
		for(int index=2; index<expressionCopy.size();index++)
		{
				if(expressionCopy.get(index-1)=="*")
				{
					Double firstNum=Double.parseDouble(expressionCopy.get(index-2));
					Double secondNum=Double.parseDouble(expressionCopy.get(index));
					Double total=new Double(firstNum*secondNum);
					
					expressionCopy.remove(index-2);
					expressionCopy.remove(index-2);
					expressionCopy.set(index-2,total.toString());
					index=1;
				}
				if(expressionCopy.get(index-1)=="d")
				{
					Double firstNum=Double.parseDouble(expressionCopy.get(index-2));
					Double secondNum=Double.parseDouble(expressionCopy.get(index));
					Double total=new Double(firstNum/secondNum);
					
					expressionCopy.remove(index-2);
					expressionCopy.remove(index-2);
					expressionCopy.set(index-2,total.toString());
					index=1;
				}
		}

		for(int index=2; index<expressionCopy.size();index++)
		{
				if(expressionCopy.get(index-1)=="+")
				{
					Double firstNum=Double.parseDouble(expressionCopy.get(index-2));
					Double secondNum=Double.parseDouble(expressionCopy.get(index));
					Double total=new Double(firstNum+secondNum);
					expressionCopy.remove(index-2);
					expressionCopy.remove(index-2);
					expressionCopy.set(index-2,total.toString());
					index=1;
				}
				if(expressionCopy.get(index-1)=="-")
				{
					Double firstNum=Double.parseDouble(expressionCopy.get(index-2));
					Double secondNum=Double.parseDouble(expressionCopy.get(index));
					Double total=new Double(firstNum-secondNum);
					expressionCopy.remove(index-2);
					expressionCopy.remove(index-2);
					expressionCopy.set(index-2,total.toString());
					index=1;
				}
		}

		
		myFitness=Math.abs(TARGET_NUM/(TARGET_NUM-Double.parseDouble(expressionCopy.get(0))));
		}
		
	}
	public ArrayList<String> getExpression()
	{
		return expression;
	}
	public double getFitness()
	{
		return myFitness;
	}
	public int getExpressionSize()
	{
		return expression.size();
	}
	public void print() {
		
		for(String s: expression)
		{
			if(s.equals("d"))
				System.out.print("/");
			else
				System.out.print(s);
			
		}
		System.out.println();
		// TODO Auto-generated method stub
		
	}

	
}