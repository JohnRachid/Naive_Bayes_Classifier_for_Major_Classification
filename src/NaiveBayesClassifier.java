import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Math;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static java.lang.Math.toIntExact;

import java.util.Arrays;

/**
 * The general process of this model is
 * Calculate each class prior by finding amount of classes in each major
 * Calculate N: which is the total number of all classes taken per major
 * For each class in the major
 * Calculate nk which is the number of times the class appears in the major
 * Calculate the bayesian estimator which is PBE(class|major) = nk+1 / n + length of total distinct amount of all classes
 * @author John Rachid
 */
public class NaiveBayesClassifier {
    private static final String trainDataPath = "data/training.psv";
    private static final String testDataPath = "data/eval.psv";
    private static final String testingFromTrainingPath = "data/testingFromTraining.psv";
    private static final String trainingFromTrainingPath = "data/trainingFromTraining.psv";
    private static final String DELIMITER = "\\|";
    private static double totalClassesTaken = 0;
    static ArrayList<String> distinctClasses;
    private static boolean evaluateStudents;


    public static void main(String[] args) throws IOException {
      boolean testingOnTraining = false;
      boolean testingOnSplitTrainingData = true;
      evaluateStudents = false;
        if (testingOnTraining) {
            trainAndTestModel(trainDataPath,trainDataPath); }
        else if(testingOnSplitTrainingData){
            trainAndTestModel(trainingFromTrainingPath, testingFromTrainingPath); }
        else if(evaluateStudents){
            trainAndTestModel(trainDataPath,testDataPath);
        }


    }

    /**
     * train and tests the model. file paths determine the type of traianing and model evaluation you do.
     * @param training the path of the training data
     * @param testing the path of the testing data
     * @throws IOException
     */
    private static void trainAndTestModel(String training,String testing)throws IOException {
        int trainingExamples = toIntExact(findNumberLineForTraining(training));
        distinctClasses = new ArrayList<String>();
        ArrayList<Major> major = fillTrainingArray(training);
        for (int i = 0; i < major.size(); i++) {
            major.get(i).setPrior(major.get(i).getAmountOfClassesTakenByMajors() / totalClassesTaken); //this sets the prior for each class
            major.get(i).calculateBayesianEstimator(distinctClasses.size());
        }
        evaluateModel(major,testing);
    }

    /**
     * checks if the class name exists within distinct classes. If it does not then it adds the class name to distinct classes
     * @param className the class name that is being checked for in distinctClasses
     */
    private static void checkIfExistsAndAdd(String className) {
        if (!distinctClasses.contains(className)) {
            distinctClasses.add(className);
        }
    }

    /**
     * finds the total amount of lines in the file of the path
     * @param path path of the file to find the number of lines
     * @return number of lines in the file
     * @throws IOException
     */
    private static long findNumberLineForTraining(String path) throws IOException {
        return Files.lines(Paths.get(path)).count();
    }

