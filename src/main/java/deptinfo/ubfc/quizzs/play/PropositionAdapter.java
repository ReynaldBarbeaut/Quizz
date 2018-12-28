package deptinfo.ubfc.quizzs.play;
/*
    Barbeaut Reynald

    This file is used for the Recycler View of the propositions when the user plays a quizz

 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import deptinfo.ubfc.quizzs.R;

public class PropositionAdapter extends RecyclerView.Adapter<PropositionAdapter.ViewHolder> implements Serializable {
    //Attributes
    private List<String> propositions;
    private LayoutInflater mInflater;
    private OnPropositionClickListener propositionClickListener;


    //Data is passed into the constructor
    PropositionAdapter(Context context, List<String> propositions, OnPropositionClickListener propositionClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.propositions = propositions;
        this.propositionClickListener = propositionClickListener;
    }

    //Inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_propositions, parent, false);
        return new ViewHolder(view);
    }

    //Binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String proposition = propositions.get(position);
        holder.myTextView.setText(proposition);
    }

    //Update the list of propositions
    public void updateData(List<String> data){
        propositions.clear();
        propositions.addAll(data);
        this.notifyDataSetChanged();
    }

    //Total number of rows
    @Override
    public int getItemCount() {
        return propositions.size();
    }


    //Stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.proposition);
            myTextView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(view.getId() == myTextView.getId()){
                //Check which proposition was clicked on
                propositionClickListener.OnPropositionClickListener(getAdapterPosition());
            }
        }
    }

    //Convenience method for getting data at click position
    String getItem(int id) {
        return propositions.get(id);
    }


}