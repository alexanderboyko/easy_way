package boyko.alex.easy_way.libraries;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;

/**
 * Created by Sasha on 09.11.2016.
 *
 */

public class DateHelper {

    public static String getStringTimeFromLong(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time * 1000);

        //Log.i("DATE", calendar.toString());

        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR_OF_DAY));
        String minutes = String.valueOf(calendar.get(Calendar.MINUTE) < 10 ? "0" + calendar.get(Calendar.MINUTE) : calendar.get(Calendar.MINUTE));

        return hour + ":" + minutes;
    }

    public static boolean ifTimesFromOneDay(long time1, long time2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(time2);

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean ifTimesFromOneMonth(long time1, long time2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(time2);

        if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)) {
            return true;
        } else return false;
    }

    public static boolean ifTimesFromOneYear(long time1, long time2) {
        //Log.i("TIMES", time1 + " " + time2);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(time2);

        //Log.i("DAYS", calendar1.get(Calendar.DAY_OF_MONTH) + " " + calendar1.get(Calendar.MONTH) + " " +calendar1.get(Calendar.YEAR) + " " + calendar2.get(Calendar.DAY_OF_MONTH) +" "+calendar2.get(Calendar.MONTH ) + " " +calendar1.get(Calendar.YEAR) + " ");

        if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)) {
            return true;
        } else return false;
    }

    public static boolean ifTimeFromCurrentMonth(long time) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time);
        if (calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == calendar1.get(Calendar.MONTH)) {
            return true;
        } else {
            return false;
        }
    }

    public static long getTodayTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String getFullMonthName(int month) {
        month += 1;
        switch (month) {
            case 1:
                return ApplicationController.getInstance().getResources().getString(R.string.fjan);
            case 2:
                return ApplicationController.getInstance().getResources().getString(R.string.ffeb);
            case 3:
                return ApplicationController.getInstance().getResources().getString(R.string.fmar);
            case 4:
                return ApplicationController.getInstance().getResources().getString(R.string.fapr);
            case 5:
                return ApplicationController.getInstance().getResources().getString(R.string.fmay);
            case 6:
                return ApplicationController.getInstance().getResources().getString(R.string.fjun);
            case 7:
                return ApplicationController.getInstance().getResources().getString(R.string.fjul);
            case 8:
                return ApplicationController.getInstance().getResources().getString(R.string.faug);
            case 9:
                return ApplicationController.getInstance().getResources().getString(R.string.fsep);
            case 10:
                return ApplicationController.getInstance().getResources().getString(R.string.foct);
            case 11:
                return ApplicationController.getInstance().getResources().getString(R.string.fnov);
            case 12:
                return ApplicationController.getInstance().getResources().getString(R.string.fdec);
            default:
                return "";
        }
    }

    public static String getShortMonthName(int month) {
        month += 1;
        switch (month) {
            case 1:
                return ApplicationController.getInstance().getResources().getString(R.string.jan);
            case 2:
                return ApplicationController.getInstance().getResources().getString(R.string.feb);
            case 3:
                return ApplicationController.getInstance().getResources().getString(R.string.mar);
            case 4:
                return ApplicationController.getInstance().getResources().getString(R.string.apr);
            case 5:
                return ApplicationController.getInstance().getResources().getString(R.string.may);
            case 6:
                return ApplicationController.getInstance().getResources().getString(R.string.jun);
            case 7:
                return ApplicationController.getInstance().getResources().getString(R.string.jul);
            case 8:
                return ApplicationController.getInstance().getResources().getString(R.string.aug);
            case 9:
                return ApplicationController.getInstance().getResources().getString(R.string.sep);
            case 10:
                return ApplicationController.getInstance().getResources().getString(R.string.oct);
            case 11:
                return ApplicationController.getInstance().getResources().getString(R.string.nov);
            case 12:
                return ApplicationController.getInstance().getResources().getString(R.string.dec);
            default:
                return "";
        }
    }

    public static String getShortDayName(int calendarDayNumber) {
        switch (calendarDayNumber) {
            case Calendar.MONDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.mon);
            case Calendar.TUESDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.tue);
            case Calendar.WEDNESDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.wed);
            case Calendar.THURSDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.thu);
            case Calendar.FRIDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.fri);
            case Calendar.SATURDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.sat);
            case Calendar.SUNDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.sun);
        }
        return "";
    }

    private static String getDayName(int calendarDayNumber) {
        switch (calendarDayNumber) {
            case Calendar.MONDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.fmon);
            case Calendar.TUESDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.ftue);
            case Calendar.WEDNESDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.fwed);
            case Calendar.THURSDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.fthu);
            case Calendar.FRIDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.ffri);
            case Calendar.SATURDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.fsat);
            case Calendar.SUNDAY:
                return ApplicationController.getInstance().getResources().getString(R.string.fsun);
        }
        return "";
    }

    public static String getSmartFormattedDate(long time) {
        String date = "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Calendar calendarToday = Calendar.getInstance();

        date += getDayName(calendar.get(Calendar.DAY_OF_WEEK));
        date += ", " + calendar.get(Calendar.DATE) + " " + getFullMonthName(calendar.get(Calendar.MONTH));
        if (calendar.get(Calendar.YEAR) != calendarToday.get(Calendar.YEAR)) {
            date += " " + calendar.get(Calendar.YEAR);
        }

        return date;
    }

    public static String getFormattedDateWithTime(long date) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH : mm", Locale.getDefault());
        Date myDate = new Date(date);
        return dateFormat.format(myDate);
    }

    public static String getFormattedDateWithoutTime(long date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date myDate = new Date(date);
        return dateFormat.format(myDate);
    }

    public static String getFormattedTime(long date) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.UK);
        Date myDate = new Date(date);
        return dateFormat.format(myDate);
    }

    /**
     * Uses to show how much time past or how many time left to from selected time till current time. Return smart formatted time,
     * like: "day ago, 12:00", "in 3 days, 13:45", "year ago".
     *
     * @param time time from this value (in ms)
     * @return smart formatted string
     */
    public static String getFormattedTimeFromCurrentTime(long time, boolean isAllDay) {
        Log.i("FORMATTEDTIME", getFormattedDateWithoutTime(time) + " " + isAllDay);
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time);

        long timeLeft = calendar.getTimeInMillis() - time;

        if (isAllDay) {
            Calendar calendar2 = Calendar.getInstance();
            if (ifTimesFromOneDay(calendar2.getTimeInMillis(), calendar1.getTimeInMillis())) {
                Log.i("FORMATTEDTIME", "ONE DAY ALL DAY");
                return ApplicationController.getInstance().getString(R.string.today) + ", " + ApplicationController.getInstance().getString(R.string.all_day);
            } else {
                calendar2.add(Calendar.DATE, -1);
                if (ifTimesFromOneDay(calendar2.getTimeInMillis(), calendar1.getTimeInMillis())) {
                    Log.i("FORMATTEDTIME", "YESTERDAY ALL DAY");
                    return ApplicationController.getInstance().getString(R.string.yesterday) + ", " + ApplicationController.getInstance().getString(R.string.all_day);
                } else {
                    calendar2.add(Calendar.DATE, 2);
                    if (ifTimesFromOneDay(calendar2.getTimeInMillis(), calendar1.getTimeInMillis())) {
                        Log.i("FORMATTEDTIME", "TOMORROW ALL DAY");
                        return ApplicationController.getInstance().getString(R.string.tomorrow) + ", " + ApplicationController.getInstance().getString(R.string.all_day);
                    }
                }
            }
        }

        //left less than hour
        if (Math.abs(timeLeft) < 3600000) {
            return getMinutesFormatted(timeLeft) + ", " + getFormattedTime(time);
        } else {
            //left less than day
            if (Math.abs(timeLeft) < 86000000) {
                return getHoursFormatted(timeLeft) + ", " + getFormattedTime(time);
            } else {
                int days = howManyDaysBetweenTimes(calendar1.getTimeInMillis(), calendar.getTimeInMillis());
                //from 1 to 25 days
                if (Math.abs(days) < 26) {
                    return getDaysFormatted(timeLeft) + ", " + getFormattedTime(time);
                } else {
                    int monthCount = howManyMonthsBetweenTimes(calendar1.getTimeInMillis(), calendar.getTimeInMillis());
                    //from 1 to 11 months
                    if (Math.abs(monthCount) < 12) {
                        if (monthCount < 0) {
                            if (Math.abs(monthCount) == 1)
                                return Math.abs(monthCount) + " " + ApplicationController.getInstance().getString(R.string.month_ago);
                            else return Math.abs(monthCount) + " " + ApplicationController.getInstance().getString(R.string.months_ago);
                        } else {
                            if (Math.abs(monthCount) == 1)
                                return ApplicationController.getInstance().getString(R.string.in) + " " + Math.abs(monthCount) + " " + ApplicationController.getInstance().getString(R.string.month_);
                            else
                                return ApplicationController.getInstance().getString(R.string.in) + " " + Math.abs(monthCount) + " " + ApplicationController.getInstance().getString(R.string.months);
                        }
                    } else {
                        return getYearsFormatted(calendar, calendar1, monthCount);
                    }
                }
            }
        }
    }

    private static String getMinutesFormatted(long timeLeft) {
        int minutes = (int) timeLeft / 60000;
        if (minutes > 0) {
            if (Math.abs(minutes) == 1) return ApplicationController.getInstance().getString(R.string.minute_ago);
            else return minutes + " " + ApplicationController.getInstance().getString(R.string.minutes_ago);
        } else {
            if (minutes == 0) return ApplicationController.getInstance().getString(R.string.right_now);
            if (Math.abs(minutes) == 1) return ApplicationController.getInstance().getString(R.string.in_a_minute);
            else
                return ApplicationController.getInstance().getString(R.string.in) + " " + Math.abs(minutes) + " " + ApplicationController.getInstance().getString(R.string.minutes);
        }
    }

    private static String getHoursFormatted(long timeLeft) {
        int hours = (int) timeLeft / 3600000;
        if (hours > 0) {
            if (Math.abs(hours) == 1) return ApplicationController.getInstance().getString(R.string.hour_ago);
            else return hours + " " + ApplicationController.getInstance().getString(R.string.hours_ago);
        } else {
            if (Math.abs(hours) == 1)
                return ApplicationController.getInstance().getString(R.string.in) + " " +ApplicationController.getInstance().getString(R.string.in_an_hour);
            else
                return ApplicationController.getInstance().getString(R.string.in) + " " + Math.abs(hours) + " " + ApplicationController.getInstance().getString(R.string.hours);
        }
    }

    private static String getDaysFormatted(long timeLeft) {
        int days = (int) timeLeft / 86000000;
        if (days > 0) {
            if (Math.abs(days) == 1) return ApplicationController.getInstance().getString(R.string.yesterday);
            else return days + " " + ApplicationController.getInstance().getString(R.string.days_ago);
        } else {
            if (Math.abs(days) == 1)
                return ApplicationController.getInstance().getString(R.string.tomorrow);
            else
                return ApplicationController.getInstance().getString(R.string.in) + " " + Math.abs(days) + " " + ApplicationController.getInstance().getString(R.string.days);
        }
    }

    private static String getYearsFormatted(Calendar calendar, Calendar calendar1, int monthCount) {
        int years;
        if (monthCount > 0) {
            years = calendar1.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
        } else {
            years = calendar.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR);
        }
        if (monthCount > 0) {
            if (years == 1) return ApplicationController.getInstance().getString(R.string.in_one_year);
            else
                return ApplicationController.getInstance().getString(R.string.in) + " " + years + ApplicationController.getInstance().getString(R.string.years);
        } else {
            if (years == 1) return ApplicationController.getInstance().getString(R.string.year_ago);
            else return Math.abs(years) + ApplicationController.getInstance().getString(R.string.years_ago);
        }
    }

    /**
     * Return value that show, how many times between two dates. Could return -2 - it means, that 2 months past. If return +2 - it means 2 months left.
     *
     * @param c1
     * @param c2
     * @return
     */

    private static int howManyMonthsBetweenTimes(long c1, long c2) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(c1);
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTimeInMillis(c2);
        int months = 0;
        if (ifTimesFromOneMonth(timeCalendar.getTimeInMillis(), todayCalendar.getTimeInMillis())) {
            return months;
        }
        while (true) {
            if (timeCalendar.getTimeInMillis() > todayCalendar.getTimeInMillis()) {
                timeCalendar.add(Calendar.MONTH, -1);
                months++;
            } else {
                timeCalendar.add(Calendar.MONTH, 1);
                months--;
            }

            if (ifTimesFromOneMonth(timeCalendar.getTimeInMillis(), todayCalendar.getTimeInMillis())) {
                return months;
            }
        }

    }

    private static int howManyDaysBetweenTimes(long time1, long time2) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(time1);
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTimeInMillis(time2);
        int days = 0;
        if (ifTimesFromOneDay(timeCalendar.getTimeInMillis(), todayCalendar.getTimeInMillis())) {
            return days;
        }
        while (true) {
            if (timeCalendar.getTimeInMillis() > todayCalendar.getTimeInMillis()) {
                timeCalendar.add(Calendar.DATE, -1);
                days++;
            } else {
                timeCalendar.add(Calendar.DATE, 1);
                days--;
            }

            if (ifTimesFromOneDay(timeCalendar.getTimeInMillis(), todayCalendar.getTimeInMillis())) {
                return days;
            }
        }
    }

    /**
     * Find Monday day, starting from specific time.
     * For example, if timeFrom is 16th June, Friday - function return  19th June, Monday
     * @param fromTime any day
     * @return next Monday starting from selected day
     */
    public static long findNextMonday(long fromTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fromTime);
        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            for (int i = 0; i < 8; i++) {
                calendar.add(Calendar.DATE, 1);
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    //MONDAY found
                    break;
                }
            }
        }else{
            return fromTime;
        }
        return calendar.getTimeInMillis();
    }

    public static boolean ifNextDayFromOtherMonth(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DATE, 1);

        return !DateHelper.ifTimesFromOneMonth(calendar.getTimeInMillis(), time);
    }

    public static long getNextDayTime(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DATE, 1);

        return calendar.getTimeInMillis();
    }

    public static long getTimeInHour(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.HOUR, 1);
        return calendar.getTimeInMillis();
    }
    public static String getFormattedCalendarMonthName(long time){
        //set this time to calendar to get month ID
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //set full month name
        if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            return getFullMonthName(calendar.get(Calendar.MONTH));
        } else {
            return DateHelper.getShortMonthName((calendar.get(Calendar.MONTH)))
                    + " " + calendar.get(Calendar.YEAR);
        }
    }

    public static long getDayInAMonth(long date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTimeInMillis();
    }

    public static long getDayLastMonth(long date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTimeInMillis();
    }

    /**
     * Using in availability calendar - determines which month has been selected: current month, previous to current month, or next to current month
     * @param firstDateOfMonth time of first day of month
     * @return 0 - previous month; 1 - current month; 2 - next month
     */
    public static int whichMonthSelectedCompareToCurrent(long firstDateOfMonth){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(firstDateOfMonth);

        if(ifTimesFromOneMonth(calendar.getTimeInMillis(), getTodayTime())) return 1;
        calendar.add(Calendar.MONTH,-1);
        if(ifTimesFromOneMonth(calendar.getTimeInMillis(), getTodayTime())) return 0;

        calendar.add(Calendar.MONTH,2);
        if(ifTimesFromOneMonth(calendar.getTimeInMillis(), getTodayTime())) return 2;

        return 1;
    }
}
