import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

/**
 * The ImageController class is responsible for processing image commands, interacting with the
 * model to perform image-related operations and the view to display results or error messages. This
 * controller supports a range of image operations including loading, saving, color component
 * extraction, image flipping, brightness adjustment, and more.
 */
public class ImageController implements ImageControllerInterface {

  private final ImageModel model;
  private final ImageView view;


  private final Map<String, Consumer<String[]>> commandMap;

  /**
   * Constructs an ImageController with the specified model and view.
   *
   * @param model the image model to handle image operations
   * @param view  the image view to display feedback to the user
   */
  public ImageController(ImageModel model, ImageView view) {
    this.model = model;
    this.view = view;

    commandMap = new HashMap<>();
    commandMap.put("load", this::handleLoad);
    commandMap.put("save", this::handleSave);
    commandMap.put("red-component", this::handleComponent);
    commandMap.put("green-component", this::handleComponent);
    commandMap.put("blue-component", this::handleComponent);
    commandMap.put("value-component", this::handleComponent);
    commandMap.put("luma-component", this::handleComponent);
    commandMap.put("intensity-component", this::handleComponent);
    commandMap.put("horizontal-flip", this::handleFlip);
    commandMap.put("vertical-flip", this::handleFlip);
    commandMap.put("brighten", this::handleBrighten);
    commandMap.put("darken", this::handleDarken);
    commandMap.put("rgb-split", this::handleRgbSplit);
    commandMap.put("rgb-combine", this::handleRgbCombine);
    commandMap.put("blur", this::handleBlur);
    commandMap.put("sharpening", this::handleSharpen);
    commandMap.put("sepia", this::handleSepia);
    commandMap.put("run", this::runScript);
    commandMap.put("histogram", this::handleHistogram);
    commandMap.put("color-correct", this::handleColorCorrect);
    commandMap.put("levels-adjust", this::handleLevelsAdjust);
    commandMap.put("greyscale", this::handleGreyscale);
    commandMap.put("compress", this::handleCompress);
    commandMap.put("partial-manipulation", this::handlePartialImageManipulation);
    commandMap.put("downscale", this::handleImageDownscaling);
  }


  /**
   * Executes a given command related to image processing. The command should be in a format that
   * the controller can parse and execute.
   *
   * @param command the command string to execute
   */
  public void executeCommand(String command) {
    String[] tokens = command.split(" ");

    if (commandMap.containsKey(tokens[0])) {
      commandMap.get(tokens[0]).accept(tokens);
    } else {
      view.showError("Invalid file or Unknown command: " + command);
    }
  }

  /**
   * Retrieves a CustomImage object by its name.
   *
   * @param imageName The name of the image to retrieve. This should correspond to a previously
   *                  loaded or added image in the model.
   * @return The CustomImage associated with the provided imageName, or null if no image with that
   *          name exists.
   */
  @Override
  public CustomImage getImage(String imageName) {
    return model.getImage(imageName);
  }


  private void handleLoad(String[] tokens) {
    if (tokens.length < 3) {
      view.showError("Invalid command. Missing arguments. " +
          "Usage: load <imagePath> <imageName>");
      return;
    }
    try {
      CustomImage load = loadImage(tokens[1]);
      if (load == null) {
        view.showError("Failed to load image: " + tokens[1]);
      } else {
        model.addImage(tokens[2], load);
        System.out.println("Successfully Loaded image: " + tokens[1]);
      }
    } catch (Exception e) {
      view.showError("Failed to load image. " + e.getMessage());
    }
  }

  private CustomImage loadImage(String imagePath) {
    CustomImage img = null;
    try {
      if (imagePath.endsWith(".ppm")) {
        int[][][] pixels = readPPM(imagePath);
        if (pixels != null) {
          img = new CustomImage(pixels.length, pixels[0].length);
          img.setPixels(pixels);
          return img;
        }
      } else {
        BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
        img = new CustomImage(bufferedImage.getHeight(), bufferedImage.getWidth());

        for (int y = 0; y < bufferedImage.getHeight(); y++) {
          for (int x = 0; x < bufferedImage.getWidth(); x++) {
            int rgb = bufferedImage.getRGB(x, y);
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;

            img.setPixel(x, y, new int[]{red, green, blue});
          }
        }
        return img;
      }
    } catch (IOException e) {
      System.out.println("Error loading image: " + e.getMessage());
    }
    return img;
  }

