import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends conectarCls{
    private JTextField usuTF;
    private JTextField passTF;
    private JButton ingrBTN;
    private JButton regisBTN;
    protected JPanel loginjp;
    ResultSet  rs;
    PreparedStatement sql1;

    public Login() {

        regisBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Busca un usuario habilitado que tenga el nombre y la contraseña ingresados, y devuelve su rol.
                String SelectLogin = "SELECT rol FROM persona WHERE nombre=? AND password=? AND habilitado=1";


                try {
                    conecV();
                    BDD = getCon();
                    sql1 = BDD.prepareStatement(SelectLogin);

                    sql1.setString(1, usuTF.getText());
                    sql1.setString(2, passTF.getText());
                    rs = sql1.executeQuery();

                    if (rs.next()) {
                        //Column 'rol1'
                        String rol = rs.getString("rol");
                        if (rol.equals("ADMINISTRADOR")) {

                            REGISTRO reg = new REGISTRO();
                            reg.setVisible(true);

                            javax.swing.SwingUtilities.getWindowAncestor(regisBTN).dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "No tiene permiso para ingresar");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
                    }

                    /* Limpiar los JTextField*/
                    usuTF.setText("");
                    passTF.setText("");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        });

        ingrBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Busca un usuario habilitado que tenga el nombre y la contraseña ingresados, y devuelve su rol.
                String sql = "SELECT rol FROM persona WHERE nombre=? AND password=? AND habilitado=1";

                try {
                    conecV();
                    BDD = getCon();
                    sql1 = BDD.prepareStatement(sql);

                    sql1.setString(1, usuTF.getText());
                    sql1.setString(2, passTF.getText());
                    rs = sql1.executeQuery();

                    if (rs.next()) {
                        String rol = rs.getString("rol");
                        if (rol.equals("ADMINISTRADOR")) {

                            EMPLEADO emp = new EMPLEADO();
                            emp.setVisible(true);

                            javax.swing.SwingUtilities.getWindowAncestor(ingrBTN).dispose();
                        } else if (rol.equals("EMPLEADO")) {

                            EMPLEADO emp = new EMPLEADO();
                            emp.setVisible(true);
                            javax.swing.SwingUtilities.getWindowAncestor(ingrBTN).dispose();

                        } else {
                            JOptionPane.showMessageDialog(null, "Rol sin Permisos: " + rol);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
                    }
                    /* Limpiar los JTextField*/
                    usuTF.setText("");
                    passTF.setText("");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });


    }

    public void setVisible(boolean b) {
        JFrame lg = new JFrame("LOGIN");
        lg.setContentPane(new Login().loginjp);
        lg.pack();
        lg.setVisible(b);

        lg.setMinimumSize(new java.awt.Dimension(600, 500));
//        lg.setMaximumSize(new java.awt.Dimension(1100, 900));
        lg.setLocationRelativeTo(null);
    }
}
