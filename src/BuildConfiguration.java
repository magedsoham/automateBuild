import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuildConfiguration {
    public static String REPO_FILE = "E:\\New folder";
    public static String FLAVORS_FILE = "E:\\New folder\\flavors.txt";
    public static String SERVER_URLS_FILE = "E:\\New folder\\serverUrls.txt";
    JPanel panel; // the panel is not visible in output
    JButton generateAPK;
    JButton uploadToStore;
    JCheckBox fromTagCheckBox;
    JComboBox tagsList,buildTypeCompoBox,urlCompoBox,flavorCompoBox, certificateStatusCompoBox;
    String tagName = null;
    JTextField newBuildNameField;
    public List<Object> tags = new ArrayList<Object>();
    public List<Object> buildTypes = new ArrayList<Object>();
    public List<Object> serverURLs = new ArrayList<Object>();
    public List<Object> flavors = new ArrayList<Object>();
    public List<Object> certificateStatus = new ArrayList<Object>();
    private boolean checkoutFromTag = false;
    private String certificateFileName = null;

    public BuildConfiguration() throws IOException, InterruptedException {
        JFrame frame = new JFrame("ِAutomate Your Build");
        //Creating the panel at bottom and adding components
        panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS) );
//        final GridBagConstraints gbc = new GridBagConstraints();
        JScrollPane spn = new JScrollPane( panel );

        tags.addAll(Utils.getTags());
        buildTypes.addAll(Utils.getBuildTypes());
        serverURLs.addAll(Utils.getListFromFile(new File(SERVER_URLS_FILE)));
        flavors.addAll(Utils.getListFromFile(new File(FLAVORS_FILE)));
        certificateStatus.addAll(Utils.getCertificateStatus());


        fromTagCheckBox = new JCheckBox("Generate from tag");
        panel.add(new JPanel( new FlowLayout(FlowLayout.LEFT)).add(fromTagCheckBox));



        final JLabel tagsLabel = new JLabel("Your selected Tag");
        tagsList = new JComboBox(tags.toArray());
        final JPanel tagsPanel = new JPanel( new FlowLayout(FlowLayout.LEFT) );
        tagsPanel.add(tagsLabel);
        tagsPanel.add(tagsList);
        tagsPanel.setVisible(false);
        panel.add(tagsPanel);

        JLabel buildTypesLabel = new JLabel("Your build type");
        buildTypeCompoBox = new JComboBox(buildTypes.toArray());
        JPanel buildTypesPanel = new JPanel( new FlowLayout(FlowLayout.LEFT) );
        buildTypesPanel.add(buildTypesLabel);
        buildTypesPanel.add(buildTypeCompoBox);
        panel.add(buildTypesPanel);


        JLabel urlLabel = new JLabel("Your server URL");
        urlCompoBox = new JComboBox(serverURLs.toArray());
        JPanel serverUrlPanel = new JPanel( new FlowLayout(FlowLayout.LEFT) );
        serverUrlPanel.add(urlLabel);
        serverUrlPanel.add(urlCompoBox);
        panel.add(serverUrlPanel);


        JLabel flavorLabel = new JLabel("Your flavor");
        flavorCompoBox = new JComboBox(flavors.toArray());
        JPanel flavorPanel = new JPanel( new FlowLayout(FlowLayout.LEFT) );
        flavorPanel.add(flavorLabel);
        flavorPanel.add(flavorCompoBox);
        panel.add(flavorPanel);

        JLabel certificateStatusLabel = new JLabel("Enable Certificate");
        certificateStatusCompoBox = new JComboBox(certificateStatus.toArray());
        JPanel certificateStatusPanel = new JPanel( new FlowLayout(FlowLayout.LEFT) );
        certificateStatusPanel.add(certificateStatusLabel);
        certificateStatusPanel.add(certificateStatusCompoBox);
        panel.add(certificateStatusPanel);


        JButton chooseCertificateBtn = new JButton("Choose Certificate Folder");
        final JLabel certificateLabel = new JLabel("Certificate");
        final JPanel certificatePanel = new JPanel( new FlowLayout(FlowLayout.LEFT) );
        certificatePanel.add(chooseCertificateBtn);
        certificatePanel.add(certificateLabel);
        certificatePanel.setVisible(false);
        panel.add(certificatePanel);


        newBuildNameField = new JTextField("Build name");
        panel.add(new JPanel( new FlowLayout(FlowLayout.LEFT) ).add(newBuildNameField));




        generateAPK = new JButton("Generate build");
        uploadToStore = new JButton("Upload");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(generateAPK);
        buttonPanel.add(uploadToStore);
        panel.add(buttonPanel);


        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frame.getPreferredSize());
        frame.getContentPane().add(panel); // Adds Button to content pane of frame
        frame.pack();
        frame.setVisible(true);



        fromTagCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(fromTagCheckBox.isSelected())
                {
                    tagsPanel.setVisible(true);
                    checkoutFromTag = true;
                }
                else
                {
                    tagsPanel.setVisible(false);
                    checkoutFromTag = false;
                }
            }
        });

        certificateStatusCompoBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(certificateStatusCompoBox.getSelectedItem().toString().equals("false"))
                {
                    certificatePanel.setVisible(false);
                }
                else
                {
                    certificatePanel.setVisible(true);
                }
            }
        });
        tagsList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tags != null && !tags.isEmpty()){
                    tagName = (String) tagsList.getSelectedItem();
                }
            }
        });
        chooseCertificateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                certificateFileName = Utils.openChooserAndGetPath(new JFileChooser(), "Certificate File");
                certificateLabel.setText(certificateFileName);
            }
        });

        generateAPK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent p) {
                try {
                    if(checkoutFromTag) {
                        Utils.checkoutTag(tagName, REPO_FILE);
                    }
                    Utils.getAPK("assemble"+flavorCompoBox.getSelectedItem().toString()
                                    +buildTypeCompoBox.getSelectedItem().toString()
                            , urlCompoBox.getSelectedItem().toString()
                            , certificateStatusCompoBox.getSelectedItem().toString()
                            , certificateFileName
                            , REPO_FILE);
                    JOptionPane.showMessageDialog(new JFrame(), "Done Generate");

                } catch (IOException e) {
                    System.out.println("exception happened - here's what I know: ");
                    e.printStackTrace();
                    System.exit(-1);
                } catch (InterruptedException e) {
                    System.out.println("exception happened - here's what I know: ");
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        });
        uploadToStore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent p) {
                try {
                    Utils.uploadToStore(REPO_FILE
                                    +"\\myFawryApp\\build\\outputs\\apk\\"
                                    +flavorCompoBox.getSelectedItem().toString()
                                    +"\\"
                                    +buildTypeCompoBox.getSelectedItem().toString()
                            , flavorCompoBox.getSelectedItem().toString()
                                    +"-"+buildTypeCompoBox.getSelectedItem().toString()+".apk"
                            , "\\\\appstore\\htdocs\\FawryWallet\\Android\\wallet2\\"
                                    +flavorCompoBox.getSelectedItem().toString()
                            , newBuildNameField.getText()+".apk");

                    JOptionPane.showMessageDialog(new JFrame(), "Done Upload");

                } catch (IOException e) {
                    System.out.println("exception happened - here's what I know: ");
                    e.printStackTrace();
                    System.exit(-1);
                } catch (InterruptedException e) {
                    System.out.println("exception happened - here's what I know: ");
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        });
    }
}
