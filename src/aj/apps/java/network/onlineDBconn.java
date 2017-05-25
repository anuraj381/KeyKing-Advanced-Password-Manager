package aj.apps.java.network;

import aj.apps.java.Main.popup;

import java.sql.*;

/**
 * This class provides methods for all the online(MySQL) database related operations for the application
 * @author Anuraj Jain
 */
public class onlineDBconn extends popup {

    private static Connection conn;

    /**
     * This method provides functionality for making a connection with online(MySQL) database
     */
    private static void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://host-ip-here:port-here/keykingonlinedb", "username", "password");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * This method provides functionality for adding account/entry details, groups details, user details to the online(MySQL) database
     * <p>this method also provides functionality for deleting the unnecessary details from online database
     * this method is a part of process for achieving synchronization</p>
     */
    public static boolean addData(String mobile, ResultSet rs1, ResultSet rs2, ResultSet rs3, ResultSet rs4, ResultSet rs5) {
        connect();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean allow = false;
        try {
            String pass = "";
            while (rs3.next()) {
                pass = rs3.getString(5);
            }
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from userdata WHERE mobile = '"+mobile+"' AND password = '"+pass+"'");
            int rowCount = 0;
            while ( rs.next() ) {
                rowCount++;
            }
            if(rowCount == 1){
                allow = true;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (allow) {
            try {
                while (rs4.next()) {
                    System.out.println(rs4.getString(1) + " " + rs4.getString(2) + " " + rs4.getString(4) + " " + rs4.getString(5));
                    int i = stmt.executeUpdate("DELETE FROM datatable WHERE mobile = '" + rs4.getString(1) + "' AND accname = '" + rs4.getString(2) + "' AND username = '" + rs4.getString(4) + "'");
                    System.out.println("datatable" + i);
                }
                while (rs1.next()) {
                    System.out.println(rs1.getString(2) + " " + rs1.getString(3));
                    try {
                        int i = stmt.executeUpdate("INSERT INTO datatable (mobile, accname, url, username, password, hint, hintOnly, grp) VALUES ('" + mobile + "', '" + rs1.getString(2) + "', '" + rs1.getString(3) + "', '" + rs1.getString(4) + "', '" + rs1.getString(5) + "', '" + rs1.getString(6) + "', " + rs1.getBoolean(7) + ", '" + rs1.getString(8) + "')");
                        System.out.println(i);
                    } catch (SQLException e) {
                        stmt.executeUpdate("UPDATE datatable set password = '" + rs1.getString(5) + "' WHERE mobile = '" + rs1.getString(1) + "' AND accname = '" + rs1.getString(2) + "'AND username = '" + rs1.getString(4) + "'");
                    }
                }
                while (rs2.next()) {
                    System.out.println(rs2.getString(2));
                    stmt.executeUpdate("INSERT IGNORE INTO grps (mobile, grp) VALUES ('" + mobile + "', '" + rs2.getString(2) + "')");
                }
                while (rs3.next()) {
                    System.out.println(rs3.getString(2) + " " + rs3.getString(3));
                    stmt.executeUpdate("INSERT IGNORE INTO userdata (name, surname, mobile, email, password) VALUES ('" + rs3.getString(1) + "', '" + rs3.getString(2) + "', '" + rs3.getString(3) + "', '" + rs3.getString(4) + "', '" + rs3.getString(5) + "')");
                }
                while (rs5.next()) {
                    System.out.println(rs5.getString(1) + " " + rs5.getString(2));
                    int i = stmt.executeUpdate("DELETE FROM grps WHERE mobile = '" + rs5.getString(1) + "' AND grp = '" + rs5.getString(2) + "'");
                    System.out.println("grps" + i);
                }

                allow = true;

            } catch (SQLException e) {
                e.printStackTrace();
                allow = false;
            }
        }

        try {
            stmt.close();
            conClose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allow;
    }

    /**
     * This method provides functionality for adding account/entry details from the online(MySQL) database to embedded(Derby) database
     * <p>this method is a part of process for achieving synchronization</p>
     */
    public static ResultSet selectData(String mobile){
        connect();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from datatable WHERE mobile = '"+mobile+"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * This method provides functionality for adding groups details from the online(MySQL) database to embedded(Derby) database
     * <p>this method is a part of process for achieving synchronization</p>
     */
    public static ResultSet selectGrps(String mobile){
        connect();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from grps WHERE mobile = '"+mobile+"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * this method provides functionality for updating user master password in the online(MySQL) database
     * @param mobile mobile number of user
     * @param pass new master password
     * @return true if updated, false otherwise
     */
    public static boolean updateMasterPass(String mobile, String pass) {
        connect();
        Statement stmt = null;
        boolean result = false;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE userdata SET password = '"+pass+"' WHERE mobile = '"+mobile+"'");
            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * this method provides functionality for closing the connection with database
     */
    private static void conClose() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
