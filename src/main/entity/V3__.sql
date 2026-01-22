ALTER TABLE chats
DROP
FOREIGN KEY fk_chats_meeting;

ALTER TABLE chats
DROP
FOREIGN KEY fk_chats_project;

ALTER TABLE chats
DROP
FOREIGN KEY fk_chats_user;

ALTER TABLE issue_assignees
DROP
FOREIGN KEY fk_issue_assignees_issue;

ALTER TABLE issue_assignees
DROP
FOREIGN KEY fk_issue_assignees_user;

ALTER TABLE issues
DROP
FOREIGN KEY fk_issues_author;

ALTER TABLE issues
DROP
FOREIGN KEY fk_issues_project;

ALTER TABLE meeting_summary
DROP
FOREIGN KEY fk_meeting_summary_edited_by;

ALTER TABLE meeting_summary
DROP
FOREIGN KEY fk_meeting_summary_meeting;

ALTER TABLE meeting_speech_scripts
DROP
FOREIGN KEY fk_mss_meeting;

ALTER TABLE meeting_speech_scripts
DROP
FOREIGN KEY fk_mss_speaker_user;

ALTER TABLE project_user_reads
DROP
FOREIGN KEY fk_project_user_reads_last_chat;

ALTER TABLE project_user_reads
DROP
FOREIGN KEY fk_project_user_reads_project;

ALTER TABLE project_user_reads
DROP
FOREIGN KEY fk_project_user_reads_user;

ALTER TABLE meetings
    ADD mode VARCHAR(255) NULL;

ALTER TABLE meetings
    MODIFY mode VARCHAR (255) NOT NULL;

DROP TABLE chats;

DROP TABLE issue_assignees;

DROP TABLE issues;

DROP TABLE meeting_speech_scripts;

DROP TABLE meeting_summary;

DROP TABLE project_user_reads;

ALTER TABLE meetings
DROP
COLUMN status;

ALTER TABLE meetings
    ADD status VARCHAR(255) NOT NULL;