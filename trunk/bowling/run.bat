@echo off

cd build
java -Djava.library.path=.\lib\native\windows -classpath .\bin;.\lib\jme.jar;.\lib\jmephysics2.jar;.\lib\gluegen-rt.jar;.\lib\jogl.jar;.\lib\jorbis-0.0.17.jar;.\lib\gluegen-rt.jar;.\lib\jogl.jar;.\lib\jorbis-0.0.17.jar;.\lib\jinput.jar;.\lib\lwjgl_util_applet.jar;.\lib\lwjgl_util.jar;.\lib\lwjgl.jar;.\lib\jme-colladabinding.jar;.\lib\swt_windows.jar;.\lib\odejava-jni.jar bowling.main.Bowling
cd ..