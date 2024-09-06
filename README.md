Hadoop Map reduce program in this project named Job_Offer calculator, process the student  CGPA data .  This Program classifies students into three different job package categories based on their academic performance, specifically focusing on their Cumulative Grade Point Average (CGPA) and the number of certifications they have obtained. Each line of the output represents a student's ID, name, and the corresponding job package category.

# CODE
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

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException { 
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
                    // Handle number format exception
                } 
            } else { 
                // Handle invalid input
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
        public void reduce(IntWritable studentId, Iterable<Text> studentInfos, Context context) throws IOException, InterruptedException { 
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

### Problem Statement: **Student Placement Package Classification Using MapReduce**

#### **Background:**
In a university setting, students' placement opportunities are often classified into different categories based on their academic performance and extracurricular achievements. Companies visiting the campus offer different placement packages categorized as "Super Dream," "Dream," or "Regular." The classification of these packages is generally based on students' cumulative grade point average (CGPA) and the number of certifications they have completed.

With the increasing number of students and the large datasets generated, manually classifying each student's placement package becomes impractical. A MapReduce-based approach can efficiently process large datasets and automate the classification process based on predefined criteria.

#### **Objective:**
The goal is to develop a MapReduce program that processes student records and classifies each student into a placement package category ("Super Dream," "Dream," or "Regular") based on their CGPA and the number of certifications they have. The program will read student data, process it to determine the appropriate placement package for each student, and output the results.

#### **Data Input:**
The input data consists of student records where each record contains the following fields separated by spaces:
- **Student ID**: A unique identifier for each student (integer).
- **Name**: The name of the student (string).
- **CGPA**: The cumulative grade point average of the student (float).
- **Certifications**: The number of certifications completed by the student (integer).

Example Input:
```
101 John 8.9 12
102 Alice 7.5 6
103 Bob 6.8 3
104 Carol 8.0 8
```

#### **Classification Criteria:**
The placement package categories are determined based on the following criteria:
- **Super Dream**: Students with a CGPA greater than 8.5 and more than 10 certifications.
- **Dream**: Students with a CGPA greater than 7.0 and more than 5 certifications.
- **Regular**: Students who do not meet the criteria for "Super Dream" or "Dream."

#### **Output:**
The output will be a list of student IDs along with their names and the corresponding placement package category. The output will be written to a file, with each line containing:
- **Student ID**: The unique identifier of the student (integer).
- **Name**: The name of the student (string).
- **Package Category**: The determined placement package category (string).

Example Output:
```
101 John,Super Dream
102 Alice,Dream
103 Bob,Regular
104 Carol,Dream
```

#### **Technical Implementation:**
The implementation is done using the Hadoop MapReduce framework with the following components:

1. **Mapper Class (`StudentMapper`)**:
   - **Input**: Takes each line of student data as input.
   - **Processing**:
     - Splits the input line into individual fields.
     - Validates and parses the fields.
     - Applies the classification criteria to determine the placement package category.
   - **Output**: Emits the student ID as the key and a combination of the student’s name and package category as the value.

2. **Reducer Class (`StudentReducer`)**:
   - **Input**: Receives the student ID as the key and corresponding details (name and package category) as values.
   - **Processing**: Simply writes the student ID along with the corresponding details to the output.
   - **Output**: Writes the final classification results to the output file.

3. **Driver Class**:
   - Configures and runs the MapReduce job.
   - Specifies the input and output paths.
   - Defines the Mapper and Reducer classes, and sets the output data types.

#### **Expected Outcomes:**
Upon successful execution, the program will produce an output file that contains the classification of each student based on their CGPA and the number of certifications they have completed. This will help the university or placement office to quickly determine which students qualify for specific types of placement opportunities, streamlining the placement process.

#### **Challenges:**
- Handling of malformed input data where CGPA or certification numbers might not be properly formatted.
- Efficiently processing large datasets, ensuring that the MapReduce job runs optimally.

#### **Extensions:**
- Incorporate additional factors into the classification, such as students’ project work, internships, or extracurricular activities.
- Implement a more granular classification system with more package categories.
- Visualize the distribution of students across different package categories using Hadoop's ecosystem tools like Pig or Hive.

This MapReduce-based solution provides a scalable and efficient way to classify students for campus placements, ensuring that the university's placement process is data-driven and fair.
