package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;
import engine.Engine;
import engine.GameManager;

public class MainFrame extends JFrame {

    private Engine engine;

    private boolean isSlowlyWriting = false;

    private NoiseFXPanel noisePanel = new NoiseFXPanel();
    private JTextPane txtPane = new JTextPane();
    private JLabel lblActions = new JLabel();
    private JLabel lblCompassCenterImage = new JLabel();
    private JLabel lblCompassEastImage = new JLabel();
    private JLabel lblCompassEastText = new JLabel();
    private JLabel lblCompassNorthEastImage = new JLabel();
    private JLabel lblCompassNorthEastText = new RotatedJLabel(0.8);
    private JLabel lblCompassNorthImage = new JLabel();
    private JLabel lblCompassNorthText = new JLabel();
    private JLabel lblCompassNorthWestImage = new JLabel();
    private JLabel lblCompassNorthWestText = new RotatedJLabel(-0.7);
    private JLabel lblCompassSouthEastImage = new JLabel();
    private JLabel lblCompassSouthEastText = new RotatedJLabel(2.4);
    private JLabel lblCompassSouthImage = new JLabel();
    private JLabel lblCompassSouthText = new JLabel();
    private JLabel lblCompassSouthWestImage = new JLabel();
    private JLabel lblCompassSouthWestText = new RotatedJLabel(-2.3);
    private JLabel lblCompassWestImage = new JLabel();
    private JLabel lblCompassWestText = new JLabel();
    private JLabel lblInput = new JLabel();
    private JLabel lblRoomImage = new JLabel();
    private JLabel lblRoomName = new JLabel();
    private JLayeredPane lypRoomImage = new JLayeredPane();
    private JPanel pnlActions = new JPanel();
    private JPanel pnlCompass = new JPanel();
    private JPanel pnlCompassNorthEast = new JPanel();
    private JPanel pnlCompassNorthWest = new JPanel();
    private JPanel pnlCompassSouthEast = new JPanel();
    private JPanel pnlCompassSouthWest = new JPanel();
    private JPanel pnlHeader = new JPanel();
    private JPanel pnlInOut = new JPanel();
    private JPanel pnlInput = new JPanel();
    private JScrollPane scrOutput = new ModernScrollPane(txtPane);
    private JTextField txtInput = new JTextField();

