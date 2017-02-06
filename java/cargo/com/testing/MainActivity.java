package cargo.com.testing;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.btn_booking_next)
    Button Next;
    @Bind(R.id.btn_booking_save)
    Button Save;
    @Bind(R.id.cardview_imagebtn_arrow)
    ImageView arrow;
    @Bind(R.id.homescreen)
    FrameLayout Homescreen;
    @Bind(R.id.finalbooking)
    LinearLayout Bookingfinal;
    @Bind(R.id.cards_activity)
    LinearLayout cardsview;
    @Bind(R.id.onclick_card_view)
    LinearLayout onclikview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_button);
        ButterKnife.bind(this);
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Viewer();
            }
        });
        Save.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Homescreen.setVisibility(View.GONE);
             Bookingfinal.setVisibility(View.GONE);
                onclikview.setVisibility(View.GONE);
             cardsview.setVisibility(View.VISIBLE);
            }
        });
        arrow.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Homescreen.setVisibility(View.GONE);
                Bookingfinal.setVisibility(View.GONE);
                onclikview.setVisibility(View.VISIBLE);
                cardsview.setVisibility(View.GONE);
            }
        });
    }

    public void Viewer(){
        Homescreen.setVisibility(View.GONE);
        Bookingfinal.setVisibility(View.VISIBLE);
        onclikview.setVisibility(View.GONE);
        cardsview.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Homescreen.setVisibility(View.VISIBLE);
        onclikview.setVisibility(View.GONE);
        Bookingfinal.setVisibility(View.GONE);
        cardsview.setVisibility(View.GONE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected

        }

        return true;
    }
}
