# Image Processing Application

## Usage

1. Launch the application by navigating to the `src` directory in the terminal.
2. Start the application with the command:
   ```bash
   java -jar ass5.jar
   ```
   OR
   ```bash
   java -jar ass5.jar -text
   ```
   OR
   ```bash
   java -jar ass5.jar -file script.txt
   ```
3. You can run a predefined script by typing ot copying the command:
   ```bash
   run script.txt
   ```
   This will execute all methods in the model using the example image and generate the modified images, which will be saved in the `res/` folder located inside the `src` directory.

4. Alternatively, you can interact with the application using text-based commands to manipulate images according to your needs. The following is the way to load the example image.
   ```bash
   load res/image1.png <imageName>
   ```
5. To exit the application, type:
   ```bash
   exit
   ```
# Image Processing Commands Usage

The application accepts various text-based commands for image processing. Here’s a list of available commands and their usage:

## 1. Load an Image
- **Command:** `load <imagePath> <imageName>`
- **Description:** Loads an image from the specified path and assigns it a name.

## 2. Save an Image
- **Command:** `save <imagePath> <imageName>`
- **Description:** Saves the current image to the specified path with the given name.

## 3. Extract Color Components
- **Command:** `red-component <imageName> <destName>`
- **Command:** `green-component <imageName> <destName>`
- **Command:** `blue-component <imageName> <destName>`
- **Command:** `value-component <imageName> <destName>`
- **Command:** `luma-component <imageName> <destName>`
- **Command:** `intensity-component <imageName> <destName>`
- **Description:** Extracts the specified color component from the source image and saves it as a new image.

## 4. Flip an Image
- **Command:** `horizontal-flip <imageName> <destName>`
- **Command:** `vertical-flip <imageName> <destName>`
- **Description:** Flips the image either horizontally or vertically and saves the result.

## 5. Brighten an Image
- **Command:** `brighten <increment> <imageName> <destName>`
- **Description:** Brightens the image by the specified increment and saves the result. The increment can be positive(brighten the image) or negitive(darken the image).

## 6. Darken an Image
- **Command:** `darken <increment> <imageName> <destName>`
- **Description:** Darkens the image by the specified increment and saves the result.The increment can be positive(darken the image) or negitive(brighten the image).

## 7. Split RGB Channels
- **Command:** `rgb-split <imageName> <redDestName> <greenDestName> <blueDestName>`
- **Description:** Splits the RGB channels of the image into separate images.

## 8. Combine RGB Channels
- **Command:** `rgb-combine <destName> <redImageName> <greenImageName> <blueImageName>`
- **Description:** Combines separate RGB channel images into a single image.

## 9. Blur an Image
- **Command:** `blur <imageName> <destName>`
- **Description:** Applies a blur filter to the image and saves the result.

## 10. Sharpen an Image
- **Command:** `sharpen <imageName> <destName>`
- **Description:** Applies a sharpening filter to the image and saves the result.

## 11. Apply Sepia Filter
- **Command:** `sepia <imageName> <destName>`
- **Description:** Applies a sepia filter to the image and saves the result.

## 12. Run a Script
- **Command:** `run <scriptFilePath>`
- **Description:** Executes commands from a script file. Each line in the file should contain a valid command.

### 13. Compress an Image
- **Command:** `compress <percentage> <imageName> <destName>`
- **Description:** Creates a compressed version of the image at the specified percentage (0-99) and saves it.

### 14. Generate Histogram
- **Command:** `histogram <imageName> <destName>`
- **Description:** Produces an image that represents the histogram of the given image. The histogram image size is 256x256 and includes line graphs for red, green, and blue channels.

### 15. Color Correction
- **Command:** `color-correct <imageName> <destName>`
- **Description:** Corrects the colors of the image by aligning the meaningful peaks of its histogram.

### 16. Levels Adjustment
- **Command:** `levels-adjust <b> <m> <w> <imageName> <destName>`
- **Description:** Adjusts the levels of the image with specified black (b), mid (m), and white (w) values (0-255), and saves the result.

### 17. Accept Command-Line Script File
- **Command:** `-file <scriptFilePath>`
- **Description:** Accepts a script file as a command-line argument. Runs the script if provided; otherwise, allows interactive command entry.

### 18. Downscale
- **Command:** `downscale <imageName> <destName> <newWidth> <newHeigth>`
- **Description:** Downscale the image to the new width and height. the new width and new height must be smaller than the original image.


### Methods Supporting Split View

Each of these commands can optionally include a `split` parameter to produce a split-view image. The `split` option takes a percentage value (0-100) that specifies the placement of a vertical line, with the filtered effect applied only to one side of the image.

#### 1. Blur with Split
- **Command:** `blur <imageName> <destName> split <percentage>`
- **Description:** Blurs the image up to the specified percentage width; the remaining part retains the original content.

#### 2. Sharpen with Split
- **Command:** `sharpen <imageName> <destName> split <percentage>`
- **Description:** Sharpens the image up to the specified percentage width; the remaining part retains the original content.

#### 3. Sepia with Split
- **Command:** `sepia <imageName> <destName> split <percentage>`
- **Description:** Applies a sepia filter up to the specified percentage width; the remaining part retains the original content.

#### 4. Greyscale with Split
- **Command:** `greyscale <imageName> <destName> split <percentage>`
- **Description:** Converts the image to greyscale up to the specified percentage width; the remaining part retains the original color.

#### 5. Color Correction with Split
- **Command:** `color-correct <imageName> <destName> split <percentage>`
- **Description:** Applies color correction up to the specified percentage width; the remaining part retains the original color.

#### 6. Levels Adjustment with Split
- **Command:** `levels-adjust <b> <m> <w> <imageName> <destName> split <percentage>`
- **Description:** Adjusts levels up to the specified percentage width, using the black (`b`), mid (`m`), and white (`w`) values; the remaining part retains the original levels.


### Partial Image Manipulation Using Masks

This feature allows image manipulations to be applied to specific regions of an image using a corresponding mask image (MI). The mask image must be the same size as the source image and use black and white colors to specify the regions for manipulation. Black areas in the mask indicate where the manipulation will be applied, and white areas will retain the original image content.


### Supported Commands
1. **Blur with Mask**
   - **Command:** `blur <imageName> <maskImaegName> <destName>`
   - **Description:** Applies a blur effect to areas specified by the black regions of the mask.

2. **Sharpen with Mask**
   - **Command:** `sharpen <imageName> <maskImaegName> <destName>`
   - **Description:** Applies a sharpening effect to areas specified by the black regions of the mask.

3. **Greyscale with Mask**
   - **Command:** `greyscale <imageName> <maskImaegName> <destName>`
   - **Description:** Converts specific areas to greyscale as defined by the black regions of the mask.

4. **Sepia with Mask**
   - **Command:** `sepia <imageName> <maskImaegName> <destName>`
   - **Description:** Applies a sepia tone to areas defined by the black regions of the mask.

5. **Component Visualization with Mask**
   - **Command:** `component <imageName> <maskImaegName> <destName>`
   - **Description:** Visualizes a specific color component (`red`, `green`, `blue`, `value`, `intensity`, `luma`) in areas defined by the black regions of the mask.


