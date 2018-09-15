package edu.jhu.kqian2.offmaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

public class StrideCalcActivity extends AppCompatActivity {

    private static final int MALE = 0;
    private static final int FEMALE = 1;

    // Source: https://www.wikihow.com/Measure-Stride-Length
    private static final double MALE_MULTIPLIER = 0.415;
    private static final double FEMALE_MULTIPLIER = 0.413;

    private double stride;

    private EditText heightEdit;
    private RadioGroup sexButtons;
    private Button submitButton;
    private TextView strideText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stride_calc);

        heightEdit = findViewById(R.id.height_edit);
        sexButtons = findViewById(R.id.sex_buttons);

        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrideCalcActivity.this.calculateStride();
            }
        });

        strideText = findViewById(R.id.stride_text);
        strideText.setVisibility(View.INVISIBLE);
    }

    public void calculateStride() {
        double height = 0;
        String heightStr = heightEdit.getText().toString();
        try {
            height = Double.parseDouble(heightStr);
        } catch (NumberFormatException e) {}
        int buttonID = sexButtons.getCheckedRadioButtonId();
        if (buttonID == R.id.sex_button_male) {
            stride = height * MALE_MULTIPLIER;
        } else {
            stride = height * FEMALE_MULTIPLIER;
        }

        strideText.setText(getString(R.string.stride_text, stride));
        strideText.setVisibility(View.VISIBLE);
    }
}
