# Hibernate and associated jars

CLASSPATH=${CLASSPATH}:l2j-mmocore-1.0.8.jar

#jython
CLASSPATH=${CLASSPATH}:jython-2.2.1.jar
CLASSPATH=${CLASSPATH}:jython-engine-1.0.0.jar

CLASSPATH=${CLASSPATH}:bsf-2.0.jar
CLASSPATH=${CLASSPATH}:bsh-2.0b4.jar
CLASSPATH=${CLASSPATH}:bsh-engine-1.0.0.jar

CLASSPATH=${CLASSPATH}:commons-lang-2.1.jar

# For connection pool
CLASSPATH=${CLASSPATH}:c3p0-0.9.1.2.jar

# for logging usage
CLASSPATH=${CLASSPATH}:commons-logging-1.1.jar
CLASSPATH=${CLASSPATH}:log4j-1.2.14.jar

# for common input output 
CLASSPATH=${CLASSPATH}:commons-io-1.2.jar

# for performance usage
CLASSPATH=${CLASSPATH}:javolution-1.5.4.2.6.jar

# main jar
CLASSPATH=${CLASSPATH}:l2j-commons-1.1.6.jar
CLASSPATH=${CLASSPATH}:l2j-gameserver-1.0.2.jar

CLASSPATH=${CLASSPATH}:core-3.3.0.jar
CLASSPATH=${CLASSPATH}:java-engine-1.0.0.jar

# For SQL use
CLASSPATH=${CLASSPATH}:mysql-connector-java-5.1.5.jar

# For irc use
CLASSPATH=${CLASSPATH}:irclib-1.10.jar

# for configuration
CLASSPATH=${CLASSPATH}:./config/
CLASSPATH=${CLASSPATH}:.

export CLASSPATH
