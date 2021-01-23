package artem.com.tz_osadchenko_simpler.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import artem.com.tz_osadchenko_simpler.R;
import artem.com.tz_osadchenko_simpler.utils.Constants;

public class SlotMachineFragment extends Fragment {

    private ImageView ivFirstSlot, ivSecondSLot, ivThirdSlot;
    private Button btnSpin;
    private TextView tvBalance;
    private EditText etBet;
    private int mBalance;

    private ArrayList<Integer> imageList, imageBetList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_slot_machine, container, false);

        restoreBalance();

        initView(view);

        btnSpin.setOnClickListener(v -> {
            if (isBetValid()){
                imageBetList.clear();
                handleImageAnimation(ivFirstSlot, ivSecondSLot, ivThirdSlot);
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.enter_valid_value,
                        mBalance), Toast.LENGTH_LONG).show();
            }
        });

        return view;

    }

    /**
     * Both methods below created to separate a lot of useless code from onCreateView
     */

    private void initView(View view) {

        ivFirstSlot = view.findViewById(R.id.ivFirstSlot);
        ivSecondSLot = view.findViewById(R.id.ivSecondSLot);
        ivThirdSlot = view.findViewById(R.id.ivThirdSlot);
        tvBalance = view.findViewById(R.id.tvBalance);
        etBet = view.findViewById(R.id.etBet);
        btnSpin = view.findViewById(R.id.btnSpin);

        ivFirstSlot.setImageResource(R.drawable.question);
        ivSecondSLot.setImageResource(R.drawable.question);
        ivThirdSlot.setImageResource(R.drawable.question);

        etBet.setHint(R.string.set_bet);

        tvBalance.setText(getString(R.string.set_start_value, mBalance));

        initImageList();

    }

    private void initImageList() {

        imageList = new ArrayList<>();
        imageBetList = new ArrayList<>();

        imageList.add(R.drawable.one);
        imageList.add(R.drawable.two);
        imageList.add(R.drawable.three);
        imageList.add(R.drawable.four);
        imageList.add(R.drawable.five);
        imageList.add(R.drawable.six);
        imageList.add(R.drawable.seven);
        imageList.add(R.drawable.eight);

    }

    /**
     * Check for null and input conditions
     */

    private boolean isBetValid() {

        boolean isBetCorrespond = false;
        String strBet = String.valueOf(etBet.getText());
        String[] separated = String.valueOf(tvBalance.getText()).split(":");
        int currentBalance = Integer.parseInt(separated[1]);

        if (!strBet.equals("")){

            int bet =  Integer.parseInt(strBet);

            if (bet >= 5 && bet <= currentBalance) {
                ivFirstSlot.setImageResource(R.drawable.question);
                ivSecondSLot.setImageResource(R.drawable.question);
                ivThirdSlot.setImageResource(R.drawable.question);
                isBetCorrespond = true;
            }
        }

        return isBetCorrespond;

    }

    /**
     * The method handles animations for
     * @param x - first imageView
     * @param y- second imageView
     * @param z- third imageView
     */

    private void handleImageAnimation(ImageView x, ImageView y, ImageView z) {

        ObjectAnimator firstAnimator = ObjectAnimator.ofFloat(x, "rotation",0f, 7200f);
        firstAnimator.setDuration(6000);

        ObjectAnimator secondAnimator = ObjectAnimator.ofFloat(y, "rotation",0f, 7200f);
        secondAnimator.setDuration(6000);

        ObjectAnimator thirdAnimator = ObjectAnimator.ofFloat(z, "rotation",0f, 7200f);
        thirdAnimator.setDuration(6000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(firstAnimator, secondAnimator, thirdAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                btnSpin.setEnabled(false);
                imageBetList.add(imageList.get((int)(imageList.size() * Math.random())));
                imageBetList.add(imageList.get((int)(imageList.size() * Math.random())));
                imageBetList.add(imageList.get((int)(imageList.size() * Math.random())));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                x.setImageResource(imageBetList.get(0));
                y.setImageResource(imageBetList.get(1));
                z.setImageResource(imageBetList.get(2));
                checkResult();
            }
        });

        animatorSet.start();

    }

    /**
     * Method checks the result from slots and sets the message
     */

    private void checkResult() {

        btnSpin.setEnabled(true);

        if (imageBetList.get(0).equals(imageBetList.get(1))
                && imageBetList.get(1).equals(imageBetList.get(2))){

            int modifier = getPrizeModifier(imageBetList.get(0));
            int prize = Integer.parseInt(String.valueOf(etBet.getText())) * modifier;
            etBet.setText("");

            new MaterialAlertDialogBuilder(getContext())
                    .setMessage(getString(R.string.jackpot, prize))
                    .setPositiveButton(getString(R.string.you_won), (dialogInterface, i) -> {
                        mBalance += prize;
                        tvBalance.setText(getString(R.string.set_start_value, mBalance));
                    })
                    .show();

        } else if (imageBetList.get(0).equals(imageBetList.get(1)) || imageBetList.get(1)
                .equals(imageBetList.get(2)) || imageBetList.get(0).equals(imageBetList.get(2))){

            mBalance += Integer.parseInt(String.valueOf(etBet.getText()));
            tvBalance.setText(getString(R.string.set_start_value, mBalance));
            etBet.setText("");
            Toast.makeText(getContext(), getString(R.string.you_won), Toast.LENGTH_LONG).show();

        } else {

            mBalance -= Integer.parseInt(String.valueOf(etBet.getText()));

            if (mBalance >= 5) {
                tvBalance.setText(getString(R.string.set_start_value, mBalance));
                etBet.setText("");
                Toast.makeText(getContext(), getString(R.string.you_lose), Toast.LENGTH_LONG).show();
            } else {

                tvBalance.setText(getString(R.string.set_start_value, mBalance));
                etBet.setText("");

                new MaterialAlertDialogBuilder(getContext())
                        .setMessage(getString(R.string.msg_try_again))
                        .setPositiveButton(getString(R.string.str_try_again), (dialogInterface, i) -> {
                            mBalance = 500;
                            tvBalance.setText(getString(R.string.set_start_value, mBalance));
                        })
                        .show();

            }

        }
    }

    /**
     * Method helps to get prize modifier
     * @param integer - id of image
     * @return the modifier of prize
     */

    private int getPrizeModifier(Integer integer){

        int x = 1;

        if (integer == R.drawable.one) {
            x = 3;
        } else if (integer == R.drawable.two) {
            x = 4;
        } else if (integer == R.drawable.three) {
            x = 5;
        } else if (integer == R.drawable.four) {
            x = 6;
        } else if (integer == R.drawable.five) {
            x = 7;
        } else if (integer == R.drawable.six) {
            x = 8;
        } else if (integer == R.drawable.seven) {
            x = 9;
        } else if (integer == R.drawable.eight) {
            x = 10;
        }

        return x;

    }

    /**
     * Save the balance
     */

    private void saveBalance(){
        SharedPreferences getPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor e = getPreferences.edit();
        e.putInt(Constants.TAG, mBalance);
        e.apply();
    }

    /**
     * Restore the balance
     */

    private void restoreBalance(){
        SharedPreferences getPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int sharedBalance = getPreferences.getInt(Constants.TAG, 0);
        if (sharedBalance != 0){
            mBalance = sharedBalance;
        } else {
            mBalance = 2000;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        saveBalance();
    }
}
