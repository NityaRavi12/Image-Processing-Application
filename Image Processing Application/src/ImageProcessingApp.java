import java.util.Scanner;

/**
 * The main application class for the Image Processing App. It initializes the image model, view,
 * and controller, and handles user input.
 */
public class ImageProcessingApp {

  /**
   * The entry point of the Image Processing App.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {
    ImageModel imageModel = new ImageModel();
    ImageView imageView = new ImageView();
    ImageController imageController = new ImageController(imageModel, imageView);

    imageView.setController(imageController);

    if (args.length > 0 && args[0].equals("-file") && args.length > 1) {
      String fileName = args[1];
      String command = "run " + fileName;
      imageController.executeCommand(command);
    } else if (args.length > 0 && args[0].equals("-text") && args.length == 1) {
      interactiveMode(imageController);
    } else {
      // Show GUI
      imageView.setVisible(true);
    }
  }

  private static void interactiveMode(ImageController imageController) {
    Scanner scanner = new Scanner(System.in);
    String command;

    System.out.println("Welcome to the Image Processing App!");
    System.out.println("Type 'exit' to quit. Enter commands to process images: ");

    while (true) {
      command = scanner.nextLine();

      if (command.equalsIgnoreCase("exit")) {
        break;
      }

      imageController.executeCommand(command);
    }

    scanner.close();
    System.out.println("Exiting the Image Processing App.");
  }
}