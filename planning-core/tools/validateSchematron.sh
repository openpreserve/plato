java -jar saxon9he.jar -xsl:../src/test/resources/schematron/iso_svrl_for_xslt2.xsl -s:$1 -o:$1.xsl
java -jar saxon9he.jar -xsl:$1.xsl -s:$2 -o:$2_report.xml

