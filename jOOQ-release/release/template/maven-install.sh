#!/bin/sh
VERSION=3.13.0-SNAPSHOT

if [ -f jOOQ-javadoc/jooq-$VERSION-javadoc.jar ]; then
  JAVADOC_JOOQ=-Djavadoc=jOOQ-javadoc/jooq-$VERSION-javadoc.jar
  JAVADOC_JOOQ_META=-Djavadoc=jOOQ-javadoc/jooq-meta-$VERSION-javadoc.jar
  JAVADOC_JOOQ_META_EXTENSIONS=-Djavadoc=jOOQ-javadoc/jooq-meta-extensions-$VERSION-javadoc.jar
  JAVADOC_JOOQ_CODEGEN=-Djavadoc=jOOQ-javadoc/jooq-codegen-$VERSION-javadoc.jar
  JAVADOC_JOOQ_CODEGEN_MAVEN=-Djavadoc=jOOQ-javadoc/jooq-codegen-maven-$VERSION-javadoc.jar

  JAVADOC_JOOQ_CHECKER=-Djavadoc=jOOQ-javadoc/jooq-checker-$VERSION-javadoc.jar




  JAVADOC_JOOQ_SCALA_2_11=-Djavadoc=jOOQ-javadoc/jooq-scala_2.11-$VERSION-javadoc.jar
  JAVADOC_JOOQ_SCALA_2_12=-Djavadoc=jOOQ-javadoc/jooq-scala_2.12-$VERSION-javadoc.jar
fi

if [ -f jOOQ-src/jooq-$VERSION-sources.jar ]; then
  SOURCES_JOOQ=-Dsources=jOOQ-src/jooq-$VERSION-sources.jar
  SOURCES_JOOQ_META=-Dsources=jOOQ-src/jooq-meta-$VERSION-sources.jar
  SOURCES_JOOQ_META_EXTENSIONS=-Dsources=jOOQ-src/jooq-meta-extensions-$VERSION-sources.jar
  SOURCES_JOOQ_CODEGEN=-Dsources=jOOQ-src/jooq-codegen-$VERSION-sources.jar
  SOURCES_JOOQ_CODEGEN_MAVEN=-Dsources=jOOQ-src/jooq-codegen-maven-$VERSION-sources.jar

  SOURCES_JOOQ_CHECKER=-Dsources=jOOQ-src/jooq-checker-$VERSION-sources.jar




  SOURCES_JOOQ_SCALA_2_11=-Djavadoc=jOOQ-src/jooq-scala_2.11-$VERSION-sources.jar
  SOURCES_JOOQ_SCALA_2_12=-Djavadoc=jOOQ-src/jooq-scala_2.12-$VERSION-sources.jar
fi

mvn install:install-file -Dfile=jOOQ-pom/pom.xml                           -DgroupId=org.jooq -DartifactId=jooq-parent          -Dversion=$VERSION -Dpackaging=pom
mvn install:install-file -Dfile=jOOQ-lib/jooq-$VERSION.jar                 -DgroupId=org.jooq -DartifactId=jooq                 -Dversion=$VERSION -Dpackaging=jar $JAVADOC_JOOQ                 $SOURCES_JOOQ                 -DpomFile=jOOQ-pom/jooq/pom.xml
mvn install:install-file -Dfile=jOOQ-lib/jooq-meta-$VERSION.jar            -DgroupId=org.jooq -DartifactId=jooq-meta            -Dversion=$VERSION -Dpackaging=jar $JAVADOC_JOOQ_META            $SOURCES_JOOQ_META            -DpomFile=jOOQ-pom/jooq-meta/pom.xml
mvn install:install-file -Dfile=jOOQ-lib/jooq-meta-extensions-$VERSION.jar -DgroupId=org.jooq -DartifactId=jooq-meta-extensions -Dversion=$VERSION -Dpackaging=jar $JAVADOC_JOOQ_META_EXTENSIONS $SOURCES_JOOQ_META_EXTENSIONS -DpomFile=jOOQ-pom/jooq-meta-extensions/pom.xml
mvn install:install-file -Dfile=jOOQ-lib/jooq-codegen-$VERSION.jar         -DgroupId=org.jooq -DartifactId=jooq-codegen         -Dversion=$VERSION -Dpackaging=jar $JAVADOC_JOOQ_CODEGEN         $SOURCES_JOOQ_CODEGEN         -DpomFile=jOOQ-pom/jooq-codegen/pom.xml
mvn install:install-file -Dfile=jOOQ-lib/jooq-codegen-maven-$VERSION.jar   -DgroupId=org.jooq -DartifactId=jooq-codegen-maven   -Dversion=$VERSION -Dpackaging=jar $JAVADOC_JOOQ_CODEGEN_MAVEN   $SOURCES_JOOQ_CODEGEN_META    -DpomFile=jOOQ-pom/jooq-codegen-maven/pom.xml

mvn install:install-file -Dfile=jOOQ-lib/jooq-checker-$VERSION.jar         -DgroupId=org.jooq -DartifactId=jooq-checker         -Dversion=$VERSION -Dpackaging=jar $JAVADOC_JOOQ_CHEKER          $SOURCES_JOOQ_CHECKER         -DpomFile=jOOQ-pom/jooq-checker/pom.xml




mvn install:install-file -Dfile=jOOQ-lib/jooq-scala_2.11-$VERSION.jar      -DgroupId=org.jooq -DartifactId=jooq-scala_2.11      -Dversion=$VERSION -Dpackaging=jar $JAVADOC_JOOQ_SCALA_2_11      $SOURCES_JOOQ_SCALA_2_11      -DpomFile=jOOQ-pom/jooq-scala_2.11/pom.xml
mvn install:install-file -Dfile=jOOQ-lib/jooq-scala_2.12-$VERSION.jar      -DgroupId=org.jooq -DartifactId=jooq-scala_2.12      -Dversion=$VERSION -Dpackaging=jar $JAVADOC_JOOQ_SCALA_2_12      $SOURCES_JOOQ_SCALA_2_12      -DpomFile=jOOQ-pom/jooq-scala_2.12/pom.xml


echo
echo
echo The different jOOQ editions are released under different Maven groupIds!
echo ------------------------------------------------------------------------
echo - org.jooq.pro        : The jOOQ Express, Professional, and Enterprise Editions
echo - org.jooq.pro-java-8 : The jOOQ Express, Professional, and Enterprise Editions with support for Java 8
echo - org.jooq.pro-java-6 : The jOOQ Express, Professional, and Enterprise Editions with support for Java 6
echo - org.jooq.trial      : The jOOQ Trial Edition
echo - org.jooq            : The jOOQ Open Source Edition