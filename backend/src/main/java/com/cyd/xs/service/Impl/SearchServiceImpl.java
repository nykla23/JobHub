package com.cyd.xs.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cyd.xs.dto.Search.SearchDTO;
import com.cyd.xs.entity.Topic.Topic;
import com.cyd.xs.mapper.Search.SearchHistoryMapper;
import com.cyd.xs.mapper.Topic.TopicMapper;
import com.cyd.xs.mapper.UserContentMapper;
import com.cyd.xs.mapper.UserSearchMapper;
import com.cyd.xs.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cyd.xs.mapper.groups.GroupMapper;
import com.cyd.xs.mapper.ExpertMapper;
import com.cyd.xs.entity.Group.Group;
import com.cyd.xs.dto.expert.VO.ExpertVO;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchHistoryMapper searchHistoryMapper;
    private final UserContentMapper userContentMapper;
    private final UserSearchMapper userSearchMapper;
    private final TopicMapper topicMapper;
    private final GroupMapper groupMapper;
    private final ExpertMapper expertMapper;
    @Override
    public SearchDTO getSearchHistoryAndHot(String userId) {
        SearchDTO dto = new SearchDTO();

        boolean hasLoginUser =
                userId != null && !"anonymous".equals(userId) && !userId.isBlank();

        if (hasLoginUser) {
            try {
                Long uid = Long.valueOf(userId);
                dto.setSearchHistory(
                        searchHistoryMapper.findRecentSearchHistory(uid, 10)
                );
            } catch (Exception e) {
                dto.setSearchHistory(Collections.emptyList());
            }
        } else {
            dto.setSearchHistory(Collections.emptyList());
        }

        try {
            List<String> hot = searchHistoryMapper.findHotKeywords(10);
            dto.setHotSearch(
                    hot == null || hot.isEmpty() ? defaultHotKeywords() : hot
            );
        } catch (Exception e) {
            dto.setHotSearch(defaultHotKeywords());
        }

        return dto;
    }

    private List<String> defaultHotKeywords() {
        return Arrays.asList(
                "秋招面试", "简历优化", "面试技巧", "职业规划", "职场心理",
                "offer选择", "春招冲刺", "行业交流", "导师咨询", "求职专项"
        );
    }

    @Override
    @Transactional
    public void clearSearchHistory(String userId) {
        if (userId == null || "anonymous".equals(userId)) {
            return;
        }
        Long uid = Long.valueOf(userId);
        searchHistoryMapper.deleteByUserId(uid);
    }

    @Override
    @Transactional
    public SearchDTO search(String keyword, String type, String sort,
                            Integer pageNum, Integer pageSize, String userId) {

        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        keyword = (keyword == null) ? "" : keyword.trim();
        type = (type == null || type.isBlank()) ? "all" : type;

        SearchDTO dto = new SearchDTO();
        dto.setKeyword(keyword);
        dto.setType(type);
        dto.setPageNum(pageNum);
        dto.setPageSize(pageSize);

        if (keyword.isEmpty()) {
            dto.setList(Collections.emptyList());
            dto.setTotal(0L);
            return dto;
        }
        boolean hasLoginUser =
                userId != null && !"anonymous".equals(userId) && !userId.isBlank();

        if (hasLoginUser) {
            try {
                Long uid = Long.valueOf(userId);
                searchHistoryMapper.saveSearchHistory(uid, keyword);
            } catch (Exception e) {
                log.warn("保存搜索历史失败，忽略", e);
            }
        }

        int offset = (pageNum - 1) * pageSize;

        // ================= topic =================
        if ("topic".equals(type)) {

            Long total = topicMapper.selectCount(
                    Wrappers.<Topic>lambdaQuery()
                            .like(Topic::getTitle, keyword)
            );

            List<Topic> topics = topicMapper.selectList(
                    Wrappers.<Topic>lambdaQuery()
                            .like(Topic::getTitle, keyword)
                            .orderByDesc("hot".equals(sort), Topic::getInteractiveCount)
                            .orderByDesc(Topic::getCreatedAt)
                            .last("LIMIT " + offset + "," + pageSize)
            );

            List<Object> result = topics.stream().map(t -> {
                SearchDTO.ContentResult cr = new SearchDTO.ContentResult();
                cr.setId(String.valueOf(t.getId()));
                cr.setTitle(t.getTitle());
                cr.setLink("/api/v1/topic/" + t.getId());
                return cr;
            }).collect(Collectors.toList());

            dto.setList(result);
            dto.setTotal(total);
            return dto;
        }
        // ================= group =================
        if ("group".equals(type)) {

            List<Group> groups = groupMapper.findGroups(
                    keyword,
                    null,           // tag 暂不从搜索页传
                    offset,
                    pageSize
            );

            Long total = groupMapper.countGroups(keyword, null);

            List<SearchDTO.ContentResult> result = groups.stream().map(g -> {
                SearchDTO.ContentResult cr = new SearchDTO.ContentResult();
                cr.setId(String.valueOf(g.getId()));
                cr.setTitle(g.getName());
                cr.setLink("/circle/" + g.getId());
                return cr;
            }).collect(Collectors.toList());

            dto.setList(new ArrayList<>(result));
            dto.setTotal(total);
            return dto;
        }
        // ================= expert =================
        if ("expert".equals(type)) {

            List<ExpertVO> experts = expertMapper.listExpertByPage(
                    keyword,
                    null,       // tag 暂不传
                    sort,
                    pageSize,
                    offset
            );

            Integer total = expertMapper.countExpertTotal(keyword, null);

            List<SearchDTO.UserResult> result = experts.stream().map(e -> {
                SearchDTO.UserResult ur = new SearchDTO.UserResult();
                ur.setId(String.valueOf(e.getId()));
                ur.setNickname(e.getName());
                ur.setIdentity(e.getCertification());
                ur.setAvatar(e.getAvatar());
                ur.setLink("/pro/expert/" + e.getId());
                return ur;
            }).collect(Collectors.toList());

            dto.setList(new ArrayList<>(result));
            dto.setTotal(total.longValue());
            return dto;
        }


        // ================= all =================
        List<Object> all = new ArrayList<>();

        List<Topic> topics = topicMapper.selectList(
                Wrappers.<Topic>lambdaQuery()
                        .like(Topic::getTitle, keyword)
                        .orderByDesc("hot".equals(sort), Topic::getInteractiveCount)
                        .orderByDesc(Topic::getCreatedAt)
                        .last("LIMIT " + offset + "," + pageSize)
        );

        topics.forEach(t -> {
            SearchDTO.ContentResult cr = new SearchDTO.ContentResult();
            cr.setId(String.valueOf(t.getId()));
            cr.setTitle(t.getTitle());
            cr.setLink("/api/v1/topic/" + t.getId());
            all.add(cr);
        });

        List<Map<String, Object>> contents =
                userContentMapper.searchUserContents(keyword, null, sort, offset, pageSize);
        all.addAll(convertToContentResult(contents, "content"));

        List<Map<String, Object>> users =
                userSearchMapper.searchUsers(keyword, offset, pageSize);
        all.addAll(convertToUserResult(users));

        Long total =
                topicMapper.selectCount(
                        Wrappers.<Topic>lambdaQuery().like(Topic::getTitle, keyword)
                )
                        + userContentMapper.countSearchUserContents(keyword, null)
                        + userSearchMapper.countSearchUsers(keyword);

        dto.setList(all);
        dto.setTotal(total);
        return dto;
    }

    private List<SearchDTO.ContentResult> convertToContentResult(
            List<Map<String, Object>> rows, String type) {

        return rows.stream().map(r -> {
            SearchDTO.ContentResult cr = new SearchDTO.ContentResult();
            cr.setId(String.valueOf(r.get("id")));
            cr.setTitle((String) r.get("title"));
            cr.setLink("/api/v1/content/" + r.get("id"));
            return cr;
        }).collect(Collectors.toList());
    }

    private List<SearchDTO.UserResult> convertToUserResult(
            List<Map<String, Object>> rows) {

        return rows.stream().map(r -> {
            SearchDTO.UserResult ur = new SearchDTO.UserResult();
            ur.setId(String.valueOf(r.get("id")));
            ur.setNickname((String) r.get("display_name"));
            ur.setIdentity((String) r.get("role"));
            ur.setAvatar((String) r.get("avatar_url"));
            ur.setLink("/api/v1/user/" + r.get("id"));
            return ur;
        }).collect(Collectors.toList());
    }
}
