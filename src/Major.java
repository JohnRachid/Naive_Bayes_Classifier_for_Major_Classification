import java.util.ArrayList;

class Major {
    private String majorType;
    private int amountWithMajor;
    private int amountOfClassesTakenByMajors;
    private double prior;
    private double[] bayesianEstimator;
     ArrayList<Class> classesTakenByMajors; //will create array with size of this arraylist when it comes to counting the amount of times each class was present
     private Class[] amountOfEachClassTaken;
    Major(String categoryName){
        majorType = categoryName;
        classesTakenByMajors = new ArrayList<>();

    }
    Class[] getAmountOfEachClassTaken(){
        return amountOfEachClassTaken;
    }
    void incrementAmountWithMajor(){
        amountWithMajor++;
    }
    void addToClasses(String classToAdd){
        classesTakenByMajors.add(new Class(classToAdd));
    }
    String getMajorType(){
        return majorType;
    }
    void setPrior(double givenPrior){
        prior = givenPrior;
    }
    void incrementAmountOfClassesTakenByMajors(){
        amountOfClassesTakenByMajors++;
    }
    int getAmountOfClassesTakenByMajors(){
        return amountOfClassesTakenByMajors;
    }

     void fillAmountOfEachClassTaken(int totalClasses) {
         amountOfEachClassTaken = new Class[totalClasses];
         for (int i = 0; i < amountOfEachClassTaken.length; i++) {
             amountOfEachClassTaken[i] = new Class(NaiveBayesClassifier.distinctClasses.get(i));
             amountOfEachClassTaken[i].setTimesTaken(0);
         }
         for(int i = 0; i < amountOfEachClassTaken.length; i++) {
             for (int k = 0; k < classesTakenByMajors.size(); k++) {
                 if (amountOfEachClassTaken[i].getClassType().equals(classesTakenByMajors.get(k).getClassType())) {
                     amountOfEachClassTaken[i].setTimesTaken(classesTakenByMajors.get(k).getTimesTaken());
                 }
             }
         }


     }

     /**
      * calculates the bayesian estimate for each class in major
      */
     void calculateBayesianEstimator(double amountOfDistinctClasses) {
         fillAmountOfEachClassTaken((int)amountOfDistinctClasses);
         String currentClass = "";
         int alpha = 1;
         bayesianEstimator = new double[NaiveBayesClassifier.distinctClasses.size()];
             //classesTakenByMajors.get(i).setBayesianValue((classesTakenByMajors.get(i).getTimesTaken() + 1.0) / (amountOfClassesTakenByMajors + totalClasses));
         for(int i = 0; i < amountOfEachClassTaken.length; i++){
             bayesianEstimator[i] = (amountOfEachClassTaken[i].getTimesTaken() + alpha) / (amountOfClassesTakenByMajors + (alpha*amountOfDistinctClasses));
         }
     }
     double[] getBayesianEstimator(){
         return bayesianEstimator;
     }
     double getPrior(){
         return prior;
     }

}
