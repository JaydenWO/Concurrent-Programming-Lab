import java.util.Queue;
public class TakeThread implements Runnable
{
	Queue queue;
	Job job;
	int totalProcess,first,twice,triple;
	public TakeThread(Queue queue,Job job)
	{
		this.queue=queue;
		this.job = job;
	}
	
	public void run()
	{
		int localProcess=0,timer=360;
		while(true)
		{
			
			if(timer==0)
			{
				break;
			}
			timer--;
			int id,t,it=8;
			Integer time;
			synchronized(queue)
			{
				while(queue.isEmpty())
				{
					try 
					{
						System.out.print("Bank Counter:Waiting for jobs\n");
						queue.wait();
					} 
					catch (InterruptedException e) 
					{
				
					}
				}
			}
			synchronized(queue)
			{
				Job myJob = (Job) queue.remove();
				id = myJob.getID();
				t = myJob.getTime();
				time = t;
			}
			
			int count=0,hiddenTime=0;
			if(t<=8&&t>0)
			{
				first++;
			}
			
			else if(t<=16&&t>=9)
			{
				twice++;
			}
			
			else
			{
				triple++;
			}
			while(t>hiddenTime)
			{
				if(count>=9)
				{
					it=8;
					count=0;
					time = time - 8;
					if(time<8)
					{
						time = t - hiddenTime + 1;
						it = t - hiddenTime + 1;
						if(it<0)
						{
							localProcess++;
							totalProcess += localProcess;
							break;
						}
					}
					
				}
				
				else
				{
					System.out.printf("Id:%d Time:%d Time left:%d seconds \n",id,time,it);
					try 
					{
						Thread.sleep(1000);
					} 
					catch (InterruptedException e) 
					{
					
					}
					it--;
					count++;
					hiddenTime++;
				}
			}
		}	
		
		System.out.printf("Job processed by this counter:%d\n",localProcess);
		
		}
	}

