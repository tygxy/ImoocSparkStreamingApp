# ImoocSparkStreamingApp

## 1.Spark Streaming功能总结
- 使用流程
	- 启动zk 
	```
	./zkServer.sh start
	```
	- 启动kafka
	```
	./kafka-server-start.sh -daemon $KAFKA_HOME/conf/server.properties 
	```
	- 启动Flume
	```
	./flume-ng agent --name a1 --conf $FLUME/conf/ --conf-file /Users/guoxingyu/Documents/work/spark/sql/generate_log/streaming_project.conf --Dflume.root.logger=INFO,console
	```
	- Spark Streaming任务开启

- Flume配置参考 /generate_log/streaming_project.conf 
- SparkStreming使用kafaka接受数据，参考/sparktrain/src/main/scala/com/imooc/spark/Project/Spark/ImoocStatStreamingApp.scala
- 注意版本 kafka(0.8.22)和spark(2.1.2)

