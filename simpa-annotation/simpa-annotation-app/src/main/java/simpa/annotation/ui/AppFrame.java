package simpa.annotation.ui;

import com.google.inject.Inject;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.net.URL;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginService;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpa.annotation.UserDefinedButtonInfo;
import simpa.annotation.UserLookupService;
import simpa.annotation.AnnotationGeneratorService;
import simpa.annotation.AnnotationLookupService;
import simpa.annotation.AnnotationPersistenceService;
import simpa.annotation.ImageCaptureService;
import simpa.annotation.TileAnnotation;
import simpa.annotation.VideoConnectionInformation;
import simpa.annotation.VideoConnectionStatus;
import simpa.annotation.VideoControlService;
import simpa.annotation.VideoTile;
import simpa.core.DataIngestorService;
import simpa.core.MosaicAssembly;

public class AppFrame extends JFrame {

    private JButton openMosaicButton;
    
    private final Logger log = LoggerFactory.getLogger(AppFrame.class);
    
    /**
     * controller is used to move all code that doesn't directly pertain to UI operations (layout and drawing)
     * out of the UI class. It also contains references to the relevant services so that they can be
     * passed on to any other classes that might need them.
     */
    private final AppFrameController controller;
    

    private ConceptWidget conceptWidget;
    private JButton openVideoConnectionButton;
    private JButton loadTileFileButton;
    private JButton loginButton;
    private JMenuItem loginMenuItem;
    private JTabbedPane tabbedPane;
    private JScrollPane imageScrollPane;
    private JScrollPane listScrollPane;
    private AnnotatedTileDisplayPanel videoTileDisplayPanel;
    private JList tileList;
    private JPopupMenu tileListPopupMenu;
    private JSplitPane eastWestSplitPane;
    private JSplitPane northSouthSplitPane;
    private JButton undoButton;
    private JButton redoButton;
    private JToolBar toolBar;
    private JMenuItem newItemMenuItem;
    private JMenu editMenu;
    private JMenuItem openMosaicMenuItem;
    private JMenuItem loadFileMenuItem;
    private JMenu fileMenu;
    private JMenuBar menuBar;
    private Action loginAction;
    private Action loadTileFileAction;
    private ChangeListener selectedButtonChangeListener;
    private VideoTileCellRenderer videoTileCellRenderer;
    private DefaultListModel tileListModel;
    private JButton preferencesButton;
    private JMenuItem preferencesMenuItem;
    private Action preferencesAction;
    private JToggleButton showOverlayToggleButton;

