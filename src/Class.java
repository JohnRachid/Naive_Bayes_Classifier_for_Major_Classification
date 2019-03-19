import java.util.ArrayList;

class Class {
    private String classType;
    private int timesTaken = 1;
     Class(String className){
        classType = className;
    }

      String getClassType() {
         return classType;
     }

     void incrementTimesTaken(){
         timesTaken++;
     }

     int getTimesTaken(){
         return timesTaken;
     }

     void setTimesTaken(int amount){
         timesTaken = amount;
     }

}
