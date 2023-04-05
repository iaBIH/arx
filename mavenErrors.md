Using: mvn compile -Dcore 

- [WARNING] The POM for com.github.ralfstuckert.pdfbox-layout:pdfbox2-layout:jar:1.0.0 is missing, no dependency information available
  - solved by:  <artifactId>pdfbox2-layout</artifactId>     <version>1.0.1</version> 
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.111.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.112.0 is invalid, transitive dependencies (if any) will not be available, - enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.113.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.114.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.114.100 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.115.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.115.100 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.116.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.116.100 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.117.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.118.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.119.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.120.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.121.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.122.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
- [WARNING] The POM for org.eclipse.platform:org.eclipse.swt:jar:3.123.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details

  -solved by removing other os deps then use 
    
      clear && clear && clear && rm -r target && mvn clean && mvn -X -Dosgi.platform=gtk.linux.x86_64 compile -Dcore 

- [WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
- [WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