    /**
     * This method reads from training.psv or trainingFromTraining.psv in order to get all of the required information from the array.
     * With this information we can calculate the bayesian value for each class for each major
     * @param path the path of file that contains the training data
     * @return returns array of majors with amountOfEachClassTaken and amountOfClassesTakenByMajors populated as well as other important attributes of Student
     */
    private static ArrayList<Major> fillTrainingArray(String path) {
        ArrayList<Major> trainingMajors = new ArrayList<Major>();
        Major currentMajor = new Major("temp");
        String currentStudent = "";
        Boolean addNewMajor;
        Boolean addNewClass;
        BufferedReader brData = null;
        try {
            brData = new BufferedReader(new FileReader(path));
            String dataLine; //line for the train_data.csv
            dataLine = brData.readLine();
            while ((dataLine = brData.readLine()) != null) {

                String[] trainDataLine = dataLine.split(DELIMITER);   //Splits the data line into a array based on where the "|" in that line. trainDataLine[0] = StudentID,
                // trainDataLine[1] = GradeLevel, trainDataLine[2] = Class, trainDataLine[3] = Grade abd trainDataLine[4] = major
                addNewMajor = true;

                for (int i = 0; i < trainingMajors.size(); i++) {
                    Major iterMajor = trainingMajors.get(i);

                    if (iterMajor.getMajorType().equals(trainDataLine[4])) { //add to that majors class
                        addNewClass = true;
                        for (int j = 0; j < trainingMajors.get(i).classesTakenByMajors.size(); j++) {
                            if (trainingMajors.get(i).classesTakenByMajors.get(j).getClassType().equals(trainDataLine[2])) {
                                trainingMajors.get(i).classesTakenByMajors.get(j).incrementTimesTaken();
                                trainingMajors.get(i).incrementAmountOfClassesTakenByMajors();
                                checkIfExistsAndAdd(trainDataLine[2]);
                                totalClassesTaken++;
                                addNewClass = false;
                            }
                        }
                        if (addNewClass) {
                            trainingMajors.get(i).addToClasses(trainDataLine[2]); //add class to major
                            checkIfExistsAndAdd(trainDataLine[2]);
                            trainingMajors.get(i).incrementAmountOfClassesTakenByMajors();
                            totalClassesTaken++;
                        }
                        addNewMajor = false;
                    }
                }
                if (addNewMajor) {
                    currentMajor = new Major(trainDataLine[4]); //creating a new major
                    currentMajor.addToClasses(trainDataLine[2]); //adding the class for that row to that major
                    checkIfExistsAndAdd(trainDataLine[2]);
                    currentMajor.incrementAmountOfClassesTakenByMajors();
                    totalClassesTaken++;
                    trainingMajors.add(currentMajor); //adding new major to the array
                }
            }

        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
                brData.close(); //closing the BufferedReaders
            } catch (IOException ie) {
                System.out.println("Error occured while closing the BufferedReaders");
                ie.printStackTrace();
            }
        }
        return trainingMajors;
    }

    /**
     * This creates an arraylist of students and according to the information in the eval.psv file. This allows for easy
     * manipulation and control of the students data.
     * @param path file path of the .psv with students
     * @return returns an arraylist of students filled with their data according to the path.
     */
    private static ArrayList<Student> fillEvalArray(String path) {
        ArrayList<Student> students = new ArrayList<Student>();
        Student currentStudent = new Student("temp");
        BufferedReader brData = null;
        String studentID = "";
        int currentLine = 0;
        try {
            brData = new BufferedReader(new FileReader(path));
            String dataLine; //line for the train_data.csv
            dataLine = brData.readLine();
            while ((dataLine = brData.readLine()) != null) {

                String[] studentDataLine = dataLine.split(DELIMITER);   //Splits the data line into a array based on where the "|" in that line. trainDataLine[0] = StudentID,
                // trainDataLine[1] = GradeLevel, trainDataLine[2] = Class, trainDataLine[3] = Grade and trainDataLine[4] = major
                if (studentID.equals(studentDataLine[0])) {
                    students.get(currentLine - 1).addToClasses(new Class(studentDataLine[2]));
                    students.get(currentLine - 1).addToGrades(studentDataLine[1]);
                    students.get(currentLine - 1).addToLetterGrade(studentDataLine[3]);
                } else {
                    Student addStudent = new Student(studentDataLine[0]);
                    addStudent.addToClasses(new Class(studentDataLine[2]));
                    addStudent.addToGrades(studentDataLine[1]);
                    addStudent.addToLetterGrade(studentDataLine[3]);
                    if (studentDataLine.length > 4 && studentDataLine[4] != null) {
                        addStudent.setActualMajor(studentDataLine[4]);
                    }
                    students.add(addStudent);
                    studentID = studentDataLine[0];

                    currentLine++;

                }

            }

        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
                brData.close(); //closing the BufferedReaders
            } catch (IOException ie) {
                System.out.println("Error occured while closing the BufferedReaders");
                ie.printStackTrace();
            }
        }


        return students;
    }

    /**
     * Evaluates the trained model according to each majors bayesian values. The arraylist students is being evaluated.
     * Once this is evaluated the performance of the model is outputted or the updated eval is outputted.
     * @param major
     * @param path
     */
    private static void evaluateModel(ArrayList<Major> major, String path) {
        double[] bayesianEstimator;
        double currentStudentsValue = 0;
        double currentPrior;
        String currentClass;
        Student currentStudent;
        double countFirstCorrect = 0.0;
        double countTopThreeCorrect = 0.0;
        int index;
        Boolean compute;
        int timesTaken;
        double currentClassValue = 0;
        double firstChoiceValue = 0, secondChoiceValue = 0, thirdChoiceValue = 0;
        ArrayList<Student> students = fillEvalArray(path);
        for (int i = 0; i < students.size(); i++) { /**
         for every student we need to get every class they take.
         For every class they take we need to get every majors bayesian value.
         **/
            currentStudent = students.get(i);
            firstChoiceValue = 0;
            secondChoiceValue = 0;
            thirdChoiceValue = 0;
            compute = true;
            for (int j = 0; j < major.size(); j++) { //for each major
                bayesianEstimator = major.get(j).getBayesianEstimator();
                currentPrior = major.get(j).getPrior();
                currentStudentsValue = 0;
                for (int k = 0; k < currentStudent.classesTaken.size(); k++) { //for class the student took
                    index = distinctClasses.indexOf(currentStudent.classesTaken.get(k).getClassType());
                    if(index == -1){
                        timesTaken = 0;
                        currentClassValue = 0;
                        compute = false;
                    }else{
                        timesTaken = currentStudent.classesTaken.get(k).getTimesTaken();
                        currentClassValue = Math.log(bayesianEstimator[index]);
                    }

                    if (timesTaken == 0) {
                        compute = false;
                    } else if (compute) {
                        currentStudentsValue = currentClassValue + currentStudentsValue;
                    }
                }
                if (compute) {
                    currentStudentsValue = Math.log(currentPrior) + currentStudentsValue; //ln(prior) +∑ln(class value) from 0 to students classes taken
                    if (firstChoiceValue == 0) { //set starting majors
                        currentStudent.setPredictedMajorOne(major.get(j).getMajorType());
                        firstChoiceValue = currentStudentsValue;
                    } else if (secondChoiceValue == 0) {
                        currentStudent.setPredictedMajorTwo(major.get(j).getMajorType());
                        secondChoiceValue = currentStudentsValue;
                    } else if (thirdChoiceValue == 0) {
                        currentStudent.setPredictedMajorThree(major.get(j).getMajorType());
                        thirdChoiceValue = currentStudentsValue;
                    }
                    if (currentStudentsValue > thirdChoiceValue || currentStudentsValue > secondChoiceValue || currentStudentsValue > firstChoiceValue) {
                        if (currentStudentsValue > firstChoiceValue) {
                            currentStudent.setPredictedMajorOne(major.get(j).getMajorType());
                            firstChoiceValue = currentStudentsValue;
                        } else if (currentStudentsValue > secondChoiceValue) {
                            currentStudent.setPredictedMajorTwo(major.get(j).getMajorType());
                            secondChoiceValue = currentStudentsValue;
                        } else {
                            currentStudent.setPredictedMajorThree(major.get(j).getMajorType());
                            thirdChoiceValue = currentStudentsValue;
                        }
                        if (thirdChoiceValue > firstChoiceValue) { //not needed
                            String temp = currentStudent.getPredictedMajorOne(); //temp = a
                            currentStudent.setPredictedMajorOne(currentStudent.getPredictedMajorThree()); //a = b
                            currentStudent.setPredictedMajorThree(temp); //b = temp

                            double tempVal = firstChoiceValue;
                            firstChoiceValue = thirdChoiceValue;
                            thirdChoiceValue = tempVal;
                        } else if (secondChoiceValue > firstChoiceValue) {
                            String temp = currentStudent.getPredictedMajorOne(); //temp = a
                            currentStudent.setPredictedMajorOne(currentStudent.getPredictedMajorTwo()); //a = b
                            currentStudent.setPredictedMajorTwo(temp); //b = temp

                            double tempVal = firstChoiceValue;
                            firstChoiceValue = secondChoiceValue;
                            secondChoiceValue = tempVal;
                        }
                    }
                }
            }
            if(currentStudent.getPredictedMajorOne() == null){ //this happens if there is a class in the testing data that was not in the training data. This is becuase if there is a zero in the ∏ then the result would always be zero.
            currentStudent.setPredictedMajorOne(major.get(1).getMajorType());//It does not hurt however to set these to any three majors.
            currentStudent.setPredictedMajorTwo(major.get(2).getMajorType());
            currentStudent.setPredictedMajorThree(major.get(3).getMajorType());
        }
            if(!evaluateStudents) { //There is no need to count the correct if there is no given majors
                if (currentStudent.getPredictedMajorOne().equals(currentStudent.getActualMajor())) {
                    countFirstCorrect++;
                }
                if (currentStudent.getPredictedMajorOne().equals(currentStudent.getActualMajor()) || currentStudent.getPredictedMajorTwo().equals(currentStudent.getActualMajor()) || currentStudent.getPredictedMajorThree().equals(currentStudent.getActualMajor())) {
                    countTopThreeCorrect++;
                }
            }
        }
        if(!evaluateStudents) {
            System.out.println("First choice correct: " + countFirstCorrect + " out of " + students.size());
            System.out.println("Top Three Correct: " + countTopThreeCorrect + " out of " + students.size());
            //statistics(countTopThreeCorrect,students,major); output confusion matrixes
        }else{
            printOutUpdatedStudents(students);
        }
    }

    /**
     * this outputs the new updatedEval.psv file with the header according to the data in the students arraylist
     * @param students arraylist of students
     */
    private static void printOutUpdatedStudents(ArrayList<Student> students){
        Student currentStudent;
        try (PrintWriter writer = new PrintWriter(new File("data/updatedEval.psv"))) {
            String header = "student_id|level|course|grade|major1|major2|major3\n";
            writer.write(header);
            writer.flush();
            for(int i = 0; i < students.size(); i++) {
                currentStudent = students.get(i);
                for (int x = 0; x < currentStudent.classesTaken.size(); x++) {
                    String temp = currentStudent.getStudentID() + "|" + currentStudent.getGrades().get(x) + "|" +
                            currentStudent.classesTaken.get(x).getClassType() + "|" + currentStudent.getLetterGrade().get(x) + "|" +
                            currentStudent.getPredictedMajorOne() + "|" + currentStudent.getPredictedMajorTwo() + "|" +
                            currentStudent.getPredictedMajorThree() + "\n";
                    writer.write(temp);
                    writer.flush();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
    }
    }

}



