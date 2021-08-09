create table user_info
(
	id int auto_increment,
	name varchar(64) default '' not null,
	gender tinyint default 0 not null comment '1代表男性，0代表女性',
	age int default 0 not null,
	telephone varchar(20) default '' not null,
	register_mode varchar(64) default '' not null comment 'by phone, by WeChat, by Alipay',
	third_party_id varchar(64) default '' not null,
	constraint user_info_pk
		primary key (id)
);

create table user_password
(
	id int auto_increment,
	encrypt_password varchar(128) default '' not null,
	user_id int default 0 not null,
	constraint user_password_pk
		primary key (id),
	constraint user_password_user_info_id_fk
		foreign key (user_id) references user_info (id)
);

