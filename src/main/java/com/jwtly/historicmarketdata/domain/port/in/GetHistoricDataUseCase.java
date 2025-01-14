package com.jwtly.historicmarketdata.domain.port.in;

import com.jwtly.historicmarketdata.domain.GetHistoricDataQuery;
import com.jwtly.historicmarketdata.domain.model.OHCLV;

import java.util.List;

public interface GetHistoricDataUseCase {
    List<OHCLV> getHistoricData(GetHistoricDataQuery query);
}
