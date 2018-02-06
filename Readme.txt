-----SETUP FOR HADOOP-----

First, to mount EBS volume, run the mount_ebs.sh which is in the configuration files folder

Next, for initial setup for java, gcc, ant, hadoop, spark, scala, run the initial_setup.sh which is in the configuration files folder


Now, do 
vi .bashrc 
and add the following paths to the bashrc file:
export CONF=/home/ubuntu/hadoop-2.7.4/etc/hadoop
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
export PATH=$PATH:$/home/ubuntu/hadoop-2.7.4/bin
export PATH=${JAVA_HOME}/bin:${PATH}
export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar
export PATH=$PATH:/home/ubuntu/scala/bin
export SPARK_HOME=/opt/spark
PATH=$PATH:$SPARK_HOME/bin
export PATH

Then do,
source ~/.bashrc

Now, do 
vi Hadoop_TeraSort.java
and put the code to Hadoop_TeraSort.java and save it under hadoop-2.7.4 directory.

Now compile the terasort code
bin/hadoop com.sun.tools.javac.Main Hadoop_TeraSort.java
jar cf Hadoop_TeraSort.jar *.class


Now configure the following files under 
cd /home/ubuntu/hadoop-2.7.2/etc/hadoop

vi core-site.xml
<property>
<name>fs.default.name</name>
<value>hdfs://ec2-18-217-101-219.us-east-2.compute.amazonaws.com:9000</value>
</property>
<property>
<name>hadoop.tmp.dir</name>
<value>/raj</value>
<description>base location for other hdfs directories.</description>
</property>

vi hadoop-env.sh
set JAVA_HOME=/usr/lib/jvm/java-8-oracle

vi hdfs-site.xml
<property>
<name>dfs.replication</name>
<value>1</value>
</property>
<property>
<name>dfs.permissions</name>
<value>false</value>
</property>

vi yarn-site.xml
<property>
<name>yarn.nodemanager.aux-services</name>
<value>mapreduce_shuffle</value>
</property>
<property>
<name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
<value>org.apache.hadoop.mapred.ShuffleHandler</value></property>
<property>
<name>yarn.resourcemanager.resource-tracker.address</name>
<value>ec2-18-217-101-219.us-east-2.compute.amazonaws.com:9025</value>
</property>
<property>
<name>yarn.resourcemanager.scheduler.address</name>
<value>ec2-18-217-101-219.us-east-2.compute.amazonaws.com:9030</value>
</property>
<property>
<name>yarn.resourcemanager.address</name>
<value>ec2-18-217-101-219.us-east-2.compute.amazonaws.com:9050</value>
</property>
<property>
<name>yarn.resourcemanager.webapp.address</name>
<value>ec2-18-217-101-219.us-east-2.compute.amazonaws.com:9006</value>
</property>
<property>
<name>yarn.resourcemanager.admin.address</name>
<value>ec2-18-217-101-219.us-east-2.compute.amazonaws.com:9008</value>
</property><!---->
<property>
<name>yarn.nodemanager.vmem-pmem-ratio</name>
<value>2.1</value>
</property>

Now,
cp /home/ubuntu/hadoop-2.7.4/etc/hadoop/mapred-site.xml.template /home/ubuntu/hadoop-2.7.4/etc/hadoop/mapred-site.xml

vi mapred-site.xml
<property>
<name>mapreduce.job.tracker</name>
<value>hdfs://ec2-18-217-101-219.us-east-2.compute.amazonaws.com:9001</value>
</property>
<property>
<name>mapreduce.framework.name</name>
<value>yarn</value>
</property>

Now, modify host and slaves files as per requirement and configuration
do, 
cd hadoop-2.7.4/etc/hadoop

For 1 node:-
slaves file:-
ec2-18-217-101-219.us-east-2.compute.amazonaws.com
//hosts file
172-31-11-63 ec2-18-217-101-219.us-east-2.compute.amazonaws.com

For multiple nodes:-
sudo vi /etc/hosts
//slave node will have masters and slaves node private ip and dns in hosts file.
//master node will have all slaves and master node private ip and dns in hosts file.

vi slaves
for instance, slaves in the master node would have:
ec2-13.59.11.106.us-west-2.compute.amazonaws.com

//for example masters hosts file:-
172.31.5.141 ec2-13.59.11.106.us-west-2.compute.amazonaws.com
172.31.2.162 ec2-18.217.97.139.us-west-2.compute.amazonaws.com
172.31.15.215 ec2-18.216.227.208.us-west-2.compute.amazonaws.com
172.31.11.249 ec2-18.220.154.235.us-west-2.compute.amazonaws.com
172.31.5.181 ec2-18.217.199.161.us-west-2.compute.amazonaws.com



