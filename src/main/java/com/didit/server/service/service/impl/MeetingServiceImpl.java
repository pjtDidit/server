package com.didit.server.service.service.impl;

import com.didit.server.data.entity.MeetingEntity;
import com.didit.server.data.entity.enums.MeetingMode;
import com.didit.server.data.entity.enums.MeetingStatus;
import com.didit.server.data.repository.MeetingRepository;
import com.didit.server.data.repository.ProjectRepository;
import com.didit.server.data.repository.UserRepository;
import com.didit.server.service.service.MeetingService;
import com.didit.server.share.result.Result;
import com.didit.server.share.result.impl.NotFoundError;
import com.didit.server.share.result.impl.ServerError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    @Override
    public Result<Long> createMeeting(long projectId, long userId, String title, MeetingMode mode){
        try {
            var project = projectRepository.findById(projectId).orElse(null);
            if (project == null){
                return Result.fail(new NotFoundError("projectId", projectId));
            }
            var user = userRepository.findById(userId).orElse(null);
            if (title.isBlank() && title.length() <= 50){
                return Result.fail(new NotFoundError("title", title));
            }
            // 1. 회의실 생성 (세션Id, 회의실상태 고려 필요)
            MeetingEntity meeting = MeetingEntity.builder()
                    .project(project)
                    .createdBy(user)
                    .title(title)
                    .status(MeetingStatus.SCHEDULED)
                    .mode(mode)
                    .build();
            return Result.ok(meetingRepository.save(meeting).getId());
        } catch (Exception e) {
            return Result.fail(new ServerError("MeetingEntity", projectId));
        }
    }
}
