package com.josh.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class QuizActivity extends AppCompatActivity {
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPreviousButton;
    private Button mCheatButton;

    private TextView mQuestionTextView;
    private static final String KEY_INDEX = "index";
    private static final String QUESTION_INDEX = "answered";
    private static final String TAG = "QuizActivity";
    private static final int REQUEST_CODE_CHEAT=0;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_canada, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private boolean[] mQuestionsAnswered = new boolean[mQuestionBank.length];
    private int mCurrentIndex = 0;
    private int mPercent=0;
    private int mCorrect=0;
    private boolean mIsCheater;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode!= Activity.RESULT_OK){
            return;
        }
        if(resultCode==REQUEST_CODE_CHEAT){
            if(data==null){
                return;
            }
            mIsCheater=CheatActivity.wasAnswerShown(data);
        }
    }

    @Override


    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBooleanArray(QUESTION_INDEX,mQuestionsAnswered);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState !=null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mQuestionsAnswered = savedInstanceState.getBooleanArray(QUESTION_INDEX);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });



        mFalseButton = (Button)findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkAnswer(false);
            }
        });

        mTrueButton = (Button)findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });

        mNextButton = (Button)findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater=false;
                updateQuestion();

            }
        });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

                Intent intent = CheatActivity.newIntent(QuizActivity.this,answerIsTrue);

                startActivityForResult(intent,REQUEST_CODE_CHEAT);
            }
        });

        mPreviousButton = (Button)findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentIndex!=0) {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                    updateQuestion();
                }
            }
        });
        updateQuestion();
    }
    private void updateQuestion(){
        mFalseButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);
        mTrueButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);

        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

    }

    private  void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        mQuestionsAnswered[mCurrentIndex]=true;
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
        int messageResId = 0;
        if(mIsCheater) {
            messageResId = R.string.judgement_toast;
        }else{
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mCorrect++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this,messageResId,Toast.LENGTH_SHORT).show();
        if(mCurrentIndex==mQuestionBank.length-1){
            mPercent= (mCorrect*100)/mQuestionBank.length;
            String percent = mPercent+" % percent";
            Toast.makeText(this,percent,Toast.LENGTH_SHORT).show();

        }




    }
}
