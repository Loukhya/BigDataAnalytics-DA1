import java.io.IOException; 
import org.apache.hadoop.conf.Configuration; 
import org.apache.hadoop.fs.Path; 
import org.apache.hadoop.io.IntWritable; 
import org.apache.hadoop.io.Text; 
import org.apache.hadoop.mapreduce.Job; 
import org.apache.hadoop.mapreduce.Mapper; 
import org.apache.hadoop.mapreduce.Reducer; 
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat; 
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat; 
 
public class StudentPackage { 
 public static class StudentMapper extends Mapper<Object, Text, IntWritable, Text> { 
     private final static IntWritable studentId = new IntWritable(); 
     private final static Text studentInfo = new Text(); 
 
     public void map(Object key, Text value, Context context) throws IOException, 
InterruptedException { 
         String[] words = value.toString().split(" "); 
         if (words.length >= 4 && !words[0].isEmpty() && !words[1].isEmpty() && 
!words[2].isEmpty() && !words[3].isEmpty()) { 
             try { 
                 int id = Integer.parseInt(words[0]); 
                 String name = words[1]; 
                 float cgpa = Float.parseFloat(words[2]); 
                 int certs = Integer.parseInt(words[3]); 
                 String packageStr = determinePackage(cgpa, certs); 
                 String outputValue = name + "," + packageStr; 
                 studentId.set(id); 
                 studentInfo.set(outputValue); 
                 context.write(studentId, studentInfo); 
             } catch (NumberFormatException e) { 
         
             } 
         } else { 
              
         } 
} 
private static String determinePackage(float cgpa, int certs) { 
if (cgpa > 8.5 && certs > 10) 
return "Super Dream"; 
else if (cgpa > 7.0 && certs > 5) 
return "Dream"; 
else 
return "Regular"; 
} 
} 
public static class StudentReducer extends Reducer<IntWritable, Text, IntWritable, Text> { 
public void reduce(IntWritable studentId, Iterable<Text> studentInfos, Context context) throws 
IOException, InterruptedException { 
for (Text info : studentInfos) { 
context.write(studentId, info); 
} 
} 
} 
public static void main(String[] args) throws Exception { 
Configuration conf = new Configuration(); 
Job job = new Job(conf, "Student Package"); 
job.setJarByClass(StudentPackage.class); 
job.setMapperClass(StudentMapper.class); 
job.setReducerClass(StudentReducer.class); 
job.setOutputKeyClass(IntWritable.class); 
job.setOutputValueClass(Text.class); 
FileInputFormat.addInputPath(job, new Path(args[0])); 
FileOutputFormat.setOutputPath(job, new Path(args[1])); 
System.exit(job.waitForCompletion(true) ? 0 : 1); 
} 
}
