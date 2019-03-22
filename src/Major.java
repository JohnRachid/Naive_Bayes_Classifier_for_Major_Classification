import java.util.ArrayList;

/**
 * @author John Rachid
 */
class Major {
    private String majorType;
    private int amountWithMajor;
    private int amountOfClassesTakenByMajors;
    private double prior;
    private double[] bayesianEstimator;
     ArrayList<Class> classesTakenByMajors; //will create array with size of this arraylist when it comes to counting the amount of times each class was present
     private Class[] amountOfEachClassTaken;

    /**
     * creates a new major object. This is used to store the training data for each major.
     * @param categoryName
     */
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

    /**
     * this creates an array where each element is a distinct class. This method also sums up the amount of times it was
     * taken. This simplifies finding the bayesian values.
     * @param totalClasses the total amount of classes in distinct classes
     */
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
     * this calculates the bayestian estimate for each class in distinct classes. This uses laplace smoothing.
     * This prevents the model outputting that there is a zero chance for the student to be apart of the major if one
     * of the taken classes is not in the training data. alpha is a hyper parameter and was set via experimentation with
     * the split training data.
     * @param amountOfDistinctClasses the size of the distinctClasses arraylist
     */
     void calculateBayesianEstimator(double amountOfDistinctClasses) {
         fillAmountOfEachClassTaken((int)amountOfDistinctClasses);
         String currentClass = "";
         double alpha = .001;
         bayesianEstimator = new double[NaiveBayesClassifier.distinctClasses.size()];
         for(int i = 0; i < amountOfEachClassTaken.length; i++){
             bayesianEstimator[i] = (amountOfEachClassTaken[i].getTimesTaken() + alpha) / (amountOfClassesTakenByMajors + (alpha*amountOfDistinctClasses));
             //this is nk+1 / n+|classes in major|
         }
     }
     double[] getBayesianEstimator(){
         return bayesianEstimator;
     }
     double getPrior(){
         return prior;
     }

}
