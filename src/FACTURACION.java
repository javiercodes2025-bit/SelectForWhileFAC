import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FACTURACION extends conectarCls{
    private JButton cliBTN;
    private JButton empBTN;
    private JButton proBTN;
    private JButton stockBTN;
    private JButton agreBTN;
    private JButton eliBTN;
    private JButton calBTN;

    private JTextField desTF;
    private JTextField subTF;
    private JTextField canTF;
    private JTextField totalTF;

    private JTable facTB;
    private JComboBox metCbox;
    private JPanel facJP;
    private JTextField cueTF;
    private JButton modBTM;
    private JButton buscliTF;
    private JTextField codTF;
    private JButton busTF;
    private JTextField stockTF;
    private JTextField npeliTF;
    private JTextField preTF;
    private JTextField nomTF;
    private JTextField desXproTF;

    Connection BDD;
    ResultSet rs = null;
    PreparedStatement sql1;

    DefaultTableModel facDTJ = new DefaultTableModel();

    public void cargarTable() {
        conecV();
        BDD = getCon();

        facDTJ.setRowCount(0);
        facDTJ.setColumnCount(0);

        String sqlSelect =
                "SELECT v.id_venta, v.cuenta, v.NombrePro,  v.metodo_pago, pxv.cantidad, pxv.DescuentoXproducto, " +

                        "v.descuento, " +
                        "v.subtotal, " +
                        "v.total, " +
                        "v.fecha_venta " +
                        "FROM venta v " +
                        "INNER JOIN peliculaXventa pxv ON v.id_venta = pxv.fk_venta " +
                        "WHERE v.habi = 1 " +
                        "AND pxv.habi = 1";

        try {

            sql1 = BDD.prepareStatement(sqlSelect);
            rs = sql1.executeQuery();

            ResultSetMetaData comDATA = rs.getMetaData();
            int comL = comDATA.getColumnCount();

            facDTJ.addColumn("ID_VENTA");
            facDTJ.addColumn("CUENTA");
            facDTJ.addColumn("NOMBREPRO");
            facDTJ.addColumn("METODO_PAGO");
            facDTJ.addColumn("CANTIDAD");
            facDTJ.addColumn("DescuentoXproducto");

            facDTJ.addColumn("SUBTOTAL");
            facDTJ.addColumn("DESCUENTO");
            facDTJ.addColumn("TOTAL");
            facDTJ.addColumn("FECHA_VENTA");

            while (rs.next()) {

                Object[] fila = new Object[comL];

                for (int i = 0; i < comL; i++) {
                    fila[i] = rs.getObject(i + 1);
                }

                facDTJ.addRow(fila);
            }

        } catch (SQLException c) {
            c.printStackTrace();
        }
    }

    public FACTURACION() {
        facTB.setModel(facDTJ);
        cargarTable();


        buscliTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    conecV();
                    BDD = getCon();

                    String sql =
                            "SELECT p.nombre " +
                                    "FROM persona p " +
                                    "INNER JOIN personaXemail pxe ON p.id_persona = pxe.fk_persona " +
                                    "INNER JOIN email e ON pxe.fk_email = e.id_email " +
                                    "WHERE e.correo = ? AND p.habilitado = 1";


                    sql1 = BDD.prepareStatement(sql);
                    sql1.setString(1, cueTF.getText()); // donde escribís el gmail
                    rs = sql1.executeQuery();

                    if (rs.next()) {

                        nomTF.setText(
                                rs.getString("nombre")
                        );

                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                "Gmail no encontrado"
                        );
                        nomTF.setText("SINCUENTA");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        busTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    conecV();
                    BDD = getCon();

                    String sql =
                            "SELECT p.titulo, p.precio, p.desc_x_produc, s.cantidad " +
                                    "FROM pelicula p " +
                                    "INNER JOIN stock s ON p.id_pelicula = s.fk_pelicula " +
                                    "WHERE p.id_pelicula = ? AND p.habi = 1";

                    sql1 = BDD.prepareStatement(sql);

                    sql1.setInt(1, Integer.parseInt(codTF.getText()));
                    rs = sql1.executeQuery();

                    if(rs.next()){

                        npeliTF.setText(
                                rs.getString("titulo")
                        );

                        stockTF.setText(
                                rs.getString("cantidad")
                        );

                        preTF.setText(
                                rs.getString("precio")
                        );

                        desXproTF.setText(
                                rs.getString("desc_x_produc")
                        );

                    }else{

                        JOptionPane.showMessageDialog(
                                null,
                                "Producto no encontrado"
                        );

                        npeliTF.setText("");
                        stockTF.setText("");
                        preTF.setText("");
                        desXproTF.setText("");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Código no encontrado"
                    );
                    ex.printStackTrace();
                }

            }
        });

        calBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    // descuento por producto, desXproTF
                    double precio = Double.parseDouble(preTF.getText());

                    int cantidad = Integer.parseInt(canTF.getText());
                    int stock = Integer.parseInt(stockTF.getText());

                    if (cantidad <= 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                "La cantidad debe ser mayor a 0"
                        );
                        return;
                    }

                    if (cantidad > stock) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Stock insuficiente. Disponible: " + stock
                        );
                        return;
                    }
                    double descProducto = 0;
                    double descuento = 0;

                    if (!desTF.getText().trim().isEmpty()) {
                        descuento = Double.parseDouble(desTF.getText());
                        if (descuento < 0 || descuento > 100) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "El descuento debe estar entre 0 y 100"
                            );
                            return;
                        }
                    }

                    if (!desXproTF.getText().trim().isEmpty()) {
                        descProducto = Double.parseDouble(desXproTF.getText()); // descuento producto
                        if (descProducto < 0 || descProducto > 100) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "El descuentoXproducto debe estar entre 0 y 100"
                            );
                            return;
                        }
                    }

                    double subtotal = precio * cantidad;
                    // descuento del producto
                    subtotal = subtotal - (subtotal * descProducto / 100.0);

                    // descuento general
                    double total = 0;
                    total =   subtotal - (subtotal * descuento / 100.0);

                    subTF.setText(String.valueOf(subtotal));
                    totalTF.setText(String.valueOf(total));


                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Ingrese un número valido... "
                    );
                    return;

                }
            }
        });
        metCbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });



       // sql1.setInt(8, cantidad);
        agreBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    conecV();
                    BDD = getCon();

                    String regVen = "{CALL registrarVenta(?,?,?,?,?,?,?,?,?)}";
                    sql1 = BDD.prepareCall(regVen);
                    sql1.setString(1, nomTF.getText());
                     //ELEGIR:
                    sql1.setString(2,
                            metCbox.getSelectedItem().toString());

                    if(metCbox.getSelectedIndex() == 0){
                        JOptionPane.showMessageDialog(
                                null,
                                "Seleccione un metodo..."
                        );
                        return;
                    }
                    sql1.setDouble(3, Double.parseDouble(subTF.getText()));
                    sql1.setDouble(4, Double.parseDouble(desTF.getText()));
                    sql1.setDouble(5, Double.parseDouble(totalTF.getText()));

                    sql1.setString(6, npeliTF.getText()); // NombrePro

                    sql1.setInt(7, Integer.parseInt(codTF.getText()));


                    int cantidad = Integer.parseInt(canTF.getText());
                    int stock = Integer.parseInt(stockTF.getText());

                    if (cantidad <= 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                "La cantidad debe ser mayor a 0"
                        );
                        return;
                    }

                    if (cantidad > stock) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Stock insuficiente. Disponible: " + stock
                        );
                        return;
                    }

                    sql1.setInt(8, Integer.parseInt(canTF.getText()));
                    sql1.setDouble(9, Double.parseDouble(desXproTF.getText())); // DescuentoXproducto


                    sql1.execute();

