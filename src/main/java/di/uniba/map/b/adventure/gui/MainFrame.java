package di.uniba.map.b.adventure.gui;

import java.awt.Color;
import java.awt.Font;
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
import java.util.concurrent.atomic.AtomicInteger;
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
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;
import di.uniba.map.b.adventure.engine.Engine;
import di.uniba.map.b.adventure.engine.GameManager;

/**
 * @author Pierdamiano Zagaria
 */
public class MainFrame extends JFrame {

    private NoiseFXPanel noisePanel;

    private Engine engine;

    private boolean isSlowlyWriting = false;

    public MainFrame() {
        initComponents();
        init();
        GameManager game = new GameManager();
        engine = new Engine(game, this);
    }

    private void init() {
        try {
            java.awt.Font compassFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream(
                            "/di/uniba/map/b/adventure/resources/LEIXO-DEMO.ttf"));
            lblCompassNorthText.setFont(compassFont.deriveFont(java.awt.Font.PLAIN, 21f));
            lblCompassSouthText.setFont(compassFont.deriveFont(java.awt.Font.PLAIN, 21f));
            lblCompassEastText.setFont(compassFont.deriveFont(java.awt.Font.PLAIN, 21f));
            lblCompassWestText.setFont(compassFont.deriveFont(java.awt.Font.PLAIN, 21f));
            lblCompassNorthEastText.setFont(compassFont.deriveFont(java.awt.Font.PLAIN, 15f));
            lblCompassNorthWestText.setFont(compassFont.deriveFont(java.awt.Font.PLAIN, 15f));
            lblCompassSouthEastText.setFont(compassFont.deriveFont(java.awt.Font.PLAIN, 15f));
            lblCompassSouthWestText.setFont(compassFont.deriveFont(java.awt.Font.PLAIN, 15f));
        } catch (Exception ex) {
            showFatalError(ex.getMessage());
        }
        noisePanel = new NoiseFXPanel();
        lypRoomImage.add(noisePanel, new Integer(1));

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
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlHeader = new JPanel();
        lblRoomName = new JLabel();
        pnlActions = new JPanel();
        lblActions = new JLabel();
        pnlInOut = new JPanel();
        scrOutput = new ModernScrollPane(txtPane);
        txtPane = new JTextPane();
        pnlInput = new JPanel();
        lblInput = new JLabel();
        txtInput = new JTextField();
        pnlCompass = new JPanel();
        lblCompassNorthImage = new JLabel();
        lblCompassWestImage = new JLabel();
        lblCompassCenterImage = new JLabel();
        lblCompassEastImage = new JLabel();
        lblCompassSouthImage = new JLabel();
        pnlCompassNorthWest = new JPanel();
        lblCompassNorthWestImage = new JLabel();
        lblCompassNorthWestText = new RotatedJLabel(-0.7);
        pnlCompassNorthEast = new JPanel();
        lblCompassNorthEastText = new RotatedJLabel(0.8);
        lblCompassNorthEastImage = new JLabel();
        pnlCompassSouthWest = new JPanel();
        lblCompassSouthWestImage = new JLabel();
        lblCompassSouthWestText = new RotatedJLabel(-2.3);
        pnlCompassSouthEast = new JPanel();
        lblCompassSouthEastImage = new JLabel();
        lblCompassSouthEastText = new RotatedJLabel(2.4);
        lblCompassSouthText = new JLabel();
        lblCompassNorthText = new JLabel();
        lblCompassWestText = new JLabel();
        lblCompassEastText = new JLabel();
        lypRoomImage = new JLayeredPane();
        lblRoomImage = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new java.awt.Dimension(583, 852));

        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        pnlHeader.setPreferredSize(new java.awt.Dimension(583, 35));
        pnlHeader.setLayout(new java.awt.BorderLayout());

        lblRoomName.setFont(new java.awt.Font("Segoe UI", 0, 18));
        lblRoomName.setText("Nome stanza");
        lblRoomName.setMaximumSize(new java.awt.Dimension(500, 25));
        lblRoomName.setMinimumSize(new java.awt.Dimension(450, 25));
        lblRoomName.setName("");
        lblRoomName.setPreferredSize(new java.awt.Dimension(700, 35));
        pnlHeader.add(lblRoomName, java.awt.BorderLayout.WEST);

        pnlActions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        lblActions.setFont(new java.awt.Font("Segoe UI", 0, 18));
        lblActions.setText("Azioni: 0");
        lblActions.setPreferredSize(new java.awt.Dimension(106, 35));
        pnlActions.add(lblActions);

        pnlHeader.add(pnlActions, java.awt.BorderLayout.EAST);

        getContentPane().add(pnlHeader, java.awt.BorderLayout.NORTH);

        pnlInOut.setPreferredSize(new java.awt.Dimension(583, 517));
        pnlInOut.setLayout(new java.awt.BorderLayout());

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

        pnlInOut.add(scrOutput, java.awt.BorderLayout.CENTER);

        pnlInput.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING, 5, 0));

        lblInput.setFont(new java.awt.Font("Segoe UI", 0, 18));
        lblInput.setText(">");
        pnlInput.add(lblInput);

        txtInput.setBackground(java.awt.Color.decode("#d6d9df"));
        txtInput.setFont(new java.awt.Font("Segoe UI", 0, 18));
        txtInput.setText("");
        txtInput.setBorder(null);
        txtInput.setCaret(new CustomCaret());
        txtInput.setPreferredSize(new java.awt.Dimension(840, 25));
        txtInput.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    String txt = txtInput.getText();
                    if (txt != null && !txt.isEmpty()) {
                        appendTxtPane(txt, true);
                        txtInput.setText("");
                        engine.commandPerformed(txt);
                    }
                }
            }
        });
        pnlInput.add(txtInput);

        pnlInOut.add(pnlInput, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlInOut, java.awt.BorderLayout.PAGE_END);

        pnlCompass
                .setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlCompass.setPreferredSize(new java.awt.Dimension(300, 300));
        pnlCompass.setLayout(new java.awt.GridBagLayout());

        lblCompassNorthImage.setIcon(new ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/resources/bussola_02.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        pnlCompass.add(lblCompassNorthImage, gridBagConstraints);

        lblCompassWestImage.setIcon(new ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/resources/bussola_04.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassWestImage, gridBagConstraints);

        lblCompassCenterImage.setIcon(new ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/resources/bussola_05.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassCenterImage, gridBagConstraints);

        lblCompassEastImage.setIcon(new ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/resources/bussola_06.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassEastImage, gridBagConstraints);

        lblCompassSouthImage.setIcon(new ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/resources/bussola_08.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(lblCompassSouthImage, gridBagConstraints);

        pnlCompassNorthWest.setLayout(new OverlayLayout(pnlCompassNorthWest));

        lblCompassNorthWestImage.setIcon(new ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/resources/bussola_01.png")));
        pnlCompassNorthWest.add(lblCompassNorthWestImage);

        lblCompassNorthWestText.setHorizontalAlignment(SwingConstants.CENTER);
        lblCompassNorthWestText.setText("NW");
        lblCompassNorthWestText.setMaximumSize(new java.awt.Dimension(83, 83));
        lblCompassNorthWestText.setMinimumSize(new java.awt.Dimension(83, 83));
        lblCompassNorthWestText.setPreferredSize(new java.awt.Dimension(83, 83));
        pnlCompassNorthWest.add(lblCompassNorthWestText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlCompass.add(pnlCompassNorthWest, gridBagConstraints);

        pnlCompassNorthEast.setLayout(new OverlayLayout(pnlCompassNorthEast));

        lblCompassNorthEastImage.setIcon(new ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/resources/bussola_03.png")));
        pnlCompassNorthEast.add(lblCompassNorthEastImage);

        lblCompassNorthEastText.setHorizontalAlignment(SwingConstants.CENTER);
        lblCompassNorthEastText.setText("NE");
        lblCompassNorthEastText.setMaximumSize(new java.awt.Dimension(83, 83));
        lblCompassNorthEastText.setMinimumSize(new java.awt.Dimension(83, 83));
        lblCompassNorthEastText.setPreferredSize(new java.awt.Dimension(83, 83));
        pnlCompassNorthEast.add(lblCompassNorthEastText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        pnlCompass.add(pnlCompassNorthEast, gridBagConstraints);

        pnlCompassSouthWest.setLayout(new OverlayLayout(pnlCompassSouthWest));

        lblCompassSouthWestImage.setFont(new java.awt.Font("Segoe UI", 0, 24));
        lblCompassSouthWestImage.setIcon(new ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/resources/bussola_07.png")));
        lblCompassSouthWestImage.setHorizontalTextPosition(SwingConstants.CENTER);
        pnlCompassSouthWest.add(lblCompassSouthWestImage);

        lblCompassSouthWestText.setHorizontalAlignment(SwingConstants.CENTER);
        lblCompassSouthWestText.setText("SW");
        lblCompassSouthWestText.setMaximumSize(new java.awt.Dimension(83, 83));
        lblCompassSouthWestText.setMinimumSize(new java.awt.Dimension(83, 83));
        lblCompassSouthWestText.setPreferredSize(new java.awt.Dimension(83, 83));
        pnlCompassSouthWest.add(lblCompassSouthWestText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(pnlCompassSouthWest, gridBagConstraints);

        pnlCompassSouthEast.setLayout(new OverlayLayout(pnlCompassSouthEast));

        lblCompassSouthEastImage.setFont(new java.awt.Font("Segoe UI", 0, 24));
        lblCompassSouthEastImage.setIcon(new ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/resources/bussola_09.png")));
        lblCompassSouthEastImage.setHorizontalTextPosition(SwingConstants.CENTER);
        pnlCompassSouthEast.add(lblCompassSouthEastImage);

        lblCompassSouthEastText.setHorizontalAlignment(SwingConstants.CENTER);
        lblCompassSouthEastText.setText("SE");
        lblCompassSouthEastText.setMaximumSize(new java.awt.Dimension(83, 83));
        lblCompassSouthEastText.setMinimumSize(new java.awt.Dimension(83, 83));
        lblCompassSouthEastText.setPreferredSize(new java.awt.Dimension(83, 83));
        pnlCompassSouthEast.add(lblCompassSouthEastText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(pnlCompassSouthEast, gridBagConstraints);

        lblCompassSouthText.setFont(new java.awt.Font("Segoe UI", 0, 18));
        lblCompassSouthText.setText("s");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        pnlCompass.add(lblCompassSouthText, gridBagConstraints);

        lblCompassNorthText.setFont(new java.awt.Font("Segoe UI", 0, 18));
        lblCompassNorthText.setText("n");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlCompass.add(lblCompassNorthText, gridBagConstraints);

        lblCompassWestText.setFont(new java.awt.Font("Segoe UI", 0, 18));
        lblCompassWestText.setText("w");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassWestText, gridBagConstraints);

        lblCompassEastText.setFont(new java.awt.Font("Segoe UI", 0, 18));
        lblCompassEastText.setText("e");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassEastText, gridBagConstraints);

        getContentPane().add(pnlCompass, java.awt.BorderLayout.WEST);

        lypRoomImage.setLayout(new OverlayLayout(lypRoomImage));

        lblRoomImage
                .setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblRoomImage.setName("");
        lblRoomImage.setPreferredSize(new java.awt.Dimension(581, 300));
        lypRoomImage.add(lblRoomImage);

        getContentPane().add(lypRoomImage, java.awt.BorderLayout.CENTER);

        pack();
    }

    public void appendTxtPane(String text, boolean isInputText) {
        StyledDocument doc = txtPane.getStyledDocument();

        try {
            if (isInputText) {
                doc.insertString(doc.getLength(), String.format("\n\n>%s", text), null);
            } else {
                printSlowly(text, 15);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void showFatalError(String message) {
        WindowEvent event = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message, "Fatal Error",
                        JOptionPane.ERROR_MESSAGE);
                dispatchEvent(event);
            }
        });

    }

    public void printSlowly(String message, int millisPerChar) throws BadLocationException {
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
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER
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

                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        txtInput.setText("Premere INVIO per continuare...");
                        txtInput.setFocusable(false);
                        txtInput.setEditable(false);

                        KeyboardFocusManager manager =
                                KeyboardFocusManager.getCurrentKeyboardFocusManager();
                        manager.addKeyEventDispatcher(new KeyEventDispatcher() {

                            @Override
                            public boolean dispatchKeyEvent(KeyEvent e) {
                                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER
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
                    };

                });
            }
        }.start();;

        // }.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    private JTextPane txtPane;
    private JLabel lblActions;
    private JLabel lblCompassCenterImage;
    private JLabel lblCompassEastImage;
    private JLabel lblCompassEastText;
    private JLabel lblCompassNorthEastImage;
    private JLabel lblCompassNorthEastText;
    private JLabel lblCompassNorthImage;
    private JLabel lblCompassNorthText;
    private JLabel lblCompassNorthWestImage;
    private JLabel lblCompassNorthWestText;
    private JLabel lblCompassSouthEastImage;
    private JLabel lblCompassSouthEastText;
    private JLabel lblCompassSouthImage;
    private JLabel lblCompassSouthText;
    private JLabel lblCompassSouthWestImage;
    private JLabel lblCompassSouthWestText;
    private JLabel lblCompassWestImage;
    private JLabel lblCompassWestText;
    private JLabel lblInput;
    private JLabel lblRoomImage;
    private JLabel lblRoomName;
    private JLayeredPane lypRoomImage;
    private JPanel pnlActions;
    private JPanel pnlCompass;
    private JPanel pnlCompassNorthEast;
    private JPanel pnlCompassNorthWest;
    private JPanel pnlCompassSouthEast;
    private JPanel pnlCompassSouthWest;
    private JPanel pnlHeader;
    private JPanel pnlInOut;
    private JPanel pnlInput;
    private JScrollPane scrOutput;
    private JTextField txtInput;

    public JTextPane getEdtOutput() {
        return txtPane;
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

    public JTextField getTxtInput() {
        return txtInput;
    }
}
