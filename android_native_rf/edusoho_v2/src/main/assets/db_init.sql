create table token (
    title varchar(64),
    type varchar(255),
    expiredTime integer,
    createdTime integer,
    data text,
    id integer,
    userId integer,
    times integer
);

create table data_cache (
    id integer,
    data text
);

create table cache_md5 (
    type varchar(64),
    key varchar(255),
    value integer
);
