import java.util.Random;
public class EvaluateThread implements Runnable
{
	double [][]array;
	int row;
	Random rand = new Random();
	double minValue=Double.POSITIVE_INFINITY;
	public EvaluateThread(double [][]array,int row)
	{
		this.array = array;
		this.row = row;
		
	}
	
	public void run()
	{
		double num,value=0;
		
		for(int a=0;a<350;a++)
		{
			for(int z=0;z<100;z++)
			{
				value += (z+1)*(array[row][z]*array[row][z]);
			}
			if(minValue>value)
			{
				minValue = value;
			}
			
			for(int x=0;x<100;x++)
			{
				num = -0.5 + (0.5 - (-0.5))* rand.nextDouble();
				if (num > -5.12 && num < 5.12)
				{
					array[row][x] = num;
				}
			}
		}
	}
	
	public double getMinValue()
	{
		return minValue;
	}
	
		
}

