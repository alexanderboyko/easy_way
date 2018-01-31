package boyko.alex.easy_way.frontend.item.item_details;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Like;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.frontend.custom_views.AvailabilityCalendar;
import boyko.alex.easy_way.frontend.explore.ItemsRecyclerAdapter;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 11.11.2017.
 *
 * Item details logic here
 */

class ItemDetailsPresenter {
    //private final String LOG_TAG = getClass().getSimpleName();
    private ItemDetailsViewActivity view;
    private static ItemDetailsPresenter presenter;

    private int previousPage;
    private long previousDate;

    private boolean likeLoading = false;

    private boolean isDataLoaded = false;

    private ItemDetailsPresenter(ItemDetailsViewActivity view) {
        this.view = view;
    }

    public static ItemDetailsPresenter getInstance(ItemDetailsViewActivity view) {
        if (presenter == null) {
            presenter = new ItemDetailsPresenter(view);
        } else {
            presenter.view = view;
        }
        return presenter;
    }

    void onCreate(@Nullable Bundle savedInstanceState){
        if(savedInstanceState == null) {
            isDataLoaded = false;
            Item item = Parcels.unwrap(view.getIntent().getParcelableExtra("item"));
            if (item != null) {
                setItem(item);
                startLoading(item);
            } else {
                String id = view.getIntent().getStringExtra("itemId");
                if (id != null) startLoading(id);
            }
        }else{
            Item item = Parcels.unwrap(savedInstanceState.getParcelable("item"));
            ArrayList<Object> similarItems = Parcels.unwrap(savedInstanceState.getParcelable("similarItems"));
            ArrayList<Booking> bookings = Parcels.unwrap(savedInstanceState.getParcelable("bookings"));
            User owner = Parcels.unwrap(savedInstanceState.getParcelable("owner"));
            ArrayList<Review> reviews = Parcels.unwrap(savedInstanceState.getParcelable("reviews"));

            if(isDataLoaded){
                setItem(item);
                initItemData(item);
                setSimilarObjectItems(similarItems);
                setBookings(bookings);
                setOwner(owner);
                setReviews(reviews);
                loadingFinished();
            }else{
                Item item1 = Parcels.unwrap(view.getIntent().getParcelableExtra("item"));
                if (item1 != null) {
                    setItem(item1);
                    startLoading(item1);
                } else {
                    String id = view.getIntent().getStringExtra("itemId");
                    if (id != null) startLoading(id);
                }
            }
        }
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RequestCodes.REQUEST_CODE_EDIT){
            if(resultCode == RequestCodes.RESULT_CODE_OK){
                if(data != null){
                    Booking booking = Parcels.unwrap(data.getParcelableExtra("booking"));
                    if(booking != null) {
                        view.addBooking(booking);
                        ArrayList<Booking> bookings = new ArrayList<>();
                        bookings.add(booking);
                        view.addEvents(ConvertHelper.convertBookingsToEvents(bookings));
                    }
                }
            }
        }
        if(requestCode == RequestCodes.REQUEST_CODE_DETAILS){
            updateLikes();
        }
    }

    private void startLoading(String itemId){
        view.setLoading(true);
        ItemDetailsModel.getInstance(this).startLoading(itemId);
    }

    private void startLoading(Item item) {
        view.setLoading(true);
        ItemDetailsModel.getInstance(this).startLoading(item);
        initItemData(item);
    }

    private void initItemData(Item item){
        if (item != null) {


            if (item.mainPhoto != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(item.mainPhoto);
                if (item.photos != null && !item.photos.isEmpty()) photos.addAll(item.photos);
                view.setItemPhoto(photos);
            }

            view.setTitle(item.title);

            //set price
            String priceFormatted = String.format(Locale.getDefault(), "%.2f", item.price) + " zł";
            PriceType priceType = DataMediator.getPriceType(item.priceTypeId);
            if (priceType != null && priceType.name != null) {
                priceFormatted += " " + priceType.name;
            }
            view.setPrice(priceFormatted);

            //rating
            if (item.ratingAverage == 0) {
                view.setRating(ApplicationController.getInstance().getString(R.string.no_rating));
            } else {
                String rating = ApplicationController.getInstance().getString(R.string.rating) + ": " + item.ratingAverage;
                view.setRating(rating);
            }

            if (item.description == null || item.description.isEmpty()) {
                view.setDescription(ApplicationController.getInstance().getString(R.string.no_info));
            } else {
                view.setDescription(item.description);
            }

            if (item.notes == null || item.notes.isEmpty()) {
                view.setNotes(ApplicationController.getInstance().getString(R.string.no_info));
            } else {
                view.setNotes(item.notes);
            }

            if (item.address != null && item.address.name != null)
                view.setLocation(item.address.name);

            view.setMonthName(DateHelper.getFormattedCalendarMonthName(DateHelper.getCurrentTime()));

            if (DataMediator.getLike(item.id) != null) {
                view.setLikeIcon(true);
            }

            if (item.ownerId.equals(DataMediator.getUser().id))
                view.setContactButtonVisibility(View.GONE);
        }
    }

    void loadingFinished() {
        isDataLoaded = true;
        view.setLoading(false);
    }

    void onSimilarItemLikeClicked(ItemsRecyclerAdapter adapter, int position, Item item){
        final Like like = DataMediator.getLike(((Item) adapter.getItems().get(position)).id);
        if (like != null) {
            //item has been liked
            FirebaseFirestore.getInstance().collection("likes").document(like.id).delete();
            DataMediator.removeLike(like.id);
        }else{
            final Like likeNew = new Like();
            likeNew.itemId = item.id;
            likeNew.userId = DataMediator.getUser().id;

            FirebaseFirestore.getInstance().collection("likes").add(likeNew).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        likeNew.id = task.getResult().getId();
                        DataMediator.getLikes().add(likeNew);
                    }
                }
            });
        }

        ((Item) adapter.getItems().get(position)).isLiked = !((Item) adapter.getItems().get(position)).isLiked;
        adapter.notifyItemChanged(position);
    }

    void onContactClicked(){
        view.launchFirstContactActivity();
    }

    void onMonthChange(AvailabilityCalendar calendarView, long firstDayOfNewMonth) {
        int monthSelected = DateHelper.whichMonthSelectedCompareToCurrent(firstDayOfNewMonth);
        int page;

        //disable swipe in calendar if not center
        //if something goes wrong with swiping - set today
        if(monthSelected == -1){
            calendarView.setCurrentDate(new Date(previousDate));
            calendarView.setPage(previousPage);
            view.setMonthName(DateHelper.getFormattedCalendarMonthName(previousDate));
            if(previousPage == AvailabilityCalendar.PAGE_LEFT){
                view.setCalendarButtonSwipeMonthLeftVisibility(View.INVISIBLE);
                view.setCalendarButtonSwipeMonthRightVisibility(View.VISIBLE);
            }
            if(previousPage == AvailabilityCalendar.PAGE_RIGHT){
                view.setCalendarButtonSwipeMonthLeftVisibility(View.VISIBLE);
                view.setCalendarButtonSwipeMonthRightVisibility(View.INVISIBLE);
            }
            if(previousPage == AvailabilityCalendar.PAGE_CENTER){
                view.setCalendarButtonSwipeMonthLeftVisibility(View.VISIBLE);
                view.setCalendarButtonSwipeMonthRightVisibility(View.VISIBLE);
            }
        }else {

            if (monthSelected == 0) {
                page = AvailabilityCalendar.PAGE_LEFT;
                view.setCalendarButtonSwipeMonthLeftVisibility(View.INVISIBLE);
                view.setCalendarButtonSwipeMonthRightVisibility(View.VISIBLE);
            } else {
                if (monthSelected == 1) {
                    page = AvailabilityCalendar.PAGE_CENTER;
                    view.setCalendarButtonSwipeMonthLeftVisibility(View.VISIBLE);
                    view.setCalendarButtonSwipeMonthRightVisibility(View.VISIBLE);
                } else {
                    page = AvailabilityCalendar.PAGE_RIGHT;
                    view.setCalendarButtonSwipeMonthLeftVisibility(View.VISIBLE);
                    view.setCalendarButtonSwipeMonthRightVisibility(View.INVISIBLE);
                }
            }
            calendarView.setPage(page);

            //change month
            view.setMonthName(DateHelper.getFormattedCalendarMonthName(firstDayOfNewMonth));
        }

        previousPage = calendarView.getPage();
        previousDate = calendarView.getFirstDayOfCurrentMonth().getTime();
    }

    void onMonthChangeButtonLeftClicked(AvailabilityCalendar calendarView, long currentMonthDate) {
        Date date = new Date();
        date.setTime(DateHelper.getFirstDayOfPreviousMonth(currentMonthDate));
        calendarView.setCurrentDate(date);
        onMonthChange(calendarView, calendarView.getFirstDayOfCurrentMonth().getTime());
    }

    void onMonthChangeButtonRightClicked(AvailabilityCalendar calendarView, long currentMonthDate) {
        Date date = new Date();
        date.setTime(DateHelper.getFirstDayOfNextMonth(currentMonthDate));
        calendarView.setCurrentDate(date);
        onMonthChange(calendarView, calendarView.getFirstDayOfCurrentMonth().getTime());
    }

    void onCalendarDayClicked(long dayClickedTime, boolean noBookings, ArrayList<Booking> bookings) {
        view.openBottomSheet();
        view.setBottomSheetDate(DateHelper.getSmartFormattedDate(dayClickedTime));
        if(noBookings) {
            view.setBottomSheetEmptyMessageVisibility(View.VISIBLE);
            view.setBottomSheetRecyclerVisibility(View.GONE);
        }else{
            view.setBottomSheetBookings(ConvertHelper.getBookingsForDate(bookings, dayClickedTime));
            view.setBottomSheetEmptyMessageVisibility(View.GONE);
            view.setBottomSheetRecyclerVisibility(View.VISIBLE);
        }
    }

    void onBottomSheetStateChanged(int newState, View shadow) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            shadow.setVisibility(View.GONE);
        }
    }

    void onBottomSheetSlide(float slideOffset, View shadow) {
        shadow.setVisibility(View.VISIBLE);
        shadow.setAlpha(slideOffset);
    }

    void onLikeClicked(Item item){
        if(!likeLoading) {
            likeLoading = true;
            Like like = DataMediator.getLike(item.id);
            if (like == null) {
                ItemDetailsModel.getInstance(this).addLike(item);
            } else {
                ItemDetailsModel.getInstance(this).removeLike(like);
            }
        }
    }

    void likeAdded(){
        view.setLikeIcon(true);
        likeLoading = false;
    }

    void likeRemoved(){
        view.setLikeIcon(false);
        likeLoading = false;
    }

    void likeGetError(){
        view.showError();
        likeLoading = false;
    }

    void setItem(Item item){
        view.setItem(item);
        initItemData(item);
    }

    void setOwner(User owner) {
        view.setOwner(owner);
    }

    void setBookings(ArrayList<Booking> bookings) {
        view.setBookings(bookings);
        view.setEvents(ConvertHelper.convertBookingsToEvents(bookings));
    }

    void setReviews(ArrayList<Review> reviews) {
        view.setReviews(reviews);
        if(reviews != null && !reviews.isEmpty()){
            view.setReviewsEmptyMessageVisibility(View.GONE);
            view.setReviewsListVisibility(View.VISIBLE);
        }else{
            view.setReviewsEmptyMessageVisibility(View.VISIBLE);
            view.setReviewsListVisibility(View.GONE);
        }
    }

    void setAddress(@NonNull Address address){
        view.setAddress(address.fullName);
    }

    void setSimilarItems(ArrayList<Item> similarItems) {
        view.setSimilarItems(similarItems);
        if(similarItems != null && !similarItems.isEmpty()){
            view.setSimilarItemsEmptyMessageVisibility(View.GONE);
            view.setSimilarItemsRecyclerVisibility(View.VISIBLE);
        }else{
            view.setSimilarItemsEmptyMessageVisibility(View.VISIBLE);
            view.setSimilarItemsRecyclerVisibility(View.GONE);
        }
    }

    private void setSimilarObjectItems(ArrayList<Object> similarItems) {
        view.setSimilarObjectItems(similarItems);
        if(similarItems != null && !similarItems.isEmpty()){
            view.setSimilarItemsEmptyMessageVisibility(View.GONE);
            view.setSimilarItemsRecyclerVisibility(View.VISIBLE);
        }else{
            view.setSimilarItemsEmptyMessageVisibility(View.VISIBLE);
            view.setSimilarItemsRecyclerVisibility(View.GONE);
        }
    }

    private void updateLikes(){
        Item item = view.getItem();
        Like like = null;
        if(item != null && item.id != null) like = DataMediator.getLike(item.id);

        if(like == null){
            view.setLikeIcon(false);
        }else{
            view.setLikeIcon(true);
        }

        ItemsRecyclerAdapter adapter = view.getSimilarItemsAdapter();
        if(adapter != null && adapter.getItems() != null && !adapter.getItems().isEmpty()){
            for(int i =0;i<adapter.getItemCount();i++){
                Like like1 = DataMediator.getLike(((Item)adapter.getItems().get(i)).id);
                ((Item) adapter.getItems().get(i)).isLiked = (like1 != null);
                adapter.notifyItemChanged(i);
            }
        }
    }
}
