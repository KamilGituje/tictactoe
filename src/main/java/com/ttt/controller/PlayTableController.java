package com.ttt.controller;

import com.ttt.model.PlayTable;
import com.ttt.model.SingleField;
import com.ttt.model.SingleMove;
import com.ttt.model.dto.PlayTableForCreationDto;
import com.ttt.service.PlayTableService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("/tables")
@RestController
public class PlayTableController {
    public PlayTableController(PlayTableService _playTableService) {
        this.playTableService = _playTableService;
    }

    private final PlayTableService playTableService;

    @GetMapping("/{playTableId}")
    public PlayTable getPlayTable(@PathVariable int playTableId) {
        return playTableService.getPlayTable(playTableId);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public PlayTable createPlayTable(@RequestBody PlayTableForCreationDto playTable) {
        return playTableService.createPlayTable(playTable);
    }

    @PutMapping("/move")
    public List<SingleField> makeMove(@RequestBody SingleMove singleMove) {
        return playTableService.makeMove(singleMove);
    }

    @DeleteMapping("/delete/{playTableId}")
    public void deletePlayTable(@PathVariable int playTableId) {
        playTableService.deletePlayTable(playTableId);
    }
}
