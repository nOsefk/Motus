
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

//	import javax.swing.JOptionPane;


public class MotusHard extends JFrame implements ActionListener {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Composants
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final JPanel panContainer = (JPanel) this.getContentPane();
    private final JPanel pan_north = new JPanel();
    private final JPanel pan_center = new JPanel();
    private final JPanel pan_south = new JPanel();


    // liste des mots
    private String[] words = {"recrache", "souffrir","venimeux", "septique", "acronyme", "lancent", "enceinte", "goulasch", "fasciner", "plancton"};

    // variables du jeu
    private String solution = "";
    private String last_wrd = "";
    private Random random = new Random();
    private int wrdnb = 0;
    private boolean wrd_found = false;
    private boolean wrd_ok = true;
    private boolean game_ok = false;
    private boolean[][] rght_plc = new boolean[8][8];
    private boolean[][] wrg_plc = new boolean[8][8];
    private boolean[] histo = new boolean[8];
    private String[] wrd_entered = new String[8];

    // Les composants de la fenetre

    private JButton cmd_ok;
    private JTextField txtwrd;
    private JLabel lbl_word;
    private JLabel lbl_title;
    private JLabel lblmessage;
    private JLabel lbl_empty;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructeurs
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public MotusHard() {

        initComposants();
        choseWord();
        setVisible(true);

    }

