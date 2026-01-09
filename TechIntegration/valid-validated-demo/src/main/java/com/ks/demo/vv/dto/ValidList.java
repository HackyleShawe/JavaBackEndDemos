package com.ks.demo.vv.dto;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class ValidList<T> {
    @Valid
    private List<T> dataList;

}
