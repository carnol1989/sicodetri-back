@echo off

REM Define the path to your JMeter installation
set JMETER_HOME=C:\apache-jmeter-5.6.3

REM Define the root path of your project
set PROJECT_HOME=C:\Intellij_2023.2.4\TFM\sicodetri

REM Define file and directory names
set JMX_FILENAME=SICODETRI_LoadTest.jmx
set PROPERTIES_FILENAME=perf-env.properties
set RESULTS_DIR_NAME=results
set REPORTS_DIR_NAME=reports
set JMX_BASE_NAME=SICODETRI_LoadTest

REM Build the full paths
set JMX_FILE_PATH=%PROJECT_HOME%\src\test\jmeter\%JMX_FILENAME%
set PROPERTIES_FILE_PATH=%PROJECT_HOME%\src\test\jmeter\%PROPERTIES_FILENAME%
set RESULTS_DIR_PATH=%PROJECT_HOME%\target\jmeter\%RESULTS_DIR_NAME%
set REPORTS_DIR_PATH=%PROJECT_HOME%\target\jmeter\%REPORTS_DIR_NAME%

REM Create the directories if they don't exist
if not exist "%RESULTS_DIR_PATH%" mkdir "%RESULTS_DIR_PATH%"
if not exist "%REPORTS_DIR_PATH%" mkdir "%REPORTS_DIR_PATH%"

REM Get current timestamp for unique filenames
set TIMESTAMP=%date:~-4,4%%date:~-7,2%%date:~-10,2%-%time:~0,2%%time:~3,2%%time:~6,2%

REM Set the path for the results and report files
set RESULTS_FILE_PATH=%RESULTS_DIR_PATH%\%TIMESTAMP%-%JMX_BASE_NAME%.csv
set REPORT_FILE_PATH=%REPORTS_DIR_PATH%\%TIMESTAMP%-%JMX_BASE_NAME%

REM Execute JMeter in non-GUI mode
echo Running JMeter in non-GUI mode...
"%JMETER_HOME%\bin\jmeter.bat" ^
    -n ^
    -t "%JMX_FILE_PATH%" ^
    -l "%RESULTS_FILE_PATH%" ^
    -o "%REPORT_FILE_PATH%" ^
    -q "%PROPERTIES_FILE_PATH%"

echo Performance test finished.
pause