package deptinfo.ubfc.quizzs.play;
/*
    Barbeaut Reynald

    This file is used to display the final score of an user after he played a quizz.

 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import deptinfo.ubfc.quizzs.R;

public class ScoreDisplay extends Activity implements View.OnClickListener{
    //All of the attributes
    private Button returnButton, playAgain;
    private TextView scoreDisplay;
    private int score, idQuizz, total;


    //The function which is called on the creation of the activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);

        this.returnButton = (Button) (findViewById(R.id.homeButton));
        this.playAgain = (Button) (findViewById(R.id.playAgain));
        this.scoreDisplay = (TextView) (findViewById(R.id.score));

        this.returnButton.setOnClickListener(this);
        this.playAgain.setOnClickListener(this);

        //We get some information of the PlayQuizz file which will be used later
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        idQuizz = extras.getInt("id");
        score = extras.getInt("score");
        total = extras.getInt("total");
        initDisplay();
    }


    //Init the display of the score
    public void initDisplay(){
        scoreDisplay.setText(String.valueOf(score)+"/"+String.valueOf(total));
    }

    @Override
    public void onClick(View v) {
        //Check if the user want to play another game with the same quizz
        if(v.getId() == playAgain.getId()){
            Intent intent = new Intent(v.getContext(), PlayQuizz.class) ;
            intent.putExtra("id", idQuizz);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            v.getContext().startActivity (intent) ;
            finish();
        }else{
            //Else it means that the player want to go back to the main menu
            finish();
        }
    }
}
