package motorphpayrollsystem;

import java.io.*;
import java.util.*;

public class MotorPHPayrollSystem {

    // =========================================================
    // FILE NAMES
    // =========================================================
    static final String EMPLOYEE_FILE = "employee_details.csv";
    static final String ATTENDANCE_FILE = "attendance_record.csv";

    // =========================================================
    // Part 1 – Login System
    // =========================================================
    public static void main(String[] args) {
        login();
    }

    static void login() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        boolean validUser = username.equals("employee") || username.equals("payroll_staff");
        boolean validPass = password.equals("12345");

        if (!validUser || !validPass) {
            System.out.println("Incorrect username and/or password.");
            System.out.println("Program terminated.");
            return;
        }

        if (username.equals("employee")) {
            employeeMenu(sc);
        } else {
            payrollStaffMenu(sc);
        }
    }

    // =========================================================
    // Part 2 – Menu System
    // =========================================================
    static void employeeMenu(Scanner sc) {
        while (true) {
            System.out.println("\n=== EMPLOYEE MENU ===");
            System.out.println("1. Enter your employee number");
            System.out.println("2. Exit the program");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("2")) {
                System.out.println("Program terminated.");
                return;
            }

            if (!choice.equals("1")) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            System.out.print("Enter your employee number: ");
            String empNo = sc.nextLine().trim();

            Map<String, String[]> employeeMap = readEmployeeDetails();
            List<String[]> attendanceRows = readAttendanceRows();
            Map<String, List<String[]>> attendanceByEmp = groupAttendanceByEmployee(attendanceRows);

            if (!employeeMap.containsKey(empNo)) {
                System.out.println("Employee number does not exist.");
                continue;
            }

            displayEmployeeInfo(empNo, employeeMap, attendanceByEmp);
        }
    }

    static void payrollStaffMenu(Scanner sc) {
        while (true) {
            System.out.println("\n=== PAYROLL STAFF MENU ===");
            System.out.println("1. Process Payroll");
            System.out.println("2. Exit the program");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("2")) {
                System.out.println("Program terminated.");
                return;
            }

            if (!choice.equals("1")) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            processPayrollMenu(sc);
        }
    }

    static void processPayrollMenu(Scanner sc) {
        while (true) {
            System.out.println("\n=== PROCESS PAYROLL ===");
            System.out.println("1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            Map<String, String[]> employeeMap = readEmployeeDetails();
            List<String[]> attendanceRows = readAttendanceRows();
            Map<String, List<String[]>> attendanceByEmp = groupAttendanceByEmployee(attendanceRows);

            if (choice.equals("1")) {
                System.out.print("Enter employee number: ");
                String empNo = sc.nextLine().trim();

                if (!employeeMap.containsKey(empNo)) {
                    System.out.println("Employee number does not exist.");
                    continue;
                }

                processPayrollForEmployee(empNo, employeeMap, attendanceByEmp);

            } else if (choice.equals("2")) {
                for (String empNo : employeeMap.keySet()) {
                    processPayrollForEmployee(empNo, employeeMap, attendanceByEmp);
                    System.out.println("--------------------------------------------------");
                }

            } else if (choice.equals("3")) {
                System.out.println("Program terminated.");
                return;

            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // =========================================================
    // Part 3 – CSV Reading
    // =========================================================
    static Map<String, String[]> readEmployeeDetails() {
        Map<String, String[]> employeeMap = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            String line = br.readLine(); 
            if (line == null) return employeeMap;

            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);

                if (data.length < 19) continue;

                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].trim().replace("\"", "");
                }

                String empNo = data[0];
                employeeMap.put(empNo, data);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Employee details file not found: " + EMPLOYEE_FILE);
        } catch (Exception e) {
            System.out.println("Error reading employee details file.");
            e.printStackTrace();
        }

        return employeeMap;
    }

    static List<String[]> readAttendanceRows() {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            String line = br.readLine(); 
            if (line == null) return rows;

            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);

                if (data.length < 6) continue;

                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].trim().replace("\"", "");
                }

                rows.add(data);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Attendance file not found: " + ATTENDANCE_FILE);
        } catch (Exception e) {
            System.out.println("Error reading attendance file.");
            e.printStackTrace();
        }

        return rows;
    }

    static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        values.add(current.toString());
        return values.toArray(new String[0]);
    }

    static Map<String, List<String[]>> groupAttendanceByEmployee(List<String[]> rows) {
        Map<String, List<String[]>> map = new TreeMap<>();

        for (String[] row : rows) {
            String empNo = row[0];
            map.putIfAbsent(empNo, new ArrayList<>());
            map.get(empNo).add(row);
        }

        return map;
    }

    // =========================================================
    // Part 4 – Date Filtering & Cutoff Logic
    // =========================================================
    static boolean isJuneToDecember(String date) {
        if (date == null || date.isEmpty()) return false;

        String[] parts = date.split("/");
        if (parts.length != 3) return false;

        int month = Integer.parseInt(parts[0]);
        return month >= 6 && month <= 12;
    }

    static String cutoffKey(String date) {
        String[] parts = date.split("/");
        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        String year = parts[2];

        String cutoff = (day <= 15) ? "C1" : "C2";
        return year + "-" + String.format("%02d", month) + "|" + cutoff;
    }

    static String yearMonthFromAttendance(List<String[]> rows, int targetMonth) {
        for (String[] row : rows) {
            String date = row[3];
            String[] parts = date.split("/");

            int month = Integer.parseInt(parts[0]);
            String year = parts[2];

            if (month == targetMonth) {
                return year + "-" + String.format("%02d", month);
            }
        }

        return "2024-" + String.format("%02d", targetMonth);
    }

    static String getMonthName(int month) {
        String[] monthNames = {
            "", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        return monthNames[month];
    }

    static int getLastDayOfMonth(int month) {
        if (month == 4 || month == 6 || month == 9 || month == 11) return 30;
        if (month == 2) return 28;
        return 31;
    }

    // =========================================================
    // Part 5 – Hours Worked Computation
    // =========================================================
    static double computeDailyHours(String timeIn, String timeOut) {
        try {
            int startMinutes = toMinutes(timeIn);
            int endMinutes = toMinutes(timeOut);

            int workStart = 8 * 60;  
            int workEnd = 17 * 60;    

        
            if (startMinutes < workStart) {
                startMinutes = workStart;
            }

          
            if (endMinutes > workEnd) {
                endMinutes = workEnd;
            }

            if (endMinutes <= startMinutes) {
                return 0.0;
            }

        
            if (startMinutes >= 480 && startMinutes <= 485 && endMinutes == 1020) {
                return 8.0;
            }

            int totalMinutes = endMinutes - startMinutes;

   
            totalMinutes -= 60;

            if (totalMinutes < 0) {
                totalMinutes = 0;
            }

            return totalMinutes / 60.0;

        } catch (Exception e) {
            return 0.0;
        }
    }

    static int toMinutes(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return hour * 60 + minute;
    }

    
    static void displayEmployeeInfo(String empNo, Map<String, String[]> employeeMap,
                                    Map<String, List<String[]>> attendanceByEmp) {

        String[] employee = employeeMap.get(empNo);
        String fullName = employee[2] + " " + employee[1];
        String birthday = employee[3];
        double hourlyRate = parseMoneySafe(employee[18]);

        System.out.println("Employee Number: " + empNo);
        System.out.println("Employee Name: " + fullName);
        System.out.println("Birthday: " + birthday);
        System.out.println("Hourly Rate: " + hourlyRate);

        List<String[]> empAttendance = attendanceByEmp.getOrDefault(empNo, new ArrayList<>());

        System.out.println("\nAttendance Records (June to December):");
        System.out.println("Date | Log In | Log Out | Hours Worked");

        for (String[] row : empAttendance) {
            String date = row[3];

            if (!isJuneToDecember(date)) {
                continue;
            }

            String timeIn = row[4];
            String timeOut = row[5];
            double hours = computeDailyHours(timeIn, timeOut);

            System.out.println(date + " | " + timeIn + " | " + timeOut + " | " + hours);
        }
    }

    // =========================================================
    // Part 6 – Payroll Processing
    // =========================================================
    static void processPayrollForEmployee(String empNo, Map<String, String[]> employeeMap,
                                          Map<String, List<String[]>> attendanceByEmp) {

        String[] employee = employeeMap.get(empNo);

        String fullName = employee[2] + " " + employee[1];
        String birthday = employee[3];
        double hourlyRate = parseMoneySafe(employee[18]);

        List<String[]> empAttendance = attendanceByEmp.getOrDefault(empNo, new ArrayList<>());
        Map<String, Double> cutoffHours = new TreeMap<>();

        System.out.println("Employee #: " + empNo);
        System.out.println("Employee Name: " + fullName);
        System.out.println("Birthday: " + birthday);
        System.out.println("Hourly Rate: " + hourlyRate);

        System.out.println("\nAttendance Records (June to December):");
        System.out.println("Date | Log In | Log Out | Hours Worked");

        for (String[] row : empAttendance) {
            String date = row[3];

            if (!isJuneToDecember(date)) {
                continue;
            }

            String timeIn = row[4];
            String timeOut = row[5];
            double hours = computeDailyHours(timeIn, timeOut);

            System.out.println(date + " | " + timeIn + " | " + timeOut + " | " + hours);

            String key = cutoffKey(date);
            cutoffHours.put(key, cutoffHours.getOrDefault(key, 0.0) + hours);
        }

        for (int month = 6; month <= 12; month++) {
            String ym = yearMonthFromAttendance(empAttendance, month);
            String keyC1 = ym + "|C1";
            String keyC2 = ym + "|C2";

            double h1 = cutoffHours.getOrDefault(keyC1, 0.0);
            double h2 = cutoffHours.getOrDefault(keyC2, 0.0);

            double gross1 = h1 * hourlyRate;
            double gross2 = h2 * hourlyRate;

            // Monthly gross first before deductions
            double monthlyGross = gross1 + gross2;
// =========================================================
    // Part 7 – Government Deductions
    // =========================================================
            double sss = computeSSS(monthlyGross);
            double philHealth = computePhilHealth(monthlyGross);
            double pagIbig = computePagIbig(monthlyGross);
            double tax = computeTax(monthlyGross);

            double totalDeductions = sss + philHealth + pagIbig + tax;

            // Deductions apply only to second cutoff
            double net1 = gross1;
            double net2 = gross2 - totalDeductions;

            String monthName = getMonthName(month);
            int lastDay = getLastDayOfMonth(month);

            System.out.println("\nCutoff Date: " + monthName + " 1 to " + monthName + " 15");
            System.out.println("Total Hours Worked: " + h1);
            System.out.println("Gross Salary: " + gross1);
            System.out.println("Net Salary: " + net1);

            System.out.println("\nCutoff Date: " + monthName + " 16 to " + monthName + " " + lastDay + " (Second payout includes all deductions)");
            System.out.println("Total Hours Worked: " + h2);
            System.out.println("Gross Salary: " + gross2);
            System.out.println("SSS: " + sss);
            System.out.println("PhilHealth: " + philHealth);
            System.out.println("Pag-IBIG: " + pagIbig);
            System.out.println("Tax: " + tax);
            System.out.println("Total Deductions: " + totalDeductions);
            System.out.println("Net Salary: " + net2);
        }
    }

    static double computeSSS(double gross) {
        return gross * 0.05;
    }

    static double computePhilHealth(double gross) {
        return gross * 0.03;
    }

    static double computePagIbig(double gross) {
        return gross * 0.02;
    }

    static double computeTax(double gross) {
        return gross * 0.10;
    }

    // =========================================================
    // Helpers
    // =========================================================
    static double parseMoneySafe(String value) {
        try {
            return Double.parseDouble(value.replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
