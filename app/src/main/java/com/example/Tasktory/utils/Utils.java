package com.example.Tasktory.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    /**
     * Kết hợp date và time thành một đối tượng Date duy nhất.
     *
     * @param date Calendar đại diện cho ngày (có thể null)
     * @param time Calendar đại diện cho giờ/phút/giây (có thể null)
     * @return Date đã được kết hợp từ date và time
     */
    public static Date combineDateAndTime(Calendar date, Calendar time) {
        if (date == null && time == null) {
            return null; // Nếu cả date và time đều null, trả về null
        }

        // Tạo Calendar để xây dựng dueDate
        Calendar combined = Calendar.getInstance();
        combined.clear(); // Xóa tất cả trường trong Calendar để tránh lỗi logic

        if (date != null) {
            // Sao chép ngày, tháng, năm từ date
            combined.set(Calendar.YEAR, date.get(Calendar.YEAR));
            combined.set(Calendar.MONTH, date.get(Calendar.MONTH));
            combined.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
        } else {
            // Nếu date bị null, đặt ngày hiện tại
            Calendar now = Calendar.getInstance();
            combined.set(Calendar.YEAR, now.get(Calendar.YEAR));
            combined.set(Calendar.MONTH, now.get(Calendar.MONTH));
            combined.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
        }

        if (time != null) {
            // Sao chép giờ, phút, giây từ time
            combined.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
            combined.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
            combined.set(Calendar.SECOND, time.get(Calendar.SECOND));
            combined.set(Calendar.MILLISECOND, time.get(Calendar.MILLISECOND));
        } else {
            // Nếu time bị null, thì đặt thời gian về 00:00:00.000
            combined.set(Calendar.HOUR_OF_DAY, 0);
            combined.set(Calendar.MINUTE, 0);
            combined.set(Calendar.SECOND, 0);
            combined.set(Calendar.MILLISECOND, 0);
        }

        // Trả về đối tượng Date
        return combined.getTime();
    }

    public static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
