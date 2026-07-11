package youtube.analysis;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Youtube {
	private static final int CATEGORY_FIELD_INDEX = 3;
	private static final int MIN_FIELD_COUNT = 6;

	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
		private final Text category = new Text();
		private final static IntWritable one = new IntWritable(1);
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] fields = line.split("\t");
			if (fields.length < MIN_FIELD_COUNT) {
				return;
			}
			category.set(fields[CATEGORY_FIELD_INDEX]);
			context.write(category, one);
		}
	}
	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			context.write(key, new IntWritable(sum));
		}
	}
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: Youtube <input path> <output path>");
			System.exit(1);
		}
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Youtube Analysis");
		job.setJarByClass(Youtube.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		Path out = new Path(args[1]);
		out.getFileSystem(conf).delete(out, true);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
