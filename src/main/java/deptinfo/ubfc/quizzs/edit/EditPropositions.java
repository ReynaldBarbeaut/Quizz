package deptinfo.ubfc.quizzs.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import deptinfo.ubfc.quizzs.dataBase.QuizzDataBase;
import deptinfo.ubfc.quizzs.R;

public class EditPropositions extends Activity implements View.OnClickListener{
    private int idQuestion, goodAnswer;
    private QuizzDataBase db;
    private Button valid, returnButton;
    private ImageButton addProposition;
    private TextView goodAnswerView, newProposition;
    private List<String> propositions = new ArrayList<String>();
    private List<String> idPropositions = new ArrayList<String>();
    private EditPropositionsAdapter adapter;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_propositions);

        this.db = new QuizzDataBase(this);

        this.newProposition = findViewById(R.id.newProposition);
        this.goodAnswerView = findViewById(R.id.goodAnswer);
        this.valid = findViewById(R.id.submit);
        this.returnButton = findViewById((R.id.returnButton));
        this.addProposition = findViewById((R.id.addNewProposition));

        this.valid.setOnClickListener(this);
        this.returnButton.setOnClickListener(this);
        this.addProposition.setOnClickListener(this);

        Intent i = getIntent();
        this.idQuestion = (int) i.getSerializableExtra("id");

        this.goodAnswer = this.db.getGoodProposition(this.idQuestion);
        setGoodAnswer();


        RecyclerView recyclerView = findViewById(R.id.propositionsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.db.chargerPropositions(this.propositions,this.idQuestion);
        this.db.chargerIdPropositions(this.idPropositions,this.idQuestion);

        adapter = new EditPropositionsAdapter(this, this.propositions,this.idPropositions,this.db,this.idQuestion);
        recyclerView.setAdapter(adapter);

    }



    public void setGoodAnswer(){
        this.goodAnswerView.setText(String.valueOf(this.goodAnswer));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == returnButton.getId()){
            finish();
        }else if(v.getId() == valid.getId()){
            updatePropositions();
        }else if(v.getId() == addProposition.getId()){
            addNewProposition();
        }

    }



    //Add a new proposition
    public void addNewProposition(){
        String newProposition = String.valueOf(this.newProposition.getText());
        if(newProposition.isEmpty()){
            Toast.makeText(this, "La nouvelle proposition ne doit pas être vide !", Toast.LENGTH_SHORT).show();
        }else{
            adapter.add(newProposition);
            this.newProposition.setText("");
        }

    }




    //Update all the propositions of a question
    public void updatePropositions(){
        String goodAnswer;
        List<String> propositionsName = adapter.getPropositionName();
        List<String> propositionsId = adapter.getPropositionId();

        for(int i = 0; i < propositionsName.size(); i++){
            Log.d("issou", propositionsName.get(i));
            db.updateReponse(Integer.parseInt(propositionsId.get(i)), propositionsName.get(i));
        }


        goodAnswer = String.valueOf(goodAnswerView.getText());
        if(goodAnswer.isEmpty() || !testParseInt(goodAnswer)){
            Toast.makeText(this, "La bonne réponse ne doit pas être vide et dois être un entier !", Toast.LENGTH_SHORT).show();
        }else if((Integer.parseInt(goodAnswer) <= 0) || (Integer.parseInt(goodAnswer) > propositionsName.size())){
            Toast.makeText(this, "La bonne reponse doit être un entier positif !", Toast.LENGTH_SHORT).show();
        }else{
            db.updateGoodReponse(idQuestion,Integer.parseInt(goodAnswer));
        }
    }



    //Test if the content is an integer
    public boolean testParseInt(String test){
        boolean parsable = true;
        try{
            Integer.valueOf(test);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }
}
