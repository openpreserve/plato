java -Xms256m -Xmx512m -cp "./xalan/xalan.jar" org.apache.xalan.xslt.Process -IN $1.xml -OUT $1_new.xml -XSL $2
