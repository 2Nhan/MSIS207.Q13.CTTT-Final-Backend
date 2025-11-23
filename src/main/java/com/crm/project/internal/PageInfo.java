package com.crm.project.internal;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageInfo<T> {

    private boolean hasPre;

    private boolean hasNext;

    private int pageNumber;

    private int totalPages;

    public PageInfo(Page<T> page) {
        this.hasPre = page.hasPrevious();
        this.hasNext = page.hasNext();
        this.pageNumber = page.getNumber() + 1;
        this.totalPages = page.getTotalPages();
    }
}
