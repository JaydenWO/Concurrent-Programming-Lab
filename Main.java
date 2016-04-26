import java.util.Random;
public class Main 
{
	public static void main(String[] args) 
	{
		double[][]array = new double[200][100];
		Thread[] threadarray = new Thread[200];
		double minValue=Double.POSITIVE_INFINITY,num;
		long start=0;
		long end=0;
		Random rand = new Random();
		
		for(int x=0;x<200;x++)
		{
			for(int y=0;y<100;y++)
			{
				num = -5.12 + (5.12 + 5.12)* rand.nextDouble();
				array[x][y] = num;
			}
		}
		
		start = System.currentTimeMillis();
		for(int a=0;a<200;a++)
		{
			EvaluateThread evaluate = new EvaluateThread(array,a);
			Thread task = new Thread(evaluate);
			threadarray[a] = new Thread(task);
			threadarray[a].start();
			
		}
		
		for(int x=0;x<200;x++)
		{
			try
			{
				threadarray[x].join();
			}
			catch(InterruptedException e) 
			{
			
			}
			
			if(threadarray[x].getMinValue()<minValue)
			{
				minValue = threadarray[x].getMinValue();
			}
			
		}
		end =  System.currentTimeMillis();
		System.out.printf("The minimum value is:%.6f",minValue);
		System.out.printf("Time taken: %d Miliseconds", end-start);

	}
	
	
	
	

}
