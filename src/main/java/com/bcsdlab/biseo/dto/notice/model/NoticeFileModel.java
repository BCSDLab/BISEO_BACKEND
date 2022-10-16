package com.bcsdlab.biseo.dto.notice.model;

import com.bcsdlab.biseo.enums.FileType;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeFileModel {
    private Long id;
    private Long noticeId;
    private String path;
    private String savedName;
    private FileType type;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isDeleted;
}