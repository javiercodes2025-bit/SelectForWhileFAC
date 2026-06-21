import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PROVEEDORES  extends conectarCls {

    private JTable table1;
    private JButton facBTN;
    private JButton cliBTN;
    private JButton emplBTN;
    private JButton stoBTN;
    private JButton agrBTN;
    private JButton modBTN;
    private JButton eliBTN;
    private JButton seleBTN;

    private JTextField nomTF;
    private JTextField gmailTF;
    private JTextField telTF;
    private JTextField domTF;
    private JTextField idproTF;
    private JPanel proJP;


    ResultSet rs=null;
    Connection BDD;
    PreparedStatement sql1;

    DefaultTableModel tj = new DefaultTableModel();
    public void  cargarTable(){
        conecV();
        BDD = getCon();

        String sqlSelect =
                "SELECT pr.id_proveedor, pr.nombre, pr.telefono, pr.email, " +
                        "pr.domicilio " +
                        "FROM proveedor pr " +
                        "LEFT JOIN stock s " +
                        "ON pr.id_proveedor = s.fk_proveedor " +
                        "WHERE pr.habi = 1";
        try {
            /*Limpiar table..*/

            tj.setRowCount(0);
            tj.setColumnCount(0);

            sql1 = BDD.prepareStatement(sqlSelect);
            rs = sql1.executeQuery();

            ResultSetMetaData comDATA = rs.getMetaData();
            int comL = comDATA.getColumnCount();

            tj.addColumn("ID_PROVEEDOR");
            tj.addColumn("NOMBRE");
            tj.addColumn("TELEFONO");
            tj.addColumn("GMAIL");
            tj.addColumn("DOMICILIO");


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

    public PROVEEDORES() {
        table1.setModel(tj);
        cargarTable();

        seleBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                int fila= table1.getSelectedRow();
                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione un Proveedor...");
                    return;
                }

                String id = table1.getValueAt(fila,0).toString();
                String nom = table1.getValueAt(fila,1).toString();
                String tel = table1.getValueAt(fila,2).toString();
                String gmail = table1.getValueAt(fila,3).toString();
                String dom = table1.getValueAt(fila,4).toString();

                idproTF.setText(id);
                nomTF.setText(nom);
                gmailTF.setText(gmail);
                telTF.setText(tel);
                domTF.setText(dom);


            }
        });

        agrBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conecV();
                BDD = getCon();

                int fila = table1.getSelectedRow();
                if (fila != -1) {
                    JOptionPane.showMessageDialog(null,
                            "Termine de modificar...");
                    return;
                }

                String SQLinsert = "INSERT INTO proveedor(nombre, telefono, email, domicilio) VALUES (?,?,?,?)";

                try {
                    sql1 =BDD.prepareStatement(SQLinsert);

                    sql1.setString(1, nomTF.getText());
                    sql1.setString(2, telTF.getText());
                    sql1.setString(3, gmailTF.getText());
                    sql1.setString(4, domTF.getText());
                    sql1.execute();

                    cargarTable();

                    /* Limpiar los JTextField*/
                    nomTF.setText("");
                    telTF.setText("");
                    gmailTF.setText("");
                    domTF.setText("");

                   // idproTF.setText("");
                } catch (SQLException ex) {
                    System.out.println("Error: insertar  empleados...");
                    ex.printStackTrace();
                }
            }
        });

        modBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                conecV();
                BDD = getCon();

                int fila = table1.getSelectedRow();

                System.out.println("Filas modificadas: " + fila);

                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione una persona para modificar...");
                    return;
                }

                int idpro = Integer.parseInt(table1.getValueAt(fila, 0).toString()
                );

                String SQLupdate = "UPDATE proveedor SET nombre=?, telefono=?, email=?, domicilio=? WHERE id_proveedor=?";
                try {

                    sql1 = BDD.prepareStatement(SQLupdate);

                    sql1.setString(1, nomTF.getText());
                    sql1.setString(2, telTF.getText());
                    sql1.setString(3, gmailTF.getText());
                    sql1.setString(4, domTF.getText());
                    sql1.setInt(5, idpro);

                    sql1.executeUpdate();
                    cargarTable();

                    /* Limpiar los JTextField*/
                    nomTF.setText("");
                    gmailTF.setText("");
                    telTF.setText("");
                    domTF.setText("");

                    idproTF.setText("");

                } catch (Exception c) {
                    System.out.println("Update: Algo salio mal..." + c);
                    c.printStackTrace();
                }

            }
        });


        eliBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conecV();
                BDD = getCon();

                int fila = table1.getSelectedRow();
                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione una persona para eliminar...");
                    return;
                }

                int id = Integer.parseInt(
                        table1.getValueAt(fila, 0 ).toString()
                );

                String SQLmodi = "update proveedor set habi = 0 where id_proveedor =?";
                try {
                    sql1 = BDD.prepareStatement(SQLmodi);

                    sql1.setInt(1,id);
                    sql1.executeUpdate();

                    cargarTable();
                }catch (Exception  c){
                    System.out.println("Update: Algo salio mal..."+c);
                    c.printStackTrace();
                }


            }
        });






        emplBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EMPLEADO EMP = new EMPLEADO();
                EMP.setVisible(true);


                javax.swing.SwingUtilities.getWindowAncestor(emplBTN).dispose();
            }
        });
        cliBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                CLIENTES CLI = new CLIENTES();
                CLI.setVisible(true);

                javax.swing.SwingUtilities.getWindowAncestor(cliBTN).dispose();
            }
        });
        stoBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             STOCK ST = new STOCK();
             ST.setVisible(true);

             javax.swing.SwingUtilities.getWindowAncestor(stoBTN).dispose();
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
        JFrame emp = new JFrame("PROVEEDORES");
        emp.setContentPane(this.proJP);
        emp.pack();
        emp.setVisible(b);



        emp.setMinimumSize(new java.awt.Dimension(900, 700));
        emp.setLocationRelativeTo(null);
    }
}
