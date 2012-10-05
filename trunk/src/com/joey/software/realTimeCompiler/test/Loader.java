package com.joey.software.realTimeCompiler.test;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import com.joey.software.SwingTools.JTextAreaWriter;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.realTimeCompiler.MemoryFileManager;
import com.joey.software.stringToolkit.StringOperations;


public class Loader extends JPanel
{
	JButton compileButton = new JButton("Compile");

	String className = "Processor";

	// Create a file object from a string
	String srcHeader = "public class " + className + " {\n"
			+ "    public static void processFrame(byte[][][] data, int frame)\n {\n";

	String srcEnd = "\n" + "    }\n}\n";

	JTextArea currentFunction = new JTextArea(
			"for(int i =0; i< 100;i++)\n{\n\tSystem.out.println(\"Hello\");\n}");

	JTextArea errorOutput = new JTextArea();

	Class compiledClass = null;

	byte[][][] nums = new byte[10][10][10];

	JTextAreaWriter compileConsole = new JTextAreaWriter();
	JTextAreaWriter jdkConsole = new JTextAreaWriter();

	public Loader()
	{
		createJPanel();
		System.setOut(new PrintStream(jdkConsole.getOutputStream()));
		System.setErr(new PrintStream(jdkConsole.getOutputStream()));
	}

	public void createClass() throws Exception
	{
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final List<String> compilerFlags = new ArrayList();
		compilerFlags.add("-Xlint:all"); // report
											// all
											// warnings
		compilerFlags.add("-g:none"); // don't
										// generate
										// debug
										// info

		// Use a customized file manager
		MemoryFileManager mfm = new MemoryFileManager(
				compiler.getStandardFileManager(null, null, null));

		String src = srcHeader + currentFunction.getText() + srcEnd;
		JavaFileObject fileObject = MemoryFileManager.makeSource(className, src);

		compileConsole.append("Starting compilation\n");
		JavaCompiler.CompilationTask task = compiler
				.getTask(compileConsole, mfm, null, compilerFlags, null, Arrays
						.asList(fileObject));
		compileConsole.append("Ending compilation\n");
		
		compileConsole.append("Checking Errors\n");
		if (task.call())
		{
			// Obtain a class loader for the
			// compiled classes
			ClassLoader cl = mfm.getClassLoader(CLASS_OUTPUT);
			// Load the compiled class
			compiledClass = cl.loadClass(className);
			compileConsole.append("Success\n");
		} else
		{
			compileConsole.append("Fail\n");
			// Report that an error occured
			compiledClass = null;
		}
		
	}

	public void runMethod() throws Exception
	{
		if (compiledClass != null)
		{
			// Find the eval method
			Method eval = compiledClass.getMethod("processFrame", byte[][][].class, int.class);
			// Invoke it
			eval.invoke(null, nums, 2);
		} 
	}

	public void process()
	{
		try
		{
			createClass();
			runMethod();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createJPanel()
	{
		JPanel source = new JPanel(new BorderLayout());
		source.setBorder(BorderFactory.createTitledBorder("SRC"));
		
		source.add(new JLabel("<HTML>"+StringOperations.replace(srcHeader, "\n", "<BR>")+"</HTML>"), BorderLayout.NORTH);
		source.add(new JScrollPane(currentFunction));
		source.add(new JLabel("<HTML>"+StringOperations.replace(srcEnd, "\n", "<BR>")+"</HTML>"), BorderLayout.SOUTH);
		
		JPanel compileHold =new JPanel(new BorderLayout());
		compileHold.setBorder(BorderFactory.createTitledBorder("Compile Log"));
		compileHold.add(new JScrollPane(compileConsole.getTextArea()));
		
		JPanel consoleHold =new JPanel(new BorderLayout());
		consoleHold.setBorder(BorderFactory.createTitledBorder("Compile Log"));
		consoleHold.add(new JScrollPane(jdkConsole.getTextArea()));
		
		JPanel consoles = new JPanel(new GridLayout(1,2));
		consoles.add(compileHold);
		consoles.add(consoleHold);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		panel.add(source);
		panel.add(consoles);

		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		add(compileButton, BorderLayout.SOUTH);

		compileButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread t = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						jdkConsole.getTextArea().setText("");
						process();
					}
				});
				t.start();

			}
		});
	}

	public static void main(String input[])
	{
		Loader load = new Loader();
		JFrame f = FrameFactroy.getFrame(load);
		FrameWaitForClose close = new FrameWaitForClose(f);
		close.waitForClose();

	}
}
