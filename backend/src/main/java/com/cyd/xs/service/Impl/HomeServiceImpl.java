package com.cyd.xs.service.Impl;

import com.cyd.xs.dto.Home.HomeDTO;
import com.cyd.xs.dto.Home.RecommendRefreshDTO;
import com.cyd.xs.entity.Home.Activity;
import com.cyd.xs.entity.User.UserContent;
import com.cyd.xs.mapper.Home.CarouselMapper;
import com.cyd.xs.mapper.Home.HotActivityMapper;
import com.cyd.xs.mapper.UserContentMapper;
import com.cyd.xs.mapper.UserMapper;
import com.cyd.xs.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import com.cyd.xs.service.UserService;
import com.cyd.xs.mapper.Topic.TopicMapper;
import com.cyd.xs.entity.Topic.Topic;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final CarouselMapper carouselMapper;
    private final HotActivityMapper hotActivityMapper;
    private final UserMapper userMapper;
    private final UserContentMapper userContentMapper;
    private final UserService userService;
    private final TopicMapper topicMapper;

    // =====================================================
    // 首页数据
    // =====================================================
    // ⭐ 固定配置
    private static final int RECOMMEND_PAGE_SIZE = 2;

    @Override
    public HomeDTO getHomeData(Long userId) {

        HomeDTO homeDTO = new HomeDTO();

        // 1️⃣ 中文身份
        String identityTag = null;
        if (userId != null) {
            identityTag = userService.getIdentityTag(userId);
        }
        homeDTO.setUserIdentity(identityTag);

        // 2️⃣ 轮播 & 热门活动
        homeDTO.setCarousel(getCarouselData());
        homeDTO.setHotActivities(getHotActivities());

        // 3️⃣ ⭐ 话题推荐（核心）
        if (identityTag != null) {

            List<String> tags =
                    RECOMMEND_TAG_MAP.getOrDefault(identityTag, List.of());

            if (!tags.isEmpty()) {

                // ① 先把「所有符合身份标签的话题」查出来
                List<Topic> allTopics =
                        topicMapper.findRecommendedTopicsByTags(tags, 100);

                if (!allTopics.isEmpty()) {
                    log.info("【推荐前】topics = {}",
                            allTopics.stream().map(Topic::getId).toList());
                    // ② 打乱顺序（关键！）
                    Collections.shuffle(allTopics);

                    log.info("【推荐后】topics = {}",
                            allTopics.stream().map(Topic::getId).toList());
                    // ③ 只取前 2 条
                    List<Topic> picked =
                            allTopics.stream().limit(2).toList();

                    List<HomeDTO.TopicRecommendItem> topicItems =
                            picked.stream().map(t -> {
                                HomeDTO.TopicRecommendItem item =
                                        new HomeDTO.TopicRecommendItem();
                                item.setId(t.getId());
                                item.setTitle(t.getTitle());
                                item.setTag(t.getTag());
                                item.setLevel(t.getLevel());
                                item.setParticipantCount(t.getParticipantCount());
                                item.setInteractiveCount(t.getInteractiveCount());
                                return item;
                            }).toList();

                    homeDTO.setTopicRecommend(topicItems);
                }
            }
        }

        return homeDTO;
    }






    // =====================================================
    // 为你推荐内容（首页内容流）
    // =====================================================
    private HomeDTO.RecommendedContent getHomeRecommendedContent(
            String userIdentity, Integer pageNum, Integer pageSize) {

        int offset = (pageNum - 1) * pageSize;

        List<String> tags =
                RECOMMEND_TAG_MAP.getOrDefault(userIdentity, List.of());

        List<UserContent> contents;

        if (!tags.isEmpty()) {
            // ⭐ 按“推荐标签”查内容
            contents = userContentMapper
                    .findRecommendedContentsByTags(tags, pageSize);
        } else {
            contents = userContentMapper
                    .findRecommendedContentsByPage(offset, pageSize);
        }

        HomeDTO.RecommendedContent result = new HomeDTO.RecommendedContent();
        result.setTotal((long) contents.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        List<HomeDTO.ContentItem> list = contents.stream().map(content -> {
            HomeDTO.ContentItem item = new HomeDTO.ContentItem();
            item.setId(content.getId());
            item.setTitle(content.getTitle());
            item.setType(content.getType());
            item.setAuthor(content.getAuthorName());
            item.setAvatarUrl(content.getAvatarUrl());
            item.setLikeCount(content.getLikeCount());
            item.setCollectCount(content.getCollectCount());
            item.setCommentCount(content.getCommentCount());
            item.setSummary(content.getSummary());
            item.setCoverImage(content.getCoverImage());
            item.setPublishTime(
                    content.getCreatedAt() != null
                            ? content.getCreatedAt()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            : ""
            );
            item.setLink("/api/v1/content/" + content.getId());
            return item;
        }).collect(Collectors.toList());

        result.setList(list);
        return result;
    }


    // =====================================================
    // 刷新推荐（接口方法，必须实现）
    // =====================================================
    @Override
    public RecommendRefreshDTO refreshRecommend(
            Long userId, Integer pageNum, Integer pageSize) {

        log.info("用户 {} 刷新推荐内容, pageNum={}, pageSize={}",
                userId, pageNum, pageSize);

        int offset = (pageNum - 1) * pageSize;

        List<UserContent> contents =
                userContentMapper.findRecommendedContentsByPage(offset, pageSize);

        Long total = userContentMapper.countPublishedContents();

        RecommendRefreshDTO result = new RecommendRefreshDTO();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        List<RecommendRefreshDTO.ContentItem> list =
                contents.stream().map(content -> {
                    RecommendRefreshDTO.ContentItem item =
                            new RecommendRefreshDTO.ContentItem();
                    item.setId(content.getId());
                    item.setTitle(content.getTitle());
                    item.setType(content.getType());
                    item.setAuthor(content.getAuthorName());
                    item.setAvatarUrl(content.getAvatarUrl());
                    item.setLikeCount(
                            content.getLikeCount() != null ? content.getLikeCount() : 0);
                    item.setCollectCount(
                            content.getCollectCount() != null ? content.getCollectCount() : 0);
                    item.setCommentCount(
                            content.getCommentCount() != null ? content.getCommentCount() : 0);
                    item.setSummary(content.getSummary());
                    item.setCoverImage(content.getCoverImage());
                    item.setPublishTime(
                            content.getCreatedAt() != null
                                    ? content.getCreatedAt()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                    : ""
                    );
                    item.setLink("/api/v1/content/" + content.getId());
                    return item;
                }).collect(Collectors.toList());

        result.setList(list);
        return result;
    }

    // =====================================================
    // 用户身份
    // =====================================================
    private String getUserIdentity(Long userId) {
        try {
            String role = userMapper.getUserRole(userId);
            return (role != null && !role.isEmpty()) ? role : "student";
        } catch (Exception e) {
            log.warn("获取用户身份失败", e);
            return "student";
        }
    }

    // =====================================================
    // 轮播图
    // =====================================================
    private List<HomeDTO.Carousel> getCarouselData() {
        try {
            List<Activity> activities = carouselMapper.findActiveCarousels(5);
            return activities.stream().map(activity -> {
                HomeDTO.Carousel carousel = new HomeDTO.Carousel();
                carousel.setId(activity.getId());
                carousel.setTitle(activity.getTitle());
                carousel.setImageUrl(activity.getImageUrl());
                carousel.setDesc(activity.getDescription());
                carousel.setLink("/api/v1/activity/" + activity.getId());
                return carousel;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取轮播图失败", e);
            return List.of();
        }
    }

    // =====================================================
    // 热门活动
    // =====================================================
    private List<HomeDTO.HotActivity> getHotActivities() {
        try {
            List<Activity> activities = hotActivityMapper.findHotActivities(3);
            return activities.stream().map(activity -> {
                HomeDTO.HotActivity hot = new HomeDTO.HotActivity();
                hot.setId(activity.getId());
                hot.setTitle(activity.getTitle());

                if (activity.getStartTime() != null && activity.getEndTime() != null) {
                    String start = activity.getStartTime()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    String end = activity.getEndTime()
                            .format(DateTimeFormatter.ofPattern("HH:mm"));
                    hot.setTime(start + " - " + end);
                } else {
                    hot.setTime("时间待定");
                }

                hot.setParticipantCount(activity.getParticipantCount());
                hot.setLink("/api/v1/activity/" + activity.getId());
                return hot;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取热门活动失败", e);
            return List.of();
        }
    }
    private static final Map<String, List<String>> RECOMMEND_TAG_MAP = Map.of(
            "学生", List.of("秋招面试", "第一份实习", "简历优化"),
            "职场菜鸟", List.of("职场新人避坑", "转正汇报", "沟通技巧"),
            "职场老手", List.of("行业交流", "管理进阶", "经验分享", "offer选择")
    );
}
