package com.ks.demo.wh.holiday.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ks.demo.wh.holiday.dto.HolidayAddDto;
import com.ks.demo.wh.holiday.dto.HolidayCacheDto;
import com.ks.demo.wh.holiday.dto.HolidayQueryDto;
import com.ks.demo.wh.holiday.dto.HolidayUpdateDto;
import com.ks.demo.wh.holiday.entity.SysHoliday;
import com.ks.demo.wh.holiday.mapper.SysHolidayMapper;
import com.ks.demo.wh.holiday.vo.HolidayVo;
import com.ks.demo.wh.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * 节假日管理服务接口
 */
@Slf4j
@Service
public class HolidayAdminService extends ServiceImpl<SysHolidayMapper, SysHoliday> {

    @Autowired
    private SysHolidayMapper sysHolidayMapper;
    @Autowired
    private HolidayCacheService holidayCacheService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 新增节假日信息
     * TODO 怎么设计入参？怎么同步缓存？
     */
    public boolean addHoliday(List<HolidayAddDto> holidayAddDtoList) {
        if(holidayAddDtoList == null || holidayAddDtoList.isEmpty()) {
            return false;
        }

        //是否可以修改已经过时了的节假日？

        return true;
    }

    public boolean delHoliday(Set<Long> ids) {
        List<SysHoliday> sysHolidays = this.listByIds(ids);
        if(CollectionUtils.isEmpty(sysHolidays)) {
            throw new IllegalArgumentException("待删除的记录不存在");
        }

        boolean removed = this.removeBatchByIds(ids);
        if(!removed) {
            return false;
        }

        for (SysHoliday sysHoliday : sysHolidays) {
            holidayCacheService.del(sysHoliday.getHoliday().format(FORMATTER));
        }

        return true;
    }

    /**
     * 更新某一节假日信息，只能更新已经存在的节假日信息
     *
     * 怎么更新缓存？更新DB、删L2、删L1
     */
    public boolean updateHoliday(HolidayUpdateDto holidayUpdateDto) {
        SysHoliday sysHoliday = this.getById(holidayUpdateDto.getId());
        if(sysHoliday == null) {
            throw new IllegalArgumentException("待更新的记录不存在");
        }

        boolean updated = this.update(Wrappers.<SysHoliday>lambdaUpdate().set(SysHoliday::getDescription, holidayUpdateDto.getDescription())
                .set(SysHoliday::getRegion, holidayUpdateDto.getRegion())
                .eq(SysHoliday::getId, holidayUpdateDto.getId()));
        if(!updated) {
            return false;
        }

        boolean del = holidayCacheService.del(sysHoliday.getHoliday().format(FORMATTER));

        return del;
    }

    public List<HolidayVo> list(HolidayQueryDto holidayQueryDto) {
        LambdaQueryWrapper<SysHoliday> queryWrapper = Wrappers.<SysHoliday>lambdaQuery().eq(SysHoliday::getDeleted, Boolean.FALSE);
        if(holidayQueryDto.getId() != null) {
            queryWrapper.eq(SysHoliday::getId, holidayQueryDto.getId());
        }
        if(holidayQueryDto.getYear() != null) {
            queryWrapper.eq(SysHoliday::getYear, holidayQueryDto.getYear());
        }
        if(holidayQueryDto.getMonth() != null) {
            if(holidayQueryDto.getYear() == null) {
                throw new IllegalArgumentException("请先输入年份");
            }
            String start = holidayQueryDto.getYear()+"-"+holidayQueryDto.getMonth()+"-1";
            String end = holidayQueryDto.getYear()+"-"+holidayQueryDto.getMonth()+"-31";
            queryWrapper.between(SysHoliday::getHoliday, start, end);
        }

        List<SysHoliday> holidayList = this.list(queryWrapper);

        return BeanCopyUtils.copyList(holidayList, HolidayVo.class);
    }

}
