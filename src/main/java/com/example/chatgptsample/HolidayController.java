package com.example.chatgptsample;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/holidays")
public class HolidayController {
    private final HolidayManager holidayManager = new HolidayManager();

    // API to add a new holiday
    @PostMapping
    public ResponseEntity<String> addHoliday(@RequestBody Holiday holiday) {
        boolean added = holidayManager.addHoliday(holiday);
        if(added) {
            return new ResponseEntity<>("Holiday added successfully.", HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>("Holiday fail to add.", HttpStatus.NOT_FOUND);

        }
    }

    // API to remove a holiday
    @DeleteMapping("/{country}/{date}")
    public ResponseEntity<String> removeHoliday(@PathVariable String country, @PathVariable String date) {
        try {
            LocalDate holidayDate = LocalDate.parse(date);
            if (holidayManager.removeHoliday(country, holidayDate)) {
                return new ResponseEntity<>("Holiday removed successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Holiday not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid date format. Use yyyy-MM-dd.", HttpStatus.BAD_REQUEST);
        }
    }

    // API to update a holiday
    @PutMapping("/{country}/{date}")
    public ResponseEntity<String> updateHoliday(
            @PathVariable String country,
            @PathVariable String date,
            @RequestBody Holiday updatedHoliday
    ) {
        LocalDate holidayDate;
        try {
            holidayDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>("Invalid date format. Use yyyy-MM-dd.", HttpStatus.BAD_REQUEST);
        }

        if (holidayManager.updateHoliday(country, holidayDate, updatedHoliday)) {
            return new ResponseEntity<>("Holiday updated successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Holiday update fail.", HttpStatus.NOT_FOUND);
        }
    }

    // API to get the next year's holiday for a specified country
    @GetMapping("/{country}/nextyear")
    @ResponseBody
    public List<Holiday> getNextYearHoliday(@PathVariable String country) {
        LocalDate currentDate = LocalDate.now();
        int nextYear = currentDate.getYear() + 1;
        LocalDate nextYearDate = LocalDate.of(nextYear, 1, 1);

        List<Holiday> nextYearHoliday = holidayManager.getNextYearHoliday(country, nextYearDate);
            return nextYearHoliday;
    }

    // API to get the next holiday date for a specified country
    @GetMapping("/{country}/next")
    public ResponseEntity<String> getNextHolidayDate(@PathVariable String country) {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextHolidayDate = holidayManager.getNextHolidayDate(country, currentDate);

        if (nextHolidayDate != null) {
            return new ResponseEntity<>(nextHolidayDate.toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No upcoming holidays for the specified country.", HttpStatus.NOT_FOUND);
        }
    }


    // API to check if a given date is a holiday
    @GetMapping("/check-holiday/{date}")
    public ResponseEntity<String> isHoliday(@PathVariable String date) {
        LocalDate checkDate;
        try {
            checkDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>("Invalid date format. Use yyyy-MM-dd.", HttpStatus.BAD_REQUEST);
        }

        if (holidayManager.isHoliday(checkDate)) {
            return new ResponseEntity<>("The date is a holiday.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The date is not a holiday.", HttpStatus.OK);
        }
    }
}
