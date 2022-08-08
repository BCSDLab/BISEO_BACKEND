package com.bcsdlab.biseo.dto.user;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCertifiedModel {
    private Long id;
    private Long userId;
    private String authNum;
    private Timestamp createdAt;
    private boolean isDeleted;
}
