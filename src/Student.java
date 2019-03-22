import java.util.ArrayList;

/**
 * @author John Rachid
 */
class Student {
    private String studentID;
    private String predictedMajorOne, predictedMajorTwo, predictedMajorThree;
    private String actualMajor;
    private ArrayList<String> grades;
    private ArrayList<String> letterGrade;
    ArrayList<Class> classesTaken;

    /**
     * creates a new student. each student has a arraylist of classesTaken,grades and letterGrades.
     * @param id the students id
     */
    Student(String id) {
        studentID = id;
        classesTaken = new ArrayList<>();
        grades = new ArrayList<>();
        letterGrade = new ArrayList<>();
    }

    String getStudentID() {
        return studentID;
    }

    void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    String getActualMajor() {
        return actualMajor;
    }

    void setActualMajor(String actualMajor) {
        this.actualMajor = actualMajor;
    }

    void addToClasses(Class classToAdd) {
        classesTaken.add(classToAdd);
    }

    public String getPredictedMajorOne() {
        return predictedMajorOne;
    }

    public void setPredictedMajorOne(String predictedMajorOne) {
        this.predictedMajorOne = predictedMajorOne;
    }

    public String getPredictedMajorTwo() {
        return predictedMajorTwo;
    }

    public void setPredictedMajorTwo(String predictedMajorTwo) {
        this.predictedMajorTwo = predictedMajorTwo;
    }

    public String getPredictedMajorThree() {
        return predictedMajorThree;
    }

    public void setPredictedMajorThree(String predictedMajorThree) {
        this.predictedMajorThree = predictedMajorThree;
    }

    public ArrayList<String> getGrades() {
        return grades;
    }

    public void addToGrades(String gradeToAdd) {
        grades.add(gradeToAdd);
    }

    public ArrayList<String> getLetterGrade() {
        return letterGrade;
    }

    public void addToLetterGrade(String letterGrade2) {
        letterGrade.add(letterGrade2);
    }
}
