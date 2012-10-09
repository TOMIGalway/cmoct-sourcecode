/*******************************************************************************
 * Copyright (c) 2012 joey.enfield.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     joey.enfield - initial API and implementation
 ******************************************************************************/
package com.joey.software.imageToolkit;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Kernel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.colorMapping.ColorMap;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;

public class ImageOperations {

	public static final int X_AXIS = 0;

	public static final int Y_AXIS = 1;

	public static final int XY_AXIS = 2;

	public static final int PROJECT_TYPE_AVERAGE = 0;

	public static final int PROJECT_TYPE_MAX = 1;

	public static final int PROJECT_TYPE_MIN = 2;

	public static final int PLANE_RED = 0;

	public static final int PLANE_GREEN = 1;

	public static final int PLANE_BLUE = 2;

	public static final int PLANE_ALPHA = 3;

	public static final int PLANE_GRAY = 4;

	public static byte[] image_byte_data(BufferedImage image) {
		WritableRaster raster = image.getRaster();
		DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
		return buffer.getData();
	}

	public static int[] imageToPixelsInts(BufferedImage image) {
		WritableRaster raster = image.getRaster();
		DataBufferInt buffer = (DataBufferInt) raster.getDataBuffer();
		return buffer.getData();
	}

	/**
	 * Ints must be maskable by red 0xff0000, <br>
	 * green 0xff00, <br>
	 * blue 0xff, <alpha> 0xff000000
	 * 
	 * @param pixels
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage pixelsToImage(int[] pixels, int width,
			int height) {
		DirectColorModel directColorModel = new DirectColorModel(32, 0xff0000,
				0xff00, 0xff, 0xff000000);
		MemoryImageSource memoryImageSource = new MemoryImageSource(width,
				height, directColorModel, pixels, 0, width);
		SampleModel sampleModel = directColorModel.createCompatibleSampleModel(
				width, height);
		DataBufferInt dataBufferInt = new DataBufferInt(pixels, width * height);

		WritableRaster writableRaster = Raster.createWritableRaster(
				sampleModel, dataBufferInt, new Point(0, 0));
		BufferedImage bufferedImage = new BufferedImage(directColorModel,
				writableRaster, false, null);

		return bufferedImage;
	}

	public static BufferedImage pixelsToImage(byte[] data, int w, int h) {
		DataBuffer buffer = new DataBufferByte(data, w * h);

		int pixelStride = 1; // assuming r, g, b,
								// skip, r, g, b,
								// skip...
		int scanlineStride = w; // no extra
								// padding
		int[] bandOffsets = { 0 }; // r, g, b
		WritableRaster raster = Raster.createInterleavedRaster(buffer, w, h,
				scanlineStride, pixelStride, bandOffsets, null);

		ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		boolean hasAlpha = false;
		boolean isAlphaPremultiplied = false;
		int transparency = Transparency.OPAQUE;
		int transferType = DataBuffer.TYPE_BYTE;
		ColorModel colorModel = new ComponentColorModel(colorSpace, hasAlpha,
				isAlphaPremultiplied, transparency, transferType);

		return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
	}

	public static void splitColor(BufferedImage src, BufferedImage red,
			BufferedImage green, BufferedImage blue) {
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				Color c = new Color(src.getRGB(x, y));

				Color r = new Color(c.getRed(), c.getRed(), c.getRed());
				Color g = new Color(c.getGreen(), c.getGreen(), c.getGreen());
				Color b = new Color(c.getBlue(), c.getBlue(), c.getBlue());

				red.setRGB(x, y, r.getRGB());
				green.setRGB(x, y, g.getRGB());
				blue.setRGB(x, y, b.getRGB());
			}
		}
	}

	public static void grabPxlData(BufferedImage img, float[][] data, int plane) {
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				data[x][y] = getPlaneFromRGBA(img.getRGB(x, y), plane);
			}
		}
	}

	public static byte[][] grabPxlData(BufferedImage img, int plane) {
		byte[][] rst = new byte[img.getWidth()][img.getHeight()];
		grabPxlData(img, rst, plane);
		return rst;
	}

	public static void grabPxlData(BufferedImage img, byte[][] data, int plane) {
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				data[x][y] = (byte) getPlaneFromRGBA(img.getRGB(x, y), plane);
			}
		}
	}

	public static void grabPxlData(BufferedImage img, int[][] data, int plane) {
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				data[x][y] = getPlaneFromRGBA(img.getRGB(x, y), plane);
			}
		}
	}

	public static void grabPxlData(BufferedImage img, double[][] data, int plane) {
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				data[x][y] = getPlaneFromRGBA(img.getRGB(x, y), plane);
			}
		}
	}

	/**
	 * This will grab the pixel data from a given buffered image. This will grab
	 * the pixel data between p1 and p2 and store it into red, green, blue; if
	 * no data is wanted in red/green/blue just pass null.
	 * 
	 * If the size of red/green/blue is 1/0 it will return the value of the
	 * pixel between p1, p2
	 * 
	 * @param img
	 * @param p1
	 *            - First point to get pixels on.
	 * @param p2
	 *            - Second point on line to get pixels from
	 * @param red
	 *            - Store red pixel values
	 * @param green
	 *            - Store green pixel values
	 * @param blue
	 *            - store blue pixel values
	 * @param size
	 *            - The number of points to get between p1 and p2
	 */
	public static void getPixelData(BufferedImage img, Point p1, Point p2,
			int[] red, int[] green, int[] blue, int[] gray, int size) {
		double step = 1.0 / (size - 1);
		double pos = 0;
		int count = 0;
		int x = 0;
		int y = 0;

		// In the case of a single size array
		// return the mid point value
		if (size == 1) {
			x = (int) Math.round((p1.x + p2.x) / 2.0);
			y = (int) Math.round((p1.y + p2.y) / 2.0);

			Color c = new Color(img.getRGB(x, y));

			// System.out.printf("[%3d , %3d ] -> [ %d , %d , %d ]\n",x,y,
			// c.getRed(), c.getGreen(),
			// c.getBlue());
			if (red != null) {
				red[count] = c.getRed();
			}
			if (green != null) {
				green[count] = c.getGreen();
			}
			if (blue != null) {
				blue[count] = c.getBlue();
			}
			if (gray != null) {
				gray[count] = (c.getRed() + c.getBlue() + c.getGreen()) / 3;
			}
			return;
		}

		// Move along a line between p1 and p2 and
		// get pixel values
		// using the equation Pnew = P1+(P2-P1)*r
		// where r = 0 - 1
		while (pos <= 1) {
			x = (int) Math.round(p1.x + (p2.x - p1.x) * pos);
			y = (int) Math.round(p1.y + (p2.y - p1.y) * pos);

			Color c;
			try {
				c = new Color(img.getRGB(x, y));
			} catch (ArrayIndexOutOfBoundsException e) {
				c = new Color(0, 0, 0);
			}

			if (red != null) {
				red[count] = c.getRed();
			}
			if (green != null) {
				green[count] = c.getGreen();
			}
			if (blue != null) {
				blue[count] = c.getBlue();
			}
			if (gray != null) {
				gray[count] = (c.getRed() + c.getBlue() + c.getGreen()) / 3;
			}

			count++;
			pos += step;
		}
	}

