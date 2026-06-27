import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Locale;

public class FACTURACION extends conectarCls{
    private JButton cliBTN;
    private JButton empBTN;
    private JButton proBTN;
    private JButton stockBTN;
    private JButton agreBTN;

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
    private JButton atrasBTN;
    private JTable carritoTable;
    private JButton finBTN;
    private JButton quitarBTN;

    Connection BDD;
    ResultSet rs = null;
    PreparedStatement sql1;

    DefaultTableModel facDTJ = new DefaultTableModel();
    DefaultTableModel carritoModel = new DefaultTableModel();

    public void cargarTable() {
        conecV();
        BDD = getCon();

        facDTJ.setRowCount(0);
        facDTJ.setColumnCount(0);

        String sqlSelect =
                "SELECT v.id_venta, v.cuenta, v.NombrePro, v.metodo_pago, " +
                        "SUM(pxv.cantidad) AS cantidad, " +
                        "v.subtotal, v.descuento, v.total, v.fecha_venta " +
                        "FROM venta v " +
                        "INNER JOIN peliculaXventa pxv ON v.id_venta = pxv.fk_venta " +
                        "WHERE v.habi = 1 AND pxv.habi = 1 " +
                        "GROUP BY v.id_venta " +
                        "ORDER BY v.fecha_venta DESC";

        try {
            sql1 = BDD.prepareStatement(
                "ALTER TABLE venta MODIFY COLUMN NombrePro VARCHAR(500)"
            );
            sql1.execute();

            sql1 = BDD.prepareStatement(sqlSelect);
            rs = sql1.executeQuery();

            ResultSetMetaData comDATA = rs.getMetaData();
            int comL = comDATA.getColumnCount();

            facDTJ.addColumn("ID_VENTA");
            facDTJ.addColumn("CUENTA");
            facDTJ.addColumn("NOMBREPRO");
            facDTJ.addColumn("METODO_PAGO");
            facDTJ.addColumn("CANTIDAD");
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

        carritoTable.setModel(carritoModel);
        carritoModel.addColumn("CÓDIGO");
        carritoModel.addColumn("PELÍCULA");
        carritoModel.addColumn("CANTIDAD");
        carritoModel.addColumn("PRECIO");
        carritoModel.addColumn("DESC_X_PROD");
        carritoModel.addColumn("SUBTOTAL");
        carritoModel.addColumn("METODO_PAGO");

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



        agreBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (npeliTF.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Busque un producto primero...");
                    return;
                }
                if (metCbox.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(null, "Seleccione un metodo de pago...");
                    return;
                }
                if (canTF.getText().trim().isEmpty() || stockTF.getText().trim().isEmpty() || preTF.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Complete cantidad, stock y precio antes de agregar...");
                    return;
                }
                try {
                    int cantidad = Integer.parseInt(canTF.getText());
                    int stock = Integer.parseInt(stockTF.getText());

                    if (cantidad <= 0) {
                        JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0");
                        return;
                    }
                    if (cantidad > stock) {
                        JOptionPane.showMessageDialog(null, "Stock insuficiente. Disponible: " + stock);
                        return;
                    }

                    double precio = Double.parseDouble(preTF.getText());
                    double descProducto = desXproTF.getText().trim().isEmpty() ? 0 : Double.parseDouble(desXproTF.getText());
                    if (descProducto < 0 || descProducto > 100) {
                        JOptionPane.showMessageDialog(null, "El descuentoXproducto debe estar entre 0 y 100");
                        return;
                    }

                    int rowExistente = -1;
                    for (int i = 0; i < carritoModel.getRowCount(); i++) {
                        if (carritoModel.getValueAt(i, 0).toString().equals(codTF.getText())) {
                            rowExistente = i;
                            break;
                        }
                    }

                    if (rowExistente != -1) {
                        int cantActual = Integer.parseInt(carritoModel.getValueAt(rowExistente, 2).toString());
                        if (cantActual + cantidad > stock) {
                            JOptionPane.showMessageDialog(null, "Stock insuficiente. Disponible: " + stock + ", ya tiene " + cantActual + " en el carrito");
                            return;
                        }
                        int nuevaCant = cantActual + cantidad;
                        double nuevoSubtotal = precio * nuevaCant;
                        nuevoSubtotal = nuevoSubtotal - (nuevoSubtotal * descProducto / 100.0);

                        carritoModel.setValueAt(String.valueOf(nuevaCant), rowExistente, 2);
                        carritoModel.setValueAt(String.format(Locale.US, "%.2f", precio), rowExistente, 3);
                        carritoModel.setValueAt(String.format(Locale.US, "%.2f", descProducto), rowExistente, 4);
                        carritoModel.setValueAt(String.format(Locale.US, "%.2f", nuevoSubtotal), rowExistente, 5);
                        carritoModel.setValueAt(metCbox.getSelectedItem().toString(), rowExistente, 6);
                    } else {
                        double subtotalItem = precio * cantidad;
                        subtotalItem = subtotalItem - (subtotalItem * descProducto / 100.0);

                        Object[] item = {
                                codTF.getText(),
                                npeliTF.getText(),
                                String.valueOf(cantidad),
                                String.format(Locale.US, "%.2f", precio),
                                String.format(Locale.US, "%.2f", descProducto),
                                String.format(Locale.US, "%.2f", subtotalItem),
                                metCbox.getSelectedItem().toString()
                        };
                        carritoModel.addRow(item);
                    }

                    double subtotalTotal = 0;
                    for (int i = 0; i < carritoModel.getRowCount(); i++) {
                        subtotalTotal += Double.parseDouble(carritoModel.getValueAt(i, 5).toString());
                    }
                    subTF.setText(String.format(Locale.US, "%.2f", subtotalTotal));

                    double descuento = desTF.getText().trim().isEmpty() ? 0 : Double.parseDouble(desTF.getText());
                    double totalFinal = subtotalTotal - (subtotalTotal * descuento / 100.0);
                    totalTF.setText(String.format(Locale.US, "%.2f", totalFinal));

                    codTF.setText("");
                    canTF.setText("");
                    npeliTF.setText("");
                    stockTF.setText("");
                    preTF.setText("");
                    desXproTF.setText("");

                } catch (NumberFormatException ex) {

                    }
            }
        });

        quitarBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int fila = carritoTable.getSelectedRow();
                if (fila == -1) {
                    JOptionPane.showMessageDialog(null, "Seleccione un item del carrito...");
                    return;
                }
                carritoModel.removeRow(fila);

                double subtotalTotal = 0;
                for (int i = 0; i < carritoModel.getRowCount(); i++) {
                    subtotalTotal += Double.parseDouble(carritoModel.getValueAt(i, 5).toString());
                }
                subTF.setText(carritoModel.getRowCount() == 0 ? "" : String.format(Locale.US, "%.2f", subtotalTotal));

                double descuento = desTF.getText().trim().isEmpty() ? 0 : Double.parseDouble(desTF.getText());
                double totalFinal = subtotalTotal - (subtotalTotal * descuento / 100.0);
                totalTF.setText(carritoModel.getRowCount() == 0 ? "" : String.format(Locale.US, "%.2f", totalFinal));
            }
        });

        finBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (carritoModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Agregue productos al carrito primero...");
                    return;
                }

                conecV();
                BDD = getCon();

                try {
                    BDD.setAutoCommit(false);

                    double descuento = desTF.getText().trim().isEmpty() ? 0 : Double.parseDouble(desTF.getText());

                    java.util.Map<String, java.util.List<Integer>> grupos = new java.util.LinkedHashMap<>();
                    for (int i = 0; i < carritoModel.getRowCount(); i++) {
                        String metodo = carritoModel.getValueAt(i, 6).toString();
                        if (!grupos.containsKey(metodo)) grupos.put(metodo, new java.util.ArrayList<>());
                        grupos.get(metodo).add(i);
                    }

                    for (java.util.Map.Entry<String, java.util.List<Integer>> entry : grupos.entrySet()) {
                        String metodo = entry.getKey();
                        java.util.List<Integer> filas = entry.getValue();

                        StringBuilder nombres = new StringBuilder();
                        double subTotalGrupo = 0;
                        for (int f : filas) {
                            subTotalGrupo += Double.parseDouble(carritoModel.getValueAt(f, 5).toString());
                            if (nombres.length() > 0) nombres.append(", ");
                            nombres.append(carritoModel.getValueAt(f, 1).toString());
                        }
                        double totalGrupo = subTotalGrupo - (subTotalGrupo * descuento / 100.0);

                        sql1 = BDD.prepareStatement(
                                "INSERT INTO venta(cuenta, metodo_pago, subtotal, descuento, total, NombrePro) " +
                                        "VALUES(?,?,?,?,?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        sql1.setString(1, nomTF.getText());
                        sql1.setString(2, metodo);
                        sql1.setDouble(3, subTotalGrupo);
                        sql1.setDouble(4, descuento);
                        sql1.setDouble(5, totalGrupo);
                        sql1.setString(6, nombres.toString());
                        sql1.executeUpdate();

                        rs = sql1.getGeneratedKeys();
                        int idVenta = 0;
                        if (rs.next()) idVenta = rs.getInt(1);

                        for (int f : filas) {
                            int idPelicula = Integer.parseInt(carritoModel.getValueAt(f, 0).toString());
                            int cant = Integer.parseInt(carritoModel.getValueAt(f, 2).toString());
                            double descXProd = Double.parseDouble(carritoModel.getValueAt(f, 4).toString());

                            sql1 = BDD.prepareStatement(
                                    "INSERT INTO peliculaXventa(fk_venta, fk_pelicula, cantidad, DescuentoXproducto) " +
                                            "VALUES(?,?,?,?)"
                            );
                            sql1.setInt(1, idVenta);
                            sql1.setInt(2, idPelicula);
                            sql1.setInt(3, cant);
                            sql1.setDouble(4, descXProd);
                            sql1.executeUpdate();

                            sql1 = BDD.prepareStatement(
                                    "UPDATE stock SET cantidad = cantidad - ? WHERE fk_pelicula = ?"
                            );
                            sql1.setInt(1, cant);
                            sql1.setInt(2, idPelicula);
                            sql1.executeUpdate();
                        }
                    }

                    BDD.commit();

                    carritoModel.setRowCount(0);
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
                    desXproTF.setText("0");

                    cargarTable();

                } catch (SQLException ex) {
                    try { BDD.rollback(); } catch (SQLException rb) { rb.printStackTrace(); }
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al registrar la venta...");
                } finally {
                    try { BDD.setAutoCommit(true); } catch (SQLException ac) { ac.printStackTrace(); }
                }
            }
        });

        modBTM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                conecV();
                BDD = getCon();

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

