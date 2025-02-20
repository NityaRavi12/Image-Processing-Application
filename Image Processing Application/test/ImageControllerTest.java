import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for ImageController.
 */
public class ImageControllerTest {

  private ImageController controller;
  private ImageModel model;
  private ImageView view;

  @Before
  public void setUp() {
    model = new ImageModel();
    view = new ImageView();
    controller = new ImageController(model, view);
  }

  @Test
  public void testLoadImage_Success() {
    String imageName = "image1";
    String imagePath = "res/image1.png";

    controller.executeCommand("load " + imagePath + " " + imageName);

    CustomImage loadedImage = model.getImage(imageName);

    assertNotNull("Image should be loaded", loadedImage);
    assertTrue("Image should have a non-zero height",
        loadedImage.getHeight() > 0);
    assertTrue("Image should have a non-zero width", loadedImage.getWidth() > 0);
    assertEquals("Error messages should be empty after loading an image", "",
        view.getFeedbackArea().getText());
  }

  @Test
  public void testComponentCommand_Red() {
    String imageName = "image1";
    String redComponentName = "redImage";

    controller.executeCommand("load res/image1.png " + imageName);
    controller.executeCommand("red-component " + imageName + " " + redComponentName);

    CustomImage redImage = model.getImage(redComponentName);

    assertNotNull("Red component image should be created", redImage);
    assertTrue("Red image should have non-zero pixels",
        redImage.getWidth() > 0 && redImage.getHeight() > 0);
    assertEquals("Error messages should be empty after extracting red component",
        "",
        view.getFeedbackArea().getText());
  }

  @Test
  public void testComponentCommand_Green() {
    String imageName = "image1";
    String greenComponentName = "greenImage";

    controller.executeCommand("load res/image1.png " + imageName);
    controller.executeCommand("green-component " + imageName + " " + greenComponentName);

    CustomImage greenImage = model.getImage(greenComponentName);

    assertNotNull("Green component image should be created", greenImage);
    assertTrue("Green image should have non-zero pixels",
        greenImage.getWidth() > 0 && greenImage.getHeight() > 0);
    assertEquals("Error messages should be empty after extracting green component",
        "",
        view.getFeedbackArea().getText());
  }


  @Test
  public void testFlipImage_Success() {
    String imageName = "image1";
    String flippedImageName = "flippedImage";

    controller.executeCommand("load res/image1.png " + imageName);
    controller.executeCommand("horizontal-flip " + imageName + " " + flippedImageName);

    CustomImage flippedImage = model.getImage(flippedImageName);
    assertNotNull("Flipped image should be created", flippedImage);
    assertTrue("Flipped image should have non-zero width and height",
        flippedImage.getWidth() > 0 && flippedImage.getHeight() > 0);
    assertEquals("Error messages should be empty after flipping the image", "",
        view.getFeedbackArea().getText());
  }

  @Test
  public void testSaveImage_Success() {
    String imageName = "TestImage";
    String savePath = "res/savedTestImage.jpg";

    controller.executeCommand("load res/image1.png " + imageName);
    controller.executeCommand("save " + savePath + " " + imageName);

    File savedFile = new File(savePath);
    assertTrue("Saved image file should exist", savedFile.exists());
    assertTrue("Saved image file should be a valid image",
        savedFile.length() > 0);
    assertEquals("Error messages should be empty after saving the image", "",
        view.getFeedbackArea().getText());
  }

  @Test
  public void testInvalidCommand() {
    controller.executeCommand("invalid-command");

    String expectedErrorMessage = "Invalid file or Unknown command";
    assertTrue("Expected error message for invalid command",
        view.getFeedbackArea().getText().contains(expectedErrorMessage));
    assertEquals("Should not have any successful messages",
        "Error: Invalid file or Unknown command: invalid-command",
        view.getFeedbackArea().getText().trim());
  }

  @Test
  public void testAdjustBrightness_Success() {
    String imageName = "image1";
    String brightenedImageName = "brightImage";

    controller.executeCommand("load res/image1.png " + imageName);
    controller.executeCommand("brighten 50 " + imageName + " " + brightenedImageName);

    CustomImage brightImage = model.getImage(brightenedImageName);
    assertNotNull("Brightened image should be created", brightImage);
    assertTrue("Image should have non-zero width and height",
        brightImage.getWidth() > 0 && brightImage.getHeight() > 0);
    assertEquals("Error messages should be empty after adjusting brightness", "",
        view.getFeedbackArea().getText());
  }

