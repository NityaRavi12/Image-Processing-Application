import java.util.Map;

/**
 * Interface for image processing operations, defining methods for loading, saving, and applying
 * various transformations and filters to images. Implementations of this interface provide a range
 * of image manipulation capabilities, including brightness adjustment, color correction, and
 * compression.
 */
public interface ImageModelInterface {

  /**
   * Adds an image to the collection if it does not already exist.
   *
   * @param imageName the name of the image to add
   * @param img       the CustomImage to be added associated with the specified image name
   */
  void addImage(String imageName, CustomImage img);


  /**
   * Retrieves an image by its name.
   *
   * @param imageName the name of the image to retrieve
   * @return the CustomImage associated with the specified name, or null if no such image exists
   */
  CustomImage getImage(String imageName);

  /**
   * Extracts a color component from the image and saves it as a new image.
   *
   * @param imageName     the name of the source image
   * @param destName      the name of the destination image file
   * @param componentType the type of color component to extract (e.g., "red", "green", "blue")
   */
  void component(String imageName, String destName, String componentType);

  /**
   * Flips the image either horizontally or vertically and saves it as a new image.
   *
   * @param imageName    the name of the source image
   * @param destName     the name of the destination image file
   * @param isHorizontal true for horizontal flip, false for vertical flip
   */
  void flipImage(String imageName, String destName, boolean isHorizontal);

  /**
   * Adjusts the brightness of the image and saves it as a new image.
   *
   * @param value      the amount to adjust the brightness (positive to brighten, negative to
   *                   darken)
   * @param imageName  the name of the source image
   * @param destName   the name of the destination image file
   * @param isBrighten true to brighten the image, false to darken it
   */
  void adjustBrightness(int value, String imageName, String destName, boolean isBrighten);

  /**
   * Combines three images (red, green, and blue) into a single image.
   *
   * @param imageName      the name of the combined image
   * @param redImageName   the name of the red component image
   * @param greenImageName the name of the green component image
   * @param blueImageName  the name of the blue component image
   */
  void combine(String imageName, String redImageName, String greenImageName, String blueImageName);

  /**
   * Applies a filter to the image, either blurring or sharpening it.
   *
   * @param imageName  the name of the source image
   * @param destName   the name of the destination image file
   * @param isBlur     true to apply a blur filter, false to apply a sharpen filter
   * @param percentage the percentage to show the image
   */
  void imageFilter(String imageName, String destName, boolean isBlur, int percentage);

  /**
   * Applies a sepia tone effect to the image and saves it as a new image.
   *
   * @param imageName  the name of the source image
   * @param destName   the name of the destination image file
   * @param percentage the percentage to show the image
   */
  void sepia(String imageName, String destName, int percentage);

  /**
   * Applies a sepia tone effect to the image and saves it as a new image.
   *
   * @param imageName  the name of the source image
   * @param destName   the name of the destination image file
   * @param percentage the percentage to show the image
   */
  void greyscale(String imageName, String destName, int percentage);

  /**
   * Generates and saves a histogram image based on the specified image.
   *
   * @param imageName the name of the source image
   * @param destName  the name of the destination histogram image file
   */
  void histogramImage(String imageName, String destName);

  /**
   * Performs color correction on the image and saves it as a new image.
   *
   * @param imageName  the name of the source image
   * @param destName   the name of the destination image file
   * @param percentage the percentage to show the image
   */
  void colorCorrection(String imageName, String destName, int percentage);

  /**
   * Adjusts the levels of an image based on specified brightness (b), midtone (m), and white point
   * (w) values. The adjusted image is saved under a new name.
   *
   * @param b             The brightness level (lower bound), must be in the range [0, 255].
   * @param m             The midtone level, must be in the range [0, 255] and greater than b.
   * @param w             The white point level (upper bound), must be in the range [0, 255] and
   *                      greater than m.
   * @param imageName     The name of the original image to adjust.
   * @param destImageName The name under which to save the adjusted image.
   * @param percentage    the percentage to show the image
   */
  void levelsAdjust(int b, int m, int w, String imageName, String destImageName, int percentage);

  /**
   * Retrieves a map of images, where each entry associates a image name with its corresponding
   * image.
   *
   * @return A map containing the images, with keys representing name of the images and values as
   *            the actual image.
   */
  Map<String, CustomImage> getImages();

  /**
   * Compresses the given image using a Haar wavelet transform and a thresholding technique. The
   * compression is achieved by reducing the number of non-zero coefficients in the Haar transform
   * based on the specified compression percentage.
   *
   * @param imageName     the name of the image to be compressed
   * @param destImageName the name for the compressed image to be saved as
   * @param percentage    the percentage of compression (0-99), which determines the proportion of
   *                      the Haar coefficients that are retained after thresholding
   * @throws IllegalArgumentException if the percentage is not between 0 and 100 (exclusive)
   */
  void imageCompression(String imageName, String destImageName, int percentage);

  /**
   * Applies an image manipulation to a portion of an image based on a provided mask image. The
   * manipulation is applied only to the pixels where the mask image is black (0, 0, 0), and the
   * original image is retained for all other pixels. Supported commands include: blur, sharpen,
   * greyscale, sepia, and component-based visualizations (e.g., red, green, blue, value, luma, and
   * intensity components).
   *
   * @param command       The image manipulation operation to perform. Supported values are: "blur",
   *                      "sharpen", "greyscale", "sepia", "red-component", "green-component",
   *                      "blue-component", "value-component", "luma-component", and
   *                      "intensity-component".
   * @param imageName     The name of the image on which to apply the manipulation. This image will
   *                      be modified based on the corresponding mask image.
   * @param maskImageName The name of the mask image, which determines which parts of the original
   *                      image will be affected. A black pixel (0, 0, 0) in the mask indicates that
   *                      the corresponding pixel in the image will be manipulated.
   * @param destName      The name to save the resulting image after manipulation.
   * @throws IllegalArgumentException If the provided imageName or maskImageName does not exist in
   *                                  the images map or if the dimensions of the image and mask do
   *                                  not match.
   */
  void partialImageManipulation(String command, String imageName, String maskImageName,
      String destName);

  /**
   * Downscales an image to the specified width and height. This method creates a new image with
   * reduced dimensions based on the original image, using linear interpolation to determine the
   * pixel colors in the downscaled image.
   *
   * @param imageName The name of the image to be downscaled.
   * @param destName  The name under which to save the downscaled image.
   * @param newWidth  The desired width for the downscaled image.
   * @param newHeight The desired height for the downscaled image.
   * @throws IllegalArgumentException If the provided imageName does not exist in the images map.
   * @throws IllegalArgumentException If newWidth or newHeight is less than or equal to zero.
   */
  void downscaleImage(String imageName, String destName, int newWidth, int newHeight);
}
