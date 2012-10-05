package com.joey.software.dft;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import com.joey.software.dsp.FastFourierTransform1;


public class SpectrumAnalyser extends Applet
{

	float[] signal;

	float[] spectrum;

	static float ampl = 1.0f; // default amplitude = 1.0 V

	static float rate = 8000.0f; // default sampling rate = 8000 samples/s

	int nSamples;

	GraphPlot plotSignal, plotSpectrum;

	Choice chWaveform, chSamples;

	TextField tfFreq, tfDCLevel, tfNoise;

	Checkbox cbAddDCLevel, cbAddNoise;

	Button btnSignal, btnSpectrum;

	void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	@Override
	public void init()
	{

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gb);
		plotSignal = new GraphPlot();
		plotSpectrum = new GraphPlot();
		// Signal plot
		buildConstraints(gbc, 0, 0, 1, 1, 1, 45);
		gbc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(plotSignal, gbc);
		add(plotSignal);

		// Spectrum plot
		buildConstraints(gbc, 0, 1, 1, 1, 0, 45);
		gbc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(plotSpectrum, gbc);
		add(plotSpectrum);

		// Control panel
		buildConstraints(gbc, 0, 2, 1, 1, 0, 10);
		gbc.fill = GridBagConstraints.BOTH;
		Panel pnlControls = new Panel();
		gb.setConstraints(pnlControls, gbc);
		add(pnlControls);
		pnlControls.setLayout(new GridLayout(3, 2));

		// Waveform selection
		Panel pnlWaveform = new Panel();
		pnlWaveform.setLayout(new FlowLayout(FlowLayout.LEFT));
		Label lblWaveform = new Label("Waveform");
		chWaveform = new Choice();
		chWaveform.addItem("Sine");
		chWaveform.addItem("Cosine");
		chWaveform.addItem("Square");
		chWaveform.addItem("Triangular");
		chWaveform.addItem("Sawtooth");
		chWaveform.select("Sine"); // default waveform
		pnlWaveform.add(lblWaveform);
		pnlWaveform.add(chWaveform);
		pnlControls.add(pnlWaveform);

		// Added DC level setting
		Panel pnlAddDCLevel = new Panel();
		pnlAddDCLevel.setLayout(new FlowLayout(FlowLayout.LEFT));
		cbAddDCLevel = new Checkbox("Add DC level");
		tfDCLevel = new TextField("0", 5);
		Label lblDCLevelUnit = new Label("V");
		pnlAddDCLevel.add(cbAddDCLevel);
		pnlAddDCLevel.add(tfDCLevel);
		pnlAddDCLevel.add(lblDCLevelUnit);
		pnlControls.add(pnlAddDCLevel);

		// Signal frequency setting
		Panel pnlFrequency = new Panel();
		pnlFrequency.setLayout(new FlowLayout(FlowLayout.LEFT));
		Label lblFreq = new Label("Frequency");
		tfFreq = new TextField("1000", 8);
		Label lblFreqUnit = new Label("Hz");
		pnlFrequency.add(lblFreq);
		pnlFrequency.add(tfFreq);
		pnlFrequency.add(lblFreqUnit);
		pnlControls.add(pnlFrequency);

		// Added random noise setting
		Panel pnlAddNoise = new Panel();
		pnlAddNoise.setLayout(new FlowLayout(FlowLayout.LEFT));
		cbAddNoise = new Checkbox("Add random noise");
		tfNoise = new TextField("0", 5);
		Label lblNoiseUnit = new Label("V");
		pnlAddNoise.add(cbAddNoise);
		pnlAddNoise.add(tfNoise);
		pnlAddNoise.add(lblNoiseUnit);
		pnlControls.add(pnlAddNoise);

		// Number of samples
		Panel pnlSamples = new Panel();
		pnlSamples.setLayout(new FlowLayout(FlowLayout.LEFT));
		Label lblSamples = new Label("Number of samples");
		chSamples = new Choice();
		chSamples.addItem("32");
		chSamples.addItem("64");
		chSamples.addItem("128");
		chSamples.addItem("256");
		chSamples.addItem("512");
		chSamples.select("256");
		pnlSamples.add(lblSamples);
		pnlSamples.add(chSamples);
		pnlControls.add(pnlSamples);

		Panel pnlButtons = new Panel();
		btnSignal = new Button("Plot signal");
		btnSpectrum = new Button("Spectrum");
		btnSpectrum.disable(); // can't show spectrum until signal generated
		pnlButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlButtons.add(btnSignal);
		pnlButtons.add(btnSpectrum);
		pnlControls.add(pnlButtons);
	}

	@Override
	public boolean action(Event evt, Object obj)
	{
		if (evt.target == btnSignal)
		{
			nSamples = Integer.parseInt(chSamples.getSelectedItem());
			signal = new float[nSamples];
			SignalGenerator sg = new SignalGenerator();
			sg.setWaveform(chWaveform.getSelectedItem());
			sg.setAmplitude(ampl);
			sg.setSamplingRate(rate);
			sg.setFrequency(Float.valueOf(tfFreq.getText()).floatValue());
			sg.setSamples(nSamples);
			boolean addDC = cbAddDCLevel.getState();
			sg.setDCLevelState(addDC);
			if (addDC)
				sg.setDCLevel(Float.valueOf(tfDCLevel.getText()).floatValue());
			boolean addNoise = cbAddNoise.getState();
			sg.setNoiseState(addNoise);
			if (addNoise)
				sg.setNoise(Float.valueOf(tfNoise.getText()).floatValue());
			signal = sg.generate();
			float maxValue = 0.0f;
			for (int i = 0; i < nSamples; i++)
				maxValue = Math.max(maxValue, Math.abs(signal[i]));
			plotSignal.setYmax(maxValue);
			plotSignal.setPlotValues(signal);
			btnSpectrum.enable();
			repaint();
			return true;
		} else if (evt.target == btnSpectrum)
		{
			spectrum = new float[nSamples / 2];
			FastFourierTransform1 fft = new FastFourierTransform1();
			spectrum = fft.fft(signal)[2];
			plotSpectrum.setPlotStyle(GraphPlot.SPECTRUM);
			plotSpectrum.setTracePlot(false);
			float maxValue = 0.0f;
			for (int i = 0; i < nSamples / 2; i++)
				maxValue = Math.max(maxValue, Math.abs(spectrum[i]));
			plotSpectrum.setYmax(maxValue);
			plotSpectrum.setPlotValues(spectrum);
			btnSpectrum.disable();
			repaint();
			return true;
		} else
			return false;
	}

}