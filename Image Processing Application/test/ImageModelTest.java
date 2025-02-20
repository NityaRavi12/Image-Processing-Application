import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for ImageModel.
 */
public class ImageModelTest {

  private ImageModel model;
  private CustomImage testImage;

  @Before
  public void setUp() {
    model = new ImageModel();
    int[][][] pixels = {
        {{255, 0, 0}, {0, 255, 0}, {0, 0, 255}},
        {{255, 255, 0}, {0, 255, 255}, {255, 0, 255}},
        {{128, 128, 128}, {64, 64, 64}, {192, 192, 192}}
    };
    testImage = new CustomImage(3, 3);  // 3x3 image
    testImage.setPixels(pixels);

    model.addImage("testImage", testImage);


  }

  @Test
  public void testGreyscale_InvalidPercentage_High() {
    try {
      model.greyscale("testImage", "greyscaleInvalidHigh", 150);

      CustomImage greyscaleImage = model.getImage("greyscaleInvalidHigh");
      assertNull("Greyscale image should not be created for invalid percentage"
          , greyscaleImage);

    } catch (Exception e) {
      fail("Greyscale conversion failed: " + e.getMessage());
    }
  }

  @Test
  public void testGreyscale_ZeroPercentage() {
    try {
      model.greyscale("testImage", "greyscaleZero", 0);

      CustomImage greyscaleImage = model.getImage("greyscaleZero");

      assertNotNull("Greyscale image should be created", greyscaleImage);
      assertTrue("Greyscale image should have non-zero width and height",
          greyscaleImage.getWidth() > 0 && greyscaleImage.getHeight() > 0);

      int[] originalPixel = testImage.getPixel(0, 0);
      int[] pixel1 = greyscaleImage.getPixel(0, 0);

      assertArrayEquals("Pixel should remain unchanged at 0%"
          , originalPixel, pixel1);
    } catch (Exception e) {
      fail("Greyscale conversion failed: " + e.getMessage());
    }
  }

  @Test
  public void testAddImage() {
    CustomImage loadedImage = model.getImage("testImage");
    assertNotNull("Image should be successfully added to the model", loadedImage);
    assertEquals("Width of loaded image should be 3", 3
        , loadedImage.getWidth());
    assertEquals("Height of loaded image should be 3", 3
        , loadedImage.getHeight());
  }

  @Test
  public void testBrightenImage() {
    model.adjustBrightness(50, "testImage", "brightenedImage"
        , true);
    CustomImage brightenedImage = model.getImage("brightenedImage");

    int[] originalPixel = testImage.getPixel(0, 0);
    int[] brightenedPixel = brightenedImage.getPixel(0, 0);
    System.out.println("Original pixel: " + Arrays.toString(originalPixel));
    System.out.println("Brightened pixel: " + Arrays.toString(brightenedPixel));

    assertEquals("Red channel should stay at 255", 255, brightenedPixel[0]);

    assertEquals("Green channel should have increased by 50"
        , originalPixel[1] + 50, brightenedPixel[1]);
    assertEquals("Blue channel should have increased by 50"
        , originalPixel[2] + 50, brightenedPixel[2]);
  }

  @Test
  public void testGreyscale_100Percentage() {
    try {
      model.greyscale("testImage", "greyscale100", 100);
      CustomImage greyscaleImage = model.getImage("greyscale100");

      assertNotNull("Greyscale image should be created", greyscaleImage);
      assertTrue("Greyscale image should have non-zero width and height",
          greyscaleImage.getWidth() > 0 && greyscaleImage.getHeight() > 0);

      int[] pixel1 = greyscaleImage.getPixel(0, 0);
      assertEquals("Pixel 1 should be greyscaled", pixel1[0], pixel1[1]);
      assertEquals("Pixel 1 should be greyscaled", pixel1[1], pixel1[2]);

      pixel1 = greyscaleImage.getPixel(0, 1);
      assertEquals("Pixel 2 should be greyscaled", pixel1[0], pixel1[1]);
      assertEquals("Pixel 2 should be greyscaled", pixel1[1], pixel1[2]);
    } catch (Exception e) {
      fail("Greyscale conversion failed: " + e.getMessage());
    }
  }