  private int[][][] readPPM(String imagePath) {
    try (Scanner sc = new Scanner(new FileInputStream(imagePath))) {

      if (!sc.next().equals("P3")) {
        System.out.println("Invalid PPM file: must begin with P3");
        return null;
      }

      int width = sc.nextInt();
      int height = sc.nextInt();
      sc.nextInt();

      int[][][] pixels = new int[height][width][3];
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixels[i][j][0] = sc.nextInt();
          pixels[i][j][1] = sc.nextInt();
          pixels[i][j][2] = sc.nextInt();
        }
      }
      return pixels;
    } catch (FileNotFoundException e) {
      System.out.println("Image " + imagePath + " not found!");
      return null;
    }
  }

  private void handleSave(String[] tokens) {
    if (tokens.length < 3) {
      view.showError("Invalid command. Missing arguments." +
          " Usage: save <imagePath> <imageName>");
      return;
    }
    try {
      saveImage(tokens[1], tokens[2]);
    } catch (Exception e) {
      view.showError("Failed to save image. " + e.getMessage());
    }
  }

  private void saveImage(String imagePath, String imageName) {
    if (!(imagePath.endsWith(".ppm") || imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg")
        || imagePath.endsWith(".png"))) {
      System.out.println("Error: Invalid file extension. Please use .ppm, .jpg, .jpeg or .png.");
      return;
    }

    if (new File(imagePath).exists()) {
      System.out.println(
          "Error: File already exists at " + imagePath + ". Choose a different file name.");
      return;
    }

    CustomImage img = model.getImage(imageName);
    if (img == null) {
      System.out.println("ERROR: Image not found: " + imageName);
      return;
    }

    try {
      if (imagePath.endsWith(".ppm")) {
        savePPM(imagePath, imageName, img.getPixels());
      } else {
        BufferedImage bufferedImage = transferToBufferedImage(img);
        ImageIO.write(bufferedImage, imagePath.substring(imagePath.lastIndexOf(".") + 1),
            new File(imagePath));
        System.out.println("Image saved: " + imagePath);
      }
    } catch (FileNotFoundException e) {
      System.out.println("Error: Directory does not exist or is inaccessible." +
          " Please check the path: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("Error saving image: " + e.getMessage());
    }
  }

  /**
   * Converts a CustomImage object to a BufferedImage.
   *
   * @param img The CustomImage object to be converted. This must not be null.
   * @return A BufferedImage object containing the same pixel data as the input CustomImage. The
   *           BufferedImage will use the RGB color model (TYPE_INT_RGB).
   */
  public BufferedImage transferToBufferedImage(CustomImage img) {
    BufferedImage bufferedImage = new BufferedImage(img.getWidth(), img.getHeight(),
        BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        int[] rgb = img.getPixel(x, y);
        int rgbValue = (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
        bufferedImage.setRGB(x, y, rgbValue);
      }
    }
    return bufferedImage;
  }

  private void savePPM(String imagePath, String imageName, int[][][] pixels) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(imagePath))) {
      int height = pixels.length;
      int width = pixels[0].length;

      writer.write("P3\n");
      writer.write(width + " " + height + "\n");
      writer.write("255\n");

      for (int[][] pixel : pixels) {
        for (int j = 0; j < width; j++) {
          writer.write(pixel[j][0] + "\n");
          writer.write(pixel[j][1] + "\n");
          writer.write(pixel[j][2] + "\n");
        }
      }

      System.out.println("PPM image saved as " + imageName);
    } catch (IOException e) {
      System.out.println("Error saving PPM image: " + e.getMessage());
    }
  }

  private void handleComponent(String[] tokens) {
    String type = tokens[0].replace("-component", "");
    if (tokens.length < 3) {
      view.showError(
          "Invalid command. Missing arguments. Usage: "
              + tokens[0] + " <imageName> <destName>");
      return;
    }
    try {
      if (tokens.length == 4) {
        model.partialImageManipulation(tokens[0], tokens[1], tokens[2], tokens[3]);
      } else {
        model.component(tokens[1], tokens[2], type);
      }
    } catch (Exception e) {
      view.showError("Failed to add " + type + " component. " + e.getMessage());
    }
  }

  private void handleFlip(String[] tokens) {
    boolean horizontal = tokens[0].startsWith("horizontal");
    if (tokens.length < 3) {
      view.showError(
          "Invalid command. Missing arguments. Usage: "
              + tokens[0] + " <imageName> <destName>");
      return;
    }
    try {
      model.flipImage(tokens[1], tokens[2], horizontal);
    } catch (Exception e) {
      view.showError("Failed to flip image. " + e.getMessage());
    }
  }

  private void handleBrighten(String[] tokens) {
    if (tokens.length < 4) {
      view.showError(
          "Invalid command. Missing arguments." +
              " Usage: brighten <increment> <imageName> <destName>");
      return;
    }
    try {
      int increment = Integer.parseInt(tokens[1]);
      model.adjustBrightness(increment, tokens[2], tokens[3], true);
    } catch (Exception e) {
      view.showError("Failed to brighten image. " + e.getMessage());
    }
  }

  private void handleDarken(String[] tokens) {
    if (tokens.length < 4) {
      view.showError(
          "Invalid command. Missing arguments." +
              " Usage: darken <increment> <imageName> <destName>");
      return;
    }
    try {
      int increment = Integer.parseInt(tokens[1]);
      model.adjustBrightness(increment, tokens[2], tokens[3], false);
    } catch (Exception e) {
      view.showError("Failed to darken image. " + e.getMessage());
    }
  }

  private void handleRgbSplit(String[] tokens) {
    if (tokens.length < 5) {
      view.showError(
          "Invalid command. Missing arguments. Usage: rgb-split <imageName> <redDestName> "
              + "<greenDestName> <blueDestName>");
      return;
    }
    try {
      model.component(tokens[1], tokens[2], "red");
      model.component(tokens[1], tokens[3], "green");
      model.component(tokens[1], tokens[4], "blue");
      if (!model.getImages().containsKey(tokens[2]) || !model.getImages().containsKey(tokens[3])
          || !model.getImages().containsKey(tokens[4])) {
        view.showError("Failed to split RGB channels.");
      } else {
        System.out.println(tokens[1] + " split successful!");
      }
    } catch (Exception e) {
      view.showError("Failed to split RGB channels. " + e.getMessage());
    }
  }

  private void handleRgbCombine(String[] tokens) {
    if (tokens.length < 5) {
      view.showError(
          "Invalid command. Missing arguments. Usage: rgb-combine <destName> <redImageName> "
              + "<greenImageName> <blueImageName>");
      return;
    }
    try {
      model.combine(tokens[1], tokens[2], tokens[3], tokens[4]);
    } catch (Exception e) {
      view.showError("Failed to combine RGB channels. " + e.getMessage());
    }
  }

  private void handleBlur(String[] tokens) {
    if (tokens.length < 3) {
      view.showError("Invalid command. Missing arguments. Usage: blur <imageName> <destName>");
      return;
    }
    try {
      if (tokens.length == 3) {
        model.imageFilter(tokens[1], tokens[2], true, 100);
      } else if (tokens.length == 5 && tokens[3].equals("split")) {
        int p = Integer.parseInt(tokens[4]);
        model.imageFilter(tokens[1], tokens[2], true, p);
      } else if (tokens.length == 4) {
        model.partialImageManipulation(tokens[0], tokens[1], tokens[2], tokens[3]);
      } else {
        view.showError("Invalid command. Missing arguments. Usage: blur "
            + "<imageName> <destName> split <percentageValue(0-100)>");
      }
    } catch (Exception e) {
      view.showError("Failed to blur image. " + e.getMessage());
    }
  }

  private void handleSharpen(String[] tokens) {
    if (tokens.length < 3) {
      view.showError("Invalid command. Missing arguments. Usage: sharpen <imageName> <destName>");
      return;
    }
    try {

      if (tokens.length == 3) {
        model.imageFilter(tokens[1], tokens[2], false, 100);
      } else if (tokens.length == 5 && tokens[3].equals("split")) {
        int p = Integer.parseInt(tokens[4]);
        model.imageFilter(tokens[1], tokens[2], false, p);
      } else if (tokens.length == 4) {
        model.partialImageManipulation(tokens[0], tokens[1], tokens[2], tokens[3]);
      } else {
        view.showError("Invalid command. Missing arguments. Usage: sharpen "
            + "<imageName> <destName> split <percentageValue(0-100)>");
      }
    } catch (Exception e) {
      view.showError("Failed to sharpen image. " + e.getMessage());
    }
  }

  private void handleSepia(String[] tokens) {
    if (tokens.length < 3) {
      view.showError("Invalid command. Missing arguments. Usage: sepia <imageName> <destName>");
      return;
    }
    try {
      if (tokens.length == 3) {
        model.sepia(tokens[1], tokens[2], 100);
      } else if (tokens.length == 5 && tokens[3].equals("split")) {
        int p = Integer.parseInt(tokens[4]);
        model.sepia(tokens[1], tokens[2], p);
      } else if (tokens.length == 4) {
        model.partialImageManipulation(tokens[0], tokens[1], tokens[2], tokens[3]);
      } else {
        view.showError("Invalid command. Missing arguments. Usage: sepia "
            + "<imageName> <destName> split <percentageValue(0-100)>");
      }
    } catch (Exception e) {
      view.showError("Failed to apply sepia filter. " + e.getMessage());
    }
  }

  private void handleGreyscale(String[] tokens) {
    if (tokens.length < 3) {
      view.showError("Invalid command. Missing arguments. Usage: greyscale <imageName> <destName>");
      return;
    }

    try {
      if (tokens.length == 3) {
        model.greyscale(tokens[1], tokens[2], 100);
      } else if (tokens.length == 5 && tokens[3].equals("split")) {
        int p = Integer.parseInt(tokens[4]);
        if (p < 0 || p > 100) {
          view.showError("Invalid percentage. Must be between 0 and 100.");
          return;
        }
        model.greyscale(tokens[1], tokens[2], p);
      } else if (tokens.length == 4) {
        model.partialImageManipulation(tokens[0], tokens[1], tokens[2], tokens[3]);
      } else {
        view.showError(
            "Invalid command. Usage: greyscale <imageName> <destName> or greyscale <imageName> "
                + "<destName> split <percentageValue(0-100)>");
      }
    } catch (Exception e) {
      view.showError("Failed to apply greyscale filter. " + e.getMessage());
    }
  }

  private void runScript(String[] tokens) {
    if (tokens.length < 2) {
      view.showError("Invalid command. Missing arguments. Usage: run <scriptFilePath>");
      return;
    }

    String scriptFilePath = tokens[1];
    try (BufferedReader reader = new BufferedReader(new FileReader(scriptFilePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();

        if (line.isEmpty() || line.startsWith("#")) {
          continue; // Skip empty lines or comments
        }

        executeCommand(line); // Execute the command in the script
      }
      view.showMessage("Script executed successfully.");
    } catch (FileNotFoundException e) {
      view.showError("Script file not found: " + scriptFilePath);
    } catch (IOException e) {
      view.showError("Error reading script file " + scriptFilePath + ": " + e.getMessage());
    }
  }


  private void handleCompress(String[] tokens) {
    if (tokens.length < 3) {
      view.showError("Invalid command. Missing arguments." +
          " Usage: compress <percentage>(0-99) <imageName> <destName>");
      return;
    }
    try {
      int percent = Integer.parseInt(tokens[1]);
      model.imageCompression(tokens[2], tokens[3], percent);
    } catch (Exception e) {
      view.showError("Failed to compress image. " + e.getMessage());
    }
  }

  private void handleHistogram(String[] tokens) {
    if (tokens.length < 3) {
      view.showError("Invalid command. Missing arguments." +
          " Usage: histogram <imageName> <destName>");
      return;
    }
    try {
      model.histogramImage(tokens[1], tokens[2]);
    } catch (Exception e) {
      view.showError("Failed to create histogram image. " + e.getMessage());
    }
  }

  private void handleColorCorrect(String[] tokens) {
    if (tokens.length < 3) {
      view.showError("Invalid command. Missing arguments." +
          " Usage: color-correct <imageName> <destName>");
      return;
    }
    try {
      if (tokens.length == 3) {
        model.colorCorrection(tokens[1], tokens[2], 100);
      } else if (tokens.length == 5 && tokens[3].equals("split")) {
        int p = Integer.parseInt(tokens[4]);
        model.colorCorrection(tokens[1], tokens[2], p);
      } else {
        view.showError("Invalid command. Missing arguments." +
            " Usage: color-correct <imageName> <destName> split <percentageValue(0-100)>");
      }
    } catch (Exception e) {
      view.showError("Failed to color-correct an image. " + e.getMessage());
    }
  }

  private void handleLevelsAdjust(String[] tokens) {
    if (tokens.length < 6) {
      view.showError("Invalid command. Missing arguments." +
          " Usage: levels-adjust <shadow-value> <mid-value> <highlight-value> "
          + "<imageName> <destName>");
      return;
    }
    try {
      int b = Integer.parseInt(tokens[1]);
      int m = Integer.parseInt(tokens[2]);
      int w = Integer.parseInt(tokens[3]);
      if (tokens.length == 6) {
        model.levelsAdjust(b, m, w, tokens[4], tokens[5], 100);
      } else if (tokens.length == 8 && tokens[6].equals("split")) {
        int p = Integer.parseInt(tokens[7]);
        model.levelsAdjust(b, m, w, tokens[4], tokens[5], p);
      } else {
        view.showError("Invalid command. Missing arguments." +
            " Usage: levels-adjust <shadow-value> <mid-value> <highlight-value> "
            + "<imageName> <destName> split <percentageValue(0-100)>");
      }
    } catch (Exception e) {
      view.showError("Failed to adjust levels of an image. " + e.getMessage());
    }
  }

  private void handlePartialImageManipulation(String[] tokens) {
    if (tokens.length < 5) {
      view.showError("Invalid command. Missing arguments. " +
          "Usage: partial-manipulation <command> <imageName> <maskImageName> <destName>");
      return;
    }
    try {
      String command = tokens[1];
      String imageName = tokens[2];
      String maskImageName = tokens[3];
      String destName = tokens[4];

      // Validate command
      if (!isValidManipulationCommand(command)) {
        view.showError("Invalid manipulation command: " + command);
        return;
      }

      model.partialImageManipulation(command, imageName, maskImageName, destName);
      view.showMessage("Partial manipulation done successfully for image: " + imageName);
    } catch (Exception e) {
      view.showError("Failed to perform partial image manipulation. " + e.getMessage());
    }
  }

  /**
   * Helper method to validate if the provided command is a recognized manipulation action.
   *
   * @param command The manipulation command string to validate.
   * @return true if the command is valid, false otherwise.
   */
  private boolean isValidManipulationCommand(String command) {
    switch (command) {
      case "blur":
      case "sharpen":
      case "greyscale":
      case "sepia":
      case "red-component":
      case "green-component":
      case "blue-component":
      case "value-component":
      case "luma-component":
      case "intensity-component":
        return true;
      default:
        return false;
    }
  }

  private void handleImageDownscaling(String[] tokens) {
    if (tokens.length < 5) {
      view.showError("Invalid command. Missing arguments. " +
          "Usage: downscale <imageName> <destName> <newWidth> <newHeight>");
      return;
    }
    try {
      String imageName = tokens[1];
      String destName = tokens[2];
      int newWidth = Integer.parseInt(tokens[3]);
      int newHeight = Integer.parseInt(tokens[4]);

      model.downscaleImage(imageName, destName, newWidth, newHeight);
    } catch (NumberFormatException e) {
      view.showError("Invalid size value provided. Width and height must be integers.");
    } catch (Exception e) {
      view.showError("Failed to downscale image. " + e.getMessage());
    }
  }
}