    /**
     * Create the frame. The services can be injected using Google Guice. See
     * {@link AppModule} for an example
     * 
     * @param annotationGeneratorService An AnnotationGeneratorService to be used
     * @param annotationLookupService An AnnotationLookupService to be used
     * @param annotationUpdateService An AnnotationUpdateService to be used
     * @param imageCaptureService  An ImageCaptureService to be used.
     * @param userLookupService The UserLookupService to be used to resolve
     *      logins and user information.
     * @param videoControlService  The VideoControlService used to control the 
     *      the video source.
     */
    @Inject
    public AppFrame(AnnotationGeneratorService annotationGeneratorService,
            AnnotationLookupService annotationLookupService,
            AnnotationPersistenceService annotationUpdateService,
            DataIngestorService dataIngestorService,
            ImageCaptureService imageCaptureService,
            UserLookupService userLookupService,
            VideoControlService videoControlService) {

        super();
        setBounds(100, 100, 500, 375);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        controller = new AppFrameController(this, annotationGeneratorService,
                annotationLookupService, annotationUpdateService,
                dataIngestorService, imageCaptureService, userLookupService,
                videoControlService);
        try {
            initialize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        // Make sure we save the last tile we annotated to the database
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                log.debug("Saving last VideoTile to persistent storage during JVM shutdown");
                VideoTile videoTile = (VideoTile) getTileList().getSelectedValue();
                if (videoTile != null) {
                    controller.persistTile(videoTile);
                }
            }
        }));
    }

    

    private Action getLoginAction() {
        if (loginAction == null) {
            loginAction = new LoginAction();
        }
        return loginAction;
    }

    private Action getLoadTileFileAction() {
        if (loadTileFileAction == null) {
            loadTileFileAction = new LoadTileFileAction();
        }
        return loadTileFileAction;
    }

    private void initialize() throws Exception {
        getContentPane().setLayout(new BorderLayout());
        setJMenuBar(getMainJMenuBar());
        getContentPane().add(getToolBar(), BorderLayout.NORTH);
        getContentPane().add(getNorthSouthSplitPane(), BorderLayout.CENTER);
    }

    /**
     * @return
     */
    protected JMenuBar getMainJMenuBar() {
        if (menuBar == null) {
            menuBar = new JMenuBar();
            menuBar.add(getFileMenu());
            menuBar.add(getEditMenu());
        }
        return menuBar;
    }

    /**
     * @return
     */
    protected JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("File");
            fileMenu.add(getLoginMenuItem());
            fileMenu.add(getLoadFileMenuItem());
            fileMenu.add(getOpenMosaicMenuItem());
            fileMenu.add(getPreferencesMenuItem());
        }
        return fileMenu;
    }

    /**
     * @return
     */
    protected JMenuItem getLoadFileMenuItem() {
        if (loadFileMenuItem == null) {
            loadFileMenuItem = new JMenuItem();
            loadFileMenuItem.setAction(getLoadTileFileAction());
            loadFileMenuItem.setText("Load File");
        }
        return loadFileMenuItem;
    }

    /**
     * @return
     */
    protected JMenuItem getOpenMosaicMenuItem() {
        if (openMosaicMenuItem == null) {
            openMosaicMenuItem = new JMenuItem();
            openMosaicMenuItem.setText("Open Existing Mosiac");
        }
        return openMosaicMenuItem;
    }

    /**
     * @return
     */
    protected JMenu getEditMenu() {
        if (editMenu == null) {
            editMenu = new JMenu();
            editMenu.setText("Edit");
            editMenu.add(getNewItemMenuItem());
        }
        return editMenu;
    }

    /**
     * @return
     */
    protected JMenuItem getNewItemMenuItem() {
        if (newItemMenuItem == null) {
            newItemMenuItem = new JMenuItem();
            newItemMenuItem.setText("Do something");
        }
        return newItemMenuItem;
    }

    /**
     * @return
     */
    protected JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(getLoginButton());
            toolBar.add(getOpenMosaicButton());
            toolBar.add(getOpenVideoConnectionButton());
            toolBar.addSeparator();
            toolBar.add(getShowOverlayToggleButton());
            toolBar.addSeparator();
            toolBar.add(getLoadTileFileButton());
            toolBar.addSeparator();
            toolBar.add(getUndoButton());
            toolBar.add(getRedoButton());
            toolBar.addSeparator();
            toolBar.add(getPreferencesButton());
            toolBar.addSeparator();
            toolBar.add(getConceptWidget());
        }
        return toolBar;
    }

    /**
     * 
     * @return
     */
    protected JToggleButton getShowOverlayToggleButton() {
        if (showOverlayToggleButton == null) {
            showOverlayToggleButton = new JToggleButton(new ImageIcon(getClass().getResource("/images/24/signpost_add.png")));
            showOverlayToggleButton.setSelectedIcon(new ImageIcon(getClass().getResource("/images/24/signpost_delete.png")));
            showOverlayToggleButton.setSelected(true);
            showOverlayToggleButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getVideoTileDisplayPanel().setShowing(showOverlayToggleButton.isSelected());
                }
            });
            getVideoTileDisplayPanel().setShowing(showOverlayToggleButton.isSelected());
        }
        return showOverlayToggleButton;
    }

    /**
     * @return
     */
    protected JButton getRedoButton() {
        if (redoButton == null) {
            redoButton = new JButton();
            redoButton.setText("");
            redoButton.setToolTipText("Redo");
            redoButton.setEnabled(false);
            redoButton.setIcon(new ImageIcon(getClass().getResource("/images/24/redo.png")));
        }
        return redoButton;
    }

    /**
     * @return
     */
    protected JButton getUndoButton() {
        if (undoButton == null) {
            undoButton = new JButton();
            undoButton.setText("");
            undoButton.setToolTipText("Undo");
            undoButton.setEnabled(false);
            undoButton.setIcon(new ImageIcon(getClass().getResource("/images/24/undo.png")));
        }
        return undoButton;
    }

    /**
     * @return
     */
    protected JSplitPane getEastWestSplitPane() {
        if (eastWestSplitPane == null) {
            eastWestSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            eastWestSplitPane.setLeftComponent(getListScrollPane());
            eastWestSplitPane.setRightComponent(getImageScrollPane());
        }
        return eastWestSplitPane;
    }

    /**
     * @return
     */
    protected JSplitPane getNorthSouthSplitPane() {
        if (northSouthSplitPane == null) {
            northSouthSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            northSouthSplitPane.setTopComponent(getEastWestSplitPane());
            northSouthSplitPane.setBottomComponent(getTabbedPane());
        }
        return northSouthSplitPane;
    }

    /**
     * @return
     */
    protected JList getTileList() {
        if (tileList == null) {
            tileList = new JList(getTileListModel());
            tileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tileList.setCellRenderer(getVideoTileCellRenderer());
            tileList.addListSelectionListener(new TileListSelectionListener());
            tileList.add(getTileListPopupMenu());
        }
        return tileList;
    }

    public JPopupMenu getTileListPopupMenu() {
        if (tileListPopupMenu == null) {
            tileListPopupMenu = new JPopupMenu();
            final JMenuItem menuItem = new JMenuItem("PLACEHOLDER");
            menuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });
            tileListPopupMenu.add(menuItem);
        }
        return tileListPopupMenu;
    }

    protected DefaultListModel getTileListModel() {
        if (tileListModel == null) {
            tileListModel = new DefaultListModel();
        }
        return tileListModel;
    }

    protected VideoTileCellRenderer getVideoTileCellRenderer() {
        if (videoTileCellRenderer == null) {
            videoTileCellRenderer = new VideoTileCellRenderer();
        }
        return videoTileCellRenderer;
    }

    /**
     * @return
     */
    protected AnnotatedTileDisplayPanel getVideoTileDisplayPanel() {
        if (videoTileDisplayPanel == null) {
            videoTileDisplayPanel = new FullAnnotatedTileDisplayPanel(controller.getAnnotationGeneratorService(),
                    controller.getAnnotationLookupService(), controller.getAnnotationPersistenceService());

            /*
             * This code block deals with what to do when a new annotation is 
             * added to the image.
             */
            videoTileDisplayPanel.addNewTileAnnotationListener(new MyNewTileAnnotationListener());

            // Use this is we need to change the font displayed when a tile is selected
            //videoTileDisplayPanel.setFont(font);

            
            
            // Listen for when the mosaicAssembly is changed. 
            controller.getPropertyChangeSupport().addPropertyChangeListener(AppFrameController.MOSAIC_ASSEMBLY_PROPERTY, 
                    new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    videoTileDisplayPanel.setMosaicAssembly((MosaicAssembly<VideoTile>) evt.getNewValue());
                }
            });

            InputMap inputMap = videoTileDisplayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = videoTileDisplayPanel.getActionMap();

            /*
             * Setup the 'delete' key to delete the selected annotations from the view 
             * as well as from the persitent data store.
             */
            String actionKey = "deleteSelectedAnnotations";
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), actionKey);
            actionMap.put(actionKey, new AbstractAction(actionKey) {

                public void actionPerformed(ActionEvent e) {
                    VideoTile videoTile = videoTileDisplayPanel.getVideoTile();
                    Collection<TileAnnotation> deadAnnotations = videoTileDisplayPanel.getSelectedAnnotations();
                    videoTileDisplayPanel.getAnnotations().removeAll(deadAnnotations);
                    controller.deleteTileAnnotations(videoTile, deadAnnotations);
                    deadAnnotations.clear();
                    videoTileDisplayPanel.repaint();
                }
            });

            /*
             * Setup a select all annotation key
             */
            actionKey = "selectAllAnnotations";
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), actionKey);
            actionMap.put(actionKey, new AbstractAction(actionKey) {

                public void actionPerformed(ActionEvent e) {
                    Collection<TileAnnotation> allAnnotations = videoTileDisplayPanel.getAnnotations();
                    Collection<TileAnnotation> selectedAnnotations = videoTileDisplayPanel.getSelectedAnnotations();
                    selectedAnnotations.clear();
                    selectedAnnotations.addAll(allAnnotations);
                    videoTileDisplayPanel.repaint();
                }
            });

        }
        return videoTileDisplayPanel;
    }

    /**
     * @return
     */
    protected JScrollPane getListScrollPane() {
        if (listScrollPane == null) {
            listScrollPane = new JScrollPane();
            listScrollPane.setViewportView(getTileList());
        }
        return listScrollPane;
    }

    /**
     * @return
     */
    protected JScrollPane getImageScrollPane() {
        if (imageScrollPane == null) {
            imageScrollPane = new JScrollPane();
            imageScrollPane.setViewportView(getVideoTileDisplayPanel());
        }
        return imageScrollPane;
    }

    /**
     * @return
     */
    protected JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
        }
        return tabbedPane;
    }

    /**
     * @return
     */
    protected JMenuItem getLoginMenuItem() {
        if (loginMenuItem == null) {
            loginMenuItem = new JMenuItem();
            loginMenuItem.setAction(getLoginAction());
            loginMenuItem.setText("Login");
        }
        return loginMenuItem;
    }

    /**
     * @return
     */
    protected JButton getLoginButton() {
        if (loginButton == null) {
            loginButton = new JButton();
            loginButton.setAction(getLoginAction());
            loginButton.setText("");
            final ImageIcon loggedOutIcon = new ImageIcon(getClass().getResource("/images/24/lock_unknown.png"));
            final ImageIcon loggedInIcon = new ImageIcon(getClass().getResource("/images/24/lock_open_ok.png"));
            loginButton.setIcon(loggedOutIcon);
            loginButton.setToolTipText("Login");

            /*
             * Toogle icon if the user is logged in.
             */
            controller.getPropertyChangeSupport().addPropertyChangeListener(AppFrameController.LOGIN_CREDENTIAL_PROPERTY, 
                    new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    LoginCredential aLoginCredential = (LoginCredential) evt.getNewValue();
                    if (log.isInfoEnabled()) {
                        log.info("Logging in as '" + aLoginCredential + "'");
                    }
                    
                    ImageIcon icon = (aLoginCredential == null) ? loggedOutIcon : loggedInIcon;
                    loginButton.setIcon(icon);
                    String msg = (aLoginCredential == null) ? "Login" : "Logged in as '" + aLoginCredential.getLogin() + "'";
                    loginButton.setToolTipText(msg);
                }
            });
        }
        return loginButton;
    }
    
    /**
     * @return
     */
    protected JMenuItem getPreferencesMenuItem() {
        if (preferencesMenuItem == null) {
            preferencesMenuItem = new JMenuItem();
            preferencesMenuItem.setAction(getPreferencesAction());
            preferencesMenuItem.setText("Preferences");
        }
        return preferencesMenuItem;
    }

    /**
     * @return
     */
    protected JButton getPreferencesButton() {
        if (preferencesButton == null) {
            preferencesButton = new JButton();
            preferencesButton.setAction(getPreferencesAction());
            preferencesButton.setText("");
            final ImageIcon preferencesIcon = new ImageIcon(getClass().getResource("/images/24/data_preferences.png"));
            preferencesButton.setIcon(preferencesIcon);
            preferencesButton.setToolTipText("Preferences");
        }
        return preferencesButton;
    }
    
    protected Action getPreferencesAction() {
        if (preferencesAction == null) {
            preferencesAction = new ShowPreferencesFrameAction();
            preferencesAction.setEnabled(false);
            controller.getPropertyChangeSupport().addPropertyChangeListener(
                    AppFrameController.LOGIN_CREDENTIAL_PROPERTY, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    LoginCredential aLoginCredential = (LoginCredential) evt.getNewValue();
                    preferencesAction.setEnabled(aLoginCredential != null);
                }
            });
        }
        return preferencesAction;
    }

    /**
     * @return
     */
    protected JButton getLoadTileFileButton() {
        if (loadTileFileButton == null) {
            loadTileFileButton = new JButton();
            loadTileFileButton.setAction(getLoadTileFileAction());
            loadTileFileButton.setText("");
            loadTileFileButton.setIcon(new ImageIcon(getClass().getResource("/images/24/document_add.png")));
            loadTileFileButton.setToolTipText("Load tile data from file");
        }
        return loadTileFileButton;
    }

    protected ConceptWidget getConceptWidget() {
        if (conceptWidget == null) {
            conceptWidget = new ConceptWidget(controller.getAnnotationLookupService().findAllConcepts());
            conceptWidget.getToggleButton().addChangeListener(getSelectedConceptChangeListener());
        }
        return conceptWidget;
    }

    /**
     * @return
     */
    protected JButton getOpenVideoConnectionButton() {
        if (openVideoConnectionButton == null) {
            openVideoConnectionButton = new JButton();
            openVideoConnectionButton.setText("");

            final ImageIcon connectedIcon = new ImageIcon(getClass().getResource("/images/24/plug_ok.png"));
            final ImageIcon notConnectedIcon = new ImageIcon(getClass().getResource("/images/24/plug_unknown.png"));
            final String notConnectedMessage = "Connect to video control";
            openVideoConnectionButton.setIcon(notConnectedIcon);
            openVideoConnectionButton.setToolTipText(notConnectedMessage);

            /*
             * Toogle icon if the connected to a video source.
             */
            controller.getPropertyChangeSupport().addPropertyChangeListener("videoConnectionInformation", new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    VideoConnectionInformation vci = (VideoConnectionInformation) evt.getNewValue();
                    ImageIcon icon = (vci != null && vci.getVideoConnectionStatus().equals(VideoConnectionStatus.CONNECTED)) ? connectedIcon : notConnectedIcon;
                    openVideoConnectionButton.setIcon(icon);
                    String msg = (vci == null) ? notConnectedMessage : "Connected to video controllor: '" +
                            vci.getVideoConnectionID() + "'";
                    openVideoConnectionButton.setToolTipText(msg);
                }
            });

            openVideoConnectionButton.addActionListener(new OpenVideoConnectionAction());


        }
        return openVideoConnectionButton;
    }

    protected ChangeListener getSelectedConceptChangeListener() {
        if (selectedButtonChangeListener == null) {
            selectedButtonChangeListener = new SelectedButtonChangeListener();
        }
        return selectedButtonChangeListener;
    }

    /**
     * @return
     */
    protected JButton getOpenMosaicButton() {
        if (openMosaicButton == null) {
            openMosaicButton = new JButton();
            final ImageIcon loadedIcon = new ImageIcon(getClass().getResource("/images/24/environment_ok.png"));
            final ImageIcon notLoadedIcon = new ImageIcon(getClass().getResource("/images/24/environment_unknown.png"));
            final String notLoadedMessage = "Open an existing mosaic from the data store";
            openMosaicButton.setIcon(notLoadedIcon);
            openMosaicButton.setToolTipText(notLoadedMessage);
            openMosaicButton.addActionListener(new OpenMosaicAssemblyAction());

            /*
             * Toogle icon if the user is logged in.
             */
            controller.getPropertyChangeSupport().addPropertyChangeListener("mosaicAssembly", new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    MosaicAssembly aMosaicAssembly = (MosaicAssembly) evt.getNewValue();
                    ImageIcon icon = (aMosaicAssembly == null) ? notLoadedIcon : loadedIcon;
                    openMosaicButton.setIcon(icon);
                    String msg = (aMosaicAssembly == null) ? notLoadedMessage : "Mosaic for '" +
                            aMosaicAssembly.getCameraIdentifier() + "-" + aMosaicAssembly.getSessionIdentifier() + "'";
                    openMosaicButton.setToolTipText(msg);
                }
            });

        }
        return openMosaicButton;
    }

    protected void configureTabbedPanel(Collection<UserDefinedButtonInfo> buttonInfos) {
        JTabbedPane pane = getTabbedPane();
        pane.removeAll();
        Set<String> tabName = UserDefinedButtonInfo.extractTabNames(buttonInfos);
        for (String tab : tabName) {
            JPanel p = new JPanel();
            pane.addTab(tab, p);
            List<UserDefinedButtonInfo> bis = UserDefinedButtonInfo.extractButtons(buttonInfos, tab);
            for (UserDefinedButtonInfo bi : bis) {
                // Configure action listener to only allow one button to be selected at a time
                JToggleButton button = new UserDefinedToggleButton(bi);
                button.addChangeListener(getSelectedConceptChangeListener());
                p.add(button);
            }
        }
    }
    
    public AppFrameController getController() {
        return controller;
    }

    /**
     * UI Action for handling the login details: Shows a dialog, validates
     * login, then passes the login info to the controllor to finish seting up
     * the UI
     */
    private class LoginAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            // show login dialog
            final JXLoginPane panel = new JXLoginPane(new LoginService() {

                public boolean authenticate(String name, char[] password,
                        String server) throws Exception {

                    return controller.isValidLogin(name, password);
                }
            });

            JXLoginPane.Status status = JXLoginPane.showLoginDialog(AppFrame.this, panel);

            /*
             * the controllor does all the heavy login lifting
             */
            if (status.equals(JXLoginPane.Status.SUCCEEDED)) {
                WaitIndicator waitIndicator = new SpinningDialWaitIndicator(AppFrame.this);
                controller.login(panel.getUserName(), panel.getPassword());
                waitIndicator.dispose();
            }

        }
    }

    /**
     * UI Action for opening a new Tile description file
     */
    private class LoadTileFileAction extends AbstractAction {

        private final OpenTileFileDialog dialog = new OpenTileFileDialog();

        LoadTileFileAction() {
            dialog.setOkActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    URL url = dialog.getUrl();
                    String sessionID = dialog.getSessionIdentifier();
                    String cameraID = dialog.getCameraIdentifier();

                    // Load tile data
                    WaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(AppFrame.this,
                            "Loading tile data from file", GlobalUIParameters.WAITINDICATOR_FONT);
                    controller.loadTileFile(cameraID, sessionID, url);
                    waitIndicator.dispose();


                    waitIndicator = new LabeledSpinningDialWaitIndicator(AppFrame.this,
                            "Persisting tile data into data store", GlobalUIParameters.WAITINDICATOR_FONT);
                    controller.persistMosaicAssembly(controller.getMosaicAssembly());
                    waitIndicator.dispose();
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            dialog.setVisible(true);
        }
    }

    /**
     * Allows only 1 button to be selected at a time. On button selection
     * it sets the selectedConceptName field
     */
    private class SelectedButtonChangeListener implements ChangeListener {

        private JToggleButton selectedButton;

        public void stateChanged(ChangeEvent e) {

            JToggleButton btn = (JToggleButton) e.getSource();

            if (selectedButton != null && !btn.equals(selectedButton)) {
                selectedButton.setSelected(false);
            }

            if (btn.isSelected()) {
                selectedButton = btn;
                controller.setSelectedConceptName(btn.getText());
            } else {
                selectedButton = null;
                controller.setSelectedConceptName(null);
            }
        }
    }

    /**
     * Updates the UI and data when a VideoTile is selected in the TileList
     */
    private class TileListSelectionListener implements ListSelectionListener {

        /*
         * Store the reference to the last video tile. Otherwise,
         * if an error occurs while fetching an image, the UI
         * will keep retrying, locking up the EDT.
         */
        private VideoTile videoTile;

        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting()) {

                VideoTile selectedVideoTile = (VideoTile) getTileList().getSelectedValue();
                
                /*
                 * Save/Update the last tile to the persistant store
                 */
                if (videoTile != null  && selectedVideoTile != videoTile) {
                    
                    /*
                     * We are not uploading images on tile selection. Instead, 
                     * the image is saved directly to a remote directory on the
                     * web server in the controller.captureImage() method.
                     */
                    
                    /*
                     * Save the video tile to the data store.
                     */
                    try {
                        controller.persistTile(videoTile);
                    }
                    catch (Exception ex) {
                        // TODO show error dialog
                        log.error("Failed to save/update " + videoTile, ex);
                        JOptionPane.showMessageDialog(AppFrame.this, "Failed to save/update " + videoTile, 
                                "SIMPA - Data Storage Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                
                /*
                 *  Capture the framegrab if needed
                 */
                if (selectedVideoTile != null && selectedVideoTile.getUrl() == null && selectedVideoTile != videoTile) {

                    WaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(AppFrame.this, "Capturing image", new Font("Serif", Font.BOLD, 24));
                    try {
                        controller.captureImage(selectedVideoTile);
                    } catch (Exception ex) {
                        log.warn("An error occurred while trying to fetch " +
                                "image for " + selectedVideoTile, ex);
                        // TODO if we can't capture an image the UI hangs need to generate a default image and save it to .simpa and use it in the UI
                    }
                    waitIndicator.dispose();
                }
                videoTile = selectedVideoTile;
                getVideoTileDisplayPanel().setVideoTile(videoTile);
            }
        }
    }

    /**
     * This class handles the actions that occur when a new tile is added to the TileDisplayPanel
     */
    private class MyNewTileAnnotationListener implements NewTileAnnotationListener {

        public void process(NewTileAnnotationEvent e) {
            TileAnnotation tileAnnotation = e.getTileAnnotation();
            if (controller.getSelectedConceptName() != null) {
                tileAnnotation.setConceptName(controller.getSelectedConceptName());
                tileAnnotation.setDate(new Date());
                tileAnnotation.setAnnotator(controller.getLoginCredential().getLogin());
                // Add annotation to selected tile
                VideoTile videoTile = (VideoTile) getTileList().getSelectedValue();
                videoTile.getAnnotations().add(tileAnnotation);

            } else {
                // No concept was selected. Remove the new tileAnnotation from the UI
                videoTileDisplayPanel.getAnnotations().remove(tileAnnotation);
                videoTileDisplayPanel.repaint();

                // Display dialog warning the user to select a concept first
                JOptionPane.showMessageDialog(AppFrame.this,
                        "You must select a concept name before you are allowed to create an annotation",
                        "SIMPA says select a concept name", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    
    /**
     * Action for connecting to video controls. Basically it just shows the connection
     * dialog. However, It also contains a HACK for updating the video connection state
     */
    private class OpenVideoConnectionAction implements ActionListener {

        // TODO Need to set a parent frame so that we can orient this modal component to it.
        final JDialog dialog = controller.getVideoControlService().getConnectionDialog();
        boolean firstShowing = true;
        
        /**
         * HACK!! the VideoControlService is stateless but we need to monitor the 
         * connection state. As a hack whenever the dialog is shown/hidden we'll
         * grab the {@link VideoConnectionInformation} from the service
         */
        final PropertyChangeListener dialogListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                controller.setVideoConnectionInformation(controller.getVideoControlService().getVideoConnectionInformation());
            }
        };

        OpenVideoConnectionAction() {
            dialog.setModal(true);
            dialog.addPropertyChangeListener(dialogListener);
        }
        
        public void actionPerformed(ActionEvent e) {
            if (firstShowing) {
                // Center dialog
                dialog.setLocationRelativeTo(AppFrame.this);
                firstShowing = false;
            }
            dialog.setVisible(true);
        }
    }
    
    private class OpenMosaicAssemblyAction implements ActionListener {

        private final OpenMosaicAssemblyDialog dialog = new OpenMosaicAssemblyDialog(AppFrame.this);

        public OpenMosaicAssemblyAction() {
            dialog.setOkActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final String sessionID = dialog.getSessionIdentifier();
                    final String cameraID = dialog.getCameraIdentifier();
                    
                    // Load tile data
                    WaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(AppFrame.this,
                            "Loading mosaic from data store", GlobalUIParameters.WAITINDICATOR_FONT);
                    try {
                        controller.openMosaicAssembly(cameraID, sessionID);
                        waitIndicator.dispose();
                    }
                    catch (Exception ex) {
                        waitIndicator.dispose();
                        String msg = "Failed to open mosaic for '" + cameraID + "-" + sessionID + "'";
                        log.error(msg, ex);
                        JOptionPane.showMessageDialog(AppFrame.this, msg, "SIMPA - Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                    
                    
                }
            });
            dialog.setModal(true);
        }
        
        public void actionPerformed(ActionEvent e) {
            dialog.setVisible(true);
        }
        
    }
    
    /**
     * Action that shows the user preferences control panel
     */
    private class ShowPreferencesFrameAction extends AbstractAction {
        
        private final PreferencesFrame preferencesFrame = new PreferencesFrame(getController().getUserLookupService());

        public ShowPreferencesFrameAction() {
            preferencesFrame.pack();
            
            /*
             * Pass on changes of the upate controller to other components.
             */
            final AppFrameController controller = getController();
            final PropertyChangeSupport pcs = controller.getPropertyChangeSupport();
            pcs.addPropertyChangeListener(AppFrameController.LOGIN_CREDENTIAL_PROPERTY, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    preferencesFrame.getController().setLoginCredential((LoginCredential) evt.getNewValue());
                }
            });
            
        }
        
        public void actionPerformed(ActionEvent e) {
            preferencesFrame.setVisible(true);
        }
        
    }
}
