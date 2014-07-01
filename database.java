
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.*;

public class database {
    OracleConnection conn=null;
    Statement stmt;
    OracleResultSet rs;
    String databaseName="hw2";
    String username="system";
    String password="hw2";
    public Connection getDBConnection(){
        try{
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            conn= (OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:"+databaseName, username,password);
            if(conn!=null)
            System.out.println("Connection Succeeded.");
            else
            System.out.println("Connection failed.");
            return conn;
        }
        catch(SQLException e){
            System.out.println(e);
            return null;
        }
    }
    public OracleResultSet getQueryResult(String query){
        try {
            stmt=conn.createStatement();
            rs=(OracleResultSet)stmt.executeQuery(query);
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(database.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
    public String getID(String query){
        try {
            String tmp="";
            stmt=conn.createStatement();
            rs=(OracleResultSet)stmt.executeQuery(query);
            while(rs.next())
                tmp=rs.getString(2);
            return tmp;
        } catch (SQLException ex) {
            Logger.getLogger(database.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }

    
}