    private void initComposants() {


        this.setBounds(300, 300, 420, 420);
        this.setResizable(false);
        panContainer.setLayout(new BorderLayout());

        lbl_word = new JLabel("Mot à trouver : ");
        lbl_word.setFont(new Font("Roboto", Font.BOLD, 12));
        lbl_word.setPreferredSize(new Dimension(100, 20));

        lbl_title = new JLabel ("Motus");
        lbl_title.setFont(new Font("Roboto", Font.BOLD, 20));


        txtwrd = new JTextField("", 10);
        //txtwrd.setBounds(130, 310, 90, 20);
        setPreferredSize(new Dimension(70, 20));
        txtwrd.addActionListener(this);

        cmd_ok = new JButton("Ok");
        cmd_ok.addActionListener(this);
        cmd_ok.setPreferredSize(new Dimension(90, 20));

        lblmessage = new JLabel();
        lblmessage.setPreferredSize(new Dimension(300, 20));

        lbl_title = new JLabel("Motus");
        lbl_title.setFont(new Font("Roboto", Font.BOLD, 30));

        pan_north.setLayout(new FlowLayout());
        pan_north.add(lbl_title);

        pan_center.setLayout(new BorderLayout());
        pan_center.add(new Painter());

        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout layoutSouth = new GridBagLayout();
        pan_south.setLayout(layoutSouth);
        //pan_south.setLayout(new BorderLayout());
        c.insets = new Insets(0,20,0,0);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        pan_south.add(lblmessage, c);
        c.insets = new Insets(10,0,15,10);
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        pan_south.add(lbl_word, c);
        c.gridx = 1;
        pan_south.add(txtwrd, c);
        c.gridx = 2;
        pan_south.add(cmd_ok, c);


        panContainer.add(pan_north, BorderLayout.NORTH);
        panContainer.add(pan_center, BorderLayout.CENTER);
        panContainer.add(pan_south, BorderLayout.SOUTH);
        panContainer.setVisible(true);

        txtwrd.requestFocus();

        panContainer.getRootPane().setDefaultButton(cmd_ok);
// remove the binding for pressed
        panContainer.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ENTER"), "none");
// retarget the binding for released
        panContainer.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("released ENTER"), "press");
    }

    //nouveau mot

    private void choseWord() {
        solution = words[random.nextInt(words.length)].toUpperCase();
        if (solution.equals(last_wrd)) {
            choseWord();
        }
        last_wrd = solution;
    }


    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {

            case "Ok":
                test_word();
                if (!wrd_ok) {
                    lblmessage.setText("Entrez un mot de 8 lettres !");
                    break;
                }
                else {
                    wrdnb++;
                    lblmessage.setText("");
                    wrd_entered[wrdnb] = txtwrd.getText().toUpperCase();
                    repaint();
                    panContainer.revalidate();
                    break;
                }
            case "Restart":
                txtwrd.setText("");
                lblmessage.setText("");
                wrdnb=0;
                choseWord();
                wrd_found=false;
                for(int i=1;i<8;i++) {
                    for (int j = 0; j < 8; j++) {
                        rght_plc[i][j] = false;
                        this.histo[j] = false;
                    }
                }

                txtwrd.setEnabled(true);
                game_ok=false;
                panContainer.revalidate();
                cmd_ok.setText("Ok");
                repaint();
        }

    }

    public class Painter extends JPanel {
        public void paint(Graphics g) {
            drawGrid(g);

            if (wrdnb == 0) {
                firstLetter(g);
            } else {
                drawHisto(g);
                drawPlaceRight(g);
                drawPlaceWrong(g);
                g.setColor(Color.black);

                writeWord(g);

                if (wrd_found && !game_ok) {
                    initWordFound();
                } else if (wrdnb == 5 && !wrd_found) {
                    initLost();
                } else if (!game_ok) {
                    nextWord(g);
                }
            }
        }

        // Dessiner la grille

        private void drawGrid(Graphics g) {
            g.setColor(Color.black);
            g.setFont(new Font("Roboto", Font.BOLD, 20));

            int x = 40;
            int y = 40;
            int nb = 0;

            while (nb < 40) {
                g.drawRect(x, y, 40, 40);
                x += 40;
                nb++;
                if (nb % 8 == 0) {
                    x = 40;
                    y += 40;
                }
            }

        }


        //Ligne suivante

        private void nextWord(Graphics g) {
            g.drawString(String.valueOf(solution.charAt(0)), 55, 70 + 40 * wrdnb);

            int pos = 1;
            int x = 95;
            int y = 70 + 40 * wrdnb;

            while (pos < 8) {
                if (histo[pos]) {
                    g.drawString(String.valueOf(solution.charAt(pos)), x, y);
                } else {
                    g.drawString(".", x, y);
                }
                x += 40;
                pos++;
            }

            txtwrd.setText("");

        }

        //Afficher première lettre

        private void firstLetter(Graphics g) {
            g.drawString(String.valueOf(solution.charAt(0)), 55, 70);

            int pos = 1;
            int x = 95;
            int y = 70 + 40 * wrdnb;

            while (pos < 8) {
                g.drawString(".", x, y);
                x += 40;
                pos++;
            }


        }


        //Bonnes et mauvaises position :

        private void drawHisto(Graphics g) {
            int x;
            int y;
            int i;
            int j;
            g.setColor(Color.yellow);

            for (i = 1; i < wrdnb; i++) {
                x = 40;
                y = 40 + 40 * (i - 1);
                for (j = 0; j < 8; j++) {
                    if (wrg_plc[i][j]) {
                        g.fillOval(x + 3, y + 3, 35, 35);
                    }
                    x += 40;
                }
            }

            g.setColor(Color.green);

            for (i = 1; i < wrdnb; i++) {
                x = 40;
                y = 40 + 40 * (i - 1);
                for (j = 0; j < 8; j++) {
                    if (rght_plc[i][j]) {
                        g.fillRect(x + 3, y + 3, 35, 35);
                    }
                    x += 40;
                }
            }
        }

        // Ecrire le mot dans la Grille

        private void writeWord(Graphics g) {

            int pos;
            int x;
            int y;

            for (int cpt = 1; cpt <= wrdnb; cpt++) {
                pos = 0;
                x = 55;
                y = 70 + 40 * (cpt - 1);

                while (pos < 8) {
                    g.drawString(String.valueOf(wrd_entered[cpt].charAt(pos)), x, y);
                    x += 40;
                    pos++;
                }
            }

        }


        // Dessiner les rectangles verts

        private void drawPlaceRight(Graphics g) {
            int x;
            int y;
            int pos;
            g.setColor(Color.green);
            pos = 0;
            x = 40;
            y = 40 + 40 * (wrdnb - 1);

            while (pos < 8) {

                if (wrd_entered[wrdnb].charAt(pos) == solution.charAt(pos)) {
                    g.fillRect(x + 3, y + 3, 35, 35);
                    rght_plc[wrdnb][pos] = true;
                    histo[pos] = true;
                } else {
                    rght_plc[wrdnb][pos] = false;
                }
                x += 40;
                pos++;
            }

            if (wrd_entered[wrdnb].equals(solution)) {
                wrd_found = true;
            }
        }

        // Dessiner les cercles jaunes

        private void drawPlaceWrong(Graphics g) {

            String sol_tmp = solution;
            char[] sol_tmp_char = sol_tmp.toCharArray();
            for (int i = 0; i < 8; i++) {
                if (sol_tmp.charAt(i) == wrd_entered[wrdnb].charAt(i)) {
                    sol_tmp_char[i] = ' ';
                }
            }
            sol_tmp = String.valueOf(sol_tmp_char);

            g.setColor(Color.yellow);
            int i = 0;
            int x = 40;
            int y = 40 + 40 * (wrdnb - 1);

            while (i < 8) {
                char letter = wrd_entered[wrdnb].charAt(i);
                if (occurence(letter, sol_tmp) > 0) {
                    g.fillOval(x + 3, y + 3, 35, 35);
                    wrg_plc[wrdnb][i] = true;
                    sol_tmp = remove(letter, sol_tmp);
                } else {
                    wrg_plc[wrdnb][i] = false;
                }
                x += 40;
                i++;
            }

        }

        // enlever la lettre de la liste temporaire

        private String remove(char letter, String sol_tmp) {
            int i = 0;
            String result = "";
            while (i < sol_tmp.length()) {
                if (sol_tmp.charAt(i) == letter) {
                    letter = '1';
                } else result += sol_tmp.charAt(i);
                i++;
            }

            return result;


        }
        //trouver si la lettre est dans le mot mais mauvaise place

        private int occurence(char letter, String sol_temp) {
            int res = 0;
            int i = 0;
            while (i < sol_temp.length()) {
                if (sol_temp.charAt(i) == letter) {
                    res++;
                }
                i++;
            }
            return res;
        }
    }

    // Partie Perdue

    private void initLost() {
        lblmessage.setText("Perdu ! Le mot à trouver était : " + solution);
        txtwrd.setText("");
        wrdnb = 0;
        cmd_ok.setText("Restart");
    }

    //Partie gagnée

    private void initWordFound() {
        lblmessage.setText("Bravo vous avez trouvez le mot!");
        txtwrd.setText("");
        game_ok = true;
        cmd_ok.setText("Restart");
    }


    // vérifier si le mot entré fait bien 7 lettres et que ce sont bien des lettres

    private void test_word() {
        String txtwrd_check = txtwrd.getText();
        if (txtwrd_check.length() != 8)
            wrd_ok = false;
        else {
            boolean isChar = true;
            int pos = 0;
            while (pos < 8 && isChar) {
                isChar = Character.isLetter(txtwrd_check.charAt(pos));
                pos++;
            }
            wrd_ok = isChar;
        }
    }

}



