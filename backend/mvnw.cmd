@IF "%DEBUG%"=="" @ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION ENABLEEXTENSIONS

SET "BASE_DIR=%~dp0"
IF "%BASE_DIR:~-1%"=="\" SET "BASE_DIR=%BASE_DIR:~0,-1%"

@REM 1. Locate Java 17
SET "JAVA_EXE="

IF DEFINED JAVA_HOME (
    SET "TEMP_HOME=%JAVA_HOME:"=%"
    IF EXIST "!TEMP_HOME!\bin\java.exe" (
        SET "JAVA_EXE=!TEMP_HOME!\bin\java.exe"
    )
)

IF NOT DEFINED JAVA_EXE (
    IF EXIST "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\java.exe" (
        SET "JAVA_HOME=%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
        SET "JAVA_EXE=%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\java.exe"
    ) ELSE IF EXIST "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\java.exe" (
        SET "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
        SET "JAVA_EXE=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\java.exe"
    ) ELSE IF EXIST "C:\Program Files\Java\jdk-17\bin\java.exe" (
        SET "JAVA_HOME=C:\Program Files\Java\jdk-17"
        SET "JAVA_EXE=C:\Program Files\Java\jdk-17\bin\java.exe"
    )
)

IF NOT DEFINED JAVA_EXE (
    SET "JAVA_EXE=java.exe"
)

@REM 2. Check bundled Maven distribution
IF EXIST "%BASE_DIR%\.mvn\apache-maven-3.9.6\bin\mvn.cmd" (
    CALL "%BASE_DIR%\.mvn\apache-maven-3.9.6\bin\mvn.cmd" %*
    EXIT /B %ERRORLEVEL%
)

@REM 3. Fallback: Download Maven 3.9.6 if needed
IF NOT EXIST "%BASE_DIR%\.mvn\apache-maven-3.9.6" (
    echo Downloading Apache Maven 3.9.6 for first-time setup...
    powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip', '$env:TEMP\apache-maven-3.9.6-bin.zip'); Expand-Archive -Path '$env:TEMP\apache-maven-3.9.6-bin.zip' -DestinationPath '%BASE_DIR%\.mvn' -Force}"
    IF EXIST "%BASE_DIR%\.mvn\apache-maven-3.9.6\bin\mvn.cmd" (
        CALL "%BASE_DIR%\.mvn\apache-maven-3.9.6\bin\mvn.cmd" %*
        EXIT /B %ERRORLEVEL%
    )
)

@REM 4. Standard Wrapper jar fallback
SET "WRAPPER_JAR=%BASE_DIR%\.mvn\wrapper\maven-wrapper.jar"
IF EXIST "%WRAPPER_JAR%" (
    "%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory="%BASE_DIR%" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
    EXIT /B %ERRORLEVEL%
)

echo Error: Maven could not be executed.
EXIT /B 1
