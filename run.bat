echo off
chcp 1251 > NUL
java -classpath C:\Users\marcky\workspace\sparql\lib\compile-time\antlr-2.7.7.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\antlr-3.0.1.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\commons-codec-1.6.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\commons-lang-2.6.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\httpclient-4.2.3.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\httpcore-4.2.2.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\j-text-utils-0.3.3.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\jcl-over-slf4j-1.6.4.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\jena-arq-2.11.1.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\jena-core-2.11.1.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\jena-iri-1.0.1.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\jena-sdb-1.4.1.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\jena-tdb-1.0.1.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\log4j-1.2.16.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\slf4j-api-1.6.4.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\slf4j-log4j12-1.6.4.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\stringtemplate-3.0.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\xercesImpl-2.11.0.jar;C:\Users\marcky\workspace\sparql\lib\compile-time\xml-apis-1.4.01.jar;C:\Users\marcky\workspace\sparql\lib\test\gunit.jar;C:\Users\marcky\workspace\sparql\bin QueryExec %1 %2