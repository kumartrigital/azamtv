create table IF NOT EXISTS b_sub_category(
     id bigint(20) not null auto_increment primary key , 
     main_category int(11)not null,
     sub_category varchar(100) not null,
     foreign key fk_sub_codevalue(main_category)
     references m_code_value(id)
     on update cascade 
     on delete restrict );
