package boyko.alex.easy_way.frontend.item.item_details;

import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Like;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.frontend.custom_views.AvailabilityCalendar;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 11.11.2017.
 */

class ItemDetailsPresenter {
    private final String LOG_TAG = getClass().getSimpleName();
    private ItemDetailsViewActivity view;
    private static ItemDetailsPresenter presenter;

    private int previousPage;
    private long previousDate;

    private boolean likeLoading = false;

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

    void startLoading(Item item) {
        view.setLoading(true);
        ItemDetailsModel.getInstance(this).startLoading(item);

        if (item.mainPhoto != null) {
            view.setItemPhoto(item.mainPhoto);
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

        view.setDescription(item.description);

        if (item.notes != null) view.setNotes(item.notes);
        if (item.address != null && item.address.name != null) view.setLocation(item.address.name);

        view.setMonthName(DateHelper.getFormattedCalendarMonthName(DateHelper.getCurrentTime()));

        if(DataMediator.getLike(item.id) != null){
            view.setLikeIcon(true);
        }
    }

    void loadingFinished() {
        view.setLoading(false);
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

}
