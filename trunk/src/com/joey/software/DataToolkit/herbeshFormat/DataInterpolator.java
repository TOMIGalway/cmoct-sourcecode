package com.joey.software.DataToolkit.herbeshFormat;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.plottingToolkit.PlotingToolkit;


public class DataInterpolator {
	public float[] y;
	public float[] x;
	public float[] xRescale;
	public float[] yRescale;

	int[] xAVal;
	int[] xBVal;
	float[] dxVal;

	public DataInterpolator(float x[], float xRescale[]) {
		this.x =x;
		this.xRescale=xRescale;
		updateIndex();
	}
	
	private void updateIndexValue() {
		for(int i= 0; i < xRescale.length; i++){
			
			float xVal = xRescale[i];
			int xA = 0;
			int xM = 0;
			int xB = x.length-1;
			int dx = xB-xA;
					
			// 	Ensure value not outside dataset
			if (x[xA] > xVal) {
				xB=xA;
				dx = 0;
			}else
			if (x[xB] < xVal) {
				xA=xB;
				dx = 0;
			}else{	
				while (dx > 1) {
					xM = (int) (xA + (xB - xA) / 2f);
					if (xVal == x[xM]) {
						xA=xM;
						xB=xM;
					} else if (xVal < x[xM]) {
						xB = xM;
					} else {
						xA = xM;
					}
					dx = xB - xA;
				}
				
				xAVal[i] = xA;
				xBVal[i] = xB;
				
				if(dx == 0){
					dxVal[i]=0;
				}else{
					dxVal[i]=x[xB]-x[xA];
				}
			}
		}
	}

	public void updateIndex() {
		xAVal = new int[x.length];
		xBVal = new int[x.length];
		dxVal = new float[x.length];
		updateIndexValue();
	}
	
	public void calc() {
		for (int i = 0; i < yRescale.length; i++) {
			yRescale[i] =  y[xAVal[i]] + (xRescale[i] - x[xAVal[i]]) * ((y[xBVal[i]] - y[xAVal[i]]) / dxVal[i]);
		}
	}

	public static float[] getData(int count, float min, float max) {
		float[] rst = new float[count];
		for (int i = 0; i < count; i++) {
			rst[i] = min + (max - min) * ((i / (float) (count - 1)));
		}
		;
		return rst;
	}

	public static float[] getSinData(float[] x) {
		float[] y = new float[x.length];
		for (int i = 0; i < x.length; i++) {
			y[i] = (float) Math.sin(x[i]);
		}
		return y;
	}

	public static void main(String input[]) {
		int num = 1024;
		float[] x = getData(num, 0f, 2 * 3.14f);
		float[] y = getSinData(x);
		float[] xRescale = getData(num, 0f, 1 * 3.14f);
		float[] yRescale = new float[num];

		DataInterpolator inter = new DataInterpolator(x,xRescale);
		inter.y = y;
		inter.yRescale = yRescale;
		
		
		int count = 10000;
		float total = 0;
		for(int i = 0;i < count; i++){
			long start = System.nanoTime();
			inter.calc();
			total += System.nanoTime()-start;
		}
		total/=count;
		System.out.println(total);
		FrameWaitForClose.waitForFrame(FrameFactroy.getFrame(PlotingToolkit
				.getChartPanel(new float[][] { inter.y, inter.yRescale })));
	}

}