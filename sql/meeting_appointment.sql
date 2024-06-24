drop table if exists meeting_attendees;
create table `meeting-appointment`.meeting_attendees
(
    id                bigint auto_increment
        primary key,
    meeting_record_id bigint                               not null comment '会议预约记录id',
    user_id           varchar(50) collate utf8mb4_bin      not null comment '参与人id',
    user_name         varchar(20)                          null comment '参会人姓名',
    gmt_create        datetime   default CURRENT_TIMESTAMP null comment '添加时间',
    gmt_modified      datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted        tinyint(1) default 0                 not null comment '逻辑删除（0未删除 1删除）'
)
    comment '参会人员表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

create index meeting_attendees_meeting_record_id_index
    on `meeting-appointment`.meeting_attendees (meeting_record_id);



create index meeting_attendees_meeting_record_id_index
    on `meeting-appointment`.meeting_attendees (meeting_record_id);



drop table if exists meeting_group;
create table meeting_group
(
    id           bigint unsigned auto_increment comment '群组id'
        primary key,
    group_name   varchar(50)                        not null comment '群组名称',
    gmt_create   datetime default CURRENT_TIMESTAMP null comment '添加时间',
    gmt_modified datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    created_by   varchar(50)                        not null comment '创建人id',
    is_deleted   tinyint  default 0                 not null comment '逻辑删除（0未删除1删除）'
)
    comment '群组表' collate = utf8mb4_bin
                     row_format = DYNAMIC;

drop table if exists meeting_notice;
create table meeting_notice
(
    id           bigint unsigned auto_increment comment '公告id'
        primary key,
    substance    varchar(255)                       null comment '公告内容',
    user_id      varchar(50)                        not null comment '创建者id',
    gmt_create   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted   tinyint  default 0                 not null comment '是否删除（0 未删除1删除）'
)
    comment '会议公告表';

drop table if exists meeting_record;
create table `meeting-appointment`.meeting_record
(
    id              bigint unsigned auto_increment
        primary key,
    title           varchar(255) collate utf8mb4_bin     not null comment '会议主题',
    admin_user_name varchar(20)                          null comment '创建人姓名',
    description     varchar(255) collate utf8mb4_bin     null comment '会议概述',
    start_time      datetime                             not null comment '开始时间',
    end_time        datetime                             not null comment '结束时间',
    meeting_room_id bigint                               not null comment '会议室id',
    status          tinyint(1) default 0                 not null comment '会议状态0已预约1进行中2已结束3已取消',
    created_by      varchar(50) collate utf8mb4_bin      not null comment '创建人id',
    gmt_create      datetime   default CURRENT_TIMESTAMP not null comment '添加时间',
    gmt_modified    datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted      tinyint(1) default 0                 not null comment '0未删除 1删除'
)
    comment '会议记录表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;



drop table if exists meeting_room;
create table meeting_room
(
    id           bigint unsigned auto_increment comment 'id'
        primary key,
    room_name    varchar(50)                          not null comment '会议室名称',
    location     VARCHAR(50)                          null comment '会议室位置',
    capacity     int                                  null comment '容量',
    status       tinyint    default 1                 not null comment '会议室状态（0暂停使用,1可使用/空闲 2为使用中不保存至数据库，实时获取）',
    gmt_create   datetime   default CURRENT_TIMESTAMP not null comment '添加时间',
    gmt_modified datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    created_by   varchar(50)                          not null comment '创建人企微id',
    equipment    varchar(50)                          not null comment '会议室设备',
    is_deleted   tinyint(1) default 0                 not null comment '0未删除 1删除'
)
    comment '会议室表' collate = utf8mb4_bin
                       row_format = DYNAMIC;

drop table if exists sys_department;
create table sys_department
(
    id              bigint auto_increment
        primary key,
    department_id   bigint            not null comment '企微部门id',
    department_name varchar(50)       null comment '部门名称',
    parent_id       bigint            not null comment '父部门id(根部门为0)',
    is_deleted      tinyint default 0 not null
);

drop table if exists sys_department_user;
create table sys_department_user
(
    id            bigint auto_increment
        primary key,
    user_id       varchar(64)       not null comment '成员id（非成员user_id）',
    department_id bigint            not null comment '部门id',
    is_deleted    tinyint default 0 not null
)
    comment '企微部门成员关联表';

drop table if exists sys_user;
create table sys_user
(
    id         bigint auto_increment
        primary key,
    user_id    varchar(50)       not null comment '企微id',
    user_name  varchar(20)       not null comment '用户名',
    password   varchar(255)      null comment '密码',
    level      tinyint default 2 not null comment '权限等级(0超级管理员，1管理员，2员工)',
    is_deleted tinyint default 0 not null comment '逻辑删除0否1是'
);


drop table if exists user_group;
create table user_group
(
    id           bigint unsigned auto_increment
        primary key,
    user_id      varchar(50)                          not null comment '成员企微id',
    group_id     bigint                               not null comment '群组id',
    gmt_create   datetime   default CURRENT_TIMESTAMP null comment '添加时间',
    gmt_modified datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted   tinyint(1) default 0                 not null comment '逻辑删除（0未删除 1删除）'
)
    comment '群组人员关联表' collate = utf8mb4_bin
                             row_format = DYNAMIC;

