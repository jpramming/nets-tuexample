@echo off

echo.
echo.
echo **********************************************
echo Step 1 - install jms-1.1.jar file
echo **********************************************
echo.
echo.
call mvn install:install-file -Dfile=jms-1.1.jar -DgroupId=javax.jms -DartifactId=jms -Dversion=1.1 -Dpackaging=jar  -DgeneratePom=true


echo.
echo.
echo **********************************************
echo Step 2 - install jmxri-1.2.1.jar file
echo **********************************************
echo.
echo.
call mvn install:install-file -Dfile=jmxri-1.2.1.jar -DgroupId=com.sun.jmx -DartifactId=jmxri -Dversion=1.2.1 -Dpackaging=jar  -DgeneratePom=true

echo.
echo.
echo **********************************************
echo Step 3 - install jmxtools-1.2.1.jar file
echo **********************************************
echo.
echo.
call mvn install:install-file -Dfile=jmxtools-1.2.1.jar -DgroupId=com.sun.jdmk -DartifactId=jmxtools -Dversion=1.2.1 -Dpackaging=jar  -DgeneratePom=true


echo.
echo.
echo **********************************************
echo Step 4 - install ooapi
echo **********************************************
echo.
echo.
call mvn install:install-file -Dfile=ooapi-1.214.1.jar -DpomFile=ooapi-1.214.1.pom 
