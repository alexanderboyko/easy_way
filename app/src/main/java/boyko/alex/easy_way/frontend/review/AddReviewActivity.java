package boyko.alex.easy_way.frontend.review;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 29.12.2017.
 */

public class AddReviewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AppCompatImageView star1, star2, star3, star4, star5;
    private TextInputEditText editText;
    private Button button;

    private String itemId, ownerId;
    private Booking booking;

    private int mark = 1;
    private boolean reviewUploading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_add_review);

        if (savedInstanceState == null) {
            itemId = getIntent().getStringExtra("itemId");
            ownerId = getIntent().getStringExtra("ownerId");
            booking = Parcels.unwrap(getIntent().getParcelableExtra("booking"));
        } else {
            itemId = savedInstanceState.getString("itemId");
            ownerId = savedInstanceState.getString("ownerId");
            booking = Parcels.unwrap(savedInstanceState.getParcelable("booking"));
            mark = savedInstanceState.getInt("mark");

        }

        init();
        setMark();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("itemId", itemId);
        outState.putString("ownerId", ownerId);
        outState.putParcelable("booking", Parcels.wrap(booking));
        outState.putInt("mark", mark);
        super.onSaveInstanceState(outState);
    }

    private void init() {
        initViews();
        initToolbar();
        initStars();
        initButton();
    }

    private void initViews() {
        toolbar = findViewById(R.id.review_toolbar);
        star1 = findViewById(R.id.review_star1);
        star2 = findViewById(R.id.review_star2);
        star3 = findViewById(R.id.review_star3);
        star4 = findViewById(R.id.review_star4);
        star5 = findViewById(R.id.review_star5);
        editText = findViewById(R.id.review_edit_text);
        button = findViewById(R.id.review_save);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.new_offer));
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initStars() {
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mark = 1;
                setMark();
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mark = 2;
                setMark();
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mark = 3;
                setMark();
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mark = 4;
                setMark();
            }
        });

        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mark = 5;
                setMark();
            }
        });
    }

    private void initButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty()) {
                    editText.setError(getString(R.string.error_empty));
                } else {
                    if (!reviewUploading) addReview();
                }
            }
        });
    }

    private void addReview() {
        reviewUploading = true;
        Review review = new Review();
        review.createdAt = DateHelper.getCurrentTime();
        review.text = editText.getText().toString();
        review.mark = mark;
        review.userPhoto = DataMediator.getUser().photo;
        review.bookingId = booking.id;
        review.itemId = itemId;
        review.ownerId = ownerId;
        review.userId = DataMediator.getUser().id;
        review.userName = DataMediator.getUser().getFullName();

        FirebaseFirestore.getInstance().collection("reviews").add(review).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    setResult(RequestCodes.RESULT_CODE_OK);
                    finish();
                } else {
                    Toast.makeText(AddReviewActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setMark() {
        switch (mark) {
            case 1:
                star1.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star2.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.primary_text), PorterDuff.Mode.SRC_ATOP);
                star3.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.primary_text), PorterDuff.Mode.SRC_ATOP);
                star4.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.primary_text), PorterDuff.Mode.SRC_ATOP);
                star5.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.primary_text), PorterDuff.Mode.SRC_ATOP);
                return;
            case 2:
                star1.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star2.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star3.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.primary_text), PorterDuff.Mode.SRC_ATOP);
                star4.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.primary_text), PorterDuff.Mode.SRC_ATOP);
                star5.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.primary_text), PorterDuff.Mode.SRC_ATOP);
                return;
            case 3:
                star1.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star2.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star3.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star4.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.primary_text), PorterDuff.Mode.SRC_ATOP);
                star5.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.primary_text), PorterDuff.Mode.SRC_ATOP);
                return;
            case 4:
                star1.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star2.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star3.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star4.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star5.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.primary_text), PorterDuff.Mode.SRC_ATOP);
                return;
            case 5:
                star1.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star2.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star3.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star4.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
                star5.setColorFilter(ContextCompat.getColor(AddReviewActivity.this, R.color.color_accent), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
