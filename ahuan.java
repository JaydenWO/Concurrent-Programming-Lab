import java.util.Random;
public class ahuan 
{
	
	public static void main (String[]args)
	{
		int row=300,col=100,point;
		double [][]opop = new double[row][col];
		double [][]npop = new double[row][col];
		double[]parent1 = new double[100];
		double[]parent2 = new double[100];
		double[]child1 = new double[100];
		double[]child2 = new double[100];
		double min=1.0,max=100.0,chance;
		Random rand = new Random();
		
		for(int x=0;x<row;x++)
		{
			for(int y=0;y<col;y++)
			{
				opop[x][y] = -5.12 + (5.12 + 5.12)* rand.nextDouble();
			}
		}
		
		evaluate(opop);
		for(int count=0;count<1000;count++)
		{
			int b=0;
			for(int x=0;x<150;x++)
			{
				parent1 = tournament(opop);
				parent2 = tournament(opop);
				chance = min + (max-min) * rand.nextDouble();
				point = rand.nextInt(99)+1;
				child1 = crossover1(parent1,parent2,chance,point);
				child2 = crossover2(parent1,parent2,chance,point);
				for(int y=0;y<100;y++)
				{
					npop[b][y]= child1[y];
					npop[b+1][y]= child2[y];
				}
				b+=2;
				
			}
			
			npop = mutate(npop);
			opop=npop;
			System.out.printf("Gen:%d ",count);
			evaluate(npop);
		}
	}
	

	public static void evaluate(double [][]array)
	{
		double value,min=Double.POSITIVE_INFINITY,max=Double.NEGATIVE_INFINITY,avg=0;
		for(int x=0;x<300;x++)
		{
			value=0;
			for(int y=0;y<100;y++)
			{
				value += (y+1)*(array[x][y]*array[x][y]);
			}
			
			if(min>value)
			{
				min = value;
			}
			
			if(max<value)
			{
				max = value;
			}
			avg+=value;
		}
		avg = avg/3;
		System.out.printf("min:%.6f max:%.6f  avg:%.6f \n",min,max,avg);
	}
	
	public static double[][] mutate(double [][]array)
	{
		Random rand = new Random();
		double min=-0.01,max=0.01,num,chance;
		
		for(int x=0;x<300;x++)
		{
			for(int y=0;y<100;y++)
			{
				num = min + (max - min)* rand.nextDouble();
				chance = 0 + (100-0)* rand.nextDouble();
				if(chance<1.00)
				{
					if (array[x][y] + num >= -5.12 && array[x][y] + num <= 5.12)
					{
						array[x][y] += num;
					}
				}
			}
		}
		return array;
	}
	
	public static double[] tournament(double[][] array)
	{
		double [] array1 = new double[100];
		double [] array2 = new double[100];
		Random rand = new Random();
		int row;
		double min1=0,min2=0;
		
		row = rand.nextInt(299)+1;
		for(int y=0;y<100;y++)
		{
			array1[y]=array[row][y];
			min1 += (y+1)*(array1[y]*array1[y]);
		}
		
		row = rand.nextInt(299)+1;
		
		for(int y=0;y<100;y++)
		{
			array2[y]=array[row][y];
			min2 += (y+1)*(array2[y]*array2[y]);
		}
		
		for(int x=0;x<50;x++)
		{
			if(min1>=min2)
			{
				min1=0;
				row = rand.nextInt(299)+1;
				for(int y=0;y<100;y++)
				{
					array1[y]=array[row][y];
					min1 += (y+1)*(array1[y]*array1[y]);
				}
			}
			
			else if(min2>min1)
			{
				row = rand.nextInt(299)+1;
				min2=0;
				for(int y=0;y<100;y++)
				{
					array2[y]=array[row][y];
					min2 += (y+1)*(array2[y]*array2[y]);
				}
			}
		}
		
		if(min1>min2)
		{
			return array2;
		}
		
		else
		{
			return array1;
		}
	}
	
	public static double[] crossover1(double[]array1,double []array2,double chance,int point)
	{
		double [] child = new double[100];
		if(chance>=70.0)
		{
			for(int x=0;x<point;x++)
			{
				child[x]=array1[x];
			}
			
			while(point<100)
			{
				child[point] = array2[point];
				point++;
			}
			return child;
		}
		
		else
		{
			for(int x=0;x<100;x++)
			{
				child[x]=array1[x];
			}
			return child;
		}
		
		
	}
	
	public static double [] crossover2(double[]array1,double []array2,double chance,int point)
	{
		double [] child = new double[100];
		if(chance>=70.0)
		{
			for(int x=0;x<point;x++)
			{
				child[x]=array2[x];
			}
			
			while(point<100)
			{
				child[point] = array2[point];
				point++;
			}
			
			return child;
		}
		
		else
		{
			for(int x=0;x<100;x++)
			{
				child[x]=array2[x];
			}
			return child;
		}
		
	}
	
	public static void clone(double[][]array1,double [][]array2)
	{
		for(int x=0;x<300;x++)
		{
			for(int y=0;y<100;y++)
			{
				array1[x][y]=array2[x][y];
			}
		}
	}
}



