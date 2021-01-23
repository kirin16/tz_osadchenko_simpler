package artem.com.tz_osadchenko_simpler.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import artem.com.tz_osadchenko_simpler.R;
import artem.com.tz_osadchenko_simpler.ui.fragment.SlotMachineFragment;

public class MainActivity extends AppCompatActivity {

    private SlotMachineFragment mSlotMachineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlotMachineFragment = new SlotMachineFragment();

        showSlotMachine();

    }

    public void showSlotMachine() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_main, mSlotMachineFragment, SlotMachineFragment.class.getSimpleName());
        ft.commit();
    }

}