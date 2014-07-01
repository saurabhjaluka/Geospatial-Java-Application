/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author saurabh
 */
public class populate {
    String databaseName="hw2";
    String databaseURL="jdbc:oracle:thin:@localhost:1521:"+databaseName;
    String username="system";
    String password="hw2";
    String buildingFile="",studentFile="",announcementSystemFile="";
    Connection conn=null;
    Statement stmt=null;
    void insertIntoBuildings(File file)
    {
        FileInputStream fis=null;
        try {
            fis = new FileInputStream(file);
            BufferedReader br=new BufferedReader(new InputStreamReader(fis));
            String line;
            String insertQuery;
            while((line=br.readLine())!=null)
            {
                String[] array=line.split(",");
                insertQuery="insert into building values('"+array[0]+"','"+array[1].substring(1)+"',"+array[2].replace(" ","")+",SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(";
                for(int i=3;i<array.length;i++)
                    insertQuery+=""+array[i]+",";
                insertQuery+=array[3]+","+array[4]+")))";
                stmt=conn.createStatement();
                stmt.execute(insertQuery);
            }   
        } catch (FileNotFoundException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void insertIntoAnnouncement_Systems(File file)
    {
        FileInputStream fis=null;
        try {
            fis = new FileInputStream(file);
            BufferedReader br=new BufferedReader(new InputStreamReader(fis));
            String line;
            String insertQuery;
            while((line=br.readLine())!=null)
            {
                String[] array=line.split(",");
                int x=0,y=0,r=0;
                insertQuery="insert into announcement_system values('"+array[0]+"',SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,4),SDO_ORDINATE_ARRAY(";
                x=Integer.parseInt(array[1].replace(" ",""));
                y=Integer.parseInt(array[2].replace(" ", ""));
                r=Integer.parseInt(array[3].replace(" ",""));
                insertQuery+=x+","+(y-r)+","+(x+r)+","+y+","+x+","+(y+r)+")),SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE("+x+","+y+",NULL),NULL,NULL))";
                stmt=conn.createStatement();
                stmt.execute(insertQuery);
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void insertIntoStudents(File file)
    {
        FileInputStream fis=null;
        try {
            fis = new FileInputStream(file);
            BufferedReader br=new BufferedReader(new InputStreamReader(fis));
            String line;
            String insertQuery;
            while((line=br.readLine())!=null)
            {
                String[] array=line.split(",");
                insertQuery="insert into student values('"+array[0]+"',SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE("+array[1].replace(" ", "")+","+array[2].replace(" ", "")+",NULL),NULL,NULL))";
                stmt=conn.createStatement();
                stmt.executeUpdate(insertQuery);
                if(conn==null)
                    System.out.println("connection is null");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } 
          finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void dropDataFromBuilding(){
        try {
            String query="delete from building";
            stmt=conn.createStatement();
            stmt.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String[] args)
    {
        System.out.println("In populate.java file!");
        if(args.length != 3)
            System.out.println("Please enter proper arguments");
        else
            new populate(args);
    }
    public void dropDataFromAnnouncement_System(){
        try {
            String query="delete from announcement_system";
            stmt=conn.createStatement();
            stmt.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void dropDataFromStudent(){
        try {
            String query="delete from student";
            stmt=conn.createStatement();
            stmt.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    populate(String[] args){
        getDBConnection();
        dropDataFromBuilding();
        dropDataFromAnnouncement_System();
        dropDataFromStudent();
        
        File f1=new File(args[0]);
        File f2=new File(args[2]);
        File f3=new File(args[1]);
        insertIntoBuildings(f1);
        insertIntoAnnouncement_Systems(f2);
        insertIntoStudents(f3);
        try {
            conn.commit(); 
            System.out.println("Data populated successfully");
        } catch (SQLException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void getDBConnection(){
        try{
         DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        }
        catch(SQLException ex){
            System.out.println(ex);
            return;
        }
        try{
            conn= DriverManager.getConnection(databaseURL,username,password);
        }
        catch(SQLException e){
            System.out.println(e);
            return;
        }
        if(conn!=null)
            System.out.println("Connection Succeeded.");
        else
            System.out.println("Connection failed.");
    }
}