	public static BufferedImage getImage(byte[][] data) {
		BufferedImage img = getBi(data.length, data[0].length);

		getImage(data, img);
		return img;
	}

	public static void getImage(byte[][] data, BufferedImage img) {

		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[x].length; y++) {
				byte dat = data[x][y];
				if (dat < 0) {
					img.setRGB(x, y, getGrayRGB(dat + 256));
				} else {
					img.setRGB(x, y, getGrayRGB(dat));
				}
			}
		}

	}

	public static BufferedImage getImage(float[][] data, float min, float max,
			ColorMap map) {
		BufferedImage img = getBi(data.length, data[0].length);
		getImage(data, min, max, map, img);
		return img;
	}

	public static void getImage(float[][] data, float min, float max,
			ColorMap map, BufferedImage img) {

		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[x].length; y++) {
				float dat = (data[x][y] - min) / (max - min);
				if (dat < 0) {
					dat = 0;
				}
				if (dat > 1) {
					dat = 1;
				}
				img.setRGB(x, y, map.getColor(dat).getRGB());
			}
		}

	}

	public static BufferedImage getImageProjection(int projectType,
			int planeType, StatusBarPanel status, BufferedImage... img) {
		int wide = img[0].getWidth();
		int high = img[0].getHeight();
		int num = img.length;

		BufferedImage rst = ImageOperations.getBi(wide, high);
		getImageProjection(projectType, planeType, status, rst, img);
		return rst;
	}

	public static void getImageProjection(int projectType, int planeType,
			StatusBarPanel status, BufferedImage rst, BufferedImage... img) {

		int wide = img[0].getWidth();
		int high = img[0].getHeight();
		int num = img.length;

		int[] vals = new int[num];
		int result = 0;

		if (status != null) {
			status.setMaximum(wide - 1);
		}
		for (int x = 0; x < wide; x++) {
			if (status != null) {
				status.setValue(x);
			}
			for (int y = 0; y < high; y++) {

				for (int i = 0; i < num; i++) {
					vals[i] = getPlaneFromRGBA(img[i].getRGB(x, y), planeType);
				}

				if (projectType == PROJECT_TYPE_AVERAGE) {
					result = DataAnalysisToolkit.getAverage(vals);
				} else if (projectType == PROJECT_TYPE_MIN) {
					result = DataAnalysisToolkit.getMin(vals);
				} else if (projectType == PROJECT_TYPE_MAX) {
					result = DataAnalysisToolkit.getMax(vals);
				}

				rst.setRGB(x, y, getGrayRGB(result));
			}
		}
	}

	/**
	 * This will get a rgb value from the val
	 * 
	 * @param val
	 * @return
	 */
	public static int getGrayRGB(int val) {
		float pos = val / 256.0f;

		if (pos < 0) {
			pos = 0;
		}

		if (pos > 1) {
			pos = 1;
		}

		Color c = new Color(pos, pos, pos);
		return c.getRGB();
	}

	public static int getPlaneFromRGBA(int rgb, int plane) {
		Color c = new Color(rgb);
		if (plane == PLANE_RED) {
			return c.getRed();
		} else if (plane == PLANE_BLUE) {
			return c.getBlue();
		} else if (plane == PLANE_GREEN) {
			return c.getGreen();
		} else if (plane == PLANE_GRAY) {
			return (c.getRed() + c.getBlue() + c.getGreen()) / 3;
		} else if (plane == PLANE_ALPHA) {
			return c.getAlpha();
		} else {
			return 0;
		}
	}

	public static BufferedImage getAverageImage(File[] data) throws IOException {
		float[][] dataHold = null;

		for (int i = 0; i < data.length; i++) {
			BufferedImage img = ImageIO.read(data[i]);
			if (i == 0) {
				dataHold = new float[img.getWidth()][img.getHeight()];
			}
			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {
					dataHold[x][y] += getGrayScale(img.getRGB(x, y))
							/ data.length;
				}
			}
		}

		BufferedImage img = ImageOperations.getBi(dataHold.length,
				dataHold[0].length);
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				Color c = new Color((int) dataHold[x][y], (int) dataHold[x][y],
						(int) dataHold[x][y]);
				img.setRGB(x, y, c.getRGB());
			}
		}

		return img;
	}

	public static BufferedImage getAverageData(Vector<BufferedImage> data,
			int[][] val) {

		int wide = val.length;
		int high = val[0].length;

		for (int x = 0; x < wide; x++) {
			for (int y = 0; y < high; y++) {
				val[x][y] = 0;
			}
		}

		for (int i = 0; i < data.size(); i++) {
			for (int x = 0; x < wide; x++) {
				for (int y = 0; y < high; y++) {
					try {
						Color c = new Color(data.get(i).getRGB(x, y));
						val[x][y] += (c.getRed() + c.getGreen() + c.getBlue()) / 3;
					} catch (Exception e) {

					}
				}
			}
		}

		// set average // getmax and min
		BufferedImage rst = ImageOperations.getBi(wide, high);
		for (int x = 0; x < wide; x++) {
			for (int y = 0; y < high; y++) {
				try {
					val[x][y] /= data.size();
					if (val[x][y] > 255) {
						val[x][y] = 255;
					}
					if (val[x][y] < 0) {
						val[x][y] = 0;
					}
					Color c = new Color(val[x][y], val[x][y], val[x][y]);
					rst.setRGB(x, y, c.getRGB());
				} catch (Exception e) {

				}
			}
		}

		// create Image
		return rst;
	}

	public static BufferedImage getUserImage() throws IOException {
		FileSelectionField imageFileSelector = new FileSelectionField();
		JPanel holder = new JPanel(new BorderLayout());
		imageFileSelector
				.setFormat(FileSelectionField.FORMAT_IMAGE_FILES_SHOW_FORMAT);
		imageFileSelector.setLabelText("Image File : ");
		imageFileSelector.setFieldSize(400);
		holder.add(imageFileSelector, BorderLayout.NORTH);

		if (JOptionPane.showConfirmDialog(null, holder,
				"Please Select New Image", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
			return ImageIO.read(imageFileSelector.getFile());
		}
		return null;
	}

	public static void fillWithRandomColors(BufferedImage src) {
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {

				src.setRGB(x, y, getRandomColor().getRGB());

			}
		}
	}

	/**
	 * Split x-Axes - horizon splits split y-axes - vertical splirs
	 * 
	 * @param src
	 * @param count
	 * @param axes
	 * @return
	 */
	public static BufferedImage[] splitImage(BufferedImage src, int count,
			int axes) {
		BufferedImage[] rst = new BufferedImage[count];

		if (axes == X_AXIS) {
			int wide = src.getWidth();
			int high = (int) Math.ceil(src.getHeight() / (double) count);
			for (int i = 0; i < count; i++) {
				rst[i] = ImageOperations.getBi(wide, high);
			}

			for (int x = 0; x < src.getWidth(); x++) {
				for (int y = 0; y < src.getHeight(); y++) {
					int index = y / high;
					int pos = y % high;
					rst[index].setRGB(x, pos, src.getRGB(x, y));

				}
			}
			return rst;
		} else if (axes == Y_AXIS) {
			int wide = (int) Math.ceil(src.getWidth() / (double) count);
			int high = src.getHeight();
			for (int i = 0; i < count; i++) {
				rst[i] = ImageOperations.getBi(wide, high);
			}

			for (int x = 0; x < src.getWidth(); x++) {
				for (int y = 0; y < src.getHeight(); y++) {
					int index = x / wide;
					int pos = x % wide;
					System.out
							.printf("Src [ %d , %d ] , Rst[%d] pos [ %d, %d] of [%d , %d] \n",
									x, y, index, pos, y, wide, high);
					rst[index].setRGB(pos, y, src.getRGB(x, y));

				}
			}
			return rst;
		}
		return null;
	}

	/**
	 * This will add a colored border to an image
	 * 
	 * @param img
	 * @param pxls
	 * @param c
	 */
	public static void addColorBorder(BufferedImage img, int pxls, Color c) {
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				boolean xSide = (x < pxls) || x >= (img.getWidth() - pxls);
				boolean ySide = (y < pxls) || y >= (img.getHeight() - pxls);
				if (xSide || ySide) {
					img.setRGB(x, y, c.getRGB());
				}
			}
		}
	}

	/**
	 * This function can be used to rotate an image in 90 degree . The image can
	 * be rotated in a clockwise or anti-clockwise direction. the direction is
	 * chosen by haveing the rotationAmmount +ve(clockwise) or
	 * -ve(anti-clockwise). The returned image will contain a copy of all the
	 * pixel data from the orignal image.
	 * 
	 * @param image
	 *            - The image to rotate
	 * @param rotationType
	 *            - The number of 90 degree rotations to perform, it should be
	 *            either 0 (none), +/- 1 (90), +/- 2 (180 or flip), +/- 3 (270),
	 *            +/- 4 (none)
	 * @return BufferedImage containing the rotated image.
	 */
	public static BufferedImage getRotatedImage(BufferedImage image,
			int rotationAmmount) {
		if (Math.abs(rotationAmmount) > 4) {
			throw new InvalidParameterException(
					"Number of rotation should be between 0 -> -/+4");
		}
		// Check if rotation ammount is 0 and so
		// return clone of image
		if (rotationAmmount % 4 == 0) {
			return cloneImage(image);
		} else if (rotationAmmount % 2 == 0) {
			return getFlippedImage(image, XY_AXIS);
		} else {
			BufferedImage result = new BufferedImage(image.getHeight(),
					image.getWidth(), image.getType());
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					result.setRGB(y, x, image.getRGB(x, y));
				}
			}

			if (rotationAmmount == 1 || rotationAmmount == -3) {
				flipImage(result, Y_AXIS);
				return result;
			} else {
				flipImage(result, X_AXIS);
				return result;
			}
		}
	}

	public static void sendImageToClipBoard(Image image) {
		ImageSelection imageSelection = new ImageSelection(image);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(imageSelection, null);

	}

	public static BufferedImage getClipBoardImage() {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();

		Transferable trans = clip.getContents(null);

		for (int i = 0; i < trans.getTransferDataFlavors().length; i++) {
			try {
				System.out.println(trans.getTransferDataFlavors()[i]);
				Object obj;

				obj = (trans.getTransferData(trans.getTransferDataFlavors()[i]));

				if (obj instanceof BufferedImage) {
					return ((BufferedImage) obj);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;

	}

	public static void main(String input[]) throws IOException {
		int wide = 512;
		int high = 256;
		BufferedImage imageA = new BufferedImage(wide, high,
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g = imageA.createGraphics();
		g.setColor(Color.RED);
		g.drawOval(0, 0, wide, high);
		g.drawLine(0, 0, wide, high);
		g.drawLine(0, high, wide / 2, high / 2);

		sendImageToClipBoard(imageA);

	}

	public static void getRotateRightByte(BufferedImage srcImg,
			BufferedImage rstImg) {
		byte[] srcData = ImageOperations.image_byte_data(srcImg);
		byte[] rstData = ImageOperations.image_byte_data(rstImg);

		for (int x = 0; x < srcImg.getWidth(); x++) {
			for (int y = 0; y < srcImg.getHeight(); y++) {
				rstData[(srcImg.getHeight() - y - 1) + x * srcImg.getHeight()] = srcData[x
						+ y * srcImg.getWidth()];
			}
		}
	}

	public static void getRotateLeftByte(BufferedImage srcImg,
			BufferedImage rstImg) {
		byte[] srcData = ImageOperations.image_byte_data(srcImg);
		byte[] rstData = ImageOperations.image_byte_data(rstImg);

		for (int x = 0; x < srcImg.getWidth(); x++) {
			for (int y = 0; y < srcImg.getHeight(); y++) {
				rstData[y + (srcImg.getWidth() - x - 1) * srcImg.getHeight()] = srcData[x
						+ y * srcImg.getWidth()];
			}
		}
	}

	public static void getFlipLRByte(BufferedImage srcImg, BufferedImage rstImg) {
		byte[] srcData = ImageOperations.image_byte_data(srcImg);
		byte[] rstData = ImageOperations.image_byte_data(rstImg);

		for (int x = 0; x < srcImg.getWidth(); x++) {
			for (int y = 0; y < srcImg.getHeight(); y++) {
				rstData[x + y * srcImg.getWidth()] = srcData[x + y
						* srcImg.getWidth()];
			}
		}
	}

	public static BufferedImage getRotatedImageFull(BufferedImage src,
			double amount) {
		return getRotatedImageFull(src, amount, Color.BLACK);
	}

	public static BufferedImage getRotatedImageFull(BufferedImage src,
			double amount, Color background) {
		BufferedImage rst = null;

		AffineTransform tra = AffineTransform.getRotateInstance(amount,
				src.getWidth() / 2, src.getHeight() / 2);

		Point2D.Float p1 = new Point2D.Float(0, 0);
		Point2D.Float p2 = new Point2D.Float(0, src.getHeight());
		Point2D.Float p3 = new Point2D.Float(src.getWidth(), src.getHeight());
		Point2D.Float p4 = new Point2D.Float(src.getWidth(), 0);

		Point2D.Float[] pSrc = new Point2D.Float[] { p1, p2, p3, p4 };
		Point2D.Float[] pDst = new Point2D.Float[4];
		tra.transform(pSrc, 0, pDst, 0, 4);

		float xRange[] = DataAnalysisToolkit.getRangef(pDst[0].x, pDst[1].x,
				pDst[2].x, pDst[3].x);
		float yRange[] = DataAnalysisToolkit.getRangef(pDst[0].y, pDst[1].y,
				pDst[2].y, pDst[3].y);

		int wide = (int) (Math.ceil(xRange[1] - xRange[0]));
		int high = (int) (Math.ceil(yRange[1] - yRange[0]));

		AffineTransform newTra = AffineTransform.getTranslateInstance(
				-xRange[0], -yRange[0]);
		newTra.concatenate(tra);

		rst = ImageOperations.getBi(wide, high);
		ImageOperations.setImage(background, rst);
		rst.createGraphics().drawImage(src, newTra, null);
		return rst;
	}

	/**
	 * This will convert a rgb value to a gray scale value
	 * 
	 * @param rgb
	 * @return
	 */
	public static int getGrayScale(int rgb) {
		Color c = new Color(rgb);
		return (c.getRed() + c.getBlue() + c.getGreen()) / 3;
	}

	/**
	 * Returns a {@link VolatileImage} with a data layout and color model
	 * compatible with this <code>GraphicsConfiguration</code>. The returned
	 * <code>VolatileImage</code> may have data that is stored optimally for the
	 * underlying graphics device and may therefore benefit from
	 * platform-specific rendering acceleration.
	 * 
	 * @param width
	 *            the width of the returned <code>VolatileImage</code>
	 * @param height
	 *            the height of the returned <code>VolatileImage</code>
	 * @param transparency
	 *            the specified transparency mode
	 * @return a <code>VolatileImage</code> whose data layout and color model is
	 *         compatible with this <code>GraphicsConfiguration</code>.
	 * @throws IllegalArgumentException
	 *             if the transparency is not a valid value
	 * @see Transparency#OPAQUE
	 * @see Transparency#BITMASK
	 * @see Transparency#TRANSLUCENT
	 * @see Component#createVolatileImage(int, int)
	 */
	public static VolatileImage createVolatileImage(int width, int height,
			int transparency) {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice()
				.getDefaultConfiguration();
		VolatileImage image = null;

		image = gc.createCompatibleVolatileImage(width, height, transparency);

		int valid = image.validate(gc);

		if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {
			image = createVolatileImage(width, height, transparency);
			return image;
		}

		return image;
	}

	/**
	 * This will flip an image around a given axes. It will return a new image.
	 * 
	 * @param image
	 * @param axes
	 *            the axes to flip the image (X_axes or Y_axes)
	 * @return The resulting image fliped around the given axes
	 */
	public static BufferedImage getFlippedImage(BufferedImage image, int axes) {
		BufferedImage result = getSameSizeImage(image);
		flipImage(result, axes);
		return result;
	}

	public static Color intropolateColor(Color c1, Color c2, double fract) {
		int red = (int) (c1.getRed() + fract * (c2.getRed() - c1.getRed()));
		int green = (int) (c1.getGreen() + fract
				* (c2.getGreen() - c1.getGreen()));
		int blue = (int) (c1.getBlue() + fract * (c2.getBlue() - c1.getBlue()));

		return new Color(red, green, blue);
	}

	public static void flipImage(BufferedImage image, int axes) {
		if (axes == X_AXIS) {
			int tmpRGB;
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight() / 2; y++) {
					tmpRGB = image.getRGB(x, y);
					image.setRGB(x, y,
							image.getRGB(x, image.getHeight() - y - 1));
					image.setRGB(x, image.getHeight() - y - 1, tmpRGB);

				}
			}
		} else if (axes == Y_AXIS) {
			int tmpRGB;
			for (int x = 0; x < image.getWidth() / 2; x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					tmpRGB = image.getRGB(x, y);
					image.setRGB(x, y,
							image.getRGB(image.getWidth() - x - 1, y));
					image.setRGB(image.getWidth() - x - 1, y, tmpRGB);
				}
			}
		} else if (axes == XY_AXIS) {
			flipImage(image, X_AXIS);
			flipImage(image, Y_AXIS);
		} else {
			throw new InvalidParameterException("No Valid axes selected");
		}
	}

	/**
	 * creates a random color
	 * 
	 * @return a random chosen color
	 */
	public static Color getRandomColor() {
		return new Color((int) (Math.random() * 255),
				(int) (Math.random() * 255), (int) (Math.random() * 255));
	}

	public static BufferedImage getGrayScale(BufferedImage source) {
		BufferedImage result = new BufferedImage(source.getWidth(),
				source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		getGrayScale(source, result);
		return result;
	}

	public static void getGrayScale(BufferedImage source, BufferedImage result) {
		if (source.getWidth() != result.getWidth()
				|| source.getHeight() != result.getHeight()) {
			throw new InvalidParameterException(
					"Images must have same dimensions");
		}
		for (int x = 0; x < source.getWidth(); x++) {
			for (int y = 0; y < source.getHeight(); y++) {
				Color oC = new Color(source.getRGB(x, y));
				int pxl = (oC.getRed() + oC.getGreen() + oC.getBlue()) / 3;
				Color newColor = new Color(pxl, pxl, pxl);
				result.setRGB(x, y, newColor.getRGB());
			}
		}
	}

	/**
	 * This class will create a gray scaled image that contains a gray gradient
	 * across the image.
	 * 
	 * @param width
	 * @param height
	 * @param freq
	 * @return
	 */
	public static BufferedImage getGrayTestImage(int width, int height, int freq) {
		return getGrayTestImage(width, height, freq, X_AXIS);
	}

	public static void getResizeImage(BufferedImage src, BufferedImage result) {
		getResizeImage(src, result, true, false);
	}

	/**
	 * This function will resize the src image to be drawn into the results
	 * image
	 * 
	 * @param src
	 * @param result
	 */
	public static void getResizeImage(BufferedImage src, BufferedImage result,
			boolean hightRes, boolean blurFirst) {
		Graphics2D g = result.createGraphics();
		if (blurFirst) {
			try {
				src = blurImage(src);
			} catch (Exception e) {
				System.out.println("The blur in image resize did not work :"
						+ e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
		if (hightRes) {
			GraphicsToolkit
					.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		}
		g.drawImage(src, 0, 0, result.getWidth(), result.getHeight(), null);
		g.dispose();
	}

	public static BufferedImage blurImage(BufferedImage image) {
		float ninth = 1.0f / 9.0f;
		float[] blurKernel = { ninth, ninth, ninth, ninth, ninth, ninth, ninth,
				ninth, ninth };

		Graphics2D g = image.createGraphics();
		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		RenderingHints hints = g.getRenderingHints();
		BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel),
				ConvolveOp.EDGE_NO_OP, hints);
		return op.filter(image, null);
	}

	public static BufferedImage getScaledImage(BufferedImage src, double scale) {
		return getScaledImage(src, scale, false, false);
	}

	public static BufferedImage getScaledImage(BufferedImage src, double scale,
			boolean highres, boolean blur) {
		BufferedImage rst = ImageOperations
				.getBi((int) (src.getWidth() * scale),
						(int) (src.getHeight() * scale));
		getResizeImage(src, rst, highres, blur);
		return rst;
	}

	public static BufferedImage getScaledFitImage(BufferedImage src, int sizeX,
			int sizeY, boolean highres, boolean blur) {
		BufferedImage rst = new BufferedImage(sizeX, sizeY,
				BufferedImage.TYPE_4BYTE_ABGR);

		int x1 = 0;
		int x2 = sizeX;
		int y1 = 0;
		int y2 = sizeY;

		double scaleX = sizeX / (double) src.getWidth();
		double scaleY = sizeY / (double) src.getHeight();

		double scale = 1;
		if (scaleX > scaleY) {
			scale = scaleY;
		} else {
			scale = scaleX;
		}
		double sX = (src.getWidth() * scale);
		x1 = (int) ((sizeX - sX) / 2);
		x2 = (int) ((sizeX + sX) / 2);

		double sY = (src.getHeight() * scale);
		y1 = (int) ((sizeY - sY) / 2);
		y2 = (int) ((sizeY + sY) / 2);

		System.out.println(x1 + "," + x2 + " - " + sX);
		System.out.println(y1 + "," + y2 + " - " + sY);

		ImageOperations.setImage(new Color(1, 1, 1, 0), rst);

		Graphics2D g2 = rst.createGraphics();
		if (highres) {
			GraphicsToolkit.setRenderingQuality(g2,
					GraphicsToolkit.HIGH_QUALITY);
		}
		g2.drawImage(src, x1, y1, x2 - x1, y2 - y1, null);

		return rst;
	}

	public static BufferedImage getGrayTestImage(int width, int height,
			int freq, int axis) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		GradientPaint gradient;
		if (axis == X_AXIS) {
			gradient = new GradientPaint(0, 0, Color.BLACK, width / freq, 0,
					Color.WHITE, true);
		} else if (axis == Y_AXIS) {
			gradient = new GradientPaint(0, 0, new Color(255, 255, 255), 0,
					height / freq, new Color(0, 0, 0), true);
		} else {
			throw new InvalidParameterException("Invalid axis selected");
		}

		Graphics2D g = image.createGraphics();
		g.setPaint(gradient);
		g.fill(image.getData().getBounds());
		g.dispose();
		return image;
	}

	public static BufferedImage getBi(Dimension d) {
		return getBi(d.width, d.height);
	}

	public static BufferedImage getBi(int size) {
		return getBi(size, size);
	}

	public static BufferedImage getBi(int wide, int high) {
		if (wide <= 0) {
			wide = 1;
		}
		if (high <= 0) {
			high = 1;
		}
		return new BufferedImage(wide, high, BufferedImage.TYPE_INT_RGB);
	}

	public static BufferedImage cloneImage(BufferedImage image) {
		int type = 0;
		if (image.getType() != BufferedImage.TYPE_CUSTOM) {
			type = image.getType();
		} else {
			type = BufferedImage.TYPE_4BYTE_ABGR;
		}
		return cloneImage(image, type);
	}

	public static BufferedImage cloneImage(BufferedImage image, int type) {
		BufferedImage result = new BufferedImage(image.getWidth(),
				image.getHeight(), type);

		result.getGraphics().drawImage(image, 0, 0, null);
		return result;
	}

	public static BufferedImage cloneImage(BufferedImage image, Color bg,
			float alpha) {
		BufferedImage result = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		setImage(bg, result);
		Graphics2D g = result.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alpha));
		g.drawImage(image, 0, 0, null);
		return result;
	}

	public static BufferedImage getSameSizeImage(BufferedImage image) {
		BufferedImage result;
		try {
			result = new BufferedImage(image.getWidth(), image.getHeight(),
					image.getType());
		} catch (IllegalArgumentException e) {
			result = new BufferedImage(image.getWidth(), image.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
		}
		/**
		 * Removed Drawing of imaged as it was wasiting time
		 */
		return result;
	}

	public static BufferedImage loadImage(File file) {

		try {

			return ImageOperations.cloneImage(ImageIO.read(file));

		} catch (Exception e) {
			throw new IllegalArgumentException(
					"The file appears to be an invalid image :\n"
							+ e.getMessage());
		}

	}

	/**
	 * This allows you to set the color of an image. It just fill the image with
	 * the chosen color.
	 * 
	 * @param Color
	 *            - Color to fill image
	 * @param image
	 *            BufferedImage - The image to be colored
	 */
	public static void setImage(Color c, BufferedImage image) {
		Graphics g = image.getGraphics();
		g.setColor(c);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.dispose();
	}

	public static void setImageColor(Color c, BufferedImage image) {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				image.setRGB(x, y, c.getRGB());
			}
		}
	}

	/**
	 * This will fill an image with random colored squares. it will break it up
	 * into numWide X numHigh squares.
	 * 
	 * @param numWide
	 * @param numHigh
	 * @param image
	 */
	public static void fillWithRandomColorSquares(int numWide, int numHigh,
			Image image) {
		// Calculate the dimension of the squares
		// in pixels
		int wide = image.getWidth(null) / numWide;
		int high = image.getHeight(null) / numHigh;

		for (int i = 0; i < numWide; i++) {
			for (int j = 0; j < numHigh; j++) {
				Graphics g = image.getGraphics();
				g.setColor(getRandomColor());
				g.fillRect(i * wide, j * high, wide, high);
				g.dispose();
			}
		}
	}

	/**
	 * The next two mehtds where got from http://javaalmanac
	 * .com/egs/java.awt.image/HasAlpha.html T
	 * 
	 * @param image
	 * @return
	 */
	// This method returns true if the specified
	// image has transparent pixels
	public static boolean hasAlpha(Image image) {
		// If buffered image, the color model is
		// readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the
		// image's color model;
		// grabbing a single pixel is usually
		// sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}

		// Get the image's color model
		ColorModel cm = pg.getColorModel();
		return cm.hasAlpha();
	}

	/**
	 * This method returns a buffered image with the contents of an image
	 * 
	 * @param image
	 *            Image - The image to be turend into a BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image image)
			throws IllegalArgumentException {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels
		// in the image are loaded
		image = new ImageIcon(image).getImage();
		// Determine if the image has transparent
		// pixels; for this method's
		// implementation, see e661 Determining If
		// an Image Has Transparent
		// Pixels
		boolean hasAlpha = false;// hasAlpha(
									// image );

		// Create a buffered image with a format
		// that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency
			// of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null),
					image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the
			// default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null),
					image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	/**
	 * This is a function that will calculate the average color of a section of
	 * an image image
	 * 
	 * @param BufferedImage
	 *            the image to get the average color
	 * @return Rectangle the area of the image to get Average of
	 */
	public static Color getAverageColor(BufferedImage image, Rectangle area) {
		// Color tempColor;
		int red = 0, green = 0, blue = 0;
		int colorVal = 0;
		int loopCount = 0;
		loopCount = 0;
		// Loop over the image and get each p
		for (int x = area.x; x < area.x + area.width; x++) {
			for (int y = area.y; y < area.y + area.height; y++) {
				/*
				 * tempColor = new Color(image.getRGB(x, y));
				 * 
				 * red += tempColor.getRed(); blue += tempColor.getBlue(); green
				 * += tempColor.getGreen();
				 */
				try {
					Color c = new Color(image.getRGB(x, y));
					red += c.getRed();
					green += c.getGreen();
					blue += c.getBlue();
					loopCount++;
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.print("\n");
					e.printStackTrace();
					System.out.printf(
							"\nImageSize[%d,%d,] Area[%d,%d,%d,%d] out[%d,%d]",
							image.getWidth(), image.getHeight(), area.x,
							area.y, area.width, area.height, x, y);
				}
			}
		}
		if (loopCount == 0) {
			loopCount = 1;
		}
		red /= loopCount;
		blue /= loopCount;
		green /= loopCount;
		return new Color(red, green, blue);
	}

	/**
	 * 
	 * @param image
	 * @return
	 */
	public static Color getAverageColor(BufferedImage image) {
		return getAverageColor(image, new Rectangle(0, 0, image.getWidth(),
				image.getHeight()));
	}

	/**
	 * 
	 * @param image
	 * @param region
	 * @return
	 */
	public static BufferedImage maskImage(BufferedImage image, Shape region,
			Color clipedColor) {
		BufferedImage result = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		maskImage(image, result, region, clipedColor);
		return result;
	}

	/**
	 * This will create a cliped image of the given Shape from the sourceData
	 * into the resultData. IT will set all others areas to the cliped color (
	 * area outside the shape of the region
	 * 
	 * @param sourceData
	 * @param result
	 * @param region
	 * @param clipedColor
	 */
	public static void maskImage(BufferedImage sourceData,
			BufferedImage result, Shape region, Color clipedColor) {
		ImageOperations.setImage(clipedColor, result);
		Graphics2D g = result.createGraphics();
		g.setClip(region);
		g.drawImage(sourceData, 0, 0, null);
		g.dispose();
	}

	public static BufferedImage cropImage(BufferedImage src, Rectangle region) {
		return src.getSubimage(region.x, region.y, region.width, region.height);
	}

	public static void fillRegion(BufferedImage image, Rectangle region, Color c) {
		Graphics2D g = image.createGraphics();
		g.setColor(c);
		g.fill(region);
		g.dispose();
	}

	/**
	 * This function just calls ImageIO.write but passes the file extends
	 * depending on the output file
	 * 
	 * @param src
	 * @param outputFile
	 * @throws IOException
	 */
	public static void saveImage(BufferedImage img, File outputFile)
			throws IOException {
		String ext = FileOperations.splitFile(outputFile)[2];
		ImageIO.write(img, ext, outputFile);
	}

	/**
	 * This function just calls ImageIO.write but passes the file extends
	 * depending on the output file
	 * 
	 * @param src
	 * @param outputFile
	 * @throws IOException
	 */
	public static void saveImage(BufferedImage img, String outputFile)
			throws IOException {
		saveImage(img, new File(outputFile));
	}

	public static void printPlanes(Color c1) {
		System.out.printf("Color RGBA[%d,%d,%d,%d]\t", c1.getRed(),
				c1.getBlue(), c1.getGreen(), c1.getAlpha());
		System.out.printf("Gy[%d]\tR[%d]\tG[%d]\tB[%d]\tA[%d]\n",
				ImageOperations.getPlaneFromRGBA(c1.getRGB(), PLANE_GRAY),
				ImageOperations.getPlaneFromRGBA(c1.getRGB(), PLANE_RED),
				ImageOperations.getPlaneFromRGBA(c1.getRGB(), PLANE_GREEN),
				ImageOperations.getPlaneFromRGBA(c1.getRGB(), PLANE_BLUE),
				ImageOperations.getPlaneFromRGBA(c1.getRGB(), PLANE_ALPHA));
		System.out.printf("Color RGBA[%d,%d,%d,%d]\n", c1.getRed(),
				c1.getBlue(), c1.getGreen(), c1.getAlpha());
		System.out.printf("Color RGBA[%d,%d,%d,%d]\n", c1.getRed(),
				c1.getBlue(), c1.getGreen(), c1.getAlpha());
	}

	public static BufferedImage getScaledFitImage(BufferedImage imgData,
			int sizeX, int sizeY) {
		return getScaledFitImage(imgData, sizeX, sizeY, true, true);
	}
}
