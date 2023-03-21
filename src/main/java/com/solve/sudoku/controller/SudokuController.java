package com.solve.sudoku.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface SudokuController {

    @CrossOrigin(origins = "*")
    @GetMapping("/sudoku")
    public int[][][] fillNonMatrixElementsIn9x9Matrix();

}


