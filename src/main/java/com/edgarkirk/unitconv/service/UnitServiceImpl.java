package com.edgarkirk.unitconv.service;

import java.util.List;

import com.edgarkirk.unitconv.api.dto.response.Unit;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public List<Unit> listUnits() {
        throw new UnsupportedOperationException("Unit service is not implemented yet");
    }
}
