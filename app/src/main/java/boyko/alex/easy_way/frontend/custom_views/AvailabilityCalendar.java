package boyko.alex.easy_way.frontend.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

/**
 * Created by Sasha on 25.11.2017.
 */

public class AvailabilityCalendar extends CompactCalendarView {
    public static final int PAGE_LEFT = 0, PAGE_CENTER = 1, PAGE_RIGHT = 2;
    private int page = 1;
    private float initialX;

    public AvailabilityCalendar(Context context) {
        super(context);
    }

    public AvailabilityCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AvailabilityCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPage(int page){
        this.page = page;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                if(page != PAGE_CENTER) {
                    float currentX = event.getX();
                    if(page == PAGE_LEFT){
                        if (initialX > currentX) {
                            return true;
                        }
                    }
                    if(page == PAGE_RIGHT){
                        if (initialX < currentX) {
                            return true;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if(page != PAGE_CENTER) {
                    float currentX = event.getX();
                    if(page == PAGE_LEFT){
                        if (initialX > currentX) {
                            return true;
                        }
                    }
                    if(page == PAGE_RIGHT){
                        if (initialX < currentX) {
                            return true;
                        }
                    }
                }

                break;
        }
        return super.onTouchEvent(event);
    }
}
