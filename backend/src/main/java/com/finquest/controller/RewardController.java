package com.finquest.controller;
import com.finquest.service.RewardService; import lombok.RequiredArgsConstructor; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*; import java.time.Year; import java.time.YearMonth; import java.util.*;
@RestController @RequestMapping("/api/rewards") @RequiredArgsConstructor
public class RewardController {
 private final RewardService service;
 @GetMapping public ResponseEntity<?> rewards(@RequestParam Long userId){return ResponseEntity.ok(service.rewards(userId));}
 @GetMapping("/history") public ResponseEntity<?> history(@RequestParam Long userId){return ResponseEntity.ok(service.history(userId));}
 @GetMapping("/streak") public ResponseEntity<?> streak(@RequestParam Long userId){return ResponseEntity.ok(service.rewards(userId));}
 @GetMapping("/monthly") public ResponseEntity<?> monthly(@RequestParam Long userId,@RequestParam(required=false) Integer year,@RequestParam(required=false) Integer month){YearMonth now=YearMonth.now();return ResponseEntity.ok(service.monthly(userId,year==null?now.getYear():year,month==null?now.getMonthValue():month));}
 @GetMapping("/yearly") public ResponseEntity<?> yearly(@RequestParam Long userId,@RequestParam(required=false) Integer year){return ResponseEntity.ok(service.yearly(userId,year==null?Year.now().getValue():year));}
}
