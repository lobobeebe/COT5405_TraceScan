# Environment
* OS: Windows 10
* Language: Java 1.8
* Processor: Intel(R) Core(TM) i5-6600K CPU @ 3.50GHz (4 CPUs), ~3.5GHz
* Memory: 16384MB RAM

# Generating Graph Information
1. Run: java -jar TraceScan.jar
* Two output files, "ModulateComms.csv" and "ModulateComps.csv" will be created.
** ModulateComms.csv
*** Each (x, y) entry tested 100000 Computers and x Communications. It took y nanoseconds to run.
** ModulateComps.csv
*** Each (x, y) entry tested x Computers and 100000 Communications. It took y nanoseconds to run.
2. Using a graphing program (such as Excel), plot the two series.