package com.edgarkirk.unitconv.service;

import java.util.List;

import com.edgarkirk.unitconv.api.dto.response.UnitResponse;

public interface UnitService {

    List<UnitResponse> findAll();
}
