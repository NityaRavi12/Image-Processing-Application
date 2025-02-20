/**
 * Represents an image with RGB pixel data. This class stores pixel data as a 3D array where each
 * pixel consists of RGB values. It allows for setting and getting pixel values, as well as
 * manipulating and retrieving the entire pixel matrix. The image is initialized with a width and
 * height, and all pixels are set to default RGB values (0, 0, 0). Each pixel's RGB values are
 * clamped to the range [0, 255]. This class implements the Image interface, providing essential
 * operations for working with images.
 */
public class CustomImage implements CustomImageInterface {

  private final int width;
  private final int height;
  private final int[][][] pixels;

  /**
   * Constructs a CustomImage with the specified height and width. Initializes the pixel array with
   * default RGB values (0, 0, 0).
   *
   * @param height the height of the image
   * @param width  the width of the image
   */
  public CustomImage(int height, int width) {
    this.width = width;
    this.height = height;
    this.pixels = new int[height][width][3]; // Store RGB values
  }

  /**
   * Gets the RGB values of the pixel at the specified coordinates.
   *
   * @param x the x-coordinate of the pixel
   * @param y the y-coordinate of the pixel
   * @return an array of 3 integers representing the RGB values of the pixel
   * @throws IllegalArgumentException if the coordinates are out of bounds
   */
  public int[] getPixel(int x, int y) {
    if (x < 0 || x >= width || y < 0 || y >= height) {
      throw new IllegalArgumentException("Pixel coordinates are out of bounds.");
    }
    return pixels[y][x].clone();
  }

  /**
   * Sets the RGB values of the pixel at the specified coordinates.
   *
   * @param x   the x-coordinate of the pixel
   * @param y   the y-coordinate of the pixel
   * @param rgb an array of 3 integers representing the new RGB values
   * @throws IllegalArgumentException if the coordinates are out of bounds
   */
  public void setPixel(int x, int y, int[] rgb) {
    if (x < 0 || x >= width || y < 0 || y >= height) {
      throw new IllegalArgumentException("Pixel coordinates are out of bounds.");
    }
    if (rgb.length != 3) {
      throw new IllegalArgumentException("RGB array should have three elements.");
    }
    pixels[y][x] = new int[]{clamp(rgb[0]), clamp(rgb[1]), clamp(rgb[2])};
  }

  /**
   * Clamps the given value to be within the 0 to 255 range.
   *
   * @param value the value to clamp
   * @return the clamped value
   */
  private int clamp(int value) {
    return Math.max(0, Math.min(value, 255));
  }

  /**
   * Retrieves the entire pixel array for the image.
   *
   * @return a 3D array containing the RGB values for all pixels in the image
   */
  public int[][][] getPixels() {
    int[][][] copy = new int[height][width][3];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        copy[i][j] = pixels[i][j].clone();
      }
    }
    return copy;
  }

  /**
   * Sets the pixel data for the image from a 3D array.
   *
   * @param newPixels a 3D array containing the new pixel data
   * @throws IllegalArgumentException if the dimensions of the new pixel array do not match the
   *                                  current image dimensions
   */
  public void setPixels(int[][][] newPixels) {
    if (newPixels.length != height || newPixels[0].length != width) {
      throw new IllegalArgumentException("New pixels array dimensions do not match.");
    }
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        this.pixels[i][j] = new int[]{
            clamp(newPixels[i][j][0]),
            clamp(newPixels[i][j][1]),
            clamp(newPixels[i][j][2])
        };
      }
    }
  }

  /**
   * Gets the width of the image.
   *
   * @return the width of the image
   */
  public int getWidth() {
    return width;
  }

  /**
   * Gets the height of the image.
   *
   * @return the height of the image
   */
  public int getHeight() {
    return height;
  }
}