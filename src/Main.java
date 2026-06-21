import javax.swing.*;

public class Main {


    public static void main(String[] args) {
        JFrame lg = new JFrame("LOGIN");
        lg.setContentPane(new Login().loginjp);
        lg.pack();
        lg.setVisible(true);


        lg.setMinimumSize(new java.awt.Dimension(600, 500));

        lg.setLocationRelativeTo(null);
    }
}