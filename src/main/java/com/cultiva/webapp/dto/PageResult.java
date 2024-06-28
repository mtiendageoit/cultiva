package com.cultiva.webapp.dto;

import java.util.List;

import lombok.Data;

@Data
public class PageResult<T> {
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;
    private List<T> data;
}