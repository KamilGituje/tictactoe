package com.ttt.service;

import com.ttt.model.PlayTable;
import com.ttt.model.SingleField;
import com.ttt.model.SingleFieldStatus;
import com.ttt.model.SingleMove;
import com.ttt.model.dto.PlayTableForCreationDto;
import com.ttt.repository.PlayTableRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.springframework.http.HttpStatus.*;

@Service
public class PlayTableService {
    public PlayTableService(PlayTableRepository _playTableRepository) {
        this.playTableRepository = _playTableRepository;
    }

    private final PlayTableRepository playTableRepository;

    public PlayTable getPlayTable(int playTableId) {
        return playTableRepository.findById(playTableId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public PlayTable addPlayTable(PlayTable playTable) {
        return playTableRepository.save(playTable);
    }

    @Transactional
    public PlayTable updatePlayTable(PlayTable playTable) {
        var playTableToUpdate = getPlayTable(playTable.getPlayTableId());
        playTableToUpdate.setSingleFields(playTable.getSingleFields());
        return playTableRepository.save(playTableToUpdate);
    }

    public void deletePlayTable(int playTableId) {
        playTableRepository.deleteById(playTableId);
    }

    public PlayTable createPlayTable(PlayTableForCreationDto playTableForCreationDto) {
        if (playTableForCreationDto.getEdgeSize() < playTableForCreationDto.getWinningRowSize()) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        var playTable = new PlayTable();
        var singleFields = new ArrayList<SingleField>();
        for (int i = 0; i < (playTableForCreationDto.getEdgeSize() * playTableForCreationDto.getEdgeSize()); i++) {
            var singleFieldToAdd = new SingleField();
            singleFieldToAdd.setPosition(i);
            singleFieldToAdd.setPlayTable(playTable);
            singleFields.add(singleFieldToAdd);
        }
        playTable.setSingleFields(singleFields);
        playTable.setCpuSide(playTableForCreationDto.getCpuSide());
        playTable.setWinningRowSize(playTableForCreationDto.getWinningRowSize());
        return addPlayTable(playTable);
    }

    public List<SingleField> makeMove(SingleMove singleMove) {
        var playTable = getPlayTable(singleMove.getPlayTableId());
        if (playTable.getSingleFields().get(singleMove.getSingleFieldPosition()).getStatus() == SingleFieldStatus.blank) {
            playTable.getSingleFields().get(singleMove.getSingleFieldPosition()).setStatus(singleMove.getSingleFieldStatus());
        } else {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        updatePlayTable(playTable);
        var boardAfterWinCheck = isWin(playTable, singleMove.getSingleFieldPosition());
        if (boardAfterWinCheck != null) {
            deletePlayTable(playTable.getPlayTableId());
            return boardAfterWinCheck;
        }
        var blankFields = playTable.getSingleFields().stream().filter(sf -> sf.getStatus() == SingleFieldStatus.blank).toList();
        if (blankFields.isEmpty()) {
            deletePlayTable(playTable.getPlayTableId());
            return playTable.getSingleFields();
        }
        var randomFieldPosition = new Random().nextInt(0, blankFields.size() - 1);
        blankFields.get(randomFieldPosition).setStatus(SingleFieldStatus.valueOf(playTable.getCpuSide().toString()));
        playTable.getSingleFields().set(blankFields.stream().filter(bf -> bf.getPosition() == blankFields.get(randomFieldPosition).getPosition()).findFirst().orElseThrow().getPosition(), blankFields.get(randomFieldPosition));
        updatePlayTable(playTable);
        boardAfterWinCheck = isWin(playTable, blankFields.get(randomFieldPosition).getPosition());
        if (boardAfterWinCheck != null) {
            deletePlayTable(playTable.getPlayTableId());
            return boardAfterWinCheck;
        }
        return playTable.getSingleFields();
    }

    private ArrayList<List<SingleField>> singleFieldsToMatrix(List<SingleField> singleFields) {
        var edgeSize = (int) Math.sqrt(singleFields.size());
        var matrixedSingleFields = new ArrayList<List<SingleField>>();
        for (int i = 0; i < edgeSize; i++) {
            matrixedSingleFields.add(singleFields.subList(i * edgeSize, i * edgeSize + edgeSize));
        }
        return matrixedSingleFields;
    }

    private List<SingleField> isWin(PlayTable playTable, int chosenPosition) {
        var winningRowCount = playTable.getWinningRowSize();
        var matrixedSingleFields = singleFieldsToMatrix(playTable.getSingleFields());
        var chosenPositionX = chosenPosition % matrixedSingleFields.size();
        var chosenPositionY = (int) Math.floor(chosenPosition / matrixedSingleFields.size());
        var boardAfterHorizontalCheck = isWinHorizontal(matrixedSingleFields, chosenPositionX, chosenPositionY, winningRowCount);
        if (boardAfterHorizontalCheck != null) {
            return boardAfterHorizontalCheck;
        }
        var boardAfterVerticalCheck = isWinVertical(matrixedSingleFields, chosenPositionX, chosenPositionY, winningRowCount);
        if (boardAfterVerticalCheck != null) {
            return boardAfterVerticalCheck;
        }
        var boardAfterDiagonalRightCheck = isWinDiagonalRight(matrixedSingleFields, chosenPositionX, chosenPositionY, winningRowCount);
        if (boardAfterDiagonalRightCheck != null) {
            return boardAfterDiagonalRightCheck;
        }
        var boardAfterDiagonalLeftCheck = isWinDiagonalLeft(matrixedSingleFields, chosenPositionX, chosenPositionY, winningRowCount);
        if (boardAfterDiagonalLeftCheck != null) {
            return boardAfterDiagonalLeftCheck;
        }
        return null;
    }

    private List<SingleField> isWinHorizontal(List<List<SingleField>> matrixedSingleFields, int chosenPositionX, int chosenPositionY, int winningRowCount) {
        var sameStatusFieldsInRow = new ArrayList<SingleField>();
        var startIndex = chosenPositionX - (winningRowCount - 1);
        if (startIndex < 0) {
            startIndex = 0;
        }
        var lineLength = (winningRowCount * 2) - 1;
        if (startIndex + lineLength > matrixedSingleFields.size()) {
            lineLength = matrixedSingleFields.size() - startIndex;
        }
        var previousStatus = matrixedSingleFields.get(chosenPositionY).get(startIndex).getStatus();
        for (int i = startIndex; i < lineLength + startIndex; i++) {
            if (matrixedSingleFields.get(chosenPositionY).get(i).getStatus() == previousStatus && matrixedSingleFields.get(chosenPositionY).get(i).getStatus() != SingleFieldStatus.blank) {
                sameStatusFieldsInRow.add(matrixedSingleFields.get(chosenPositionY).get(i));
            } else if (matrixedSingleFields.get(chosenPositionY).get(i).getStatus() != previousStatus && matrixedSingleFields.get(chosenPositionY).get(i).getStatus() != SingleFieldStatus.blank) {
                sameStatusFieldsInRow.clear();
                sameStatusFieldsInRow.add(matrixedSingleFields.get(chosenPositionY).get(i));
            } else {
                sameStatusFieldsInRow.clear();
            }
            previousStatus = matrixedSingleFields.get(chosenPositionY).get(i).getStatus();
            if (sameStatusFieldsInRow.size() == winningRowCount) {
                return sameStatusFieldsInRow;
            }
        }
        return null;
    }

    private List<SingleField> isWinVertical(List<List<SingleField>> matrixedSingleFields, int chosenPositionX, int chosenPositionY, int winningRowCount) {
        var sameStatusFieldsInRow = new ArrayList<SingleField>();
        var startIndex = chosenPositionY - (winningRowCount - 1);
        if (startIndex < 0) {
            startIndex = 0;
        }
        var lineLength = (winningRowCount * 2) - 1;
        if (startIndex + lineLength > matrixedSingleFields.size()) {
            lineLength = matrixedSingleFields.size() - startIndex;
        }
        var previousStatus = matrixedSingleFields.get(startIndex).get(chosenPositionX).getStatus();
        for (int i = startIndex; i < lineLength + startIndex; i++) {
            {
                if (matrixedSingleFields.get(i).get(chosenPositionX).getStatus() == previousStatus && matrixedSingleFields.get(i).get(chosenPositionX).getStatus() != SingleFieldStatus.blank) {
                    sameStatusFieldsInRow.add(matrixedSingleFields.get(i).get(chosenPositionX));
                } else if (matrixedSingleFields.get(i).get(chosenPositionX).getStatus() != previousStatus && matrixedSingleFields.get(i).get(chosenPositionX).getStatus() != SingleFieldStatus.blank) {
                    sameStatusFieldsInRow.clear();
                    sameStatusFieldsInRow.add(matrixedSingleFields.get(i).get(chosenPositionX));
                } else {
                    sameStatusFieldsInRow.clear();
                }
                previousStatus = matrixedSingleFields.get(i).get(chosenPositionX).getStatus();
                if (sameStatusFieldsInRow.size() == winningRowCount) {
                    return sameStatusFieldsInRow;
                }
            }
        }
        return null;
    }

    private List<SingleField> isWinDiagonalRight(List<List<SingleField>> matrixedSingleFields, int chosenPositionX, int chosenPositionY, int winningRowCount) {
        var sameStatusFieldsInRow = new ArrayList<SingleField>();
        var startIndexX = chosenPositionX - (winningRowCount - 1);
        var startIndexY = chosenPositionY - (winningRowCount - 1);
        if (startIndexX < 0) {
            startIndexY = startIndexY + Math.abs(startIndexX);
            startIndexX = 0;
        }
        if (startIndexY < 0) {
            startIndexX = startIndexX + Math.abs(startIndexY);
            startIndexY = 0;
        }
        var lineLength = (winningRowCount * 2) - 1;
        if (startIndexX + lineLength > matrixedSingleFields.size()) {
            lineLength = matrixedSingleFields.size() - startIndexX;
        }
        if (startIndexY + lineLength > matrixedSingleFields.size()) {
            lineLength = matrixedSingleFields.size() - startIndexY;
        }

        var previousStatus = matrixedSingleFields.get(startIndexY).get(startIndexX).getStatus();
        int j = startIndexX;
        for (int i = startIndexY; i < lineLength + startIndexY; i++) {
            if (matrixedSingleFields.get(i).get(j).getStatus() == previousStatus && matrixedSingleFields.get(i).get(j).getStatus() != SingleFieldStatus.blank) {
                sameStatusFieldsInRow.add(matrixedSingleFields.get(i).get(j));
            } else if (matrixedSingleFields.get(i).get(j).getStatus() != previousStatus && matrixedSingleFields.get(i).get(j).getStatus() != SingleFieldStatus.blank) {
                sameStatusFieldsInRow.clear();
                sameStatusFieldsInRow.add(matrixedSingleFields.get(i).get(j));
            } else {
                sameStatusFieldsInRow.clear();
            }
            previousStatus = matrixedSingleFields.get(i).get(j).getStatus();
            if (sameStatusFieldsInRow.size() == winningRowCount) {
                return sameStatusFieldsInRow;
            }
            j++;
        }
        return null;
    }

    private List<SingleField> isWinDiagonalLeft(List<List<SingleField>> matrixedSingleFields, int chosenPositionX, int chosenPositionY, int winningRowCount) {
        var sameStatusFieldsInRow = new ArrayList<SingleField>();
        var startIndexX = chosenPositionX + (winningRowCount - 1);
        var startIndexY = chosenPositionY - (winningRowCount - 1);
        if (startIndexX > matrixedSingleFields.size() - 1) {
            startIndexY = startIndexY + (startIndexX - (matrixedSingleFields.size() - 1));
            startIndexX = matrixedSingleFields.size() - 1;
        }
        if (startIndexY < 0) {
            startIndexX = startIndexX - Math.abs(startIndexY);
            startIndexY = 0;
        }
        var lineLength = (winningRowCount * 2) - 1;
        if (startIndexX - lineLength < 0) {
            lineLength = startIndexX + 1;
        }
        if (startIndexY + lineLength > matrixedSingleFields.size()) {
            lineLength = matrixedSingleFields.size() - startIndexY;
        }
        var previousStatus = matrixedSingleFields.get(startIndexY).get(startIndexX).getStatus();
        int j = startIndexX;
        for (int i = startIndexY; i < lineLength + startIndexY; i++) {
            if (matrixedSingleFields.get(i).get(j).getStatus() == previousStatus && matrixedSingleFields.get(i).get(j).getStatus() != SingleFieldStatus.blank) {
                sameStatusFieldsInRow.add(matrixedSingleFields.get(i).get(j));
            } else if (matrixedSingleFields.get(i).get(j).getStatus() != previousStatus && matrixedSingleFields.get(i).get(j).getStatus() != SingleFieldStatus.blank) {
                sameStatusFieldsInRow.clear();
                sameStatusFieldsInRow.add(matrixedSingleFields.get(i).get(j));
            } else {
                sameStatusFieldsInRow.clear();
            }
            previousStatus = matrixedSingleFields.get(i).get(j).getStatus();
            if (sameStatusFieldsInRow.size() == winningRowCount) {
                return sameStatusFieldsInRow;
            }
            j--;
        }
        return null;
    }
}