import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Math;
import java.util.ArrayList;

import static java.lang.Math.toIntExact;

import java.util.Arrays;

/**
 * The general process of this model is
 * Calculate each class prior by finding amount of classes in each major
 * Calculate N: which is the total number of all classes taken per major
 * For each class in the major
 * Calculate nk which is the number of times the class appears in the major
 * Calculate the bayesian estimator which is PBE(class|major) = nk+1 / n + length of total distinct amount of all classes
 */
public class NaiveBayesClassifier {
    private static final String trainDataPath = "data/training.psv";
    private static final String testDataPath = "data/eval.psv";
    private static final String DELIMITER = "\\|";
    private static double totalClassesTaken = 0;
    static ArrayList<String> distinctClasses;

    public static void main(String[] args) throws IOException {
        int trainingExamples = toIntExact(findNumberLineForTraining());
        distinctClasses = new ArrayList<String>();
        ArrayList<Major> major = fillTrainingArray(trainingExamples);
        for (int i = 0; i < major.size(); i++) {
            major.get(i).setPrior(major.get(i).getAmountOfClassesTakenByMajors() / totalClassesTaken); //this sets the prior for each class
            major.get(i).calculateBayesianEstimator(distinctClasses.size());
        }
        evaluateModel(major, trainingExamples);
//        ArrayList<Student> students = fillEvalArray(toIntExact(findNumberLineForTraining()));
//        for(int i = 0; i < students.size(); i++){
////            for (int j = 0; j < students.get(i).classesTaken.size(); j++){
////                System.out.println("Student ID:" + students.get(i).getStudentID() + " Class: " + students.get(i).classesTaken.get(j).getClassType());
////            }
//        }
    }

    private static void checkIfExistsAndAdd(String className) {
        if (!distinctClasses.contains(className)) {
            distinctClasses.add(className);
        }
    }

    private static long findNumberLineForTraining() throws IOException {
        return Files.lines(Paths.get(trainDataPath)).count();
    }

    private static ArrayList<Major> fillTrainingArray(int numLines) {
        ArrayList<Major> trainingMajors = new ArrayList<Major>();
        Major currentMajor = new Major("temp");
        String currentStudent = "";
        Boolean addNewMajor;
        Boolean addNewClass;
        BufferedReader brData = null;
        try {
            brData = new BufferedReader(new FileReader(trainDataPath));
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
//        for(int i = 0; i < trainingMajors.size(); i++){
//            System.out.println("Major:" + trainingMajors.get(i).getMajorType() + " with " + trainingMajors.get(i).getAmountOfClassesTakenByMajors());
//            for(int j = 0; j < trainingMajors.get(i).classesTakenByMajors.size(); j++){
//                System.out.println(trainingMajors.get(i).classesTakenByMajors.get(j).getClassType() + " Times Taken = " + trainingMajors.get(i).classesTakenByMajors.get(j).getTimesTaken());
//            }
//        }
//        System.out.println(totalClassTaken);

        return trainingMajors;
    }

    /**
     * This fills students with the data from the training data
     *
     * @param numLines
     * @return
     */
    private static ArrayList<Student> fillEvalArray(int numLines) {
        ArrayList<Student> students = new ArrayList<Student>();
        Student currentStudent = new Student("temp");
        BufferedReader brData = null;
        String studentID = "";
        int currentLine = 0;
        try {
            brData = new BufferedReader(new FileReader(trainDataPath));
            String dataLine; //line for the train_data.csv
            dataLine = brData.readLine();
            while ((dataLine = brData.readLine()) != null) {

                String[] studentDataLine = dataLine.split(DELIMITER);   //Splits the data line into a array based on where the "|" in that line. trainDataLine[0] = StudentID,
                // trainDataLine[1] = GradeLevel, trainDataLine[2] = Class, trainDataLine[3] = Grade abd trainDataLine[4] = major
                if (studentID.equals(studentDataLine[0])) {
                    students.get(currentLine - 1).addToClasses(new Class(studentDataLine[2]));
                } else {
                    Student addStudent = new Student(studentDataLine[0]);
                    addStudent.addToClasses(new Class(studentDataLine[2]));
                    if (studentDataLine[4] != null) {
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

    private static void evaluateModel(ArrayList<Major> major, int totalLines) {
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
        //Class currentClass;
        String firstChoice, secondChoice, thirdChoice;
        double currentClassValue = 0;
        double firstChoiceValue = 0, secondChoiceValue = 0, thirdChoiceValue = 0;
        ArrayList<Student> students = fillEvalArray(totalLines);
        for (int i = 0; i < students.size(); i++) { /**
         for every student we need to get every class they take.
         For every class they take we need to get every majors bayesian value.
         **/
            currentStudent = students.get(i);
            firstChoiceValue = 0;
            secondChoiceValue = 0;
            thirdChoiceValue = 0;
            compute = true;
            for (int j = 0; j < major.size(); j++) {
                bayesianEstimator = major.get(j).getBayesianEstimator();
                currentPrior = major.get(j).getPrior();
                currentStudentsValue = 0;
                for (int k = 0; k < currentStudent.classesTaken.size(); k++) {
                    //currentClass = currentStudent.classesTaken.get(k).getClassType();
                    index = distinctClasses.indexOf(currentStudent.classesTaken.get(k).getClassType());
                    timesTaken = currentStudent.classesTaken.get(k).getTimesTaken();
                    currentClassValue = Math.log(bayesianEstimator[index]);
                    if (timesTaken == 0) {
                        compute = false;
                    } else if (compute) {
                        currentStudentsValue = currentClassValue + currentStudentsValue;
                    }
                }
                if (compute) {
                    currentStudentsValue = Math.log(currentPrior) + currentStudentsValue;
                    if (firstChoiceValue == 0) {
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
                // System.out.println("Value" + currentStudentsValue + "for " + currentStudent.getActualMajor() + " current " + major.get(j).getMajorType() + " firstChoice" + currentStudent.getPredictedMajorOne());
            }
            //System.out.println("Value" + currentStudentsValue);
            //System.out.println("Student ID:" + currentStudent.getStudentID() + " Actual: " + currentStudent.getActualMajor() + " First predicted :" + currentStudent.getPredictedMajorOne() + " second predicted :" + currentStudent.getPredictedMajorTwo() + " third predicted :" + currentStudent.getPredictedMajorThree() );
            if (currentStudent.getPredictedMajorOne().equals(currentStudent.getActualMajor())) {
                countFirstCorrect++;
            }
            if (currentStudent.getPredictedMajorOne().equals(currentStudent.getActualMajor()) || currentStudent.getPredictedMajorTwo().equals(currentStudent.getActualMajor()) || currentStudent.getPredictedMajorThree().equals(currentStudent.getActualMajor())) {
                countTopThreeCorrect++;
            }

        }
        double temp = countFirstCorrect / students.size();
        double temp2 = countTopThreeCorrect / students.size();
        System.out.println("First choice correct: " + countFirstCorrect + " out of " + students.size());
        System.out.println("TopThreeCorrect: " + countTopThreeCorrect + " out of " + students.size());
    }

}
