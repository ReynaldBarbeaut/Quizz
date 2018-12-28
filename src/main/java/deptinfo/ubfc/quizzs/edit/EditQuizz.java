package deptinfo.ubfc.quizzs.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import deptinfo.ubfc.quizzs.dataBase.QuizzDataBase;
import deptinfo.ubfc.quizzs.R;

public class EditQuizz extends Activity implements View.OnClickListener{
    //Attributes
    private int idQuizz;
    private TextView quizzName, newQuestion;
    private QuizzDataBase db;
    private Button home, submit;
    private ImageButton addNewQuestion;
    private EditQuestionsAdapter adapter;
    private List<String> questions = new ArrayList<String>();
    private List<String> idQuestions = new ArrayList<String>();


    //The function which is used at the creation of the activity
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_quizz);

        quizzName = findViewById(R.id.quizzName);
        newQuestion = findViewById(R.id.newQuestion);


        home = findViewById(R.id.home);
        home.setOnClickListener(this);


        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);

        addNewQuestion = findViewById(R.id.addNewQuestion);
        addNewQuestion.setOnClickListener(this);

        this.db = new QuizzDataBase(this);

        Intent i = getIntent();
        this.idQuizz = (int) i.getSerializableExtra("id");
        displayQuizzName();

        RecyclerView recyclerView = findViewById(R.id.questionsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.db.chargerQuestions(this.questions,this.idQuizz);
        this.db.chargerIdQuestions(this.idQuestions,this.idQuizz);

        adapter = new EditQuestionsAdapter(this, this.questions,this.idQuestions,this.db, this.idQuizz);
        recyclerView.setAdapter(adapter);
    }


    public void displayQuizzName(){
        quizzName.setText(db.getQuizzName(idQuizz));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == home.getId()){
            finish();
        }else if(v.getId() == submit.getId()){
            updateQuizz();
        }else if(v.getId() == addNewQuestion.getId()){
            addNewQuestion();
        }
    }



    //Update the quizz
    public void updateQuizz(){
        String quizz;
        List<String> questionsName = adapter.getQuestionName();
        List<String> questionsId = adapter.getQuestionId();

        for(int i = 0; i < questions.size(); i++){
            db.updateQuestion(Integer.parseInt(questionsId.get(i)), questionsName.get(i));
        }


        quizz = String.valueOf(quizzName.getText());
        if(quizz.isEmpty()){
            Toast.makeText(this, "Le nom du quizz ne doit pas être vide !", Toast.LENGTH_SHORT).show();
        }else{
            db.updateQuizz(idQuizz,quizz);
        }
    }


    //Add a new question
    public void addNewQuestion(){
        String newQuestion = String.valueOf(this.newQuestion.getText());
        if(newQuestion.isEmpty()){
            Toast.makeText(this, "La nouvelle question ne doit pas être vide !", Toast.LENGTH_SHORT).show();
        }else{
            adapter.add(newQuestion);
            this.newQuestion.setText("");
        }

    }


}
