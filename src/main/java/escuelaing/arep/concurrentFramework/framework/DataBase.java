package escuelaing.arep.concurrentFramework.framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import escuelaing.arep.concurrentFramework.framework.models.User;

/**
 * DataBase
 */
public class DataBase {

  private static Connection con;

  public static void connection() {
    try {
      Class.forName("org.postgresql.Driver");
      String host = "ec2-35-168-54-239.compute-1.amazonaws.com";
      String db = "d8g67as15jh72k";
      String port = "5432";
      String user = "wwycfalrdvlodn";
      String passwd = "a6d4b6436e88f6fee723d67818cc6c4a83576b2bfe73fe66f7793e0f70ddef4d";
      con = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + db, user, passwd);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static List<User> getData(String query) {
    List<User> res = new ArrayList<User>();
    try {
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        User tempUser = new User();
        tempUser.setId((long) rs.getInt(1));
        tempUser.setName(rs.getString(1));
        tempUser.setDescription(rs.getString("description"));
        res.add(tempUser);
      }
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    return res;
  }
}