package com.bcsdlab.biseo.repository;

import com.bcsdlab.biseo.dto.notice.NoticeAndFileModel;
import com.bcsdlab.biseo.dto.notice.NoticeModel;
import com.bcsdlab.biseo.dto.notice.NoticeReadModel;
import com.bcsdlab.biseo.dto.notice.NoticeTargetModel;
import com.bcsdlab.biseo.dto.user.UserModel;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeRepository {

    void createNotice(NoticeModel noticeModel);
    void createTarget(NoticeTargetModel targetModel);
    NoticeAndFileModel findByNoticeId(Long id);
    NoticeReadModel findReadLogByUserId(Long noticeId, Long userId);
    void createReadLog(NoticeReadModel noticeReadModel);
    List<UserModel> findReadLogByNoticeId(Long noticeId);
    List<UserModel> findNotReadLogByNoticeId(Long noticeId);
}
