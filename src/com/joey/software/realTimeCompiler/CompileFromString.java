package com.joey.software.realTimeCompiler;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

/**
 * JSR 199 Demo application: compile from a String.
 *
 * <p><b>This is NOT part of any supported API.
 * If you write code that depends on this, you do so at your own
 * risk.  This code and its internal interfaces are subject to change
 * or deletion without notice.</b></p>
 *
 * @author Peter von der Ah&eacute;
 */
public class CompileFromString {

    /**
     * The name of the class used to evaluate expressions.
     */
    private final static String CLASS_NAME = "EvalExpression";

    /**
     * Object used to signal errors from evalExpression.
     */
    public final static Object ERROR = new Object() {
        @Override
		public String toString() { return "error"; }
    };

    /**
     * Compile and evaluate the specified expression using the
     * given compiler.
     * @param compiler a JSR 199 compiler tool used to compile the given expression
     * @param expression a Java Programming Language expression
     * @return the value of the expression; ERROR if any errors occured during compilation
     * @throws java.lang.Exception exceptions are ignored for brevity
     */
    public static Object evalExpression(JavaCompiler compiler,
                                        DiagnosticListener<JavaFileObject> listener,
                                        List<String> flags,
                                        String expression)
        throws Exception
    {
        // Use a customized file manager
        MemoryFileManager mfm =
            new MemoryFileManager(compiler.getStandardFileManager(listener, null, null));

        // Create a file object from a string
        JavaFileObject fileObject = MemoryFileManager.makeSource(CLASS_NAME,
            "public class " + CLASS_NAME + " {\n" +
            "    public static Object eval() throws Throwable {\n" +
            "        return " + expression + ";\n" +
            "    }\n}\n");

        JavaCompiler.CompilationTask task =
            compiler.getTask(null, mfm, listener, flags, null, Arrays.asList(fileObject));
        if (task.call()) {
            // Obtain a class loader for the compiled classes
            ClassLoader cl = mfm.getClassLoader(CLASS_OUTPUT);
            // Load the compiled class
            Class compiledClass = cl.loadClass(CLASS_NAME);
            // Find the eval method
            Method eval = compiledClass.getMethod("eval");
            // Invoke it
            return eval.invoke(null);
        } else {
            // Report that an error occured
            return ERROR;
        }
    }

    /**
     * Main entry point for program; ask user for expressions,
     * compile, evaluate, and print them.
     *
     * @param args ignored
     * @throws java.lang.Exception exceptions are ignored for brevity
     */
    public static void main(String... args) throws Exception {
        // Get a compiler tool
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final List<String> compilerFlags = new ArrayList();
        compilerFlags.add("-Xlint:all"); // report all warnings
        compilerFlags.add("-g:none"); // don't generate debug info
        String expression = "System.getProperty(\"java.vendor\")";
        while (true) {
            expression = JOptionPane.showInputDialog("Please enter a Java expression",
                                                     expression);
            if (expression == null)
                return; // end program on "cancel"
            long time = System.currentTimeMillis();
            Object result = evalExpression(compiler, null, compilerFlags, expression);
            time = System.currentTimeMillis() - time;
            System.out.format("Elapsed time %dms %n", time);
            if (result == ERROR)
                System.out.format("Error compiling \"%s\"%n", expression);
            else
                System.out.format("%s => %s%n", expression, result);
        }
    }
}

 