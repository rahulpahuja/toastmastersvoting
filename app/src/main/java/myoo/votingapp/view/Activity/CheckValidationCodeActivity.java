package myoo.votingapp.view.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import myoo.votingapp.R;


public class CheckValidationCodeActivity extends Activity implements View.OnClickListener {

    Button btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_0;
    ImageView imageViewBack;
    String data = "";
    TextView txt_forget,txt_message;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        init();

        // adding  dots
        addBottomDots(data.length());


    }

    public void init() {

        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_3 = (Button) findViewById(R.id.btn_3);
        btn_4 = (Button) findViewById(R.id.btn_4);
        btn_5 = (Button) findViewById(R.id.btn_5);
        btn_6 = (Button) findViewById(R.id.btn_6);
        btn_7 = (Button) findViewById(R.id.btn_7);
        btn_8 = (Button) findViewById(R.id.btn_8);
        btn_9 = (Button) findViewById(R.id.btn_9);
        btn_0 = (Button) findViewById(R.id.btn_0);

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        txt_forget = (TextView) findViewById(R.id.txt_forget);
        txt_message = (TextView) findViewById(R.id.txt_message);
        txt_forget.setVisibility(View.GONE);

        if (VotingActivity.Companion.isVoteSelectionScreen() == true)
        {
            txt_message.setVisibility(View.VISIBLE);
        }


        //Implementing onClick
        imageViewBack.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_0.setOnClickListener(this);
        txt_forget.setOnClickListener(this);
    }


    private void addBottomDots(int currentPage) {
        dotsLayout.removeAllViews();
        dots = new TextView[4];
        if (currentPage == 0) {
            for (int i = 0; i < 4; i++) {
                dots[i] = new TextView(this);
                dots[i].setGravity(Gravity.CENTER);
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(50);
                dots[i].setPadding(15, 15, 15, 15);
                dots[i].setTextColor(getResources().getColor(R.color.dot_in_active));
                dotsLayout.addView(dots[i]);
            }
        } else {
            for (int i = 0; i < dots.length; i++) {
                if (i >= currentPage) {
                    dots[i] = new TextView(this);
                    dots[i].setGravity(Gravity.CENTER);
                    dots[i].setText(Html.fromHtml("&#8226;"));
                    dots[i].setTextSize(50);
                    dots[i].setPadding(15, 15, 15, 15);
                    dots[i].setTextColor(getResources().getColor(R.color.dot_in_active));
                    dotsLayout.addView(dots[i]);
                } else {
                    dots[i] = new TextView(this);
                    dots[i].setGravity(Gravity.CENTER);
                    dots[i].setText(Html.fromHtml("&#8226;"));
                    dots[i].setTextSize(50);
                    dots[i].setPadding(15, 15, 15, 15);
                    dots[i].setTextColor(getResources().getColor(R.color.dot_in_inactive));
                    dotsLayout.addView(dots[i]);
                }

            }
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_1:
                data = data + "1";
                CheckCode(data);
                break;

            case R.id.btn_2:
                data = data + "2";
                CheckCode(data);
                break;

            case R.id.btn_3:
                data = data + "3";
                CheckCode(data);
                break;

            case R.id.btn_4:
                data = data + "4";
                CheckCode(data);
                break;

            case R.id.btn_5:
                data = data + "5";
                CheckCode(data);
                break;

            case R.id.btn_6:
                data = data + "6";
                CheckCode(data);
                break;

            case R.id.btn_7:
                data = data + "7";
                CheckCode(data);
                break;

            case R.id.btn_8:
                data = data + "8";
                CheckCode(data);
                break;

            case R.id.btn_9:
                data = data + "9";
                CheckCode(data);
                break;

            case R.id.btn_0:
                data = data + "0";
                CheckCode(data);
                break;


            case R.id.imageViewBack:
                if (data.length() != 0) {
                    data = data.substring(0, data.length() - 1);
                    CheckCode(data);
                }

                break;
            case R.id.txt_forget:
                Intent intent = new Intent(CheckValidationCodeActivity.this, ForgetCodeActivity.class);
                startActivity(intent);
                finish();

                break;


        }
    }


    public void CheckCode(String code) {
        if (data.length() == 4) {
            addBottomDots(code.length());
            SharedPreferences sharedpreferences = getSharedPreferences("votingapp", Context.MODE_PRIVATE);
            if (sharedpreferences.getString("password", "").equalsIgnoreCase("")) {
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("password", data);
                editor.commit();
            } else {
                String current_password = sharedpreferences.getString("password", "");
                if (current_password.equalsIgnoreCase(data)) {
                    if (PrepareAllVotingItemsScreen.Companion.isPrepareallVotingResult() == true) {
                        PrepareAllVotingItemsScreen.Companion.setPrepareallVotingResult(false);
                        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);

                        intent.putExtra("meeting_number", getIntent().getStringExtra("meeting"));

                        startActivity(intent);
                        finish();
                    } else if (VotingActivity.Companion.isVoteSelectionScreen() == true)
                    {
                        VotingActivity.Companion.setVoteSelectionScreen(false);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else if (VotingActivity.Companion.isVoteSelectionScreen() == true)
                    {
                        VotingActivity.Companion.setPrepareallVotingResult(false);
                        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                        intent.putExtra("meeting_number", getIntent().getStringExtra("meeting"));
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(CheckValidationCodeActivity.this, "Invalid Password", Toast.LENGTH_LONG).show();
                    data = "";
                    addBottomDots(data.length());
                }
            }


        } else {
            addBottomDots(code.length());
        }

    }
}
