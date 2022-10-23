package di.uniba.map.b.adventure;

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
import javax.swing.BoundedRangeModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;
import di.uniba.map.b.adventure.engine.Engine;
import di.uniba.map.b.adventure.engine.GameDescription;
import di.uniba.map.b.adventure.engine.HauntedHouseGame;

/**
 * @author Pierdamiano Zagaria
 */
public class GameJFrame extends javax.swing.JFrame {

    private NoiseEffectPanel noisePanel;

    private Engine engine;

    private boolean isSlowlyWriting = false;

    public GameJFrame() {
        initComponents();
        init();
        GameDescription game = new HauntedHouseGame();
        engine = new Engine(game, this);
    }

    private void init() {
        try {
            java.awt.Font compassFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/di/uniba/map/b/adventure/img/LEIXO-DEMO.ttf"));
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
        noisePanel = new NoiseEffectPanel();
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

        pnlHeader = new javax.swing.JPanel();
        lblRoomName = new javax.swing.JLabel();
        pnlActions = new javax.swing.JPanel();
        lblActions = new javax.swing.JLabel();
        pnlInOut = new javax.swing.JPanel();
        scrOutput = new ModernScrollPane(txtPane);
        txtPane = new javax.swing.JTextPane();
        pnlInput = new javax.swing.JPanel();
        lblInput = new javax.swing.JLabel();
        txtInput = new javax.swing.JTextField();
        pnlCompass = new javax.swing.JPanel();
        lblCompassNorthImage = new javax.swing.JLabel();
        lblCompassWestImage = new javax.swing.JLabel();
        lblCompassCenterImage = new javax.swing.JLabel();
        lblCompassEastImage = new javax.swing.JLabel();
        lblCompassSouthImage = new javax.swing.JLabel();
        pnlCompassNorthWest = new javax.swing.JPanel();
        lblCompassNorthWestImage = new javax.swing.JLabel();
        lblCompassNorthWestText = new RotatedJLabel(-0.7);
        pnlCompassNorthEast = new javax.swing.JPanel();
        lblCompassNorthEastText = new RotatedJLabel(0.8);
        lblCompassNorthEastImage = new javax.swing.JLabel();
        pnlCompassSouthWest = new javax.swing.JPanel();
        lblCompassSouthWestImage = new javax.swing.JLabel();
        lblCompassSouthWestText = new RotatedJLabel(-2.3);
        pnlCompassSouthEast = new javax.swing.JPanel();
        lblCompassSouthEastImage = new javax.swing.JLabel();
        lblCompassSouthEastText = new RotatedJLabel(2.4);
        lblCompassSouthText = new javax.swing.JLabel();
        lblCompassNorthText = new javax.swing.JLabel();
        lblCompassWestText = new javax.swing.JLabel();
        lblCompassEastText = new javax.swing.JLabel();
        lypRoomImage = new javax.swing.JLayeredPane();
        lblRoomImage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new java.awt.Dimension(583, 852));

        pnlHeader.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)),
                javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10)));
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
                .setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlCompass.setPreferredSize(new java.awt.Dimension(300, 300));
        pnlCompass.setLayout(new java.awt.GridBagLayout());

        lblCompassNorthImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/img/bussola_02.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        pnlCompass.add(lblCompassNorthImage, gridBagConstraints);

        lblCompassWestImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/img/bussola_04.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassWestImage, gridBagConstraints);

        lblCompassCenterImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/img/bussola_05.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassCenterImage, gridBagConstraints);

        lblCompassEastImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/img/bussola_06.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        pnlCompass.add(lblCompassEastImage, gridBagConstraints);

        lblCompassSouthImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/img/bussola_08.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(lblCompassSouthImage, gridBagConstraints);

        pnlCompassNorthWest.setLayout(new javax.swing.OverlayLayout(pnlCompassNorthWest));

        lblCompassNorthWestImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/img/bussola_01.png")));
        pnlCompassNorthWest.add(lblCompassNorthWestImage);

        lblCompassNorthWestText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCompassNorthWestText.setText("NW");
        lblCompassNorthWestText.setMaximumSize(new java.awt.Dimension(83, 83));
        lblCompassNorthWestText.setMinimumSize(new java.awt.Dimension(83, 83));
        lblCompassNorthWestText.setPreferredSize(new java.awt.Dimension(83, 83));
        pnlCompassNorthWest.add(lblCompassNorthWestText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlCompass.add(pnlCompassNorthWest, gridBagConstraints);

        pnlCompassNorthEast.setLayout(new javax.swing.OverlayLayout(pnlCompassNorthEast));

        lblCompassNorthEastImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/img/bussola_03.png")));
        pnlCompassNorthEast.add(lblCompassNorthEastImage);

        lblCompassNorthEastText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCompassNorthEastText.setText("NE");
        lblCompassNorthEastText.setMaximumSize(new java.awt.Dimension(83, 83));
        lblCompassNorthEastText.setMinimumSize(new java.awt.Dimension(83, 83));
        lblCompassNorthEastText.setPreferredSize(new java.awt.Dimension(83, 83));
        pnlCompassNorthEast.add(lblCompassNorthEastText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        pnlCompass.add(pnlCompassNorthEast, gridBagConstraints);

        pnlCompassSouthWest.setLayout(new javax.swing.OverlayLayout(pnlCompassSouthWest));

        lblCompassSouthWestImage.setFont(new java.awt.Font("Segoe UI", 0, 24));
        lblCompassSouthWestImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/img/bussola_07.png")));
        lblCompassSouthWestImage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlCompassSouthWest.add(lblCompassSouthWestImage);

        lblCompassSouthWestText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCompassSouthWestText.setText("SW");
        lblCompassSouthWestText.setMaximumSize(new java.awt.Dimension(83, 83));
        lblCompassSouthWestText.setMinimumSize(new java.awt.Dimension(83, 83));
        lblCompassSouthWestText.setPreferredSize(new java.awt.Dimension(83, 83));
        pnlCompassSouthWest.add(lblCompassSouthWestText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        pnlCompass.add(pnlCompassSouthWest, gridBagConstraints);

        pnlCompassSouthEast.setLayout(new javax.swing.OverlayLayout(pnlCompassSouthEast));

        lblCompassSouthEastImage.setFont(new java.awt.Font("Segoe UI", 0, 24));
        lblCompassSouthEastImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/di/uniba/map/b/adventure/img/bussola_09.png")));
        lblCompassSouthEastImage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlCompassSouthEast.add(lblCompassSouthEastImage);

        lblCompassSouthEastText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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

        lypRoomImage.setLayout(new javax.swing.OverlayLayout(lypRoomImage));

        lblRoomImage
                .setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameJFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameJFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameJFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameJFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameJFrame().setVisible(true);
            }
        });
    }

    private javax.swing.JTextPane txtPane;
    private javax.swing.JLabel lblActions;
    private javax.swing.JLabel lblCompassCenterImage;
    private javax.swing.JLabel lblCompassEastImage;
    private javax.swing.JLabel lblCompassEastText;
    private javax.swing.JLabel lblCompassNorthEastImage;
    private javax.swing.JLabel lblCompassNorthEastText;
    private javax.swing.JLabel lblCompassNorthImage;
    private javax.swing.JLabel lblCompassNorthText;
    private javax.swing.JLabel lblCompassNorthWestImage;
    private javax.swing.JLabel lblCompassNorthWestText;
    private javax.swing.JLabel lblCompassSouthEastImage;
    private javax.swing.JLabel lblCompassSouthEastText;
    private javax.swing.JLabel lblCompassSouthImage;
    private javax.swing.JLabel lblCompassSouthText;
    private javax.swing.JLabel lblCompassSouthWestImage;
    private javax.swing.JLabel lblCompassSouthWestText;
    private javax.swing.JLabel lblCompassWestImage;
    private javax.swing.JLabel lblCompassWestText;
    private javax.swing.JLabel lblInput;
    private javax.swing.JLabel lblRoomImage;
    private javax.swing.JLabel lblRoomName;
    private javax.swing.JLayeredPane lypRoomImage;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlCompass;
    private javax.swing.JPanel pnlCompassNorthEast;
    private javax.swing.JPanel pnlCompassNorthWest;
    private javax.swing.JPanel pnlCompassSouthEast;
    private javax.swing.JPanel pnlCompassSouthWest;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlInOut;
    private javax.swing.JPanel pnlInput;
    private javax.swing.JScrollPane scrOutput;
    private javax.swing.JTextField txtInput;

    public javax.swing.JEditorPane getEdtOutput() {
        return txtPane;
    }

    public javax.swing.JLabel getLblCompassEastText() {
        return lblCompassEastText;
    }

    public javax.swing.JLabel getLblCompassNorthEastText() {
        return lblCompassNorthEastText;
    }

    public javax.swing.JLabel getLblCompassNorthText() {
        return lblCompassNorthText;
    }

    public javax.swing.JLabel getLblCompassNorthWestText() {
        return lblCompassNorthWestText;
    }

    public javax.swing.JLabel getLblCompassSouthEastText() {
        return lblCompassSouthEastText;
    }

    public javax.swing.JLabel getLblCompassSouthText() {
        return lblCompassSouthText;
    }

    public javax.swing.JLabel getLblCompassSouthWestText() {
        return lblCompassSouthWestText;
    }

    public javax.swing.JLabel getLblCompassWestText() {
        return lblCompassWestText;
    }

    public javax.swing.JLabel getLblActions() {
        return lblActions;
    }

    public javax.swing.JLabel getLblRoomImage() {
        return lblRoomImage;
    }

    public javax.swing.JLabel getLblRoomName() {
        return lblRoomName;
    }

    public javax.swing.JTextField getTxtInput() {
        return txtInput;
    }
}