  @Test
  public void testDarkenImage() {
    model.adjustBrightness(50, "testImage", "darkenedImage"
        , false);  // Darken by 50
    CustomImage darkenedImage = model.getImage("darkenedImage");

    int[] originalPixel = testImage.getPixel(0, 0);
    int[] darkenedPixel = darkenedImage.getPixel(0, 0);

    System.out.println("Original pixel: " + Arrays.toString(originalPixel));
    System.out.println("Darkened pixel: " + Arrays.toString(darkenedPixel));

    assertTrue("Brightness should have decreased"
        , darkenedPixel[0] < originalPixel[0]);
    assertEquals("Green channel should not be modified", darkenedPixel[1],
        originalPixel[1]);
    assertEquals("Blue channel should not be modified", darkenedPixel[2],
        originalPixel[2]);

    assertEquals("Red channel should decrease by 50", 205, darkenedPixel[0]);
  }


  @Test
  public void testGreyscale() {
    model.greyscale("testImage", "greyscaleImage", 100);
    CustomImage greyscaleImage = model.getImage("greyscaleImage");

    int[] greyscalePixel = greyscaleImage.getPixel(0, 0);
    assertEquals("Greyscale pixel should have equal RGB values"
        , greyscalePixel[0], greyscalePixel[1]);
    assertEquals("Greyscale pixel should have equal RGB values"
        , greyscalePixel[0], greyscalePixel[2]);
  }

  @Test
  public void testFlip() {
    model.flipImage("testImage", "flippedImage", true);
    CustomImage flippedImage = model.getImage("flippedImage");
    int[] originalPixel = testImage.getPixel(0, 0);
    int[] flippedPixel = flippedImage.getPixel(2, 0);
    assertArrayEquals("Pixels should be horizontally flipped"
        , originalPixel, flippedPixel);
  }

  @Test
  public void testSharpeningFilter() {
    model.imageFilter("testImage", "sharpenedImage"
        , false, 50); // Applying sharpening to first 50%
    CustomImage sharpenedImage = model.getImage("sharpenedImage");
    assertNotNull("Sharpened image should be created", sharpenedImage);
    int[] originalPixel = testImage.getPixel(0, 0);
    int[] sharpenedPixel = sharpenedImage.getPixel(0, 0);
    assertNotEquals("Pixel values should be sharpened"
        , originalPixel[0], sharpenedPixel[0]);
  }

  @Test
  public void testColorCorrectionWithZeroPercentage() {
    model.colorCorrection("testImage", "correctedImage0", 0);
    CustomImage correctedImage = model.getImage("correctedImage0");
    assertNotNull(correctedImage);
    assertArrayEquals(testImage.getPixels(), correctedImage.getPixels());
  }

  @Test
  public void testColorCorrectionWithGreaterThan100Percentage() {
    model.colorCorrection("testImage", "correctedImageAbove100"
        , 150);

    CustomImage correctedImage = model.getImage("correctedImageAbove100");
    assertNull(correctedImage);
  }

  @Test
  public void testColorCorrectionWithNegativePercentage() {
    model.colorCorrection("testImage", "correctedImageNegative"
        , -10);

    CustomImage correctedImage = model.getImage("correctedImageNegative");
    assertNull(correctedImage);
  }

  @Test
  public void testColorCorrectionWith50Percentage() {

    model.colorCorrection("testImage", "correctedImage50", 50);
    CustomImage correctedImage = model.getImage("correctedImage50");
    assertNotNull(correctedImage);

    assertNotEquals(testImage.getPixels(), correctedImage.getPixels());
    int[][][] pixels = correctedImage.getPixels();
    for (int y = 0; y < testImage.getHeight(); y++) {
      for (int x = 0; x < testImage.getWidth() / 2; x++) {
        int[] correctedPixel = pixels[y][x];
      }
    }
  }

  @Test
  public void testColorCorrectionWith100Percentage() {

    model.colorCorrection("testImage", "correctedImage100"
        , 100);
    CustomImage correctedImage = model.getImage("correctedImage100");
    assertNotNull(correctedImage);
    assertNotEquals(testImage.getPixels(), correctedImage.getPixels());
  }


