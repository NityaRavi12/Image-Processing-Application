/**
 * Interface representing the essential operations for an image. Provides methods to access and
 * manipulate individual pixels, retrieve and set pixel data in bulk, and obtain image dimensions.
 * This interface assumes an RGB color model with each pixel represented by an array of three
 * integer values for the red, green, and blue channels.
 */
public interface CustomImageInterface {

  /**
   * Gets the RGB values of the pixel at the specified coordinates.
   *
   * @param x the x-coordinate of the pixel
   * @param y the y-coordinate of the pixel
   * @return an array of 3 integers representing the RGB values of the pixel
   * @throws IllegalArgumentException if the coordinates are out of bounds
   */
  int[] getPixel(int x, int y);

  /**
   * Sets the RGB values of the pixel at the specified coordinates.
   *
   * @param x   the x-coordinate of the pixel
   * @param y   the y-coordinate of the pixel
   * @param rgb an array of 3 integers representing the new RGB values
   * @throws IllegalArgumentException if the coordinates are out of bounds
   */
  void setPixel(int x, int y, int[] rgb);

  /**
   * Retrieves the entire pixel array for the image.
   *
   * @return a 3D array containing the RGB values for all pixels in the image
   */
  int[][][] getPixels();

  /**
   * Sets the pixel data for the image from a 3D array.
   *
   * @param newPixels a 3D array containing the new pixel data
   * @throws IllegalArgumentException if the dimensions of the new pixel array do not match the
   *                                  current image dimensions
   */
  void setPixels(int[][][] newPixels);

  /**
   * Gets the width of the image.
   *
   * @return the width of the image
   */
  int getWidth();

  /**
   * Gets the height of the image.
   *
   * @return the height of the image
   */
  int getHeight();
}
