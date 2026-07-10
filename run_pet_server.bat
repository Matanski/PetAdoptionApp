@echo off
set JDK=C:\Users\Matan\.jdks\openjdk-26.0.1\bin
cd /d "%~dp0PetServer"

echo Compiling Pet server...
dir /s /b src\main\java\*.java > "%TEMP%\petsrc.txt"
"%JDK%\javac" -d out -cp "lib\AlgorithmModule.jar;lib\gson-2.10.jar" @"%TEMP%\petsrc.txt"
if errorlevel 1 goto fail

echo Starting Pet server on port 34567...
"%JDK%\java" -cp "out;lib\AlgorithmModule.jar;lib\gson-2.10.jar" com.hit.server.ServerDriver
goto end

:fail
echo.
echo COMPILE FAILED.
pause

:end
