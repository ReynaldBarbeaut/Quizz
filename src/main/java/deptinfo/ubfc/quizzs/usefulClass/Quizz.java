package deptinfo.ubfc.quizzs.usefulClass;
/*
    Barbeaut Reynald

    This file represent a quizz as an object

 */
import java.util.ArrayList;
import java.util.List;

public class Quizz implements java.io.Serializable {

    public String quizz;
    public List<Question> questions;


    public Quizz(String quizz){
        this.quizz = quizz;
        this.questions = new ArrayList<Question>();
    }

    public void addQuestion(Question question){
        this.questions.add(question);
    }

}
