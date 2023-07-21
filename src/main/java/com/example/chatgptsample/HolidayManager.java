package com.example.chatgptsample;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.springframework.core.io.ClassPathResource;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HolidayManager {
    private List<Holiday> holidays;
    private final String filename = "holidays.csv";

    public HolidayManager() {
        holidays = new ArrayList<>();
        loadHolidaysFromCSV();
    }

    private void loadHolidaysFromCSV() {
        try (CSVReader reader = new CSVReader(new FileReader(new ClassPathResource(filename).getFile()))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                Holiday holiday = Holiday.readFromFile(line);
                holidays.add(holiday);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to save holidays to CSV file
    private void saveHolidays1() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(new ClassPathResource(filename).getFile()))) {
            for (Holiday holiday : holidays) {
                String[] data = {holiday.getCountry(), holiday.getDate().toString(), holiday.getName()};
                writer.writeNext(data);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to save holidays to the CSV file
    public void saveHolidays() {
        try {
            StringBuilder content = new StringBuilder();
            for (Holiday holiday : holidays) {
                content.append(holiday.toCsvString()).append("\n");
            }
            Files.write(Paths.get(new ClassPathResource(filename).getURI()), content.toString().getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // API 1: Add new holiday
    public boolean addHoliday(Holiday holiday) {
        // Reject if any of the required fields are missing
        if (holiday == null || holiday.getDate() == null || holiday.getCountry() == null || holiday.getName() == null) {
            return false;
        }
// Reject if the holiday with the same date and country already exists
        if (isDuplicateHoliday(holiday.getDate(), holiday.getCountry())) {
            return false;
        }
        holidays.add(holiday);
        saveHolidays();
        return true;

    }

    // Method to check if a holiday with the same date and country already exists
    private boolean isDuplicateHoliday(LocalDate date, String country) {
        for (Holiday holiday : holidays) {
            if (holiday.getDate().equals(date) && holiday.getCountry().equals(country)) {
                return true;
            }
        }
        return false;
    }

    // API 2: Update holiday
    public boolean updateHoliday(String country, LocalDate date, Holiday updatedHoliday) {
        boolean isUpdate=false;
        // Reject if updating to a holiday with the same date and country as another existing holiday
        if (isDuplicateHoliday(updatedHoliday.getDate(), updatedHoliday.getCountry())) {
            return false;
        }
        for (int i = 0; i < holidays.size(); i++) {
            Holiday holiday = holidays.get(i);
            if (holiday.getCountry().equals(country) && holiday.getDate().equals(date)) {
                holidays.set(i, updatedHoliday);
                isUpdate=true;
            }
        }

        if(isUpdate) {
            saveHolidays();
            return true;
        }else {
            return false;
        }
    }

    // API 3: Remove holiday
    public boolean removeHoliday(String country, LocalDate date) {
        boolean isDelete=false;
        for (int i = 0; i < holidays.size(); i++) {
            Holiday holiday = holidays.get(i);
            if (holiday.getCountry().equals(country) && holiday.getDate().equals(date)) {
                holidays.remove(i);
                isDelete=true;
            }
        }
        if(isDelete) {
            saveHolidays();
            return true;
        }else {
            return false;
        }
    }

    // Method to get the next year's holiday for a specified country
    public List<Holiday> getNextYearHoliday(String country, LocalDate nextYearDate) {
        List<Holiday> nextYearHolidays = new ArrayList<>();
        for (Holiday holiday : holidays) {
            if (holiday.getCountry().equals(country) && holiday.getDate().isAfter(nextYearDate.minusDays(1))) {
                nextYearHolidays.add(holiday);
            }
        }
        return nextYearHolidays;
    }

    // Method to get the next holiday date for a specified country based on the current date
    public LocalDate getNextHolidayDate(String country, LocalDate currentDate) {
        List<Holiday> holidaysForCountry = getHolidaysForCountry(country);

        // Filter holidays that are after the current date
        List<Holiday> upcomingHolidays = holidaysForCountry.stream()
                .filter(holiday -> holiday.getDate().isAfter(currentDate))
                .sorted(Comparator.comparing(Holiday::getDate))
                .collect(Collectors.toList());;

        if (!upcomingHolidays.isEmpty()) {
            return upcomingHolidays.get(0).getDate();
        }

        return null;
    }

    // Helper method to get holidays for a specific country
    private List<Holiday> getHolidaysForCountry(String country) {
        return holidays.stream()
                .filter(holiday -> holiday.getCountry().equals(country))
                .collect(Collectors.toList());
    }

    // Method to check if a given date is a holiday
    public boolean isHoliday(LocalDate date) {
        for (Holiday holiday : holidays) {
            if (holiday.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }
}
