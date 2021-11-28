import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainForm{
    private JPanel panel1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextArea textArea1;
    private JButton goButton;
    private Main main;


    public MainForm() {
        main = new Main();
        this.goButton.addActionListener(this.button1Clicked());
    }

    public ActionListener button1Clicked() {
        return e ->{
            int length = Integer.parseInt(textField1.getText());
            int count = Integer.parseInt(textField2.getText());
            main.prepareData(count);
            this.textArea1.append("Length: " + length + "\n");
            this.textArea1.append("Products: " + main.getProducts().size() + "\n");
            this.textArea1.append("Transactions: " + main.getshoppingCarts().size() + "\n");
            main.setLength(length);
            SwingUtilities.invokeLater(main);
        };
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600,400));
        frame.pack();
        frame.setVisible(true);
    }
}