//                    JOptionPane.showMessageDialog(
//                            null,
//                            "Factura registrada"
//                    );


                    cueTF.setText("");
                    subTF.setText("");
                    desTF.setText("0");

                    totalTF.setText("");
                    codTF.setText("");
                    canTF.setText("");

                    npeliTF.setText("");

                    stockTF.setText("");
                    preTF.setText("");
                    nomTF.setText("SINCUENTA");

                    cargarTable();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            null,
                            "Ingrese todos los campos..."
                    );
                }
            }
        });

        modBTM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int fila = facTB.getSelectedRow();
                    if (fila == -1) {
                        JOptionPane.showMessageDialog(
                                null,  "Seleccione una factura"
                        );
                        return;
                    }

                    int idVenta = Integer.parseInt( facTB.getValueAt(fila, 0).toString()
                    );

                    String modVentas =   "{CALL modificarVenta(?,?,?,?,?,?,?,?,?)}";
                    sql1 = BDD.prepareCall(modVentas);

                    sql1.setInt(1, idVenta);

                    sql1.setString(2, nomTF.getText());

                    sql1.setString(3,
                            metCbox.getSelectedItem().toString());

                    if (metCbox.getSelectedIndex() == 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Seleccione un metodo..."
                        );
                        return;
                    }

                    if(subTF.getText().trim().isEmpty()
                            || totalTF.getText().trim().isEmpty()){

                        JOptionPane.showMessageDialog(
                                null,
                                "Calcule el subtotal y total primero"
                        );
                        return;
                    }

                    sql1.setDouble(4, Double.parseDouble(subTF.getText()));

                    sql1.setDouble(5, Double.parseDouble(desTF.getText()));

                    sql1.setDouble(6, Double.parseDouble(totalTF.getText()));

                    sql1.setString(7, npeliTF.getText());

                    int cantidad = Integer.parseInt(canTF.getText());


                    if (cantidad <= 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                "La cantidad debe ser mayor a 0"
                        );
                        return;
                    }


                    sql1.setInt(8, cantidad);
                    sql1.setDouble(9,   Double.parseDouble(desXproTF.getText()));
                    sql1.executeUpdate();

