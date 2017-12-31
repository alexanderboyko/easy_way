package boyko.alex.easy_way.frontend.item.item_details.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Dialog;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Message;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 24.12.2017.
 * <p>
 * This is activity where user can send first message to other user from details item activity
 */

public class ContactActivity extends AppCompatActivity {
    private final int PRICE_TYPE_DAY = 1, PRICE_TYPE_HOUR = 2;

    private Toolbar toolbar;
    private TextInputLayout dateToLayout;
    private TextInputEditText dateFromEditText, dateToEditText, message;
    private TextView price;
    private Button buttonSend;
    private ProgressBar progressBar;

    private ArrayList<Booking> bookings;
    private Item item;
    private User itemOwner;

    private Booking createdBooking = null;

    private long dateFrom, dateTo;
    private boolean isSelectDateFrom = false;

    private boolean messageError = true, datesError = false;
    private int priceType;

    private String createdDialogId, createdBookingId, createdMessageId;
    private boolean downloadingError = false;

    private boolean dialogCheckLoadingFinished1 = false, dialogCheckLoadingFinished2 = false, isDialogExists = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_contact);

        if (savedInstanceState == null) {
            bookings = Parcels.unwrap(getIntent().getParcelableExtra("bookings"));
            item = Parcels.unwrap(getIntent().getParcelableExtra("item"));
            itemOwner = Parcels.unwrap(getIntent().getParcelableExtra("itemOwner"));

            PriceType priceType = DataMediator.getPriceType(item.priceTypeId);
            if (priceType != null && priceType.shortName.equals("/day"))
                this.priceType = PRICE_TYPE_DAY;
            else this.priceType = PRICE_TYPE_HOUR;

            if (this.priceType == PRICE_TYPE_DAY) {
                dateFrom = DateHelper.getNextDayTime(DateHelper.getCurrentTime());
                dateTo = DateHelper.getNextDayTime(dateFrom);
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(DateHelper.getNextDayTime(DateHelper.getCurrentTime()));
                calendar.set(Calendar.HOUR_OF_DAY, 8);
                calendar.set(Calendar.MINUTE, 0);
                dateFrom = calendar.getTimeInMillis();

                calendar.set(Calendar.HOUR_OF_DAY, 16);
                calendar.set(Calendar.MINUTE, 0);
                dateTo = calendar.getTimeInMillis();
            }

            initStartDates();
        } else {
            bookings = Parcels.unwrap(savedInstanceState.getParcelable("bookings"));
            item = Parcels.unwrap(savedInstanceState.getParcelable("item"));
            itemOwner = Parcels.unwrap(savedInstanceState.getParcelable("itemOwner"));
            dateFrom = savedInstanceState.getLong("dateFrom");
            dateTo = savedInstanceState.getLong("dateTo");
            isSelectDateFrom = savedInstanceState.getBoolean("isSelectDateFrom");
            messageError = savedInstanceState.getBoolean("messageError");
            datesError = savedInstanceState.getBoolean("datesError");
            priceType = savedInstanceState.getInt("priceType");

            if (messageError || datesError) setSaveButtonEnabled(false);
            else setSaveButtonEnabled(true);
        }

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("bookings", Parcels.wrap(bookings));
        outState.putParcelable("item", Parcels.wrap(item));
        outState.putParcelable("itemOwner", Parcels.wrap(itemOwner));
        outState.putLong("dateFrom", dateFrom);
        outState.putLong("dateTo", dateTo);
        outState.putBoolean("isSelectDateFrom", isSelectDateFrom);
        outState.putBoolean("messageError", messageError);
        outState.putBoolean("datesError", datesError);
        outState.putInt("priceType", priceType);
        super.onSaveInstanceState(outState);
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

    private void init() {
        initViews();
        initToolbar();
        initInputs();
    }

    private void initViews() {
        toolbar = findViewById(R.id.first_toolbar);
        dateFromEditText = findViewById(R.id.first_date_from);
        dateToEditText = findViewById(R.id.first_date_to);
        message = findViewById(R.id.first_message);
        buttonSend = findViewById(R.id.first_send);
        dateToLayout = findViewById(R.id.first_date_to_layout);
        progressBar = findViewById(R.id.first_progressbar);
        price = findViewById(R.id.first_price);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(item.title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initInputs() {
        if (priceType == PRICE_TYPE_DAY) {
            dateFromEditText.setText(DateHelper.getSmartFormattedDate(dateFrom));
            dateToEditText.setText(DateHelper.getSmartFormattedDate(dateTo));
        } else {
            dateFromEditText.setText(DateHelper.getSmartFormattedDateWithTime(dateFrom));
            dateToEditText.setText(DateHelper.getSmartFormattedDateWithTime(dateTo));
        }

        dateFromEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelectDateFrom = true;
                showDatePicker();
            }
        });

        dateToEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelectDateFrom = false;
                if (priceType == PRICE_TYPE_DAY) {
                    showDatePicker();
                } else {
                    showTimePicker(dateFrom);
                }
            }
        });

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    messageError = true;
                    setSaveButtonEnabled(false);
                } else {
                    messageError = false;
                    if (!datesError) {
                        setSaveButtonEnabled(true);
                    }
                }
            }
        });

        String priceFormatted = getBookingPrice() + " zł";
        price.setText(priceFormatted);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        if (messageError || datesError) setSaveButtonEnabled(false);
        else setSaveButtonEnabled(true);
    }

    private void initStartDates() {
        if (priceType == PRICE_TYPE_DAY) {
            Calendar[] disabledDays = getDisabledDays();
            Calendar calendarFrom = Calendar.getInstance();
            Calendar calendarTo = Calendar.getInstance();

            boolean settingDates = true;
            while (settingDates) {
                calendarFrom.setTimeInMillis(dateFrom);
                calendarTo.setTimeInMillis(dateTo);

                boolean dateFromCanSet = true;
                boolean dateToCanSet = true;
                //check if dateFrom and dateTo dates is free
                for (Calendar disabledDay : disabledDays) {
                    if (DateHelper.ifTimesFromOneDay(calendarFrom.getTimeInMillis(), disabledDay.getTimeInMillis())) {
                        dateFromCanSet = false;
                        break;
                    }
                    if (DateHelper.ifTimesFromOneDay(calendarTo.getTimeInMillis(), disabledDay.getTimeInMillis())) {
                        dateToCanSet = false;
                        break;
                    }
                }

                if (dateFromCanSet && dateToCanSet) {
                    //dates are free
                    settingDates = false;
                } else {
                    dateFrom = DateHelper.getNextDayTime(dateFrom);
                    dateTo = DateHelper.getNextDayTime(dateFrom);
                }
            }
        } else {
            boolean settingDates = true;
            while (settingDates) {
                Calendar calendarFrom = Calendar.getInstance();
                Calendar calendarTo = Calendar.getInstance();
                calendarFrom.setTimeInMillis(dateFrom);
                calendarTo.setTimeInMillis(dateTo);

                Timepoint[] disabledHours = getDisabledTimes(calendarFrom.getTimeInMillis());

                boolean datesCanSet = true;
                //check if dateFrom and dateTo dates is free
                for (Timepoint timepoint : disabledHours) {
                    if (timepoint.getHour() >= calendarFrom.get(Calendar.HOUR_OF_DAY)
                            && timepoint.getHour() <= calendarTo.get(Calendar.HOUR_OF_DAY)) {
                        datesCanSet = false;
                        break;
                    }
                }

                if (datesCanSet) {
                    //dates are free
                    settingDates = false;
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(DateHelper.getNextDayTime(dateFrom));
                    calendar.set(Calendar.HOUR_OF_DAY, 8);
                    calendar.set(Calendar.MINUTE, 0);
                    dateFrom = calendar.getTimeInMillis();

                    calendar.set(Calendar.HOUR_OF_DAY, 16);
                    calendar.set(Calendar.MINUTE, 0);
                    dateTo = calendar.getTimeInMillis();
                }
            }
        }
    }

    private void setSaveButtonEnabled(boolean isEnable) {
        if (isEnable) {
            buttonSend.setTextColor(ContextCompat.getColor(this, R.color.color_accent));
        } else {
            buttonSend.setTextColor(ContextCompat.getColor(this, R.color.grey_light));
        }
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(isSelectDateFrom ? dateFrom : dateTo);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(year, monthOfYear, dayOfMonth);

                        if (priceType == PRICE_TYPE_HOUR) {
                            showTimePicker(calendar1.getTimeInMillis());
                        } else {
                            ContactActivity.this.onDateSet(calendar1.getTimeInMillis(), false);
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        if (isSelectDateFrom) {
            datePickerDialog.setMinDate(Calendar.getInstance());
        } else {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(dateFrom);
            datePickerDialog.setMinDate(calendar1);
        }
        if (priceType == PRICE_TYPE_DAY) datePickerDialog.setDisabledDays(getDisabledDays());
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.show(getFragmentManager(), "datePicker");
    }

    private void showTimePicker(final long dateTimeSelected) {
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dateTimeSelected);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                onDateSet(calendar.getTimeInMillis(), true);
            }
        }, true);
        if (!isSelectDateFrom) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateTimeSelected);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            timePickerDialog.setMinTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        }
        timePickerDialog.enableMinutes(false);
        timePickerDialog.enableSeconds(false);
        timePickerDialog.setDisabledTimes(getDisabledTimes(dateTimeSelected));
        timePickerDialog.dismissOnPause(true);
        timePickerDialog.show(getFragmentManager(), "timePicker");
    }

    private void onDateSet(long time, boolean withTime) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time);
        if (isSelectDateFrom) {
            dateFrom = calendar1.getTimeInMillis();
            if (withTime)
                dateFromEditText.setText(DateHelper.getSmartFormattedDateWithTime(calendar1.getTimeInMillis()));
            else
                dateFromEditText.setText(DateHelper.getSmartFormattedDate(calendar1.getTimeInMillis()));

            if (priceType == PRICE_TYPE_HOUR) {
                calendar1.setTimeInMillis(dateFrom);
                calendar1.add(Calendar.HOUR_OF_DAY, 1);
                dateTo = calendar1.getTimeInMillis();
                if (withTime)
                    dateToEditText.setText(DateHelper.getSmartFormattedDateWithTime(calendar1.getTimeInMillis()));
                else
                    dateToEditText.setText(DateHelper.getSmartFormattedDate(calendar1.getTimeInMillis()));
            } else {
                calendar1.setTimeInMillis(dateFrom);
                calendar1.add(Calendar.DATE, 1);
                dateTo = calendar1.getTimeInMillis();
                if (withTime)
                    dateToEditText.setText(DateHelper.getSmartFormattedDateWithTime(calendar1.getTimeInMillis()));
                else
                    dateToEditText.setText(DateHelper.getSmartFormattedDate(calendar1.getTimeInMillis()));
            }

        } else {
            dateTo = calendar1.getTimeInMillis();
            if (withTime)
                dateToEditText.setText(DateHelper.getSmartFormattedDateWithTime(calendar1.getTimeInMillis()));
            else
                dateToEditText.setText(DateHelper.getSmartFormattedDate(calendar1.getTimeInMillis()));
        }

        if (dateFrom > dateTo) {
            dateToLayout.setError(getString(R.string.error_date_range));
            datesError = true;
            setSaveButtonEnabled(false);
            return;
        }

        //check date range
        //if (priceType == PRICE_TYPE_DAY) {
        //if price type is days - check if not one day
