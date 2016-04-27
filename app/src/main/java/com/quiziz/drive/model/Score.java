package com.quiziz.drive.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pototo 07/04/16.
 */
public class Score implements Parcelable {
    private String score;
    private int faceImageId;
    private String scoreMessage;
    private String rightQuestions;

    public static final Parcelable.Creator<Score> CREATOR = new Parcelable.Creator<Score>() {
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        public Score[] newArray(int size) {
            return new Score[size];
        }
    };

    public Score(){}

    public Score(Parcel in){
        score = in.readString();
        faceImageId = in.readInt();
        scoreMessage = in.readString();
        rightQuestions = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(score);
        out.writeInt(faceImageId);
        out.writeString(scoreMessage);
        out.writeString(rightQuestions);
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getFaceImageId() {
        return faceImageId;
    }

    public void setFaceImageId(int faceImageId) {
        this.faceImageId = faceImageId;
    }

    public String getScoreMessage() {
        return scoreMessage;
    }

    public void setScoreMessage(String scoreMessage) {
        this.scoreMessage = scoreMessage;
    }

    public String getRightQuestions() {
        return rightQuestions;
    }

    public void setRightQuestions(String rightQuestions) {
        this.rightQuestions = rightQuestions;
    }
}
