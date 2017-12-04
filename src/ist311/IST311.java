package ist311;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author laurel
 */
public class IST311 extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
    
    void doLogon(Stage primaryStage){
        final PasswordField pb = new PasswordField();
        Label label1 = new Label("Email:");
        TextField textField = new TextField ();
        HBox hbUserName = new HBox();
        hbUserName.getChildren().addAll(label1, textField);
        hbUserName.setSpacing(10);
        final Label message = new Label("");
        final Label label2 = new Label("Password:");
        Group root = new Group();
        Scene scene = new Scene(root, 300, 300, Color.WHITE);
        HBox hb = new HBox();
        hb.getChildren().addAll(label2,pb, message);
        hb.setSpacing(10);
        VBox vbox = new VBox(8); // spacing = 8
        vbox.getChildren().addAll(hbUserName,hb);
        root.getChildren().add(vbox);
        primaryStage.setTitle("");
        primaryStage.setScene(scene); 
        primaryStage.sizeToScene();
        primaryStage.show();
        pb.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent e) {
            if(pb.getText().length()>3&&textField.getText().length()>3){
                User u = getIDFromLogin(textField.getText(), pb.getText());
                if (u!=null){
                    message.setText("Credentials are correct!");
                    message.setTextFill(Color.rgb(21, 117, 84));
                    showClazzes(primaryStage,u);
                }else {
                    message.setText("Your username or password is incorrect!");
                    message.setTextFill(Color.rgb(210, 39, 30));
                    
                }
                //pb.clear();
            }
        }});
    }
    
    User getIDFromLogin(String email, String password){
        Connection cn; Statement st;
        try {
            Class.forName("org.sqlite.JDBC");
            cn = DriverManager.getConnection("jdbc:sqlite:tryagain.db");
            st=cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id FROM Student WHERE lower(email) = lower('"+email+"') AND password = '"+password+"';");
            if (rs.next()) {              
                return new Student(rs.getInt(1));
            }
             rs = st.executeQuery("SELECT id FROM Teacher WHERE lower(email) = lower('"+email+"') AND password = '"+password+"';");
            if (rs.next()) {
                return new Teacher(rs.getInt(1));
            }
        }
        catch (Exception e) {System.out.println(e.getMessage());
            System.out.println("This isn't working.");}
        
        return null;
    }
    
    ArrayList<Clazz> getClazzes(User u){
        Connection cn; Statement st;
        ArrayList<Clazz> list= new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            cn = DriverManager.getConnection("jdbc:sqlite:tryagain.db");
            st=cn.createStatement();
            if (u instanceof Student) {              
                ResultSet rs = st.executeQuery("SELECT clazz.id, clazz.name FROM clazz, studentclass, student WHERE studentid=" + ((Student) u).id+" AND studentid = student.id AND clazz.id=clazzid;");
                while(rs.next()){
                    list.add(new Clazz(rs.getInt(1),rs.getString(2)));
                }
            } else if (u instanceof Teacher) {
                ResultSet rs = st.executeQuery("SELECT clazz.id, name FROM clazz, teacher WHERE teacherid = teacher.id AND teacherid = "+((Teacher) u).id+";");
                while(rs.next()){
                    list.add(new Clazz(rs.getInt(1),rs.getString(2)));
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("This isn't working.");}
        return list;
    
    }
 
    ArrayList<StudentAssignment> getAssignments(User u, Clazz c){
        Connection cn; Statement st;
        ArrayList<StudentAssignment> list= new ArrayList<StudentAssignment>();
        try {
            Class.forName("org.sqlite.JDBC");
            cn = DriverManager.getConnection("jdbc:sqlite:tryagain.db");
            st=cn.createStatement();
            
            if (u instanceof Student) {              
                ResultSet rs = st.executeQuery("SELECT assignment.id, assignment.name, receivedscore, maxpoints"
                        +" FROM assignment, studentassignment"
                        +" WHERE  studentid="+((Student) u).id+" AND clazzid="+c.id+" AND assignmentid=Assignment.id;");
                while(rs.next()){
                    list.add(new StudentAssignment(rs.getInt(1),rs.getString(2),rs.getShort(3),rs.getShort(4)));
                }
            } else if (u instanceof Teacher) {
                ResultSet rs = st.executeQuery("SELECT distinct assignment.id, assignment.name, receivedscore, maxpoints, student.fname"
                        +" FROM assignment, clazz, studentassignment, student"
                        +" WHERE  clazz.teacherid="+((Teacher) u).id+" AND clazzid="+c.id+" AND assignmentid=Assignment.id AND studentid=student.id;");
                while(rs.next()){
                    list.add(new StudentAssignment(rs.getInt(1),rs.getString(2),rs.getShort(3),rs.getShort(4), rs.getString(5)));
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("This isn't working.");}
        return list;
    
    }

    void showClazzes(Stage primaryStage, User u){
        final Random rng = new Random();
        VBox content = new VBox(5);
        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        for(Clazz c:getClazzes(u)){
            AnchorPane anchorPane = new AnchorPane();
            Label label = new Label(c.name);
            AnchorPane.setLeftAnchor(label, 5.0);
            AnchorPane.setTopAnchor(label, 5.0);
            Button button = new Button("Go →");
            button.setOnAction(evt -> showStudentAssignments(primaryStage,u,c));
            AnchorPane.setRightAnchor(button, 5.0);
            AnchorPane.setTopAnchor(button, 5.0);
            AnchorPane.setBottomAnchor(button, 5.0);
            anchorPane.getChildren().addAll(label, button);
            content.getChildren().add(anchorPane);
        }
        /*Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
        
            AnchorPane anchorPane = new AnchorPane();
            Label label = new Label("Pane "+(content.getChildren().size()+1));
            AnchorPane.setLeftAnchor(label, 5.0);
            AnchorPane.setTopAnchor(label, 5.0);
            Button button = new Button("Go →");
            button.setOnAction(evt -> content.getChildren().remove(anchorPane));
            AnchorPane.setRightAnchor(button, 5.0);
            AnchorPane.setTopAnchor(button, 5.0);
            AnchorPane.setBottomAnchor(button, 5.0);
            anchorPane.getChildren().addAll(label, button);
            content.getChildren().add(anchorPane);
        });*/

        Scene scene = new Scene(new BorderPane(scroller, null, null, null/*addButton*/, null), 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    void showStudentAssignments(Stage primaryStage, User u, Clazz c){
        final Random rng = new Random();
        VBox content = new VBox(5);
        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        for(StudentAssignment sa:getAssignments(u,c)){
            AnchorPane anchorPane = new AnchorPane();
            System.out.println(sa.studentName);
            Label label = new Label(sa.assignmentText+"        "+sa.studentName==null?"":sa.studentName+"         "+sa.actualPoints+"/"+sa.maxPoints);
            AnchorPane.setLeftAnchor(label, 5.0);
            AnchorPane.setTopAnchor(label, 5.0);
            Button button = new Button("Go →");
            button.setOnAction(evt -> System.out.println(sa.assignmentText));
            AnchorPane.setRightAnchor(button, 5.0);
            AnchorPane.setTopAnchor(button, 5.0);
            AnchorPane.setBottomAnchor(button, 5.0);
            anchorPane.getChildren().addAll(label, button);
            content.getChildren().add(anchorPane);
        }
        Button addButton = new Button(" ← Back");
        addButton.setOnAction(e -> {
            showClazzes(primaryStage, u);
        });

        Scene scene = new Scene(new BorderPane(scroller, null, null, addButton, null), 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        doLogon(primaryStage);
    }
}
class User{
    //static int idNext=0;
    int id;
    String fName,lName,email,password;
    User(int idNum){
        id=idNum;
        //idNext++;
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
    
    public Student(int idNum) {
        super(idNum);
    }
    
}
class Teacher extends User{
    
    public Teacher(int idNum) {
        super(idNum);
    }
    
}
class Clazz{
    int id;
    String name;
    Teacher t;
    ArrayList<Student> studentList = new ArrayList<Student>();
    ArrayList<StudentAssignment> gradeList = new ArrayList<>();
    
    Clazz(int idnum, String txt){
        id=idnum; name=txt;
    }
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

class StudentAssignment{
    Assignment a;
    float actualPoints, maxPoints;
    String assignmentText, studentName;
    Student s;
    Clazz c;
    int id;
    StudentAssignment(Student theStudent, Assignment theAssignment){
        this.s=theStudent;this.a=theAssignment;
    }
    StudentAssignment(){}
    StudentAssignment(int idnum, String aText, int actual, int max){
        id=idnum;assignmentText=aText;actualPoints=actual;maxPoints=max;
    }
    StudentAssignment(int idnum, String aText, int actual, int max, String name){
        id=idnum;assignmentText=aText;actualPoints=actual;maxPoints=max;studentName=name;
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
        else if (ft < 87 && ft >= 83) {return "B";}
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