import java.util.*;
import java.sql.*;
class Global{
   static String name="";
   static String pass="";
   static String phno="";
   static int bal=0;
}

class thread10 extends Thread{
    Statement stmt;
    Connection con;
    String ph;

    thread10(Connection con){
        this.con = con;

    }
    @Override
    public void run() {
        int balance=Global.bal;
        String name=Global.name;

        try {
            stmt = con.createStatement();
            String sta = "UPDATE bank SET balance='" + balance + "'where user_name='" + name + "'and phonenumber='" +Global.phno + "'";
            stmt.executeUpdate(sta);
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
class thread20 extends Thread{
    Statement stmt;
    Connection con;
    String transname;
    String tphno;
    int trbal;
    thread20(Connection con,String transname,int trbal,String tphno){
        this.con = con;
        this.transname=transname;
        this.trbal=trbal;
        this.tphno=tphno;
    }
    @Override
    public void run() {
        try {
            stmt = con.createStatement();
            String sta = "UPDATE bank SET balance='" + trbal + "'where user_name='" + transname + "'and phonenumber='" + tphno + "'";
            stmt.executeUpdate(sta);
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

public class Main {
    static Connection con;
    static Statement stmt;
    static ResultSet rst;

    static void Login() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("1.Deposit Money\n2.withdraw money\n3.Transfer Money\n4.view account balance\n5.exit");
            System.out.println("Enter your choice:");
            int choi = sc.nextInt();
            if (choi == 1)
            {
                System.out.println("Enter the amount to be deposited:");
                int amt = sc.nextInt();

                    stmt = con.createStatement();
                    String state = "SELECT balance FROM `bank` WHERE user_name='" + Global.name + "'and password='" + Global.pass + "'";
                    rst = stmt.executeQuery(state);
                    if (rst.next()) {
                        Global.bal = rst.getInt(1);
                        Global.bal += amt;
                        String sta = "UPDATE bank SET balance='" + Global.bal + "'where user_name='" + Global.name + "'and password='" + Global.pass + "'";
                        stmt.executeUpdate(sta);
                        String s="self";
                        String st1="INSERT INTO `transaction_history`(`Credit_From`, `Deposited_To`, `Amount`) VALUES ('"+s+"','"+Global.name+"','"+amt+"')";
                        stmt.executeUpdate(st1);
                        System.out.println("Amount deposited");
                        Login();
                    }
            }
            else if (choi == 2) {
                System.out.println("Enter the amount to be withdrawn:");
                int withdraw = sc.nextInt();
                    stmt = con.createStatement();
                    String state = "SELECT balance FROM `bank` WHERE user_name='" + Global.name + "'and password='" + Global.pass + "'";
                    rst = stmt.executeQuery(state);
                    if (rst.next()) {
                        Global.bal = rst.getInt(1);
                        if ((Global.bal - withdraw) >= 1000) {
                            Global.bal -= withdraw;
                            String s="self";
                            String sta = "UPDATE bank SET balance='" + Global.bal + "'where user_name='" + Global.name + "'and password='" + Global.pass + "'";
                            stmt.executeUpdate(sta);
                            String st1="INSERT INTO `transaction_history`(`Credit_From`, `Deposited_To`, `Amount`) VALUES ('"+Global.name+"','"+s+"','"+withdraw+"')";
                            stmt.executeUpdate(st1);
                            System.out.println("Amount Withdrawn Successfully\n\n");
                            Login();
                        } else {
                            System.out.println("Insufficient Balance\n\n");
                            Login();
                        }
                    }
            }
            else if (choi == 3)
            {
                int flag = 0;
                System.out.println("Enter Details of the Account Holder to whom You need to send Money");
                System.out.println("Enter the name :");
                String transsname = sc.next();
                System.out.println("Enter phone number:");
                String ph = sc.next();
                    stmt = con.createStatement();
                    String stm = "SELECT * FROM `bank` WHERE user_Name='" + transsname + "'and phonenumber='" + ph + "'";
                    rst = stmt.executeQuery(stm);
                    if (rst.next()) {
                        flag = 1;
                    }
                if (flag == 1)
                {
                    stmt = con.createStatement();
                    String state = "SELECT balance FROM `bank` WHERE user_name='" + Global.name + "'and password='" + Global.pass + "'";
                    rst = stmt.executeQuery(state);
                    if (rst.next()) {
                            Global.bal = rst.getInt(1);
                    }
                    System.out.println("Enter the amount to transfer:");
                    int tamt = sc.nextInt();
                    int availbal=0;
                    availbal = Global.bal - tamt;
                    if (availbal >= 1000)
                    {
                        Global.bal=availbal;
                        stmt = con.createStatement();
                        String state1 = "SELECT balance FROM `bank` WHERE user_name='" + transsname + "'and phonenumber='" + ph + "'";
                        rst = stmt.executeQuery(state1);
                        if (rst.next())
                        {
                            int trbal = rst.getInt(1);
                            trbal += tamt;
                            thread10 t1=new thread10(con);
                            thread20 t2=new thread20(con,transsname,trbal,ph);
                            t1.start();
                            t2.start();
                            System.out.println("\nTRANSACTION SUCCESSFUL");
                            stmt = con.createStatement();
                            String stat1="INSERT INTO `transaction_history`(`Credit_From`, `Deposited_To`, `Amount`) VALUES ('"+Global.name+"','"+transsname+"','"+tamt+"')";
                            stmt.executeUpdate(stat1);
                            Login();
                        }
                    }
                    else {
                        System.out.println("\nInsufficient Balance !!\n");
                        Login();
                    }
                }
                else {
                    System.out.println("Invalid Account details..\n");
                    Login();
                }
            }
            else if (choi == 4)
            {
                stmt = con.createStatement();
                String stamt = "SELECT balance FROM `bank` WHERE user_name='" + Global.name + "'and password='" + Global.pass + "'";
                rst = stmt.executeQuery(stamt);
                if (rst.next()) {
                    System.out.println("your Available Balance: " + rst.getInt(1));
                }
                Login();
            }
            else if (choi == 5) {
                menu();
            }
            else {
                System.out.println("\n### Invalid Input ###\n");
                Login();
            }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    static void menu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("1.Signup\n2.Login\n3.Exit");
        System.out.println("your choice:");
        String name = "";
        String pass = "";
        String num = "";
        int bal = 0;
        int ch = sc.nextInt();
        if (ch == 1) {
            System.out.println("\n***** Sign-Up *****");
            System.out.println("Enter your name:");
            name = sc.next();
            System.out.println("Enter your password:");
            pass = sc.next();
            System.out.println("Enter your Phone number:");
            num = sc.next();
            bal = 1000;
            try {
                stmt = con.createStatement();
                String st = "INSERT INTO `bank`(`user_Name`, `Password`, `phonenumber`,`balance`) VALUES ('" + name + "','" + pass + "','" + num + "','" + bal + "')";
                String stm = "SELECT * FROM `bank` WHERE phonenumber = '" + num + "'";
                rst = stmt.executeQuery(stm);
                if (rst.next())
                    System.out.println("User already exists ");
                else {
                    stmt.executeUpdate(st);
                    System.out.println("Account Created Successfully");
                    menu();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (ch == 2)
        {
            System.out.println("*****   LOGIN   *****");
            System.out.println("Enter your Name");
            String cname = sc.next();
            System.out.println("Enter your password:");
            String cpass = sc.next();
            try {
                stmt = con.createStatement();
                String stm = "SELECT * FROM `bank` WHERE user_Name='" + cname + "'and Password='" + cpass + "'";
                rst = stmt.executeQuery(stm);
                if (rst.next()) {
                    System.out.println(rst.getString(2)+" Login Successful...");
                    Global.pass = cpass;
                    Global.name = cname;
                    Global.phno=rst.getString(4);
                    Login();
                } else {
                    System.out.println("Invalid login credentials.");
                    menu();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        else if(ch==3)
        {
            System.out.println("****    Thank You User  ****");
        }
        else
        {
            System.out.println("Invalid Input");
            menu();
        }
    }
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ashwn", "root", "");
            System.out.println("connection Successful");
            menu();
        }
        catch (SQLException | ClassNotFoundException ex)
        {
            System.out.println(ex.getMessage());
        }

    }
}
