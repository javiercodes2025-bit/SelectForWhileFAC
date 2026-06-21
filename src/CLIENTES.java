import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CLIENTES extends conectarCls{
    private JButton facBTN;
    private JButton empBTN;
    private JButton proBTN;
    private JButton stockBTN;
    private JTable table1;
    private JButton agrBTN;
    private JButton modBTN;
    private JButton eliBTN;
    private JTextField idTF;
    private JTextField nomTF;
    private JTextField gmailTF;
    private JTextField passTF;
    private JTextField apeTF;
    private JTextField telTF;
    private JPanel cliJP;
    private JTextField rolTF;
    private JButton selBTN;
    private JTextField numcalleTF;
    private JTextField calleTF;
    private JTextField ciudadTF;
    private JComboBox rolComBox;
    private JTextField dniTF;

    Connection BDD;
    ResultSet rs = null;
    PreparedStatement sql1;
    DefaultTableModel CliDefTable = new DefaultTableModel();

    public void cargarTable() {
        conecV();
        BDD = getCon();

        /*Limpiar table..*/
        CliDefTable.setRowCount(0);
        CliDefTable.setColumnCount(0);

        String sqlSelect =
                "SELECT p.id_persona, p.nombre, p.apellido, " +
                        "t.numero, e.correo, p.password, p.rol, " +
                        "c.dni, " +
                        "d.calle, d.numero, d.ciudad " +
                        "FROM persona p " +
                        "INNER JOIN cliente c ON p.id_persona = c.fk_persona " +
                        "LEFT JOIN personaXtelefonos pxt ON p.id_persona = pxt.fk_persona " +
                        "LEFT JOIN telefonos t ON pxt.fk_telefono = t.id_telefono " +
                        "LEFT JOIN personaXemail pxe ON p.id_persona = pxe.fk_persona " +
                        "LEFT JOIN email e ON pxe.fk_email = e.id_email " +
                        "LEFT JOIN personaXdireccion pxd ON p.id_persona = pxd.fk_persona " +
                        "LEFT JOIN direccion d ON pxd.fk_direccion = d.id_direccion " +
                        "WHERE p.rol = 'CLIENTE' " +
                        "AND p.habilitado = 1";
        try {
            sql1 = BDD.prepareStatement(sqlSelect);
            rs = sql1.executeQuery();

            ResultSetMetaData comDATA = rs.getMetaData();
            int comL = comDATA.getColumnCount();

            CliDefTable.addColumn("ID_PERSONA");
            CliDefTable.addColumn("NOMBRE");
            CliDefTable.addColumn("APELLIDO");
            CliDefTable.addColumn("TELEFONO");
            CliDefTable.addColumn("GMAIL");
            CliDefTable.addColumn("PASSWORD");
            CliDefTable.addColumn("ROL");
            CliDefTable.addColumn("DNI");
            CliDefTable.addColumn("CALLE");
            CliDefTable.addColumn("NUMERO");
            CliDefTable.addColumn("CIUDAD");

            while (rs.next()) {
                Object[] fila = new Object[comL];
                for (int i = 0; i < comL; i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                CliDefTable.addRow(fila);
            }
        } catch (SQLException c) {
            c.printStackTrace();
        }
    }

    public CLIENTES() {
        table1.setModel(CliDefTable);
       cargarTable();

       /*  El Profe comentó se pued usar procedure */
        agrBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conecV();
                BDD = getCon();

                /* Seria agregar los datos de cliente... con unos wheres si el rol es CLIENTE...
                PERO DESABILITADO(DEFAULT) para habilitar y ahi lo agregas a una tabla intermedia. */

                int fila = table1.getSelectedRow();
                if (fila != -1) {
                    JOptionPane.showMessageDialog(null,
                            "Termine de modificar...");
                    return;
                }
                if (nomTF.getText().trim().isEmpty()
                        || apeTF.getText().trim().isEmpty()
                        || telTF.getText().trim().isEmpty()
                        || gmailTF.getText().trim().isEmpty()
                        || passTF.getText().trim().isEmpty()
                        || calleTF.getText().trim().isEmpty()
                        || numcalleTF.getText().trim().isEmpty()
                        || ciudadTF.getText().trim().isEmpty()
                        || dniTF.getText().trim().isEmpty()
                        || rolComBox.getSelectedItem() == null) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Complete todos los campos..."
                    );
                    return;
                }


                /*Se puede hacerlo asi... si te complica.*/
                String sqlInsert = "{CALL registrarCliente(?,?,?,?,?,?,?,?,?,?)}";
                try {


                    sql1 = BDD.prepareCall(sqlInsert);

                    sql1.setString(1, nomTF.getText());
                    sql1.setString(2, apeTF.getText());
                    sql1.setString(3, passTF.getText());
                    sql1.setString(4, rolComBox.getSelectedItem().toString());
                    sql1.setString(5, telTF.getText());
                    sql1.setString(6, gmailTF.getText());

                    sql1.setString(7, calleTF.getText());
                    sql1.setString(8, numcalleTF.getText());
                    sql1.setString(9, ciudadTF.getText());
                    sql1.setString(10, dniTF.getText());

                    sql1.execute();

//                    JOptionPane.showMessageDialog(
//                            null,
//                            "Cliente registrado correctamente"
//                    );

                    cargarTable();

                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 1062) {  //Para duplicate entry
                        JOptionPane.showMessageDialog(null, "El DNI ya existe en la BDD");
                    } else {
                        ex.printStackTrace();
                    }
//                    ex.printStackTrace();
                }


            }
        });


        selBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int fila = table1.getSelectedRow();

                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione un cliente...");
                    return;
                }

                String id = table1.getValueAt(fila, 0).toString();
                String nom = table1.getValueAt(fila, 1).toString();
                String ape = table1.getValueAt(fila, 2).toString();

                String tel = "";
                if(table1.getValueAt(fila, 3) != null){
                    tel = table1.getValueAt(fila, 3).toString();
                }

                String gmail = "";
                if(table1.getValueAt(fila, 4) != null){
                    gmail = table1.getValueAt(fila, 4).toString();
                }

                String pass = table1.getValueAt(fila, 5).toString();
                //rol 6

                String dni = "";
                if(table1.getValueAt(fila, 7) != null){
                    dni = table1.getValueAt(fila, 7).toString();
                }

                String calle = "";
                if(table1.getValueAt(fila, 8) != null){
                    calle = table1.getValueAt(fila, 8).toString();
                }

                String numCalle = "";
                if(table1.getValueAt(fila, 9) != null){
                    numCalle = table1.getValueAt(fila, 9).toString();
                }

                String ciudad = "";
                if(table1.getValueAt(fila, 10) != null){
                    ciudad = table1.getValueAt(fila, 10).toString();
                }

                idTF.setText(id);
                nomTF.setText(nom);
                apeTF.setText(ape);
                telTF.setText(tel);
                gmailTF.setText(gmail);
                passTF.setText(pass);

                dniTF.setText(dni);
                calleTF.setText(calle);
                numcalleTF.setText(numCalle);
                ciudadTF.setText(ciudad);

            }
        });


        modBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int fila = table1.getSelectedRow();
                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione un cliente...");
                    return;
                }

                int idClie = Integer.parseInt(
                        table1.getValueAt(fila, 0 ).toString()
                );
                String SQLmodi = "{CALL modificarCliente(?,?,?,?,?,?,?,?,?,?,?)}";
                try {
                    sql1 = BDD.prepareCall(SQLmodi);
                    sql1.setInt(1, idClie);
                    sql1.setString(2, nomTF.getText());
                    sql1.setString(3, apeTF.getText());
                    sql1.setString(4, telTF.getText());      // p_telefono
                    sql1.setString(5, gmailTF.getText());     // p_email
                    sql1.setString(6, passTF.getText());      // p_password
                    sql1.setString(7, rolComBox.getSelectedItem().toString()); // p_rol
                    sql1.setString(8, calleTF.getText());
                    sql1.setString(9, numcalleTF.getText());
                    sql1.setString(10, ciudadTF.getText());
                    sql1.setString(11, dniTF.getText());
                    sql1.executeUpdate();
                    cargarTable();



                    nomTF.setText("");
                    apeTF.setText("");
                    telTF.setText("");
                    gmailTF.setText("");
                    passTF.setText("");

                    //rolTF.setText("");

                    calleTF.setText("");
                    numcalleTF.setText("");
                    ciudadTF.setText("");
                    dniTF.setText("");

                    idTF.setText("");

                    // quitar la selección de la tabla...
                    table1.clearSelection();
                }catch (Exception  c){
                    System.out.println("Update: Algo salio mal..."+c);
                    c.printStackTrace();
                }



            }
        });

        eliBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int fila = table1.getSelectedRow();
                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione un cliente...");
                    return;
                }

                int idClie = Integer.parseInt(
                        table1.getValueAt(fila, 0 ).toString()
                );
                String SQLmodi = "update persona set habilitado = 0 where id_persona =?";
                try {

                    sql1 = BDD.prepareStatement(SQLmodi);
                    sql1.setInt(1,idClie);
                    sql1.executeUpdate();
                    cargarTable();

                }catch (Exception  c){
                    System.out.println("Eliminar: Algo salio mal..."+c);
                    c.printStackTrace();
                }
            }
        });



        empBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EMPLEADO empSET = new EMPLEADO();
                empSET.setVisible(true);

                javax.swing.SwingUtilities.getWindowAncestor(empBTN).dispose();
            }
        });

        stockBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                STOCK STK = new STOCK();
                STK.setVisible(true);

                javax.swing.SwingUtilities.getWindowAncestor(stockBTN).dispose();
            }
        });

        proBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PROVEEDORES PRO = new PROVEEDORES();
                PRO.setVisible(true);

                javax.swing.SwingUtilities.getWindowAncestor(proBTN).dispose();
            }
        });
        facBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FACTURACION FAC = new FACTURACION();
                FAC.setVisible(true);


                javax.swing.SwingUtilities.getWindowAncestor(facBTN).dispose();
            }
        });
    }

    public  void setVisible(boolean b){
        JFrame cli = new JFrame("CLIENTES");
        cli.setContentPane(new CLIENTES().cliJP);
        cli.pack();
        cli.setVisible(b);

        cli.setMinimumSize(new java.awt.Dimension(900, 700));
        cli.setLocationRelativeTo(null);
        cli.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

}
