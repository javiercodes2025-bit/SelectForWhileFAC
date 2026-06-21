import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class STOCK extends conectarCls {


    private JTable table1;
    private JButton agrBTN;
    private JButton eliBTN;
    private JButton modBTN;
    private JButton facBTN;
    private JButton proBTN;
    private JButton cliBTN;
    private JButton empBTN;
    private JTextField tituloTF;
    private JTextField anioTF;
    private JTextField duracTF;
    private JTextField precioTF;
    private JTextField canTF;
    private JButton selectBTN;

    private JPanel stockJP;
    private JTextField idpeliTF;
    private JTextField decuXTF;

    Connection BDD;
    ResultSet rs=null;
    PreparedStatement sqlSelect;
    DefaultTableModel stockDFB = new DefaultTableModel();

    public void cargarTable() {
        conecV();
        BDD = getCon();

        /*Limpiar table..*/
        stockDFB.setRowCount(0);
        stockDFB.setColumnCount(0);

        String selectT =
                "SELECT p.id_pelicula, p.titulo, p.anio, p.duracion, " +
                        "p.precio, p.desc_x_produc, s.cantidad " +
                        "FROM pelicula p " +
                        "LEFT JOIN stock s ON p.id_pelicula = s.fk_pelicula " +
                        "WHERE p.habi = 1";
        try {
            sqlSelect = BDD.prepareStatement(selectT);
             rs = sqlSelect.executeQuery();

             ResultSetMetaData comDATA = rs.getMetaData();
             int columnas = comDATA.getColumnCount();


            stockDFB.addColumn("ID_PELICULA");

            stockDFB.addColumn("TITULO");
            stockDFB.addColumn("AÑO");
            stockDFB.addColumn("DURACION");
            stockDFB.addColumn("PRECIO");
            stockDFB.addColumn("DescuXproduc");
//            stockDFB.addColumn("PROVEEDOR");
            stockDFB.addColumn("CANTIDAD");

            while (rs.next()) {
                Object[] fila = new Object[columnas];
                for (int i = 0; i < columnas; i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                stockDFB.addRow(fila);
            }


        } catch (SQLException e) {
            System.out.println("Select: Error algo salio mal... " + e);
            e.printStackTrace();
        }

    }
    public STOCK() {
        table1.setModel(stockDFB);
        cargarTable();

        selectBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                conecV();
//                BDD = getCon();


                int fila= table1.getSelectedRow();

                String idpe  = table1.getValueAt(fila,0).toString();

                String titulo = table1.getValueAt(fila,1).toString();
                String anio = table1.getValueAt(fila,2).toString();
                String durac = table1.getValueAt(fila,3).toString();
                String precio = table1.getValueAt(fila,4).toString();
                String descu = table1.getValueAt(fila, 5).toString();
                String cant = table1.getValueAt(fila, 6).toString();

                idpeliTF.setText(idpe);
                tituloTF.setText(titulo);
                anioTF.setText(anio);
                duracTF.setText(durac);
                precioTF.setText(precio);
                canTF.setText(cant);
                decuXTF.setText(descu);
            }
        });
        /*sería primero el proveedor y luego lo terminas en stock. */
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
                int desc = Integer.parseInt(decuXTF.getText());
                if (desc < 0 || desc > 100) {
                    JOptionPane.showMessageDialog(null, "El descuento debe estar entre 0 y 100");
                    return;
                }
                String SQLinsert = "{CALL registrarPelicula(?,?,?,?,?,?)}";

                try {

                    sqlSelect = BDD.prepareCall(SQLinsert);

                    sqlSelect.setString(1, tituloTF.getText());
                    sqlSelect.setInt(2, Integer.parseInt(anioTF.getText()));
                    sqlSelect.setInt(3, Integer.parseInt(duracTF.getText()));
                    sqlSelect.setDouble(4, Double.parseDouble(precioTF.getText()));

                    sqlSelect.setInt(5, Integer.parseInt(decuXTF.getText())); // descuento producto
                    sqlSelect.setInt(6, Integer.parseInt(canTF.getText()));   // cantidad
                    sqlSelect.execute();

                    tituloTF.setText("");
                    anioTF.setText("");
                    duracTF.setText("");
                    precioTF.setText("");
                    canTF.setText("");
                    decuXTF.setText("0");

                    cargarTable();
                } catch (SQLException ex) {
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
                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione una fila...");
                    return;
                }

                int idPelicula = Integer.parseInt(
                        table1.getValueAt(fila, 0 ).toString()
                );

                try {
                    BDD.setAutoCommit(false);

                    String sqlPeli = "UPDATE pelicula SET titulo=?, anio=?, duracion=?, precio=?, desc_x_produc=? WHERE id_pelicula=?";
                    sqlSelect = BDD.prepareStatement(sqlPeli);
                    sqlSelect.setString(1, tituloTF.getText());
                    sqlSelect.setInt(2, Integer.parseInt(anioTF.getText()));
                    sqlSelect.setInt(3, Integer.parseInt(duracTF.getText()));
                    sqlSelect.setDouble(4, Double.parseDouble(precioTF.getText()));
                    sqlSelect.setInt(5, Integer.parseInt(decuXTF.getText()));
                    sqlSelect.setInt(6, idPelicula);
                    sqlSelect.executeUpdate();

                    String sqlStock = "UPDATE stock SET cantidad=? WHERE fk_pelicula=?";
                    sqlSelect = BDD.prepareStatement(sqlStock);
                    sqlSelect.setInt(1, Integer.parseInt(canTF.getText()));
                    sqlSelect.setInt(2, idPelicula);
                    sqlSelect.executeUpdate();

                    BDD.commit();
                    cargarTable();

                    tituloTF.setText("");
                    anioTF.setText("");
                    duracTF.setText("");
                    precioTF.setText("");
                    canTF.setText("");
                    idpeliTF.setText("");
                    decuXTF.setText("0");

                    table1.clearSelection();
                } catch (Exception c) {
                    try { if (BDD != null) BDD.rollback(); } catch (SQLException ignored) {}
                    JOptionPane.showMessageDialog(null, "Error al modificar: " + c.getMessage());
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
                            "Seleccione una fila...");
                    return;
                }

                int idstock = Integer.parseInt(
                        table1.getValueAt(fila, 0 ).toString()
                );

                String SQLmodi = "update pelicula set habi = 0 where id_pelicula =?";
                try {

                    sqlSelect = BDD.prepareStatement(SQLmodi);
                    sqlSelect.setInt(1,idstock);
                    sqlSelect.executeUpdate();
                    cargarTable();

                    // quitar la selección de la tabla...
                    table1.clearSelection();
                }catch (Exception  c){
                    System.out.println("Eliminar: Algo salio mal..."+c);
                    c.printStackTrace();
                }

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
        empBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EMPLEADO EMP = new EMPLEADO();
                EMP.setVisible(true);

                javax.swing.SwingUtilities.getWindowAncestor(empBTN).dispose();
            }
        });


        proBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PROVEEDORES pro  = new PROVEEDORES();
                pro.setVisible(true);

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
        JFrame STK = new JFrame("STOCK");
        STK.setContentPane(this.stockJP);
        STK.pack();
        STK.setVisible(b);


        STK.setMinimumSize(new java.awt.Dimension(900, 700));
        STK.setLocationRelativeTo(null);
        STK.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

}
