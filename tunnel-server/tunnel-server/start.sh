for i in ./bin/*;
do CLASSPATH=$i:"$CLASSPATH";
done
 
export CLASSPATH=:$CLASSPATH
 
java -classpath .:${CLASSPATH} com.tunnel.server.SRun $1
