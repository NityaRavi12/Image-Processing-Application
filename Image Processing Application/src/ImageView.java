import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * ImageView provides a graphical user interface for the image processing application.
 */
public class ImageView extends JFrame implements ImageViewInterface {

  private final JTextArea feedbackArea;
  private final JLabel imageLabel;
  private final JLabel histogramImageLabel;
  private boolean isImageSaved;
  private String imagePath;
  private String commandLine;
  private ImageController controller;
  private String currentImageName;
  private Map<String, Function<String, String>> commandMap;

  /**
   * Constructs the ImageView and initializes the GUI components.
   */
  public ImageView() {
    // Command map setup
    setupCommandMap();
    isImageSaved = false;

    // JFrame setup
    setTitle("Image Processing App");
    setSize(800, 800);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Center window on screen
    setLayout(new BorderLayout());

    // Initializing the histogram panel
    JPanel histogramPanel = new JPanel();
    histogramImageLabel = new JLabel();
    histogramPanel.setLayout(new BorderLayout());
    histogramPanel.setPreferredSize(new Dimension(256, 256));
    histogramPanel.add(histogramImageLabel, BorderLayout.CENTER);

    // Create a panel to hold both the histogram and the button panel
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BorderLayout());
    rightPanel.add(histogramPanel, BorderLayout.NORTH);
    rightPanel.add(createButtonPanel(), BorderLayout.SOUTH);

    // Image Display Area
    JPanel imagePanel = new JPanel();
    imagePanel.setLayout(new BorderLayout());
    imageLabel = new JLabel();
    imagePanel.add(imageLabel, BorderLayout.CENTER);
    JScrollPane imageScrollPane = new JScrollPane(imagePanel);
    add(imageScrollPane, BorderLayout.CENTER);

    // Add right panel containing histogram and buttons to the right section of the JFrame
    add(rightPanel, BorderLayout.EAST);

