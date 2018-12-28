package deptinfo.ubfc.quizzs.usefulClass;
/*
    Barbeaut Reynald

    This file represent a question as an object
 */

import java.util.ArrayList;
import java.util.List;

public class Question implements java.io.Serializable{
    public String question;
    public List<String> propositions;
    public int bonneReponse;


    public Question(String question){
        this.question = question;
        this.propositions = new ArrayList<String>();
        this.bonneReponse = 0;
    }

    public void addReponse(String reponse){
        this.propositions.add(reponse);
    }

    public void setBonneReponse(int bonneReponse){
        this.bonneReponse = bonneReponse;
    }
}
