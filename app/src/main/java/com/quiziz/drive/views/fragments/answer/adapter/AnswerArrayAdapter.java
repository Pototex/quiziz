package com.quiziz.drive.views.fragments.answer.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quiziz.drive.R;
import com.quiziz.drive.model.Question;

import java.util.List;

/**
 * Created by pototo on 15/03/16.
 */
public class AnswerArrayAdapter extends ArrayAdapter<Question>{
    private static final int VIEW_TYPE_COUNT = 2;
    private static final String OF = " de ";
    private static final int QUESTION = 0;
    private static final int RESULT = 1;
    private LayoutInflater mInflater;
    private OnAnswerArrayAdapterListener mCallback;

    public static class ViewHolder{
        ImageView mIconResultImageView;
        TextView mQuestionOfTotalQuestionsTextView;
        TextView mQuestionTextView;
        TextView mCorrectAnswerTextView;
        TextView mIncorrectAnswerTextView;

        TextView mResultScoreTextView;
        ImageView mResultScoreFaceImageView;
        TextView mResultScoreMessageTextView;
        TextView mResultAmountOfRightQuestionsTextView;
        ImageView mShareFacebookImageView;
        ImageView mShareTwitterImageView;
        ImageView mShareWhatsappImageView;
        TextView mResultCheckAnswerTextView;
        TextView mResultRepeatTestAgainTextView;
        TextView mResultRepeatIncorrectAnswerTextView;
        TextView mResultGoToBeginTextView;
    }

    public interface OnAnswerArrayAdapterListener {
        void setResultScoreTextView(TextView textView);
        void setResultScoreFaceImageView(ImageView imageView);
        void setResultScoreMessageTextView(TextView textView);
        void setResultAmountOfRightQuestionsTextView(TextView textView);
        void setImageViewMap(ImageView facebook, ImageView twitter, ImageView whatsapp);
        void setGoToBeginTextView(TextView textView);
        void setRepeatIncorrectAnswerTextView(TextView textView);
        void setRepeatTestAgainTextView(TextView textView);
    }

    public void onAttach(OnAnswerArrayAdapterListener callback){
        mCallback = callback;
    }

    private boolean isAttached(){
        return mCallback != null;
    }

    public AnswerArrayAdapter(Context context, List<Question> questions){
        super(context, 0, questions);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        int type = getItemViewType(position);

        if(convertView == null){
            viewHolder = new ViewHolder();

            switch (type) {
                case RESULT:
                    convertView = mInflater.inflate(R.layout.array_adapter_answer_result, parent, false);
                    setResultHolder(convertView, viewHolder);
                    break;
                case QUESTION:
                    convertView = mInflater.inflate(R.layout.array_adapter_answer, parent, false);
                    setQuestionHolder(convertView, viewHolder);
                    break;
            }

            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder)convertView.getTag();

        int total = getCount() - 1;
        int questionIndex = position + 1;
        Question question = getItem(position);

        if(question.isResult())
            setResultValues(viewHolder);
        else
            setQuestionValues(viewHolder, question, total, questionIndex);

        return convertView;
    }

    private void setResultHolder(View convertView, ViewHolder viewHolder) {
        viewHolder.mResultScoreTextView = (TextView)convertView.findViewById(R.id.result_score);
        viewHolder.mResultScoreFaceImageView = (ImageView)convertView.findViewById(R.id.result_score_face);
        viewHolder.mResultScoreMessageTextView = (TextView)convertView.findViewById(R.id.result_score_message);
        viewHolder.mResultAmountOfRightQuestionsTextView = (TextView)convertView.findViewById(R.id.result_amount_of_right_questions);

        viewHolder.mShareFacebookImageView = (ImageView)convertView.findViewById(R.id.result_share_facebook);
        viewHolder.mShareTwitterImageView = (ImageView)convertView.findViewById(R.id.result_share_twitter);
        viewHolder.mShareWhatsappImageView = (ImageView)convertView.findViewById(R.id.result_share_whatsapp);

        viewHolder.mResultRepeatTestAgainTextView = (TextView)convertView.findViewById(R.id.result_repeat_test_again);
        viewHolder.mResultRepeatIncorrectAnswerTextView = (TextView)convertView.findViewById(R.id.result_repeat_incorrect_answer);
        viewHolder.mResultGoToBeginTextView = (TextView)convertView.findViewById(R.id.result_go_to_begin);


    }

    private void setResultValues(ViewHolder viewHolder){
        if(isAttached()) {
            mCallback.setResultScoreTextView(viewHolder.mResultScoreTextView);
            mCallback.setResultScoreMessageTextView(viewHolder.mResultScoreMessageTextView);
            mCallback.setResultScoreFaceImageView(viewHolder.mResultScoreFaceImageView);
            mCallback.setResultAmountOfRightQuestionsTextView(viewHolder.mResultAmountOfRightQuestionsTextView);

            mCallback.setImageViewMap(viewHolder.mShareFacebookImageView, viewHolder.mShareTwitterImageView, viewHolder.mShareWhatsappImageView);

            mCallback.setGoToBeginTextView(viewHolder.mResultGoToBeginTextView);
            mCallback.setRepeatTestAgainTextView(viewHolder.mResultRepeatTestAgainTextView);
            mCallback.setRepeatIncorrectAnswerTextView(viewHolder.mResultRepeatIncorrectAnswerTextView);
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Question question = getItem(position);
        return question.isResult() ? RESULT : QUESTION;
    }

    private void setQuestionValues(ViewHolder viewHolder, Question question, int total, int questionIndex) {
        viewHolder.mQuestionOfTotalQuestionsTextView.setText(questionIndex + OF + total);
        if(question.isSpendTime())
            viewHolder.mIconResultImageView.setImageResource(R.drawable.icon_time_small);
        else
            viewHolder.mIconResultImageView.setImageResource(question.isAnswerRight() ? R.drawable.icon_check_small : R.drawable.icon_wrong_small);
        viewHolder.mQuestionTextView.setText(Html.fromHtml(question.getQuestion()));

        if(question.isAnswerRight()){
            viewHolder.mIncorrectAnswerTextView.setVisibility(View.GONE);
            viewHolder.mCorrectAnswerTextView.setText(question.getUserChoosesAnswerText());
        }else{
            viewHolder.mIncorrectAnswerTextView.setVisibility(View.VISIBLE);

            String userChooses = question.getUserChoosesAnswerText();
            viewHolder.mIncorrectAnswerTextView.setText(userChooses);
            viewHolder.mIncorrectAnswerTextView.setVisibility((userChooses != null && !userChooses.isEmpty()) ? View.VISIBLE : View.GONE);

            viewHolder.mCorrectAnswerTextView.setText(question.getRightAnswerText());
        }
    }

    private void setQuestionHolder(View convertView, ViewHolder viewHolder) {
        viewHolder.mIconResultImageView = (ImageView) convertView.findViewById(R.id.icon_result);
        viewHolder.mQuestionOfTotalQuestionsTextView = (TextView) convertView.findViewById(R.id.question_of_total_questions);
        viewHolder.mQuestionTextView = (TextView) convertView.findViewById(R.id.question_text);
        viewHolder.mCorrectAnswerTextView = (TextView) convertView.findViewById(R.id.correct_answer);
        viewHolder.mIncorrectAnswerTextView = (TextView) convertView.findViewById(R.id.incorrect_answer);
    }
}
