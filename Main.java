import java.util.*;
import java.io.*;

class PurchaseThread extends Thread
{
    String productId;
    int quantity;
    PurchaseThread(String productId,int quantity)
    {
        this.productId=productId;
        this.quantity=quantity;
    }
    public void run()
    {
        Inventory.purchase_product(productId,quantity);
    }
}
class negativeException extends Exception
{
    public negativeException(String str)
    {
        super(str);
    }
}
class Product
{
    public String productId;
    public String productName;
    public double productPrice;
    public int productQuantity;

    Product(String id, String name, double price, int quantity)
    {
        this.productId=id;
        this.productName=name;
        this.productPrice=price;
        this.productQuantity=quantity;
    }
}
class Inventory
{
    static HashMap<String,Product> map=new HashMap<String, Product>();

    static void readProduct()
    {
        try
        {
            File myObj = new File("/src/productDetails.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine())
            {
                String data = myReader.nextLine();
                String[] productDetails=data.split(",");
                String productId=productDetails[0];
                String productName=productDetails[1];
                double productPrice=Double.parseDouble(productDetails[2]);
                int productQuantity=Integer.parseInt(productDetails[3]);
                Product temp=new Product(productId,productName,productPrice,productQuantity);
                Inventory.addProduct_in_map(temp);
            }
            myReader.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
    static void addNewProduct() throws negativeException
    {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter id of Product : ");
        String id=sc.next();
        System.out.println("Enter Name of Product : ");
        String name=sc.next();
        System.out.println("Enter Product Price : ");
        double price=sc.nextDouble();
        if(price<0)
        {
            throw new negativeException("No Negative Values allowed");
        }
        System.out.println("Enter Product Quantity : ");
        int quant=sc.nextInt();
        if(quant<0)
        {
            throw new negativeException("No Negative Values allowed");
        }
        Product temp=new Product(id,name,price,quant);
        Inventory.addProduct_in_map(temp);
        String product=id+","+name+","+price+","+quant;
        try
        {
            FileWriter myWriter = new FileWriter("src/productDetails.txt",true);
            BufferedWriter bf=new BufferedWriter(myWriter);
            bf.write(product);
            bf.newLine();
            bf.close();
        }
        catch (IOException e)
        {
            System.out.println("An error occurred in logging file");
            e.printStackTrace();
        }


    }

    static void updateLog(String logInfo)
    {
        try
        {
            FileWriter myWriter = new FileWriter("src/logs.txt",true);
            BufferedWriter bf=new BufferedWriter(myWriter);
            bf.write(logInfo);
            bf.newLine();
            bf.close();
        }
        catch (IOException e)
        {
            System.out.println("An error occurred in logging file");
            e.printStackTrace();
        }

    }
    static void updatePurchasesLog(String logInfo)
    {
        try
        {
            FileWriter myWriter = new FileWriter("src/purchaseDetails.txt",true);
            BufferedWriter bf=new BufferedWriter(myWriter);
            bf.write(logInfo);
            bf.newLine();
            bf.close();
        }
        catch (IOException e)
        {
            System.out.println("An error occurred in logging purchases file");
            e.printStackTrace();
        }

    }

    static void addProduct_in_map(Product newProduct)
    {
        if(map.get(newProduct.productId)!=null)
        {
            Product curr=map.get(newProduct.productId);
            if(curr.productName.equals(newProduct.productName))
            {
                if(curr.productPrice<newProduct.productPrice)
                {
                    curr.productPrice=newProduct.productPrice;
                }
                else if(curr.productQuantity<newProduct.productQuantity)
                {
                    curr.productQuantity=newProduct.productQuantity;
                }
            }
            else
                {
                System.out.println("Already id is used with other item");
            }
            map.put(curr.productId,curr);
        }
        else{
            map.put(newProduct.productId,newProduct);

            Inventory.updateLog("Product read from file and added!!");
        }
    }
    static void getProductDetails(){
        System.out.println("P-ID\t"+"P-Name\t"+"P-Price\t"+"P-Quantity");
        for(Map.Entry<String,Product> entry:map.entrySet()){
            Product curr=entry.getValue();
            System.out.println(curr.productId+" "+curr.productName+" "+curr.productPrice+" "+curr.productQuantity);
        }
        Inventory.updateLog("Product Details Viewed");
    }
    static void getSpecificProduct(String id){
        Product curr=map.get(id);
        System.out.println("P-ID\t"+"P-Name\t"+"P-Price\t"+"P-Quantity");
        System.out.println(curr.productId+" "+curr.productName+" "+curr.productPrice+" "+curr.productQuantity);
        Inventory.updateLog("Specific Product Viewed : "+curr.productName);
    }
    static synchronized void purchase_product(String id,int quantity){
        Product currProduct=map.get(id);
        if(currProduct.productQuantity>quantity){
            //purchasing product
            try{
                Thread.sleep(1000);

            }catch (InterruptedException e){
                System.out.println("Exception : "+e);
            }
            //updating inventory
            currProduct.productQuantity=currProduct.productQuantity-quantity;
            map.put(currProduct.productId,currProduct);
            updatePurchasesLog("Purchased "+quantity+" "+currProduct.productName+" "+Thread.currentThread().getName());
        }
        else{
            System.out.println("Insufficient Stock for the Product");

        }

    }

    static void General_Report(){
        double sum=0;
        try {
            FileWriter myWriter = new FileWriter("src/totalReport.txt",true);
            BufferedWriter bf=new BufferedWriter(myWriter);
            for(Map.Entry<String,Product> entry:map.entrySet()){
                Product curr=entry.getValue();
                sum=sum+(curr.productPrice*curr.productQuantity);
                String product=curr.productId+","+curr.productName+","+curr.productPrice+","+curr.productQuantity;
                bf.write(product);
                bf.newLine();
            }
            bf.write("The Total Value of Inventory : "+sum);
            bf.newLine();
            bf.close();
        } catch (IOException e) {
            System.out.println("An error occurred in logging in General Report file");
            e.printStackTrace();
        }

        System.out.println("Total Value of Inventory is : "+sum);
        Inventory.updateLog("General Report is Updated ");
    }
    static void Individual_Report(String id)
    {
        Product curr=map.get(id);
        String product=curr.productId+","+curr.productName+","+curr.productPrice+","+curr.productQuantity;
        try {
            FileWriter myWriter = new FileWriter("src/productReport.txt",true);
            BufferedWriter bf=new BufferedWriter(myWriter);
            bf.write(product);
            bf.newLine();
            bf.write("Value is : "+curr.productPrice*curr.productQuantity);
            bf.close();
        } catch (IOException e)
        {
            System.out.println("An error occurred in logging file");
            e.printStackTrace();
        }
        Inventory.updateLog("Individual Product Report is Updated");


    }
    static boolean isEmpty()
    {
        return map.isEmpty();
    }

}

public class Main
{
    static void add()
    {
        try
        {
            Inventory.addNewProduct();
        }
        catch(negativeException e)
        {
            System.out.println(e);
        }
    }

    static void purchase()
    {
        Scanner sc=new Scanner(System.in);
        if(!Inventory.isEmpty())
        {
            Inventory.getProductDetails();
            System.out.println("Enter Product Id to purchase : ");
            String p_id=sc.next();
            System.out.println("Enter Product Quantity : ");
            int quantity=sc.nextInt();
            Thread t1=new PurchaseThread(p_id,quantity);
            //Thread t2=new PurchaseThread("P-ID-1",1);
            t1.start();
            //t2.start();
        }
        else
            {
            System.out.println("Inventory is Empty!!");
        }

    }

    static void showDetails()
    {
        Scanner sc=new Scanner(System.in);
        if(!Inventory.isEmpty())
        {
            System.out.println("Enter Product Id to get Details : ");
            String p_id=sc.next();
            Inventory.getSpecificProduct(p_id);
        }
        else
            {
            System.out.println("Inventory is Empty!!");
        }

    }
    static void displayReport()
    {
        Scanner sc=new Scanner(System.in);
        if(!Inventory.isEmpty())
        {
            System.out.println("Enter Product ID : ");
            String p_id=sc.next();
            Inventory.getSpecificProduct(p_id);
            Inventory.Individual_Report(p_id);
        }
        else
            {
            System.out.println("Inventory is Empty!!");
        }

    }
    static void displayTotalReport()
    {
        Scanner sc=new Scanner(System.in);
        if(!Inventory.isEmpty())
        {
            Inventory.getProductDetails();
            Inventory.General_Report();

        }
        else{
            System.out.println("Inventory is Empty!!");
        }

    }

    public static void main(String[] args)
    {
        Scanner sc=new Scanner(System.in);
        Inventory.readProduct();
        boolean loop=true;
        while(loop)
        {
            System.out.println("\nChoices:");
            System.out.println("1.Add Item");
            System.out.println("2.Purchase");
            System.out.println("3.Display Specific Product Details");
            System.out.println("4.Display Report");
            System.out.println("5.Exit");
            System.out.print("Enter Choice:");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    add();
                    break;

                case 2:
                    purchase();
                    break;

                case 3:
                    showDetails();
                    break;

                case 4:
                    System.out.println("\n1.Display Report of Specific Product");
                    System.out.println("2.Display Report of All Products");
                    System.out.print("Enter Inventory Choice:");
                    int report_choice = sc.nextInt();
                    if (report_choice == 1)
                    {
                        displayReport();
                    }
                    else if (report_choice == 2)
                    {
                        displayTotalReport();
                    }
                    break;

                case 5:
                    loop = false;
                    break;

                default:
                    System.out.println("Invalid Choice\n");
            }
        }
    }
}
