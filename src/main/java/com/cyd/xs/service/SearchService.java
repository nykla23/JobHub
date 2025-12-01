package com.cyd.xs.service;

import com.cyd.xs.dto.Search.SearchDTO;

public interface SearchService {
    SearchDTO getSearchHistoryAndHot(String userId);

    void clearSearchHistory(String userId);

    SearchDTO search(String keyword, String type, String sort, Integer pageNum, Integer pageSize, String userId);
}
