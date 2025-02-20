import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for CustomImage.
 */
public class CustomImageTest {

  private CustomImage image;

  @Before
  public void setUp() {
    image = new CustomImage(10, 10);
  }

  @Test
  public void testConstructor() {
    int expectedHeight = 10;
    int expectedWidth = 10;

    int actualHeight = image.getHeight();
    int actualWidth = image.getWidth();
    int[][][] pixelArray = image.getPixels();
    assertEquals(expectedHeight, actualHeight);
    assertEquals(expectedWidth, actualWidth);
    assertNotNull(pixelArray);
    assertEquals(expectedHeight, pixelArray.length);
    assertEquals(expectedWidth, pixelArray[0].length);
  }

  @Test
  public void testSetPixelOutOfBounds() {
    try {
      image.setPixel(-2, -2, new int[]{255, 0, 0});
      fail("Expected IllegalArgumentException not thrown");
    } catch (IllegalArgumentException e) {
      assertNotNull(e.getMessage());
    }
  }


  @Test(expected = IllegalArgumentException.class)
  public void testGetPixelOutOfBounds() {
    image.getPixel(10, 10);
  }

  @Test
  public void testSetPixelValid() {
    image.setPixel(4, 4, new int[]{255, 0, 0});
    assertArrayEquals(new int[]{255, 0, 0}, image.getPixel(4, 4));
  }


  @Test
  public void testSetPixels() {
    int[][][] expectedPixels = createTestPixels(10, 10);
    image.setPixels(expectedPixels);

    int[][][] actualPixels = image.getPixels();
    for (int i = 0; i < expectedPixels.length; i++) {
      for (int j = 0; j < expectedPixels[0].length; j++) {
        assertArrayEquals("Pixels at (" + i + "," + j + ") should match",
            expectedPixels[i][j], actualPixels[i][j]);
      }
    }
  }

  private int[][][] createTestPixels(int height, int width) {
    int[][][] pixels = new int[height][width][3];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        pixels[i][j] = new int[]{
            clamp(i * 51),
            clamp(j * 51),
            clamp(0)
        };
      }
    }
    return pixels;
  }

  private int clamp(int value) {
    return Math.max(0, Math.min(value, 255));
  }

  @Test
  public void testGetPixels() {
    int[][][] pixels = image.getPixels();
    assertNotNull(pixels);
    assertEquals(10, pixels.length);
    assertEquals(10, pixels[0].length);
    assertArrayEquals(new int[]{0, 0, 0}, pixels[0][0]);
  }

  @Test
  public void testGetWidthAndHeight() {
    assertEquals("Width should be 10", 10, image.getWidth());
    assertEquals("Height should be 10", 10, image.getHeight());
  }

  @Test
  public void testSetAndGetEdgePixels() {
    int[] color = {128, 128, 128};
    image.setPixel(0, 0, color);
    image.setPixel(9, 9, color);

    assertArrayEquals("Top-left pixel must match the color", color,
        image.getPixel(0, 0));
    assertArrayEquals("Bottom-right pixel must match the color", color,
        image.getPixel(9, 9));
  }

  @Test
  public void testClamping() {
    int[] overflowColor = {300, -10, 260};
    int[] clampedColor = {255, 0, 255};
    image.setPixel(3, 3, overflowColor);
    assertArrayEquals("Pixel color should be clamped", clampedColor, image.getPixel(3, 3));
  }

  @Test
  public void testSetMultiplePixels() {
    int[] color1 = {100, 150, 200};
    int[] color2 = {0, 255, 0};

    image.setPixel(1, 1, color1);
    image.setPixel(2, 2, color2);

    assertArrayEquals("Pixel (1, 1) should match color1", color1, image.getPixel(1, 1));
    assertArrayEquals("Pixel (2, 2) should match color2", color2, image.getPixel(2, 2));
  }

  @Test
  public void testSetPixel255() {
    int[] color = {255, 255, 255};
    image.setPixel(5, 5, color);
    assertArrayEquals(color, image.getPixel(5, 5));
  }

  @Test
  public void testBoundaryPixels() {
    int[] topLeftColor = {1, 2, 3};
    int[] bottomRightColor = {4, 5, 6};
    image.setPixel(0, 0, topLeftColor);
    image.setPixel(9, 9, bottomRightColor);

    assertArrayEquals("Top-left pixel should match", topLeftColor, image.getPixel(0, 0));
    assertArrayEquals("Bottom-right pixel should match", bottomRightColor, image.getPixel(9, 9));
  }

  @Test
  public void testPixelIntegrity() {
    int[][][] originalPixels = createTestPixels(10, 10);
    image.setPixels(originalPixels);
    int[][][] retrievedPixels = image.getPixels();

    retrievedPixels[0][0][0] = 999;
    assertArrayEquals("Original pixel data should not change",
        originalPixels[0][0], image.getPixel(0, 0));
  }
}
