package boyko.alex.easy_way.frontend.item.item_details;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.backend.models.User;

/**
 * Created by Sasha on 11.11.2017.
 */

class ItemDetailsPresenter {
    private ItemDetailsViewActivity view;
    private static ItemDetailsPresenter presenter;

    private ItemDetailsPresenter(ItemDetailsViewActivity view){
        this.view = view;
    }

    public static ItemDetailsPresenter getInstance(ItemDetailsViewActivity view){
        if(presenter == null){
            presenter = new ItemDetailsPresenter(view);
        }else{
            presenter.view = view;
        }
        return presenter;
    }

    void startLoading(Item item){
        view.setLoading(true);
        ItemDetailsModel.getInstance(this).startLoading(item);

        if(item.photos != null && !item.photos.isEmpty()) {
            view.setItemPhoto(item.photos.get(0));
        }

        view.setTitle(item.title);

        //set price
        String priceFormatted = String.format(Locale.getDefault(), "%.2f",item.price)+" zł";
        PriceType priceType = DataMediator.getPriceType(item.priceTypeId);
        if(priceType != null && priceType.name != null){
            priceFormatted += " " + priceType.name;
        }
        view.setPrice(priceFormatted);

        //rating
        if(item.ratingAverage == 0){
            view.setRating(ApplicationController.getInstance().getString(R.string.no_rating));
        }else{
            String rating = ApplicationController.getInstance().getString(R.string.rating) + ": " + item.ratingAverage;
            view.setRating(rating);
        }

        view.setDescription(item.description);

        if(item.notes != null) view.setNotes(item.notes);
        if(item.address != null && item.address.name != null) view.setLocation(item.address.name);
    }

    void loadingFinished(){
        view.setLoading(false);
    }

    void setOwner(User owner){
        view.setOwner(owner);
    }

    void setBookings(ArrayList<Booking> bookings) {
        view.setBookings(bookings);
    }

    void setReviews(ArrayList<Review> reviews) {
        view.setReviews(reviews);
    }

    void setSimilarItems(ArrayList<Item> similarItems) {
        view.setSimilarItems(similarItems);
    }

}
