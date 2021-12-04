package cs601.project4.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.beans.BeanProperty;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Controller
public class JdbcManager {

    public static void insertToUser(Connection con, String name, String email, String zipcode, String startdate) throws SQLException {
        String insertContactSql = "INSERT INTO contacts (name, email, zipcode, created_at) VALUES (?, ?, ?, ?);";
        PreparedStatement insertContactStmt = con.prepareStatement(insertContactSql);
        // check if names contains other than alpha numeric
        insertContactStmt.setString(1, name);
        insertContactStmt.setString(2, email);
        insertContactStmt.setString(3, zipcode);
        insertContactStmt.setString(4, startdate);
        insertContactStmt.executeUpdate();
    }

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username")
    private String username;

    @Value("${spring.datasource.password")
    private String password;

    public JdbcManager() {
        System.out.println(url);
    }
    @BeanProperty
    public static void main(String[] args){
        JdbcManager jd = new JdbcManager();
//        JdbcConfig config = Utilities.readConfig();
//        if(config == null) {
//            System.exit(1);
//        }
//
//        // Make sure that mysql-connector-java is added as a dependency.
//        // Force Maven to Download Sources and Documentation
//        try (Connection con = DriverManager
//                .getConnection("jdbc:mysql://localhost:3306/" + config.getDatabase(), config.getUsername(), config.getPassword())) {
//
//            executeSelectAndPrint(con);
//            System.out.println("*****************");
//
//            executeInsert(con,"Sami", 2024, "srollins", "2006-09-01");
//
//            executeSelectAndPrint(con);
//            System.out.println("*****************");
//
//            executeInsert(con,"Bertha", 9876, "bzuniga", "2009-09-01");
//
//            executeSelectAndPrint(con);
//            System.out.println("*****************");
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }
}
