import java.awt.image.BufferedImage;

/**
 * An interface for the ImageController, providing a method to handle image processing commands and
 * facilitate interaction between the image model and the view. Implementations of this interface
 * interpret and execute commands for processing images, passing relevant data to the model for
 * manipulation and updating the view to display results or errors as needed.
 */
public interface ImageControllerInterface {

  /**
   * Executes a given command related to image processing. The command should be in a format that
   * the controller can parse and execute.
   *
   * @param command the command string to execute
   */
  void executeCommand(String command);

  /**
   * Retrieves a CustomImage object by its name.
   *
   * @param imageName The name of the image to retrieve. This should correspond to a previously
   *                  loaded or added image in the model.
   * @return The CustomImage associated with the provided imageName, or null if no image with that
   *            name exists.
   */
  CustomImage getImage(String imageName);

  /**
   * Converts a CustomImage object to a BufferedImage.
   *
   * @param img The CustomImage object to be converted. This must not be null.
   * @return A BufferedImage object containing the same pixel data as the input CustomImage. The
   *             BufferedImage will use the RGB color model (TYPE_INT_RGB).
   */
  BufferedImage transferToBufferedImage(CustomImage img);
}