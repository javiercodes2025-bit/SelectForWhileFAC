import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EMPLEADO extends conectarCls {

    private JButton facBTN;
    private JButton cliBTN;
    private JButton proBTN;
    private JButton stockBTN;
    private JTable table1;
    private JButton eliBTN;
    private JButton agrBTN;
    private JButton modBTN;
    private JTextField idEmpTF;
    private JTextField hEnTF;
    private JTextField hsaTF;
    private JTextField sueldoTF;
    private JPanel empJP;
    private JButton selBTN;
    private JTextField numdTF;
    private JComboBox TipoComBox;

    /*
      Select xx, xx, xx  from persona where habilitado = 1
    Connection BDD;
        PreparedStatement
                ResultSet

    */

    ResultSet rs=null;
    Connection BDD;
    PreparedStatement sql1;


    DefaultTableModel tj = new DefaultTableModel();

    public void  cargarTable(){
        conecV();
        BDD = getCon();

        String sqlSelect =
                "SELECT p.id_persona, p.nombre, " +
                        "p.apellido,  t.numero AS telefono, em.correo AS email, p.password,    p.rol, " +
                        "e.hora_entrada, e.hora_salida, e.sueldo,    doc.numero_documento, " +
                        "doc.tipo, d.calle, d.numero, d.ciudad " +
                        "FROM persona p " +

                        "LEFT JOIN empleado e ON p.id_persona = e.fk_persona " +

                        "LEFT JOIN personaXtelefonos pxt ON p.id_persona = pxt.fk_persona " +
                        "LEFT JOIN telefonos t ON pxt.fk_telefono = t.id_telefono " +

                        "LEFT JOIN personaXemail pxe ON p.id_persona = pxe.fk_persona " +
                        "LEFT JOIN email em ON pxe.fk_email = em.id_email " +

                        "LEFT JOIN personaXdireccion pxd ON p.id_persona = pxd.fk_persona " +
                        "LEFT JOIN direccion d ON pxd.fk_direccion = d.id_direccion " +

                        "LEFT JOIN empleadosXdocumento exd ON e.id_empleado = exd.fk_empleado " +
                        "LEFT JOIN documento doc ON exd.fk_documento = doc.id_documento " +

                        "WHERE p.rol IN ('EMPLEADO','ADMINISTRADOR') " +
                        "AND p.habilitado = 1";

        try {
            /*Limpiar table..*/

            tj.setRowCount(0);
            tj.setColumnCount(0);

            sql1 = BDD.prepareStatement(sqlSelect);
            rs = sql1.executeQuery();

            ResultSetMetaData comDATA = rs.getMetaData();
            int comL = comDATA.getColumnCount();


            tj.addColumn("ID_PERSONA");
            tj.addColumn("NOMBRE");
            tj.addColumn("APELLIDO");
            tj.addColumn("TELEFONO");
            tj.addColumn("EMAIL");
            tj.addColumn("PASSWORD");
            tj.addColumn("ROL");
            tj.addColumn("HORA_ENTRADA");
            tj.addColumn("HORA_SALIDA");
            tj.addColumn("SUELDO");

            tj.addColumn("NRO_DOCUMENTO");
            tj.addColumn("TIPO_DOCUMENTO");

            tj.addColumn("CALLE");
            tj.addColumn("NUMERO");
            tj.addColumn("CIUDAD");

            while (rs.next()) {
                Object[] fila = new Object[comL];

                for (int i = 0; i < comL; i++) {
                    fila[i] = rs.getObject(i + 1);

                }
                tj.addRow(fila);
            }

        } catch (SQLException c) {

            c.printStackTrace();
        }

    };


    public EMPLEADO() {
        table1.setModel(tj);
        cargarTable();

        selBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int fila= table1.getSelectedRow();
                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione una persona de la tabla");
                    return;
                }


                String idEmp = table1.getValueAt(fila,0).toString();

                String hEn = "";
                if(table1.getValueAt(fila,7) != null){
                    hEn = table1.getValueAt(fila,7).toString();
                }

                String hsa = "";
                if(table1.getValueAt(fila,8) != null){
                    hsa = table1.getValueAt(fila,8).toString();
                }

                String sueldo = "";
                if(table1.getValueAt(fila,9) != null){
                    sueldo = table1.getValueAt(fila,9).toString();
                }
                String nuDocu = "";
                if(table1.getValueAt(fila,10) != null){
                    nuDocu = table1.getValueAt(fila,10).toString();
                }


                idEmpTF.setText(idEmp);
                hEnTF.setText(hEn);
                hsaTF.setText(hsa);
                sueldoTF.setText(sueldo);
                numdTF.setText(nuDocu);




            }
        });


        agrBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conecV();
                BDD = getCon();


                String SQLinsert = "{CALL registrarEmpleado(?,?,?,?,?,?)}";

                try {
                    int fila = table1.getSelectedRow();

                    if (fila == -1) {
                        JOptionPane.showMessageDialog(null,
                                "Seleccione una persona(Para completar su rol)");
                        return;
                    }
                    int fkPersona = Integer.parseInt(table1.getValueAt(fila, 0).toString()
                    );
                    sql1 = BDD.prepareStatement(SQLinsert);
                    /*La columna 0 contiene id_persona(Select) para tu fk_persona*/


                    sql1.setInt(1, fkPersona);

                    sql1.setString(2, hEnTF.getText());
                    sql1.setString(3, hsaTF.getText());
                    sql1.setInt(4, Integer.parseInt(sueldoTF.getText()));
                    sql1.setString(5, numdTF.getText());
                    sql1.setString(6, TipoComBox.getSelectedItem().toString());

                    if (table1.getValueAt(fila, 2) != null ||
                            table1.getValueAt(fila, 3) != null ||
                            table1.getValueAt(fila, 4) != null) {

                        JOptionPane.showMessageDialog(
                                null,
                                "Esta persona ya está registrada como empleado"
                        );
                        return;
                    }
                    sql1.executeUpdate();

                    cargarTable();

                    hEnTF.setText("");
                    hsaTF.setText("");
                    sueldoTF.setText("");

                    idEmpTF.setText("");
                    numdTF.setText("");
                    TipoComBox.setSelectedIndex(0);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }


            }
        });

        eliBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int fila = table1.getSelectedRow();

                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione una persona para eliminar...");
                    return;
                }

                int idEmpl = Integer.parseInt(
                        table1.getValueAt(fila, 0 ).toString()
                );
                String SQLmodi = "update persona set habilitado = 0 where id_persona =?";
                try {

                    sql1 = BDD.prepareStatement(SQLmodi);


                    sql1.setInt(1,idEmpl);

                    sql1.executeUpdate();

                    cargarTable();
                }catch (Exception  c){
                    System.out.println("Update: Algo salio mal..."+c);
                    c.printStackTrace();
                }

            }
        });

        modBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int fila = table1.getSelectedRow();
                System.out.println("Filas modificadas: " + fila);

                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione una persona para modificar...");
                    return;
                }
                int idEmpl = Integer.parseInt(
                        table1.getValueAt(fila, 0 ).toString()
                );
                String SQLupdat = "{CALL actualizarEmpleado(?,?,?,?,?,?)}";
                try {

                    sql1 = BDD.prepareStatement(SQLupdat);


                    sql1.setInt(1, idEmpl);
                    sql1.setString(2, hEnTF.getText());
                    sql1.setString(3, hsaTF.getText());

                    sql1.setInt(4, Integer.parseInt(sueldoTF.getText()));
                    sql1.setString(5, numdTF.getText());
                    if(TipoComBox.getSelectedIndex() == 0){
                        JOptionPane.showMessageDialog(
                                null,
                                "Seleccione un Tipo..."
                        );
                        return;
                    }
                    sql1.setString(6, TipoComBox.getSelectedItem().toString());
                    sql1.executeUpdate();
                    cargarTable();

                  /* Limpiar los JTextField*/
                   hEnTF.setText("");
                   hsaTF.setText("");
                   sueldoTF.setText("");

                    idEmpTF.setText("");
                    numdTF.setText("");
                    TipoComBox.setSelectedIndex(0);

                }catch (Exception  c){
                    System.out.println("Update: Algo salio mal..."+c);
                    c.printStackTrace();
                }
            }
        });



        cliBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CLIENTES cliSET = new CLIENTES();
                cliSET.setVisible(true);


                javax.swing.SwingUtilities.getWindowAncestor(cliBTN).dispose();
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
    public void setVisible(boolean b){
        JFrame emp = new JFrame("EMPLEADOS");
        emp.setContentPane(new EMPLEADO().empJP);
        emp.pack();
        emp.setVisible(b);



        emp.setMinimumSize(new java.awt.Dimension(900, 700));
        emp.setLocationRelativeTo(null);
    }

}
