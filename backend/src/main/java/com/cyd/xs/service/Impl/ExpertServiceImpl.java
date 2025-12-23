package com.cyd.xs.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyd.xs.dto.expert.VO.ExpertVO;
import com.cyd.xs.entity.Expert.Expert;
import com.cyd.xs.mapper.ExpertMapper;
import com.cyd.xs.service.ExpertService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpertServiceImpl extends ServiceImpl<ExpertMapper, Expert> implements ExpertService {

    @Override
    public Page<ExpertVO> getExpertPage(String keyword, String tag, String sort, Integer pageNum, Integer pageSize) {
        // 处理默认值
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 10;
        if (sort == null || sort.isEmpty()) sort = "score"; // 默认评分排序

        // 计算分页偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询分页数据
        List<ExpertVO> expertList = baseMapper.listExpertByPage(keyword, tag, sort, pageSize, offset);
        // 统计总数
        Integer total = baseMapper.countExpertTotal(keyword, tag);

        // 拼接专家主页链接（示例：/api/v1/expert/16001）
        expertList.forEach(vo -> {
            vo.setLink("/api/v1/expert/" + vo.getId());
            // 处理头像默认值（若用户表无头像，设置默认图片）
            if (vo.getAvatar() == null || vo.getAvatar().isEmpty()) {
                vo.setAvatar("https://picsum.photos/id/64/200/200"); // 随机默认头像
            }
        });
        System.out.println(
                "【DEBUG ExpertPage】 keyword=" + keyword
                        + ", tag=" + tag
                        + ", sort=" + sort
                        + ", pageNum=" + pageNum
                        + ", pageSize=" + pageSize
        );
        // 封装分页结果
        Page<ExpertVO> page = new Page<>(pageNum, pageSize);
        page.setRecords(expertList);
        page.setTotal(total);
        return page;
    }
}