    public MainFrame() {
        initComponents();
        GameManager game = new GameManager();
        engine = new Engine(game, this);
    }

    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new Dimension(583, 852));

        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        pnlHeader.setPreferredSize(new Dimension(583, 35));
        pnlHeader.setLayout(new BorderLayout());

        lblRoomName.setFont(new Font("Segoe UI", 0, 18));
        lblRoomName.setText("Nome stanza");
        lblRoomName.setMaximumSize(new Dimension(500, 25));
        lblRoomName.setMinimumSize(new Dimension(450, 25));
        lblRoomName.setName("");
        lblRoomName.setPreferredSize(new Dimension(700, 35));
        pnlHeader.add(lblRoomName, BorderLayout.WEST);

        pnlActions.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        lblActions.setFont(new Font("Segoe UI", 0, 18));
        lblActions.setText("Azioni: 0");
        lblActions.setPreferredSize(new Dimension(106, 35));
        pnlActions.add(lblActions);

        pnlHeader.add(pnlActions, BorderLayout.EAST);

        getContentPane().add(pnlHeader, BorderLayout.NORTH);

        pnlInOut.setPreferredSize(new Dimension(583, 517));
        pnlInOut.setLayout(new BorderLayout());

        scrOutput.setBorder(null);

        Color bgColor = new Color(214, 217, 223);
        UIDefaults defaults = new UIDefaults();
        defaults.put("TextPane[Enabled].backgroundPainter", bgColor);
        txtPane.putClientProperty("Nimbus.Overrides", defaults);
        txtPane.putClientProperty("Nimbus.Overrides.InheritDefaults", true);

        txtPane.setEditable(false);
        txtPane.setBackground(bgColor);
        txtPane.setMargin(new Insets(2, 2, 2, 2));
        txtPane.setFont(new Font("Serif", 0, 16));
        scrOutput.setViewportView(txtPane);

        pnlInOut.add(scrOutput, BorderLayout.CENTER);

        pnlInput.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 0));

        lblInput.setFont(new Font("Segoe UI", 0, 18));
        lblInput.setText(">");
        pnlInput.add(lblInput);

        txtInput.setBackground(Color.decode("#d6d9df"));
        txtInput.setFont(new Font("Segoe UI", 0, 18));
        txtInput.setText("");
        txtInput.setBorder(null);
        txtInput.setCaret(new CustomCaret());
        txtInput.setPreferredSize(new Dimension(840, 25));
        txtInput.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    String txt = txtInput.getText();
                    if (txt != null && !txt.isEmpty()) {
                        appendText(txt, true);
                        txtInput.setText("");
                        engine.commandPerformed(txt);
                    }
                }
            }
        });
        pnlInput.add(txtInput);

        pnlInOut.add(pnlInput, BorderLayout.SOUTH);

        getContentPane().add(pnlInOut, BorderLayout.PAGE_END);

        pnlCompass.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        pnlCompass.setPreferredSize(new Dimension(300, 300));
        pnlCompass.setLayout(new GridBagLayout());

        try {
            lblCompassNorthImage.setIcon(getResourceAsImageIcon("/resources/img/bussola_02.png"));
            lblCompassWestImage.setIcon(getResourceAsImageIcon("/resources/img/bussola_04.png"));
            lblCompassCenterImage.setIcon(getResourceAsImageIcon("/resources/img/bussola_05.png"));
            lblCompassEastImage.setIcon(getResourceAsImageIcon("/resources/img/bussola_06.png"));
            lblCompassSouthImage.setIcon(getResourceAsImageIcon("/resources/img/bussola_08.png"));
            lblCompassNorthWestImage
                    .setIcon(getResourceAsImageIcon("/resources/img/bussola_01.png"));
            lblCompassNorthEastImage
                    .setIcon(getResourceAsImageIcon("/resources/img/bussola_03.png"));
            lblCompassSouthWestImage
                    .setIcon(getResourceAsImageIcon("/resources/img/bussola_07.png"));
            lblCompassSouthEastImage
                    .setIcon(getResourceAsImageIcon("/resources/img/bussola_09.png"));
        } catch (IOException e) {
            showFatalError(this, "Error occurred on loading of compass images. Details: "
                    + e.getMessage());
        }

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        pnlCompass.add(lblCompassNorthImage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassWestImage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassCenterImage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassEastImage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(lblCompassSouthImage, gridBagConstraints);

        pnlCompassNorthWest.setLayout(new OverlayLayout(pnlCompassNorthWest));

        pnlCompassNorthWest.add(lblCompassNorthWestImage);

        lblCompassNorthWestText.setHorizontalAlignment(SwingConstants.CENTER);
        lblCompassNorthWestText.setText("NW");
        lblCompassNorthWestText.setMaximumSize(new Dimension(83, 83));
        lblCompassNorthWestText.setMinimumSize(new Dimension(83, 83));
        lblCompassNorthWestText.setPreferredSize(new Dimension(83, 83));
        pnlCompassNorthWest.add(lblCompassNorthWestText);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlCompass.add(pnlCompassNorthWest, gridBagConstraints);

        pnlCompassNorthEast.setLayout(new OverlayLayout(pnlCompassNorthEast));

        pnlCompassNorthEast.add(lblCompassNorthEastImage);

        lblCompassNorthEastText.setHorizontalAlignment(SwingConstants.CENTER);
        lblCompassNorthEastText.setText("NE");
        lblCompassNorthEastText.setMaximumSize(new Dimension(83, 83));
        lblCompassNorthEastText.setMinimumSize(new Dimension(83, 83));
        lblCompassNorthEastText.setPreferredSize(new Dimension(83, 83));
        pnlCompassNorthEast.add(lblCompassNorthEastText);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        pnlCompass.add(pnlCompassNorthEast, gridBagConstraints);

        pnlCompassSouthWest.setLayout(new OverlayLayout(pnlCompassSouthWest));

        lblCompassSouthWestImage.setFont(new Font("Segoe UI", 0, 24));

        lblCompassSouthWestImage.setHorizontalTextPosition(SwingConstants.CENTER);
        pnlCompassSouthWest.add(lblCompassSouthWestImage);

        lblCompassSouthWestText.setHorizontalAlignment(SwingConstants.CENTER);
        lblCompassSouthWestText.setText("SW");
        lblCompassSouthWestText.setMaximumSize(new Dimension(83, 83));
        lblCompassSouthWestText.setMinimumSize(new Dimension(83, 83));
        lblCompassSouthWestText.setPreferredSize(new Dimension(83, 83));
        pnlCompassSouthWest.add(lblCompassSouthWestText);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(pnlCompassSouthWest, gridBagConstraints);

        pnlCompassSouthEast.setLayout(new OverlayLayout(pnlCompassSouthEast));
        lblCompassSouthEastImage.setFont(new Font("Segoe UI", 0, 24));
        lblCompassSouthEastImage.setHorizontalTextPosition(SwingConstants.CENTER);
        pnlCompassSouthEast.add(lblCompassSouthEastImage);

        lblCompassSouthEastText.setHorizontalAlignment(SwingConstants.CENTER);
        lblCompassSouthEastText.setText("SE");
        lblCompassSouthEastText.setMaximumSize(new Dimension(83, 83));
        lblCompassSouthEastText.setMinimumSize(new Dimension(83, 83));
        lblCompassSouthEastText.setPreferredSize(new Dimension(83, 83));
        pnlCompassSouthEast.add(lblCompassSouthEastText);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(pnlCompassSouthEast, gridBagConstraints);

        lblCompassSouthText.setFont(new Font("Segoe UI", 0, 18));
        lblCompassSouthText.setText("s");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        pnlCompass.add(lblCompassSouthText, gridBagConstraints);

        lblCompassNorthText.setFont(new Font("Segoe UI", 0, 18));
        lblCompassNorthText.setText("n");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlCompass.add(lblCompassNorthText, gridBagConstraints);

        lblCompassWestText.setFont(new Font("Segoe UI", 0, 18));
        lblCompassWestText.setText("w");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassWestText, gridBagConstraints);

        lblCompassEastText.setFont(new Font("Segoe UI", 0, 18));
        lblCompassEastText.setText("e");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassEastText, gridBagConstraints);

        getContentPane().add(pnlCompass, BorderLayout.WEST);

        lypRoomImage.setLayout(new OverlayLayout(lypRoomImage));

        lblRoomImage.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        lblRoomImage.setName("");
        lblRoomImage.setPreferredSize(new Dimension(581, 300));
        lypRoomImage.add(lblRoomImage);

        try {
            Font compassFont = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream(
                            "/resources/font/LEIXO-DEMO.ttf"));
            lblCompassNorthText.setFont(compassFont.deriveFont(Font.PLAIN, 21f));
            lblCompassSouthText.setFont(compassFont.deriveFont(Font.PLAIN, 21f));
            lblCompassEastText.setFont(compassFont.deriveFont(Font.PLAIN, 21f));
            lblCompassWestText.setFont(compassFont.deriveFont(Font.PLAIN, 21f));
            lblCompassNorthEastText.setFont(compassFont.deriveFont(Font.PLAIN, 15f));
            lblCompassNorthWestText.setFont(compassFont.deriveFont(Font.PLAIN, 15f));
            lblCompassSouthEastText.setFont(compassFont.deriveFont(Font.PLAIN, 15f));
            lblCompassSouthWestText.setFont(compassFont.deriveFont(Font.PLAIN, 15f));
        } catch (Exception e) {
            showFatalError(this, e.getMessage());
        }

        lypRoomImage.add(noisePanel, new Integer(1));

        getContentPane().add(lypRoomImage, BorderLayout.CENTER);

        txtInput.requestFocus();
        txtInput.setCaretPosition(txtInput.getText().length());
        txtPane.setCaretPosition(txtPane.getDocument().getLength());

        ((DefaultCaret) txtPane.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        scrOutput.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            private int max = 0;
            private final BoundedRangeModel model = scrOutput.getVerticalScrollBar().getModel();

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (model.getMaximum() != max) {
                    // Scroll to bottom
                    model.setValue(model.getMaximum() - model.getExtent());
                }
                max = model.getMaximum();
            }
        });

        pack();
    }

    private static ImageIcon getResourceAsImageIcon(String path) throws IOException {
        InputStream inputStream = MainFrame.class.getResourceAsStream(path);

        return new ImageIcon(ImageIO.read(inputStream));
    }

    public void appendText(String text, boolean inputText) {
        StyledDocument doc = txtPane.getStyledDocument();

        try {
            if (inputText) {
                doc.insertString(doc.getLength(), String.format("\n\n>%s", text), null);
            } else {
                printSlowly(text, 15);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void appendText(String text) {
        appendText(text, false);
    }

    public static void showFatalError(MainFrame gui, String message) {
        WindowEvent event = new WindowEvent(gui, WindowEvent.WINDOW_CLOSING);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message, "Fatal Error",
                        JOptionPane.ERROR_MESSAGE);
                gui.dispatchEvent(event);
            }
        });
    }

    public static int askLoadingConfirmation() {
        return JOptionPane.showConfirmDialog(null,
                "É stato trovata una partita precedentemente salvata. Desideri caricarla?",
                "Caricamento salvataggio",
                JOptionPane.YES_NO_OPTION);
    }

    private void printSlowly(String message, int millisPerChar) throws BadLocationException {
        txtInput.setEditable(false);
        txtInput.setFocusable(false);

        StyledDocument doc = txtPane.getStyledDocument();
        doc.insertString(doc.getLength(), "\n\n", null);

        Timer timer = new Timer(millisPerChar, null);
        AtomicInteger counter = new AtomicInteger(0);

        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    doc.insertString(doc.getLength(),
                            String.valueOf(message.charAt(counter.getAndIncrement())),
                            null);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                txtPane.setCaretPosition(txtPane.getDocument().getLength());
                if (counter.get() >= message.length()) {
                    timer.stop();
                    txtInput.setEditable(true);
                    txtInput.setFocusable(true);
                    txtInput.requestFocusInWindow();
                    isSlowlyWriting = false;
                }
            }
        });

        timer.start();
        isSlowlyWriting = true;

        // Skip writing animation
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        && e.getID() == KeyEvent.KEY_RELEASED) {
                    timer.stop();
                    try {
                        doc.insertString(doc.getLength(), message.substring(counter.get()), null);
                        txtInput.setEditable(true);
                        txtInput.setFocusable(true);
                        txtInput.requestFocusInWindow();
                        isSlowlyWriting = false;
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                    manager.removeKeyEventDispatcher(this);
                } else if (counter.get() >= message.length()) {
                    manager.removeKeyEventDispatcher(this);
                }

                return true;
            }

        });

    }

    public void waitForEnterKey() {
        new Thread("WaitForInputThread") {
            public void run() {
                while (isSlowlyWriting) {
                    try {
                        sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    txtInput.setText("Premere INVIO per continuare...");
                    txtInput.setFocusable(false);
                    txtInput.setEditable(false);

                    KeyboardFocusManager manager =
                            KeyboardFocusManager.getCurrentKeyboardFocusManager();
                    manager.addKeyEventDispatcher(new KeyEventDispatcher() {

                        @Override
                        public boolean dispatchKeyEvent(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_ENTER
                                    && e.getID() == KeyEvent.KEY_RELEASED) {
                                txtInput.setText("");
                                txtInput.setEditable(true);
                                txtInput.setFocusable(true);
                                txtInput.requestFocusInWindow();
                                manager.removeKeyEventDispatcher(this);
                                engine.commandPerformed("");
                            }
                            return true;
                        }
                    });
                });
            }
        }.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Create and display the form */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    public JLabel getLblCompassEastText() {
        return lblCompassEastText;
    }

    public JLabel getLblCompassNorthEastText() {
        return lblCompassNorthEastText;
    }

    public JLabel getLblCompassNorthText() {
        return lblCompassNorthText;
    }

    public JLabel getLblCompassNorthWestText() {
        return lblCompassNorthWestText;
    }

    public JLabel getLblCompassSouthEastText() {
        return lblCompassSouthEastText;
    }

    public JLabel getLblCompassSouthText() {
        return lblCompassSouthText;
    }

    public JLabel getLblCompassSouthWestText() {
        return lblCompassSouthWestText;
    }

    public JLabel getLblCompassWestText() {
        return lblCompassWestText;
    }

    public JLabel getLblActions() {
        return lblActions;
    }

    public JLabel getLblRoomImage() {
        return lblRoomImage;
    }

    public JLabel getLblRoomName() {
        return lblRoomName;
    }
}
