package com.cyd.xs.mapper;


import com.cyd.xs.entity.User.Carousel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarouselMapper {

    @Select("SELECT * FROM carousels WHERE status = 'active' ORDER BY sort_order ASC LIMIT #{limit}")
    List<Carousel> findActiveCarousels(int limit);
}