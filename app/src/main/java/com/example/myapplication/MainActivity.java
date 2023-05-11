package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    HashMap<String, String> stitch_instructions;
    HashMap<Integer, String> pattern_singles_instructions;
    HashMap<Integer, String> pattern_repeat_instructions;
    Integer counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stitch_instructions = create_stitch_instructions();
        pattern_singles_instructions = create_pattern_singles_instructions();
        pattern_repeat_instructions = create_pattern_repeat_instructions();

        final TextView txt_instructions=(TextView)findViewById(R.id.txt_instructions);
        final TextView txt_current_row=(TextView)findViewById(R.id.txt_current_row);
        final TextView txt_repeat_row=(TextView)findViewById(R.id.txt_repeat_row);
        final TextView txt_stitch=(TextView)findViewById(R.id.txt_stitch);
        final TextView txt_side=(TextView)findViewById(R.id.txt_side);
        final Button buttonAdd=(Button)findViewById(R.id.button_add);
        final Button buttonSubtract=(Button)findViewById(R.id.button_subract);

        counter = get_saved_counter();

        // Set up defaults
        txt_current_row.setText(update_current_row_text());
        txt_side.setText(update_side());
        txt_repeat_row.setText(get_row_repeat());
        txt_instructions.setText(get_pattern_instructions());
        txt_stitch.setText(get_stitch());

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                try {
                    counter = add_to_counter(1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                txt_current_row.setText(update_current_row_text());
                txt_side.setText(update_side());
                txt_instructions.setText(get_pattern_instructions());
                txt_repeat_row.setText(get_row_repeat());
                txt_stitch.setText(get_stitch());
            }
        });

        buttonSubtract.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                try {
                    counter = add_to_counter(-1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                txt_current_row.setText(update_current_row_text());
                txt_side.setText(update_side());
                txt_instructions.setText(get_pattern_instructions());
                txt_repeat_row.setText(get_row_repeat());
                txt_stitch.setText(get_stitch());
            }
        });
    }

    public String update_current_row_text() {
        return "Row: " + counter.toString();
    }

    public String update_side() {
        return counter % 2 == 0 ? "Wrong Side" : "Right Side";
    }

    public String get_pattern_instructions() {

        String out = "";
        if (pattern_singles_instructions.containsKey(counter)) {
            return pattern_singles_instructions.get(counter);
        } else {
            if (pattern_repeat_instructions.containsKey(counter)) {
                return pattern_repeat_instructions.get(counter);
            } else {
                Integer pattern_row = calculate_repeat();
                return pattern_repeat_instructions.get(pattern_row);

            }
        }
    }

    public Integer calculate_repeat() {
        // taking the current counter minus the repeat offset,
        // find its remainder from the total repeats.
        // re-add the offset to get the correct row key
        return  ((counter - 3) % 16) + 3;
    }

    public String get_row_repeat() {
        if (counter > 18 && counter < 109) {
            Integer repeat = calculate_repeat();
            return "Repeat of Row " + repeat.toString();
        } else return "";
    }

    public String get_stitch() {
        // X-st are every 4 starting from row 4
        // bobbles are every row 6
        Integer repeat = calculate_repeat();
        String stitch = "";
        if (repeat == 4 || repeat == 8 || repeat == 12 || repeat == 16) {
            stitch = "X-st\n";
            stitch += stitch_instructions.get("X-st");
        }
        if(repeat == 6) {
            stitch = "Bobble\n";
            stitch += stitch_instructions.get("bobble");
        }

        return stitch;
    }

    public Integer get_saved_counter() {
        Integer saved_counter = 0;
        try {
            FileInputStream fin = openFileInput("counter.txt");
            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            fin.close();
            saved_counter = Integer.parseInt(temp);

        } catch (IOException e) {
            return 0;
        }
        return saved_counter;
    }

    public Integer add_to_counter(Integer addition) throws IOException {
        FileOutputStream fOut = openFileOutput("counter.txt", Context.MODE_PRIVATE);

        Integer result;
        result = (counter + addition < 0) ? 0 : counter + addition;
        if (result >= 111) {
            result = 110;
        }

        String str = result.toString();
        fOut.write(str.getBytes());
        fOut.close();

        return result;
    }

    public HashMap<String, String> create_stitch_instructions() {
        HashMap<String, String> stitch_instructions = new HashMap<String, String>();
        stitch_instructions.put("bobble","Dc4tog in the same st. In next st, *yoh, " +
                "pull up a loop, yoh, pull through 2 loops; rep from * 3 times, " +
                "until you have 5 loops on hook, yoh");
        stitch_instructions.put("X-st","Skip next st, 1 dc in next st, 1 dc in skipped st.");
        return stitch_instructions;
    }

    public HashMap<Integer, String> create_pattern_singles_instructions() {
        HashMap<Integer, String> pattern_singles_instructions = new HashMap<Integer, String>();
        pattern_singles_instructions.put(0, "With color A, chain 194.\nOr multiples of 16 chs + 18.");
        pattern_singles_instructions.put(1, "2 sc in 2nd ch from hook, *1 sc in next " +
                "6 chs, sc3tog over next 3 chs, 1 sc in next 6 chs, 3 sc in next ch; rep from * " +
                "until 16 chs rem, 1 sc in next 6 chs, sc3tog over next 3 chs, 1 sc in next 6 chs, " +
                "2 sc in last ch. Turn.");
        pattern_singles_instructions.put(2, "Ch 3 (counts as dc here and throughout), *1 dc in same " +
                "st, 1 dc in next 6 sts, dc3tog over next 3 sts, 1 dc in next 6 sts, 3 dc in next " +
                "st; rep from * until 16 sts rem, 1 dc in next 6 sts, dc3tog over next 3 sts, 1 dc " +
                "in next 6 sts, 2 dc in last st. Break A. Turn.");
        pattern_singles_instructions.put(109, "Ch 1, 2 sc in same st, *1 sc in next 6 sts, sc3tog " +
                "over next 3 sts, *1 sc in next 6 sts, 3 sc in next st; rep from * until 16 sts rem," +
                " 1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 2 sc in last st.");
        pattern_singles_instructions.put(110, "FINISHING: Turn blanket ¼ turn. Continue with A and " +
                "evenly work sc along side edge of blanket until you reach 1st row. Break and " +
                "reattach A on other side and work sc evenly along that side. Fasten off.");
        return pattern_singles_instructions;
    }

    public HashMap<Integer, String> create_pattern_repeat_instructions() {
        HashMap<Integer, String> pattern_repeat_instructions = new HashMap<Integer, String>();
        pattern_repeat_instructions.put(3, "Attach B with a standing sc, 1 sc in same st, *1 sc in " +
                "next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 3 sc in next st; rep from" +
                " * until 16 sts rem, 1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 " +
                "sts, 2 sc in last st. Turn.");
        pattern_repeat_instructions.put(4,"Ch 3, 1 dc in same st, *X-st 3 times, dc3tog over next 3 " +
                "sts, X-st three times, 3 dc in next st; rep from * until 16 sts rem, X-st 3 times, " +
                "dc3tog over next 3 sts, X-st three times, 2 dc in last st. Turn.");
        pattern_repeat_instructions.put(5,"Ch 1, 2 sc in same st, *1 sc in next 6 sts, sc3tog over " +
                "next 3 sts, *1 sc in next 6 sts, 3 sc in next st; rep from * until 16 sts rem, 1 " +
                "sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 2 sc in last st. " +
                "Break B. Turn.");
        pattern_repeat_instructions.put(6,"Attach A with a standing sc, 1 sc in same st, *[1 sc in next st, bobble in next st, 1 sc in next st] twice, sc3tog over next 3 sts, [1 sc in next st, bobble in next st, 1 sc in next st] twice, 3 sc in next st; rep from * until 16 sts rem, [1 sc in next st, bobble in next st, 1 sc in next st] twice, sc3tog over next 3 sts, [1 sc in next st, bobble in next st, 1 sc in next st] twice, 2 sc in last st. Break A. Turn.");
        pattern_repeat_instructions.put(7,"Attach B with a standing sc, 1 sc in same st, *1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 3 sc in next st; rep from * until 16 sts rem, 1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 2 sc in last st. Turn.");
        pattern_repeat_instructions.put(8, "Ch 3, 1 dc in same st, *X-st 3 times, dc3tog over next 3 sts, X-st three times, 3 dc in next st; rep from * until 16 sts rem, X-st 3 times, dc3tog over next 3 sts, X-st three times, 2 dc in last st. Turn.");
        pattern_repeat_instructions.put(9, "Ch 1, 2 sc in same st, *1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 3 sc in next st; rep from * until 16 sts rem, 1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 2 sc in last st. Break B. Turn.");
        pattern_repeat_instructions.put(10, "With A, attach with sl st, ch 3, 1 dc in same st, *1 dc in next 6 sts, dc3tog over next 3 sts, 1 dc in next 6 sts, 3 dc in next st; rep from * until 16 sts rem, 1 dc in same st, 1 dc in next 6 sts, dc3tog over next 3 sts, 1 dc in next 6 sts, 2 dc in last st. Break A. Turn.");
        pattern_repeat_instructions.put(11, "Attach C with a standing sc, 1 sc in same st, *1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 3 sc in next st; rep from * until 16 sts rem, 1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 2 sc in last st. Turn.");
        pattern_repeat_instructions.put(12, "Ch 3, 1 dc in same st, *X-st 3 times, dc3tog over next 3 sts, X-st three times, 3 dc in next st; rep from * until 16 sts rem, X-st 3 times, dc3tog over next 3 sts, X-st three times, 2 dc in last st. Turn.");
        pattern_repeat_instructions.put(13, "Ch 1, 2 sc in same st, *1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 3 sc in next st; rep from * until 16 sts rem, 1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 2 sc in last st. Break C. Turn.");
        pattern_repeat_instructions.put(14, "Attach A with a standing sc, 1 sc in same st, *[1 sc in next st, bobble in next st, 1 sc in next st] twice, sc3tog over next 3 sts, [1 sc in next st, bobble in next st, 1 sc in next st] twice, 3 sc in next st; rep from * until 16 sts rem, [1 sc in next st, bobble in next st, 1 sc in next st] twice, sc3tog over next 3 sts, [1 sc in next st, bobble in next st, 1 sc in next st] twice, 2 sc in last st. Break A. Turn.");
        pattern_repeat_instructions.put(15, "Attach C with a standing sc, 1 sc in same st, *1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 3 sc in next st; rep from * until 16 sts rem, 1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 2 sc in last st. Turn.");
        pattern_repeat_instructions.put(16, "Ch 3, 1 dc in same st, *X-st 3 times, dc3tog over next 3 sts, X-st three times, 3 dc in next st; rep from * until 16 sts rem, X-st 3 times, dc3tog over next 3 sts, X-st three times, 2 dc in last st. Turn.");
        pattern_repeat_instructions.put(17, "Ch 1, 2 sc in same st, *1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 3 sc in next st; rep from * until 16 sts rem, 1 sc in next 6 sts, sc3tog over next 3 sts, *1 sc in next 6 sts, 2 sc in last st. Break C. Turn.");
        pattern_repeat_instructions.put(18, "With A, attach with sl st, ch 3, 1 dc in same st, *1 dc in next 6 sts, dc3tog over next 3 sts, 1 dc in next 6 sts, 3 dc in next st; rep from * until 16 sts rem, 1 dc in same st, 1 dc in next 6 sts, dc3tog over next 3 sts, 1 dc in next 6 sts, 2 dc in last st. Break A. Turn.");
        return pattern_repeat_instructions;
    }
}