    // Feedback Area
    feedbackArea = new JTextArea(5, 40);
    feedbackArea.setEditable(false);
    feedbackArea.setBackground(Color.LIGHT_GRAY);
    JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
    add(feedbackScroll, BorderLayout.SOUTH);
  }

  private static JFileChooser getjFileChooser() {
    JFileChooser saveDialog = new JFileChooser();
    saveDialog.setDialogTitle("Save Image");

    // Add file format filters
    FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Images (*.png)", "png");
    FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPG Images (*.jpg)", "jpg");
    FileNameExtensionFilter jpegFilter = new FileNameExtensionFilter("JPEG Images (*.jpeg)",
        "jpeg");
    FileNameExtensionFilter ppmFilter = new FileNameExtensionFilter("PPM Images (*.ppm)", "ppm");

    saveDialog.addChoosableFileFilter(pngFilter);
    saveDialog.addChoosableFileFilter(jpgFilter);
    saveDialog.addChoosableFileFilter(jpegFilter);
    saveDialog.addChoosableFileFilter(ppmFilter);
    saveDialog.setAcceptAllFileFilterUsed(false);
    saveDialog.setFileFilter(pngFilter);
    return saveDialog;
  }


  /**
   * Gets the feedback area for displaying messages.
   *
   * @return The feedback JTextArea.
   */
  public JTextArea getFeedbackArea() {
    return feedbackArea;
  }


  /**
   * Sets the current image name.
   *
   * @param currentImageName the name of the currently loaded image
   */
  public void setCurrentImageName(String currentImageName) {
    this.currentImageName = currentImageName;
  }

  /**
   * Displays a message in the feedback area.
   *
   * @param message the message to show
   */
  public void showMessage(String message) {
    feedbackArea.setText(message);
  }

  /**
   * Displays an error message in the feedback area.
   *
   * @param errorMessage the error message to show
   */
  public void showError(String errorMessage) {
    feedbackArea.setText("Error: " + errorMessage);
  }

  /**
   * Sets the controller for this view.
   *
   * @param controller the ImageController managing this view
   */
  public void setController(ImageController controller) {
    this.controller = controller;
  }

  private void setupCommandMap() {
    commandMap = new HashMap<>();
    commandMap.put("load", this::handleLoad);
    commandMap.put("save", this::handleSave);
    commandMap.put("red-component", this::handleDestNameOnly);
    commandMap.put("green-component", this::handleDestNameOnly);
    commandMap.put("blue-component", this::handleDestNameOnly);
    commandMap.put("horizontal-flip", this::handleDestNameOnly);
    commandMap.put("vertical-flip", this::handleDestNameOnly);
    commandMap.put("brighten", this::handleDestNameAndOneInteger);
    commandMap.put("darken", this::handleDestNameAndOneInteger);
    commandMap.put("blur", this::handleSplitView);
    commandMap.put("sharpening", this::handleSplitView);
    commandMap.put("sepia", this::handleSplitView);
    commandMap.put("color-correct", this::handleSplitView);
    commandMap.put("greyscale", this::handleSplitView);
    commandMap.put("levels-adjust", this::handleLevelsAdjust);
    commandMap.put("compress", this::handleDestNameAndOneInteger);
    commandMap.put("downscale", this::handleDownscaling);
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(0, 2));

    addButton(buttonPanel, "Load Image", "load");
    addButton(buttonPanel, "Save Image", "save");
    addButton(buttonPanel, "Red Component", "red-component");
    addButton(buttonPanel, "Green Component", "green-component");
    addButton(buttonPanel, "Blue Component", "blue-component");
    addButton(buttonPanel, "Greyscale", "greyscale");
    addButton(buttonPanel, "Flip Horizontal", "horizontal-flip");
    addButton(buttonPanel, "Flip Vertical", "vertical-flip");
    addButton(buttonPanel, "Brighten", "brighten");
    addButton(buttonPanel, "Darken", "darken");
    addButton(buttonPanel, "Blur", "blur");
    addButton(buttonPanel, "Sharpen", "sharpening");
    addButton(buttonPanel, "Sepia", "sepia");
    addButton(buttonPanel, "Compress Image", "compress");
    addButton(buttonPanel, "Color Correction", "color-correct");
    addButton(buttonPanel, "Levels Adjustment", "levels-adjust");
    addButton(buttonPanel, "Downscale", "downscale");

    return buttonPanel;
  }

  private void addButton(JPanel panel, String buttonText, String command) {
    JButton button = new JButton(buttonText);
    button.addActionListener(e -> executeCommand(command));
    panel.add(button);
  }

  private void executeCommand(String command) {
    if (!command.equals("save") && currentImageName != null) {
      if (!saveImage()) {
        return;
      }
    }
    commandLine = "";
    if (commandMap.containsKey(command)) {
      commandLine = commandMap.get(command).apply(command);
      if (commandLine != null) {
        showMessage(currentImageName + " Image Successfully loaded. Histogram Updated.");
        controller.executeCommand(commandLine);
        updateUI();
      }
    } else {
      showError("Unknown command.");
    }
  }

  private void updateUI() {
    CustomImage img = controller.getImage(currentImageName);
    BufferedImage bufferedImage = controller.transferToBufferedImage(img);
    imageLabel.setIcon(new ImageIcon(bufferedImage));

    String currentHistogramName = currentImageName + "his";
    controller.executeCommand("histogram " + currentImageName + " " + currentHistogramName);
    CustomImage histogram = controller.getImage(currentHistogramName);
    BufferedImage bufferedHistogramImage = controller.transferToBufferedImage(histogram);
    histogramImageLabel.setIcon(new ImageIcon(bufferedHistogramImage));
  }

  private String handleLoad(String s) {
    String commandLine = s + " ";

    JDialog dialog = new JDialog(this, "Load Image", true);
    dialog.setLayout(new GridLayout(0, 2));

    JButton chooseImageButton = new JButton("Choose an Image");
    dialog.add(chooseImageButton);

    JLabel filePathLabel = new JLabel("No file selected.");
    dialog.add(filePathLabel);

    JLabel nameLabel = new JLabel("Enter Image Name:");
    JTextField nameField = new JTextField(20);
    dialog.add(nameLabel);
    dialog.add(nameField);

    JButton loadButton = new JButton("Load");
    JButton cancelButton = new JButton("Cancel");
    dialog.add(loadButton);
    dialog.add(cancelButton);

    chooseImageButton.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Select an Image");

      FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files",
          "ppm", "png", "jpeg", "jpg");
      fileChooser.setFileFilter(filter);

      int result = fileChooser.showOpenDialog(dialog);
      if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        imagePath = selectedFile.getAbsolutePath();
        filePathLabel.setText("Selected File: " + imagePath);
      } else {
        filePathLabel.setText("No file selected.");
      }
    });

    loadButton.addActionListener(e -> {
      currentImageName = nameField.getText().trim();
      if (imagePath.isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "Please choose an image file.", "Error",
            JOptionPane.ERROR_MESSAGE);
      } else if (currentImageName.isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "Please enter an image name.", "Error",
            JOptionPane.ERROR_MESSAGE);
      } else {
        dialog.dispose();
      }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    commandLine = commandLine + imagePath + " " + currentImageName;
    imagePath = "";
    return commandLine;
  }

  private String handleSave(String s) {
    if (currentImageName == null) {
      showError("No Image Loaded. Please Load Image.");
      return null;
    }

    commandLine = s + " ";

    JFileChooser saveDialog = getjFileChooser();

    int userChoice = saveDialog.showSaveDialog(null);

    if (userChoice == JFileChooser.APPROVE_OPTION) {
      File selectedFile = saveDialog.getSelectedFile();
      imagePath = selectedFile.getAbsolutePath();
      imagePath += "." + ((FileNameExtensionFilter) saveDialog.getFileFilter()).getExtensions()[0];
      commandLine = commandLine + imagePath + " " + currentImageName;
      imagePath = "";
      isImageSaved = true;
      return commandLine;
    } else {
      showError("Save operation canceled by the user.");
      return null;
    }
  }

  private String handleDestNameAndOneInteger(String s) {
    if (currentImageName == null) {
      showError("No Image Loaded. Please Load Image.");
      return null;
    }
    String commandLine = s + " ";

    JDialog dialog = new JDialog(this, s, true);
    dialog.setLayout(new GridLayout(0, 2));

    JLabel intLabel = new JLabel("Enter an Integer:");
    JTextField intField = new JTextField();
    JLabel nameLabel = new JLabel("Enter Image Name:");
    JTextField nameField = new JTextField();
    dialog.add(intLabel);
    dialog.add(intField);
    dialog.add(nameLabel);
    dialog.add(nameField);

    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    dialog.add(new JLabel());
    dialog.add(buttonPanel);
    String[] result = new String[2];

    okButton.addActionListener(e -> {
      String intText = intField.getText().trim();
      String strText = nameField.getText().trim();

      try {
        result[0] = intText;
        result[1] = strText;
        dialog.dispose();
      } catch (NumberFormatException ex) {
        showError("Invalid Integer, Please enter a valid number.");
      }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    try {
      int number = Integer.parseInt(result[0].trim());
      if (s.equals("compress")) {
        if (number < 0 || number > 99) {
          showError("Invalid percentage. Must be between 0 and 99.");
          return null;
        } else {
          commandLine += result[0] + " " + currentImageName + " " + result[1];
          currentImageName = result[1];
        }
      } else {
        commandLine += result[0] + " " + currentImageName + " " + result[1];
        currentImageName = result[1];
      }
    } catch (NumberFormatException ex) {
      showError("Invalid Input.");
    }
    return commandLine;
  }

  private String handleDestNameOnly(String s) {
    if (currentImageName == null) {
      showError("No Image Loaded. Please Load Image.");
      return null;
    }
    commandLine = s + " " + currentImageName;
    String destName;
    String imageNameInput = JOptionPane.showInputDialog(this,
        "Enter Image Name:", s, JOptionPane.PLAIN_MESSAGE);
    if (imageNameInput != null && !imageNameInput.trim().isEmpty()) {
      destName = imageNameInput;
      currentImageName = imageNameInput;
      commandLine += " " + destName;
    } else {
      feedbackArea.setText("No image name provided.");
    }

    return commandLine;
  }


  private String handleSplitView(String s) {
    if (currentImageName == null) {
      showError("No Image Loaded. Please Load Image.");
      return null;
    }
    commandLine = s + " " + currentImageName + " ";
    JDialog dialog = new JDialog(this, s, true);
    dialog.setLayout(new GridLayout(0, 2));

    JLabel intLabel = new JLabel("Enter Split View Percentage:");
    JTextField intField = new JTextField("100");
    JLabel nameLabel = new JLabel("Enter Image Name:");
    JTextField nameField = new JTextField();
    dialog.add(intLabel);
    dialog.add(intField);
    dialog.add(nameLabel);
    dialog.add(nameField);

    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    dialog.add(new JLabel());
    dialog.add(buttonPanel);
    String[] result = new String[2];

    okButton.addActionListener(e -> {
      String intText = intField.getText().trim();
      String strText = nameField.getText().trim();

      try {
        int percentage = Integer.parseInt(intText);
        if (percentage < 0 || percentage > 100) {
          showError("Invalid Split View Percentage. Integer between 0 and 100");
          return;
        }
        result[0] = intText;
        result[1] = strText;
        dialog.dispose();
      } catch (NumberFormatException ex) {
        showError("Invalid Integer, Please enter a valid number.");
      }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    // Final Input validation and command preparation
    int percentage = Integer.parseInt(result[0].trim());
    if (percentage < 0 || percentage > 100) {
      showError("Invalid Split View Percentage. Integer between 0 and 100");
      return null;
    } else {
      commandLine += result[1] + " split " + result[0];
      currentImageName = result[1];
    }

    return commandLine;
  }

  private String handleLevelsAdjust(String s) {
    if (currentImageName == null) {
      showError("No Image Loaded. Please Load Image.");
      return null;
    }
    commandLine = s + " ";

    JDialog dialog = new JDialog(this, s, true);
    dialog.setLayout(new GridLayout(0, 2));

    JLabel bLabel = new JLabel("Enter b Value:");
    JTextField bField = new JTextField();
    JLabel mLabel = new JLabel("Enter m Value:");
    JTextField mField = new JTextField();
    JLabel wLabel = new JLabel("Enter w Value:");
    JTextField wField = new JTextField();
    JLabel intLabel = new JLabel("Enter Split View Percentage:");
    JTextField intField = new JTextField("100");
    JLabel nameLabel = new JLabel("Enter Image Name:");
    JTextField nameField = new JTextField();
    dialog.add(bLabel);
    dialog.add(bField);
    dialog.add(mLabel);
    dialog.add(mField);
    dialog.add(wLabel);
    dialog.add(wField);
    dialog.add(intLabel);
    dialog.add(intField);
    dialog.add(nameLabel);
    dialog.add(nameField);

    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    dialog.add(new JLabel());
    dialog.add(buttonPanel);
    String[] result = new String[5];

    okButton.addActionListener(e -> {
      String bText = bField.getText();
      String mText = mField.getText();
      String wText = wField.getText();
      String intText = intField.getText().trim();
      String strText = nameField.getText().trim();

      try {
        result[0] = bText;
        result[1] = mText;
        result[2] = wText;
        result[3] = intText;
        result[4] = strText;
        int b = Integer.parseInt(result[0].trim());
        int m = Integer.parseInt(result[1].trim());
        int w = Integer.parseInt(result[2].trim());
        int percentage = Integer.parseInt(result[3].trim());
        // Validation checks
        if (b < 0 || m < 0 || w < 0 || w > 255 || b >= m || m >= w || percentage < 0
            || percentage > 100) {
          showError(
              "Invalid input values. Ensure 0 <= b < m < w <= 255 and the split"
                  + " percentage must be between 0 and 100.");
          return;
        }
        dialog.dispose();
      } catch (NumberFormatException ex) {
        showError("Invalid Integer, Please enter valid numbers.");
      }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    int b = Integer.parseInt(result[0].trim());
    int m = Integer.parseInt(result[1].trim());
    int w = Integer.parseInt(result[2].trim());
    int percentage = Integer.parseInt(result[3].trim());

    if (b < 0 || m < 0 || w < 0 || w > 255 || b >= m || m >= w || percentage < 0
        || percentage > 100) {
      showError(
          "Invalid input values. Ensure 0 <= b < m < w <= 255 and the split "
              + "percentage must be between 0 and 100.");
      return null;
    } else {
      commandLine +=
          b + " " + m + " " + w + " " + currentImageName + " " + result[4] + " split " + percentage;
      currentImageName = result[4];
    }
    return commandLine;
  }

  private boolean saveImage() {
    if (!isImageSaved) {
      int choice = JOptionPane.showConfirmDialog(
          this,
          "The current image is not saved. Would you like to save it before proceeding?",
          "Unsaved Changes",
          JOptionPane.YES_NO_CANCEL_OPTION,
          JOptionPane.WARNING_MESSAGE
      );

      if (choice == JOptionPane.YES_OPTION) {
        executeCommand("save");
        return isImageSaved;
      } else {
        return choice != JOptionPane.CANCEL_OPTION;
      }
    }
    return true;
  }

  private String handleDownscaling(String s) {
    if (currentImageName == null) {
      showError("No Image Loaded. Please Load Image.");
      return null;
    }
    commandLine = s + " ";

    JDialog dialog = new JDialog(this, s, true);
    dialog.setLayout(new GridLayout(0, 2));

    JLabel widthLabel = new JLabel("Enter New Width:");
    JTextField widthField = new JTextField();
    JLabel heightLabel = new JLabel("Enter New Height:");
    JTextField heightField = new JTextField();
    JLabel nameLabel = new JLabel("Enter Image Name:");
    JTextField nameField = new JTextField();
    dialog.add(widthLabel);
    dialog.add(widthField);
    dialog.add(heightLabel);
    dialog.add(heightField);
    dialog.add(nameLabel);
    dialog.add(nameField);

    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    dialog.add(new JLabel());
    dialog.add(buttonPanel);
    String[] result = new String[3];

    okButton.addActionListener(e -> {
      String widthText = widthField.getText();
      String heightText = heightField.getText();
      String strText = nameField.getText().trim();

      try {
        result[0] = widthText;
        result[1] = heightText;
        result[2] = strText;
        int width = Integer.parseInt(result[0].trim());
        int height = Integer.parseInt(result[1].trim());

        if (width > controller.getImage(currentImageName).getWidth()
            || height > controller.getImage(currentImageName).getHeight()) {
          showError("The new width and height should be smaller than "
              + "the current image.");
          return;
        }
        dialog.dispose();
      } catch (NumberFormatException ex) {
        showError("Invalid Integer, Please enter valid numbers.");
      }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    int width = Integer.parseInt(result[0].trim());
    int height = Integer.parseInt(result[1].trim());
    // Validation checks
    if (width > controller.getImage(currentImageName).getWidth() || height > controller.getImage(
        currentImageName).getHeight()) {
      showError("The new width and height should be smaller than the current image.");
      return null;
    } else {
      commandLine += currentImageName + " " + result[2] + " " + width + " " + height;
      currentImageName = result[2];
    }
    return commandLine;
  }

}