//            if (DateHelper.ifTimesFromOneDay(dateFrom, dateTo)) {
//                dateToLayout.setError(getString(R.string.error_one_day));
//                datesError = true;
//                setSaveButtonEnabled(false);
//                return;
//            }
        if (priceType == PRICE_TYPE_HOUR) {
            //if hours - check if not one hour
            Calendar calendarStart = Calendar.getInstance();
            Calendar calendarEnd = Calendar.getInstance();
            calendarStart.setTimeInMillis(dateFrom);
            calendarEnd.setTimeInMillis(dateTo);

            //check if from one day
            if (!DateHelper.ifTimesFromOneDay(dateFrom, dateTo)) {
                dateToLayout.setError(getString(R.string.error_different_days));
                datesError = true;
                setSaveButtonEnabled(false);
                return;
            }
            if (calendarStart.get(Calendar.HOUR_OF_DAY) == calendarEnd.get(Calendar.HOUR_OF_DAY)) {
                dateToLayout.setError(getString(R.string.error_one_hour));
                datesError = true;
                setSaveButtonEnabled(false);
                return;
            }
        }


        dateToLayout.setError(null);
        dateToLayout.setError(null);
        datesError = false;

        String priceFormatted = getBookingPrice() + " zł";
        price.setText(priceFormatted);

        if (!messageError) {
            setSaveButtonEnabled(true);
        }
    }

    private Calendar[] getDisabledDays() {
        ArrayList<Calendar> days = new ArrayList<>();
        for (Booking booking : bookings) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(booking.startedAt);
            while (true) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTimeInMillis(calendar.getTimeInMillis());
                if (!DateHelper.ifTimesFromOneDay(calendar1.getTimeInMillis(), booking.endAt)) {
                    days.add(calendar1);
                    calendar.add(Calendar.DATE, 1);
                } else {
                    days.add(calendar1);
                    break;
                }
            }
        }
        return days.toArray(new Calendar[days.size()]);
    }

    private Timepoint[] getDisabledTimes(long forDay) {
        ArrayList<Timepoint> hours = new ArrayList<>();
        for (Booking booking : bookings) {
            if (DateHelper.ifTimesFromOneDay(booking.startedAt, forDay)) {
                Calendar startAt = Calendar.getInstance();
                startAt.setTimeInMillis(booking.startedAt);

                Calendar endAt = Calendar.getInstance();
                endAt.setTimeInMillis(booking.endAt);
                while (true) {
                    hours.add(new Timepoint(startAt.get(Calendar.HOUR_OF_DAY)));
                    startAt.add(Calendar.HOUR_OF_DAY, 1);

                    if (startAt.get(Calendar.HOUR_OF_DAY) == endAt.get(Calendar.HOUR_OF_DAY)) {
                        hours.add(new Timepoint(startAt.get(Calendar.HOUR_OF_DAY)));
                        break;
                    }
                }
            }
        }
        return hours.toArray(new Timepoint[hours.size()]);
    }

    private void sendMessage() {
        if (!messageError && !datesError) {
            progressBar.setVisibility(View.VISIBLE);
            messageError = true;

            getExistingDialogAndStartLoading();
        }
    }

    private double getBookingPrice() {
        if (priceType == PRICE_TYPE_DAY) {
            return DateHelper.getDaysNumberInRange(dateFrom, dateTo) * item.price;
        } else {
            return DateHelper.getHoursNumberInRange(dateFrom, dateTo) * item.price;
        }
    }

    private void getExistingDialogAndStartLoading() {
        FirebaseFirestore.getInstance().collection("dialogs")
                .whereEqualTo("user1Id", DataMediator.getUser().id)
                .whereEqualTo("user2Id", itemOwner.id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty() && !isDialogExists) {
                                isDialogExists = true;
                                Dialog dialog = task.getResult().getDocuments().get(0).toObject(Dialog.class);
                                dialog.id = task.getResult().getDocuments().get(0).getId();
                                createdDialogId = dialog.id;
                                createMessage(dialog);
                                createBooking(dialog.id);
                            } else {
                                //no dialog
                                if (dialogCheckLoadingFinished2 && !isDialogExists) {
                                    createDialog();
                                }
                            }
                        }
                        dialogCheckLoadingFinished1 = true;
                    }
                });

        FirebaseFirestore.getInstance().collection("dialogs")
                .whereEqualTo("user1Id", itemOwner.id)
                .whereEqualTo("user2Id", DataMediator.getUser().id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty() && !isDialogExists) {
                                isDialogExists = true;
                                Dialog dialog = task.getResult().getDocuments().get(0).toObject(Dialog.class);
                                dialog.id = task.getResult().getDocuments().get(0).getId();
                                createdDialogId = dialog.id;
                                createMessage(dialog);
                                createBooking(dialog.id);
                            } else {
                                //no dialog
                                if (dialogCheckLoadingFinished1 && !isDialogExists) {
                                    createDialog();
                                }
                            }
                        }
                        dialogCheckLoadingFinished2 = true;
                    }
                });
    }

    private void createDialog() {
        final Dialog dialog = new Dialog();
        dialog.user1FullName = DataMediator.getUser().getFullName();
        dialog.user1HasUnreadMessage = false;
        dialog.user1Id = DataMediator.getUser().id;
        dialog.user1Photo = DataMediator.getUser().photo;

        dialog.user2FullName = itemOwner.getFullName();
        dialog.user2HasUnreadMessage = true;
        dialog.user2Id = itemOwner.id;
        dialog.user2Photo = itemOwner.photo;
        dialog.lastUpdate = DateHelper.getCurrentTime();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("dialogs")
                .add(dialog)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            dialog.id = task.getResult().getId();
                            createdDialogId = task.getResult().getId();
                            createMessage(dialog);
                            createBooking(dialog.id);
                        } else {
                            downloadingError = true;
                            checkIfDownloaded();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        downloadingError = true;
                        checkIfDownloaded();
                        Toast.makeText(ContactActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createBooking(String dialogId) {
        final Booking booking = new Booking();
        booking.ownerId = itemOwner.id;
        booking.clientId = DataMediator.getUser().id;
        booking.itemId = item.id;
        booking.dialogId = dialogId;
        booking.priceTypeId = item.priceTypeId;
        booking.price = getBookingPrice();
        booking.createdAt = DateHelper.getCurrentTime();
        booking.updatedAt = DateHelper.getCurrentTime();
        booking.startedAt = dateFrom;
        booking.endAt = dateTo;
        booking.isCanceled = false;
        booking.isFinished = false;
        booking.isStarted = false;
        booking.isConfirmed = false;
        booking.isReviewAdded = false;
        booking.itemTitle = item.title;
        booking.itemPhoto = item.mainPhoto;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookings")
                .add(booking)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            createdBookingId = task.getResult().getId();
                            createdBooking = booking;
                        } else {
                            downloadingError = true;
                        }
                        checkIfDownloaded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        downloadingError = true;
                        checkIfDownloaded();
                        Toast.makeText(ContactActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createMessage(Dialog dialog) {
        final Message message = new Message();
        message.id_dialog = dialog.id;
        message.senderId = DataMediator.getUser().id;
        message.sentAt = Calendar.getInstance().getTimeInMillis();
        message.text = this.message.getText().toString() + "\nPlease check booking in section Bookings.";
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("messages")
                .add(message)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            createdMessageId = task.getResult().getId();
                            message.id = createdMessageId;
                            db.collection("dialogs").document(createdDialogId).update("lastMessage", message.text);
                        } else {
                            downloadingError = true;
                        }
                        checkIfDownloaded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ContactActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        downloadingError = true;
                        checkIfDownloaded();
                    }
                });
    }

    private void checkIfDownloaded() {
        if (downloadingError) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (createdMessageId != null)
                db.collection("messages").document(createdMessageId).delete();
            if (createdBookingId != null)
                db.collection("bookings").document(createdBookingId).delete();
            if (createdDialogId != null)
                db.collection("dialogs").document(createdDialogId).delete();
            downloadingError = false;

            progressBar.setVisibility(View.GONE);
            messageError = false;
        } else {
            if (createdMessageId != null && createdBookingId != null && createdDialogId != null) {
                //downloaded
                progressBar.setVisibility(View.GONE);
                messageError = false;

                Intent intent = new Intent();
                intent.putExtra("booking", Parcels.wrap(createdBooking));

                setResult(RequestCodes.RESULT_CODE_OK, intent);
                finish();
            }
        }
    }
}
