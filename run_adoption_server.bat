@echo off
set JDK=C:\Users\Matan\.jdks\openjdk-26.0.1\bin
cd /d "%~dp0AdoptionServer"

echo Compiling Adoption server...
dir /s /b src\main\java\*.java > "%TEMP%\adoptsrc.txt"
"%JDK%\javac" -d out -cp "lib\gson-2.10.jar" @"%TEMP%\adoptsrc.txt"
if errorlevel 1 goto fail

echo Starting Adoption server on port 34568...
"%JDK%\java" -cp "out;lib\gson-2.10.jar" com.hit.server.ServerDriver
goto end

:fail
echo.
echo COMPILE FAILED.
pause

:end
