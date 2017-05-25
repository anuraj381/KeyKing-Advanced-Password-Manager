package aj.apps.java.embeddedDB;

import aj.apps.java.Main.Main;
import aj.apps.java.Main.details;
import aj.apps.java.Main.popup;
import aj.apps.java.network.onlineDBconn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.derby.jdbc.EmbeddedDriver;

import java.sql.*;

/**
 * This class provides methods for all the embedded(Derby) database related operations for the application
 * @author Anuraj Jain
 */
public class dbConn {

    private static Connection conn = null;

    /**
     * This method checks the existence of keykingdb
     */
    public static void checkdb() {
        Task<Boolean> backgroundTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return null;
            }

            @Override
            public void run() {
                try {
                    Driver derbyEmbeddedDriver = new EmbeddedDriver();
                    DriverManager.registerDriver(derbyEmbeddedDriver);
                    conn = DriverManager.getConnection("jdbc:derby:keykingdb", "root", "pass123");
                    conn.setAutoCommit(false);
                    conn.commit();
                    Main.connection = "done";
                } catch (SQLException e) {
                    Main.connection = "failed";
                    e.printStackTrace();
                }
            }
        };

        Thread backgroundThread = new Thread(backgroundTask);
        backgroundThread.start();
    }

    /**
     * this method creates the keykingdb embedded(Derby) database
     * with tables - userdata, datatable, grps, datatableTemp, grpsTemp, securityTable
     */
    public static void initialise() {

        Task<Boolean> backgroundTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return null;
            }

            @Override
            public void run() {
                Statement statement = null;
                Driver derbyEmbeddedDriver = new EmbeddedDriver();
                try {
                    DriverManager.registerDriver(derbyEmbeddedDriver);
                    conn = DriverManager.getConnection("jdbc:derby:keykingdb;create=true", "root", "pass123");
                    conn.setAutoCommit(false);
                    statement = conn.createStatement();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                String createSQL = "CREATE TABLE userdata ("
                        + "name VARCHAR(50), "
                        + "surname VARCHAR(50), "
                        + "mobile VARCHAR(10), "
                        + "email VARCHAR(50), "
                        + "password VARCHAR(50), PRIMARY KEY(mobile))";

                String dataTable = "CREATE TABLE datatable ("
                        + "mobile VARCHAR(10), "
                        + "accname VARCHAR(50), "
                        + "url VARCHAR(100), "
                        + "username VARCHAR(50), "
                        + "password VARCHAR(50), "
                        + "hint VARCHAR(500), "
                        + "hintOnly BOOLEAN, "
                        + "grp VARCHAR(50), PRIMARY KEY(mobile, accname, username))";

                String grps = "CREATE TABLE grps (mobile VARCHAR(10), grp VARCHAR(50), PRIMARY KEY(mobile, grp))";

                String dataTableTemp = "CREATE TABLE datatableTemp ("
                        + "mobile VARCHAR(10), "
                        + "accname VARCHAR(50), "
                        + "url VARCHAR(100), "
                        + "username VARCHAR(50), "
                        + "password VARCHAR(50), "
                        + "hint VARCHAR(500), "
                        + "hintOnly BOOLEAN, "
                        + "grp VARCHAR(50), PRIMARY KEY(mobile, accname, username))";

                String grpsTemp = "CREATE TABLE grpsTemp (mobile VARCHAR(10), grp VARCHAR(50), PRIMARY KEY(mobile, grp))";

                String securityTable = "CREATE TABLE securityTable ("
                        + "mobile VARCHAR(10), "
                        + "id INT, "
                        + "recog BOOLEAN, "
                        + "otp BOOLEAN, PRIMARY KEY(mobile))";

                try {
                    statement.execute(createSQL);
                    statement.execute(dataTable);
                    statement.execute(grps);
                    statement.execute(dataTableTemp);
                    statement.execute(grpsTemp);
                    statement.execute(securityTable);

                    statement.close();
                    conn.commit();
                    Main.connection = "done";
                    System.out.println("Done");
                } catch (SQLException e) {
                    e.printStackTrace();
                    Main.connection = "failed";
                    System.out.println("failed");
                }

                try {
                    DriverManager.getConnection("jdbc:derby:;shutdown=true");
                } catch (SQLException ex) {
                    if (((ex.getErrorCode() == 50000) && ("XJ015".equals(ex.getSQLState())))) {
                        System.out.println("Derby shutdown properly");
                    } else {
                        System.out.println("Derby didn't shutdown properly");
                    }
                }
            }
        };

        Thread backgroundThread = new Thread(backgroundTask);
        backgroundThread.start();
    }

    /**
     * This method provides functionality for making a connection with embedded(Derby) database
     */
    private static void connect(){

        Driver derbyEmbeddedDriver = new EmbeddedDriver();
        try {
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection("jdbc:derby:keykingdb", "root", "pass123");
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates some groups by default like Internet, Email, Applications etc.
     * @param mobile mobile number of the user
     */
    public static void initialGrps(String mobile) {
        connect();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO grps (mobile, grp) VALUES ('"+mobile+"', 'Internet'), ('"+mobile+"', 'Email'), ('"+mobile+"', 'Applications'), ('"+mobile+"', 'Windows'), ('"+mobile+"', 'Banking'), ('"+mobile+"', 'Network')");
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
    }

    /**
     * adds user details to the database in the table userdata
     * @param name firstname of the user
     * @param surname lastname of the user
     * @param mobile mobile number of the user
     * @param email email of the user
     * @param password password for the account
     * @return true if details successfully added, false otherwise
     */
    public static boolean addUser(String name, String surname, String mobile, String email, String password){
        connect();
        Statement stmt = null;
        boolean result = false;
        try {
            stmt = conn.createStatement();
            //TODO: check for mobile number not exist in database... also make mobile as unique in database or primary key...
            stmt.executeUpdate("INSERT INTO userdata (name, surname, mobile, email, password) VALUES ('"+name+"', '"+surname+"', '"+mobile+"', '"+email+"', '"+password+"')");
            stmt.close();
            conn.commit();
            result = true;
        } catch (SQLException e) {
            result = false;
            e.printStackTrace();
        }
        conClose();
        return result;
    }

    /**
     * adds a new group details to the database in the table grps
     * @param name name of the group
     * @param mobile mobile number of the user
     */
    public static void addGrp(String mobile, String name){
        connect();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO grps (mobile, grp) VALUES ('"+mobile+"', '"+name+"')");
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
    }

    /**
     * adds account/entry details to the database in the table datatable
     * @param mobile mobile number of the user
     * @param accname account/entry name
     * @param url URL
     * @param username username
     * @param hint hint (if provided)
     * @param password password for the account/entry
     * @param hintOnly true if hint provided
     * @param selected group name
     */
    public static void addAccount(String mobile, String accname, String url, String username, String password, String hint, boolean hintOnly, String selected){
        connect();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO datatable (mobile, accname, url, username, password, hint, hintOnly, grp) VALUES ('"+mobile+"', '"+accname+"', '"+url+"', '"+username+"', '"+password+"', '"+hint+"', "+hintOnly+", '"+selected+"')");
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
    }

    /**
     * this method closes the connection with the database
     */
    private static void conClose(){
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * checks user password in the database to log hin/her in
     * @param number mobile number of user
     * @param password password
     * @return true if password is valid, false otherwise
     */
    public static String login(String number, String password) {
        connect();
        Statement stmt = null;
        String name = "";
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from userdata WHERE mobile = '"+number+"' AND password = '"+password+"'");
            int rowCount = 0;
            while ( rs.next() ) {
                System.out.println(rs.getString(1) + "  " +rs.getString(2));
                name = rs.getString(1)+" "+rs.getString(2);
                rowCount++;
            }
            if(rowCount == 1){
                //System.out.println("login success...");
            }else{
                //System.out.println("login failed...");
                name = "";
            }
            rs.close();
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return name;
    }

    /**
     * select and returns all group names for a particular user logged in
     * @param mobile mobile number of the user
     * @return list of groups
     */
    public static ObservableList<String> selectGrps(String mobile) {
        connect();
        Statement stmt = null;
        ObservableList<String> items = FXCollections.observableArrayList();
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from grps WHERE mobile = '"+mobile+"'");
            while (rs.next()) {
                items.add(rs.getString(2));
            }
            rs.close();
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return items;
    }

    /**
     * select and returns accounts/entries for given group
     * @param mobile mobile number of the user
     * @param selected group name
     * @return list of accounts/entries
     */
    public static ObservableList<details> selectItem(String mobile, String selected) {
        connect();
        Statement stmt = null;
        ObservableList<details> items = FXCollections.observableArrayList();
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from datatable WHERE mobile = '"+mobile+"' AND grp = '"+selected+"'");
            while (rs.next()) {
                if(rs.getBoolean(7)){
                    items.add(new details(rs.getString(2),rs.getString(3),rs.getString(4),"",rs.getString(6)));
                }else {
                    items.add(new details(rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)));
                }
            }
            rs.close();
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return items;
    }

    /**
     * select all accounts/entries details for particular user associated with given mobile number
     * @param mobile mobile number of the user
     * @return list of accounts/entries
     */
    public static ObservableList<details> selectAllItems(String mobile) {
        connect();
        Statement stmt = null;
        ObservableList<details> items = FXCollections.observableArrayList();
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from datatable WHERE mobile = '"+mobile+"'");
            while (rs.next()) {
                System.out.println(rs.getString(2) + "  " +rs.getString(3) + "  " +rs.getString(4) + "  " +rs.getString(5) + "  " +rs.getString(6)+ "  " +rs.getBoolean(7));
                if(rs.getBoolean(7)){
                    items.add(new details(rs.getString(2),rs.getString(3),rs.getString(4),"",rs.getString(6)));
                }else {
                    items.add(new details(rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)));
                }
            }
            rs.close();
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return items;
    }

    /**
     * This method provides functionality for synchronizing data between online(MySQL) and embedded(Derby) databases
     * this method performs it's task on the background thread
     * @param mobile mobile number of the user
     */
    public static void sync(String mobile) {

        Task<Boolean> backgroundTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return null;
            }

            @Override
            public void run() {
                connect();
                Statement stmt1 = null;
                Statement stmt2 = null;
                Statement stmt3 = null;
                Statement stmt4 = null;
                Statement stmt5 = null;
                Statement stmt6 = null;
                Statement stmt7 = null;
                ResultSet rs1 = null;
                ResultSet rs2 = null;
                ResultSet rs3 = null;
                ResultSet rs4 = null;
                ResultSet rs5 = null;
                try {
                    stmt1 = conn.createStatement();
                    stmt2 = conn.createStatement();
                    stmt3 = conn.createStatement();
                    stmt4 = conn.createStatement();
                    stmt5 = conn.createStatement();
                    stmt6 = conn.createStatement();
                    stmt7 = conn.createStatement();
                    rs1 = stmt1.executeQuery("select * from datatable WHERE mobile = '" + mobile + "'");
                    rs2 = stmt2.executeQuery("select * from grps WHERE mobile = '" + mobile + "'");
                    rs3 = stmt3.executeQuery("select * from userdata WHERE mobile = '" + mobile + "'");

                    rs4 = stmt4.executeQuery("SELECT * FROM datatableTemp WHERE mobile='" + mobile + "'");
                    rs5 = stmt5.executeQuery("SELECT * FROM grpsTemp WHERE mobile='" + mobile + "'");

                } catch (SQLException e) {
                    e.printStackTrace();
                    popup.getResult = "failed";
                }

                if (onlineDBconn.addData(mobile, rs1, rs2, rs3, rs4, rs5)){
                    System.out.println("sync down: downloading and syncing data");
                    try {
                        stmt6 = conn.createStatement();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    ResultSet rs6 = onlineDBconn.selectData(mobile);
                    try {
                        while (rs6.next()) {
                            try {
                                stmt6.executeUpdate("INSERT INTO datatable (mobile, accname, url, username, password, hint, hintOnly, grp) VALUES ('" + mobile + "', '" + rs6.getString(2) + "', '" + rs6.getString(3) + "', '" + rs6.getString(4) + "', '" + rs6.getString(5) + "', '" + rs6.getString(6) + "', " + rs6.getBoolean(7) + ", '" + rs6.getString(8) + "')");
                            }catch (SQLException e) {
                                e.printStackTrace();
                                try {
                                    stmt6.executeUpdate("UPDATE datatable set password = '"+rs6.getString(5)+"' WHERE mobile = '"+rs6.getString(1)+"' AND accname = '"+rs6.getString(2)+"'AND username = '"+rs6.getString(4)+"'");
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        stmt7 = conn.createStatement();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    ResultSet rs7 = onlineDBconn.selectGrps(mobile);
                    try {
                        while (rs7.next()) {
                            stmt7.executeUpdate("INSERT INTO grps (mobile, grp) VALUES ('" + mobile + "', '" + rs7.getString(2) + "')");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        stmt7.executeUpdate("DELETE FROM datatableTemp WHERE mobile = '"+mobile+"'");
                        stmt7.executeUpdate("DELETE FROM grpsTemp WHERE mobile = '"+mobile+"'");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    popup.getResult = "done";
                }else{
                    popup.getResult = "failed";
                }

                try {
                    rs1.close();
                    rs2.close();
                    rs3.close();
                    rs4.close();
                    rs5.close();
                    stmt1.close();
                    stmt2.close();
                    stmt3.close();
                    stmt4.close();
                    stmt5.close();
                    stmt6.close();
                    stmt7.close();
                    conn.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conClose();
            }
        };

        Thread backgroundThread = new Thread(backgroundTask);
        backgroundThread.start();
    }

    /**
     * this method provides functionality for changing user master password
     * @param mobile mobile number of the user
     * @param password old password
     * @param newPass new password
     * @return true if updated, false otherwise
     */
    public static boolean changeMasterPassword(String mobile, String password, String newPass){
        connect();
        Statement stmt = null;
        boolean allow = false;
        try {
            stmt = conn.createStatement();
            int i = stmt.executeUpdate("UPDATE userdata SET password = '"+newPass+"' WHERE mobile = '"+mobile+"' AND password = '"+password+"'");
            System.out.println(i);
            if (i == 1){
                allow = true;
            }
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return allow;
    }

    /**
     * this method provides functionality for changing password of a particular entry/account in the table in the client area
     * @param mobile mobile number of the user
     * @param accname account/entry name
     * @param username username for entry/account
     * @param newPass new password
     * @return true if updated, false otherwise
     */
    public static boolean changePassword(String mobile, String accname, String username, String newPass) {
        connect();
        Statement stmt = null;
        Statement stmt2 = null;
        boolean allow = false;
        try {
            stmt = conn.createStatement();
            stmt2 = conn.createStatement();

            ResultSet rs2 = stmt2.executeQuery("select * from datatable WHERE mobile = '" + mobile + "' AND accname = '" + accname + "' AND username = '" + username + "'");
            while (rs2.next()) {
                System.out.println(rs2.getString(2) + "  " + rs2.getString(3) + "  " + rs2.getString(4) + "  " + rs2.getString(5) + "  " + rs2.getString(6) + "  " + rs2.getBoolean(7));
                try {
                    stmt.executeUpdate("INSERT INTO dataTableTemp (mobile, accname, url, username, password, hint, hintOnly, grp) VALUES ('" + rs2.getString(1) + "', '" + rs2.getString(2) + "', '" + rs2.getString(3) + "', '" + rs2.getString(4) + "', '" + rs2.getString(5) + "', '" + rs2.getString(6) + "', " + rs2.getBoolean(7) + ", '" + rs2.getString(8) + "')");
                }catch (SQLException e){}
            }
            int i = stmt.executeUpdate("UPDATE datatable SET password = '" + newPass + "' WHERE mobile = '" + mobile + "' AND accname = '" + accname + "' AND username = '" + username + "'");
            if (i == 1) {
                allow = true;
            }

            rs2.close();
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return allow;
    }

    /**
     * this method is used to get the password for particular entry
     * @param mobile mobile number of the user
     * @param accname account/entry name
     * @param username username for the entry/account
     * @return password
     */
    public static String getPassword(String mobile, String accname, String username){
        connect();
        Statement stmt = null;
        String pass = "something in here";
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select password from datatable WHERE mobile = '"+mobile+"' AND username = '"+username+"' AND accname = '"+accname+"'");
            while ( rs.next() ) {
                System.out.println(rs.getString("password"));
                pass = rs.getString("password");
            }
            rs.close();
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return pass;
    }

    /**
     * this method provides functionality for removing a particular entry/account
     * @param mobile mobile number of the user
     * @param accname account name
     * @param username username of the account/entry
     * @return true if removed, false otherwise
     */
    public static boolean removeAcc(String mobile, String accname, String username){
        connect();
        Statement stmt1 = null;
        Statement stmt2 = null;
        boolean pass = false;
        try {
            stmt1 = conn.createStatement();
            stmt2 = conn.createStatement();

            ResultSet rs = stmt1.executeQuery("SELECT * FROM datatable WHERE mobile = '"+mobile+"' AND accname = '"+accname+"' AND username = '"+username+"'");
            while(rs.next()){
                stmt2.executeUpdate("INSERT INTO datatableTemp (mobile, accname, url, username, password, hint, hintOnly, grp) VALUES ('"+rs.getString(1)+"', '"+rs.getString(2)+"', '"+rs.getString(3)+"', '"+rs.getString(4)+"', '"+rs.getString(5)+"', '"+rs.getString(6)+"', '"+rs.getBoolean(7)+"', '"+rs.getString(8)+"')");
            }

            int i = stmt1.executeUpdate("DELETE FROM datatable WHERE mobile = '"+mobile+"' AND accname = '"+accname+"' AND username = '"+username+"'");
            if(i==1) {
                pass = true;
            }
            stmt1.close();
            stmt2.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return pass;
    }

    /**
     * this method provides functionality for removing a particular group of entries/accounts
     * @param mobile mobile number of the user
     * @param name group name
     */
    public static void deleteGrp(String mobile, String name){
        connect();
        Statement stmt1 = null;
        Statement stmt2 = null;
        boolean ans = false;
        try {
            stmt1 = conn.createStatement();
            stmt2 = conn.createStatement();
            ResultSet rs = stmt1.executeQuery("SELECT * FROM datatable WHERE mobile = '"+mobile+"' AND grp = '"+name+"'");
            while(rs.next()){
                stmt2.executeUpdate("INSERT INTO datatableTemp (mobile, accname, url, username, password, hint, hintOnly, grp) VALUES ('"+rs.getString(1)+"', '"+rs.getString(2)+"', '"+rs.getString(3)+"', '"+rs.getString(4)+"', '"+rs.getString(5)+"', '"+rs.getString(6)+"', '"+rs.getBoolean(7)+"', '"+rs.getString(8)+"')");
            }
            stmt1.executeUpdate("INSERT INTO grpsTemp (mobile, grp) VALUES ('"+mobile+"', '"+name+"')");

            stmt1.executeUpdate("DELETE FROM grps WHERE mobile = '"+mobile+"' AND grp = '"+name+"'");
            stmt1.executeUpdate("DELETE FROM datatable WHERE mobile = '"+mobile+"' AND grp = '"+name+"'");
            stmt1.close();
            stmt2.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
    }

    /**
     * this method functionality for serching particular entry/account
     * @param mobile mobile number of the user
     * @param text text to be searched
     * @return list of found entries/accounts
     */
    public static ObservableList<details> search(String mobile, String text){
        connect();
        Statement stmt = null;
        ObservableList<details> items = FXCollections.observableArrayList();
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from datatable WHERE mobile = '"+mobile+"' AND (accname = '"+text+"' OR username = '"+text+"' OR url = '"+text+"' OR hint = '"+text+"')");
            while (rs.next()) {
                System.out.println(rs.getString(2) + "  " +rs.getString(3) + "  " +rs.getString(4) + "  " +rs.getString(5) + "  " +rs.getString(6)+ "  " +rs.getBoolean(7));
                if(rs.getBoolean(7)){
                    items.add(new details(rs.getString(2),rs.getString(3),rs.getString(4),"",rs.getString(6)));
                }else {
                    items.add(new details(rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)));
                }
            }
            rs.close();
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return items;
    }

    /**
     * this method is used for selecting id of the user associated face recognition security dataset for user
     * @param mobile mobile number of the user
     * @return id if security layer enabled, 0 otherwise
     */
    public static int selectRecogSecurity(String mobile){
        connect();
        Statement stmt = null;
        int toReturn = 0;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from securityTable WHERE mobile = '"+mobile+"'");
            while (rs.next()) {
                if (rs.getBoolean(3)){
                    toReturn = rs.getInt(2);
                }
            }
            rs.close();
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return toReturn;
    }

    /**
     * this method is used for selecting if OTP verification security is enables for the user
     * @param mobile mobile number of the user
     * @return true if security layer enabled, false otherwise
     */
    public static boolean selectOtpSecurity(String mobile){
        connect();
        Statement stmt = null;
        boolean toReturn = false;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from securityTable WHERE mobile = '"+mobile+"'");
            while (rs.next()) {
                if (rs.getBoolean(4)){
                    toReturn = true;
                }
            }
            rs.close();
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return toReturn;
    }

    /**
     * adds the security layer details to the database
     */
    public static boolean addSecurity(String mobile, int id, boolean faceRecog, boolean otp){
        connect();
        boolean done = false;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO securityTable (mobile, id, recog, otp) VALUES ('"+mobile+"', "+id+", "+faceRecog+", "+otp+")");
            stmt.close();
            conn.commit();
            done = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return done;
    }

    /**
     * modifies the security layer details to the database
     */
    public static boolean modifySecurity(String mobile, int id, boolean switcher, boolean faceRecog, boolean otp){
        connect();
        boolean done = false;
        Statement stmt = null;
        String sql = "";
        if (switcher){
            sql = "UPDATE securityTable SET id = "+id+", recog = "+faceRecog+" WHERE mobile = '"+mobile+"'";
        } else {
            sql = "UPDATE securityTable SET otp = "+otp+" WHERE mobile = '"+mobile+"'";
        }
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();
            done = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return done;
    }

    /**
     * clears all the details of particular user from the database
     * @param mobile mobile number of the user
     * @return true if done, false otherwise
     */
    public static boolean clear(String mobile){
        connect();
        Statement stmt = null;
        boolean toReturn = false;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM datatable WHERE mobile = '"+mobile+"'");
            stmt.executeUpdate("DELETE FROM grps WHERE mobile = '"+mobile+"'");
            stmt.executeUpdate("DELETE FROM datatableTemp WHERE mobile = '"+mobile+"'");
            stmt.executeUpdate("DELETE FROM grpsTemp WHERE mobile = '"+mobile+"'");
            toReturn = true;
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conClose();
        return toReturn;
    }

}