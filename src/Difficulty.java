import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Difficulty extends JFrame implements ActionListener  {


    private final JPanel panContainer = (JPanel) this.getContentPane();
    private final JLabel lbl_title = new JLabel ("Motus");
    private final JLabel chose = new JLabel("          Mode :");
    private final JButton easy = new JButton("Facile");
    private final JButton normal = new JButton("Normal");
    private final JButton hard = new JButton("Difficile");



    public Difficulty() {

        initComposants();
        setVisible(true);

    }

    private void initComposants(){

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(450, 450, 200, 240);
        this.setResizable(false);


        lbl_title.setFont(new Font("Roboto", Font.BOLD, 30));
        chose.setPreferredSize(new Dimension(100,30));
        easy.setPreferredSize(new Dimension(100,30));
        easy.addActionListener(this);
        normal.setPreferredSize(new Dimension(100,30));
        normal.addActionListener(this);
        hard.setPreferredSize(new Dimension(100,30));
        hard.addActionListener(this);


        panContainer.setLayout(new FlowLayout());

        panContainer.add(lbl_title);
        panContainer.add(chose);
        panContainer.add(easy);
        panContainer.add(normal);
        panContainer.add(hard);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {

            case "Facile":
                MotusEasy me = new MotusEasy();
                break;

            case "Normal":
                Motus m = new Motus();
                break;

            case "Difficile" :
                MotusHard mh = new MotusHard();
                break;
        }
    }
}
