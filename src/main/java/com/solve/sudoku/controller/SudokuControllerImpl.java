package com.solve.sudoku.controller;

import com.solve.sudoku.service.SudokuSolvingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SudokuControllerImpl implements SudokuController {

    @Autowired
    SudokuSolvingService sudokuSolvingService;

    @Override
    public int[][][] fillNonMatrixElementsIn9x9Matrix() {
        return sudokuSolvingService.fillNonMatrixElementsIn9x9Matrix();
    }
}
