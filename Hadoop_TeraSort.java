import java.io.IOException;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;

/**
 * @author Raj
 */
public class Hadoop_TeraSort{
    public Hadoop_TeraSort(){
        super();
    }

    /**
     * Main Method
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        long start_time = System.currentTimeMillis();                                               //starting the timer in ms
		Configuration conf = new Configuration();   
		Job job = new Job(conf, "Hadoop TeraSort");                                                 //starting a new job
		job.setJarByClass(Hadoop_TeraSort.class);	                                                //method for setting application class
		job.setMapperClass(Hadoop_TeraSort_Mapper.class);	                                        //method for setting mapper class
		job.setCombinerClass(Hadoop_TeraSort_Reducer.class);
		job.setReducerClass(Hadoop_TeraSort_Reducer.class);	                                        //method for setting reducer class
		job.setOutputKeyClass(Text.class);	                                                        //method for output key
		job.setOutputValueClass(Text.class);	                                                    //method for output value
		FileInputFormat.addInputPath(job, new Path(args[0]));	                                    //input file path arg
		FileOutputFormat.setOutputPath(job, new Path(args[1]));	                                    //output file path arg
		System.exit(job.waitForCompletion(true) ? 0 : 1);	                                        //wait for job completion 
		long end_time = System.currentTimeMillis();                                                 //ending the timer in ms
		System.out.println("Total Execution Time: " + ((end_time - start_time)/1000)+" secs");      //calculating total execution time in secs
    }
}

/**
 * Mapper Class for Hadoop TeraSort
 */
class Hadoop_TeraSort_Mapper extends Mapper<Object, Text, Text, Text>{

    /**
     * @param key
	 * @param value
	 * @param context 
     */
    public void map(Object key, Text value, Context context){
        Text key_text = new Text();
        Text val_text = new Text();
        
        String str = value.toString();
        String k = str.substring(0,10);
        String v = str.substring(10);
        key_text.set(k);
        val_text.set(v);
        context.write(key_text, val_text);
    }
}

/**
 * Reducer Class for Hadoop TeraSort
 */
class Hadoop_TeraSort_Reducer extends Reducer<Text, Text, Text, Text>{

    /**
     * @param key
	 * @param value
	 * @param context 
     */
    public void reduce(Text key, Text value, Context context) throws IOException, InterruptedException{
        try{
            Text k = new Text();
            Text v = new Text();

            k.set(key.toString() + value.toString());
            v.set("");
            context.write(k, v);
        }catch(Exception e){
            e.printStackTrace();
        }
	}
}
