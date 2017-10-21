/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ist311;

import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author laurel
 */
public class IST311 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLTester test = new SQLTester();
        test.trySQL();
    }
    
}
class Sys{
    ArrayList<Clazz> classList=new ArrayList<Clazz>();
    void Logon(){
        //get username & password
        
        //verify logon
    }
    void fetchClass(){
        //goto db and get class data
    }
    void fetchUsers(){
        //goto db and get user data
    }
    
    
}

class User{
    static int idNext=0;
    int id;
    String fName,lName,email,password;
    User(){
        id=idNext;
        idNext++;
    }
    public int getID() {
        return id;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

class Student extends User{
    
}
class Teacher extends User{
    
}
class Clazz{
    static int idNext=0;
    int id;
    Teacher t;
    ArrayList<Student> studentList = new ArrayList<Student>();
    ArrayList<Grade> gradeList = new ArrayList<Grade>();
    
}
class Assignment{
    static int idNext=0;
    int id;
    String description;
    float maxPoints;
    public float getMaxPoints(){
        return maxPoints;
    } 
    // Method to enter a short description for given assignmentt
    void setDeesciption(String dc) {this.description = dc;}
}

class Grade{
    Assignment a;
    float actualPoints;
    Student s;
    Clazz c;
    Grade(Student theStudent, Assignment theAssignment){
        this.s=theStudent;this.a=theAssignment;
    }
    float getPercent(){
        return actualPoints/a.getMaxPoints()*100;
    }
    // Returns a letter grade given percentage
    String getLetterGrade(float ft) {
        if (ft >= 100) {return "A+";}
        else if (ft < 100 && ft >= 93) {return "A";}
        else if (ft < 93 && ft >= 90) {return "A-";}
        else if (ft < 90 && ft >= 87) {return "B+";}
        else if (ft < 87 && ft >= 83 ) {return "B";}
        else if (ft < 83 && ft >= 80) {return "B-";}
        else if (ft < 80 && ft >= 77) {return "C+";}
        else if (ft < 77 && ft >= 73) {return "C";}
        else if (ft < 73 && ft >= 70) {return "C-";}
        else if (ft < 70 && ft >= 67) {return "D+";}
        else if (ft < 67 && ft >= 63) {return "D";}
        else if (ft < 63 && ft >= 60) {return "D-";}
        else {return "F";}
    }
}




///// Basic code for running jdbc to open a .db database file
///// with SQL commands

class SQLTester {
    Connection k; Statement st;
    String que = "SELECT username FROM Teacher;";
    String o;
    public SQLTester() {}
    
    public void trySQL() {
        try {
            Class.forName("org.sqlite.JDBC");
            k = DriverManager.getConnection("jdbc:sqlite:tryagain.db");
            st = k.createStatement();
            
            ResultSet rs = st.executeQuery(que);
            o = rs.getString("username");
            
            System.out.println(o);
        }
        catch (Exception e) {System.out.println("Failed");}
    }
}