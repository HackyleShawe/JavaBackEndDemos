package com.ks.demo.mosq.service;

import com.ks.demo.mosq.dao.PersonDao;
import com.ks.demo.mosq.dto.PersonQueryDto;
import com.ks.demo.mosq.dto.PersonQueryResDto;
import com.ks.demo.mosq.dto.PersonSaveDto;
import com.ks.demo.mosq.entity.PersonEntity;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PersonService {
    @Autowired
    private PersonDao personDao;

    public boolean save(PersonSaveDto personSaveDto) {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setName(personSaveDto.getName());
        personEntity.setGender(personSaveDto.getGender());
        personEntity.setAddress(personSaveDto.getAddress());
        //转换职业发展的多选项
        if(null != personSaveDto.getCareerOption() && !"".equals(personSaveDto.getCareerOption())) {
            personEntity.setCareers(multiOptionConvert(personSaveDto.getCareerOption()));
        }
        //转换兴趣爱好的多选项
        if(null != personSaveDto.getInterestOption() && !"".equals(personSaveDto.getInterestOption())) {
            personEntity.setInterests(multiOptionConvert(personSaveDto.getInterestOption()));
        }

        if(personSaveDto.getId() == null) {
            personEntity.setId(System.currentTimeMillis());
            personEntity.setCreateTime(new Date());
            personEntity.setUpdateTime(personEntity.getCreateTime());
            personEntity.setDeleted(0); //魔法常量，实际开发中应该避免，写到常量类中
            return personDao.add(personEntity);
        } else {
            personEntity.setUpdateTime(new Date());
            return personDao.update(personEntity);
        }
    }

    public List<PersonQueryResDto> query(PersonQueryDto personQueryDto) {
        PersonEntity personEntity = new PersonEntity();
        if(null != personQueryDto.getQueryName() && !"".equals(personQueryDto.getQueryName())) {
            personEntity.setName(personQueryDto.getQueryName());
        }
        //转换职业发展的多选项
        if(null != personQueryDto.getQueryCareerOption() && !"".equals(personQueryDto.getQueryCareerOption())) {
            personEntity.setCareers(multiOptionConvert(personQueryDto.getQueryCareerOption()));
        }
        //转换兴趣爱好的多选项
        if(null != personQueryDto.getQueryInterestOption() && !"".equals(personQueryDto.getQueryInterestOption())) {
            personEntity.setInterests(multiOptionConvert(personQueryDto.getQueryInterestOption()));
        }

        List<PersonEntity> personEntityList = personDao.query(personEntity);

        //数据转换
        List<PersonQueryResDto> personQueryResDtoList = new ArrayList<>(personEntityList.size());
        for (PersonEntity person : personEntityList) {
            PersonQueryResDto personQueryResDto = new PersonQueryResDto();
            personQueryResDto.setId(person.getId());
            personQueryResDto.setName(person.getName());
            personQueryResDto.setGender(person.getGender());
            personQueryResDto.setAddress((person.getAddress()));
            //将数字型的多选项转换为编号串
            personQueryResDto.setCareerOption(multiOptionConvert(person.getCareers()));
            personQueryResDto.setInterestOption(multiOptionConvert(person.getInterests()));
            personQueryResDto.setCreateTime(person.getCreateTime());
            personQueryResDto.setUpdateTime(person.getUpdateTime());

            personQueryResDtoList.add(personQueryResDto);
        }
        return personQueryResDtoList;
    }

    @Test
    public void testCalc() {
        String opt = "1,3,4,6,7,8,10";
        System.out.println(multiOptionConvert(opt));
        System.out.println(multiOptionConvertByPlain(opt));
    }


    @Test
    public void testMultiOptionConvert() {
        long res = multiOptionConvert("3,4,8,9,10");
        System.out.println(res);
        System.out.println(multiOptionConvert(res));
    }

    @Test
    public void testMultiOptionConvertByPlain() {
        long res = multiOptionConvertByPlain("3,4,8,9,10");
        System.out.println(res);
        System.out.println(multiOptionConvertByPlain(res));
    }

    /**
     * 将字符串的职业发展多选项、兴趣爱好多选项，转换为一个Long数字
     * @param optionStr 输入示例：1,2,5,6
     */
    public long multiOptionConvert(String optionStr) {
        if(null == optionStr || "".equals(optionStr)) {
            return 0;
        }

        String[] optionArr = optionStr.split(",");
        String collect = IntStream.rangeClosed(1, 63) //int型的位宽为32个比特位，[1, 31]，long型的位宽为64个比特位，[1,63]
                .mapToObj(i -> {
                    for (String opt : optionArr) {
                        if(String.valueOf(64 - i).equals(opt)) {
                            return "1";
                        }
                    }
                    return "0";
                })
                .collect(Collectors.joining());

        return Long.parseLong(collect, 2);
    }

    /**
     * 将多选项的数字串，转换为一个Long数字
     * 易懂、简单的原始实现
     */
    public long multiOptionConvertByPlain(String optionStr) {
        if(StringUtils.isBlank(optionStr)) {
            return 0;
        }

        //将前端传递来的选项串，转换为数字，存于List
        String[] optionArr = optionStr.split(",");
        List<Integer> optList = new ArrayList<>();
        for (String opt : optionArr) {
            optList.add(Integer.parseInt(opt));
        }

        //选项中的最大值有多大，二进制层面就有多少位
        byte[] bitArr = new byte[optList.stream().max(Long::compare).orElse(1)];

        for (Integer opt : optList) {
            bitArr[opt - 1] = 1; //有这个数字，表明该个数字所代表的下标在二进制层面的位上为1
        }

        StringBuilder binStr = new StringBuilder();
        for (int i = bitArr.length-1; i >= 0; i--) {
            binStr.append(bitArr[i]);
        }

        return Long.parseLong(binStr.toString(), 2);
    }

    /**
     * 将Long型的职业发展多选项、兴趣爱好多选项，转换为下标串格式
     * @param option INT数字
     * @return 例如：1,2,5,6
     */
    public String multiOptionConvert(long option) {
        AtomicLong atomicLong = new AtomicLong(Math.max(option, 0L));
        return IntStream.rangeClosed(1, 63) //long类型的位宽为64个比特位
                .map(i -> {
                    int s = atomicLong.get() % 2 > 0 ? i : 0;
                    atomicLong.set(atomicLong.get() / 2);
                    return s;
                }).filter(s -> s > 0).mapToObj(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * 将Long型的多选项，转换成下标串的形式
     * 易懂、简单的原始实现
     */
    public String multiOptionConvertByPlain(long option) {
        StringBuilder res = new StringBuilder();
        String binStr = Long.toBinaryString(option);
        String binStrRev = new StringBuilder(binStr).reverse().toString();
        for (int i = 0; i < binStrRev.length(); i++) {
            if('1' == (binStrRev.charAt(i))) {
                res.append(i+1).append(",");
            }
        }

        return res.toString();
    }
}
