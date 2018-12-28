package deptinfo.ubfc.quizzs.play;
/*
    Barbeaut Reynald

    This file has the objective to display and allow the user to play with the quizzs
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import deptinfo.ubfc.quizzs.dataBase.QuizzDataBase;
import deptinfo.ubfc.quizzs.R;
import deptinfo.ubfc.quizzs.usefulClass.Quizz;

public class PlayQuizz extends Activity implements View.OnClickListener, OnPropositionClickListener{
    //All the attributes
    private QuizzDataBase quizzDB;
    PropositionAdapter adapter;
    private int idQuizz, currentQuestion;
    private TextView quizzTitle, questionTitle;
    private Quizz quizz;
    private List<String> test = new ArrayList<>();
    private Button returnButton;
    private int score;

    //The function which is used at the creation of the activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.play_quizz);
        Intent i = getIntent();

        this.quizzTitle = (TextView) (findViewById(R.id.title));
        this.questionTitle = (TextView) (findViewById(R.id.question));
        this.returnButton = (Button) (findViewById(R.id.buttonReturn));

        this.returnButton.setOnClickListener(this);

        this.quizzDB = new QuizzDataBase(this);
        this.idQuizz = (int) i.getSerializableExtra("id");
        setQuizz();


        this.currentQuestion = 0;
        this.score = 0;

        setQuizzName();
        setQuestionName();

        RecyclerView recyclerView = findViewById(R.id.propositionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PropositionAdapter(this, this.quizz.questions.get(this.currentQuestion).propositions,this);
        recyclerView.setAdapter(adapter);

    }

    //Load the quizz with some information in the data base
    public void setQuizz(){
        this.quizz = this.quizzDB.getQuizz(this.idQuizz);
    }




    //Set the name of the quizz on the view
    public void setQuizzName(){
        this.quizzTitle.setText(this.quizz.quizz);
    }


    //Set the name of the question on the view
    public void setQuestionName(){
        this.questionTitle.setText("Question "+(this.currentQuestion + 1)+" : "+this.quizz.questions.get(this.currentQuestion).question);
    }

    //Check the onclick on the return button
    @Override
    public void onClick(View v) {
        if(v.getId()== returnButton.getId()){
            this.finish();
        }
    }

    //Using this listener allow us to know a which position the user clicked
    @Override
    public void OnPropositionClickListener(int position) {
        //If he clicked on the good answer his score increase
        if ((position + 1) == this.quizz.questions.get(currentQuestion).bonneReponse) {
            this.score ++;
        }
        this.currentQuestion++;
        //if the number of the question isn't the number of the last one we update the question and the proposition
        if (this.currentQuestion != this.quizz.questions.size()) {
            setQuestionName();
            adapter.updateData(this.quizz.questions.get(this.currentQuestion).propositions);
        } else {
            //Else we display the score view by creating a new activity
            Intent intent = new Intent(this, ScoreDisplay.class) ;
            Bundle extra = new Bundle();
            extra.putInt("id",this.idQuizz);
            extra.putInt("score",this.score);
            extra.putInt("total",this.quizz.questions.size());
            intent.putExtras(extra);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            this.startActivity (intent) ;
            this.finish();
        }
    }
}
