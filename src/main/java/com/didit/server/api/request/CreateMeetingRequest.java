package com.didit.server.api.request;

import com.didit.server.data.entity.enums.MeetingMode;

public record CreateMeetingRequest (String title, MeetingMode mode){
}
