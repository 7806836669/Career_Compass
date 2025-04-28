package com.sruthi.myapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sruthi.myapp.model.FieldsQuestion;

import java.util.List;
import java.util.Map;

public class FieldsQuestionAdapter extends RecyclerView.Adapter<FieldsQuestionAdapter.ViewHolder> {

    private List<FieldsQuestion> questionsList;
    private Map<Integer, String> selectedAnswers;

    public FieldsQuestionAdapter(List<FieldsQuestion> questionsList, Map<Integer, String> selectedAnswers) {
        this.questionsList = questionsList;
        this.selectedAnswers = selectedAnswers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FieldsQuestion question = questionsList.get(position);
        holder.questionText.setText(question.getQuestionText());
        holder.optionA.setText(question.getOptionA());
        holder.optionB.setText(question.getOptionB());
        holder.optionC.setText(question.getOptionC());
        holder.optionD.setText(question.getOptionD());

        String selected = selectedAnswers.get(question.getId());
        if (selected != null) {
            switch (selected) {
                case "a":
                    holder.optionA.setChecked(true);
                    break;
                case "b":
                    holder.optionB.setChecked(true);
                    break;
                case "c":
                    holder.optionC.setChecked(true);
                    break;
                case "d":
                    holder.optionD.setChecked(true);
                    break;
            }
        } else {
            holder.radioGroup.clearCheck();
        }

        holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String answer = "";
            if (checkedId == holder.optionA.getId()) {
                answer = "a";
            } else if (checkedId == holder.optionB.getId()) {
                answer = "b";
            } else if (checkedId == holder.optionC.getId()) {
                answer = "c";
            } else if (checkedId == holder.optionD.getId()) {
                answer = "d";
            }
            selectedAnswers.put(question.getId(), answer);
        });
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView questionText;
        RadioButton optionA, optionB, optionC, optionD;
        RadioGroup radioGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            optionA = itemView.findViewById(R.id.radioOptionA);
            optionB = itemView.findViewById(R.id.radioOptionB);
            optionC = itemView.findViewById(R.id.radioOptionC);
            optionD = itemView.findViewById(R.id.radioOptionD);
            radioGroup = itemView.findViewById(R.id.radioGroup);
        }
    }
}
