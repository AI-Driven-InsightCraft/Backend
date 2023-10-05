create database if not exists kaka;

use kaka;

create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null,
    userPassword varchar(512)                           not null,
    userName     varchar(256)                           null,
    userAvatar   varchar(1024)                          null,
    userRole     varchar(256) default 'user'            not null comment 'user/admin',
    createTime   datetime     default CURRENT_TIMESTAMP not null,
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    isDelete     tinyint      default 0                 not null,
    index idx_userAccount (userAccount)
) collate = utf8mb4_unicode_ci;

-- 图表表
create table if not exists chart
(
    id           bigint auto_increment comment 'id' primary key,
    goal				 text  null,
    `name`               varchar(128) null,
    chartData    text  null,
    chartType	   varchar(128) null,
    genChart		 text	 null,
    genResult		 text	 null,
    status       varchar(128) not null default 'wait' comment 'wait,running,succeed,failed',
    execMessage  text   null,
    userId       bigint null ,
    createTime   datetime     default CURRENT_TIMESTAMP not null,
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    isDelete     tinyint      default 0                 not null
) collate = utf8mb4_unicode_ci;
