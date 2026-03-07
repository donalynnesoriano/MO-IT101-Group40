package motorphpayrollsystem;

import java.util.*;

public class DateUtils {

    // ====== DATE FILTERING & CUTOFF LOGIC ======

    static boolean isJuneToDecember(String date) {
        if (date == null || date.length() < 7) return false;

        String[] parts = date.split("-");
        if (parts.length < 2) return false;

        int month = Integer.parseInt(parts[1]);
        return month >= 6 && month <= 12;
    }

    static String cutoffKey(String date) {
        String[] parts = date.split("-");
        String year = parts[0];
        String month = parts[1];
        int day = Integer.parseInt(parts[2]);

        String cutoff = (day <= 15) ? "C1" : "C2";

        return year + "-" + month + "|" + cutoff;
    }

    static String yearMonthFromAnyRecord(List<String[]> empRows, int targetMonth) {
        for (String[] r : empRows) {
            String date = r[4];
            String[] parts = date.split("-");
            int month = Integer.parseInt(parts[1]);

            if (month == targetMonth) {
                return parts[0] + "-" + parts[1];
            }
        }

        String firstDate = empRows.get(0)[4];
        String[] parts = firstDate.split("-");
        return parts[0] + "-" + String.format("%02d", targetMonth);
    }

}