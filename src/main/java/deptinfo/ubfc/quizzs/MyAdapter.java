package deptinfo.ubfc.quizzs;
/*
    Barbeaut Reynald

    This file is the adapter for the RecyclerView in the main activity
 */
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import deptinfo.ubfc.quizzs.dataBase.QuizzDataBase;
import deptinfo.ubfc.quizzs.edit.EditQuizz;
import deptinfo.ubfc.quizzs.play.PlayQuizz;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Serializable {
    //Attributes
    private List<String> quizzs;
    private List<String> idQuizzs;
    private LayoutInflater mInflater;
    private QuizzDataBase quizzDB;


    //Initalisation of the datas
    MyAdapter(Context context, List<String> quizzs,List<String> idQuizzs, QuizzDataBase quizzDB) {
        this.mInflater = LayoutInflater.from(context);
        this.quizzs = quizzs;
        this.idQuizzs = idQuizzs;
        this.quizzDB = quizzDB;
    }

    //Inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    //Binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String quizz = quizzs.get(position);
        String id = idQuizzs.get(position);
        holder.play.setContentDescription(id);
        holder.edit.setContentDescription(id);
        holder.delete.setContentDescription(id);
        holder.myTextView.setText(quizz);
    }

    //Update the data
    public void updateData(List<String> data, List<String> id){
        quizzs.clear();
        quizzs.addAll(data);

        idQuizzs.clear();
        idQuizzs.addAll(id);

        this.notifyDataSetChanged();
    }

    //Add a quizz
    public void add(String quizz){
        this.quizzDB.insertQuizz(quizz);
        int idQuizz = quizzDB.getLastQuizz();

        this.quizzDB.insertQuestion(idQuizz,"Question 1", 1);
        int idQuestion = quizzDB.getLastQuestion();

        this.quizzDB.insertReponse(idQuestion,"Reponse 1");


        quizzs.add(quizz);
        idQuizzs.add(String.valueOf(idQuizz));
        notifyItemInserted(quizzs.size()-1);
        notifyItemRangeChanged(quizzs.size()-1, quizzs.size());
        notifyItemRangeChanged(idQuizzs.size()-1, idQuizzs.size());
    }

    public void removeAt(int position){
        quizzs.remove(position);
        idQuizzs.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, quizzs.size());
        notifyItemRangeChanged(position, idQuizzs.size());
    }

    //Total number of rows
    @Override
    public int getItemCount() {
        return quizzs.size();
    }


    //Stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageButton play, edit, delete;
        List<String> quizzsName;
        List<String> quizzsId;

        ViewHolder(View itemView) {
            //Init each buttons which are inside rows
            super(itemView);
            myTextView = itemView.findViewById(R.id.QuizzName);
            play = itemView.findViewById(R.id.playButton);
            edit = itemView.findViewById(R.id.editButton);
            delete = itemView.findViewById(R.id.deleteButton);
            play.setOnClickListener(this);
            edit.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        //Check which button is used to launch the corresponding activity
        @Override
        public void onClick(View view) {
            //Launch the pay quizz activity
            if(view.getId() == play.getId()){
                Intent intent = new Intent(view.getContext(), PlayQuizz.class) ;
                intent.putExtra("id", Integer.parseInt(view.getContentDescription().toString()));
                view.getContext().startActivity (intent) ;

            }else if(view.getId() == edit.getId()){
                //Launch the edit quizz activity
                Intent intent = new Intent(view.getContext(), EditQuizz.class) ;
                intent.putExtra("id", Integer.parseInt(view.getContentDescription().toString()));
                view.getContext().startActivity (intent) ;
            }else{
                //Delete the corresponding quizz and update the RecyclerView
                int id = Integer.parseInt((String) view.getContentDescription());
                quizzDB.deleteOnCascade(id);
                removeAt(getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return quizzs.get(id);
    }

}