//                    JOptionPane.showMessageDialog(
//                            null,  "Factura modificada"
//                    );

                    cargarTable();
                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        });

        eliBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                int fila = facTB.getSelectedRow();
                if (fila == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione una Venta de la tabla");
                  /*  System.out.println("Seleccione una Venta de la tabla");*/

                    return;
                }

                int idVenta = Integer.parseInt(
                        facTB.getValueAt(fila, 0 ).toString()
                );
                String DesVenta =
                        "UPDATE venta v  INNER JOIN peliculaXventa pxv ON v.id_venta = pxv.fk_venta " +
                                "SET v.habi = 0, pxv.habi = 0 WHERE v.id_venta = ?";
                try {

                    sql1 = BDD.prepareStatement(DesVenta);
                    sql1.setInt(1,idVenta);
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
                EMPLEADO EMP = new EMPLEADO();
                EMP.setVisible(true);


                javax.swing.SwingUtilities.getWindowAncestor(empBTN).dispose();
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
        proBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PROVEEDORES PROV = new PROVEEDORES();
                PROV.setVisible(true);


                javax.swing.SwingUtilities.getWindowAncestor(proBTN).dispose();
            }
        });
        stockBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                STOCK ST = new STOCK();
                ST.setVisible(true);


                javax.swing.SwingUtilities.getWindowAncestor(stockBTN).dispose();
            }
        });


    }

    public void setVisible(boolean b){
        JFrame fac = new JFrame("FACTURACION");
        fac.setContentPane(new FACTURACION().facJP);
        fac.pack();
        fac.setVisible(b);



        fac.setMinimumSize(new java.awt.Dimension(900, 700));
        fac.setLocationRelativeTo(null);
    }


}
