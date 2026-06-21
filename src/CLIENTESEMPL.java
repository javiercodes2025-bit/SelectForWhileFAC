import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CLIENTESEMPL extends conectarCls{
    private JButton facBTN;

    private JButton stockBTN;
    private JTable table1;

    private JPanel cliJP;

    Connection BDD;
    ResultSet rs = null;
    PreparedStatement sql1;
    DefaultTableModel CliDefTable = new DefaultTableModel();

    public void cargarTable() {
        conecV();
        BDD = getCon();

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

    public CLIENTESEMPL() {
        table1.setModel(CliDefTable);
       cargarTable();





        stockBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                STOCKEMPL STK = new STOCKEMPL();
                STK.setVisible(true);

                javax.swing.SwingUtilities.getWindowAncestor(stockBTN).dispose();
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
        JFrame cli = new JFrame("CLIENTES");
        cli.setContentPane(new CLIENTESEMPL().cliJP);
        cli.pack();
        cli.setVisible(b);


        cli.setMinimumSize(new java.awt.Dimension(900, 700));
        cli.setLocationRelativeTo(null);
        cli.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

}
