import java.util.ArrayList;

class Student {
    private String studentID;
    private String predictedMajorOne, predictedMajorTwo, predictedMajorThree;
    private String actualMajor;
    ArrayList<Class> classesTaken;


    Student(String id) {
        studentID = id;
        classesTaken = new ArrayList<>();
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

}
