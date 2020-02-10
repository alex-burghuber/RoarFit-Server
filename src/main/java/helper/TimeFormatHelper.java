package helper;

import java.text.SimpleDateFormat;

public class TimeFormatHelper {

    public SimpleDateFormat formatter;
    private static TimeFormatHelper timeFormatHelper;

    private TimeFormatHelper() {
        formatter = new SimpleDateFormat("mm:ss");
    }

    public static TimeFormatHelper getInstance() {
        if (timeFormatHelper == null) {
            timeFormatHelper = new TimeFormatHelper();
        }
        return timeFormatHelper;
    }
}
