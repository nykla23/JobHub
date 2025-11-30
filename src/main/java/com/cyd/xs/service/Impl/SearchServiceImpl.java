package com.cyd.xs.service.Impl;

import com.cyd.xs.dto.user.Search.SearchDTO;
import com.cyd.xs.entity.User.HomeContent.HomeContent;
import com.cyd.xs.entity.User.Topic.Topic;
import com.cyd.xs.mapper.*;
import com.cyd.xs.service.SearchService;
import com.cyd.xs.util.IDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchHistoryMapper searchHistoryMapper;
    private final TopicMapper topicMapper;
    private final HomeContentMapper homeContentMapper;
    private final GroupMapper groupMapper;
    private final UserMapper userMapper;
    private final ExpertMapper expertMapper;

    @Override
    public SearchDTO getSearchHistoryAndHot(String userId) {
        log.info("用户 {} 获取搜索历史和热门搜索", userId);

        SearchDTO searchDTO = new SearchDTO();

        try {
            // 个人搜索历史
            List<String> histories = searchHistoryMapper.findRecentSearchHistory(userId, 10);
            searchDTO.setSearchHistory(histories);

            // 热门搜索
            List<String> hotKeywords = searchHistoryMapper.findHotKeywords(10);
            searchDTO.setHotSearch(hotKeywords.isEmpty() ?
                    Arrays.asList("秋招面试", "简历优化", "面试技巧", "职业规划", "职场心理",
                            "offer选择", "春招冲刺", "行业交流", "导师咨询", "求职专项") :
                    hotKeywords);

            return searchDTO;
        } catch (Exception e) {
            log.error("获取搜索历史和热门搜索失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取搜索历史和热门搜索失败");
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
    @Transactional
    public SearchDTO search(String keyword, String type, String sort, Integer pageNum, Integer pageSize, String userId) {
        log.info("用户 {} 搜索关键词: {}, 类型: {}, 排序: {}", userId, keyword, type, sort);

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyword(keyword);
        searchDTO.setType(type);

        try {
            // 记录搜索历史
            String searchHistoryId = IDGenerator.generateId();
            searchHistoryMapper.saveSearchHistory(searchHistoryId, userId, keyword);

            // 计算分页偏移量
            int offset = (pageNum - 1) * pageSize;

            // 根据type返回不同结果
            if (type == null || "topic".equals(type)) {
                List<Topic> topics = topicMapper.searchTopics(keyword, sort, offset, pageSize);
                searchDTO.setTopic(topics.stream().map(topic -> {
                    SearchDTO.TopicResult tr = new SearchDTO.TopicResult();
                    tr.setId(topic.getId());
                    tr.setTitle(topic.getTitle());
                    tr.setDesc(topic.getDescription());
                    tr.setHotValue(topic.getHotValue());
                    if (topic.getTags() != null) {
                        tr.setTags(Arrays.asList(topic.getTags().split(",")));
                    }
                    tr.setParticipantCount(topic.getParticipantCount());
                    tr.setLink("/api/v1/topic/" + topic.getId());
                    return tr;
                }).collect(Collectors.toList()));
                searchDTO.setTotal(topicMapper.countSearchTopics(keyword));
            }

            if (type == null || "content".equals(type)) {
                List<HomeContent> contents = homeContentMapper.searchContents(keyword, sort, offset, pageSize);
                searchDTO.setContent(contents.stream().map(content -> {
                    SearchDTO.ContentResult cr = new SearchDTO.ContentResult();
                    cr.setId(content.getId());
                    cr.setTitle(content.getTitle());
                    cr.setAuthor(content.getAuthorName());
                    cr.setLink("/api/v1/content/" + content.getId());
                    return cr;
                }).collect(Collectors.toList()));
                if (type != null && "content".equals(type)) {
                    searchDTO.setTotal(homeContentMapper.countSearchContents(keyword));
                }
            }

            // 设置分页信息
            searchDTO.setPageNum(pageNum);
            searchDTO.setPageSize(pageSize);

            return searchDTO;
        } catch (Exception e) {
            log.error("搜索失败: {}", e.getMessage(), e);
            throw new RuntimeException("搜索失败");
        }
    }
}