  @Test
  public void testRgbSplit_Success() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);
      controller.executeCommand("rgb-split " + imageName + " redImage greenImage blueImage");
      assertNotNull("Red component image should be created",
          model.getImage("redImage"));
      assertNotNull("Green component image should be created",
          model.getImage("greenImage"));
      assertNotNull("Blue component image should be created",
          model.getImage("blueImage"));

      assertEquals("Error messages should be empty after RGB split", "",
          view.getFeedbackArea().getText());

    } catch (Exception e) {
      fail("RGB split failed: " + e.getMessage());
    }
  }

  @Test
  public void testRgbCombine_Success() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);
      controller.executeCommand("rgb-split " + imageName +
          " redImage greenImage blueImage");
      String combinedImage = "combinedImage";
      controller.executeCommand("rgb-combine " + combinedImage +
          " redImage greenImage blueImage");
      CustomImage combinedImg = model.getImage(combinedImage);
      assertNotNull("Combined image should be created", combinedImg);
      assertTrue("Combined image should have non-zero width and height",
          combinedImg.getWidth() > 0 && combinedImg.getHeight() > 0);
      assertEquals("Error messages should be empty after RGB combine", "",
          view.getFeedbackArea().getText());

    } catch (Exception e) {
      fail("RGB combine failed: " + e.getMessage());
    }
  }

  @Test
  public void testColorCorrection_Success() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);
      String correctedImage = "correctedImage";
      controller.executeCommand(
          "color-correct " + imageName + " " + correctedImage + " split " + "50");
      CustomImage colorCorrectedImg = model.getImage(correctedImage);
      assertNotNull("Color-corrected image should be created", colorCorrectedImg);
      assertTrue("Color-corrected image should have non-zero width and height",
          colorCorrectedImg.getWidth() > 0 && colorCorrectedImg.getHeight() > 0);
      assertEquals("Error messages should be empty after color correction", "",
          view.getFeedbackArea().getText());

    } catch (Exception e) {
      fail("Color correction failed: " + e.getMessage());
    }
  }

  @Test
  public void testBlurring_Success() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);

      String blurredImage = "blurredImage";
      controller.executeCommand("blur " + imageName + " " + blurredImage);

      CustomImage blurredImg = model.getImage(blurredImage);
      assertNotNull("Blurred image should be created", blurredImg);
      assertTrue("Blurred image should have non-zero width and height",
          blurredImg.getWidth() > 0 && blurredImg.getHeight() > 0);
      assertEquals("Error messages should be empty after applying blur filter",
          "",
          view.getFeedbackArea().getText());

    } catch (Exception e) {
      fail("Blurring failed: " + e.getMessage());
    }
  }

  @Test
  public void testGreyscale_Success() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);
      String greyscaleImage = "greyscaleImage";

      controller.executeCommand("greyscale " + imageName + " " + greyscaleImage);
      CustomImage greyscaleImg = model.getImage(greyscaleImage);

      assertNotNull("Greyscale image should be created", greyscaleImg);
      assertTrue("Greyscale image should have non-zero width and height",
          greyscaleImg.getWidth() > 0 && greyscaleImg.getHeight() > 0);

      assertEquals("Error messages should be empty after applying greyscale filter",
          "",
          view.getFeedbackArea().getText());
    } catch (Exception e) {
      fail("Greyscale filter failed: " + e.getMessage());
    }
  }

  @Test
  public void testInvalidFilePathOnLoad() {
    String imageName = "invalidImageName";

    controller.executeCommand("load invalid/path/to/image.png " + imageName);

    String expectedErrorMessage = "Failed to load image: invalid/path/to/image.png";
    assertTrue("Expected error message for file not found",
        view.getFeedbackArea().getText().contains(expectedErrorMessage));
    assertEquals("Should not have any successful messages",
        "Error: Failed to load image: invalid/path/to/image.png",
        view.getFeedbackArea().getText().trim());
  }

  @Test
  public void testHistogram_Success() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);

      String histogramImage = "histogramImage";

      controller.executeCommand("histogram " + imageName + " " + histogramImage);
      CustomImage histogramImg = model.getImage(histogramImage);

      assertNotNull("Histogram image should be created", histogramImg);
      assertTrue("Histogram image should have non-zero width and height",
          histogramImg.getWidth() > 0 && histogramImg.getHeight() > 0);

      assertEquals("Error messages should be empty after generating histogram",
          "",
          view.getFeedbackArea().getText());

    } catch (Exception e) {
      fail("Histogram generation failed: " + e.getMessage());
    }
  }

  @Test
  public void testColorCorrectionInvalidPercentageLow() {
    try {
      String imageName = "image1";
      controller.executeCommand("load res/image1.png " + imageName);
      String correctedImage = "correctedImage";
      controller.executeCommand("color-correct " + imageName + " " + correctedImage + " -10");


    } catch (Exception e) {
      fail("Error handling for low percentage failed: " + e.getMessage());
    }
  }

  @Test
  public void testSepia_Success() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);
      String sepiaImage = "sepiaImage";

      controller.executeCommand("sepia " + imageName + " " + sepiaImage);
      CustomImage sepiaImg = model.getImage(sepiaImage);

      assertNotNull("Sepia image should be created", sepiaImg);
      assertTrue("Sepia image should have non-zero width and height",
          sepiaImg.getWidth() > 0 && sepiaImg.getHeight() > 0);

      assertEquals("Error messages should be empty after applying sepia filter",
          "",
          view.getFeedbackArea().getText());

    } catch (Exception e) {
      fail("Sepia filter failed: " + e.getMessage());
    }
  }

  @Test
  public void testColorCorrectionInvalidPercentageHigh() {
    try {
      String imageName = "image1";
      controller.executeCommand("load res/image1.png " + imageName);

      String correctedImage = "correctedImage";
      controller.executeCommand("color-correct "
          + imageName + " " + correctedImage + " 150");

    } catch (Exception e) {
      fail("Error handling for high percentage failed: " + e.getMessage());
    }
  }

  @Test
  public void testRgbSplitAndCombine() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);
      controller.executeCommand("rgb-split " + imageName + " redImage greenImage blueImage");

      String combinedImage = "combinedImage";
      controller.executeCommand("rgb-combine " + combinedImage + " redImage greenImage blueImage");
      CustomImage combinedImg = model.getImage(combinedImage);

      assertNotNull("Combined image should be created", combinedImg);
      assertEquals("Error messages should be empty after split and combine", "",
          view.getFeedbackArea().getText());

    } catch (Exception e) {
      fail("RGB split and combine failed: " + e.getMessage());
    }
  }

  @Test
  public void testMissingArguments_Error() {
    controller.executeCommand("compress");
    String expectedErrorMessage = "Invalid command. Missing arguments. " +
        "Usage: compress <percentage>(0-99) <imageName> <destName>";
    String actualErrorMessage = view.getFeedbackArea().getText().trim();
    assertTrue("Expected missing arguments error message but got: " + actualErrorMessage,
        actualErrorMessage.contains(expectedErrorMessage));
  }

  @Test
  public void testLoadImage_FileNotFound() {
    controller.executeCommand("load res/image3.png imageNotFound");
    assertTrue("Error loading image:",
        view.getFeedbackArea().getText().contains("Failed to load image: res/image3.png"));
    assertEquals("Should not have any regular messages",
        "Error: Failed to load image: res/image3.png", view.getFeedbackArea().getText());
  }

  @Test
  public void testScriptExecution_Success() {
    try {
      String scriptPath = "res/script.txt";
      controller.executeCommand("run " + scriptPath);
      assertTrue("Expected success message after script execution",
          view.getFeedbackArea().getText().contains("Script executed successfully"));


    } catch (Exception e) {
      fail("Script execution failed: " + e.getMessage());
    }
  }

  @Test
  public void testLevelsAdjust_Success() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);
      String adjustedImage = "adjustedImage";
      controller.executeCommand("levels-adjust 50 100 200 " + imageName + " " + adjustedImage);

      CustomImage adjustedImg = model.getImage(adjustedImage);
      assertNotNull("Levels-adjusted image should be created", adjustedImg);
      assertTrue("Levels-adjusted image should have non-zero width and height",
          adjustedImg.getWidth() > 0 && adjustedImg.getHeight() > 0);
      assertEquals("Error messages should be empty after levels adjustment", "",
          view.getFeedbackArea().getText());

    } catch (Exception e) {
      fail("Levels adjustment failed: " + e.getMessage());
    }
  }

  @Test
  public void testColorCorrect_Success() {
    String imageName = "image1";

    try {

      controller.executeCommand("load res/image1.png " + imageName);
      String colorCorrectedImage = "colorCorrectedImage";

      controller.executeCommand("color-correct " + imageName + " " + colorCorrectedImage);
      CustomImage colorCorrectedImg = model.getImage(colorCorrectedImage);
      assertNotNull("Color-corrected image should be created", colorCorrectedImg);
      assertTrue("Color-corrected image should have non-zero width and height",
          colorCorrectedImg.getWidth() > 0 && colorCorrectedImg.getHeight() > 0);

      assertEquals("Error messages should be empty after color correction", "",
          view.getFeedbackArea().getText());

    } catch (Exception e) {
      fail("Color correction failed: " + e.getMessage());
    }
  }

  @Test
  public void testAdjustBrightness_Failure() {
    String imageName = "image1";
    controller.executeCommand("load res/image1.png " + imageName);
    controller.executeCommand("brighten abc " + imageName + " brightImage");
    String expectedErrorMessage = "Failed to brighten image. For input string: \"abc\"";
    assertTrue("Expected error message for invalid argument",
        view.getFeedbackArea().getText().contains("Failed to brighten image."));
  }

  @Test(expected = AssertionError.class)
  public void testApplyBlur_Failure() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);

      String blurredImage = "blurredImage";
      controller.executeCommand("blur y " + imageName + " " + blurredImage);
      fail("Expected an exception for invalid blur argument.");

    } catch (Exception e) {
      assertTrue("Expected error message for invalid argument",
          view.getFeedbackArea().getText().contains("Invalid command argument for blur"));
      assertEquals("Should not have any regular messages", "",
          view.getFeedbackArea().getText());
    }
  }

  @Test(expected = AssertionError.class)
  public void testLevelsAdjust_Failure() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);

      String adjustedImage = "adjustedImage";
      controller.executeCommand("levels-adjust 50 100 350 " + imageName + " " + adjustedImage);

      fail("Expected an exception for invalid levels adjustment arguments.");

    } catch (Exception e) {
      assertTrue("Expected error message for invalid argument",
          view.getFeedbackArea().getText().contains("Invalid command argument for levels-adjust"));

      assertEquals("Should not have any regular messages", "",
          view.getFeedbackArea().getText());
    }
  }

  @Test(expected = AssertionError.class)
  public void testColorCorrect_Failure() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);

      String colorCorrectedImage = "colorCorrectedImage";
      controller.executeCommand("color-correct bk " + imageName + " " + colorCorrectedImage);
      fail("Expected an exception for invalid color correction argument.");

    } catch (Exception e) {
      assertTrue("Expected error message for invalid argument",
          view.getFeedbackArea().getText().contains("Invalid command argument for color-correct"));

      assertEquals("Should not have any regular messages", "",
          view.getFeedbackArea().getText());
    }
  }

  @Test(expected = AssertionError.class)
  public void testCompress_Failure() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);

      String compressedImage = "compressedImage";
      controller.executeCommand("compress 800 " + imageName + " " + compressedImage);

      fail("Expected an exception for invalid compress percentage.");

    } catch (Exception e) {
      assertTrue("Expected error message for invalid argument",
          view.getFeedbackArea().getText().contains("Invalid command argument for compress"));

      assertEquals("Should not have any regular messages", "",
          view.getFeedbackArea().getText());
    }
  }

  @Test(expected = AssertionError.class)
  public void testHistogram_failure() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);

      String histogramImage = "histogramImage";
      controller.executeCommand("histogram ab" + imageName + " " + histogramImage);

      fail("Expected an exception for invalid histogram argument.");

    } catch (Exception e) {
      assertTrue("Expected error message for invalid argument",
          view.getFeedbackArea().getText().contains("Invalid command argument for histogram"));

      assertEquals("Should not have any regular messages", "",
          view.getFeedbackArea().getText());
    }
  }

  @Test(expected = AssertionError.class)
  public void testGreyscale_Failure() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);

      String greyscaleImage = "greyscaleImage";
      controller.executeCommand("greyscale k " + imageName + " " + greyscaleImage);
      fail("Expected an exception for invalid greyscale argument.");

    } catch (Exception e) {
      assertTrue("Expected error message for invalid argument",
          view.getFeedbackArea().getText().contains("Invalid command argument for greyscale"));

      assertEquals("Should not have any regular messages", "",
          view.getFeedbackArea().getText());
    }
  }

  @Test(expected = AssertionError.class)
  public void testSepia_Failure() {
    String imageName = "image1";
    try {
      controller.executeCommand("load res/image1.png " + imageName);

      String sepiaImage = "sepiaImage";
      controller.executeCommand("sepia pdp " + imageName + " " + sepiaImage);
      fail("Expected an exception for invalid sepia argument.");

    } catch (Exception e) {
      assertTrue("Expected error message for invalid argument",
          view.getFeedbackArea().getText().contains("Invalid command argument for sepia"));

      assertEquals("Should not have any regular messages", "",
          view.getFeedbackArea().getText());
    }
  }

  @Test(expected = AssertionError.class)
  public void testRgbSplit_Failure() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);

      controller.executeCommand("rgb-split g " + imageName + " redImage greenImage blueImage");

      fail("Expected an exception for invalid rgb-split argument.");

    } catch (Exception e) {
      assertTrue("Expected error message for invalid argument",
          view.getFeedbackArea().getText().contains("Invalid command argument for rgb-split"));

      assertNull("Red component image should not be created",
          model.getImage("redImage"));
      assertNull("Green component image should not be created",
          model.getImage("greenImage"));
      assertNull("Blue component image should not be created",
          model.getImage("blueImage"));

      assertEquals("Should not have any regular messages", "",
          view.getFeedbackArea().getText());
    }
  }

  @Test(expected = AssertionError.class)
  public void testRgbCombine_Failure() {
    String imageName = "image1";

    try {
      controller.executeCommand("load res/image1.png " + imageName);

      controller.executeCommand("rgb-split " + imageName + " redImage greenImage blueImage");

      String combinedImage = "combinedImage";
      controller.executeCommand(
          "rgb-combine z " + combinedImage + " redImage greenImage blueImage");
      fail("Expected an exception for invalid rgb-combine arguments.");

    } catch (Exception e) {
      assertTrue("Expected error message for invalid argument",
          view.getFeedbackArea().getText().contains("Invalid command argument for rgb-combine"));
      assertNull("Combined image should not be created",
          model.getImage("combinedImage"));
      assertEquals("Should not have any regular messages",
          "", view.getFeedbackArea().getText());
    }
  }

  @Test(expected = AssertionError.class)
  public void testFlipImage_Failure() {
    String imageName = "testImage";

    try {
      controller.executeCommand("load res/image1.png " + imageName);
      String flippedImageName = "flippedImage";

      controller.executeCommand("horizontal-flip y " + imageName + " " + flippedImageName);

      fail("Expected an exception for invalid flip argument.");

    } catch (Exception e) {
      assertTrue("Expected error message for invalid argument for horizontal-flip",
          view.getFeedbackArea().getText()
              .contains("Invalid command argument for horizontal-flip"));
      assertEquals("Should not have any regular messages", "",
          view.getFeedbackArea().getText());
    }
  }

  @Test
  public void testImageNameChangeOnComponentExtraction() {
    String imageName = "baseImage";
    String redComponentName = "redComponent";

    controller.executeCommand("load res/image1.png " + imageName);

    controller.executeCommand(
        "red-component " + imageName + " " + redComponentName); // Extract red component

    CustomImage redImage = model.getImage(redComponentName);
    assertNotNull("Red component image should be created", redImage);
    assertEquals("Error messages should be empty after extracting red component",
        "",
        view.getFeedbackArea().getText());
  }

  @Test
  public void testPartialManipulation_Success() {
    String imageName = "image1";
    String maskImageName = "maskImage";
    String resultImageName = "partialManipulatedImage";
    controller.executeCommand("load res/image1.png " + imageName);
    controller.executeCommand("load res/mask.png " + maskImageName);
    controller.executeCommand(
        "partial-manipulation blur " + imageName + " " + maskImageName + " " + resultImageName);

    // Retrieve the result image
    CustomImage resultImage = model.getImage(resultImageName);

    assertNotNull("Result image should be created", resultImage);
    assertTrue("Result image should have non-zero pixels",
        resultImage.getWidth() > 0 && resultImage.getHeight() > 0);
    assertEquals("Error messages should be empty after partial manipulation",
        "Partial manipulation done successfully for image: image1",
        view.getFeedbackArea().getText());
  }

  @Test
  public void testDownscaleImage_Success() {
    String imageName = "image1";
    String downscaledImageName = "downscaledImage";

    controller.executeCommand("load res/image1.png " + imageName);

    // Perform downscaling operation
    controller.executeCommand("downscale " + imageName + " " + downscaledImageName + " 2 2");

    CustomImage downscaledImage = model.getImage(downscaledImageName);
    assertNotNull("Downscaled image should be created", downscaledImage);
    assertEquals("Downscaled image width should be 2",
        2, downscaledImage.getWidth());
    assertEquals("Downscaled image height should be 2",
        2, downscaledImage.getHeight());
    assertEquals("Error messages should be empty after downscaling", "",
        view.getFeedbackArea().getText());
  }

  @Test
  public void testDownscaleImage_WithValidParams() {
    String imageName = "image1";
    String downscaledImageName = "downscaledImage";

    controller.executeCommand("load res/image1.png " + imageName);
    controller.executeCommand("downscale " + imageName + " " + downscaledImageName + " 4 4");

    CustomImage downscaledImage = model.getImage(downscaledImageName);
    assertNotNull("Downscaled image should be created", downscaledImage);
    assertEquals("Downscaled image should have expected width",
        4, downscaledImage.getWidth());
    assertEquals("Downscaled image should have expected height",
        4, downscaledImage.getHeight());

    assertEquals("Error messages should be empty after downscaling", "",
        view.getFeedbackArea().getText());
  }

  @Test
  public void testDownscaleImage_AspectRatioChange() {
    String imageName = "image1";
    String downscaledImageName = "downscaledAspectImage";

    controller.executeCommand("load res/image1.png " + imageName);
    controller.executeCommand("downscale " + imageName + " " + downscaledImageName + " 1 2");

    CustomImage downscaledImage = model.getImage(downscaledImageName);
    assertNotNull("Downscaled image should be created", downscaledImage);
    assertEquals("Downscaled image width should be 1",
        1, downscaledImage.getWidth());
    assertEquals("Downscaled image height should be 2",
        2, downscaledImage.getHeight());

    assertEquals("Error messages should be empty after downscaling", "",
        view.getFeedbackArea().getText());
  }

  @Test
  public void testPartialManipulation_WithDifferentCommands() {
    String imageName = "image1";
    String maskImageName = "maskImage";
    String resultImageNameBlur = "partialBlurredImage";
    String resultImageNameGreyscale = "partialGreyscaleImage";

    controller.executeCommand("load res/image1.png " + imageName);
    controller.executeCommand("load res/mask.png " + maskImageName);
    controller.executeCommand(
        "partial-manipulation blur " + imageName + " " + maskImageName + " " +
            resultImageNameBlur);
    controller.executeCommand(
        "partial-manipulation greyscale " + imageName + " " + maskImageName + " "
            + resultImageNameGreyscale);

    CustomImage resultantBlurredImage = model.getImage(resultImageNameBlur);
    CustomImage resultantGreyscaleImage = model.getImage(resultImageNameGreyscale);

    assertNotNull("Blurred result image should be created", resultantBlurredImage);
    assertNotNull("Greyscale result image should be created", resultantGreyscaleImage);

    assertEquals("Error messages should be empty after partial manipulation",
        "Partial manipulation done successfully for image: image1",
        view.getFeedbackArea().getText());
  }
}
