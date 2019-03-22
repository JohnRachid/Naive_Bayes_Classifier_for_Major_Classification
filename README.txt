This is a Naive Bayes Classifier for major Classification with the goal of correctly predicting the majors of students based
on their classes. Running the NaiveBayesClassifier will train the model with bayesian estimation.]

Changing
      boolean testingOnTraining
      boolean testingOnSplitTrainingData
      evaluateStudents
Will change how the model was trained / tested.
    testingOnTraining = true; trains and tests the model on training.psv
    boolean testingOnSplitTrainingData = true; trains the model on 80% of the training.psv and tests it on the other 20%
    evaluateStudents = true; will train the model on the training.psv and output the updatedEval.psv file with the appropriate major predictions;

This was developed using Intellij with java jdk 1.8
The project organization is as follows:
MainProject folder
    data
        eval.psv
        testingFromTraining.psv
        Training.psv
        trainingFromtraining.psv
        updatedEval.psv
    src
        Class.java
        Major.java
        NaiveBayesClassifier.java
        Student.java
HOW TO RUN
------------------------------------------
Ensure the file paths for in NaiveBayesClassifier are properly set or organize your project like mine is above.
Run naiveBayesClassifier