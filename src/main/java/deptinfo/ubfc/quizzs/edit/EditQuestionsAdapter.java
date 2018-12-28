package deptinfo.ubfc.quizzs.edit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import deptinfo.ubfc.quizzs.dataBase.QuizzDataBase;
import deptinfo.ubfc.quizzs.R;

public class EditQuestionsAdapter extends RecyclerView.Adapter<EditQuestionsAdapter.ViewHolder> implements Serializable {
    //Attributes
    private List<String> questions,idQuestions;
    private LayoutInflater mInflater;
    private QuizzDataBase db;
    private int idQuizz;



    //Data is passed into the constructor
    EditQuestionsAdapter(Context context, List<String> questions, List<String> idQuestions, QuizzDataBase db, int idQuizz) {
        this.mInflater = LayoutInflater.from(context);
        this.questions = questions;
        this.idQuestions = idQuestions;
        this.db = db;
        this.idQuizz =idQuizz;
    }

    //Inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_edit_questions, parent, false);
        return new EditQuestionsAdapter.ViewHolder(view);
    }


    //Add a question
    public void add(String question){
        this.db.insertQuestion(this.idQuizz,question,1);
        int idQuestion = db.getLastQuestion();
        this.db.insertReponse(idQuestion,"Reponse 1");
        String id = String.valueOf(idQuestion);

        questions.add(question);
        idQuestions.add(id);
        notifyItemInserted(questions.size()-1);
        notifyItemRangeChanged(questions.size()-1, questions.size());
        notifyItemRangeChanged(idQuestions.size()-1, idQuestions.size());
    }


    //Binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String question = questions.get(position);
        holder.questionName.setText(question);
        holder.deleteQuestion.setContentDescription(idQuestions.get(position));
        holder.editQuestion.setContentDescription(idQuestions.get(position));
    }


    //Remove a string at a position
    public void removeAt(int position){
        questions.remove(position);
        idQuestions.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, questions.size());
        notifyItemRangeChanged(position, idQuestions.size());
    }


    //Change a string at a position
    public void changeAt(int position, String newQuestion){
        questions.set(position,newQuestion);
    }

    //Total number of rows
    @Override
    public int getItemCount() {
        return questions.size();
    }


    //Stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextView.OnEditorActionListener, TextWatcher {
        ImageButton editQuestion, deleteQuestion;
        TextView questionName;


        ViewHolder(View itemView) {
            super(itemView);

            editQuestion = itemView.findViewById(R.id.editQuestion);
            deleteQuestion = itemView.findViewById(R.id.deleteQuestion);
            questionName = itemView.findViewById(R.id.questionName);
            questionName.addTextChangedListener(this);

            editQuestion.setOnClickListener(this);
            deleteQuestion.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(view.getId() == editQuestion.getId()){
                //Check which proposition was clicked on
                int id = Integer.parseInt((String) view.getContentDescription());
                Intent intent = new Intent(view.getContext(), EditPropositions.class) ;
                intent.putExtra("id", id);
                view.getContext().startActivity (intent) ;
            }else if(view.getId() == deleteQuestion.getId()){
                //Check which proposition was clicked on
                if(questions.size() > 1) {
                    int id = Integer.parseInt((String) view.getContentDescription());
                    db.deleteQuestion(id);
                    db.deleteReponse(id);
                    removeAt(getAdapterPosition());
                }else{
                    Toast.makeText(view.getContext(), "Vous ne pouvez pas supprimer la seule question restante !", Toast.LENGTH_SHORT).show();
                }
            }
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!s.equals("") ) {
                changeAt(getAdapterPosition(), String.valueOf(s));
            }
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            return false;
        }
    }

    //Convenience method for getting data at click position
    String getItem(int id) {
        return questions.get(id);
    }

    //Getting the list of question
    List<String> getQuestionName(){
        return this.questions;
    }

    //Getting the list of the id of the questions
    List<String> getQuestionId(){
        return this.idQuestions;
    }
}
