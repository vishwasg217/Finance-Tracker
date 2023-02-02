
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.UUID;
class Record
{
    static String user_id;
    static String password;
    static String first_name;
    static String last_name;
    static int acc_bal;
    public static PreparedStatement ps;
    static void login() throws Exception
    {
        Scanner in = new Scanner(System.in);

        System.out.println("Enter the user ID: ");
        user_id=in.next();
        System.out.println("Enter the password: ");
        password=in.next();
        ResultSet rs = MainClass.st.executeQuery("SELECT user_id, password FROM user");
        while(rs.next())
        {
            if((user_id.equals(rs.getString("user_id")))&&(password.equals(rs.getString("password"))))
            {
                System.out.println("Login Successful");

                Record.mainMenu();
            }
        }
        System.out.println("Login Unsuccessful");
        user_id="";
        password="";
    }

    private static void mainMenu() throws Exception {
        Scanner in = new Scanner(System.in);
        String query = "select account_balance from user where user_id='"+user_id+"'";
        ResultSet rs=MainClass.st.executeQuery(query);
        rs.next();
        acc_bal = rs.getInt("account_balance");
        System.out.println("YOUR ACCOUNT BALANCE: Rs. "+acc_bal);
        for(;;)
        {
            System.out.println("Choose an option:\n1. Make a transaction\n2. View all transactions\n3. Filter according to time and category\n4. Finance Analyzer\n5. Change Parameters for Finance Analyser\n6. Logout");
            int ch=in.nextInt();
            switch(ch)
            {
                case 1 -> Record.makeTransaction();
                case 2 -> Record.allRecords();
                case 3 -> Record.filter();
                case 4 -> Record.financeAnalyzer();
                case 5 -> Record.changeParameters();
                case 6 -> {
                                user_id="";
                                password="";
                                MainClass.main(null);
                }
            }
        }
    }

    private static void changeParameters() throws SQLException {
        Scanner in = new Scanner(System.in);
        int[] para = new int[12];
        ResultSet rs = MainClass.st.executeQuery("select parameters_id from user where user_id = '"+user_id+"'");
        rs.next();
        int para_id = rs.getInt(1);
        rs=MainClass.st.executeQuery("select * from parameters where id= '"+para_id+"'");
        rs.next();
        for(int i=0;i<para.length;i++)
            para[i]=rs.getInt(i+2);

        System.out.println("1. Salary: "+para[0]);
        System.out.println("2. Stocks: "+para[1]);
        System.out.println("3. Fixed Deposits: "+para[2]);
        System.out.println("4. Mutual Funds: "+para[3]);
        System.out.println("5. Real Estate: "+para[4]);
        System.out.println("6. Rent: "+para[5]);
        System.out.println("7. Food: "+para[6]);
        System.out.println("8. Travel: "+para[7]);
        System.out.println("9. Utilities: "+para[8]);
        System.out.println("10. Leisure: "+para[9]);
        System.out.println("11. EMI/Loans: "+para[10]);
        System.out.println("12. Miscellaneous: "+para[11]);
        System.out.println();
        System.out.println("These are the current values set for each category. Please select a category for which you'd like change the value: ");
        int ch=in.nextInt();
        String category;
        switch(ch)
        {
            case 1 -> category = "salary";

            case 2 -> category = "stocks";

            case 3 -> category = "fds";

            case 4 -> category = "mfs";

            case 5 -> category = "re";

            case 6 -> category = "rent";

            case 7 -> category = "food";

            case 8 -> category = "travel";

            case 9 -> category = "utilities";

            case 10 -> category = "leisure";

            case 11 -> category = "emi_loans";

            case 12 -> category = "misc";

            default -> throw new IllegalStateException("Unexpected value: " + ch);
        }
        System.out.println("Enter the new value: ");
        int newValue=in.nextInt();

        ps=MainClass.con.prepareStatement("update parameters set "+category+"="+newValue+" where id="+para_id);
        int c=ps.executeUpdate();
        if(c==1)
            System.out.println("Parameters changed");
    }

