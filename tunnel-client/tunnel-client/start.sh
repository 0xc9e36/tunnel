for i in ./bin/*.jar;
do CLASSPATH=$i:"$CLASSPATH";
done
 
export CLASSPATH=:$CLASSPATH
 
java -classpath .:${CLASSPATH} com.tunnel.client.core.Run
