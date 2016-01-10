package org.myorg;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
/**
 * MapReducer to determine the number of ROBBERY crimes that occured within 2000 ft of 3803 Forbes avenue.
 * @author Adithya
 *
 */

public class OaklandCrimeStats extends Configured implements Tool {

        public static class CrimeCountMap extends Mapper<LongWritable, Text, Text, IntWritable>
        {
                private final static IntWritable one = new IntWritable(1);
                private Text word = new Text();
	                /**
	                 * method that accepts value as each line of the input file and writes out a context having key as the text "Number of crimes.." and value as 1.
	                 */
                @Override
                public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
                {
                       String line = value.toString();
                       String[] lineArray = line.split("\t");
                        if(lineArray!=null){
                        	if(lineArray[4].equals("ROBBERY")){
                        		double distance = Math.sqrt(Math.pow((Double.parseDouble(lineArray[0]) - 1354326.897), 2) + Math.pow((Double.parseDouble(lineArray[1]) - 411447.7828), 2));
                        		if(distance < 2000){
                        			word.set("Number of crimes that occured within 2000 feet of 3803 Forbes Avenue:");
                        			context.write(word, one);
                        		}
                        	}
                        }
                }
        }
    
        public static class CrimeCountReducer extends Reducer<Text, IntWritable, Text, IntWritable>
        {		
        	/**
             * method that iterates all values over a particular key, which is the text assigned in Mapper, and sums up the values
            */

                public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
                {
                        int sum = 0;
                      //sum all values by iterating all values over a particular key
                        for(IntWritable value: values)
                        {
                                sum += value.get();
                        }
                        context.write(key, new IntWritable(sum));
                }
                
        }
        
        public int run(String[] args) throws Exception  {
               
                Job job = new Job(getConf());
                job.setJarByClass(OaklandCrimeStats.class);
                job.setJobName("oaklandcrimestats");
                
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(IntWritable.class);
                
                job.setMapperClass(CrimeCountMap.class);
                job.setCombinerClass(CrimeCountReducer.class);
                job.setReducerClass(CrimeCountReducer.class);
                
                job.setInputFormatClass(TextInputFormat.class);
                job.setOutputFormatClass(TextOutputFormat.class);
                
                
                FileInputFormat.setInputPaths(job, new Path(args[0]));
                FileOutputFormat.setOutputPath(job, new Path(args[1]));
                
                boolean success = job.waitForCompletion(true);
                return success ? 0: 1;
        }
        
       
        public static void main(String[] args) throws Exception {
                int result = ToolRunner.run(new OaklandCrimeStats(), args);
                System.exit(result);
        }
       
} 