---Follow below steps on all nodes as per requirement
eval "$(ssh-agent)"
chmod 400 i34xlarge.pem
ssh-add i34xlarge.pem
ssh-keygen -t rsa

--For multiple nodes
ssh-copy-id -i ~/.ssh/id_rsa.pub ubuntu@ec2-18-217-101-219.us-east-2.compute.amazonaws.com
chmod 0600 ~/.ssh/authorized_keys


---Starting hadoop by following commands:
cd hadoop-2.7.4/
bin/hadoop namenode -format
ssh localhost 
cd sbin
./start-dfs.sh        
./start-yarn.sh
       
---To check if all name nodes and data nodes are up and running, check using following command:-
jps


---Output for jps command:-
67534 NodeManager
75477 Jps
12633 ResourceManager
98734 DataNode
49080 NameNode


Now, Create and setup hdfs directory:-
cd
cd hadoop-2.7.4
bin/hadoop fs -mkdir /hdfs_raj

cd /raj
mkdir data
cd data

Now Download gensort application and generate file of 128Gb and copy that file to hdfs
cd /data
wget www.ordinal.com/try.cgi/gensort-linux-1.5.tar.gz
tar -xzvf gensort-linux-1.5.tar.gz
cd 64
./gensort -a 120000000000 128gbfile
cd 
cd hadoop-2.7.4
bin/hadoop dfs -copyFromLocal /raj/data/64/128gbfile /hdfs_raj
bin/hadoop dfs -ls /hdfs_raj/
bin/hadoop dfs -rm -r -f /hdfs_raj/output

Now Execute hadoop program to sort 128GB file.
bin/hadoop jar Hadoop_TeraSort.jar Hadoop_TeraSort /hdfs_raj/128gbfile /hdfs_raj/output

---Command to copy the file from hdfs to our instance EC2:-
bin/hadoop dfs -get /hdfs_raj/output/part-r-00000 /raj

---Command to check if file is properly sorted or not
cd /raj/data/64
./valsort /raj/part-r-00000





-----SETUP FOR SPARK-----
Firstly, Scala and Spark will be installed from the initial_setup.sh and mount.sh which is run at start of setups
And also its paths are configured in the .bashrc file.

Referring from: https://sparkour.urizone.net/recipes/installing-ec2/ for using 16 cores of the instance i3.4xlarge.
# Create a symbolic link to make it easier to access
sudo ln -fs spark-2.2.0-bin-hadoop2.7 /opt/spark

Then, check for the version using:
# Confirm that spark-submit is now in the PATH.
spark-submit --version

Then, 
# Create a Log4J configuration file from the provided template.
cp $SPARK_HOME/conf/log4j.properties.template $SPARK_HOME/conf/log4j.properties
vi $SPARK_HOME/conf/log4j.properties
# (on line 19 of the file, change the log level from INFO to ERROR)
# log4j.rootCategory=ERROR, console
# Save the file and exit the text editor.

--Now create file using gensort:-
wget www.ordinal.com/try.cgi/gensort-linux-1.5.tar.gz
tar -xzvf gensort-linux-1.5.tar.gz
cd 64
./gensort -a 12000000000 128gbfile

--Now, Create directory in hdfs
cd 
cd ephemeral-hdfs
bin/hadoop fs -mkdir /raj_spark
// copy 128 Gb input file to hdfs.
bin/hadoop fs -Ddfs.replication=1 -put /raj/64/128gbfile /raj_spark

--Now, Check the content of hdfs by using the command:
bin/hadoop dfs -ls /raj_spark/


--Now create a scala file spark_terasort.scala and write the below code in it and save it:

val starttime = System.currentTimeMillis()
val inputfile = sc.textFile("hdfs:////raj_spark/128gbfile")
val file_to__be_sort = file_input.map(line => (line.take(10), line.drop(10)))
val sort = file_to_sort.sortByKey()
val lines = sort.map {case (key,value) => s"$key $value"}
lines.saveAsTextFile("/raj_spark/spark_sorted_128gb_output")
val endtime = System.currentTimeMillis()
println ("Time taken to sort the given file :-" + (endtime - starttime) + " ms")


Then, go to:
cd /opt/spark/bin and execute the spark-shell like:
./spark-shell -i spark_terasort.scala

Now, Copy file from hdfs to other location.
bin/hadoop dfs -getmerge /raj_spark/spark_sorted_128gb_output /raj/128gb_final_spark_sorted_file




-----SETUP FOR SHARED MEMORY----
Shared-Memory Sort Program execution steps for 2 configuration:
1)- compile the two Java classes :- 1. javac SharedMemory.java
2. javac SortFiles.java
2)- Execute the code: java SharedMemory
3)- It will ask to input filename that you want to sort and number of threads you want to create.
4)- After execution, Time required to sort the file will be displayed, and the sorted file will be generated.