  @Test
  public void testImageBlur() {
    model.imageFilter("testImage", "blurredImage"
        , true, 50);

    CustomImage blurredImage = model.getImage("blurredImage");
    int[] originalPixel = testImage.getPixel(0, 0);
    int[] blurredPixel = blurredImage.getPixel(0, 0);

    assertNotEquals("Blurred pixel should not be the same as original pixel"
        , originalPixel[0], blurredPixel[0]);
    assertNotEquals("Blurred pixel should not be the same as original pixel"
        , originalPixel[1], blurredPixel[1]);
    assertNotEquals("Blurred pixel should not be the same as original pixel"
        , originalPixel[2], blurredPixel[2]);
  }

  @Test
  public void testSepia() {
    model.sepia("testImage", "sepiaImage", 100);
    CustomImage sepiaImage = model.getImage("sepiaImage");

    assertNotNull("Sepia image should be created", sepiaImage);
    int[] originalPixel = testImage.getPixel(0, 0);
    int[] sepiaPixel = sepiaImage.getPixel(0, 0);
    assertNotEquals("Pixel values should be changed by sepia filter"
        , originalPixel[0], sepiaPixel[0]);
  }

  @Test
  public void testHistogramImageGeneration() {
    model.histogramImage("testImage", "testHistogram");
    CustomImage histogramImage = model.getImage("testHistogram");
    assertNotNull("Histogram image should be generated", histogramImage);
    int[] pixel = histogramImage.getPixel(0, 0);
    assertTrue("Histogram pixel values should be in the valid range"
        , pixel[0] >= 0 && pixel[0] <= 255);
    assertTrue("Histogram pixel values should be in the valid range"
        , pixel[1] >= 0 && pixel[1] <= 255);
    assertTrue("Histogram pixel values should be in the valid range"
        , pixel[2] >= 0 && pixel[2] <= 255);
  }

  @Test
  public void testHistogramMaxCountScaling() {
    model.histogramImage("testImage", "testHistogram");
    CustomImage histogramImage = model.getImage("testHistogram");
    assertNotNull("Histogram image should be generated", histogramImage);

    int[] pixel = histogramImage.getPixel(0, 0);
    assertTrue("Histogram pixel values should be scaled and non-zero"
        , pixel[0] > 0 || pixel[1] > 0 || pixel[2] > 0);
  }


  @Test
  public void testColorCorrection_withInvalidPercentage() {
    model.colorCorrection("testImage", "colorCorrectedImage"
        , -1);
    CustomImage correctedImage = model.getImage("colorCorrectedImage");
    assertNull("Image should not be created for invalid percentage", correctedImage);

    model.colorCorrection("testImage", "colorCorrectedImage"
        , 101);
    correctedImage = model.getImage("colorCorrectedImage");
    assertNull("Image should not be created for invalid percentage"
        , correctedImage);
  }

  @Test
  public void testLevelsAdjustWithInvalidInput() {
    model.levelsAdjust(100, 50, 200, "testImage"
        , "adjustedImage", 50);

    CustomImage adjustedImage = model.getImage("adjustedImage");
    assertNull(adjustedImage);
  }

  @Test
  public void testLevelsAdjustWith100PercentAdjustment() {
    model.levelsAdjust(50, 100, 200, "testImage"
        , "adjustedImage", 100);
    CustomImage adjustedImage = model.getImage("adjustedImage");
    assertNotNull(adjustedImage);

    int[] pixel = adjustedImage.getPixel(0, 0);
    assertTrue(pixel[0] >= 0 && pixel[0] <= 255);
    assertTrue(pixel[1] >= 0 && pixel[1] <= 255);
    assertTrue(pixel[2] >= 0 && pixel[2] <= 255);
  }

  @Test
  public void testLevelsAdjustWithInvalidPercentage() {
    model.levelsAdjust(50, 100, 200, "testImage"
        , "adjustedImage", 110);
    CustomImage adjustedImage = model.getImage("adjustedImage");
    assertNull(adjustedImage);
  }

