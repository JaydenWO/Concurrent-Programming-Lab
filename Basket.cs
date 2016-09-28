using System;
using System.Collections.Generic;

public class Basket 
{

    private static List<Fruits> fruitList = new List<Fruits>();
    private static List<Fruits> fruitList1 = new List<Fruits>();
    

    public void AddFruit()
    {
        for (int i=0; i<3; i++)
        {
            Fruits apple = new Fruits("apple", "red", 300, 800);
            fruitList.Add(apple);
            
        }

       for (int i=0; i<6; i++)
        {
            Fruits lemon = new Fruits("lemon", "yellow", 100, 600);
            fruitList.Add(lemon);
            
        }

       for (int i=0; i<3; i++)
        {
            Fruits watermelon = new Fruits("watermelon", "green", 300, 1000);
            fruitList.Add(watermelon);
        }
    }

    public int DiscardFruit(string name, int weight)
    {
        int count = 0;
        foreach (Fruits fruit in fruitList)
        {
            if (fruit.FruitName != name && fruit.FruitWeight < weight)
            {
                fruitList.Remove(fruit);
                count++;
            }
        }
        return count;
        //fruitList.RemoveAll(x => x.FruitName != name && x.FruitWeight < weight);
    }

    public List<Fruits> GetFruitType(string name)
    {
         fruitList1= fruitList;
        fruitList1.RemoveAll(fruit => fruit.FruitName != name);
        return fruitList1;
    }
	
}
