package deptinfo.ubfc.quizzs.edit;

import android.content.Context;
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

public class EditPropositionsAdapter extends RecyclerView.Adapter<EditPropositionsAdapter.ViewHolder> implements Serializable {


    //Attributes
    private List<String> propositions,idPropositions;
    private LayoutInflater mInflater;
    private QuizzDataBase db;
    private int idQuestion;



    //Data is passed into the constructor
    EditPropositionsAdapter(Context context, List<String> propositions, List<String> idPropositions, QuizzDataBase db, int idQuestion) {
        this.mInflater = LayoutInflater.from(context);
        this.propositions = propositions;
        this.idPropositions = idPropositions;
        this.db = db;
        this.idQuestion = idQuestion;
    }

    //Inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_edit_propositions, parent, false);
        return new ViewHolder(view);
    }



    //Binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String proposition = propositions.get(position);
        holder.propositionName.setText(proposition);
        holder.deleteProposition.setContentDescription(idPropositions.get(position));
    }


    //Remove a string at a position
    public void removeAt(int position){
        propositions.remove(position);
        idPropositions.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, propositions.size());
        notifyItemRangeChanged(position, idPropositions.size());
    }


    public void add(String proposition){
        this.db.insertReponse(this.idQuestion,proposition);
        String id = String.valueOf(this.db.getLastReponse());
        propositions.add(proposition);
        idPropositions.add(id);
        notifyItemInserted(propositions.size()-1);
        notifyItemRangeChanged(propositions.size()-1, propositions.size());
        notifyItemRangeChanged(idPropositions.size()-1, idPropositions.size());
    }


    //Change a string at a position
    public void changeAt(int position, String newQuestion){
        propositions.set(position,newQuestion);
    }

    //Total number of rows
    @Override
    public int getItemCount() {
        return propositions.size();
    }


    //Stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextView.OnEditorActionListener, TextWatcher {
        ImageButton deleteProposition;
        TextView propositionName;


        ViewHolder(View itemView) {
            super(itemView);

            deleteProposition = itemView.findViewById(R.id.deleteButton);
            propositionName = itemView.findViewById(R.id.propositions);
            propositionName.addTextChangedListener(this);

            deleteProposition.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(view.getId() == deleteProposition.getId()){
                //Check which proposition was clicked on
                if(propositions.size() > 1) {
                    int id = Integer.parseInt((String) view.getContentDescription());
                    db.deleteUneReponse(id);
                    removeAt(getAdapterPosition());
                }else{
                    Toast.makeText(view.getContext(), "Vous ne pouvez pas supprimer la seule proposition restante !", Toast.LENGTH_SHORT).show();
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
        return propositions.get(id);
    }

    //Getting the list of propositions
    List<String> getPropositionName(){
        return this.propositions;
    }

    //Getting the list of the id of the proposition
    List<String> getPropositionId(){
        return this.idPropositions;
    }
}


