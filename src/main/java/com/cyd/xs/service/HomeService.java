package com.cyd.xs.service;

import com.cyd.xs.dto.Home.HomeDTO;
import com.cyd.xs.dto.Home.RecommendRefreshDTO;
import com.cyd.xs.dto.Search.SearchDTO;

import java.util.List;

public interface HomeService {

    /**
     * 获取首页数据
     */
    HomeDTO getHomeData(String userId);

    /**
     * 推荐内容换一批（新版本，带分页）
     */
    RecommendRefreshDTO refreshRecommend(String userId, Integer pageNum, Integer pageSize);

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
     * 首页推荐内容刷新（旧版本，保持兼容）
     */
    List<HomeDTO.RecommendContent> refreshRecommend(String userId, Integer pageSize);
}