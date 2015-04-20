This is a very basic introduction on how to get the source code up and running. This assumes you know nothing about SVN or anything


(1) Install svn for windows (http://tortoisesvn.net/)

(2) Create a folder to act as your new eclipse workspace ( say c:\project\ )

(3) Create a folder to hold the project ( say c:\project\Cmoct Project\ )

(4) Once you have tortoise install if you right click on the new project folder (Cmoct Project) and you will have two new options Svn Checkout... and Tortoise SVN. Click the SVN Checkout...

(5) A Checkout dialog will appear, asking for a URL of repository
The url is https://cmoct-sourcecode.googlecode.com/svn/trunk/
Everything else should be fine, just click ok,
It will then checkout the source code.

(6) Download and install eclipse and Java JDK.
Once you have started eclipse, click File, Import.
Under General select Existing Project Into Workspace.
In root directory select the directory where you checked out your code (c:\project\Cmoct Project)
It should detect the project (if not click the white area under Porjects) then click Finish.
You should now have the code in your project, the two entry points are under
src-com-joey-programs

There is
CrossCorellationAnalysis.java (Main cmOCT) program
VolumeRenderingProgram.java (3D volume rendering software)