import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;

/**
 * The ImageModel class is responsible for managing and processing a collection of images. It allows
 * operations such as adding images, retrieving images, manipulating individual color components
 * (red, green, blue), applying filters, adjusting brightness, flipping images, and creating
 * histograms or combined images. The class provides various image transformation functionalities to
 * work with pixel data. This class operates on custom image representations (CustomImage) that
 * contain pixel data and provides methods for manipulating these images in a variety of ways, such
 * as extracting color components, flipping the image, adjusting brightness, and applying filters
 * (blur, sharpen). It also supports generating histograms of images, applying grayscale, sepia
 * tones, and more. The ImageModel works with an internal map of images, identified by unique names,
 * and exposes methods to retrieve, add, and manipulate these images. Images are processed and
 * transformed based on pixel values and can be saved with new names after transformations are
 * applied. The `ImageModel` class ensures that the operations do not modify the original image
 * directly, but instead creates new image objects with the requested transformations.
 */
public class ImageModel implements ImageModelInterface {

  private final Map<String, CustomImage> images;
  private final Map<String, Function<int[], int[]>> componentMap;

  /**
   * constructor for image Model.
   */
  public ImageModel() {
    images = new HashMap<>();
    componentMap = new HashMap<>();
    componentMap.put("red", this::extractRed);
    componentMap.put("green", this::extractGreen);
    componentMap.put("blue", this::extractBlue);
    componentMap.put("value", this::extractValue);
    componentMap.put("luma", this::extractLuma);
    componentMap.put("intensity", this::extractIntensity);
  }

  private static CustomImage cropToOriginalSize(int[][][] pixels, int originalWidth,
      int originalHeight) {
    CustomImage croppedImage = new CustomImage(originalHeight, originalWidth);

    // Copy the original dimensions back to the new cropped image
    for (int y = 0; y < originalHeight; y++) {
      for (int x = 0; x < originalWidth; x++) {
        croppedImage.setPixel(x, y, pixels[y][x]);
      }
    }

    return croppedImage;
  }

  private static CustomImage padToSquarePowerOfTwo(CustomImage image) {
    int originalWidth = image.getWidth();
    int originalHeight = image.getHeight();

    // Calculate the nearest power of two for the larger dimension to make it square
    int maxDimension = Math.max(originalWidth, originalHeight);
    int squareSize = nextPowerOfTwo(maxDimension);

    // If the image is already a square with power-of-two dimensions, return the original image
    if (originalWidth == squareSize && originalHeight == squareSize) {
      return image;
    }

    // Create a new square image with the padded size
    CustomImage paddedImage = new CustomImage(squareSize, squareSize);

    // Copy the original pixels into the new padded image
    for (int y = 0; y < originalHeight; y++) {
      for (int x = 0; x < originalWidth; x++) {
        paddedImage.setPixel(x, y, image.getPixel(x, y));
      }
    }

    // Remaining pixels in paddedImage are automatically initialized to zero (black padding)
    return paddedImage;
  }

  private static int nextPowerOfTwo(int n) {
    if (n <= 0) {
      throw new IllegalArgumentException("Number must be positive");
    }
    int power = 1;
    while (power < n) {
      power *= 2;
    }
    return power;
  }


  /**
   * Adds an image to the collection if it does not already exist.
   *
   * @param imageName the name of the image to add
   * @param img       the CustomImage to be added associated with the specified image name
   */
  @Override
  public void addImage(String imageName, CustomImage img) {
    if (images.containsKey(imageName)) {
      throw new IllegalArgumentException(
          "Image '" + imageName + "' already exists."); // Ensure this throws exception
    }
    images.put(imageName, img);
  }

  /**
   * Retrieves an image by its name.
   *
   * @param imageName the name of the image to retrieve
   * @return the CustomImage associated with the specified name, or null if no such image exists
   */
  public CustomImage getImage(String imageName) {
    return images.get(imageName);
  }

  /**
   * Retrieves a map of images, where each entry associates a image name with its corresponding
   * image.
   *
   * @return A map containing the images, with keys representing name of the images and values as
   *            the actual image.
   */
  public Map<String, CustomImage> getImages() {
    return images;
  }


