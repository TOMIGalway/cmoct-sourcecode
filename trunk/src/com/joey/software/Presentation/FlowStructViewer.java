package com.joey.software.Presentation;


import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.colorMapping.ColorMap;

public class FlowStructViewer
{

	public static void main(String input[]) throws IOException
	{
		float scale = 0.3f;
		BufferedImage struct = ImageOperations.getScaledImage(ImageOperations.getAverageImage(ImageFileSelectorPanel.getUserSelection()), scale);
		BufferedImage flow = ImageOperations.getScaledImage(ImageOperations.getAverageImage(ImageFileSelectorPanel.getUserSelection()), scale);
		
		BufferedImage rst = ImageOperations.getSameSizeImage(struct);
		
		getCompositeImage(rst, getData(struct), 0, 255, getData(flow), 0, 255);
		
		FrameFactroy.getFrame(rst);
	}
	
	public static float[][] getData(BufferedImage img)
	{
		float[][] rst = new float[img.getWidth()][img.getHeight()];
		
		for(int x= 0; x < img.getWidth(); x++)
		{
			for(int y= 0;y < img.getHeight(); y++)
			{
				rst[x][y] = ImageOperations.getGrayScale(img.getRGB(x, y));
			}
		}
		return rst;
	}
	
	public static void getCompositeImage(BufferedImage data, float[][] dataSrc, float minSrc, float maxSrc, float[][] dataCorr, float minCorr, float maxCorr)
	{
		ColorMap map = ColorMap.getColorMap(ColorMap.TYPE_GLOW);
		float val = 1;

		for (int x = 0; x < data.getWidth(); x++)
		{
			for (int y = 0; y < data.getHeight(); y++)
			{
				val = (dataSrc[x][y] - minSrc) / (maxSrc - minSrc);

				if (val > 1)
				{
					val = 1;
				}
				if (val < 0)
				{
					val = 0;
				}
				data
						.setRGB(x, y, ImageOperations
								.getGrayRGB((int) (255 * val)));
			}
		}

		Graphics2D g = data.createGraphics();

		for (int x = 0; x < data.getWidth(); x++)
		{
			for (int y = 0; y < data.getHeight(); y++)
			{
				val = (dataCorr[x][y] - minCorr) / (maxCorr - minCorr);
				if (val > 1)
				{
					val = 1;
				}
				if (val < 0)
				{
					val = 0;
				}
				val = val;
				g.setComposite(AlphaComposite
						.getInstance(AlphaComposite.SRC_OVER, 1f * val));

				g.setColor(map.getColorInterpolate(val));
				g.drawRect(x, y, 1, 1);
			}
		}
	}
}
