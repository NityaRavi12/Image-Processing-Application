# Image Processing Application

## Overview

This application provides functionality for loading, manipulating, and saving images. It supports
multiple file formats and implements various image processing operations. The architecture follows
the MVC (Model-View-Controller) design pattern.

## Image Attribution

- The images used in this project are my own original photographs. I, Gary Zang, am the author and owner of the image and authorize their use in this project.

## Summary of Changes

## 1. GUI Implementation in `ImageView` Class

- **Change**:
    - The `ImageView` class now extends `JFrame` and provides a graphical user interface (GUI).
    - Added buttons for various image operations:
        - **Load/Save**: `load`, `save`.
        - **Color Components**: `red-component`, `green-component`, `blue-component`.
        - **Image Manipulations**: `greyscale`, `horizontal-flip`, `vertical-flip`, `brighten`,
          `darken`, `blur`, `sharpening`, `sepia`.
        - **Advanced Features**: `compress`, `color-correct`, `levels-adjust`, `downscale`.
- **Justification**:
    - Enhances user experience by providing an intuitive interface to perform operations.
    - Makes the application more accessible to users who prefer graphical interfaces over
      command-line interactions.

---

## 2. Downscale Image Manipulation

- **Change**:
    - Added a new method `downscale` in the `Model` to resize images to specified dimensions (
      `newWidth` and `newHeight`).
    - Introduced a corresponding command in the `Controller` (e.g.,
      `downscale imageName destName newWidth newHeight`).
    - The feature is available both in the GUI and command-line interface.
- **Justification**:
    - Provides essential functionality for resizing images, a common requirement in image
      processing.
    - Ensures consistency across different interaction modes (GUI and text-based).

---

## 3. Partial Image Manipulation Using Masks

- **Change**:
    - Added support for partial image manipulations using a mask image (`MI`):
        - **Mask Image**: A black-and-white image of the same dimensions as the source image (`I`).
        - The manipulation is applied only to pixels corresponding to black regions in `MI`.
    - Supported manipulations: `blur`, `sharpen`, `greyscale`, `sepia`, and component
      visualizations(`red-component`, `green-component`, `blue-component`, `value-component`,
      `intensity-component`, `luma-component`).
    - Modified `Controller` to handle commands in the form:
        - `operation sourceImage maskImage destImage`: Applies the operation using the mask.
    - Added a method `partialImageManipulation` in the `Model` to handle this functionality.
- **Justification**:
    - Enables advanced editing for selective modifications, adding precision and flexibility.
    - Supports scripting for batch processing and automation without complicating the GUI.
