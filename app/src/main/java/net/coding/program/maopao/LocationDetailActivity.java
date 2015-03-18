package net.coding.program.maopao;

import android.view.View;
import android.widget.TextView;

import net.coding.program.BaseActivity;
import net.coding.program.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Neutra on 2015/3/14.
 */
@EActivity(R.layout.activity_location_detail)
public class LocationDetailActivity extends BaseActivity {
    @ViewById
    TextView nameText, addressText;
    @ViewById
    View map_button, customText;
    @Extra
    double latitude, longitude;
    @Extra
    String name, address;
    @Extra
    boolean isCustom;

    @AfterViews
    void afterViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(name == null) name = "";
        name = name.replaceFirst(".*·","");
        nameText.setText(name);
        addressText.setText(address);
        customText.setVisibility(isCustom? View.VISIBLE:View.GONE);
        if (address != null) {
            map_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocationMapActivity_.intent(LocationDetailActivity.this)
                            .latitude(latitude).longitude(longitude)
                            .name(name).address(address).start();
                }
            });
        }
    }

    @OptionsItem(android.R.id.home)
    void close() {
        onBackPressed();
    }
}
