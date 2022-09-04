package com.bcsdlab.biseo.controller;

import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.dto.notice.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.NoticeResponseDTO;
import com.bcsdlab.biseo.enums.UserType;
import com.bcsdlab.biseo.service.NoticeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping
    @Auth(type = UserType.COUNCIL)
    public ResponseEntity<Long> createNotice(@RequestPart NoticeRequestDTO request,
        @RequestPart List<MultipartFile> files) {
        return new ResponseEntity<>(noticeService.createNotice(request, files), HttpStatus.OK);
    }

    @GetMapping("/{noticeId}")
    @Auth
    public ResponseEntity<NoticeResponseDTO> getNotice(@PathVariable("noticeId") Long noticeId) {
        return new ResponseEntity<>(noticeService.getNotice(noticeId), HttpStatus.OK);
    }
}