    private static void financeAnalyzer() throws SQLException {
        Scanner in = new Scanner(System.in);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate st = null;
        LocalDate ed = null;
        Date start;
        Date end;
        String startdate, enddate;
        System.out.println("Choose time filter: ");
        System.out.println("1. All time\t2. last 1 week\t3. last 1 month\t4. last 3 months\t5. last 6 months\t6. last 1 year\t7. custom");
        int ch = in.nextInt();
        switch (ch)
        {
            case 1 -> {
                ed = LocalDate.now();
                st = LocalDate.parse("2002/21/11", dtf);
            }
            case 2 -> {
                ed = LocalDate.now();
                st = ed.minusDays(7);
            }
            case 3 -> {
                ed = LocalDate.now();
                st = ed.minusMonths(1);
            }
            case 4 -> {
                ed = LocalDate.now();
                st = ed.minusMonths(3);
            }
            case 5 -> {
                ed = LocalDate.now();
                st = ed.minusMonths(6);
            }
            case 6 -> {
                ed = LocalDate.now();
                st = ed.minusMonths(12);
            }
            case 7 -> {
                System.out.println("Enter the start date: ");
                startdate = in.next();
                System.out.println("Enter the end date: ");
                enddate = in.next();
                st = LocalDate.parse(startdate, dtf);
                ed = LocalDate.parse(enddate, dtf);
            }
            default -> System.out.println("Please choose a valid option");
        }

        assert st != null;
        start = Date.valueOf(st);
        end = Date.valueOf(ed);

        System.out.println("Choose category for this transaction: ");
        System.out.println("1. Salary\t2. Stocks\t3. FDs\t4. Mutual funds\t5. Real estate\t6. Rent");
        System.out.println("7. Food\t8. Travel\t9. Utilities\t10. Leisure\t11. EMI/Loans\t12. Miscellaneous");
        ch=in.nextInt();
        int cat_id;
        String category;
        switch(ch)
                {
                    case 1 -> {
                        cat_id = 1;
                        category = "salary";
                    }
                    case 2 -> {
                        cat_id = 2;
                        category = "stocks";
                    }
                    case 3 -> {
                        cat_id = 3;
                        category = "fds";
                    }
                    case 4 -> {
                        cat_id = 4;
                        category = "mfs";
                    }
                    case 5 -> {
                        cat_id = 5;
                        category = "re";
                    }
                    case 6 -> {
                        cat_id = 6;
                        category = "rent";
                    }
                    case 7 -> {
                        cat_id = 7;
                        category = "food";
                    }
                    case 8 -> {
                        cat_id = 8;
                        category = "travel";
                    }
                    case 9 -> {
                        cat_id = 9;
                        category = "utilities";
                    }
                    case 10 -> {
                        cat_id = 10;
                        category = "leisure";
                    }
                    case 11 -> {
                        cat_id = 11;
                        category = "emi_loans";
                    }
                    case 12 -> {
                        cat_id = 12;
                        category = "misc";
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + ch);
                }


        ResultSet rs = MainClass.st.executeQuery("select sum(amount) from record where receiver_id = '"+user_id+"' and category_id="+cat_id+" and trans_date between '"+start+"' and '"+end+"'");
        rs.next();
        int income = rs.getInt(1);
        rs = MainClass.st.executeQuery("select sum(amount) from record where sender_id = '"+user_id+"' and category_id="+cat_id+" and trans_date between '"+start+"' and '"+end+"'");
        rs.next();
        int expense = rs.getInt(1);
        int netIncome = income-expense;
        System.out.println("Net Income: Rs. "+netIncome);
        rs = MainClass.st.executeQuery("select parameters_id from user where user_id='"+user_id+"'");
        rs.next();
        int para_id=rs.getInt(1);
        long days = ChronoUnit.DAYS.between(st, ed);
        System.out.println(days);
        rs = MainClass.st.executeQuery("select "+category+" from parameters where id="+para_id);
        rs.next();
        int para =rs.getInt(1);
        float a1=Math.abs(netIncome);
        float a2 = days*para;
        System.out.println("days: "+days);
        System.out.println("para: "+para);
        System.out.println("a2: "+a2);
        float outcome;
        outcome=((a1-a2)/a2)*100;
        if(cat_id<6)
        {
            if(outcome>0)
                System.out.println("Your net income is "+outcome+"% more than the standard");
            else
                System.out.println("Your net income is "+String.format("%.5f", Math.abs(outcome))+"% less than the standard");
        }
        else
        {
            if(outcome>0)
                System.out.println("Your net spend is "+outcome+"% more than the standard");
            else
                System.out.println("Your net spend is "+Math.abs(outcome)+"% less than the standard");
        }


    }