  @Test
  public void testLevelsAdjustWithEdgeCaseValues() {
    model.levelsAdjust(0, 128, 255, "testImage"
        , "adjustedImage", 50);

    CustomImage adjustedImage = model.getImage("adjustedImage");
    assertNotNull(adjustedImage);
    int[] pixel = adjustedImage.getPixel(0, 0);
    assertTrue(pixel[0] >= 0 && pixel[0] <= 255);
    assertTrue(pixel[1] >= 0 && pixel[1] <= 255);
    assertTrue(pixel[2] >= 0 && pixel[2] <= 255);

    pixel = adjustedImage.getPixel(2, 2);
    assertArrayEquals(new int[]{192, 192, 192}, pixel);
  }

  @Test
  public void testLevelsAdjustWithPartialAdjustment() {
    model.levelsAdjust(50, 100, 200, "testImage"
        , "adjustedImage", 30);
    CustomImage adjustedImage = model.getImage("adjustedImage");
    assertNotNull(adjustedImage);
    int[] pixel = adjustedImage.getPixel(0, 0);
    assertTrue(pixel[0] >= 0 && pixel[0] <= 255);
    assertTrue(pixel[1] >= 0 && pixel[1] <= 255);
    assertTrue(pixel[2] >= 0 && pixel[2] <= 255);
    pixel = adjustedImage.getPixel(2, 2);
    assertArrayEquals(new int[]{192, 192, 192}, pixel);
  }

  @Test
  public void testImageCompressionWithValidPercentage() {
    model.imageCompression("testImage", "compressedImage50"
        , 50);
    CustomImage compressedImage = model.getImage("compressedImage50");
    assertNotNull(compressedImage);
    int[][][] compressedPixels = compressedImage.getPixels();
    assertNotNull(compressedPixels);
    assertTrue(compressedPixels.length > 0);
  }

  @Test
  public void testImageCompressionWithInvalidPercentage() {
    model.imageCompression("testImage", "compressedImageInvalid"
        , -10);
    CustomImage compressedImage = model.getImage("compressedImageInvalid");
    assertNull(compressedImage);
  }

  @Test
  public void testImageCompressionWithNonExistentImage() {
    model.imageCompression("nonExistentImage", "compressedImage"
        , 50);
    CustomImage compressedImage = model.getImage("compressedImage");
    assertNull(compressedImage);
  }

