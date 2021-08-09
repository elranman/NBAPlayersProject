package com.test.nba.nbaInfo.controller;

import com.test.nba.nbaInfo.domain.Player;
import com.test.nba.nbaInfo.service.NbaDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class NbaController {

    @Autowired
    NbaDetailsService nbaDetailsService;

    @GetMapping("/player")
    public ResponseEntity<List<Player>> getPlayerDetails()  {
        try {
            return new ResponseEntity<>(nbaDetailsService.getPlayerDetails(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
