package com.solve.sudoku.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@RestController
public class SudokuController {
    public final static Integer[] ALL_VALID_ENTRY_NUMBER = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    public final static Integer[] COUNT_OF_NUMBERS_TO_BE_HIDDEN = {1};

    @CrossOrigin(origins = "*")
    @GetMapping("/sudoku")
    public int[][][] fillNonMatrixElementsIn9x9Matrix() {
        int[][] matrix9x9WithOnlyDiagonalElements = createBaseMatrixWithDiagonalElements();
        int[][] temporary9x9MatrixWithDiagonalElementsOnly = mapDiagonalMatrixInTempMatrix(matrix9x9WithOnlyDiagonalElements);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (temporary9x9MatrixWithDiagonalElementsOnly[i][j] == 0) {
                    Set<Integer> numberSetAllowedAtGivenPlace = new HashSet<>(Arrays.asList(ALL_VALID_ENTRY_NUMBER));
                    Set<Integer> numberSetPresentInRow = getSetOfNonMatrixNumberInARow(temporary9x9MatrixWithDiagonalElementsOnly, i);
                    Set<Integer> numberSetPresentInColumn = getSetOfNonMatrixNumbersInColumn(temporary9x9MatrixWithDiagonalElementsOnly, j);
                    Set<Integer> numberSetPresentInMatrix = getSetOfNumbersInGivenMatrix(temporary9x9MatrixWithDiagonalElementsOnly, i, j);
                    numberSetAllowedAtGivenPlace.removeAll(numberSetPresentInRow);
                    numberSetAllowedAtGivenPlace.removeAll(numberSetPresentInColumn);
                    numberSetAllowedAtGivenPlace.removeAll(numberSetPresentInMatrix);

                    if (!numberSetAllowedAtGivenPlace.isEmpty()) {
                        int numberToBePlacedAtGivenPosition = selectRandomElementFromGivenSetOfNumber(numberSetAllowedAtGivenPlace);
                        temporary9x9MatrixWithDiagonalElementsOnly[i][j] = numberToBePlacedAtGivenPosition;
                    } else {
                        temporary9x9MatrixWithDiagonalElementsOnly = mapDiagonalMatrixInTempMatrix(matrix9x9WithOnlyDiagonalElements);
                        i = 0;
                        j = -1;
                    }
                }
            }
        }

        int[][] solvedMatrixWithSomeRandomNumbersRemoved = getRandomNumberRemovedFromPrefilledMatrix(temporary9x9MatrixWithDiagonalElementsOnly);

        printArrayMatrix(temporary9x9MatrixWithDiagonalElementsOnly);
        System.out.println(" ");
        printArrayMatrix(solvedMatrixWithSomeRandomNumbersRemoved);
        System.out.println(" ");

        int[][][] solutionAndPuzzleArray = new int[2][9][9];
        solutionAndPuzzleArray[0] = temporary9x9MatrixWithDiagonalElementsOnly;
        solutionAndPuzzleArray[1] = solvedMatrixWithSomeRandomNumbersRemoved;

        return solutionAndPuzzleArray;
    }

    public Set<Integer> getSetOfNumbersInGivenMatrix(int[][] matrix9x9, int rowIndex, int columnIndex) {
        Set<Integer> setOfNumbersInGivenMatrix = new HashSet<>();
        int matrixRowStartIndex = getMatrixStartPosition(rowIndex);
        int matrixColumnStartIndex = getMatrixStartPosition(columnIndex);
        for (int i = matrixRowStartIndex; i < matrixRowStartIndex + 3; i++) {
            for (int j = matrixColumnStartIndex; j < matrixColumnStartIndex + 3; j++) {
                int elementAtGivenPosition = matrix9x9[i][j];
                if (elementAtGivenPosition != 0) {
                    setOfNumbersInGivenMatrix.add(elementAtGivenPosition);
                }
            }
        }

        return setOfNumbersInGivenMatrix;
    }

    public int getMatrixStartPosition(int index) {
        if (index < 3) {
            return 0;
        } else if (index < 6) {
            return 3;
        } else if (index < 9) {
            return 6;
        } else {
            throw new Error("Wrong indexing");
        }
    }

    public int[][] mapDiagonalMatrixInTempMatrix(int[][] diagonal9x9Matrix) {
        int[][] temporary9x9Matrix = new int[9][9];

        for (int i = 0; i < 9; i++) {
            System.arraycopy(diagonal9x9Matrix[i], 0, temporary9x9Matrix[i], 0, 9);
        }
        return temporary9x9Matrix;
    }

    public Set<Integer> getSetOfNonMatrixNumberInARow(int[][] matrix9x9, int rowStartIndex) {
        Set<Integer> setOfNumbersInARow = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            int numberAtGivenPosition = matrix9x9[rowStartIndex][i];
            if (numberAtGivenPosition != 0) {
                setOfNumbersInARow.add(numberAtGivenPosition);
            }
        }

        return setOfNumbersInARow;
    }

    public Set<Integer> getSetOfNonMatrixNumbersInColumn(int[][] matrix9x9, int columnStartIndex) {
        Set<Integer> setOfNumbersInAColumn = new HashSet<>();

        for (int i = 0; i < 9; i++) {
            int numberAtGivenPosition = matrix9x9[i][columnStartIndex];
            if (numberAtGivenPosition != 0) {
                setOfNumbersInAColumn.add(numberAtGivenPosition);
            }
        }

        return setOfNumbersInAColumn;
    }

    public int[][] createBaseMatrixWithDiagonalElements() {
        int[][] baseMatrixWithDiagonalElements = createNxNMatrix(9);
        int[][] topLeftDiagonalMatrix = createDiagonalMatrix();
        int[][] middleCenterDiagonalMatrix = createDiagonalMatrix();
        int[][] bottomRightDiagonalMatrix = createDiagonalMatrix();

        baseMatrixWithDiagonalElements = map3x3MatrixTo9x9Matrix(baseMatrixWithDiagonalElements, topLeftDiagonalMatrix, 0, 0);
        baseMatrixWithDiagonalElements = map3x3MatrixTo9x9Matrix(baseMatrixWithDiagonalElements, middleCenterDiagonalMatrix, 3, 3);
        baseMatrixWithDiagonalElements = map3x3MatrixTo9x9Matrix(baseMatrixWithDiagonalElements, bottomRightDiagonalMatrix, 6, 6);

        return baseMatrixWithDiagonalElements;
    }

    public int[][] map3x3MatrixTo9x9Matrix(int[][] matrix9x9, int[][] matrix3x3, int rowStartIndex, int columnStartIndex) {
        for (int i = rowStartIndex; i < rowStartIndex + 3; i++) {
            for (int j = columnStartIndex; j < columnStartIndex + 3; j++) {
                matrix9x9[i][j] = matrix3x3[getIthIndexFor3x3Matrix(i)][getIthIndexFor3x3Matrix(j)];
            }
        }

        return matrix9x9;
    }

    public int getIthIndexFor3x3Matrix(int index) {
        if (index < 3) return index;
        if (index < 6) return index - 3;
        return index - 6;
    }

    public int[][] createDiagonalMatrix() {
        int[][] sudoku3x3 = createNxNMatrix(3);
        Set<Integer> numbersToBeAdded = new HashSet<>(Arrays.asList(ALL_VALID_ENTRY_NUMBER));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int numberToBePlaced = selectRandomElementFromGivenSetOfNumber(numbersToBeAdded);
                sudoku3x3[i][j] = numberToBePlaced;
                numbersToBeAdded.remove(numberToBePlaced);
            }
        }

        return sudoku3x3;
    }

    public int selectRandomElementFromGivenSetOfNumber(Set<Integer> integerSet) {
        try {
            Integer[] numberSet = integerSet.toArray(Integer[]::new);
            int lengthOfNumberSet = numberSet.length;
            Random random = new Random();
            int randomArrayIndex = random.nextInt(lengthOfNumberSet);

            return numberSet[randomArrayIndex];
        } catch (Exception e) {
            return 0;
        }
    }

    public int[][] createNxNMatrix(int matrixSide) {
        int[][] matrixNxN = new int[matrixSide][matrixSide];

        for (int i = 0; i < matrixSide; i++) {
            for (int j = 0; j < matrixSide; j++) {
                matrixNxN[i][j] = 0;
            }
        }

        return matrixNxN;
    }

    public void printArrayMatrix(int[][] arrayMatrix) {
        int arrayMatrixY = arrayMatrix[0].length;

        for (int[] matrix : arrayMatrix) {
            for (int j = 0; j < arrayMatrixY; j++) {
                System.out.print(" " + matrix[j]);
            }
            System.out.println(" ");
        }
    }

    public int[][] getRandomNumberRemovedFromPrefilledMatrix(int[][] matrix9x9) {
        int[][] tempMatrix9x9 = createNxNMatrix(9);

        for (int i = 0; i < 9; i++) {
            Integer[] currentRow =  getIntArrayCastedToIntegerArray(matrix9x9[i]);

            int countOfNumbersToBeHiddenInARow = selectRandomElementFromGivenSetOfNumber(new HashSet<>(Arrays.asList(COUNT_OF_NUMBERS_TO_BE_HIDDEN)));

            Set<Integer> numbersToBeHidden =  getMRandomNumbersFromNNumbers(countOfNumbersToBeHiddenInARow, new HashSet<>(Arrays.asList(currentRow)));

            for (int j = 0; j < 9; j++) {
                tempMatrix9x9[i][j] = numbersToBeHidden.contains(matrix9x9[i][j]) ? 0 : matrix9x9[i][j];
            }
        }

        return tempMatrix9x9;
    }

    public Integer[] getIntArrayCastedToIntegerArray(int[] numberArray) {
        Integer[] integerArray = new Integer[numberArray.length];
        for(int i = 0; i < numberArray.length; i++) {
            integerArray[i] = getIntCastedToInteger(numberArray[i]);
        }

        return integerArray;
    }

    public Integer getIntCastedToInteger(int n) {
        return n;
    }

    public Set<Integer> getMRandomNumbersFromNNumbers(int M, Set<Integer> numberSet) {
        Set<Integer> randomNumbers = new HashSet<>();

        while(randomNumbers.size() < M) {
            randomNumbers.add(selectRandomElementFromGivenSetOfNumber(numberSet));
        }

        return randomNumbers;
    }
}


