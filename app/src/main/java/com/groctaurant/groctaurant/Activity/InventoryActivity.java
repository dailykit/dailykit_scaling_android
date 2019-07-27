package com.groctaurant.groctaurant.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.groctaurant.groctaurant.R;

public class InventoryActivity extends AppCompatActivity {

    LinearLayout realTimeTab, planningTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        realTimeTab = (LinearLayout) findViewById(R.id.real_time_tab);
        planningTab = (LinearLayout) findViewById(R.id.planning_tab);

        realTimeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRealTime = new Intent(InventoryActivity.this, IngredientActivity.class);
                startActivity(intentRealTime);
                finish();
            }
        });

        planningTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPlanning = new Intent(InventoryActivity.this, PlanningActivity.class);
                startActivity(intentPlanning);
                finish();
            }
        });
    }
}
