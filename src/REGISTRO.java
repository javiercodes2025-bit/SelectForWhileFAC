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
                "SELECT p.nombre, p.apellido, t.numero, e.correo, p.password, p.rol " +
                        "FROM persona p " +
                        "LEFT JOIN personaXtelefonos pxt ON p.id_persona = pxt.fk_persona " +
                        "LEFT JOIN telefonos t ON pxt.fk_telefono = t.id_telefono " +
                        "LEFT JOIN personaXemail pxe ON p.id_persona = pxe.fk_persona " +
                        "LEFT JOIN email e ON pxe.fk_email = e.id_email " +
                        "WHERE p.habilitado = 1";


        try {
            sql1 = BDD.prepareStatement(SQLsel);
            rs = sql1.executeQuery();

            ResultSetMetaData setCol = rs.getMetaData();
            int canCol = setCol.getColumnCount();


//            tj.addColumn("id_persona");
            tj.addColumn("NOMBRE");
            tj.addColumn("APELLIDO");
            tj.addColumn("TELEFONO");
            tj.addColumn("GMAIL");
            tj.addColumn("PASSWORD");
            tj.addColumn("ROL");

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


        /*
public void seleccionar(){
        ConnecV();
        BDD = getCon();
        jt.setRowCount(0);

        String SQLPROD = "Select idAutor, nombre, nacionalidad  from autor where habi = 1";

        try{
            sql1= BDD.prepareStatement(SQLPROD);
            rs = sql1.executeQuery();

            ResultSetMetaData resul = rs.getMetaData();
            int cantColums = resul.getColumnCount();


            jt.addColumn("idAutor");
            jt.addColumn("nombre");
            jt.addColumn("nacionalidad");
*/
        /*
            while(rs.next()) {
                Object[] filas = new Object[cantColums];
                for (int i= 0; i < cantColums; i++){
                    filas[i] = rs.getObject(i + 1);
                }
                jt.addRow(filas);
            }

            //toma la fila 0... como el id.
            table1.getColumnModel().getColumn(0).setMinWidth(0);
            table1.getColumnModel().getColumn(0).setMaxWidth(0);
            table1.getColumnModel().getColumn(0).setWidth(0);

        }catch(SQLException c){

            c.printStackTrace();
        }

    };
*/

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


        res.setMinimumSize(new java.awt.Dimension(900, 700));
        res.setLocationRelativeTo(null);
    }

}
