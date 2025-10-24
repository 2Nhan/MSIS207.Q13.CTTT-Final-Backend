package com.crm.project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> content;

    @JsonProperty("has_pre")
    private boolean hasPre;

    @JsonProperty("has_next")
    private boolean hasNext;

    @JsonProperty("page_no")
    private int pageNumber;

    @JsonProperty("page_size")
    private int pageSize;

    @JsonProperty("total_pages")
    private int totalPages;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.hasPre = page.hasPrevious();
        this.hasNext = page.hasNext();
        this.pageNumber = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
    }
}