  /**
   * Extracts a color component from the image and saves it as a new image.
   *
   * @param imageName     the name of the source image
   * @param destName      the name of the destination image file
   * @param componentType the type of color component to extract (e.g., "red", "green", "blue")
   */
  public void component(String imageName, String destName, String componentType) {
    CustomImage img = images.get(imageName);
    if (img == null) {
      System.out.println("Image not found: " + imageName);
      return;
    }

    CustomImage resultImage = new CustomImage(img.getHeight(), img.getWidth());

    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        int[] rgb = img.getPixel(x, y);
        int[] newPixel = componentMap.get(componentType).apply(rgb);
        resultImage.setPixel(x, y, newPixel);
      }
    }
    addImage(destName, resultImage);
    System.out.println(componentType + " component of " + imageName + " added as: " + destName);
  }

  private int[] extractRed(int[] rgb) {
    int red = rgb[0];
    return new int[]{red, red, red};
  }

  private int[] extractGreen(int[] rgb) {
    int green = rgb[1];
    return new int[]{green, green, green};
  }

  private int[] extractBlue(int[] rgb) {
    int blue = rgb[2];
    return new int[]{blue, blue, blue};
  }

  private int[] extractValue(int[] rgb) {
    int red = rgb[0];
    int green = rgb[1];
    int blue = rgb[2];
    int value = Math.max(red, Math.max(green, blue));
    return new int[]{value, value, value};
  }

  private int[] extractLuma(int[] rgb) {
    int red = rgb[0];
    int green = rgb[1];
    int blue = rgb[2];
    int luma = (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
    return new int[]{luma, luma, luma};
  }

  private int[] extractIntensity(int[] rgb) {
    int red = rgb[0];
    int green = rgb[1];
    int blue = rgb[2];
    int intensity = (red + green + blue) / 3;
    return new int[]{intensity, intensity, intensity};
  }

  /**
   * Flips the image either horizontally or vertically and saves it as a new image.
   *
   * @param imageName    the name of the source image
   * @param destName     the name of the destination image file
   * @param isHorizontal true for horizontal flip, false for vertical flip
   */
  public void flipImage(String imageName, String destName, boolean isHorizontal) {
    CustomImage img = images.get(imageName);
    if (img == null) {
      System.out.println("ERROR: Image not found: " + imageName);
      return;
    }

    CustomImage flippedImage = new CustomImage(img.getHeight(), img.getWidth());

    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        int[] pixel = img.getPixel(x, y);

        if (isHorizontal) {
          flippedImage.setPixel(img.getWidth() - 1 - x, y, pixel);
        } else {
          flippedImage.setPixel(x, img.getHeight() - 1 - y, pixel);
        }
      }
    }

    addImage(destName, flippedImage);
    if (isHorizontal) {
      System.out.println(imageName + "Horizontal flip completed and added as: " + destName);
    } else {
      System.out.println(imageName + "Vertical flip completed and added as: " + destName);
    }

  }

  /**
   * Adjusts the brightness of the image and saves it as a new image.
   *
   * @param value      the amount to adjust the brightness (positive to brighten, negative to
   *                   darken)
   * @param imageName  the name of the source image
   * @param destName   the name of the destination image file
   * @param isBrighten true to brighten the image, false to darken it
   */
  public void adjustBrightness(int value, String imageName, String destName, boolean isBrighten) {
    CustomImage img = images.get(imageName);
    if (img == null) {
      System.out.println("Image not found: " + imageName);
      return;
    }

    CustomImage result = new CustomImage(img.getHeight(), img.getWidth());

    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        int[] pixel = img.getPixel(x, y);

        // Adjust each color channel (red, green, blue) independently
        int red = adjustValue(pixel[0], value, isBrighten);
        int green = adjustValue(pixel[1], value, isBrighten);
        int blue = adjustValue(pixel[2], value, isBrighten);

        // Set the new pixel in the result image
        result.setPixel(x, y, new int[]{red, green, blue});
      }
    }

    // Save the result to the model
    addImage(destName, result);
    if (isBrighten) {
      System.out.println(imageName + " Brightened by " + value + " and added as: " + destName);
    } else {
      System.out.println(imageName + " Darkened by " + value + " and added as: " + destName);
    }
  }

  private int adjustValue(int colorValue, int increment, boolean isBrightening) {
    if (isBrightening) {
      return Math.min(colorValue + increment, 255);  // Ensure value doesn't exceed 255
    } else {
      return Math.max(colorValue - increment, 0);    // Ensure value doesn't go below 0
    }
  }


  /**
   * Combines three images (red, green, and blue) into a single image.
   *
   * @param destName           the name of the combined image
   * @param redComponentName   the name of the red component image
   * @param greenComponentName the name of the green component image
   * @param blueComponentName  the name of the blue component image
   */
  public void combine(String destName, String redComponentName, String greenComponentName,
      String blueComponentName) {
    CustomImage redImage = images.get(redComponentName);
    CustomImage greenImage = images.get(greenComponentName);
    CustomImage blueImage = images.get(blueComponentName);

    if (redImage == null || greenImage == null || blueImage == null) {
      System.out.println("One or more component images not found.");
      return;
    }

    // Assuming all the component images have the same dimensions
    CustomImage combinedImage = new CustomImage(redImage.getHeight(), redImage.getWidth());

    // Loop through every pixel
    for (int y = 0; y < redImage.getHeight(); y++) {
      for (int x = 0; x < redImage.getWidth(); x++) {
        // Get the pixel data for each channel
        int[] redPixel = redImage.getPixel(x, y);
        int[] greenPixel = greenImage.getPixel(x, y);
        int[] bluePixel = blueImage.getPixel(x, y);

        // Combine the channels: we only take the first value for each channel
        int red = redPixel[0];       // Red channel (stored at index 0)
        int green = greenPixel[1];   // Green channel (stored at index 1)
        int blue = bluePixel[2];     // Blue channel (stored at index 2)

        // Set the combined pixel in the resulting image
        combinedImage.setPixel(x, y, new int[]{red, green, blue});
      }
    }

    // Add the combined image to the model
    addImage(destName, combinedImage);
    System.out.println("Images combined and added as: " + destName);
  }


  /**
   * Applies a filter to the image, either blurring or sharpening it.
   *
   * @param imageName  the name of the source image
   * @param destName   the name of the destination image file
   * @param isBlur     true to apply a blur filter, false to apply a sharpen filter
   * @param percentage the percentage to show the image
   */
  public void imageFilter(String imageName, String destName, boolean isBlur, int percentage) {
    if ((percentage < 0) || (percentage > 100)) {
      System.out.println("Percentage must be between 0 and 100");
      return;
    }

    CustomImage image = images.get(imageName);
    if (image == null) {
      System.out.println("ERROR: Image not found: " + imageName);
      return;
    }

    CustomImage result = new CustomImage(image.getHeight(), image.getWidth());
    int width = image.getWidth();
    int height = image.getHeight();

    int p = (int) (width * (percentage / 100.0));

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int[] pixel;
        if (x < p) {
          // Apply filter only to a portion of the image based on percentage
          if (isBlur) {
            pixel = applyFilter(image, x, y, blurFilter(), 16);  // Apply blur
          } else {
            pixel = applyFilter(image, x, y, sharpeningFilter(), 8);  // Apply sharpening
          }
        } else {
          pixel = image.getPixel(x, y);  // Keep the original pixel for the rest
        }
        result.setPixel(x, y, pixel);
      }
    }

    addImage(destName, result);
    if (isBlur) {
      System.out.println(imageName + " Blur completed and added as: " + destName);
    } else {
      System.out.println(imageName + " Sharpening completed and added as: " + destName);
    }
  }


  private int[] applyFilter(CustomImage img, int x, int y, int[][] filter, int factor) {
    int[] result = new int[3];  // For RGB channels
    int filterSize = filter.length;
    int filterWidth = filter[0].length;
    int filterHalf = filterSize / 2;

    for (int i = 0; i < filterSize; i++) {
      for (int j = 0; j < filterWidth; j++) {
        int imgX = x + i - filterHalf;
        int imgY = y + j - filterHalf;
        if (imgX < 0 || imgX >= img.getWidth() || imgY < 0 || imgY >= img.getHeight()) {
          continue;  // Skip out-of-bounds pixels
        }
        int[] pixel = img.getPixel(imgX, imgY);
        for (int channel = 0; channel < 3; channel++) {
          result[channel] += pixel[channel] * filter[i][j];  // Accumulate color values
        }
      }
    }

    for (int channel = 0; channel < 3; channel++) {
      result[channel] = Math.min(255, Math.max(result[channel] / factor, 0));  // Normalize
    }

    return result;  // Return modified pixel values
  }

  private int[][] blurFilter() {
    return new int[][]{
        {1, 2, 1},
        {2, 4, 2},
        {1, 2, 1}
    };
  }


  private int[][] sharpeningFilter() {
    return new int[][]{
        {-1, -1, -1, -1, -1},
        {-1, 2, 2, 2, -1},
        {-1, 2, 8, 2, -1},
        {-1, 2, 2, 2, -1},
        {-1, -1, -1, -1, -1}
    };
  }


  /**
   * Applies a sepia tone effect to the image and saves it as a new image.
   *
   * @param imageName  the name of the source image
   * @param destName   the name of the destination image file
   * @param percentage the percentage to show the image
   */
  public void sepia(String imageName, String destName, int percentage) {

    if ((percentage < 0) || (percentage > 100)) {
      System.out.println("Percentage must be between 0 and 100");
      return;
    }

    CustomImage image = images.get(imageName);
    if (image == null) {
      System.out.println("Image not found: " + imageName);
      return;
    }

    CustomImage sepiaImage = new CustomImage(image.getHeight(), image.getWidth());
    int p = (int) (image.getWidth() * (percentage / 100.0));

    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int[] rgb = image.getPixel(x, y);

        if (x < p) {
          sepiaImage.setPixel(x, y, applySepia(rgb));
        } else {
          sepiaImage.setPixel(x, y, rgb);
        }
      }
    }

    addImage(destName, sepiaImage);
    System.out.println("Sepia filter applied to " + imageName + " and saved as: " + destName);
  }

  private int[] applySepia(int[] rgb) {
    double[][] sepiaMatrix = {
        {0.393, 0.769, 0.189},
        {0.349, 0.686, 0.168},
        {0.272, 0.534, 0.131}
    };

    int[] newRgb = new int[3];
    for (int i = 0; i < 3; i++) {
      double transformedValue = 0;
      for (int j = 0; j < 3; j++) {
        transformedValue += rgb[j] * sepiaMatrix[i][j];
      }
      newRgb[i] = Math.min(255, Math.max(0, (int) transformedValue)); // Clamp values
    }
    return newRgb;
  }

  /**
   * Applies a sepia tone effect to the image and saves it as a new image.
   *
   * @param imageName  the name of the source image
   * @param destName   the name of the destination image file
   * @param percentage the percentage to show the image
   */
  @Override
  public void greyscale(String imageName, String destName, int percentage) {
    if ((percentage < 0) || (percentage > 100)) {
      System.out.println("Percentage must be between 0 and 100");
      return;
    }

    CustomImage image = images.get(imageName);
    if (image == null) {
      System.out.println("Image not found: " + imageName);
      return;
    }

    CustomImage greyscaleImage = new CustomImage(image.getHeight(), image.getWidth());
    int p = (int) (image.getWidth() * (percentage / 100.0));

    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int[] rgb = image.getPixel(x, y);

        if (x < p) {
          int[] newRgb = extractLuma(rgb);
          greyscaleImage.setPixel(x, y, newRgb);
        } else {
          greyscaleImage.setPixel(x, y, rgb);
        }
      }
    }

    addImage(destName, greyscaleImage);
    System.out.println("Greyscale filter applied to " + imageName + " and saved as: " + destName);
  }


  /**
   * Generates and saves a histogram image based on the specified image.
   *
   * @param imageName the name of the source image
   * @param destName  the name of the destination histogram image file
   */
  public void histogramImage(String imageName, String destName) {

    CustomImage img = images.get(imageName);
    if (img == null) {
      System.out.println("Image not found: " + imageName);
      return;
    }

    int[] redCounts = new int[256];
    int[] greenCounts = new int[256];
    int[] blueCounts = new int[256];
    calculateHistogram(img, redCounts, greenCounts, blueCounts);

    int width = 256;
    int height = 256;
    BufferedImage histogramImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = histogramImage.createGraphics();

    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);

    // Find the max counts for each channel
    int maxRed = 0;
    int maxGreen = 0;
    int maxBlue = 0;
    for (int i = 0; i < 256; i++) {
      maxRed = Math.max(maxRed, redCounts[i]);
      maxGreen = Math.max(maxGreen, greenCounts[i]);
      maxBlue = Math.max(maxBlue, blueCounts[i]);
    }

    // Set scale factors for each channel
    double redScaleFactor = maxRed == 0 ? 0 : (height - 10) / (double) maxRed;
    double greenScaleFactor = maxGreen == 0 ? 0 : (height - 10) / (double) maxGreen;
    double blueScaleFactor = maxBlue == 0 ? 0 : (height - 10) / (double) maxBlue;

    g.setStroke(new BasicStroke(1));

    for (int i = 0; i < 255; i++) {
      // Calculate scaled heights for each channel
      int redHeight1 = (int) (redCounts[i] * redScaleFactor);
      int greenHeight1 = (int) (greenCounts[i] * greenScaleFactor);
      int blueHeight1 = (int) (blueCounts[i] * blueScaleFactor);

      int redHeight2 = (int) (redCounts[i + 1] * redScaleFactor);
      int greenHeight2 = (int) (greenCounts[i + 1] * greenScaleFactor);
      int blueHeight2 = (int) (blueCounts[i + 1] * blueScaleFactor);

      // Draw each channel independently
      if (redHeight1 > 0 || redHeight2 > 0) {
        g.setColor(Color.RED);
        g.drawLine(i, height - redHeight1 - 1, i + 1, height - redHeight2);
      }

      if (greenHeight1 > 0 || greenHeight2 > 0) {
        g.setColor(Color.GREEN);
        g.drawLine(i, height - greenHeight1 - 1, i + 1, height - greenHeight2);
      }

      if (blueHeight1 > 0 || blueHeight2 > 0) {
        g.setColor(Color.BLUE);
        g.drawLine(i, height - blueHeight1 - 1, i + 1, height - blueHeight2);
      }
    }

    g.dispose();

    CustomImage resultingHistogram = convertToCustomImage(histogramImage, height, width);

    addImage(destName, resultingHistogram);
    System.out.println("Histogram Created for " + imageName + " saved as: " + destName);
  }

  private void calculateHistogram(CustomImage image, int[] redCounts, int[] greenCounts,
      int[] blueCounts) {
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int[] pixel = image.getPixel(x, y);
        redCounts[pixel[0]]++;
        greenCounts[pixel[1]]++;
        blueCounts[pixel[2]]++;
      }
    }
  }

  private CustomImage convertToCustomImage(BufferedImage histogramImage, int height, int width) {
    CustomImage resultingHistogram = new CustomImage(height, width);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int rgb = histogramImage.getRGB(x, y);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        resultingHistogram.setPixel(x, y, new int[]{red, green, blue});
      }
    }
    return resultingHistogram;
  }


  /**
   * Performs color correction on the image and saves it as a new image.
   *
   * @param imageName  the name of the source image
   * @param destName   the name of the destination image file
   * @param percentage the percentage to show the image
   */
  public void colorCorrection(String imageName, String destName, int percentage) {

    if (percentage < 0 || percentage > 100) {
      System.out.println("Percentage must be between 0 and 100");
      return;
    }

    CustomImage image = images.get(imageName);
    if (image == null) {
      System.out.println("Image not found: " + imageName);
      return;
    }

    int[] redCounts = new int[256];
    int[] greenCounts = new int[256];
    int[] blueCounts = new int[256];
    calculateHistogram(image, redCounts, greenCounts, blueCounts);

    // Find peaks for red, green, and blue channels
    int redPeak = findPeak(redCounts);
    int greenPeak = findPeak(greenCounts);
    int bluePeak = findPeak(blueCounts);

    // Calculate the average peak and offsets for each channel
    int averagePeak = (redPeak + greenPeak + bluePeak) / 3;
    int redOffset = averagePeak - redPeak;
    int greenOffset = averagePeak - greenPeak;
    int blueOffset = averagePeak - bluePeak;

    CustomImage correctedImage = new CustomImage(image.getHeight(), image.getWidth());

    // Calculate the correction boundary
    int correctionBoundary = (int) (image.getWidth() * (percentage / 100.0));

    // Apply color correction to the image
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int[] pixel = image.getPixel(x, y);

        // Apply correction within the correction boundary
        if (x < correctionBoundary) {
          // Adjust the color channels with the calculated offsets
          int red = clamp(pixel[0] + redOffset);
          int green = clamp(pixel[1] + greenOffset);
          int blue = clamp(pixel[2] + blueOffset);

          correctedImage.setPixel(x, y, new int[]{red, green, blue});
        } else {
          // Leave the pixel unchanged if outside the correction boundary
          correctedImage.setPixel(x, y, pixel);
        }
      }
    }

    // Save the corrected image
    images.put(destName, correctedImage);
    System.out.println("Color corrected image saved as: " + destName);
  }


  private int findPeak(int[] channelCounts) {
    int peak = -1;
    int maxCount = 0;
    // Use the full range 0 to 255 to find the peak
    for (int i = 0; i < 256; i++) {
      if (channelCounts[i] > maxCount) {
        maxCount = channelCounts[i];
        peak = i;
      }
    }
    return peak;
  }


  private int clamp(int value) {
    return Math.max(0, Math.min(255, value));
  }

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
  public void levelsAdjust(int b, int m, int w, String imageName, String destImageName,
      int percentage) {

    if (b < 0 || m < 0 || w < 0 || w > 255 || b >= m || m >= w) {
      System.out.println("Invalid input values. Ensure 0 <= b < m < w <= 255.");
      return;
    }

    if (percentage < 0 || percentage > 100) {
      System.out.println("Percentage must be between 0 and 100");
      return;
    }

    CustomImage image = images.get(imageName);
    if (image == null) {
      System.out.println("Image not found: " + imageName);
      return;
    }

    CustomImage adjustedImage = new CustomImage(image.getHeight(), image.getWidth());
    int p = (int) (image.getWidth() * (percentage / 100.0));
    double a = aCalculate(b, m, w);
    double b2 = bCalculate(b, m, w);
    double c = cCalculate(b, m, w);

    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int[] pixel = image.getPixel(x, y);
        int[] adjustedPixel;

        if (x < p) {
          adjustedPixel = new int[3];
          for (int channel = 0; channel < 3; channel++) {
            adjustedPixel[channel] = applyLevelsAdjustment(pixel[channel], b, w, a, b2, c);
          }
        } else {
          adjustedPixel = pixel;
        }
        adjustedImage.setPixel(x, y, adjustedPixel);
      }
    }

    images.put(destImageName, adjustedImage);
    System.out.println("Levels adjusted image saved as: " + destImageName);
  }

  private int applyLevelsAdjustment(int value, int b, int w, double a, double b2, double c) {
    int result;
    if (value <= b) {
      return 0;
    } else if (value >= w) {
      return 255;
    } else {
      result = (int) (a * value * value + b2 * value + c);
    }
    return Math.min(255, Math.max(0, result));
  }

  private double cCalculate(int b, int m, int w) {
    double a = calculateA(b, m, w);
    double ac = b * b * (255 * m - 128 * w) - b * (255 * m * m - 128 * w * w);
    return ac / a;
  }

  private double bCalculate(int b, int m, int w) {
    double a = calculateA(b, m, w);
    double ab = b * b * (128 - 255) + 255 * m * m - 128 * w * w;
    return ab / a;
  }

  private double aCalculate(int b, int m, int w) {
    double a = calculateA(b, m, w);
    double aa = -b * (128 - 255) + 128 * w - 255 * m;
    return aa / a;
  }

  private double calculateA(int b, int m, int w) {
    return (b * b * (m - w) - b * (m * m - w * w) + w * m * m - m * w * w);
  }

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
  public void imageCompression(String imageName, String destImageName, int percentage) {
    if (percentage < 0 || percentage > 100) {
      System.out.println("Percentage must be between 0 and 100");
      return;
    }

    CustomImage image;
    image = images.get(imageName);
    if (image == null) {
      System.out.println("Image not found: " + imageName);
      return;
    }

    int originalHeight = image.getHeight();
    int originalWidth = image.getWidth();

    image = padToSquarePowerOfTwo(image);

    int[][][] pixels = image.getPixels();
    int size = image.getHeight();

    // Apply Haar transform to each color channel
    for (int channel = 0; channel < 3; channel++) {
      haarTransform2D(pixels, size, channel);
    }

    // Zero out smallest coefficients based on the compression percentage
    applyThresholding(pixels, size, percentage / 100.00);

    // Apply inverse Haar transform
    for (int channel = 0; channel < 3; channel++) {
      inverseHaarTransform2D(pixels, size, channel);
    }

    CustomImage compressedImage = cropToOriginalSize(pixels, originalWidth, originalHeight);
    images.put(destImageName, compressedImage);
    System.out.println("Compressed image saved as: " + destImageName);
  }

  private void inverseHaarTransform2D(int[][][] pixels, int size, int channel) {
    int currentSize = 2;

    // Inverse Haar Transform for the 2D image (first on columns, then on rows)
    while (currentSize <= size) {
      // Process each column
      for (int j = 0; j < currentSize; j++) {  // Ensuring we cover the entire width
        int[] col = new int[currentSize];
        // Copy the current column into a temporary array
        for (int i = 0; i < currentSize; i++) {
          col[i] = pixels[i][j][channel];
        }
        // Apply inverse 1D Haar transform to the column
        invert(col, currentSize);
        // Copy the transformed column back into the pixels array
        for (int i = 0; i < currentSize; i++) {
          pixels[i][j][channel] = col[i];
        }
      }

      // Process each row
      for (int i = 0; i < currentSize; i++) {  // Ensuring we cover the entire height
        int[] row = new int[currentSize];
        // Copy the current row into a temporary array
        for (int j = 0; j < currentSize; j++) {
          row[j] = pixels[i][j][channel];
        }
        // Apply inverse 1D Haar transform to the row
        invert(row, currentSize);
        // Copy the transformed row back into the pixels array
        for (int j = 0; j < currentSize; j++) {
          pixels[i][j][channel] = row[j];
        }
      }

      currentSize *= 2;
    }
  }

  private void invert(int[] array, int length) {
    int[] temp = new int[length];

    int halfLength = length / 2;
    // Perform inverse Haar transform to restore original values
    for (int i = 0; i < halfLength; i++) {
      int average = array[i];
      int difference = array[halfLength + i];

      // Calculate original values from average and difference
      temp[2 * i] = (int) ((average + difference) / Math.sqrt(2));
      temp[2 * i + 1] = (int) ((average - difference) / Math.sqrt(2));
    }

    System.arraycopy(temp, 0, array, 0, length);
  }

  private void applyThresholding(int[][][] pixels, int size, double compressionRatio) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();

    // Flatten the pixel data and store absolute values in the heap for thresholding
    int nonZeroCount = 0;
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        for (int channel = 0; channel < 3; channel++) {
          if (pixels[y][x][channel] != 0) {
            minHeap.add(Math.abs(pixels[y][x][channel]));
            nonZeroCount++;
          }
        }
      }
    }
    // Determine the threshold size based on the compression ratio
    int thresholdSize = (int) (nonZeroCount * (1 - compressionRatio));

    // Keep only the largest values up to thresholdSize, to discard the smallest
    while (minHeap.size() > thresholdSize) {
      minHeap.poll();
    }

    // The root of the heap is now the threshold value for compression
    int thresholdValue = minHeap.peek();

    // Apply thresholding: Set all values below threshold to zero
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        for (int channel = 0; channel < 3; channel++) {
          if (Math.abs(pixels[y][x][channel]) <= thresholdValue) {
            pixels[y][x][channel] = 0;
          }
        }
      }
    }
  }

  private void haarTransform2D(int[][][] pixels, int size, int channel) {
    int currentSize = size;

    while (currentSize > 1) {

      // Transform rows
      for (int i = 0; i < currentSize; i++) {
        int[] row = new int[currentSize];
        for (int j = 0; j < currentSize; j++) {
          row[j] = pixels[i][j][channel];
        }

        // Apply 1D Haar transform to the row
        transform1D(row, currentSize);

        // Copy the transformed row back into the pixels array
        for (int j = 0; j < currentSize; j++) {
          pixels[i][j][channel] = row[j];
        }
      }

      // Transform columns
      for (int j = 0; j < currentSize; j++) {
        int[] col = new int[currentSize];
        for (int i = 0; i < currentSize; i++) {
          col[i] = pixels[i][j][channel];
        }

        // Apply 1D Haar transform to the column
        transform1D(col, currentSize);

        // Copy the transformed column back into the pixels array
        for (int i = 0; i < currentSize; i++) {
          pixels[i][j][channel] = col[i];
        }
      }

      // Halve the dimensions for the next iteration
      currentSize /= 2;
    }
  }

  private void transform1D(int[] array, int length) {
    int[] temp = new int[length];

    int halfLength = length / 2;

    for (int i = 0; i < halfLength; i++) {
      temp[i] = (int) ((array[2 * i] + array[2 * i + 1]) / Math.sqrt(2));
      temp[halfLength + i] = (int) ((array[2 * i] - array[2 * i + 1]) / Math.sqrt(
          2));
    }

    System.arraycopy(temp, 0, array, 0, length);
  }

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
  public void partialImageManipulation(String command, String imageName, String maskImageName,
      String destName) {

    CustomImage image = images.get(imageName);
    CustomImage maskImage = images.get(maskImageName);
    if (image == null) {
      System.out.println("Image not found: " + imageName);
      return;
    } else if (maskImage == null) {
      System.out.println("Image not found: " + maskImageName);
      return;
    }
    if (image.getWidth() != maskImage.getWidth() || image.getHeight() != maskImage.getHeight()) {
      System.out.println("Image and Mask Image are not the same Image!");
      return;
    }

    CustomImage resultImage = new CustomImage(image.getHeight(), image.getWidth());
    // Initialize with original image
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        resultImage.setPixel(x, y, image.getPixel(x, y));
      }
    }

    // Create mask array
    boolean[][] isBlackPixel = new boolean[maskImage.getHeight()][maskImage.getWidth()];
    for (int y = 0; y < maskImage.getHeight(); y++) {
      for (int x = 0; x < maskImage.getWidth(); x++) {
        int[] pixel = maskImage.getPixel(x, y);
        isBlackPixel[y][x] = (pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0);
      }
    }

    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if (isBlackPixel[y][x]) {
          int[] rgb = image.getPixel(x, y);
          switch (command) {
            case "blur":
              resultImage.setPixel(x, y, applyFilter(image, x, y, blurFilter(), 16));
              break;

            case "sharpen":
              resultImage.setPixel(x, y, applyFilter(image, x, y, sharpeningFilter(), 8));
              break;

            case "greyscale":
              resultImage.setPixel(x, y, extractLuma(rgb));
              break;

            case "sepia":
              resultImage.setPixel(x, y, applySepia(rgb));
              break;

            case "red-component":
              resultImage.setPixel(x, y, extractRed(rgb));
              break;

            case "green-component":
              resultImage.setPixel(x, y, extractGreen(rgb));
              break;

            case "blue-component":
              resultImage.setPixel(x, y, extractBlue(rgb));
              break;

            case "value-component":
              resultImage.setPixel(x, y, extractValue(rgb));
              break;

            case "luma-component":
              resultImage.setPixel(x, y, extractLuma(rgb));
              break;

            case "intensity-component":
              resultImage.setPixel(x, y, extractIntensity(rgb));
              break;
            default:
              System.out.println("Unknown command: " + command);
              return;
          }
        }
      }
    }

    addImage(destName, resultImage);
    System.out.println("Partial Image Manipulation Successfully completed! Saved as: " + destName);
  }

  /**
   * Downscales an image to the specified width and height. Saves the image with a new name.
   *
   * @param imageName The name of the image to be downscaled.
   * @param destName  The name under which the downscaled image will be saved.
   * @param newWidth  The desired width of the downscaled image.
   * @param newHeight The desired height of the downscaled image.
   */
  public void downscaleImage(String imageName, String destName, int newWidth, int newHeight) {
    CustomImage originalImage = images.get(imageName);
    if (originalImage == null) {
      System.out.println("Image not found: " + imageName);
      return;
    }
    CustomImage downscaledImage = new CustomImage(newHeight, newWidth);
    int originalWidth = originalImage.getWidth();
    int originalHeight = originalImage.getHeight();
    double xScale = (double) originalWidth / newWidth;
    double yScale = (double) originalHeight / newHeight;

    for (int yDest = 0; yDest < newHeight; yDest++) {
      for (int xDest = 0; xDest < newWidth; xDest++) {
        double xSrc = xDest * xScale;
        double ySrc = yDest * yScale;

        int x1 = (int) Math.floor(xSrc);
        int y1 = (int) Math.floor(ySrc);
        int x2 = Math.min(x1 + 1, originalWidth - 1);
        int y2 = Math.min(y1 + 1, originalHeight - 1);

        // Handle edge cases by clamping to the image boundaries
        if (x1 < 0) {
          x1 = 0;
        }
        if (y1 < 0) {
          y1 = 0;
        }

        // Calculate the fractional part
        double xFrac = xSrc - x1;
        double yFrac = ySrc - y1;

        // Retrieve pixel colors
        int[] pixel11 = originalImage.getPixel(x1, y1);
        int[] pixel21 = originalImage.getPixel(x2, y1);
        int[] pixel12 = originalImage.getPixel(x1, y2);
        int[] pixel22 = originalImage.getPixel(x2, y2);
        int[] newPixel = new int[3];
        for (int channel = 0; channel < 3; channel++) {
          double top = pixel11[channel] * (1 - xFrac) + pixel21[channel] * xFrac;
          double bottom = pixel12[channel] * (1 - xFrac) + pixel22[channel] * xFrac;
          newPixel[channel] = clamp((int) Math.round(top * (1 - yFrac) + bottom * yFrac));
        }
        downscaledImage.setPixel(xDest, yDest, newPixel);
      }
    }
    addImage(destName, downscaledImage);
    System.out.println("Downscaled image saved as: " + destName);
  }
}