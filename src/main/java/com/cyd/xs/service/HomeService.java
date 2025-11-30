package com.cyd.xs.service;

import com.cyd.xs.dto.user.Home.HomeDTO;
import com.cyd.xs.dto.user.Search.SearchDTO;

import java.util.List;

public interface HomeService {

    /**
     * 首次进入APP - 身份标签选择
     */
    HomeDTO selectIdentity(String identityType, String userId);

    /**
     * 全域搜索
     */
    SearchDTO search(String keyword, String tabType, Integer page, Integer pageSize, String userId);

    /**
     * 清除搜索历史
     */
    void clearSearchHistory(String userId);

    /**
     * 首页推荐内容刷新
     */
    List<HomeDTO.RecommendContent> refreshRecommend(String userId, Integer pageSize);

    HomeDTO getHomeData(String userId);
}