    private static void filter() throws Exception
    {
        Scanner in = new Scanner(System.in);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate st = null;
        LocalDate ed = null;
        Date start;
        Date end;
        String startdate, enddate;
        System.out.println("Choose time filter: ");
        System.out.println("1. All time\t2. last 1 week\t3. last 1 month\t4. last 3 months\t5. last 6 months\t6. last 1 year\t7. custom");
        int ch = in.nextInt();
        switch (ch)
        {
            case 1 -> {
                ed = LocalDate.now();
                st = LocalDate.parse("2002/21/11", dtf);
            }
            case 2 -> {
                ed = LocalDate.now();
                st = ed.minusDays(7);
            }
            case 3 -> {
                ed = LocalDate.now();
                st = ed.minusMonths(1);
            }
            case 4 -> {
                ed = LocalDate.now();
                st = ed.minusMonths(3);
            }
            case 5 -> {
                ed = LocalDate.now();
                st = ed.minusMonths(6);
            }
            case 6 -> {
                ed = LocalDate.now();
                st = ed.minusMonths(12);
            }
            case 7 -> {
                System.out.println("Enter the start date: ");
                startdate = in.next();
                System.out.println("Enter the end date: ");
                enddate = in.next();
                st = LocalDate.parse(startdate, dtf);
                ed = LocalDate.parse(enddate, dtf);
            }
            default -> System.out.println("Please choose a valid option");
        }

        assert st != null;
        start = Date.valueOf(st);
        end = Date.valueOf(ed);

        System.out.println("Choose category for this transaction: ");
        System.out.println("1. Salary\t2. Stocks\t3. FDs\t4. Mutual funds\t5. Real estate\t6. Rent");
        System.out.println("7. Food\t8. Travel\t9. Utilities\t10. Leisure\t11. EMI/Loans\t12. Miscellaneous");
        ch=in.nextInt();
        int cat_id;
        cat_id = switch(ch)
                {
                    case 1 -> 1;
                    case 2 -> 2;
                    case 3 -> 3;
                    case 4 -> 4;
                    case 5 -> 5;
                    case 6 -> 6;
                    case 7 -> 7;
                    case 8 -> 8;
                    case 9 -> 9;
                    case 10 -> 10;
                    case 11 -> 11;
                    case 12 -> 12;
                    default -> throw new IllegalStateException("Unexpected value: " + ch);
                };

        ResultSet rs = MainClass.st.executeQuery("select substring(record.transaction_ID, 1, 6) as transaction_id, record.trans_date, concat(user.first_name, \" \", user.last_name) as sender_name, concat(u2.first_name, \" \", u2.last_name) as receiver_name, record.amount, category.category_name from record join user on record.sender_id=user.user_id  join user u2 on record.receiver_id=u2.user_id join category on record.category_id=category.category_id\n" +
                "where (sender_id='"+user_id+ "' or receiver_id='"+user_id+"')\n" +
                "and record.category_id="+ cat_id +"\n"+
                "and record.trans_date between '"+start+ "' and '"+end+"' order by trans_date desc");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        System.out.format("%11s%20s%20s%22s%15s%15s\n", "TRANSACTION ID", "DATE AND TIME", "SENDER NAME", "RECEIVER NAME", "AMOUNT", "CATEGORY");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        while(rs.next())
        {
            System.out.format("%10s%23s%22s%21s%15s%16s\n", rs.getString("transaction_id"), rs.getString("trans_date"), rs.getString("sender_name"), rs.getString("receiver_name"), rs.getString("amount"), rs.getString("category_name"));
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");

        rs = MainClass.st.executeQuery("select sum(amount) from record where receiver_id = '"+user_id+"' and category_id="+cat_id);
        rs.next();
        int income = rs.getInt(1);
        rs = MainClass.st.executeQuery("select sum(amount) from record where sender_id = '"+user_id+"' and category_id="+cat_id);
        rs.next();
        int expense = rs.getInt(1);
        System.out.println("Income: Rs. "+income);
        System.out.println("Expense: Rs. "+expense);
        System.out.println("Net Income: Rs. "+(income-expense));
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");

        mainMenu();
    }

    private static void allRecords() throws Exception
    {
        ResultSet rs = MainClass.st.executeQuery("select substring(record.transaction_ID, 1, 6) as transaction_id, record.trans_date, concat(user.first_name, \" \", user.last_name) as sender_name, concat(u2.first_name, \" \", u2.last_name) as receiver_name, record.amount, category.category_name from record join user on record.sender_id=user.user_id  join user u2 on record.receiver_id=u2.user_id join category on record.category_id=category.category_id where record.sender_id='"+user_id+"' or record.receiver_id='"+user_id+"' order by trans_date desc");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        System.out.format("%11s%20s%20s%22s%15s%15s\n", "TRANSACTION ID", "DATE AND TIME", "SENDER NAME", "RECEIVER NAME", "AMOUNT", "CATEGORY");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        while(rs.next())
        {
            System.out.format("%10s%23s%22s%21s%15s%16s\n", rs.getString("transaction_id"), rs.getString("trans_date"), rs.getString("sender_name"), rs.getString("receiver_name"), rs.getString("amount"), rs.getString("category_name"));
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        rs = MainClass.st.executeQuery("select sum(amount) from record where receiver_id = '"+user_id+"'");
        rs.next();
        int income = rs.getInt(1);
        rs = MainClass.st.executeQuery("select sum(amount) from record where sender_id = '"+user_id+"'");
        rs.next();
        int expense = rs.getInt(1);
        System.out.println("Income: Rs. "+income);
        System.out.println("Expense: Rs. "+expense);
        System.out.println("Net Income: Rs. "+(income-expense));
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        mainMenu();
    }

    private static void makeTransaction() throws Exception {
        Scanner in = new Scanner(System.in);
        ResultSet rs = MainClass.st.executeQuery("select count(user_id) from user");
        rs.next();
        int n = rs.getInt("count(user_id)");
        rs  = MainClass.st.executeQuery("select user_id, first_name, last_name from user where user_id != '"+user_id+"'");

        String[] firstname = new String[n+1];
        String[] lastname = new String[n+1];
        String[] id = new String[n+1];
        int i=1;
        while(rs.next())
        {
            id[i]=rs.getString("user_id");
            firstname[i]=rs.getString("first_name");
            lastname[i]=rs.getString("last_name");
            i++;
        }
        System.out.println("Please choose the recipient");
        for(int j=1;j<n;j++)
        {
            System.out.println(j+". "+firstname[j]+" "+lastname[j]);
        }
        int rec=in.nextInt();
        String rec_id = id[rec];
        System.out.println("Enter the amount: ");
        int amount=in.nextInt();
        System.out.println("Choose category for this transaction: ");
        System.out.println("1. Salary\t2. Stocks\t3. FDs\t4. Mutual funds\t5. Real estate\t6. Rent");
        System.out.println("7. Food\t8. Travel\t9. Utilities\t10. Leisure\t11. EMI/Loans\t12. Miscellaneous");
        int cat_id;
        int ch=in.nextInt();

        cat_id = switch(ch)
                {
                    case 1 -> 1;
                    case 2 -> 2;
                    case 3 -> 3;
                    case 4 -> 4;
                    case 5 -> 5;
                    case 6 -> 6;
                    case 7 -> 7;
                    case 8 -> 8;
                    case 9 -> 9;
                    case 10 -> 10;
                    case 11 -> 11;
                    case 12 -> 12;
                    default -> throw new IllegalStateException("Unexpected value: " + ch);
                };

        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString();

        LocalDate ld = LocalDate.now();
        Date d = Date.valueOf(ld);

        String query="insert into record values (?,?,?,?,?,?)";
        ps=MainClass.con.prepareStatement(query);
        ps.setString(1, uid);
        ps.setDate(2, d);
        ps.setString(3, user_id);
        ps.setString(4, rec_id);
        ps.setInt(5, amount);
        ps.setInt(6, cat_id);

        int c=ps.executeUpdate();
        if(c==1)
        {
            System.out.println("Transaction Successful");
        }
        query="update user set account_balance=account_balance-"+amount+" where user_id='"+user_id+"'";
        ps=MainClass.con.prepareStatement(query);
        c= ps.executeUpdate();
        query="update user set account_balance=account_balance+"+amount+" where user_id='"+rec_id+"'";
        ps=MainClass.con.prepareStatement(query);
        c= ps.executeUpdate();
        mainMenu();
    }

    public static void createAccount() throws Exception {
        Scanner in = new Scanner(System.in);
        int c;
        String query="insert into user values (?,?,?,?,?,?)";
        ps=MainClass.con.prepareStatement(query);
        System.out.println("Enter user ID: ");
        user_id=in.next();
        System.out.println("Enter first name: ");
        first_name= in.next();
        System.out.println("Enter last name: ");
        last_name=in.next();
        System.out.println("Enter account balance");
        acc_bal=in.nextInt();
        System.out.println("Enter password: ");
        password=in.next();
        System.out.println("Confirm password: ");
        String cpass = in.next();
        if(!(password.equals(cpass)))
        {
            System.out.println("Passwords do not match!");
            return;
        }
        ps.setString(1,user_id);
        ps.setString(2,password);
        ps.setString(3,first_name);
        ps.setString(4,last_name);
        ps.setInt(5, acc_bal);
        ps.setNull(6, Types.NULL);
        try
        {
            c = ps.executeUpdate();
        }catch(SQLIntegrityConstraintViolationException e)
        {
            System.out.println("User ID already exists, please try again");
            return;
        }

        if(c==1)
        {
            System.out.println("Account creation successful");
            Record.setParameters();
        }

    }

    private static void setParameters() throws Exception {
        Scanner in = new Scanner(System.in);
        int[] para = new int[12];
        int c;
        String query = "select parameters_id from user where user_id = '"+user_id+"'";
        ResultSet rs = MainClass.st.executeQuery(query);
        rs.next();
        int para_id = rs.getInt("parameters_id");
        System.out.println("Please set the parameters for the finance analyzer feature: ");
        System.out.println("Enter the value that is your average income or expense per month, in that particular category.");
        System.out.println("If the category does not apply to you , then you can enter the value as zero.");
        System.out.println("Income categories: ");
        System.out.println("Salary: ");
        para[0]=in.nextInt();
        System.out.println("Stocks: ");
        para[1]=in.nextInt();
        System.out.println("Fixed Deposits: ");
        para[2]=in.nextInt();
        System.out.println("Mutual Funds");
        para[3]=in.nextInt();
        System.out.println("Real Estate");
        para[4]=in.nextInt();
        System.out.println("Income categories: ");
        System.out.println("Rent: ");
        para[5]=in.nextInt();
        System.out.println("Food: ");
        para[6]=in.nextInt();
        System.out.println("Travel: ");
        para[7]=in.nextInt();
        System.out.println("Utilities: ");
        para[8]=in.nextInt();
        System.out.println("Leisure: ");
        para[9]=in.nextInt();
        System.out.println("Loans: ");
        para[10]=in.nextInt();
        System.out.println("Miscellaneous: ");
        para[11]=in.nextInt();
//        for(int i=0;i<6;i++)
//        {
//            para[13]=para[13]+para[i];
//        }
//        for(int i=6;i<13;i++)
//        {
//            para[14]=para[14]+para[i];
//        }
        for(int i=0;i<12;i++)
        {
            para[i]=para[i]/30;
        }
        query = "insert into parameters values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        ps=MainClass.con.prepareStatement(query);
        ps.setInt(1,para_id);
        ps.setInt(2,para[0]);
        ps.setInt(3,para[1]);
        ps.setInt(4,para[2]);
        ps.setInt(5,para[3]);
        ps.setInt(6,para[4]);
        ps.setInt(7,para[5]);
        ps.setInt(8,para[6]);
        ps.setInt(9,para[7]);
        ps.setInt(10,para[8]);
        ps.setInt(11,para[9]);
        ps.setInt(12,para[10]);
        ps.setInt(13,para[11]);
        try
        {
            c = ps.executeUpdate();
        }catch(SQLIntegrityConstraintViolationException e)
        {
            System.out.println("User ID already exists, please try again");
            return;
        }
        if(c==1)
        {
            System.out.println("Parameters are set!");
            Record.mainMenu();
        }


    }
}

public class MainClass
{
    public static Connection con;
    public static Statement st;


    public static void main(String[] args) throws Exception
    {

        Scanner in = new Scanner(System.in);
        String url = "jdbc:mysql://localhost:3306/finance_tracker";
        String uname = "root";
        String pass= "vishwas21@mysql";
        con = DriverManager.getConnection(url, uname, pass);
        st =con.createStatement();
        System.out.println("WELCOME!");
        for(;;)
        {
            System.out.println("---------------------------------------------------------------------");
            System.out.println("Choose an option:");
            System.out.println("1. Login\n2.Create Account\n3. Exit");
            int ch=in.nextInt();
            switch(ch)
            {
                case 1 -> Record.login();
                case 2 -> Record.createAccount();
                case 3 -> System.exit(0);
                default -> System.out.println("Please enter valid option");
            }
        }

        /*String url = "jdbc:mysql://localhost:3306/tutorial";
        String uname = "root";
        String pass= "vishwas21@mysql";

            Connection con = DriverManager.getConnection(url, uname, pass);
            Statement st =con.createStatement();
            String query = "SELECT * FROM employee";
            ResultSet rs = st.executeQuery(query);
            while(rs.next())
            {
                System.out.println(rs.getString("first_name"));
            }
         */
    }
}
