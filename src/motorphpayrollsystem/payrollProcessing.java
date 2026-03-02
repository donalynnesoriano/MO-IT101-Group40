package MotorPHPayrollSystem;

import java.util.Scanner;

public class payrollProcessing {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Input Employee 1
        System.out.println("Enter Employee Name: ");
        String name = sc.nextLine();

        System.out.println("Enter Hourly Rate: ");
        double rate = sc.nextDouble();

        System.out.println("Enter Cutoff 1 Hours: ");
        double cutoff1 = sc.nextDouble();

        System.out.println("Enter Cutoff 2 Hours: ");
        double cutoff2 = sc.nextDouble();

        Employee emp = new Employee(name, rate, cutoff1, cutoff2);
        emp.processPayrollForEmployee();

        sc.close();
    }
}

class Employee {
    String name;
    double hourlyRate;
    double hoursCutoff1;
    double hoursCutoff2;

    public Employee(String name, double hourlyRate,
                    double hoursCutoff1, double hoursCutoff2) {
    	
        this.name = name;
        this.hourlyRate = hourlyRate;
        this.hoursCutoff1 = hoursCutoff1;
        this.hoursCutoff2 = hoursCutoff2;
    }

    public void processPayrollForEmployee() {

        double totalHours = hoursCutoff1 + hoursCutoff2;
        double grossCutoff1 = hoursCutoff1 * hourlyRate;
        double grossCutoff2 = hoursCutoff2 * hourlyRate;
        double totalGross = grossCutoff1 + grossCutoff2;
        double tax = totalGross * 0.20;

        double netCutoff1 = grossCutoff1;
        double netCutoff2 = grossCutoff2 - tax;

        System.out.println("=========== PAYROLL SUMMARY ===========");
        System.out.println("Employee Name: " + name);
        System.out.println("Hourly Rate: ₱" + hourlyRate);
        System.out.println("Total Hours: " + totalHours);
        System.out.println("Total Gross Salary: ₱" + totalGross);
        System.out.println("Net Salary (After Tax): ₱" + (netCutoff1 + netCutoff2));
        System.out.println("========================================");
    }
}