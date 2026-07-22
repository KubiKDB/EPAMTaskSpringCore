package com.daniel.taskspringcore.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daniel.taskspringcore.dao.TrainingTypeDAO;
import com.daniel.taskspringcore.dto.DtoMapper;
import com.daniel.taskspringcore.dto.TrainingTypeDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrainingTypeService {

    private final TrainingTypeDAO trainingTypeDAO;

    public TrainingTypeService(TrainingTypeDAO trainingTypeDAO) {
        this.trainingTypeDAO = trainingTypeDAO;
    }

    @Transactional(readOnly = true)
    public List<TrainingTypeDTO> getAll() {
        log.debug("Fetching all training types");
        return trainingTypeDAO.findAll().stream().map(DtoMapper::toDto).toList();
    }
}
