using System;

public class Fruits 
{
    private string fruitName,fruitColor;
    private int fruitWeight;
    private static Random rand = new Random();

    public Fruits(string name, string color,int minWeight,int maxWeight)
    {
        fruitName = name;
        fruitColor = color;
        fruitWeight = randomWeight(minWeight,maxWeight);
    }
    public int randomWeight(int min, int max)
    {
        return rand.Next(min, max);
    }
    public string FruitName
    {
        set
        {
            fruitName = value;
        }
        get
        {
            return fruitName;
        }
    }

    public string FruitColor
    {
        set
        {
            fruitColor = value;
        }
        get
        {
            return fruitColor;
        }
    }

    public int FruitWeight
    {
        set
        {
			fruitWeight=value;
		}
        get
        {
            return fruitWeight;
        }
    }

    public override string ToString()
    {
        return string.Format("Fruitname: {0}\n FruitColor:{1}\n FruitWeight:{2}",
            fruitName, fruitColor, fruitWeight);
    }



}
