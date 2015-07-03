create table if not exists new (
    id integer primary key AutoIncrement,
    toId integer,
    title varchar(100),
    content varchar(255),
    createTime integer,
    imgUrl varchar(255),
    unread integer,
    type varchar,
    detailId integer,
    belongId integer,
    isTop integer
);

create table if not exists chat (
    id integer primary key AutoIncrement,
    newId integer,
    fromId integer,
    toId integer,
    nickname varchar(100),
    headimgurl varchar(255),
    content varchar(255),
    type varchar,
    createdTime integer
);

