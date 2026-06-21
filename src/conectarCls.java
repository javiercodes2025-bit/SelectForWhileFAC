import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conectarCls {
    Connection BDD;

    public void conecV() {

        String url = "jdbc:mysql://localhost:3306/sistemadepeliculas";
        String user = "root";
        String pass = "";

        try {
            BDD = DriverManager.getConnection(url, user, pass);
//            System.out.println("BDD: Conectado...");

        } catch (SQLException e) {
            System.out.println("BDD: Algo salio mal..." + e);
            e.printStackTrace();
        }
    }

    public Connection getCon() {
        return BDD;
    }

}
