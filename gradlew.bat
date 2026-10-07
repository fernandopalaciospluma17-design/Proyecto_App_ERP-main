@rem
@rem  Gradle start up script for Windows
@rem
@if "%DEBUG%"=="" @echo off
@if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Find gradle launcher jar
set GRADLE_WRAPPER_JAR=%DIRNAME%\gradle\wrapper\gradle-wrapper.jar
if exist "%GRADLE_WRAPPER_JAR%" goto runWithWrapper

@rem If wrapper jar is missing, run with system/cached gradle
if exist "C:\Users\craft\.gradle\wrapper\dists\gradle-9.3.0-bin\79n14ral3mx1ozqr3csh2u872\gradle-9.3.0\bin\gradle.bat" (
    call "C:\Users\craft\.gradle\wrapper\dists\gradle-9.3.0-bin\79n14ral3mx1ozqr3csh2u872\gradle-9.3.0\bin\gradle.bat" %*
    goto end
)

:runWithWrapper
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% -classpath "%GRADLE_WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe_ / _powershell.exe_ exit code!
if  "%GRADLE_EXIT_CONSOLE%" == "1" set EXIT_CODE=1

:end
@if "%OS%"=="Windows_NT" rem endlocal
@exit /B %ERRORLEVEL%
