package com.bcsdlab.biseo.dto.notice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDTO {

    private Long id;
    private String userId;
    private String title;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Long scrapId;
    private List<String> files = new ArrayList<>();
    private List<String> imgs = new ArrayList<>();

    public void addFile(String file) {
        files.add(file);
    }

    public void addImgs(String img) {
        imgs.add(img);
    }
}
