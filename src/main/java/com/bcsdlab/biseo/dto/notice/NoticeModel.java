package com.bcsdlab.biseo.dto.notice;

import com.bcsdlab.biseo.dto.user.UserModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeModel {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isDeleted;
}
