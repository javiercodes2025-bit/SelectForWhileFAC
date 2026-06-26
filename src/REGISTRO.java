import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class REGISTRO extends conectarCls  {
    private JPanel registrojp;
    private JButton atrasButton;
    private JButton regiBTN;
    private JTable table1;
    private JTextField nomTF;
    private JTextField telTF;
    private JTextField gmailTF;
    private JTextField rolTF;
    private JTextField apeTF;
    private JTextField passTF;
    private JTextField calleTF;
    private JTextField numcTF;
    private JTextField ciudadTF;
    private JComboBox rolCbox;
    private JButton modificarBTN;
    private JButton selBTN;


    /*
    ResultSet rs = null;
    PreparedStatement sql1;
    DefaultTableModel jt = new DefaultTableModel();
    */
    Connection BDD;
    PreparedStatement sql1;
    ResultSet rs;
    DefaultTableModel tj = new DefaultTableModel();

    public void cargarTabla() {
        conecV();
        BDD = getCon();

        tj.setRowCount(0);
        tj.setColumnCount(0); // sin esto, te duplica los titulos.

        String SQLsel =
                "SELECT p.id_persona, p.nombre, p.apellido, t.numero, e.correo, p.password, p.rol, " +
                        "d.calle, d.numero, d.ciudad " +
                        "FROM persona p " +
                        "LEFT JOIN personaXtelefonos pxt ON p.id_persona = pxt.fk_persona " +
                        "LEFT JOIN telefonos t ON pxt.fk_telefono = t.id_telefono " +
                        "LEFT JOIN personaXemail pxe ON p.id_persona = pxe.fk_persona " +
                        "LEFT JOIN email e ON pxe.fk_email = e.id_email " +
                        "LEFT JOIN personaXdireccion pxd ON p.id_persona = pxd.fk_persona " +
                        "LEFT JOIN direccion d ON pxd.fk_direccion = d.id_direccion " +
                        "WHERE p.rol IN ('ADMINISTRADOR','EMPLEADO') AND p.habilitado = 1";


        try {
            sql1 = BDD.prepareStatement(SQLsel);
            rs = sql1.executeQuery();

            ResultSetMetaData setCol = rs.getMetaData();
            int canCol = setCol.getColumnCount();


            tj.addColumn("ID");
            tj.addColumn("NOMBRE");
            tj.addColumn("APELLIDO");
            tj.addColumn("TELEFONO");
            tj.addColumn("GMAIL");
            tj.addColumn("PASSWORD");
            tj.addColumn("ROL");
            tj.addColumn("CALLE");
            tj.addColumn("NUMERO");
            tj.addColumn("CIUDAD");

            while (rs.next()){
                // Creamos un arreglo de objetos con el mismo número de posiciones
                // que columnas tiene el resultado de la consulta.
                Object[] fil = new Object[canCol];

                // Recorremos cada columna de la fila actual del ResultSet.
                // getObject(i + 1) obtiene el valor de la columna (ResultSet usa índices desde 1).
                for (int i = 0; i < canCol; i++) {
                    fil[i] = rs.getObject(i + 1);
                }

               tj.addRow(fil);
            }



        } catch (SQLException e) {
            System.out.println("Select: algo salio mal..." + e.getMessage());
           e.printStackTrace();
        }


    }
    public REGISTRO() {
        table1.setModel(tj);
        cargarTabla();

        regiBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conecV();
                BDD = getCon();

                String SLQinsert ="{CALL registrarpersona(?,?,?,?,?,?,?,?,?)}";

                try{

                    sql1 = BDD.prepareStatement(SLQinsert);

                    sql1.setString(1, nomTF.getText());
                    sql1.setString(2, apeTF.getText());
                    sql1.setString(3, passTF.getText());

                    sql1.setString(4, rolCbox.getSelectedItem().toString());
                    if(rolCbox.getSelectedIndex() == 0){
                        JOptionPane.showMessageDialog(
                                null,
                                "Seleccione un rol..."
                        );
                        return;
                    }
                    sql1.setString(5, telTF.getText());
                    sql1.setString(6, gmailTF.getText());

                    sql1.setString(7, calleTF.getText());
                    sql1.setString(8, numcTF.getText());
                    sql1.setString(9, ciudadTF.getText());

                    sql1.executeUpdate();

                    cargarTabla();

                    /* Limpiar los JTextField*/
                    nomTF.setText("");
                    apeTF.setText("");
                    telTF.setText("");

                    gmailTF.setText("");
                    passTF.setText("");

                    calleTF.setText("");
                    numcTF.setText("");
                    ciudadTF.setText("");
                    rolCbox.setSelectedIndex(0);

                } catch (SQLException ex) {
                    System.out.print("insert: Error... " + ex);
                    ex.printStackTrace();
                }


            }
        });

        selBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int fila = table1.getSelectedRow();
                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione una persona en la tabla..");
                    return;
                }

                nomTF.setText(table1.getValueAt(fila, 1) != null ? table1.getValueAt(fila, 1).toString() : "");
                apeTF.setText(table1.getValueAt(fila, 2) != null ? table1.getValueAt(fila, 2).toString() : "");
                telTF.setText(table1.getValueAt(fila, 3) != null ? table1.getValueAt(fila, 3).toString() : "");
                gmailTF.setText(table1.getValueAt(fila, 4) != null ? table1.getValueAt(fila, 4).toString() : "");
                passTF.setText(table1.getValueAt(fila, 5) != null ? table1.getValueAt(fila, 5).toString() : "");
                calleTF.setText(table1.getValueAt(fila, 7) != null ? table1.getValueAt(fila, 7).toString() : "");
                numcTF.setText(table1.getValueAt(fila, 8) != null ? table1.getValueAt(fila, 8).toString() : "");
                ciudadTF.setText(table1.getValueAt(fila, 9) != null ? table1.getValueAt(fila, 9).toString() : "");

                String rolActual = table1.getValueAt(fila, 6).toString();
                if (rolActual.equals("EMPLEADO")) {
                    rolCbox.setSelectedIndex(1);
                } else if (rolActual.equals("ADMINISTRADOR")) {
                    rolCbox.setSelectedIndex(2);
                }
            }
        });

        modificarBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conecV();
                BDD = getCon();

                int fila = table1.getSelectedRow();
                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione una persona para modificar...");
                    return;
                }

                if (nomTF.getText().trim().isEmpty() || apeTF.getText().trim().isEmpty() ||
                        passTF.getText().trim().isEmpty() || telTF.getText().trim().isEmpty() ||
                        gmailTF.getText().trim().isEmpty() || calleTF.getText().trim().isEmpty() ||
                        numcTF.getText().trim().isEmpty() || ciudadTF.getText().trim().isEmpty()) {
                    nomTF.setText(table1.getValueAt(fila, 1) != null ? table1.getValueAt(fila, 1).toString() : "");
                    apeTF.setText(table1.getValueAt(fila, 2) != null ? table1.getValueAt(fila, 2).toString() : "");
                    telTF.setText(table1.getValueAt(fila, 3) != null ? table1.getValueAt(fila, 3).toString() : "");
                    gmailTF.setText(table1.getValueAt(fila, 4) != null ? table1.getValueAt(fila, 4).toString() : "");
                    passTF.setText(table1.getValueAt(fila, 5) != null ? table1.getValueAt(fila, 5).toString() : "");
                    calleTF.setText(table1.getValueAt(fila, 7) != null ? table1.getValueAt(fila, 7).toString() : "");
                    numcTF.setText(table1.getValueAt(fila, 8) != null ? table1.getValueAt(fila, 8).toString() : "");
                    ciudadTF.setText(table1.getValueAt(fila, 9) != null ? table1.getValueAt(fila, 9).toString() : "");
                    String rolActualSel = table1.getValueAt(fila, 6).toString();
                    if (rolActualSel.equals("EMPLEADO")) {
                        rolCbox.setSelectedIndex(1);
                    } else if (rolActualSel.equals("ADMINISTRADOR")) {
                        rolCbox.setSelectedIndex(2);
                    }
                    JOptionPane.showMessageDialog(null, "Termine de modificar...");
                    return;
                }

                String rolActual = table1.getValueAt(fila, 6).toString();
                String nuevoRol = rolCbox.getSelectedItem().toString();

                if (rolCbox.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(null, "Seleccione un rol...");
                    return;
                }

                if (rolActual.equals("ADMINISTRADOR") && nuevoRol.equals("EMPLEADO")) {
                    JOptionPane.showMessageDialog(null,
                            "No se puede cambiar de ADMINISTRADOR a EMPLEADO");
                    return;
                }

                int idPersona = Integer.parseInt(table1.getValueAt(fila, 0).toString());

                try {
                    BDD.setAutoCommit(false);

                    sql1 = BDD.prepareStatement(
                            "UPDATE persona SET nombre=?, apellido=?, password=?, rol=? WHERE id_persona=?"
                    );
                    sql1.setString(1, nomTF.getText());
                    sql1.setString(2, apeTF.getText());
                    sql1.setString(3, passTF.getText());
                    sql1.setString(4, nuevoRol);
                    sql1.setInt(5, idPersona);
                    sql1.executeUpdate();

                    sql1 = BDD.prepareStatement(
                            "UPDATE telefonos t INNER JOIN personaXtelefonos pt ON t.id_telefono=pt.fk_telefono " +
                                    "SET t.numero=? WHERE pt.fk_persona=?"
                    );
                    sql1.setString(1, telTF.getText());
                    sql1.setInt(2, idPersona);
                    sql1.executeUpdate();

                    sql1 = BDD.prepareStatement(
                            "UPDATE email e INNER JOIN personaXemail pe ON e.id_email=pe.fk_email " +
                                    "SET e.correo=? WHERE pe.fk_persona=?"
                    );
                    sql1.setString(1, gmailTF.getText());
                    sql1.setInt(2, idPersona);
                    sql1.executeUpdate();

                    sql1 = BDD.prepareStatement(
                            "UPDATE direccion d INNER JOIN personaXdireccion pd ON d.id_direccion=pd.fk_direccion " +
                                    "SET d.calle=?, d.numero=?, d.ciudad=? WHERE pd.fk_persona=?"
                    );
                    sql1.setString(1, calleTF.getText());
                    sql1.setString(2, numcTF.getText());
                    sql1.setString(3, ciudadTF.getText());
                    sql1.setInt(4, idPersona);
                    sql1.executeUpdate();

                    BDD.commit();

                    cargarTabla();

                    nomTF.setText("");
                    apeTF.setText("");
                    telTF.setText("");
                    gmailTF.setText("");
                    passTF.setText("");
                    calleTF.setText("");
                    numcTF.setText("");
                    ciudadTF.setText("");
                    rolCbox.setSelectedIndex(0);

                } catch (SQLException ex) {
                    try {
                        BDD.rollback();
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                    System.out.println("modificar: Error... " + ex);
                    ex.printStackTrace();
                } finally {
                    try {
                        BDD.setAutoCommit(true);
                    } catch (SQLException autoCommitEx) {
                        autoCommitEx.printStackTrace();
                    }
                }
            }
        });

        atrasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login lg = new Login();
               lg.setVisible(true);


                javax.swing.SwingUtilities.getWindowAncestor(atrasButton).dispose();
            }
        });

    }

    public void setVisible(boolean b) {
        JFrame res = new JFrame("REGISTRO");
        res.setContentPane(new REGISTRO().registrojp);
        res.pack();
        res.setVisible(b);


        res.setMinimumSize(new java.awt.Dimension(700, 600));
        res.setLocationRelativeTo(null);
    }

}
