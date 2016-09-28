using System;
using System.Collections.Generic;

public class Basket 
{

    private static List<Fruits> fruitList = new List<Fruits>();
   
    public void AddFruit()
    {
        fruitList.Clear();
        for (int a=0; a<3; a++)
        {
            Fruits apple = new Fruits("apple", "red", 300, 800);
            fruitList.Add(apple);
            
        }

       for (int b=0; b<6; b++)
        {
            Fruits lemon = new Fruits("lemon", "yellow", 100, 600);
            fruitList.Add(lemon);
            
        }

       for (int c=0; c<3; c++)
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
        List<Fruits> fruitList1 = new List<Fruits>(fruitList);
        fruitList1.RemoveAll(fruit => fruit.FruitName != name);
        return fruitList1;
    }

   
	
}
