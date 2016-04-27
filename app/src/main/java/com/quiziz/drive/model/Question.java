package com.quiziz.drive.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.quiziz.drive.model.db.QuizizParseContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pototo on 03/03/16.
 */
public class Question implements Parcelable{

    private static final String TAG = "Question";
    private static final int OPTION_RIGHT_INDEX = 1;
    private static final int OPTION_TEXT_INDEX = 0;
    private static final int PERFECT_SCORE = 100;
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String ACTIVE = "Activa";
    private static final String INACTIVE = "Inactiva";


    public enum UserChooses {
        A,B,C
    }

    private String state;
    private int chapter;
    private String question;
    private List<String> optionA;
    private List<String> optionB;
    private List<String> optionC;
    private String image;
    private String imageUrl;
    private int imageHeight;
    private int imageWidth;
    private int difficulty;
    private boolean isAnswerRight;
    private UserChooses userChooses;
    private boolean isSpendTime;
    private OnQuestionListener mCallback;
    private boolean isResult;

    public interface OnQuestionListener {
        void onPostExecute(ArrayList<Question> questions);
    }

    public void attach(OnQuestionListener callback){
        mCallback = callback;
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public Question(){}

    public Question(Parcel in){
        state = in.readString();
        chapter = in.readInt();
        question = in.readString();
        optionA = in.readArrayList(Question.class.getClassLoader());
        optionB = in.readArrayList(Question.class.getClassLoader());
        optionC = in.readArrayList(Question.class.getClassLoader());
        image = in.readString();
        imageUrl = in.readString();
        imageHeight = in.readInt();
        imageWidth = in.readInt();
        difficulty = in.readInt();
        isAnswerRight = in.readByte() != 0;
    }


    public Question(ParseObject row){
        state = row.getString(QuizizParseContract.Question.STATE);
        chapter = row.getInt(QuizizParseContract.Question.CHAPTHER);
        question = row.getString(QuizizParseContract.Question.QUESTION);
        optionA = row.getList(QuizizParseContract.Question.OPTION_A);
        optionB = row.getList(QuizizParseContract.Question.OPTION_B);
        optionC = row.getList(QuizizParseContract.Question.OPTION_C);
        image = row.getString(QuizizParseContract.Question.IMAGE);
        imageUrl = row.getString(QuizizParseContract.Question.IMAGE_URL);
        imageHeight = row.getInt(QuizizParseContract.Question.IMAGE_HEIGHT);
        imageWidth = row.getInt(QuizizParseContract.Question.IMAGE_WIDTH);
        difficulty = row.getInt(QuizizParseContract.Question.DIFFICULTY);
    }

    public static int getQuestionsCount(final Configuration configuration){
        int count = 0;
        try{
            List<Integer> chapters = configuration.getChapters(true);
            int difficulty = configuration.getDifficulty();

            ParseQuery query = new ParseQuery(QuizizParseContract.Question.TABLE_NAME);
            query.whereEqualTo(QuizizParseContract.Question.DIFFICULTY, difficulty);
            query.whereContainedIn(QuizizParseContract.Question.CHAPTHER, chapters);

            count = query.count();
        }catch(ParseException exception){
            Log.e(TAG, exception.getMessage());
        }
        return count;
    }

    public void getQuestionsInBackground(final Configuration configuration){
        try{
            List<Integer> chapters = configuration.getChapters(true);
            int difficulty = configuration.getDifficulty();

            ParseQuery query = new ParseQuery(QuizizParseContract.Question.TABLE_NAME);
            query.whereEqualTo(QuizizParseContract.Question.DIFFICULTY, difficulty);
            query.whereEqualTo(QuizizParseContract.Question.STATE, ACTIVE);
            query.whereContainedIn(QuizizParseContract.Question.CHAPTHER, chapters);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> results, ParseException e) {
                    int size = results.size();
                    int amountOfQuestions = configuration.getQuestions();
                    amountOfQuestions = amountOfQuestions > size ? size : amountOfQuestions;
                    List<Integer> randomIndexList = getRandomIndexList(amountOfQuestions, size);
                    ArrayList<Question> questions = new ArrayList();
                    for(Integer randomIndex : randomIndexList) {
                        ParseObject row = results.get(randomIndex);
                        questions.add(new Question(row));
                    }
                    if(isAttached())
                        mCallback.onPostExecute(questions);
                }
            });
        }catch(ClassCastException exception){
            Log.e(TAG, exception.getMessage());
        }
    }

    private boolean isAttached() {
        return mCallback != null;
    }

    private static List<Integer> getRandomIndexList(int amountOfQuestions, int size) {
        int randomSize = size - 1;
        List<Integer> randomIndexList = new ArrayList();
        for(int i = 0; i < amountOfQuestions; i++) {
            int randomIndex = randomElement(0, randomSize);
            if(!randomIndexList.contains(randomIndex))
                randomIndexList.add(randomIndex);
            else
                i--;
        }
        return randomIndexList;
    }

    public static int randomElement(int min, int max) {
        Random rn = new Random();
        int random = rn.nextInt(max - min + 1) + min;
        return random;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(state);
        out.writeInt(chapter);
        out.writeString(question);
        out.writeList(optionA);
        out.writeList(optionB);
        out.writeList(optionC);
        out.writeString(image);
        out.writeString(imageUrl);
        out.writeInt(imageHeight);
        out.writeInt(imageWidth);
        out.writeInt(difficulty);
        out.writeByte((byte)(isAnswerRight ? 1 : 0));
    }

    public String getState() {
        return state;
    }

    public int getChapter() {
        return chapter;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptionA() {
        return optionA;
    }

    public List<String> getOptionB() {
        return optionB;
    }

    public List<String> getOptionC() {
        return optionC;
    }

    public String getImage() {
        return image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void checkIfOptionAIsTheRightAnswer(){
        if(optionA.size() >= 2){
            String value = optionA.get(OPTION_RIGHT_INDEX);
            isAnswerRight = value.equals(TRUE) ? true : false;
        }
    }

    public void checkIfOptionBIsTheRightAnswer(){
        if(optionB.size() >= 2){
            String value = optionB.get(OPTION_RIGHT_INDEX);
            isAnswerRight = value.equals(TRUE) ? true : false;
        }
    }

    public void checkIfOptionCIsTheRightAnswer(){
        if(optionC.size() >= 2){
            String value = optionC.get(OPTION_RIGHT_INDEX);
            isAnswerRight = value.equals(TRUE) ? true : false;
        }
    }

    public boolean isAnswerRight(){
        return isAnswerRight;
    }

    public boolean hasImage() {
        return image.equals(FALSE) ? false : true;
    }

    public void setUserChooses(UserChooses userChooses){
        this.userChooses = userChooses;
    }

    public String getUserChoosesAnswerText(){
        String answerText = "";

        if(userChooses == UserChooses.A)
            answerText = optionA.get(OPTION_TEXT_INDEX);
        else if(userChooses == UserChooses.B)
            answerText = optionB.get(OPTION_TEXT_INDEX);
        else if(userChooses == UserChooses.C)
            answerText = optionC.get(OPTION_TEXT_INDEX);

        return answerText;
    }

    public String getRightAnswerText(){
        String rightAnswerText = "";

        String valueA = optionA.get(OPTION_RIGHT_INDEX);
        String valueB = optionB.get(OPTION_RIGHT_INDEX);
        String valueC = optionC.get(OPTION_RIGHT_INDEX);

        if(valueA.equals(TRUE))
            rightAnswerText = optionA.get(OPTION_TEXT_INDEX);
        else if(valueB.equals(TRUE))
            rightAnswerText = optionB.get(OPTION_TEXT_INDEX);
        else if(valueC.equals(TRUE))
            rightAnswerText = optionC.get(OPTION_TEXT_INDEX);

        return rightAnswerText;
    }

    public boolean isSpendTime() {
        return isSpendTime;
    }

    public void setSpendTime(boolean isSpendTime) {
        this.isSpendTime = isSpendTime;
    }

    public static double getScore(List<Question> questions){
        double score = 0;
        if(questions != null) {
            int total = questions.size();
            int rightAnswersCount = getAmountOfRightAnswers(questions);
            score = (PERFECT_SCORE * rightAnswersCount) / total;
        }
        return score;
    }

    public static int getAmountOfRightAnswers(List<Question> questions){
        int count = 0;
        for(Question question : questions)
            if(question.isAnswerRight())
                count++;
        return count;
    }

    public static List<Question> getIncorrectAnswers(List<Question> questions){
        List<Question> incorrectAnswers = new ArrayList();
        for(Question question : questions)
            if(!question.isAnswerRight())
                incorrectAnswers.add(question);
        return incorrectAnswers;
    }

    public boolean isResult() {
        return isResult;
    }

    public void setResult(boolean isResult) {
        this.isResult = isResult;
    }
}
