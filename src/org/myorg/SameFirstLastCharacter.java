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
 * MapReduce program to find number of words having same first and last characters
 * @author Adithya Balasubramanian
 * 
 */

public class SameFirstLastCharacter extends Configured implements Tool {

        public static class WordCountMap extends Mapper<LongWritable, Text, Text, IntWritable>
        {
                private final static IntWritable one = new IntWritable(1);
                private Text word = new Text();
                
                @Override
                public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
                {
                        String line = value.toString();
                        StringTokenizer tokenizer = new StringTokenizer(line);
                        while(tokenizer.hasMoreTokens())
                        {
                        	 word.set(tokenizer.nextToken());
                        	if(word.toString().charAt(0) == word.toString().charAt(word.toString().length()-1)){
                                word.set("number of words having same first and last character:");
                        	}else{
                        		word.set("number of words whose first and last characters do not match:");
                        	}
                        	context.write(word, one);
                        }
                }
        }
        
        public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable>
        {
                public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
                {
                        int sum = 0;
                        for(IntWritable value: values)
                        {
                                sum += value.get();
                        }
                        context.write(key, new IntWritable(sum));
                }
                
        }
        
        public int run(String[] args) throws Exception  {
               
                Job job = new Job(getConf());
                job.setJarByClass(SameFirstLastCharacter.class);
                job.setJobName("totalmatchnomatch");
                
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(IntWritable.class);
                
                job.setMapperClass(WordCountMap.class);
                job.setCombinerClass(WordCountReducer.class);
                job.setReducerClass(WordCountReducer.class);
                
                
                job.setInputFormatClass(TextInputFormat.class);
                job.setOutputFormatClass(TextOutputFormat.class);
                
                
                FileInputFormat.setInputPaths(job, new Path(args[0]));
                FileOutputFormat.setOutputPath(job, new Path(args[1]));
                
                boolean success = job.waitForCompletion(true);
                return success ? 0: 1;
        }
        
       
        public static void main(String[] args) throws Exception {
                int result = ToolRunner.run(new SameFirstLastCharacter(), args);
                System.exit(result);
        }
       
} 

