

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class HW2 {
    public static void main(String[] args) throws SQLException, UnsupportedLookAndFeelException {
        try {        
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(HW2.class.getName()).log(Level.SEVERE, null, ex);
        }
        JFrame jFrame=new JFrame("Saurabh Jaluka , 1212768133");
        MainPanel jpanel=new MainPanel();
        jFrame.add(jpanel);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}
