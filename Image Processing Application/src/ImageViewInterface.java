import javax.swing.JTextArea;

/**
 * ImageViewInterface defines the contract for any view class in the image processing application,
 * ensuring that basic functionalities are provided.
 */
public interface ImageViewInterface {

  /**
   * Displays a message in the feedback area.
   *
   * @param message The message to show.
   */
  void showMessage(String message);

  /**
   * Displays an error message in the feedback area.
   *
   * @param errorMessage The error message to show.
   */
  void showError(String errorMessage);

  /**
   * Gets the feedback area for displaying messages.
   *
   * @return The feedback JTextArea.
   */
  JTextArea getFeedbackArea();

}