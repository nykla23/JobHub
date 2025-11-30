package com.cyd.xs.service.Impl;

import cn.hutool.json.JSONUtil;
import com.cyd.xs.dto.user.Home.HomeDTO;
import com.cyd.xs.dto.user.Search.SearchDTO;
import com.cyd.xs.entity.User.HomeContent.HomeContent;
import com.cyd.xs.entity.User.Topic.Topic;
import com.cyd.xs.entity.User.User;
import com.cyd.xs.entity.User.UserProfile;
import com.cyd.xs.mapper.*;
import com.cyd.xs.service.HomeService;
import com.cyd.xs.util.IDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final HomeContentMapper homeContentMapper;
    private final TopicMapper topicMapper;
    private final SearchHistoryMapper searchHistoryMapper;
    private final CarouselMapper carouselMapper;
    private final HotActivityMapper hotActivityMapper;
    private final UserMapper userMapper;

    @Override
    public HomeDTO getHomeData(String userId) {
        log.info("用户 {} 获取首页数据", userId);

        HomeDTO homeDTO = new HomeDTO();

        try {
            // 获取用户信息和身份
            User user = userMapper.selectById(Long.valueOf(userId));
            if (user != null) {
                // 解析用户身份信息
                UserProfile profile = JSONUtil.toBean(user.getProfileJson(), UserProfile.class);
                homeDTO.setUserIdentity(profile.getCareerStage());
            } else {
                homeDTO.setUserIdentity("student"); // 默认身份
            }

            // 获取轮播图
            var carousels = carouselMapper.findActiveCarousels(5);
            homeDTO.setCarousel(carousels.stream().map(carousel -> {
                HomeDTO.Carousel c = new HomeDTO.Carousel();
                c.setId(carousel.getId());
                c.setTitle(carousel.getTitle());
                c.setImageUrl(carousel.getImageUrl());
                c.setDesc(carousel.getDescription());
                c.setLink(carousel.getLink());
                return c;
            }).collect(Collectors.toList()));

            // 获取热门活动
            var hotActivities = hotActivityMapper.findHotActivities(3);
            homeDTO.setHotActivities(hotActivities.stream().map(activity -> {
                HomeDTO.HotActivity ha = new HomeDTO.HotActivity();
                ha.setId(activity.getId());
                ha.setTitle(activity.getTitle());
                ha.setTime(activity.getTime());
                ha.setParticipantCount(activity.getParticipantCount());
                ha.setLink(activity.getLink());
                return ha;
            }).collect(Collectors.toList()));

            // 获取推荐内容
            var contents = homeContentMapper.findRecommendedContents(5);
            HomeDTO.RecommendedContent recommendedContent = new HomeDTO.RecommendedContent();
            recommendedContent.setTotal(homeContentMapper.countPublishedContents());
            recommendedContent.setPageNum(1);
            recommendedContent.setPageSize(5);
            recommendedContent.setList(contents.stream().map(content -> {
                HomeDTO.ContentItem item = new HomeDTO.ContentItem();
                item.setId(content.getId());
                item.setTitle(content.getTitle());
                item.setType(content.getContentType());
                item.setAuthor(content.getAuthorName());
                item.setLikeCount(content.getLikeCount() != null ? content.getLikeCount() : 0);
                item.setCollectCount(content.getCollectCount() != null ? content.getCollectCount() : 0);
                item.setPublishTime(content.getCreatedAt() != null ? content.getCreatedAt().toString() : "2025-05-18 10:00:00");
                item.setLink("/api/v1/content/" + content.getId());
                return item;
            }).collect(Collectors.toList()));
            homeDTO.setRecommendedContent(recommendedContent);

            return homeDTO;
        } catch (Exception e) {
            log.error("获取首页数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取首页数据失败");
        }
    }

    @Override
    public RecommendRefreshDTO refreshRecommend(String userId, Integer pageNum, Integer pageSize) {
        log.info("用户 {} 刷新推荐内容, 页码: {}, 条数: {}", userId, pageNum, pageSize);

        try {
            int offset = (pageNum - 1) * pageSize;
            var contents = homeContentMapper.findRecommendedContentsByPage(offset, pageSize);

            RecommendRefreshDTO result = new RecommendRefreshDTO();
            result.setTotal(homeContentMapper.countPublishedContents());
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
            result.setList(contents.stream().map(content -> {
                RecommendRefreshDTO.ContentItem item = new RecommendRefreshDTO.ContentItem();
                item.setId(content.getId());
                item.setTitle(content.getTitle());
                item.setType(content.getContentType());
                item.setAuthor(content.getAuthorName());
                item.setLikeCount(content.getLikeCount() != null ? content.getLikeCount() : 0);
                item.setCollectCount(content.getCollectCount() != null ? content.getCollectCount() : 0);
                item.setPublishTime(content.getCreatedAt() != null ? content.getCreatedAt().toString() : "2025-05-18 10:00:00");
                item.setLink("/api/v1/content/" + content.getId());
                return item;
            }).collect(Collectors.toList()));

            return result;
        } catch (Exception e) {
            log.error("刷新推荐内容失败: {}", e.getMessage(), e);
            throw new RuntimeException("刷新推荐内容失败");
        }
    }

    @Override
    public HomeDTO selectIdentity(String identityType, String userId) {
        log.info("用户 {} 选择身份标签: {}", userId, identityType);

        HomeDTO homeDTO = new HomeDTO();

        try {
            // 获取推荐内容
            List<HomeContent> contents = homeContentMapper.findRecentContents(10);
            homeDTO.setRecommendContent(contents.stream().map(content -> {
                HomeDTO.RecommendContent rc = new HomeDTO.RecommendContent();
                rc.setContentId(content.getId());
                rc.setTitle(content.getTitle());
                rc.setType(content.getContentType());
                rc.setAuthor(content.getAuthorName());
                rc.setViewCount(content.getViewCount());
                return rc;
            }).collect(Collectors.toList()));

            // 获取社区话题
            List<Topic> topics = topicMapper.findTopicsByCondition(null, "hot", 0, 5);
            homeDTO.setCommunityTopics(topics.stream().map(topic -> {
                HomeDTO.CommunityTopic ct = new HomeDTO.CommunityTopic();
                ct.setTopicId(topic.getId());
                ct.setTitle(topic.getTitle());
                ct.setParticipantCount(topic.getParticipantCount());
                return ct;
            }).collect(Collectors.toList()));

            // 获取活动列表（这里简化处理）
            homeDTO.setActivities(List.of(
                    createActivity("a3001", "春招冲刺直播", "2025-12-10 19:00"),
                    createActivity("a3002", "简历优化工作坊", "2025-12-15 14:00")
            ));

            return homeDTO;
        } catch (Exception e) {
            log.error("身份标签选择失败: {}", e.getMessage(), e);
            throw new RuntimeException("身份标签选择失败");
        }
    }

    @Override
    public SearchDTO search(String keyword, String tabType, Integer page, Integer pageSize, String userId) {
        log.info("用户 {} 搜索关键词: {}, 标签类型: {}", userId, keyword, tabType);

        SearchDTO searchDTO = new SearchDTO();

        try {
            // 记录搜索历史
            searchHistoryMapper.saveSearchHistory(userId, keyword);

            // 根据tabType返回不同结果
            if (tabType == null || "topic".equals(tabType)) {
                List<Topic> topics = topicMapper.findTopicsByCondition(null, "comprehensive", 0, 10);
                searchDTO.setTopic(topics.stream().map(topic -> {
                    SearchDTO.TopicResult tr = new SearchDTO.TopicResult();
                    tr.setTopicId(topic.getId());
                    tr.setTitle(topic.getTitle());
                    tr.setParticipantCount(topic.getParticipantCount());
                    return tr;
                }).collect(Collectors.toList()));
            }

            if (tabType == null || "content".equals(tabType)) {
                List<HomeContent> contents = homeContentMapper.findRecentContents(10);
                searchDTO.setContent(contents.stream().map(content -> {
                    SearchDTO.ContentResult cr = new SearchDTO.ContentResult();
                    cr.setContentId(content.getId());
                    cr.setTitle(content.getTitle());
                    cr.setAuthor(content.getAuthorName());
                    return cr;
                }).collect(Collectors.toList()));
            }

            // 热门关键词
            searchDTO.setHotKeywords(Arrays.asList("秋招面试", "简历优化", "offer选择", "职场新人", "实习经验"));

            // 历史搜索记录
            List<String> histories = searchHistoryMapper.findRecentSearchHistory(userId, 10);
            searchDTO.setHistoryKeywords(histories);

            return searchDTO;
        } catch (Exception e) {
            log.error("搜索失败: {}", e.getMessage(), e);
            throw new RuntimeException("搜索失败");
        }
    }

    @Override
    @Transactional
    public void clearSearchHistory(String userId) {
        log.info("用户 {} 清除搜索历史", userId);

        try {
            int deletedCount = searchHistoryMapper.deleteByUserId(userId);
            log.info("用户 {} 清除了 {} 条搜索历史", userId, deletedCount);
        } catch (Exception e) {
            log.error("清除搜索历史失败: {}", e.getMessage(), e);
            throw new RuntimeException("清除搜索历史失败");
        }
    }

    @Override
    public List<HomeDTO.RecommendContent> refreshRecommend(String userId, Integer pageSize) {
        log.info("用户 {} 刷新推荐内容, 条数: {}", userId, pageSize);

        try {
            if (pageSize == null) pageSize = 20;

            List<HomeContent> contents = homeContentMapper.findRecentContents(pageSize);
            return contents.stream().map(content -> {
                HomeDTO.RecommendContent rc = new HomeDTO.RecommendContent();
                rc.setContentId(content.getId());
                rc.setTitle(content.getTitle());
                rc.setType(content.getContentType());
                rc.setAuthor(content.getAuthorName());
                rc.setViewCount(content.getViewCount());
                rc.setInteractiveCount(content.getInteractiveCount());
                return rc;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("刷新推荐内容失败: {}", e.getMessage(), e);
            throw new RuntimeException("刷新推荐内容失败");
        }
    }

    private HomeDTO.Activity createActivity(String activityId, String title, String time) {
        HomeDTO.Activity activity = new HomeDTO.Activity();
        activity.setActivityId(activityId);
        activity.setTitle(title);
        activity.setTime(time);
        return activity;
    }
}
