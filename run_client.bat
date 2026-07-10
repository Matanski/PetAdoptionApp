@echo off
set JDK=C:\Users\Matan\.jdks\openjdk-26.0.1\bin
cd /d "%~dp0PetAdoptionClient"

set FX=lib\javafx-base-23-win.jar;lib\javafx-controls-23-win.jar;lib\javafx-graphics-23-win.jar;lib\javafx-fxml-23-win.jar

echo Compiling client...
dir /s /b src\main\java\*.java > "%TEMP%\clientsrc.txt"
"%JDK%\javac" -d out -cp "lib\gson-2.10.jar;%FX%" @"%TEMP%\clientsrc.txt"
if errorlevel 1 goto fail

echo Starting client...
"%JDK%\java" -cp "out;lib\gson-2.10.jar;%FX%" com.hit.client.Launcher
goto end

:fail
echo.
echo COMPILE FAILED.
pause

:end
