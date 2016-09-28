using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System.Collections.Generic;
using System;
public class GameManager : MonoBehaviour 
{
    private Basket myBasket;
    private System.Random rand = new System.Random();
    private List<GameObject> appleObject = new List<GameObject>();
    private List<GameObject> lemonObject = new List<GameObject>();
    private List<GameObject> watermelonObject = new List<GameObject>();
    private List<Fruits> fruitList1 = new List<Fruits>();
    public InputField appleInputField, lemonInputField, watermelonInputField;
    public int removeCount;

    public void OnClickDiscard()
    {
        if (appleInputField.text != "")
        {
            int appleWeight = Convert.ToInt32(appleInputField.text);
            removeCount=myBasket.DiscardFruit("apple", appleWeight);
            for (int x = 0; x < removeCount; x++)
            {
                Destroy(appleObject[x]);
            }
        }
       
        if (lemonInputField.text != "")
        {
            int lemonWeight = Convert.ToInt32(lemonInputField.text);
            removeCount = myBasket.DiscardFruit("lemon", lemonWeight);
            for (int x = 0; x < removeCount; x++)
            {
                Destroy(appleObject[x]);
            }
        }
        
        if (watermelonInputField.text != "")
        {
            int watermelonWeight = Convert.ToInt32(watermelonInputField.text);
            removeCount = myBasket.DiscardFruit("watermelon", watermelonWeight);
            for (int x = 0; x < removeCount; x++)
            {
                Destroy(appleObject[x]);
            }
        }
    }
	
    public void CreateObject(string fruitType,List <GameObject> fruitObjectList,List<Fruits> fruitList)
    {
        //fruitList= myBasket.GetFruitType(fruitType);
        foreach (Fruits fruit in myBasket.GetFruitType(fruitType))
        {
           
            GameObject fruitObject = GameObject.CreatePrimitive(PrimitiveType.Cube);
            fruitObject.transform.position = new Vector3((float)rand.NextDouble() * 16 - 8, (float)rand.NextDouble() * 6 - 3, 0);
            
            Material material = new Material(fruitObject.GetComponent<Renderer>().material);
            material.EnableKeyword("_EMISSION");
            if(fruit.FruitColor=="red")
            {
                material.SetColor("_EmissionColor", Color.red);
            }
            else if(fruit.FruitColor=="green")
            {
                 material.SetColor("_EmissionColor", Color.green);
            }
            else
            {
                 material.SetColor("_EmissionColor", Color.yellow);
            }

            fruitObject.GetComponent<Renderer>().material = material;
            fruitObjectList.Add(fruitObject);
        }
    }

    public void RemoveAll()
    {
        foreach (GameObject fruit in appleObject)
        {
            Destroy(fruit);
        }
        appleObject.Clear();
        
        
        foreach (GameObject fruit in lemonObject)
        {
            Destroy(fruit);
        }
        lemonObject.Clear();
       
        foreach (GameObject fruit in watermelonObject)
        {
            Destroy(fruit);
        }
        watermelonObject.Clear();
      }

    public void onClickRestart()
    {
        Restart();
    }

    public void Restart()
    {
        RemoveAll();
        myBasket = new Basket();
        myBasket.AddFruit();

        CreateObject("apple", appleObject, fruitList1);
        CreateObject("lemon", lemonObject, fruitList1);
        CreateObject("watermelon", watermelonObject, fruitList1);
    }

    void Start()
    {
        Restart();
    }


    void Update()
    {

    }
    


}
