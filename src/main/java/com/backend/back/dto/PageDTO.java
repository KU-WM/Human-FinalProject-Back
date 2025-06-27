package com.backend.back.dto;

import lombok.Data;

@Data
public class PageDTO {
    private int page;           // 현재 페이지
    private int pageSize;       // 한 페이지에 보여줄 리스트 개수
    private int pageLen;        // 전체 페이지의 개수
    private String pageFrom;    // 어떤 항목의 페이지를 출력할 것인지

    public PageDTO() {
        this.page = 1;
        this.pageSize = 30;
        this.pageLen = 10;
    }

    public int getOffset() {
        return (this.page - 1) * this.pageSize;
    }
}
