import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class STOCKEMPL extends conectarCls {


    private JTable table1;
    private JButton facBTN;

    private JButton cliBTN;

    private JPanel stockJP;

    Connection BDD;
    ResultSet rs=null;
    PreparedStatement sqlSelect;
    DefaultTableModel stockDFB = new DefaultTableModel();

    public void cargarTable() {
        conecV();
        BDD = getCon();

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
    public STOCKEMPL() {
        table1.setModel(stockDFB);
        cargarTable();



        cliBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CLIENTESEMPL CLI = new CLIENTESEMPL();
                CLI.setVisible(true);

                javax.swing.SwingUtilities.getWindowAncestor(cliBTN).dispose();
            }
        });

        facBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FACTURACIONEMPL FAC = new FACTURACIONEMPL();
                FAC.setVisible(true);


                javax.swing.SwingUtilities.getWindowAncestor(facBTN).dispose();
            }
        });

    }
    public  void setVisible(boolean b){
        JFrame ST = new JFrame("STOCK");
        ST.setContentPane(new STOCKEMPL().stockJP);
        ST.pack();
        ST.setVisible(b);


        ST.setMinimumSize(new java.awt.Dimension(900, 700));
        ST.setLocationRelativeTo(null);
        ST.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

}
