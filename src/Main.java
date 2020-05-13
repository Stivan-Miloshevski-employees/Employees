import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        List<Data> employees = new ArrayList<>();

        System.out.println("Enter file path:");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String nowDate = LocalDate.now().format(dtf);

        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().equals("")) {
                String[] data = (line.split(",\\s+"));

                if (data[3].trim().equals("NULL")) {
                    data[3] = nowDate;
                }

                Data employee = new Data(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2], data[3].trim());
                employees.add(employee);
            }
        }
        Map<String, Long> workBetweenTwoEmployees = new HashMap<>();

        for (Data currentEmployee : employees) {
            employees.stream()
                    .filter(employee -> currentEmployee.getProjectID().equals(employee.getProjectID())
                            && !currentEmployee.getEmployeeID().equals(employee.getEmployeeID()))
                    .forEach(employee -> {
                        LocalDate currentEmployeeDateFrom = LocalDate.from(dtf.parse(currentEmployee.getDateFrom()));

                        LocalDate currentEmployeeDateTo = LocalDate.from(dtf.parse(currentEmployee.getDateTo()));

                        LocalDate employeeDateFrom = LocalDate.from(dtf.parse(employee.getDateFrom()));

                        LocalDate employeeDateTo = LocalDate.from(dtf.parse(employee.getDateTo()));

                        Instant instantCurrentEmployeeDateFrom = currentEmployeeDateFrom
                                .atStartOfDay(ZoneId.systemDefault()).toInstant();

                        long lengthCurrentEmployeeDateFrom = instantCurrentEmployeeDateFrom.toEpochMilli();

                        Instant instantCurrentEmployeeDateTo = currentEmployeeDateTo
                                .atStartOfDay(ZoneId.systemDefault()).toInstant();

                        long lengthCurrentEmployeeDateTo = instantCurrentEmployeeDateTo.toEpochMilli();

                        Instant instantEmployeeDateFrom = employeeDateFrom
                                .atStartOfDay(ZoneId.systemDefault()).toInstant();

                        long lengthEmployeeDateFrom = instantEmployeeDateFrom.toEpochMilli();

                        Instant instantEmployeeDateTo = employeeDateTo
                                .atStartOfDay(ZoneId.systemDefault()).toInstant();

                        long lengthEmployeeDateTo = instantEmployeeDateTo.toEpochMilli();

                        if (!(lengthCurrentEmployeeDateTo < lengthEmployeeDateFrom || lengthCurrentEmployeeDateFrom > lengthEmployeeDateTo)) {
                            if (lengthCurrentEmployeeDateFrom >= lengthEmployeeDateFrom) {
                                if (lengthCurrentEmployeeDateTo <= lengthEmployeeDateTo) {
                                    calculateDays(workBetweenTwoEmployees, currentEmployee, employee,
                                            currentEmployeeDateFrom, currentEmployeeDateTo);
                                } else {
                                    calculateDays(workBetweenTwoEmployees, currentEmployee, employee,
                                            currentEmployeeDateFrom, employeeDateTo);
                                }
                            } else {
                                if (lengthCurrentEmployeeDateTo <= lengthEmployeeDateTo) {
                                    calculateDays(workBetweenTwoEmployees, currentEmployee, employee,
                                            employeeDateFrom, currentEmployeeDateTo);
                                } else {
                                    calculateDays(workBetweenTwoEmployees, currentEmployee, employee,
                                            employeeDateFrom,
                                            employeeDateTo);
                                }
                            }
                        }
                    });
        }

        Map.Entry<String, Long> stringLongEntry = workBetweenTwoEmployees
                .entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .findFirst()
                .orElse(null);

        if (stringLongEntry == null) {
            System.out.println("Not exist");// TO DO
        } else {
            workBetweenTwoEmployees
                    .entrySet()
                    .stream()
                    .filter(w -> w.getValue().equals(stringLongEntry.getValue()))
                    .forEach(w -> {
                        String key = w.getKey();

                        System.out.println("A pair of employees: " + key.substring(0, key.indexOf(" "))
                                + " and" + key.substring(key.indexOf(" ")));
                    });
        }
    }

    private static void calculateDays(Map<String, Long> workBetweenTwoEmployees, Data currentEmployee, Data employee,
                                      LocalDate dateFrom, LocalDate dateTo) {

        long days = ChronoUnit.DAYS.between(dateFrom, dateTo);

        String right = currentEmployee.getEmployeeID() + " " + employee.getEmployeeID();

        String left = employee.getEmployeeID() + " " + currentEmployee.getEmployeeID();

        if (workBetweenTwoEmployees.containsKey(right)) {
            Long currentDays = workBetweenTwoEmployees.get(right);

            workBetweenTwoEmployees.put(right, currentDays + days);

        } else if (workBetweenTwoEmployees.containsKey(left)) {
            Long currentDays = workBetweenTwoEmployees.get(left);

            workBetweenTwoEmployees.put(left, currentDays + days);

        } else {
            workBetweenTwoEmployees.put(right, days);
        }
    }
}