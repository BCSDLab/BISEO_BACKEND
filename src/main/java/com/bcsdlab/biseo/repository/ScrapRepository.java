package com.bcsdlab.biseo.repository;

import com.bcsdlab.biseo.dto.scrap.ScrapModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScrapRepository {

    void createScrap(ScrapModel scrapModel);
}
