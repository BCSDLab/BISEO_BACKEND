package com.bcsdlab.biseo.serviceImpl;

import com.bcsdlab.biseo.dto.notice.NoticeAndFileModel;
import com.bcsdlab.biseo.dto.notice.NoticeFileModel;
import com.bcsdlab.biseo.dto.notice.NoticeModel;
import com.bcsdlab.biseo.dto.notice.NoticeReadModel;
import com.bcsdlab.biseo.dto.notice.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.NoticeResponseDTO;
import com.bcsdlab.biseo.dto.notice.NoticeTargetModel;
import com.bcsdlab.biseo.dto.user.UserModel;
import com.bcsdlab.biseo.dto.user.UserResponseDTO;
import com.bcsdlab.biseo.enums.Department;
import com.bcsdlab.biseo.enums.FileType;
import com.bcsdlab.biseo.mapper.NoticeMapper;
import com.bcsdlab.biseo.mapper.UserMapper;
import com.bcsdlab.biseo.repository.NoticeRepository;
import com.bcsdlab.biseo.repository.UserRepository;
import com.bcsdlab.biseo.service.NoticeService;
import com.bcsdlab.biseo.util.JwtUtil;
import com.bcsdlab.biseo.util.S3Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private static String extRegExp = "^([\\S\\s]+(\\.(?i)(jpg|jpeg|png|gif|bmp))$)";
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final S3Util s3Util;

    @Override
    public Long createNotice(NoticeRequestDTO request, List<MultipartFile> files) {
        // 예외 처리
        if (request.getGrade().size() == 0) {
            throw new RuntimeException("학년을 선택해야 합니다.");
        }

        // notice 기본 정보 저장
        NoticeModel notice = NoticeMapper.INSTANCE.toNoticeModel(request);
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        notice.setUserId(userId);
        noticeRepository.createNotice(notice);

        // notice target 학과/학년 저장
        List<NoticeTargetModel> targetList = new ArrayList<>();
        // 학생회 : 0
        targetList.add(new NoticeTargetModel(notice.getId(), request.getDepartment().getValue()));
        for (Integer grade : request.getGrade()) {
            targetList.add(new NoticeTargetModel(notice.getId(), request.getDepartment().getValue() + grade));
        }
        noticeRepository.createTarget(targetList);

        // notice File 저장
        List<NoticeFileModel> fileList = uploadFiles(notice.getId(), files);
        if (fileList.size() != 0) {
            noticeRepository.createFiles(fileList);
        }

        // TODO : notice target에 푸시 알림


        return notice.getId();
    }

    @Override
    public NoticeResponseDTO getNotice(Long noticeId) {
        if (noticeId < 1) {
            throw new RuntimeException("잘못된 접근입니다.");
        }

        // 공지 조회
        NoticeAndFileModel noticeAndFile = noticeRepository.findNoticeAndFileById(noticeId);
        if (noticeAndFile == null) {
            throw new RuntimeException("존재하지 않는 공지입니다.");
        }

        // 조회 가능한 학과인가?
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        Integer userDepartment = userRepository.findUserDepartmentById(userId);
        List<Integer> noticeTarget = noticeRepository.findTargetByNoticeId(noticeId);
        if (!noticeTarget.contains(userDepartment)) {
            throw new RuntimeException("읽을 권한이 없습니다.");
        }

        NoticeResponseDTO response = NoticeMapper.INSTANCE.toResponseDTO(noticeAndFile);

        // File, Img 구분
        for (NoticeFileModel file : noticeAndFile.getFiles()) {
            if (file.getType() == FileType.FILE) {
                response.getFiles().add(file.getPath());
            } else if (file.getType() == FileType.IMG) {
                response.getImgs().add(file.getPath());
            }
        }

        // 읽은 유저 읽음처리
        if (noticeRepository.findReadLogByUserId(noticeId, userId) == null) {
            NoticeReadModel noticeReadModel = NoticeReadModel.builder()
                .userId(userId)
                .noticeId(noticeId)
                .build();
            noticeRepository.createReadLog(noticeReadModel);
        }
        return response;
    }


    // 커서기반 페이지네이션
    @Override
    public List<NoticeResponseDTO> getNoticeList(String searchBy, Long cursor, Integer limits) {
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        Integer userDepartment = userRepository.findUserDepartmentById(userId);
        List<NoticeAndFileModel> noticeModelList = noticeRepository.getNoticeList(userDepartment, searchBy, cursor, limits);

        return noticeModelList.stream()
            .map(NoticeMapper.INSTANCE::toResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getReadLog(Long noticeId, Boolean isRead) {
        if (noticeId < 1 || isRead == null) {
            throw new RuntimeException("잘못된 접근입니다.");
        }
        NoticeModel notice = noticeRepository.findNoticeById(noticeId);
        if (notice == null) {
            throw new RuntimeException("존재하지 않는 공지입니다.");
        }
        List<UserModel> userList = isRead ? noticeRepository.findReadLogByNoticeId(noticeId)
            : noticeRepository.findNotReadLogByNoticeId(noticeId);

        List<UserResponseDTO> responses = new ArrayList<>();
        for (UserModel model : userList) {
            UserResponseDTO response = UserMapper.INSTANCE.toUserResponse(model);
            response.setGrade(model.getDepartment() % 10);
            response.setDepartment(Department.getDepartment(model.getDepartment() / 10 * 10));
            responses.add(response);
        }
        return responses;
    }

    // 수정시
    // 해당 과/학년 전체 재공지 필요
    // 파일 : 다 지우고 다시 업로드?
    @Override
    public Long updateNotice(Long noticeId, NoticeRequestDTO request, List<MultipartFile> files) {
        // 공지가 존재하지 않는다면
        NoticeModel notice = noticeRepository.findNoticeById(noticeId);
        if (notice == null) {
            throw new RuntimeException("존재하지 않는 공지입니다.");
        }

        // 작성자가 아니라면
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        if (!notice.getUserId().equals(userId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // 전부 다시 업로드
        // 게시글 : Update
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        noticeRepository.updateNoticeById(notice);

        // 타겟 : 삭제 후 재생성
        noticeRepository.deleteTargetByNoticeId(noticeId);
        List<NoticeTargetModel> targetList = new ArrayList<>();
        // 학생회 : 0
        targetList.add(new NoticeTargetModel(notice.getId(), request.getDepartment().getValue()));
        for (Integer grade : request.getGrade()) {
            targetList.add(new NoticeTargetModel(notice.getId(), request.getDepartment().getValue() + grade));
        }
        noticeRepository.createTarget(targetList);

        // 읽은 유저 : 전체 삭제
        noticeRepository.deleteReadListByNoticeId(noticeId);

        // notice File 저장
        noticeRepository.deleteNoticeFileByNoticeId(noticeId);
        List<NoticeFileModel> fileList = uploadFiles(notice.getId(), files);
        if (fileList.size() != 0) {
            noticeRepository.createFiles(fileList);
        }

        // TODO : notice target에 푸시 알림

        return notice.getId();
    }

    @Override
    public String deleteNotice(Long noticeId) {
        NoticeModel notice = noticeRepository.findNoticeById(noticeId);
        if (notice == null) {
            throw new RuntimeException("존재하지 않는 공지입니다.");
        }

        // 작성자가 아니라면
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        if (!notice.getUserId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        noticeRepository.deleteNoticeById(noticeId);
        noticeRepository.deleteTargetByNoticeId(noticeId);
        noticeRepository.deleteReadListByNoticeId(noticeId);
        noticeRepository.deleteNoticeFileByNoticeId(noticeId);

        return "게시글 삭제 완료";
    }

    private List<NoticeFileModel> uploadFiles(Long noticeId, List<MultipartFile> files) {
        List<NoticeFileModel> models = new ArrayList<>();

        for (MultipartFile file : files) {
            NoticeFileModel model = new NoticeFileModel();
            UUID uuid = UUID.randomUUID();
            String savedName = noticeId + "/" + uuid + "/" + file.getOriginalFilename();
            model.setNoticeId(noticeId);
            model.setSavedName(savedName);
            model.setType(checkFileType(savedName));
            try {
                model.setPath(s3Util.uploadFile(savedName, file));
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 문제가 발생하였습니다.");
            }
            models.add(model);
        }

        return models;
    }

    private FileType checkFileType(String fileName) {
        if (fileName.matches(extRegExp)) {
            return FileType.IMG;
        }
        return FileType.FILE;
    }
}
