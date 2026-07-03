package com.edgarkirk.unitconv.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.UnitService;

@Service
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public List<UnitResponse> findAll() {
        return List.of();
    }
}
