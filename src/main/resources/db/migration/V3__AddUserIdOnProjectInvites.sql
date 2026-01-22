alter table project_invites
    add user_id BIGINT not null after project_id;

alter table project_invites
    add constraint fk_project_invites_user_id
        foreign key (user_id) references users (id);