  @Test
  public void testImageCompressionWithLargeImageSize() {
    int[][][] pixels = new int[8][8][3];
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        pixels[i][j] = new int[]{i * 30, j * 30, (i + j) * 10};
      }
    }
    CustomImage largeImage = new CustomImage(8, 8);
    largeImage.setPixels(pixels);
    model.addImage("largeImage", largeImage);
    model.imageCompression("largeImage", "compressedLargeImage30"
        , 30);
    CustomImage compressedImage = model.getImage("compressedLargeImage30");
    assertNotNull(compressedImage);
    int[][][] compressedPixels = compressedImage.getPixels();
    assertNotNull(compressedPixels);
    assertTrue(compressedPixels.length > 0);
  }

  @Test
  public void testCombineWithEmptyComponentImages() {

    CustomImage redComponent = new CustomImage(0, 0);
    CustomImage greenComponent = new CustomImage(0, 0);
    CustomImage blueComponent = new CustomImage(0, 0);

    // Add empty images to the model
    model.addImage("redComponent", redComponent);
    model.addImage("greenComponent", greenComponent);
    model.addImage("blueComponent", blueComponent);
    model.combine("combinedImage", "redComponent"
        , "greenComponent", "blueComponent");
    CustomImage combinedImage = model.getImage("combinedImage");
    assertNotNull(combinedImage);
    assertEquals(0, combinedImage.getHeight());
    assertEquals(0, combinedImage.getWidth());
  }

  @Test
  public void testCombineWithNoImagesAdded() {
    model.combine("combinedImage", "redComponent"
        , "greenComponent", "blueComponent");

    CustomImage combinedImage = model.getImage("combinedImage");
    assertNull(combinedImage);

  }

  @Test
  public void testCombineImagesSuccessfully() {
    CustomImage redComponent = new CustomImage(3, 3);
    CustomImage greenComponent = new CustomImage(3, 3);
    CustomImage blueComponent = new CustomImage(3, 3);
    redComponent.setPixels(new int[][][]{
        {{255, 0, 0}, {255, 0, 0}, {255, 0, 0}},
        {{255, 0, 0}, {255, 0, 0}, {255, 0, 0}},
        {{255, 0, 0}, {255, 0, 0}, {255, 0, 0}}
    });

    greenComponent.setPixels(new int[][][]{
        {{0, 255, 0}, {0, 255, 0}, {0, 255, 0}},
        {{0, 255, 0}, {0, 255, 0}, {0, 255, 0}},
        {{0, 255, 0}, {0, 255, 0}, {0, 255, 0}}
    });

    blueComponent.setPixels(new int[][][]{
        {{0, 0, 255}, {0, 0, 255}, {0, 0, 255}},
        {{0, 0, 255}, {0, 0, 255}, {0, 0, 255}},
        {{0, 0, 255}, {0, 0, 255}, {0, 0, 255}}
    });

    model.addImage("redComponent", redComponent);
    model.addImage("greenComponent", greenComponent);
    model.addImage("blueComponent", blueComponent);
    model.combine("combinedImage", "redComponent"
        , "greenComponent", "blueComponent");

    CustomImage combinedImage = model.getImage("combinedImage");
    assertNotNull("The combined image should exist", combinedImage);

    int[][][] pixels = combinedImage.getPixels();
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) {
        int[] combinedPixel = pixels[y][x];
        assertEquals("Red channel at (" + x + ", " + y + ")", 255
            , combinedPixel[0]);
        assertEquals("Green channel at (" + x + ", " + y + ")", 255
            , combinedPixel[1]);
        assertEquals("Blue channel at (" + x + ", " + y + ")", 255
            , combinedPixel[2]);
      }
    }
  }

  @Test
  public void testDownscaleImageInvalidImage() {
    model.downscaleImage("nonExistentImage", "downscaledImage",
        2, 2);

    assertNull(model.getImage("downscaledImage"));
  }

  @Test
  public void testPartialImageManipulationBlur() {
    model.addImage("originalImage", testImage);
    CustomImage maskImage = new CustomImage(3, 3);
    maskImage.setPixels(new int[][][]{
        {{255, 255, 255}, {255, 255, 255}, {255, 255, 255}},
        {{255, 255, 255}, {0, 0, 0}, {255, 255, 255}},
        {{255, 255, 255}, {255, 255, 255}, {255, 255, 255}}
    });
    model.addImage("maskImage", maskImage);

    model.partialImageManipulation("blur", "originalImage",
        "maskImage", "blurredOutput");
    CustomImage outputImage = model.getImage("blurredOutput");
    assertNotNull("Output image should be created", outputImage);

    int[] blurredPixel = outputImage.getPixel(1, 1);
    assertNotEquals("Center pixel should have changed", testImage.getPixel(1, 1), blurredPixel);
  }

  @Test
  public void testPartialImageManipulationEdgePixels() {
    model.addImage("originalImage", testImage);
    CustomImage maskImage = new CustomImage(3, 3);
    maskImage.setPixels(new int[][][]{
        {{0, 0, 0}, {255, 255, 255}, {0, 0, 0}},
        {{255, 255, 255}, {255, 255, 255}, {255, 255, 255}},
        {{0, 0, 0}, {255, 255, 255}, {0, 0, 0}}
    });
    model.addImage("maskImage", maskImage);

    model.partialImageManipulation("blur", "originalImage",
        "maskImage", "outputImage");

    CustomImage outputImage = model.getImage("outputImage");
    assertNotNull("Output image should be created", outputImage);

    int[] cornerPixel = outputImage.getPixel(0, 0);
    assertNotEquals("Top-left corner pixel should have changed due to blur",
        testImage.getPixel(0, 0), cornerPixel);
  }

  @Test
  public void testPartialImageManipulationGreyscale() {
    model.addImage("originalImage", testImage);
    CustomImage maskImage = new CustomImage(3, 3);
    maskImage.setPixels(new int[][][]{
        {{255, 255, 255}, {0, 0, 0}, {255, 255, 255}},
        {{255, 255, 255}, {0, 0, 0}, {255, 255, 255}},
        {{255, 255, 255}, {255, 255, 255}, {255, 255, 255}}
    });
    model.addImage("maskImage", maskImage);

    model.partialImageManipulation("greyscale", "originalImage",
        "maskImage", "greyscaleOutput");
    CustomImage outputImage = model.getImage("greyscaleOutput");
    assertNotNull("Output image should be created", outputImage);

    int[] greyscalePixel = outputImage.getPixel(1, 1);
    assertArrayEquals("Center pixel should be greyscaled",
        new int[]{(greyscalePixel[0]), (greyscalePixel[0]), (greyscalePixel[0])}, greyscalePixel);
  }

  @Test
  public void testDownscaleImage() {
    model.addImage("teImage", testImage);
    model.downscaleImage("testImage", "downscaledImage",
        2, 2);
    CustomImage downscaledImage = model.getImage("downscaledImage");

    assertNotNull("Downscaled image should be created", downscaledImage);
    assertEquals("Downscaled image width should be 2", 2,
        downscaledImage.getWidth());
    assertEquals("Downscaled image height should be 2", 2,
        downscaledImage.getHeight());
  }

  @Test
  public void testDownscaleImageWithLargeImageSize() {
    int[][][] largePixels = new int[8][8][3];
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        largePixels[i][j] = new int[]{i * 30, j * 30, (i + j) * 10};
      }
    }
    CustomImage largeImage = new CustomImage(8, 8);
    largeImage.setPixels(largePixels);
    model.addImage("largeImage", largeImage);
    model.downscaleImage("largeImage", "downscaledLargeImage",
        4, 4);
    CustomImage downscaledImage = model.getImage("downscaledLargeImage");

    assertNotNull("Downscaled large image should be created", downscaledImage);
    assertEquals("Downscaled large image width should be 4",
        4, downscaledImage.getWidth());
    assertEquals("Downscaled large image height should be 4",
        4, downscaledImage.getHeight());
  }

  @Test
  public void testDownscaleImageWithInvalidSource() {
    model.downscaleImage("nonExistent", "invalidDownscale",
        2, 2);
    CustomImage downscaledImage = model.getImage("invalidDownscale");
    assertNull("Downscaled image should not exist since source image does not exist",
        downscaledImage);
  }

  @Test
  public void testDownscaleImageToOriginalSize() {
    model.addImage("AssignImage", testImage);
    model.downscaleImage("testImage", "sameSizeImage",
        3, 3);

    CustomImage sameSizeImage = model.getImage("sameSizeImage");
    assertNotNull("Downscaled image should be created", sameSizeImage);
    assertEquals("Downscaled image width should be 3",
        3, sameSizeImage.getWidth());
    assertEquals("Downscaled image height should be 3",
        3, sameSizeImage.getHeight());
    for (int y = 0; y < sameSizeImage.getHeight(); y++) {
      for (int x = 0; x < sameSizeImage.getWidth(); x++) {
        assertArrayEquals("Pixel at (" + x + ", " + y + ") should match",
            testImage.getPixel(x, y),
            sameSizeImage.getPixel(x, y));
      }
    }
  }

  @Test
  public void testDownscaleImageAspectRatioChange() {
    model.addImage("BackupImage", testImage);

    model.downscaleImage("testImage", "downscaledImage",
        1, 3); // Expects to retrieve only height

    CustomImage downscaledImage = model.getImage("downscaledImage");
    assertNotNull("Downscaled image should be created", downscaledImage);
    assertEquals("Downscaled image width should be 1",
        1, downscaledImage.getWidth());
    assertEquals("Downscaled image height should be 3",
        3, downscaledImage.getHeight());

    for (int y = 0; y < downscaledImage.getHeight(); y++) {
      int[] pixel = downscaledImage.getPixel(0, y);
      assertNotNull("Pixel in downscaled image should not be null", pixel);
    }
  }

}