//        eliBTN.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                conecV();
//                BDD = getCon();
//
//                int fila = facTB.getSelectedRow();
//                if (fila == -1) {
//                    JOptionPane.showMessageDialog(null,
//                            "Seleccione una Venta de la tabla");
//                  /*  System.out.println("Seleccione una Venta de la tabla");*/
//
//                    return;
//                }
//
//                int idVenta = Integer.parseInt(
//                        facTB.getValueAt(fila, 0 ).toString()
//                );
//                String DesVenta =
//                        "UPDATE venta v  INNER JOIN peliculaXventa pxv ON v.id_venta = pxv.fk_venta " +
//                                "SET v.habi = 0, pxv.habi = 0 WHERE v.id_venta = ?";
//                try {
//
//                    sql1 = BDD.prepareStatement(DesVenta);
//                    sql1.setInt(1,idVenta);
//                    sql1.executeUpdate();
//                    cargarTable();
//
//                }catch (Exception  c){
//                    System.out.println("Eliminar: Algo salio mal..."+c);
//                    c.printStackTrace();
//                }
//            }
//        });
//



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


        atrasBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login lg = new Login();
                lg.setVisible(true);

                javax.swing.SwingUtilities.getWindowAncestor(atrasBTN).dispose();
            }
        });
    }

    public void setVisible(boolean b){
        JFrame fac = new JFrame("FACTURACION");
        fac.setContentPane(this.facJP);
        fac.pack();
        fac.setVisible(b);



        fac.setMinimumSize(new java.awt.Dimension(900, 700));
        fac.setLocationRelativeTo(null);
    }


}
