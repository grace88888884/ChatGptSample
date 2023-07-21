package com.example.chatgptsample;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
public class Holiday {
    private String country;
    private LocalDate date;
    private String name;

    // Method to write holiday to a CSV file
    public void writeToFile(String filename) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename, true))) {
            String[] data = {country, date.toString(), name};
            writer.writeNext(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to convert a Holiday object to a CSV-formatted string
    public String toCsvString() {
        return country + "," + date.toString() + "," + name;
    }

    // Static method to read holidays from a CSV file
    public static Holiday readFromFile(String[] data) {
        String country = data[0];
        LocalDate date = LocalDate.parse(data[1]);
        String name = data[2];
        return new Holiday(country, date, name);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Holiday(String country, LocalDate date, String name) {
        this.country = country;
        this.date = date;
        this.name = name;
    }
}
